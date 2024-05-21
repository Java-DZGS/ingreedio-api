package pl.edu.pw.mini.ingreedio.api.review.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.review.dto.ReviewDto;
import pl.edu.pw.mini.ingreedio.api.review.mapper.ReviewDtoMapper;
import pl.edu.pw.mini.ingreedio.api.review.model.Review;
import pl.edu.pw.mini.ingreedio.api.review.repository.ReviewRepository;
import pl.edu.pw.mini.ingreedio.api.user.model.User;
import pl.edu.pw.mini.ingreedio.api.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewDtoMapper reviewDtoMapper;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<ReviewDto> getReviewById(Long id) {
        return reviewRepository.findById(id)
            .map(reviewDtoMapper);
    }

    @Transactional
    public Optional<ReviewDto> addReview(User user, Review review) {
        Optional<Review> reviewOptional = reviewRepository.findByUserIdAndProductId(user.getId(),
            review.getProductId());
        if (reviewOptional.isPresent()) {
            return Optional.empty();
        }
        Review addedReview = reviewRepository.save(review);

        List<Review> reviews = addedReview.getUser().getReviews();
        reviews.add(addedReview);

        userRepository.save(addedReview.getUser());

        return Optional.of(reviewDtoMapper.apply(addedReview));
    }

    @Transactional
    public Optional<ReviewDto> editReview(User user, Review updatedReview) {
        Optional<Review> reviewOptional = reviewRepository
            .findByUserIdAndProductId(user.getId(), updatedReview.getProductId());
        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            review.setContent(updatedReview.getContent());
            review.setRating(updatedReview.getRating());

            List<Review> reviews = review.getUser().getReviews();
            reviews.remove(review);
            Review editedReview = reviewRepository.save(review);
            reviews.add(editedReview);

            userRepository.save(review.getUser());

            return Optional.of(reviewDtoMapper.apply(editedReview));
        }
        return Optional.empty();
    }

    @Transactional
    public void deleteReview(User user, Long productId) {
        Optional<Review> reviewOptional = reviewRepository
            .findByUserIdAndProductId(user.getId(), productId);
        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();

            reviewRepository.deleteById(review.getId());

            List<Review> reviews = review.getUser().getReviews();
            reviews.remove(review);
            userRepository.save(review.getUser());
        }
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getProductReviews(Long productId) {
        return reviewRepository.getProductReviews(productId)
            .stream()
            .map(reviewDtoMapper)
            .collect(Collectors.toList());
    }
}
