package pl.edu.pw.mini.ingreedio.api.mapper;

import java.util.function.Function;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.dto.IngredientDto;
import pl.edu.pw.mini.ingreedio.api.model.Ingredient;

@Service
public class IngredientDtoMapper implements Function<Ingredient, IngredientDto> {
    @Override
    public IngredientDto apply(Ingredient ingredient) {
        return IngredientDto.builder()
            .id(ingredient.getId())
            .name(ingredient.getName())
            .build();
    }
}