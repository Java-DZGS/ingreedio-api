package pl.edu.pw.mini.ingreedio.api.ingredient.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.mini.ingreedio.api.auth.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.ingredient.dto.IngredientDto;
import pl.edu.pw.mini.ingreedio.api.ingredient.service.IngredientService;
import pl.edu.pw.mini.ingreedio.api.product.exception.IngredientNotFoundException;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
@Tag(name = "Ingredients")
public class IngredientController {
    private final IngredientService ingredientService;

    @Operation(summary = "Search ingredients",
        description = "Fetches a list of ingredients based on the provided query and limits "
                      + "the results to the specified count.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ingredients retrieved successfully",
            content = @Content(schema = @Schema(implementation = IngredientDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<IngredientDto>> getIngredients(
        Authentication authentication,
        @RequestParam(defaultValue = "10") int count,
        @RequestParam(defaultValue = "") String query,
        @RequestParam(defaultValue = "true") Boolean skipAllergens) {
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok(
                ingredientService.getIngredients(count, query,
                    ((AuthInfo) authentication.getPrincipal()).getUser(), skipAllergens));
        }

        return ResponseEntity.ok(ingredientService.getIngredients(count, query));
    }

    @Operation(summary = "Get ingredients by IDs",
        description = "Fetches a set of ingredients based on the provided set of ingredient IDs."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ingredients retrieved successfully",
            content = @Content(
                array = @ArraySchema(schema = @Schema(implementation = IngredientDto.class))
            ))
    })
    @GetMapping("/get-by")
    public ResponseEntity<Set<IngredientDto>> getIngredientsByIds(
        @RequestParam("ids") Set<Long> ingredientsIds) {
        return ResponseEntity.ok(ingredientService.getIngredientsByIds(ingredientsIds));
    }

    @Operation(summary = "Get liked ingredients",
        description = "Fetches a list of ingredients liked by the user.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liked ingredients retrieved successfully",
            content = @Content(
                array = @ArraySchema(schema = @Schema(implementation = IngredientDto.class))
            ))
    })
    @GetMapping("/liked")
    public ResponseEntity<List<IngredientDto>> getLikedIngredients() {
        List<IngredientDto> likedIngredients = ingredientService.getLikedIngredients();
        return ResponseEntity.ok(likedIngredients);
    }

    @Operation(summary = "Get allergens",
        description = "Fetches a list of ingredients that are classified as allergens.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Allergens retrieved successfully",
            content = @Content(
                array = @ArraySchema(schema = @Schema(implementation = IngredientDto.class))))
    })
    @GetMapping("/allergens")
    public ResponseEntity<List<IngredientDto>> getAllergens() {
        List<IngredientDto> allergens = ingredientService.getAllergens();
        return ResponseEntity.ok(allergens);
    }

    @Operation(summary = "Like an ingredient",
        description = "Likes an ingredient based on the provided ingredient ID.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ingredient liked successfully",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Ingredient not found", content = @Content)
    })
    @PostMapping("/{id}/likes")
    public ResponseEntity<Void> likeIngredient(@PathVariable Long id) {
        boolean likeSucceeded = ingredientService.likeIngredient(id);
        if (!likeSucceeded) {
            throw new IngredientNotFoundException(id);
        }

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Unlike an ingredient",
        description = "Unlikes an ingredient based on the provided ingredient ID.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ingredient unliked successfully",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Ingredient not found", content = @Content)
    })
    @DeleteMapping("/{id}/likes")
    public ResponseEntity<Void> unlikeIngredient(@PathVariable Long id) {
        boolean unlikeSucceeded = ingredientService.unlikeIngredient(id);
        if (!unlikeSucceeded) {
            throw new IngredientNotFoundException(id);
        }

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Add an allergen",
        description = "Marks an ingredient as an allergen based on the provided ingredient ID.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            description = "Ingredient marked as allergen successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "Ingredient not found", content = @Content)
    })
    @PostMapping("/{id}/allergens")
    public ResponseEntity<Void> addAllergen(@PathVariable Long id) {
        boolean addAllergenSucceeded = ingredientService.addAllergen(id);
        if (!addAllergenSucceeded) {
            throw new IngredientNotFoundException(id);
        }

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove an allergen",
        description = "Unmarks an ingredient as an allergen based on the provided ingredient ID.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            description = "Ingredient unmarked as allergen successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "Ingredient not found", content = @Content)
    })
    @DeleteMapping("/{id}/allergens")
    public ResponseEntity<Void> removeAllergen(@PathVariable Long id) {
        boolean removeAllergenSucceeded = ingredientService.removeAllergen(id);
        if (!removeAllergenSucceeded) {
            throw new IngredientNotFoundException(id);
        }

        return ResponseEntity.ok().build();
    }
}
