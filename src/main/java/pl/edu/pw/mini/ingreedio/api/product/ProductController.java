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
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import pl.edu.pw.mini.ingreedio.api.auth.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.common.validation.ValidationGroups;
import pl.edu.pw.mini.ingreedio.api.product.dto.ProductDto;
import pl.edu.pw.mini.ingreedio.api.product.dto.ProductPageDto;
import pl.edu.pw.mini.ingreedio.api.product.dto.ProductRequestDto;
import pl.edu.pw.mini.ingreedio.api.product.dto.ProductViewDto;
import pl.edu.pw.mini.ingreedio.api.product.exception.ProductNotFoundException;
import pl.edu.pw.mini.ingreedio.api.product.model.ProductDocument;
import pl.edu.pw.mini.ingreedio.api.product.service.PaginationService;
import pl.edu.pw.mini.ingreedio.api.product.service.ProductCriteriaService;
import pl.edu.pw.mini.ingreedio.api.product.service.ProductService;
import pl.edu.pw.mini.ingreedio.api.review.dto.ReviewDto;
import pl.edu.pw.mini.ingreedio.api.review.dto.ReviewRequestDto;
import pl.edu.pw.mini.ingreedio.api.review.model.Review;
import pl.edu.pw.mini.ingreedio.api.user.model.User;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products")
public class ProductController {
    private final ProductService productService;
    private final PaginationService paginationService;
    private final ProductCriteriaService productCriteriaService;

    private final ModelMapper modelMapper;

    @Operation(summary = "Get matching products",
        description = "Fetches a list of products based on various search criteria such as "
            + "ingredients, rating, phrase, and sorting options. If authenticated, user gets "
            + "additional info about whether the product is liked.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductPageDto.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<ProductPageDto> searchForProducts(
        Authentication authentication,
        @RequestParam("page-number") Optional<Integer> pageNumber,
        @RequestParam("ingredients-exclude") Optional<Set<Long>> ingredientsToExclude,
        @RequestParam("ingredients-include") Optional<Set<Long>> ingredientsToInclude,
        @RequestParam("min-rating") Optional<Integer> minRating,
        @RequestParam("phrase") Optional<String> phrase,
        @RequestParam("sort-by") Optional<List<String>> sortBy,
        @RequestParam("liked") Optional<Boolean> liked,
        @RequestParam("brands-exclude") Optional<Set<Long>> brandsToExclude,
        @RequestParam("brands-include") Optional<Set<Long>> brandsToInclude,
        @RequestParam("providers") Optional<Set<Long>> providers,
        @RequestParam("categories") Optional<Set<Long>> categories) {
        Page<ProductDocument> products = productService.getProductsMatchingCriteria(
            productCriteriaService.getProductsCriteria(
                ingredientsToExclude,
                ingredientsToInclude,
                minRating,
                phrase,
                sortBy,
                liked,
                providers,
                brandsToExclude,
                brandsToInclude,
                categories
            ),
            paginationService.getPageRequest(pageNumber)
        );

        User user = (authentication != null && authentication.isAuthenticated())
            ? ((AuthInfo) authentication.getPrincipal()).getUser()
            : null;

        List<ProductViewDto> productsDtos = products.getContent()
            .stream()
            .map(product -> modelMapper
                .map(product, ProductViewDto.ProductViewDtoBuilder.class)
                .isLiked(user != null && productService.isProductLikedByUser(product, user))
                .build()
            )
            .collect(Collectors.toList());

        return ResponseEntity.ok(new ProductPageDto(productsDtos, products.getTotalPages()));
    }

