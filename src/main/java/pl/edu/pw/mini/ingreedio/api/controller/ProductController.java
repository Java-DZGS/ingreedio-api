package pl.edu.pw.mini.ingreedio.api.controller;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.mini.ingreedio.api.dto.FullProductDto;
import pl.edu.pw.mini.ingreedio.api.dto.ProductListResponseDto;
import pl.edu.pw.mini.ingreedio.api.model.Product;
import pl.edu.pw.mini.ingreedio.api.service.PaginationService;
import pl.edu.pw.mini.ingreedio.api.service.ProductService;
import pl.edu.pw.mini.ingreedio.api.service.ProductsCriteriaService;

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
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product savedProduct = productService.addProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
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

    @Operation(summary = "",
        description = "",
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
}
