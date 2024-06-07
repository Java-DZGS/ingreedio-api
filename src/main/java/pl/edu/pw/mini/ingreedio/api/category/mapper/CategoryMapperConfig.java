package pl.edu.pw.mini.ingreedio.api.category.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.edu.pw.mini.ingreedio.api.category.dto.CategoryDto;
import pl.edu.pw.mini.ingreedio.api.category.dto.CategoryDto.CategoryDtoBuilder;
import pl.edu.pw.mini.ingreedio.api.category.model.Category;
import pl.edu.pw.mini.ingreedio.api.common.mapping.BuilderConverter;
import pl.edu.pw.mini.ingreedio.api.common.mapping.MapperConfig;

@Component
public class CategoryMapperConfig implements MapperConfig {
    @Override
    public void setupMapper(ModelMapper mapper) {
        mapper.addConverter(new BuilderConverter<>(CategoryDtoBuilder::build,
            CategoryDtoBuilder.class), Category.class, CategoryDto.class);
    }
}
