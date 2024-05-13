package pl.edu.pw.mini.ingreedio.api.product.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
                Boolean isLiked = product.getLikedBy() != null
                    ? product.getLikedBy().contains(userId) : false;
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

    public Optional<Product> editProduct(Long id, Product product) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct != null) {
            existingProduct.setName(product.getName());
            existingProduct.setSmallImageUrl(product.getSmallImageUrl());
            existingProduct.setLargeImageUrl(product.getLargeImageUrl());
            existingProduct.setProvider(product.getProvider());
            existingProduct.setBrand(product.getBrand());
            existingProduct.setShortDescription(product.getShortDescription());
            existingProduct.setLongDescription(product.getLongDescription());
            existingProduct.setVolume(product.getVolume());
            existingProduct.setIngredients(product.getIngredients());
            existingProduct.setRating(product.getRating());

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
                Boolean isLiked = product.getLikedBy() != null
                    ? product.getLikedBy().contains(userId) : false;
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
        if (ratings == null) {
            ratings = new TreeMap<Long, Integer>();
        }

        if (ratings.containsKey(userId)) {
            return false;
        }

        ratings.put(userId, review.getRating());
        product.setRatings(ratings);
        productRepository.save(product);

        return true;
    }

    public boolean editReview(Long id, Review review) {
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
        if (!Objects.equals(userId, id)) {
            return false;
        }

        Optional<Review> reviewOptional = reviewService.editReview(review);
        if (reviewOptional.isEmpty()) {
            return false;
        }

        Product product = productOptional.get();
        Map<Long, Integer> ratings = product.getRatings();
        if (ratings == null) {
            return false;
        }

        ratings.put(userId, review.getRating());
        product.setRatings(ratings);
        productRepository.save(product);

        return true;
    }

    public boolean deleteReview(Long productId, Long reviewId) {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());
        if (userOptional.isEmpty()) {
            return false;
        }

        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return false;
        }

        Optional<Review> reviewOptional = reviewService.getReviewById(reviewId);
        if (reviewOptional.isEmpty()) {
            return false;
        }

        Review review = reviewOptional.get();

        User user = userOptional.get();
        Long userId = user.getId();
        if (!Objects.equals(userId, review.getUser().getId())) {
            return false;
        }

        Product product = productOptional.get();
        Map<Long, Integer> ratings = product.getRatings();
        if (ratings == null || !ratings.containsKey(userId)) {
            return false;
        }

        reviewService.deleteReview(reviewId);

        ratings.remove(userId);
        product.setRatings(ratings);
        productRepository.save(product);

        return true;
    }

    public Optional<List<Review>> getProductReviews(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(reviewService.getProductreviews(productId));
    }
}