package pl.edu.pw.mini.ingreedio.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import pl.edu.pw.mini.ingreedio.api.dto.ProductListResponseDto;
import pl.edu.pw.mini.ingreedio.api.mapper.ProductDtoMapper;
import pl.edu.pw.mini.ingreedio.api.model.Product;
import pl.edu.pw.mini.ingreedio.api.service.ProductService;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products" /*, description = "..."*/)
public class ProductController {
    private final ProductService productService;
    private final ProductDtoMapper productDtoMapper;
    private final int pageSize = 10;

    @Operation(summary = "Get matching products", description = "Get matching products")
    @GetMapping
    public ResponseEntity<ProductListResponseDto> getProducts(
        int pageNumber,
        @RequestParam Optional<String> name,
        @RequestParam Optional<String> provider,
        @RequestParam Optional<String> brand,
        @RequestParam Optional<Integer> volumeFrom,
        @RequestParam Optional<Integer> volumeTo,
        @RequestParam Optional<String[]> ingredients) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        Page<ProductDto> page = productService.getProductsMatching(
            ProductFilterCriteria.builder()
                .name(name.orElse(null))
                .provider(provider.orElse(null))
                .brand(brand.orElse(null))
                .volumeFrom(volumeFrom.orElse(null))
                .volumeTo(volumeTo.orElse(null))
                .ingredients(ingredients.orElse(null))
                .build(),
            pageRequest
        );

        int totalPages = page.getTotalPages();

        ProductListResponseDto responseDto = new ProductListResponseDto(page.getContent(),
            totalPages);

        return ResponseEntity.ok(responseDto);
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
}
