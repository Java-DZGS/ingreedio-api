package pl.edu.pw.mini.ingreedio.api.review.mapper;

import java.util.function.Function;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.review.dto.ReviewDto;
import pl.edu.pw.mini.ingreedio.api.review.model.Review;

@Service
public class ReviewDtoMapper implements Function<Review, ReviewDto> {
    @Override
    public ReviewDto apply(Review review) {
        return ReviewDto.builder()
            .reviewId(review.getId())
            .displayName(review.getUser().getDisplayName())
            .productId(review.getProductId())
            .rating(review.getRating())
            .content(review.getContent())
            .createdAt(review.getCreatedAt())
            .userId(review.getUser().getId())
            .build();
    }
}