    @Operation(summary = "Get full info of a specific product",
        description = "Fetches detailed information of a product based on the provided product ID.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductDto.class))),
        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(Authentication authentication,
                                                     @PathVariable long id) {
        ProductDocument product = productService.getProductById(id);

        User user = (authentication != null && authentication.isAuthenticated())
            ? ((AuthInfo) authentication.getPrincipal()).getUser()
            : null;

        return ResponseEntity.ok(modelMapper
            .map(product, ProductDto.ProductDtoBuilder.class)
            .isLiked(user != null && productService.isProductLikedByUser(product, user))
            .build());
    }

    @Operation(summary = "Add a new product",
        description = "Adds a new product to the inventory.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product added successfully",
            content = @Content(schema = @Schema(implementation = ProductDocument.class)))
    })
    @PreAuthorize("hasAuthority('ADD_PRODUCT')")
    @PostMapping
    public ResponseEntity<ProductDto> addProduct(
        @RequestBody @Validated(ValidationGroups.Put.class) ProductRequestDto productRequest) {
        ProductDocument product = modelMapper
            .map(productRequest, ProductDocument.ProductDocumentBuilder.class)
            .build();

        ProductDocument savedProduct = productService
            .addProduct(productService.makeProductFieldsValid(product));

        return new ResponseEntity<>(modelMapper
            .map(savedProduct, ProductDto.ProductDtoBuilder.class)
            .build(), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete a product",
        description = "Deletes a product from the inventory based on the provided product ID.",
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
    public ResponseEntity<Void> deleteProduct(@PathVariable long id) {
        productService.deleteProductById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Update an entire product",
        description = "Updates all details of a product in the inventory based on "
            + "the provided product ID and new product details.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product edited successfully",
            content = @Content(schema = @Schema(implementation = ProductDocument.class))),
        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @PreAuthorize("hasAuthority('EDIT_PRODUCT')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDocument> updateProduct(@PathVariable long id,
                                                         @Validated(ValidationGroups.Put.class)
                                                         @RequestBody ProductRequestDto product) {
        ProductDocument newProduct = modelMapper
            .map(product, ProductDocument.ProductDocumentBuilder.class)
            .id(id)
            .build();

        return ResponseEntity.ok(productService
            .updateProduct(productService.makeProductFieldsValid(newProduct)));
    }

    @Operation(
        summary = "Partially update a product",
        description = "Partially updates details of a product in the inventory based on "
            + "the provided product ID and new product details.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product edited successfully",
            content = @Content(schema = @Schema(implementation = ProductDocument.class))),
        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @PreAuthorize("hasAuthority('EDIT_PRODUCT')")
    @PatchMapping("/{id}")
    public ResponseEntity<ProductDocument> updateProductPatch(@PathVariable long id,
                                                         @Validated(ValidationGroups.Patch.class)
                                                         @RequestBody ProductRequestDto product) {
        ProductDocument productPatch = modelMapper
            .map(product, ProductDocument.ProductDocumentBuilder.class)
            .id(id)
            .build();

        return ResponseEntity.ok(productService
            .updateProduct(productService.makeProductFieldsValid(productPatch)));
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
    public ResponseEntity<Void> likeProduct(Authentication authentication, @PathVariable long id) {
        User user = ((AuthInfo) authentication.getPrincipal()).getUser();
        productService.likeProduct(id, user);
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
    public ResponseEntity<Void> unlikeProduct(Authentication authentication,
                                              @PathVariable long id) {
        User user = ((AuthInfo) authentication.getPrincipal()).getUser();
        productService.unlikeProduct(id, user);
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
    public ResponseEntity<ReviewDto> addReview(@PathVariable long id,
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

    @Operation(summary = "Get product reviews",
        description = "Fetches a list of reviews for a product based on the provided product ID.",
        security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully",
            content = @Content(
                array = @ArraySchema(schema = @Schema(implementation = ReviewDto.class))
            )),
        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewDto>> getProductReviews(Authentication authentication,
                                                             @PathVariable long id) {
        if (authentication != null && authentication.isAuthenticated()) {
            return productService.getProductReviews(id,
                ((AuthInfo) authentication.getPrincipal()).getUser())
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ProductNotFoundException(id));
        }

        return productService.getProductReviews(id).map(ResponseEntity::ok)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
