package pl.edu.pw.mini.ingreedio.api.product.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.edu.pw.mini.ingreedio.api.brand.dto.BrandDto;
import pl.edu.pw.mini.ingreedio.api.brand.dto.BrandDto.BrandDtoBuilder;
import pl.edu.pw.mini.ingreedio.api.category.dto.CategoryDto;
import pl.edu.pw.mini.ingreedio.api.category.dto.CategoryDto.CategoryDtoBuilder;
import pl.edu.pw.mini.ingreedio.api.common.mapping.BuilderConverter;
import pl.edu.pw.mini.ingreedio.api.common.mapping.MapperConfig;
import pl.edu.pw.mini.ingreedio.api.common.mapping.NullableConverter;
import pl.edu.pw.mini.ingreedio.api.ingredient.dto.IngredientDto;
import pl.edu.pw.mini.ingreedio.api.ingredient.dto.IngredientDto.IngredientDtoBuilder;
import pl.edu.pw.mini.ingreedio.api.product.dto.ProductDto;
import pl.edu.pw.mini.ingreedio.api.product.dto.ProductDto.ProductDtoBuilder;
import pl.edu.pw.mini.ingreedio.api.product.dto.ProductRequestDto;
import pl.edu.pw.mini.ingreedio.api.product.model.BrandDocument;
import pl.edu.pw.mini.ingreedio.api.product.model.CategoryDocument;
import pl.edu.pw.mini.ingreedio.api.product.model.IngredientDocument;
import pl.edu.pw.mini.ingreedio.api.product.model.ProductDocument;
import pl.edu.pw.mini.ingreedio.api.product.model.ProductDocument.ProductDocumentBuilder;
import pl.edu.pw.mini.ingreedio.api.product.model.ProviderDocument;
import pl.edu.pw.mini.ingreedio.api.provider.dto.ProviderDto;
import pl.edu.pw.mini.ingreedio.api.provider.dto.ProviderDto.ProviderDtoBuilder;

@Component
public class ProductMapperConfig implements MapperConfig {
    @Override
    public void setupMapper(ModelMapper mapper) {
        // These converters are required for ProductDocument -> ProductDto mapping
        mapper.addConverter(new BuilderConverter<>(BrandDtoBuilder::build, BrandDtoBuilder.class),
            BrandDocument.class, BrandDto.class);

        mapper.addConverter(
            new BuilderConverter<>(ProviderDtoBuilder::build, ProviderDtoBuilder.class),
            ProviderDocument.class, ProviderDto.class);

        mapper.addConverter(
            new BuilderConverter<>(CategoryDtoBuilder::build, CategoryDtoBuilder.class),
            CategoryDocument.class, CategoryDto.class);

        mapper.addConverter(
            new BuilderConverter<>(IngredientDtoBuilder::build, IngredientDtoBuilder.class),
            IngredientDocument.class, IngredientDto.class);

        mapper.addConverter(
            new BuilderConverter<>(ProductDtoBuilder::build, ProductDtoBuilder.class),
            ProductDocument.class, ProductDto.class);

        // These converters are required for ProductRequestDto -> ProductDocument mapping
        mapper.addConverter(
            new NullableConverter<>(source -> CategoryDocument.builder().id(source).build()),
            Long.class, CategoryDocument.class);

        mapper.addConverter(
            new NullableConverter<>(source -> BrandDocument.builder().id(source).build()),
            Long.class, BrandDocument.class);

        mapper.addConverter(
            new NullableConverter<>(source -> ProviderDocument.builder().id(source).build()),
            Long.class, ProviderDocument.class);

        mapper.addConverter(
            new NullableConverter<>(source -> IngredientDocument.builder().id(source).build()),
            Long.class, IngredientDocument.class);

        mapper.addConverter(
            new BuilderConverter<>(ProductDocumentBuilder::build, ProductDocumentBuilder.class),
            ProductRequestDto.class, ProductDocument.class);
    }
}
