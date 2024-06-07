package pl.edu.pw.mini.ingreedio.api.user.dto;

import java.util.Set;
import lombok.Builder;
import pl.edu.pw.mini.ingreedio.api.ingredient.dto.IngredientDto;

@Builder
public record UserDto(int userId, String email, String displayName,
                      Set<IngredientDto> likedIngredients, Set<IngredientDto> allergens) {
}
