package pl.edu.pw.mini.ingreedio.api.product.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.product.dto.ReviewDto;
import pl.edu.pw.mini.ingreedio.api.product.mapper.ReviewDtoMapper;
import pl.edu.pw.mini.ingreedio.api.product.model.Review;
import pl.edu.pw.mini.ingreedio.api.product.repository.ReviewRepository;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewDtoMapper reviewDtoMapper;

    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    public Optional<Review> addReview(Long userId, Review review) {
        Optional<Review> reviewOptional = reviewRepository.findByUserIdAndProductId(userId,
            review.getProductId());
        if (reviewOptional.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(reviewRepository.save(review));
    }

    public Optional<Review> editReview(Long userId, Review updatedReview) {
        Optional<Review> reviewOptional = reviewRepository
            .findByUserIdAndProductId(userId, updatedReview.getProductId());
        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            review.setContent(updatedReview.getContent());
            review.setRating(updatedReview.getRating());

            return Optional.of(reviewRepository.save(review));
        }
        return Optional.empty();
    }

    public void deleteReview(Long userId, Long productId) {
        Optional<Review> reviewOptional = reviewRepository
            .findByUserIdAndProductId(userId, productId);
        reviewOptional.ifPresent(review -> reviewRepository.deleteById(review.getId()));
    }

    public List<ReviewDto> getProductReviews(Long productId) {
        return reviewRepository.getProductReviews(productId)
            .stream()
            .map(reviewDtoMapper)
            .collect(Collectors.toList());
    }
}
