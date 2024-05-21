package pl.edu.pw.mini.ingreedio.api.ingredient.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.mini.ingreedio.api.ingredient.dto.IngredientDto;
import pl.edu.pw.mini.ingreedio.api.ingredient.service.IngredientService;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
@Tag(name = "Ingredients" /*, description = "..."*/)
public class IngredientController {
    private final IngredientService ingredientService;

    @Operation(summary = "Search ingredients",
        description = "Search ingredients")
    @GetMapping
    public ResponseEntity<List<IngredientDto>> getIngredients(
        @RequestParam int count,
        @RequestParam(required = false) String query) {
        String name = query != null ? query : "";
        List<IngredientDto> matchingIngredients = ingredientService.getIngredients(name);
        if (matchingIngredients.size() > count) {
            matchingIngredients = matchingIngredients.subList(0, count);
        }
        return ResponseEntity.ok(matchingIngredients);
    }

    @Operation(summary = "Get full ingredients by ids",
        description = "Get full ingredients by ids")
    @GetMapping("/get-by")
    public ResponseEntity<Set<IngredientDto>> getIngredientsByIds(
        @RequestParam("ids") Set<Long> ingredientsIds) {
        return ResponseEntity.ok(ingredientService.getIngredientsByIds(ingredientsIds));
    }

    @Operation(summary = "Get liked ingredients",
        description = "Get liked ingredients",
        security = {@SecurityRequirement(name = "Bearer Authentication")})
    @GetMapping("/liked")
    public ResponseEntity<List<IngredientDto>> getLikedIngredients() {
        List<IngredientDto> likedIngredients = ingredientService.getLikedIngredients();
        return ResponseEntity.ok(likedIngredients);
    }

    @Operation(summary = "Get allergens",
        description = "Get allergens",
        security = {@SecurityRequirement(name = "Bearer Authentication")})
    @GetMapping("/allergens")
    public ResponseEntity<List<IngredientDto>> getAllergens() {
        List<IngredientDto> allergens = ingredientService.getAllergens();
        return ResponseEntity.ok(allergens);
    }

    @Operation(summary = "Like an ingredient",
        description = "Like an ingredient",
        security = {@SecurityRequirement(name = "Bearer Authentication")})
    @PostMapping("/{id}/likes")
    public ResponseEntity<Void> likeIngredient(@PathVariable Long id) {
        boolean likeSucceeded = ingredientService.likeIngredient(id);
        if (likeSucceeded) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Unlike ingredient",
        description = "Unlike ingredient",
        security = {@SecurityRequirement(name = "Bearer Authentication")})
    @DeleteMapping("/{id}/likes")
    public ResponseEntity<Void> unlikeIngredient(@PathVariable Long id) {
        boolean unlikeSucceeded = ingredientService.unlikeIngredient(id);
        if (unlikeSucceeded) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Add allergen",
        description = "Add allergen",
        security = {@SecurityRequirement(name = "Bearer Authentication")})
    @PostMapping("/{id}/allergens")
    public ResponseEntity<Void> addAllergen(@PathVariable Long id) {
        boolean addAllergenSucceeded = ingredientService.addAllergen(id);
        if (addAllergenSucceeded) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Remove allergen",
        description = "Remove allergen",
        security = {@SecurityRequirement(name = "Bearer Authentication")})
    @DeleteMapping("/{id}/allergens")
    public ResponseEntity<Void> removeAllergen(@PathVariable Long id) {
        boolean removeAllergenSucceeded = ingredientService.removeAllergen(id);
        if (removeAllergenSucceeded) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
