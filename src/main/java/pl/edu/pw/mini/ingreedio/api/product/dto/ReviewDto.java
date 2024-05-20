package pl.edu.pw.mini.ingreedio.api.product.dto;

import java.sql.Timestamp;
import lombok.Builder;

@Builder
public record ReviewDto(Long userId,
                        String displayName,
                        Long productId,
                        Integer rating,
                        String content,
                        Timestamp createdAt) {
}
