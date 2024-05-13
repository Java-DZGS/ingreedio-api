package pl.edu.pw.mini.ingreedio.api.product.mapper;

import java.util.function.Function;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.product.dto.IngredientDto;
import pl.edu.pw.mini.ingreedio.api.product.model.Ingredient;

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