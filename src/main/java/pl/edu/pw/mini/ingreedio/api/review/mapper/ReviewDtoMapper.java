package pl.edu.pw.mini.ingreedio.api.review.mapper;

import java.util.function.Function;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.review.dto.ReviewDto;
import pl.edu.pw.mini.ingreedio.api.review.model.Review;
import pl.edu.pw.mini.ingreedio.api.user.model.User;

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
            .likesCount(review.getLikingUsers().size())
            .dislikesCount(review.getDislikingUsers().size())
            .build();
    }

    public ReviewDto apply(Review review, User user) {
        return ReviewDto.builder()
            .reviewId(review.getId())
            .displayName(review.getUser().getDisplayName())
            .productId(review.getProductId())
            .rating(review.getRating())
            .content(review.getContent())
            .createdAt(review.getCreatedAt())
            .userId(review.getUser().getId())
            .likesCount(review.getLikingUsers().size())
            .dislikesCount(review.getDislikingUsers().size())
            .isCurrentUser(review.getUser().getId().equals(user.getId()))
            .isLiked(review.getLikingUsers().contains(user))
            .isDisliked(review.getDislikingUsers().contains(user))
            .build();
    }
}
