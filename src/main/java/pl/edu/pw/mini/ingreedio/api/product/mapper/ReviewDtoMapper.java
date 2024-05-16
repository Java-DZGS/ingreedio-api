package pl.edu.pw.mini.ingreedio.api.product.mapper;

import java.util.function.Function;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.product.dto.ReviewDto;
import pl.edu.pw.mini.ingreedio.api.product.model.Review;

@Service
public class ReviewDtoMapper implements Function<Review, ReviewDto> {
    @Override
    public ReviewDto apply(Review review) {
        return ReviewDto.builder()
            .displayName(review.getUser().getDisplayName())
            .productId(review.getProductId())
            .rating(review.getRating())
            .content(review.getContent())
            .build();
    }
}
