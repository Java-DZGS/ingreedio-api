package pl.edu.pw.mini.ingreedio.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.mini.ingreedio.api.dto.FullProductDto;
import pl.edu.pw.mini.ingreedio.api.dto.ProductDto;
import pl.edu.pw.mini.ingreedio.api.model.Product;
import pl.edu.pw.mini.ingreedio.api.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
//@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Products" /*, description = "..."*/)
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get all products", description = "Get all products")
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "Get info of a specific product",
            description = "Get info of a specific product")
    @GetMapping("/{id}")
    public ResponseEntity<FullProductDto> getProductById(@PathVariable Long id) {
        FullProductDto product = productService.getProductById(id);
        if (product != null) {
            return new ResponseEntity<>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Add a product to the database",
            description = "Add a product to the database",
            security = {@SecurityRequirement(name = "Bearer Authentication")})
    @PostMapping
    @ResponseBody
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product savedProduct = productService.addProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @Operation(summary = "Filter products")
    @GetMapping("/filter")
    public ResponseEntity<List<FullProductDto>> filterProducts(@RequestParam(required = false) String name,
                                                               @RequestParam(required = false) String provider,
                                                               @RequestParam(required = false) String brand,
                                                               @RequestParam(required = false) Integer volumeFrom,
                                                               @RequestParam(required = false) Integer volumeTo,
                                                               @RequestParam(required = false) String ingredient) {
        List<FullProductDto> products = productService.filterProducts(
                name,
                provider,
                brand,
                volumeFrom, volumeTo,
                ingredient
        );

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

}
