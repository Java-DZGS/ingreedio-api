package pl.edu.pw.mini.ingreedio.api.brand.mapper;

import java.util.function.Function;
import pl.edu.pw.mini.ingreedio.api.brand.dto.BrandDto;
import pl.edu.pw.mini.ingreedio.api.brand.model.Brand;

public class BrandDtoMapper implements Function<Brand, BrandDto> {
    @Override
    public BrandDto apply(Brand brand) {
        return BrandDto.builder()
            .id(brand.getId())
            .name(brand.getName())
            .build();
    }
}
