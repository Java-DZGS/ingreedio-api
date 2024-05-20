package pl.edu.pw.mini.ingreedio.api.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.mini.ingreedio.api.product.dto.FullProductDto;
import pl.edu.pw.mini.ingreedio.api.product.dto.ProductListResponseDto;
import pl.edu.pw.mini.ingreedio.api.product.dto.ProductRequestDto;
import pl.edu.pw.mini.ingreedio.api.product.dto.ReviewDto;
import pl.edu.pw.mini.ingreedio.api.product.dto.ReviewRequestDto;
import pl.edu.pw.mini.ingreedio.api.product.model.Product;
import pl.edu.pw.mini.ingreedio.api.product.model.Review;
import pl.edu.pw.mini.ingreedio.api.product.service.PaginationService;
import pl.edu.pw.mini.ingreedio.api.product.service.ProductService;
import pl.edu.pw.mini.ingreedio.api.product.service.ProductsCriteriaService;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products" /*, description = "..."*/)
public class ProductController {
    private final ProductService productService;
    private final PaginationService paginationService;
    private final ProductsCriteriaService productsCriteriaService;

    @Operation(summary = "Get matching products",
        description = "Get matching products",
        security = {@SecurityRequirement(name = "Bearer Authentication")})
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping
    public ResponseEntity<ProductListResponseDto> getProducts(
        @RequestParam("page-number") Optional<Integer> pageNumber,
        @RequestParam("ingredients-exclude") Optional<Set<Long>> ingredientsToExclude,
        @RequestParam("ingredients-include") Optional<Set<Long>> ingredientsToInclude,
        @RequestParam("min-rating") Optional<Integer> minRating,
        @RequestParam("phrase") Optional<String> phrase,
        @RequestParam("sort-by") Optional<List<String>> sortBy,
        @RequestParam("liked") Optional<Boolean> liked) {

        ProductListResponseDto products = productService.getProductsMatchingCriteria(
            productsCriteriaService.getProductsCriteria(
                ingredientsToExclude,
                ingredientsToInclude,
                minRating,
                phrase,
                sortBy,
                liked
                // TODO: provider, brand, category
            ),
            paginationService.getPageRequest(pageNumber)
        );

        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Get info of a specific product",
        description = "Get info of a specific product",
        security = {@SecurityRequirement(name = "Bearer Authentication")})
    @GetMapping("/{id}")
    public ResponseEntity<FullProductDto> getProductById(@PathVariable Long id) {
        return productService.getProductById(id).map(ResponseEntity::ok)
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Add a product to the database",
        description = "Add a product to the database",
        security = {@SecurityRequirement(name = "Bearer Authentication")})
    @PreAuthorize("hasAuthority('ADD_PRODUCT')")
    @PostMapping
    @ResponseBody
    public ResponseEntity<Product> addProduct(@RequestBody ProductRequestDto productRequest) {
        Product product = Product.builder()
            .name(productRequest.name())
            .smallImageUrl(productRequest.smallImageUrl())
            .largeImageUrl(productRequest.largeImageUrl())
            .provider(productRequest.provider())
            .brand(productRequest.brand())
            .shortDescription(productRequest.shortDescription())
            .longDescription(productRequest.longDescription())
            .volume(productRequest.volume())
            .ingredients(productRequest.ingredients())
            .build();

        Product savedProduct = productService.addProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @Operation(summary = "Delete product from the database",
        description = "Delete product from the database",
        security = {@SecurityRequirement(name = "Bearer Authentication")})
    @PreAuthorize("hasAuthority('REMOVE_PRODUCT')")
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Edit product in the database",
        description = "Edit product in the database",
        security = {@SecurityRequirement(name = "Bearer Authentication")})
    @PreAuthorize("hasAuthority('EDIT_PRODUCT')")
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Product> editProduct(@PathVariable Long id,
                                               @RequestBody ProductRequestDto product) {
        Optional<Product> editedProduct = productService.editProduct(id, product);
        if (editedProduct.isPresent()) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "",
        description = "",
        security = {@SecurityRequirement(name = "Bearer Authentication")})
    @PostMapping("/{id}/likes")
    @ResponseBody
    public ResponseEntity<Void> likeProduct(@PathVariable Long id) {
        boolean likeSucceeded = productService.likeProduct(id);
        if (likeSucceeded) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Unlike product",
        description = "Unlike product",
        security = {@SecurityRequirement(name = "Bearer Authentication")})
    @DeleteMapping("/{id}/likes")
    @ResponseBody
    public ResponseEntity<Void> unlikeProduct(@PathVariable Long id) {
        boolean unlikeSucceeded = productService.unlikeProduct(id);
        if (unlikeSucceeded) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Add product review",
        description = "Add product review",
        security = {@SecurityRequirement(name = "Bearer Authentication")})
    @PostMapping("/{id}/ratings")
    @ResponseBody
    public ResponseEntity<Void> addReview(@PathVariable Long id,
                                          @RequestBody ReviewRequestDto reviewRequest) {
        if (reviewRequest.rating() < 0 || reviewRequest.rating() > 10) {
            return ResponseEntity.badRequest().build();
        }

        Review review = Review.builder()
            .productId(id)
            .rating(reviewRequest.rating())
            .content(reviewRequest.content())
            .build();
        boolean added = productService.addReview(review);
        if (added) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Edit product review",
        description = "Edit product review",
        security = {@SecurityRequirement(name = "Bearer Authentication")})
    @PutMapping("/{id}/ratings")
    @ResponseBody
    public ResponseEntity<Void> editReview(@PathVariable Long id,
                                           @RequestBody ReviewRequestDto reviewRequest) {
        if (reviewRequest.rating() < 0 || reviewRequest.rating() > 10) {
            return ResponseEntity.badRequest().build();
        }

        Review review = Review.builder()
            .productId(id)
            .rating(reviewRequest.rating())
            .content(reviewRequest.content())
            .build();
        boolean edited = productService.editReview(review);
        if (edited) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Delete product review",
        description = "Delete product review",
        security = {@SecurityRequirement(name = "Bearer Authentication")})
    @DeleteMapping("/{id}/ratings")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        boolean deleted = productService.deleteReview(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Get product reviews",
        description = "Get product reviews",
        security = {@SecurityRequirement(name = "Bearer Authentication")})
    @GetMapping("/{id}/ratings")
    @ResponseBody
    public ResponseEntity<List<ReviewDto>> getProductReviews(@PathVariable Long id) {
        Optional<List<ReviewDto>> reviewsOptional = productService.getProductReviews(id);
        return reviewsOptional.map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}