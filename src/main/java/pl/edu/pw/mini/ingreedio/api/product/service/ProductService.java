package pl.edu.pw.mini.ingreedio.api.product.service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.auth.service.AuthService;
import pl.edu.pw.mini.ingreedio.api.brand.exception.BrandNotFoundException;
import pl.edu.pw.mini.ingreedio.api.brand.service.BrandService;
import pl.edu.pw.mini.ingreedio.api.category.service.CategoryService;
import pl.edu.pw.mini.ingreedio.api.ingredient.service.IngredientService;
import pl.edu.pw.mini.ingreedio.api.product.criteria.ProductCriteria;
import pl.edu.pw.mini.ingreedio.api.product.exception.ProductNotFoundException;
import pl.edu.pw.mini.ingreedio.api.product.model.BrandDocument;
import pl.edu.pw.mini.ingreedio.api.product.model.CategoryDocument;
import pl.edu.pw.mini.ingreedio.api.product.model.IngredientDocument;
import pl.edu.pw.mini.ingreedio.api.product.model.ProductDocument;
import pl.edu.pw.mini.ingreedio.api.product.model.ProviderDocument;
import pl.edu.pw.mini.ingreedio.api.product.repository.ProductRepository;
import pl.edu.pw.mini.ingreedio.api.provider.exception.ProviderNotFoundException;
import pl.edu.pw.mini.ingreedio.api.provider.service.ProviderService;
import pl.edu.pw.mini.ingreedio.api.review.dto.ReviewDto;
import pl.edu.pw.mini.ingreedio.api.review.model.Review;
import pl.edu.pw.mini.ingreedio.api.review.service.ReviewService;
import pl.edu.pw.mini.ingreedio.api.user.model.User;
import pl.edu.pw.mini.ingreedio.api.user.service.UserService;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final SequenceGeneratorService sequenceGenerator;
    private final UserService userService;

    private final IngredientService ingredientService;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final ProviderService providerService;

    private final ReviewService reviewService;

    private final ModelMapper modelMapper;

    private final AuthService authService;


    @Transactional(readOnly = true)
    public List<ProductDocument> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional
    // This function checks whether the provider/brand/category/ingredients ids do
    // exist in the postgresql database and adds (updates) missing (incorrect) fields
    // if it's necessary.
    //
    // In case that every id is valid the product with correct subdocuments is returned.
    //
    // If there exists at least one invalid id a corresponding exception is thrown.
    public ProductDocument makeProductFieldsValid(ProductDocument product) throws
        BrandNotFoundException, ProviderNotFoundException {
        if (product.getIngredients() != null) {
            Set<Long> ingredientIds = product
                .getIngredients()
                .stream()
                .map(IngredientDocument::getId)
                .collect(Collectors.toSet());

            Set<IngredientDocument> ingredients = ingredientService
                .getIngredientsByIds(ingredientIds)
                .stream()
                .map(ingredient -> modelMapper
                    .map(ingredient, IngredientDocument.IngredientDocumentBuilder.class)
                    .build())
                .collect(Collectors.toSet());

            product.setIngredients(ingredients);
        }

        if (product.getBrand() != null) {
            BrandDocument brand = modelMapper
                .map(brandService
                    .getBrandById(product.getBrand().getId()),
                    BrandDocument.BrandDocumentBuilder.class)
                .build();

            product.setBrand(brand);
        }

        if (product.getProvider() != null) {
            ProviderDocument provider = modelMapper
                .map(providerService
                    .getProviderById(product.getProvider().getId()),
                    ProviderDocument.ProviderDocumentBuilder.class)
                .build();

            product.setProvider(provider);
        }

        if (product.getCategories() != null) {
            Set<Long> categoriesIds = product
                .getCategories()
                .stream()
                .map(CategoryDocument::getId)
                .collect(Collectors.toSet());

            Set<CategoryDocument> categories = categoryService
                .getCategoriesByIds(categoriesIds)
                .stream()
                .map(category -> modelMapper
                    .map(category, CategoryDocument.CategoryDocumentBuilder.class)
                    .build())
                .collect(Collectors.toSet());

            product.setCategories(categories);
        }

        return product;
    }

    @Transactional(readOnly = true)
    public ProductDocument getProductById(long id) throws ProductNotFoundException {
        return productRepository
            .findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Transactional
    public ProductDocument addProduct(ProductDocument product) {
        product.setId(sequenceGenerator.generateSequence(ProductDocument.SEQUENCE_NAME));
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProductById(long id) throws ProductNotFoundException {
        ProductDocument product = getProductById(id);

        userService.allUsersUnlikeProduct(product.getId());
        productRepository.deleteById(product.getId());
    }

    @Transactional
    public ProductDocument updateProduct(ProductDocument productPatch)
        throws ProductNotFoundException {
        ProductDocument oldProduct = getProductById(productPatch.getId());

        // Update a field of the oldProduct only if corresponding field
        // in the productPatch is not null
        for (Field field : productPatch.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())
                || Modifier.isFinal(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);
            try {
                Object newValue = field.get(productPatch);
                if (newValue != null) {
                    field.set(oldProduct, newValue);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return productRepository.save(oldProduct);
    }

    @Transactional(readOnly = true)
    public Page<ProductDocument> getProductsMatchingCriteria(ProductCriteria criteria,
                                                             PageRequest pageRequest) {
        return productRepository.getProductsMatchingCriteria(criteria, pageRequest);
    }

    public boolean isProductLikedByUser(ProductDocument product, User user) {
        return product.getLikedBy() != null && product.getLikedBy().contains(user.getId());
    }

    @Transactional
    public void likeProduct(Long productId, User user) throws ProductNotFoundException {
        ProductDocument product = getProductById(productId);

        Set<Long> likedBy = product.getLikedBy();
        if (likedBy == null) {
            likedBy = new HashSet<>();
        }

        if (!likedBy.contains(user.getId())) {
            likedBy.add(user.getId());
            product.setLikedBy(likedBy);
            productRepository.save(product);
            userService.likeProduct(user.getId().intValue(), productId);
        }
    }

    @Transactional
    public void unlikeProduct(Long productId, User user) {
        ProductDocument product = getProductById(productId);

        Set<Long> likedBy = product.getLikedBy();
        if (likedBy == null) {
            return;
        }

        if (likedBy.contains(user.getId())) {
            likedBy.remove(user.getId());
            product.setLikedBy(likedBy);
            productRepository.save(product);
            userService.unlikeProduct(user.getId().intValue(), productId);
        }
    }


    @Transactional
    public Optional<ReviewDto> addReview(Review review) throws ProductNotFoundException {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        ProductDocument product = getProductById(review.getProductId());

        User user = userOptional.get();
        review.setUser(user);

        Optional<ReviewDto> reviewOptional = reviewService.addReview(user, review);
        if (reviewOptional.isEmpty()) {
            return Optional.empty();
        }

        Map<Long, Integer> ratings = product.getRatings();
        Integer ratingSum = product.getRatingSum();

        if (ratings == null) {
            ratings = new TreeMap<>();
            ratingSum = 0;
        }

        Long userId = user.getId();
        if (ratings.containsKey(userId)) {
            return Optional.empty();
        }

        ratings.put(userId, review.getRating());
        ratingSum = ratingSum + review.getRating();
        product.setRatings(ratings);
        product.setRatingSum(ratingSum);

        Integer rating = ratingSum / ratings.size();
        product.setRating(rating);

        productRepository.save(product);

        return reviewOptional;
    }

    @Transactional
    public Optional<ReviewDto> editReview(Review review) throws ProductNotFoundException {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        ProductDocument product = getProductById(review.getProductId());

        User user = userOptional.get();
        Long userId = user.getId();

        Optional<ReviewDto> reviewOptional = reviewService.editReview(user, review);
        if (reviewOptional.isEmpty()) {
            return Optional.empty();
        }

        Map<Long, Integer> ratings = product.getRatings();
        if (ratings == null) {
            return Optional.empty();
        }
        Integer ratingSum = product.getRatingSum();
        ratingSum = ratingSum - ratings.get(userId) + review.getRating();
        ratings.put(userId, review.getRating());

        product.setRatings(ratings);
        product.setRatingSum(ratingSum);

        Integer rating = ratings.isEmpty() ? 0 : (ratingSum / ratings.size());
        product.setRating(rating);

        productRepository.save(product);

        return reviewOptional;
    }

    @Transactional
    public boolean deleteReview(Long productId) throws ProductNotFoundException {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());
        if (userOptional.isEmpty()) {
            return false;
        }

        ProductDocument product = getProductById(productId);

        User user = userOptional.get();
        Long userId = user.getId();

        Map<Long, Integer> ratings = product.getRatings();
        if (ratings == null || !ratings.containsKey(userId)) {
            return false;
        }

        reviewService.deleteReview(user, productId);

        Integer ratingSum = product.getRatingSum();
        ratingSum = ratingSum - ratings.get(userId);

        ratings.remove(userId);
        product.setRatings(ratings);
        product.setRatingSum(ratingSum);

        Integer rating = ratings.isEmpty() ? 0 : (ratingSum / ratings.size());
        product.setRating(rating);

        productRepository.save(product);

        return true;
    }

    @Transactional(readOnly = true)
    public Optional<List<ReviewDto>> getProductReviews(Long productId)
        throws ProductNotFoundException {
        getProductById(productId);

        return Optional.of(reviewService.getProductReviews(productId));
    }

    @Transactional(readOnly = true)
    public Optional<List<ReviewDto>> getProductReviews(Long productId, User user) {
        getProductById(productId);
        return Optional.of(reviewService.getProductReviews(productId, user));
    }

    public Optional<ReviewDto> getProductUserReview(Long id) {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();
        return reviewService.getProductUserReview(user, id);
    }
}