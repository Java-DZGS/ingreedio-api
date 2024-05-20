package pl.edu.pw.mini.ingreedio.api.product.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.auth.service.AuthService;
import pl.edu.pw.mini.ingreedio.api.product.criteria.ProductCriteria;
import pl.edu.pw.mini.ingreedio.api.product.dto.FullProductDto;
import pl.edu.pw.mini.ingreedio.api.product.dto.ProductDto;
import pl.edu.pw.mini.ingreedio.api.product.dto.ProductListResponseDto;
import pl.edu.pw.mini.ingreedio.api.product.dto.ProductRequestDto;
import pl.edu.pw.mini.ingreedio.api.product.dto.ReviewDto;
import pl.edu.pw.mini.ingreedio.api.product.mapper.FullProductDtoMapper;
import pl.edu.pw.mini.ingreedio.api.product.mapper.ProductDtoMapper;
import pl.edu.pw.mini.ingreedio.api.product.model.Product;
import pl.edu.pw.mini.ingreedio.api.product.model.Review;
import pl.edu.pw.mini.ingreedio.api.product.repository.ProductRepository;
import pl.edu.pw.mini.ingreedio.api.user.model.User;
import pl.edu.pw.mini.ingreedio.api.user.service.UserService;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserService userService;
    private final AuthService authService;
    private final ReviewService reviewService;
    private final ProductDtoMapper productDtoMapper;
    private final FullProductDtoMapper fullProductDtoMapper;
    private final SequenceGeneratorService sequenceGenerator;

    public List<ProductDto> getAllProducts() {
        return productRepository
            .findAll()
            .stream()
            .map(productDtoMapper).collect(Collectors.toList());
    }

    public Optional<FullProductDto> getProductById(Long id) {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Long userId = user.getId();
            Optional<Product> productOptional = productRepository.findById(id);
            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                Boolean isLiked =
                    product.getLikedBy() != null && product.getLikedBy().contains(userId);
                return Optional.ofNullable(FullProductDto.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .largeImageUrl(product.getLargeImageUrl())
                    .provider(product.getProvider())
                    .brand(product.getBrand())
                    .volume(product.getVolume())
                    .longDescription(product.getLongDescription())
                    .ingredients(product.getIngredients())
                    .isLiked(isLiked)
                    .rating(product.getRating())
                    .build());
            }
            return productOptional.map(fullProductDtoMapper);
        }
        return productRepository.findById(id).map(fullProductDtoMapper);
    }

    public Product addProduct(Product product) {
        product.setId(sequenceGenerator.generateSequence(Product.SEQUENCE_NAME));
        return productRepository.save(product);
    }

    public boolean deleteProduct(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            userService.deleteProduct(id);
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Product> editProduct(Long id, ProductRequestDto product) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct != null) {
            existingProduct.setName(product.name());
            existingProduct.setSmallImageUrl(product.smallImageUrl());
            existingProduct.setLargeImageUrl(product.largeImageUrl());
            existingProduct.setProvider(product.provider());
            existingProduct.setBrand(product.brand());
            existingProduct.setShortDescription(product.shortDescription());
            existingProduct.setLongDescription(product.longDescription());
            existingProduct.setVolume(product.volume());
            existingProduct.setIngredients(product.ingredients());

            productRepository.save(existingProduct);
            return Optional.of(existingProduct);
        }
        return Optional.empty();
    }

    public ProductListResponseDto getProductsMatchingCriteria(
        ProductCriteria criteria, PageRequest pageRequest) {
        Page<Product> productsPage = productRepository
            .getProductsMatchingCriteria(criteria, pageRequest);

        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());

        if (userOptional.isEmpty()) {
            return new ProductListResponseDto(
                productsPage.getContent().stream().map(productDtoMapper).toList(),
                productsPage.getTotalPages());
        }

        User user = userOptional.get();
        Long userId = user.getId();

        List<ProductDto> productDtos = productsPage.getContent().stream()
            .map(product -> {
                Boolean isLiked =
                    product.getLikedBy() != null && product.getLikedBy().contains(userId);
                return ProductDto.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .smallImageUrl(product.getSmallImageUrl())
                    .provider(product.getProvider())
                    .shortDescription(product.getShortDescription())
                    .isLiked(isLiked)
                    .build();
            })
            .collect(Collectors.toList());

        return new ProductListResponseDto(productDtos,
            productsPage.getTotalPages());
    }

    public boolean likeProduct(Long productId) {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());
        if (userOptional.isEmpty()) {
            return false;
        }

        Long userId = userOptional.get().getId();

        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return false;
        }

        Product product = productOptional.get();
        List<Long> likedBy = product.getLikedBy();

        if (likedBy == null) {
            likedBy = new ArrayList<>();
        }

        if (!likedBy.contains(userId)) {
            likedBy.add(userId);
            product.setLikedBy(likedBy);
            productRepository.save(product);
            userService.likeProduct(userId.intValue(), productId);
        }
        return true;
    }

    public boolean unlikeProduct(Long productId) {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());
        if (userOptional.isEmpty()) {
            return false;
        }

        Long userId = userOptional.get().getId();

        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return false;
        }

        Product product = productOptional.get();
        List<Long> likedBy = product.getLikedBy();

        if (likedBy == null) {
            return true;
        }

        if (likedBy.contains(userId)) {
            likedBy.remove(userId);
            product.setLikedBy(likedBy);
            productRepository.save(product);
            userService.unlikeProduct(userId.intValue(), productId);
        }
        return true;
    }

    public boolean addReview(Review review) {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());
        if (userOptional.isEmpty()) {
            return false;
        }

        Optional<Product> productOptional = productRepository.findById(review.getProductId());
        if (productOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();
        Long userId = user.getId();
        review.setUser(user);

        Optional<Review> reviewOptional = reviewService.addReview(userId, review);
        if (reviewOptional.isEmpty()) {
            return false;
        }

        Product product = productOptional.get();
        Map<Long, Integer> ratings = product.getRatings();
        Integer ratingSum = product.getRatingSum();

        if (ratings == null) {
            ratings = new TreeMap<>();
            ratingSum = 0;
        }

        if (ratings.containsKey(userId)) {
            return false;
        }

        ratings.put(userId, review.getRating());
        ratingSum = ratingSum + review.getRating();
        product.setRatings(ratings);
        product.setRatingSum(ratingSum);

        Integer rating = ratingSum / ratings.size();
        product.setRating(rating);

        productRepository.save(product);

        return true;
    }

    public boolean editReview(Review review) {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());
        if (userOptional.isEmpty()) {
            return false;
        }

        Optional<Product> productOptional = productRepository.findById(review.getProductId());
        if (productOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();
        Long userId = user.getId();

        Optional<Review> reviewOptional = reviewService.editReview(userId, review);
        if (reviewOptional.isEmpty()) {
            return false;
        }

        Product product = productOptional.get();
        Map<Long, Integer> ratings = product.getRatings();
        if (ratings == null) {
            return false;
        }
        Integer ratingSum = product.getRatingSum();
        ratingSum = ratingSum - ratings.get(userId) + review.getRating();
        ratings.put(userId, review.getRating());

        product.setRatings(ratings);
        product.setRatingSum(ratingSum);

        Integer rating = ratings.isEmpty() ? 0 : (ratingSum / ratings.size());
        product.setRating(rating);

        productRepository.save(product);

        return true;
    }

    public boolean deleteReview(Long productId) {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());
        if (userOptional.isEmpty()) {
            return false;
        }

        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();
        Long userId = user.getId();

        Product product = productOptional.get();
        Map<Long, Integer> ratings = product.getRatings();
        if (ratings == null || !ratings.containsKey(userId)) {
            return false;
        }

        reviewService.deleteReview(userId, productId);

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

    public Optional<List<ReviewDto>> getProductReviews(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(reviewService.getProductReviews(productId));
    }
}