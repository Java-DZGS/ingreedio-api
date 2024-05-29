package pl.edu.pw.mini.ingreedio.api.product.mapper;

import java.util.function.Function;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.product.dto.ProductDto;
import pl.edu.pw.mini.ingreedio.api.product.model.Product;

@Service
public class ProductDtoMapper implements Function<Product, ProductDto> {
    @Override
    public ProductDto apply(Product product) {
        return ProductDto.builder()
            .id(product.getId())
            .name(product.getName())
            .brand(product.getBrand())
            .rating(product.getRating())
            .smallImageUrl(product.getSmallImageUrl())
            .provider(product.getProvider())
            .shortDescription(product.getShortDescription())
            .build();
    }

    public ProductDto applyWithIsLiked(Product product, boolean isLiked) {
        return ProductDto.builder()
            .id(product.getId())
            .name(product.getName())
            .brand(product.getBrand())
            .rating(product.getRating())
            .smallImageUrl(product.getSmallImageUrl())
            .provider(product.getProvider())
            .shortDescription(product.getShortDescription())
            .isLiked(isLiked)
            .build();
    }
}
