package pl.edu.pw.mini.ingreedio.api.brand.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.mini.ingreedio.api.brand.model.Brand;
import pl.edu.pw.mini.ingreedio.api.brand.service.BrandService;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@Tag(name = "Brands")
public class BrandController {
    private final BrandService brandService;

    @Operation(summary = "Get brands by ids",
        description = "Get brands by ids")
    @GetMapping("/get-by")
    public ResponseEntity<Set<Brand>> getBrandsByIds(
        @RequestParam("ids") Set<Long> brandIds) {
        return ResponseEntity.ok(brandService.getBrandsByIds(brandIds));
    }
}
