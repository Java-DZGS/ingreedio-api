package pl.edu.pw.mini.ingreedio.api.product.mapper;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.edu.pw.mini.ingreedio.api.brand.dto.BrandDto;
import pl.edu.pw.mini.ingreedio.api.category.dto.CategoryDto;
import pl.edu.pw.mini.ingreedio.api.common.MapperConfig;
import pl.edu.pw.mini.ingreedio.api.ingredient.dto.IngredientDto;
import pl.edu.pw.mini.ingreedio.api.product.model.BrandDocument;
import pl.edu.pw.mini.ingreedio.api.product.model.CategoryDocument;
import pl.edu.pw.mini.ingreedio.api.product.model.IngredientDocument;
import pl.edu.pw.mini.ingreedio.api.product.model.ProviderDocument;
import pl.edu.pw.mini.ingreedio.api.provider.dto.ProviderDto;

@Component
public class ProductMapperConfig implements MapperConfig {
    @Override
    public void setupMapper(ModelMapper mapper) {
        // These converters are required for ProductDocument -> ProductDto mapping
        mapper.addConverter(new AbstractConverter<BrandDocument, BrandDto>() {
            @Override
            protected BrandDto convert(BrandDocument source) {
                return BrandDto.builder().id(source.getId()).name(source.getName()).build();
            }
        });

        mapper.addConverter(new AbstractConverter<ProviderDocument, ProviderDto>() {
            @Override
            protected ProviderDto convert(ProviderDocument source) {
                return ProviderDto.builder().id(source.getId()).name(source.getName()).build();
            }
        });

        mapper.addConverter(new AbstractConverter<CategoryDocument, CategoryDto>() {
            @Override
            protected CategoryDto convert(CategoryDocument source) {
                return CategoryDto.builder().id(source.getId()).name(source.getName()).build();
            }
        });

        mapper.addConverter(new AbstractConverter<IngredientDocument, IngredientDto>() {
            @Override
            protected IngredientDto convert(IngredientDocument source) {
                return IngredientDto.builder().id(source.getId()).name(source.getName()).build();
            }
        });

        // These converters are required for ProductRequestDto -> ProductDocument mapping
        mapper.addConverter(new AbstractConverter<Long, CategoryDocument>() {
            @Override
            protected CategoryDocument convert(Long source) {
                return source == null ? null : CategoryDocument.builder().id(source).build();
            }
        });

        mapper.addConverter(new AbstractConverter<Long, BrandDocument>() {
            @Override
            protected BrandDocument convert(Long source) {
                return source == null ? null : BrandDocument.builder().id(source).build();
            }
        });

        mapper.addConverter(new AbstractConverter<Long, ProviderDocument>() {
            @Override
            protected ProviderDocument convert(Long source) {
                return source == null ? null : ProviderDocument.builder().id(source).build();
            }
        });

        mapper.addConverter(new AbstractConverter<Long, IngredientDocument>() {
            @Override
            protected IngredientDocument convert(Long source) {
                return source == null ? null : IngredientDocument.builder().id(source).build();
            }
        });
    }
}
