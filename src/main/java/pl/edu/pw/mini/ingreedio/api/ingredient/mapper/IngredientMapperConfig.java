package pl.edu.pw.mini.ingreedio.api.ingredient.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.edu.pw.mini.ingreedio.api.common.mapping.BuilderConverter;
import pl.edu.pw.mini.ingreedio.api.common.mapping.MapperConfig;
import pl.edu.pw.mini.ingreedio.api.ingredient.dto.IngredientDto;
import pl.edu.pw.mini.ingreedio.api.ingredient.dto.IngredientDto.IngredientDtoBuilder;
import pl.edu.pw.mini.ingreedio.api.ingredient.model.Ingredient;

@Component
public class IngredientMapperConfig implements MapperConfig {
    @Override
    public void setupMapper(ModelMapper mapper) {
        mapper.addConverter(new BuilderConverter<>(IngredientDtoBuilder::build,
            IngredientDtoBuilder.class), Ingredient.class, IngredientDto.class);
    }
}
