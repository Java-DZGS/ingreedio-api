package pl.edu.pw.mini.ingreedio.api.ingredient;

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
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
import pl.edu.pw.mini.ingreedio.api.ingredient.model.Ingredient;
import pl.edu.pw.mini.ingreedio.api.ingredient.service.IngredientService;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
@Tag(name = "Ingredients")
public class IngredientController {
    private final IngredientService ingredientService;
    private final ModelMapper modelMapper;

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
        @RequestParam(name = "skip-allergens", defaultValue = "true") boolean skipAllergens) {
        if (authentication != null && authentication.isAuthenticated()) {
            List<IngredientDto> ingredientDtoList = ingredientService
                .getIngredients(
                    count,
                    query.toUpperCase(),
                    ((AuthInfo) authentication.getPrincipal()).getUser(), skipAllergens)
                .stream()
                .map((ingredient) -> modelMapper.map(ingredient,
                    IngredientDto.IngredientDtoBuilder.class).build()).toList();

            return ResponseEntity.ok(ingredientDtoList);
        }

        List<IngredientDto> ingredientDtoList = ingredientService
            .getIngredients(count, query.toUpperCase())
            .stream()
            .map((ingredient) -> modelMapper.map(ingredient,
                IngredientDto.IngredientDtoBuilder.class).build()).toList();
        return ResponseEntity.ok(ingredientDtoList);
    }

    @Operation(summary = "Get ingredient by ID",
        description = "Fetches an ingredient based on the provided ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ingredient retrieved successfully",
            content = @Content(schema = @Schema(implementation = IngredientDto.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<IngredientDto> getIngredientById(@PathVariable long id) {
        Ingredient ingredient = ingredientService.getIngredientById(id);
        IngredientDto ingredientDto = modelMapper.map(
            ingredient, IngredientDto.IngredientDtoBuilder.class).build();
        return ResponseEntity.ok(ingredientDto);
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
        Set<IngredientDto> ingredientDtos = ingredientService
            .getIngredientsByIds(ingredientsIds)
            .stream()
            .map((ingredient) -> modelMapper.map(
                ingredient, IngredientDto.IngredientDtoBuilder.class).build())
            .collect(Collectors.toSet());
        return ResponseEntity.ok(ingredientDtos);
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
    public ResponseEntity<List<IngredientDto>> getLikedIngredients(Authentication authentication) {
        List<IngredientDto> ingredientDtos = ingredientService
            .getLikedIngredients(((AuthInfo) authentication.getPrincipal()).getUser())
            .stream()
            .map((ingredient) -> modelMapper.map(
                ingredient, IngredientDto.IngredientDtoBuilder.class).build())
            .toList();
        return ResponseEntity.ok(ingredientDtos);
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
    public ResponseEntity<List<IngredientDto>> getAllergens(Authentication authentication) {
        List<IngredientDto> ingredientDtos = ingredientService
            .getAllergens(((AuthInfo) authentication.getPrincipal()).getUser())
            .stream()
            .map((ingredient) -> modelMapper.map(
                ingredient, IngredientDto.IngredientDtoBuilder.class).build())
            .toList();
        return ResponseEntity.ok(ingredientDtos);
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
    public ResponseEntity<Void> likeIngredient(
        Authentication authentication,
        @PathVariable long id) {
        ingredientService.likeIngredient(id, ((AuthInfo) authentication.getPrincipal()).getUser());
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
    public ResponseEntity<Void> unlikeIngredient(
        Authentication authentication,
        @PathVariable long id) {
        ingredientService.unlikeIngredient(
            id, ((AuthInfo) authentication.getPrincipal()).getUser());
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
    public ResponseEntity<Void> addAllergen(
        Authentication authentication,
        @PathVariable Long id) {
        ingredientService.addAllergen(id, ((AuthInfo) authentication.getPrincipal()).getUser());
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
    public ResponseEntity<Void> removeAllergen(
        Authentication authentication,
        @PathVariable Long id) {
        ingredientService.removeAllergen(id, ((AuthInfo) authentication.getPrincipal()).getUser());
        return ResponseEntity.ok().build();
    }
}
