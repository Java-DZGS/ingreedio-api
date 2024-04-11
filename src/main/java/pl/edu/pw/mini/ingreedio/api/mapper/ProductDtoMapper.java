package pl.edu.pw.mini.ingreedio.api.mapper;

import java.util.function.Function;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.dto.ProductDto;
import pl.edu.pw.mini.ingreedio.api.model.Product;

@Service
public class ProductDtoMapper implements Function<Product, ProductDto> {
    @Override
    public ProductDto apply(Product product) {
        return ProductDto.builder()
            .id(product.getId())
            .name(product.getName())
            .smallImageUrl(product.getSmallImageUrl())
            .provider(product.getProvider())
            .shortDescription(product.getShortDescription())
            .build();
    }
}
