package pl.edu.pw.mini.ingreedio.api.review.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.auth.exception.NotLoggedInException;
import pl.edu.pw.mini.ingreedio.api.auth.service.AuthService;
import pl.edu.pw.mini.ingreedio.api.review.dto.ReportDto;
import pl.edu.pw.mini.ingreedio.api.review.dto.ReviewDto;
import pl.edu.pw.mini.ingreedio.api.review.exception.ReportEmptyReviewAttemptException;
import pl.edu.pw.mini.ingreedio.api.review.exception.ReviewNotFoundException;
import pl.edu.pw.mini.ingreedio.api.review.mapper.ReportDtoMapper;
import pl.edu.pw.mini.ingreedio.api.review.mapper.ReviewDtoMapper;
import pl.edu.pw.mini.ingreedio.api.review.model.Report;
import pl.edu.pw.mini.ingreedio.api.review.model.Review;
import pl.edu.pw.mini.ingreedio.api.review.repository.ReportRepository;
import pl.edu.pw.mini.ingreedio.api.review.repository.ReviewRepository;
import pl.edu.pw.mini.ingreedio.api.user.model.User;
import pl.edu.pw.mini.ingreedio.api.user.repository.UserRepository;
import pl.edu.pw.mini.ingreedio.api.user.service.UserService;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewDtoMapper reviewDtoMapper;
    private final ReportRepository reportRepository;
    private final ReportDtoMapper reportDtoMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public Optional<ReviewDto> getReviewById(Long id) {
        return reviewRepository.findById(id)
            .map(reviewDtoMapper);
    }

    @Transactional
    public ReportDto reportReview(Long reviewId, Long userId, String content)
        throws ReportEmptyReviewAttemptException, ReviewNotFoundException {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);

        if (optionalReview.isEmpty()) {
            throw new ReviewNotFoundException(reviewId);
        }

        if (optionalReview.get().getContent().isEmpty()) {
            throw new ReportEmptyReviewAttemptException();
        }

        Report report = Report.builder()
            .review(optionalReview.get())
            .user(userRepository.findById(userId).get())
            .content(content)
            .build();

        return reportDtoMapper.apply(reportRepository.save(report));
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

        return Optional.of(reviewDtoMapper.apply(addedReview, user));
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

            return Optional.of(reviewDtoMapper.apply(editedReview, user));
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

    @Transactional(readOnly = true)
    public List<ReviewDto> getProductReviews(Long productId, User user) {
        return reviewRepository.getProductReviews(productId)
            .stream()
            .sorted((first, second) -> -Boolean.compare(
                first.getUser().getId().equals(user.getId()),
                second.getUser().getId().equals(user.getId())))
            .map(review -> reviewDtoMapper.apply(review, user))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ReviewDto> getProductUserReview(User user, Long productId) {
        Optional<Review> reviewOptional = reviewRepository
            .findByUserIdAndProductId(user.getId(), productId);
        return reviewOptional.map(reviewDtoMapper);
    }

    @Transactional
    public void likeReview(Long reviewId)
        throws NotLoggedInException, ReviewNotFoundException {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());

        if (userOptional.isEmpty()) {
            throw new NotLoggedInException();
        }

        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);

        if (reviewOptional.isEmpty()) {
            throw new ReviewNotFoundException(reviewId);
        }

        Set<User> likingUsers = reviewOptional.get().getLikingUsers();
        Set<User> dislikingUsers = reviewOptional.get().getDislikingUsers();
        likingUsers.add(userOptional.get());
        dislikingUsers.remove(userOptional.get());
        reviewOptional.get().setLikingUsers(likingUsers);
        reviewOptional.get().setDislikingUsers(dislikingUsers);

        reviewRepository.save(reviewOptional.get());
        userService.likeReview(userOptional.get(), reviewOptional.get());
    }

    @Transactional
    public void unlikeReview(Long reviewId)
        throws NotLoggedInException, ReviewNotFoundException {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());

        if (userOptional.isEmpty()) {
            throw new NotLoggedInException();
        }

        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);

        if (reviewOptional.isEmpty()) {
            throw new ReviewNotFoundException(reviewId);
        }

        Set<User> likingUsers = reviewOptional.get().getLikingUsers();
        likingUsers.remove(userOptional.get());
        reviewOptional.get().setLikingUsers(likingUsers);

        reviewRepository.save(reviewOptional.get());
        userService.unlikeReview(userOptional.get(), reviewOptional.get());
    }

    @Transactional
    public void dislikeReview(Long reviewId)
        throws NotLoggedInException, ReviewNotFoundException {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());

        if (userOptional.isEmpty()) {
            throw new NotLoggedInException();
        }

        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);

        if (reviewOptional.isEmpty()) {
            throw new ReviewNotFoundException(reviewId);
        }

        Set<User> likingUsers = reviewOptional.get().getLikingUsers();
        Set<User> dislikingUsers = reviewOptional.get().getDislikingUsers();
        likingUsers.remove(userOptional.get());
        dislikingUsers.add(userOptional.get());
        reviewOptional.get().setLikingUsers(likingUsers);
        reviewOptional.get().setDislikingUsers(dislikingUsers);

        reviewRepository.save(reviewOptional.get());
        userService.dislikeReview(userOptional.get(), reviewOptional.get());
    }

    @Transactional
    public void undislikeReview(Long reviewId)
        throws NotLoggedInException, ReviewNotFoundException {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());

        if (userOptional.isEmpty()) {
            throw new NotLoggedInException();
        }

        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);

        if (reviewOptional.isEmpty()) {
            throw new ReviewNotFoundException(reviewId);
        }

        Set<User> dislikingUsers = reviewOptional.get().getDislikingUsers();
        dislikingUsers.remove(userOptional.get());
        reviewOptional.get().setDislikingUsers(dislikingUsers);

        reviewRepository.save(reviewOptional.get());
        userService.undislikeReview(userOptional.get(), reviewOptional.get());
    }
}
