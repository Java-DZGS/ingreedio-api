package pl.edu.pw.mini.ingreedio.api.brand;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.mini.ingreedio.api.brand.model.Brand;
import pl.edu.pw.mini.ingreedio.api.brand.service.BrandService;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@Tag(name = "Brands")
public class BrandController {
    private final BrandService brandService;

    @Operation(summary = "Get brand by id", description = "Get brand by id")
    @GetMapping("/{id}")
    public ResponseEntity<Brand> getBrandsByIds(@PathVariable long id) {
        return ResponseEntity.ok(brandService.getBrandById(id));
    }

    @Operation(summary = "Get brands by ids", description = "Get brands by ids")
    @GetMapping("/get-by")
    public ResponseEntity<Set<Brand>> getBrandsByIds(@RequestParam("ids") Set<Long> brandIds) {
        return ResponseEntity.ok(brandService.getBrandsByIds(brandIds));
    }
}
