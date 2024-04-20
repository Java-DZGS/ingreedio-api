package pl.edu.pw.mini.ingreedio.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.mini.ingreedio.api.criteria.ProductFilterCriteria;
import pl.edu.pw.mini.ingreedio.api.dto.FullProductDto;
import pl.edu.pw.mini.ingreedio.api.dto.ProductDto;
import pl.edu.pw.mini.ingreedio.api.model.Product;
import pl.edu.pw.mini.ingreedio.api.service.ProductService;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products" /*, description = "..."*/)
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "Get matching products", description = "Get matching products")
    @GetMapping
    public ResponseEntity<List<ProductDto>> getProducts(
        @RequestParam Optional<String> name,
        @RequestParam Optional<String> provider,
        @RequestParam Optional<String> brand,
        @RequestParam Optional<Integer> volumeFrom,
        @RequestParam Optional<Integer> volumeTo,
        @RequestParam Optional<String[]> ingredients) {
        List<ProductDto> products = productService.getProductsMatching(
            ProductFilterCriteria.builder()
                .name(name.orElse(null))
                .provider(provider.orElse(null))
                .brand(brand.orElse(null))
                .volumeFrom(volumeFrom.orElse(null))
                .volumeTo(volumeTo.orElse(null))
                .ingredients(ingredients.orElse(null))
                .build()
        );

        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Get info of a specific product",
        description = "Get info of a specific product")
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

}
