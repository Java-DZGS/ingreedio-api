package pl.edu.pw.mini.ingreedio.api.product.dto;

import lombok.Builder;

@Builder
public record ReviewDto(String displayName, Long productId, Integer rating, String content) {
}
