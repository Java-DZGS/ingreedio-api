package pl.edu.pw.mini.ingreedio.api.mapper;

import java.util.function.Function;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.dto.FullProductDto;
import pl.edu.pw.mini.ingreedio.api.model.Product;

@Service
public class FullProductDtoMapper implements Function<Product, FullProductDto> {
    @Override
    public FullProductDto apply(Product product) {
        return FullProductDto.builder()
            .id(product.getId())
            .name(product.getName())
            .largeImageUrl(product.getLargeImageUrl())
            .provider(product.getProvider())
            .brand(product.getBrand())
            .longDescription(product.getLongDescription())
            .volume(product.getVolume())
            .rating(product.getRating())
            .ingredients(product.getIngredients())
            .build();
    }
}