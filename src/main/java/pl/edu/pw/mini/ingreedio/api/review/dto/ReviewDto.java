package pl.edu.pw.mini.ingreedio.api.review.dto;

import java.sql.Timestamp;
import lombok.Builder;

@Builder
public record ReviewDto(Long reviewId,
                        long userId,
                        String displayName,
                        Long productId,
                        Integer rating,
                        String content,
                        Timestamp createdAt,
                        Integer likesCount,
                        Integer dislikesCount,
                        Boolean isCurrentUser,
                        Boolean isLiked,
                        Boolean isDisliked) {
}
