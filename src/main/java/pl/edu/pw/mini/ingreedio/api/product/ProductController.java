package pl.edu.pw.mini.ingreedio.api.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import pl.edu.pw.mini.ingreedio.api.product.dto.FullProductDto;
import pl.edu.pw.mini.ingreedio.api.product.dto.ProductListResponseDto;
import pl.edu.pw.mini.ingreedio.api.product.dto.ProductRequestDto;
import pl.edu.pw.mini.ingreedio.api.product.exception.ProductNotFoundException;
import pl.edu.pw.mini.ingreedio.api.product.model.Product;
import pl.edu.pw.mini.ingreedio.api.product.service.PaginationService;
import pl.edu.pw.mini.ingreedio.api.product.service.ProductService;
import pl.edu.pw.mini.ingreedio.api.product.service.ProductsCriteriaService;
import pl.edu.pw.mini.ingreedio.api.review.dto.ReviewDto;
import pl.edu.pw.mini.ingreedio.api.review.dto.ReviewRequestDto;
import pl.edu.pw.mini.ingreedio.api.review.model.Review;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products")
public class ProductController {
    private final ProductService productService;
    private final PaginationService paginationService;
    private final ProductsCriteriaService productsCriteriaService;

    @Operation(summary = "Get matching products",
        description = "Fetches a list of products based on various search criteria such as "
            + "ingredients, rating, phrase, and sorting options. If authenticated, user gets "
            + "additional info about whether the product is liked.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductListResponseDto.class)))
    })
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
        description = "Fetches detailed information of a product based on the provided product ID.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product retrieved successfully",
            content = @Content(schema = @Schema(implementation = FullProductDto.class))),
        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<FullProductDto> getProductById(@PathVariable Long id) {
        return productService.getProductById(id).map(ResponseEntity::ok)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Operation(summary = "Add a new product",
        description = "Adds a new product to the inventory. Requires the 'ADD_PRODUCT' authority.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product added successfully",
            content = @Content(schema = @Schema(implementation = Product.class)))
    })
    @PreAuthorize("hasAuthority('ADD_PRODUCT')")
    @PostMapping
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

    @Operation(summary = "Delete a product",
        description = "Deletes a product from the inventory based on the provided product ID. "
            + "Requires the 'REMOVE_PRODUCT' authority.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product deleted successfully",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content)
    })
    @PreAuthorize("hasAuthority('REMOVE_PRODUCT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (!deleted) {
            throw new ProductNotFoundException(id);
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Edit a product",
        description = "Edits a product in the inventory based on the provided product ID and new "
            + "product details. Requires the 'EDIT_PRODUCT' authority.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product edited successfully",
            content = @Content(schema = @Schema(implementation = Product.class))),
        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @PreAuthorize("hasAuthority('EDIT_PRODUCT')")
    @PutMapping("/{id}")
    public ResponseEntity<Product> editProduct(@PathVariable Long id,
                                               @RequestBody ProductRequestDto product) {
        return productService.editProduct(id, product).map(ResponseEntity::ok)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Operation(summary = "Like a product",
        description = "Likes a product based on the provided product ID.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product liked successfully",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @PostMapping("/{id}/likes")
    public ResponseEntity<Void> likeProduct(@PathVariable Long id) {
        boolean likeSucceeded = productService.likeProduct(id);
        if (!likeSucceeded) {
            throw new ProductNotFoundException(id);
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Unlike a product",
        description = "Unlikes a product based on the provided product ID.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product unliked successfully",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @DeleteMapping("/{id}/likes")
    public ResponseEntity<Void> unlikeProduct(@PathVariable Long id) {
        boolean unlikeSucceeded = productService.unlikeProduct(id);
        if (!unlikeSucceeded) {
            throw new ProductNotFoundException(id);
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Add a review",
        description = "Adds a review to a product based on the provided product ID "
            + "and review details.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Review added successfully",
            content = @Content(schema = @Schema(implementation = ReviewDto.class)))
    })
    @PostMapping("/{id}/reviews")
    public ResponseEntity<ReviewDto> addReview(@PathVariable Long id,
                                               @Valid @RequestBody ReviewRequestDto reviewRequest) {
        Review review = Review.builder()
            .productId(id)
            .rating(reviewRequest.rating())
            .content(reviewRequest.content())
            .build();
        Optional<ReviewDto> reviewOptional = productService.addReview(review);
        //TODO: proper exceptions, requires refactor
        return reviewOptional.map(reviewDto -> new ResponseEntity<>(reviewDto, HttpStatus.CREATED))
            .orElseThrow(() -> Problem.valueOf(Status.BAD_REQUEST));
    }

    @Operation(summary = "Edit a review",
        description = "Edits a review for a product based on the provided product ID "
            + "and review details.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review edited successfully",
            content = @Content(schema = @Schema(implementation = ReviewDto.class)))
    })
    @PutMapping("/{id}/reviews")
    public ResponseEntity<ReviewDto> editReview(@PathVariable Long id,
                                                @Valid @RequestBody
                                                ReviewRequestDto reviewRequest) {
        Review review = Review.builder()
            .productId(id)
            .rating(reviewRequest.rating())
            .content(reviewRequest.content())
            .build();
        Optional<ReviewDto> reviewOptional = productService.editReview(review);
        //TODO: proper exceptions, requires refactor
        return reviewOptional.map(reviewDto -> new ResponseEntity<>(reviewDto, HttpStatus.OK))
            .orElseThrow(() -> Problem.valueOf(Status.BAD_REQUEST));
    }

    @Operation(summary = "Delete a review",
        description = "Deletes a review for a product based on the provided product ID.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review deleted successfully",
            content = @Content)
    })
    @DeleteMapping("/{id}/reviews")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        boolean deleted = productService.deleteReview(id);
        if (!deleted) {
            throw Problem.valueOf(Status.BAD_REQUEST); //TODO: proper exceptions, requires refactor
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get product reviews",
        description = "Fetches a list of reviews for a product based on the provided product ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully",
            content = @Content(
                array = @ArraySchema(schema = @Schema(implementation = ReviewDto.class))
            )),
        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewDto>> getProductReviews(@PathVariable Long id) {
        return productService.getProductReviews(id).map(ResponseEntity::ok)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Operation(summary = "Get user review for a product",
        description = "Fetches the review submitted by the authenticated user for a specific "
            + "product based on the provided product ID.",
        security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User review retrieved successfully",
            content = @Content(schema = @Schema(implementation = ReviewDto.class))),
        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @GetMapping("/{id}/review")
    public ResponseEntity<ReviewDto> getProductUserReview(@PathVariable Long id) {
        Optional<ReviewDto> reviewOptional = productService.getProductUserReview(id);
        return reviewOptional.map(ResponseEntity::ok)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
