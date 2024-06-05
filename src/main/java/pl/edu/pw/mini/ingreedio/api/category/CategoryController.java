package pl.edu.pw.mini.ingreedio.api.category;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.mini.ingreedio.api.category.model.Category;
import pl.edu.pw.mini.ingreedio.api.category.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category")
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "Get category by id", description = "Get category by id")
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @Operation(summary = "Get categories by ids", description = "Get categories by ids")
    @GetMapping("/get-by")
    public ResponseEntity<Set<Category>> getCategoriesByIds(
        @RequestParam("ids") Set<Long> categoryIds) {
        return ResponseEntity.ok(categoryService.getCategoriesByIds(categoryIds));
    }
}
