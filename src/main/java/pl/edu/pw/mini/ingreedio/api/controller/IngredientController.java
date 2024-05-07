package pl.edu.pw.mini.ingreedio.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.mini.ingreedio.api.dto.IngredientDto;
import pl.edu.pw.mini.ingreedio.api.dto.ProductDto;
import pl.edu.pw.mini.ingreedio.api.model.Ingredient;
import pl.edu.pw.mini.ingreedio.api.service.IngredientService;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
@Tag(name = "Ingredients" /*, description = "..."*/)
public class IngredientController {
    private final IngredientService ingredientService;

    @Operation(summary = "Get liked ingredients", description = "Get liked ingredients")
    @GetMapping("/liked")
    public ResponseEntity<List<IngredientDto>> getLikedIngredients() {
        return ResponseEntity.ok(List.of());
    }

    @Operation(summary = "Get allergens", description = "Get allergens")
    @GetMapping("/allergens")
    public ResponseEntity<List<IngredientDto>> getAllergens() {
        return ResponseEntity.ok(List.of());
    }

    @Operation(summary = "Like an ingredient", description = "Like an ingredient")
    @PostMapping("/{id}/likes")
    public ResponseEntity<Void> likeIngredient() {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Add allergen", description = "Add allergen")
    @GetMapping("/{id}/allergens")
    public ResponseEntity<Void> addAllergen() {
        return ResponseEntity.ok().build();
    }

}
