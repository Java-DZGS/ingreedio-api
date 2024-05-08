package pl.edu.pw.mini.ingreedio.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.criteria.ProductsCriteria;
import pl.edu.pw.mini.ingreedio.api.dto.FullProductDto;
import pl.edu.pw.mini.ingreedio.api.dto.ProductDto;
import pl.edu.pw.mini.ingreedio.api.dto.ProductListResponseDto;
import pl.edu.pw.mini.ingreedio.api.mapper.FullProductDtoMapper;
import pl.edu.pw.mini.ingreedio.api.mapper.ProductDtoMapper;
import pl.edu.pw.mini.ingreedio.api.model.Product;
import pl.edu.pw.mini.ingreedio.api.model.User;
import pl.edu.pw.mini.ingreedio.api.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserService userService;
    private final AuthService authService;
    private final ProductDtoMapper productDtoMapper;
    private final FullProductDtoMapper fullProductDtoMapper;
    private final SequenceGeneratorService sequenceGenerator;

    private final int pageSize = 10;

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
                    ? product.getLikedBy().contains(userId) : null;
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

    public ProductListResponseDto getProductsMatchingCriteria(
        ProductsCriteria criteria, Integer pageNumber) {
        PageRequest pageable = PageRequest.of(pageNumber, pageSize);
        Page<Product> productsPage = productRepository
            .getProductsMatchingCriteria(criteria, pageable);

        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());

        if (userOptional.isEmpty()) {
            return new ProductListResponseDto(
                productsPage.getContent().stream().map(productDtoMapper).toList(),
                productsPage.getTotalPages() / pageSize);
        }
        
        User user = userOptional.get();
        Long userId = user.getId();

        List<ProductDto> productDtos = productsPage.getContent().stream()
            .map(product -> {
                Boolean isLiked = product.getLikedBy() != null
                    ? product.getLikedBy().contains(userId) : null;
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
            productsPage.getTotalPages() / pageSize);
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
}