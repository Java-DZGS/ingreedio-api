package pl.edu.pw.mini.ingreedio.api.category.mapper;

import java.util.function.Function;
import pl.edu.pw.mini.ingreedio.api.category.dto.CategoryDto;
import pl.edu.pw.mini.ingreedio.api.category.model.Category;

public class CategoryDtoMapper implements Function<Category, CategoryDto> {
    @Override
    public CategoryDto apply(Category category) {
        return CategoryDto.builder()
            .id(category.getId())
            .name(category.getName())
            .build();
    }
}
