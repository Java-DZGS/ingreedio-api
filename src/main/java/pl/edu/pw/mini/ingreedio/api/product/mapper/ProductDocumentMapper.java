package pl.edu.pw.mini.ingreedio.api.product.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.edu.pw.mini.ingreedio.api.product.dto.ProductDto;
import pl.edu.pw.mini.ingreedio.api.product.dto.ProductViewDto;
import pl.edu.pw.mini.ingreedio.api.product.model.ProductDocument;

//@Component
//@AllArgsConstructor
//private class ProductDocumentMapper {
//    public ProductDto toDto(ProductDocument product) {
//        return ProductDto.builder()
//            .id(product.getId())
//            .name(product.getName())
//            .largeImageUrl(product.getLargeImageUrl())
//            .provider(providerDocumentMapper.toDto(product.getProvider()))
//            .brand(brandDocumentMapper.toDto(product.getBrand()))
//            .categories(product.getCategories()
//                .stream()
//                .map(categoryDocumentMapper::toDto)
//                .toList())
//            .longDescription(product.getLongDescription())
//            .volume(product.getVolume())
//            .rating(product.getRating())
//            .ingredients(product.getIngredients()
//                .stream()
//                .map(ingredientDocumentMapper::toDto)
//                .toList())
//            // TODO: liked
//            .build();
//    }
//
//    public ProductViewDto toViewDto(ProductDocument product) {
//        return ProductViewDto.builder()
//            .id(product.getId())
//            .name(product.getName())
//            .provider(product.getProvider().getName())
//            .brand(product.getBrand().getName())
//            .rating(product.getRating())
//            // TODO: liked
//            .build();
//    }
//}