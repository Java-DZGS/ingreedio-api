package pl.edu.pw.mini.ingreedio.api.category;

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
import pl.edu.pw.mini.ingreedio.api.category.dto.CategoryDto;
import pl.edu.pw.mini.ingreedio.api.category.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category")
public class CategoryController {
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;

    @Operation(summary = "Get category by ID", description = "Get category by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable long id) {
        return ResponseEntity.ok(modelMapper.map(categoryService.getCategoryById(id),
            CategoryDto.CategoryDtoBuilder.class).build());
    }

    @Operation(summary = "Get categories by IDs", description = "Get categories by IDs")
    @GetMapping("/get-by")
    public ResponseEntity<Set<CategoryDto>> getCategoriesByIds(
        @RequestParam("ids") Set<Long> categoryIds) {
        return ResponseEntity.ok(categoryService.getCategoriesByIds(categoryIds)
            .stream()
            .map(category -> modelMapper
                .map(category, CategoryDto.CategoryDtoBuilder.class)
                .build())
            .collect(Collectors.toSet()));
    }

    @Operation(summary = "Get all categories", description = "Get all existing categories")
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories()
            .stream()
            .map(category -> modelMapper
                .map(category, CategoryDto.CategoryDtoBuilder.class)
                .build())
            .toList());
    }
}
