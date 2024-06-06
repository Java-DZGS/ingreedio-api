package pl.edu.pw.mini.ingreedio.api.brand;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.mini.ingreedio.api.brand.dto.BrandDto;
import pl.edu.pw.mini.ingreedio.api.brand.model.Brand;
import pl.edu.pw.mini.ingreedio.api.brand.service.BrandService;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@Tag(name = "Brands")
public class BrandController {
    private final BrandService brandService;
    private final ModelMapper modelMapper;

    @Operation(summary = "Get brand by ID", description = "Get brand by ID")
    @GetMapping("/{id}")
    public ResponseEntity<BrandDto> getBrandById(@PathVariable long id) {
        return ResponseEntity.ok(modelMapper.map(brandService.getBrandById(id),
            BrandDto.BrandDtoBuilder.class).build());
    }

    @Operation(summary = "Get brands by IDs", description = "Get brands by IDs")
    @GetMapping("/get-by")
    public ResponseEntity<Set<BrandDto>> getBrandsByIds(@RequestParam("ids") Set<Long> brandIds) {
        Set<Brand> brands = brandService.getBrandsByIds(brandIds);
        Set<BrandDto> brandDtos = brands.stream()
                .map(brand -> modelMapper.map(brand, BrandDto.BrandDtoBuilder.class).build())
                .collect(Collectors.toSet());
        return ResponseEntity.ok(brandDtos);
    }

    @Operation(summary = "Get all brands", description = "Get all existing brands")
    @GetMapping
    public ResponseEntity<List<BrandDto>> getAllBrands() {
        List<Brand> brands = brandService.getAllBrands();
        List<BrandDto> brandDtos = brands.stream()
                .map(brand -> modelMapper.map(brand, BrandDto.BrandDtoBuilder.class).build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(brandDtos);
    }
}
