package pl.edu.pw.mini.ingreedio.api.brand.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.edu.pw.mini.ingreedio.api.brand.dto.BrandDto;
import pl.edu.pw.mini.ingreedio.api.brand.dto.BrandDto.BrandDtoBuilder;
import pl.edu.pw.mini.ingreedio.api.brand.model.Brand;
import pl.edu.pw.mini.ingreedio.api.common.mapping.BuilderConverter;
import pl.edu.pw.mini.ingreedio.api.common.mapping.MapperConfig;

@Component
public class BrandMapperConfig implements MapperConfig {
    @Override
    public void setupMapper(ModelMapper mapper) {
        mapper.addConverter(new BuilderConverter<>(BrandDtoBuilder::build,
            BrandDtoBuilder.class), Brand.class, BrandDto.class);
    }
}