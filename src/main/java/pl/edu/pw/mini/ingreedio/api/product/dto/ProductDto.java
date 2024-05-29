package pl.edu.pw.mini.ingreedio.api.product.dto;

import lombok.Builder;

@Builder
public record ProductDto(Long id, String name, String brand, String smallImageUrl, String provider,
                         String shortDescription, Boolean isLiked, Integer rating) {

}
