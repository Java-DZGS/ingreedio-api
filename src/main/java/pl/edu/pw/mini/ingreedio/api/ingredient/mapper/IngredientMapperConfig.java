package pl.edu.pw.mini.ingreedio.api.ingredient.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.edu.pw.mini.ingreedio.api.common.MapperConfig;

@Component
public class IngredientMapperConfig implements MapperConfig {
    @Override
    public void setupMapper(ModelMapper mapper) {
    }
}
