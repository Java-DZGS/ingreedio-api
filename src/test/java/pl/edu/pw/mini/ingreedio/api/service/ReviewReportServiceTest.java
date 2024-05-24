package pl.edu.pw.mini.ingreedio.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import pl.edu.pw.mini.ingreedio.api.IntegrationTest;
import pl.edu.pw.mini.ingreedio.api.auth.service.AuthService;
import pl.edu.pw.mini.ingreedio.api.product.model.Product;
import pl.edu.pw.mini.ingreedio.api.product.service.ProductService;
import pl.edu.pw.mini.ingreedio.api.review.dto.ReviewDto;
import pl.edu.pw.mini.ingreedio.api.review.exception.ReportEmptyReviewAttemptException;
import pl.edu.pw.mini.ingreedio.api.review.model.Review;
import pl.edu.pw.mini.ingreedio.api.review.repository.ReviewRepository;
import pl.edu.pw.mini.ingreedio.api.review.service.ReportService;
import pl.edu.pw.mini.ingreedio.api.review.service.ReviewService;
import pl.edu.pw.mini.ingreedio.api.user.model.User;
import pl.edu.pw.mini.ingreedio.api.user.service.UserService;

public class ReviewReportServiceTest extends IntegrationTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ProductService productService;

    @Nested
    class ReportTest {

        @Test
        @WithMockUser(username = "user", password = "user", roles = {})
        public void givenNonEmptyReview_whenReportReview_reportIsCreated() {
            // Given
            Product product = productService
                .addProduct(Product.builder().name("testProduct").build());
            Review review = Review.builder()
                .productId(product.getId())
                .content("testContent")
                .rating(5)
                .build();

            // When
            Optional<ReviewDto> reviewDto = productService.addReview(review);
            assertThat(reviewDto).isPresent();

            Optional<User> userOptional = userService
                .getUserByUsername(authService.getCurrentUsername());
            assertThat(userOptional).isPresent();

            reviewService.reportReview(reviewDto.get().reviewId(),
                userOptional.get().getId(),
                "this review is problematic");

            // Then
            assertThat(reportService.getReports().size()).isEqualTo(1);
        }

        @Test
        @WithMockUser(username = "user", password = "user", roles = {})
        public void givenEmptyReview_whenReportReview_reportIsCreated() {
            // Given
            Product product = productService
                .addProduct(Product.builder().name("testProduct").build());
            Review review = Review.builder()
                .productId(product.getId())
                .rating(5)
                .content("")
                .build();

            // When
            Optional<ReviewDto> reviewDto = productService.addReview(review);
            assertThat(reviewDto).isPresent();

            Optional<User> userOptional = userService
                .getUserByUsername(authService.getCurrentUsername());
            assertThat(userOptional).isPresent();

            // Then
            assertThatExceptionOfType(ReportEmptyReviewAttemptException.class).isThrownBy(
                () -> reviewService.reportReview(
                    review.getId(), userOptional.get().getId(), "this review is problematic"
                ));
        }
    }

    @Nested
    class LikesDislikesTest {
        @Test
        @WithMockUser(username = "user", password = "user", roles = {})
        public void givenReview_whenLike_thenLikeCountIsIncreased() {
            // Given
            Product product = productService
                .addProduct(Product.builder().name("testProduct").build());
            Review review = Review.builder()
                .productId(product.getId())
                .rating(5)
                .content("")
                .build();
            Optional<ReviewDto> reviewDto = productService.addReview(review);
            assertThat(reviewDto).isPresent();
            int likesCount = reviewDto.get().likesCount();
            int dislikesCount = reviewDto.get().dislikesCount();

            // When
            long reviewId = reviewDto.get().reviewId();
            reviewService.likeReview(reviewId);

            // Then
            Optional<ReviewDto> reviewDtoAfter = reviewService.getReviewById(reviewId);
            assertThat(reviewDtoAfter).isPresent();
            assertThat(reviewDtoAfter.get().likesCount()).isEqualTo(likesCount + 1);
            assertThat(reviewDtoAfter.get().dislikesCount()).isEqualTo(dislikesCount);
        }

        @Test
        @WithMockUser(username = "user", password = "user", roles = {})
        public void givenReview_whenLikeUnlike_thenLikeCountIsConstant() {
            // Given
            Product product = productService
                .addProduct(Product.builder().name("testProduct").build());
            Review review = Review.builder()
                .productId(product.getId())
                .rating(5)
                .content("")
                .build();
            Optional<ReviewDto> reviewDto = productService.addReview(review);
            assertThat(reviewDto).isPresent();
            int likesCount = reviewDto.get().likesCount();
            int dislikesCount = reviewDto.get().dislikesCount();

            // When
            long reviewId = reviewDto.get().reviewId();
            reviewService.likeReview(reviewId);
            reviewService.unlikeReview(reviewId);

            // Then
            Optional<ReviewDto> reviewDtoAfter = reviewService.getReviewById(reviewId);
            assertThat(reviewDtoAfter).isPresent();
            assertThat(reviewDtoAfter.get().likesCount()).isEqualTo(likesCount);
            assertThat(reviewDtoAfter.get().dislikesCount()).isEqualTo(dislikesCount);
        }

        @Test
        @WithMockUser(username = "user", password = "user", roles = {})
        public void givenReview_whenDoubleLike_thenLikeCountIsIncreasedByOne() {
            // Given
            Product product = productService
                .addProduct(Product.builder().name("testProduct").build());
            Review review = Review.builder()
                .productId(product.getId())
                .rating(5)
                .content("")
                .build();
            Optional<ReviewDto> reviewDto = productService.addReview(review);
            assertThat(reviewDto).isPresent();
            int likesCount = reviewDto.get().likesCount();
            int dislikesCount = reviewDto.get().dislikesCount();

            // When
            long reviewId = reviewDto.get().reviewId();
            reviewService.likeReview(reviewId);
            reviewService.likeReview(reviewId);

            // Then
            Optional<ReviewDto> reviewDtoAfter = reviewService.getReviewById(reviewId);
            assertThat(reviewDtoAfter).isPresent();
            assertThat(reviewDtoAfter.get().likesCount()).isEqualTo(likesCount + 1);
        }

        @Test
        @WithMockUser(username = "user", password = "user", roles = {})
        public void givenReview_whenDislike_thenDislikeCountIsIncreased() {
            // Given
            Product product = productService
                .addProduct(Product.builder().name("testProduct").build());
            Review review = Review.builder()
                .productId(product.getId())
                .rating(5)
                .content("")
                .build();
            Optional<ReviewDto> reviewDto = productService.addReview(review);
            assertThat(reviewDto).isPresent();
            int likesCount = reviewDto.get().likesCount();
            int dislikesCount = reviewDto.get().dislikesCount();

            // When
            long reviewId = reviewDto.get().reviewId();
            reviewService.dislikeReview(reviewId);

            // Then
            Optional<ReviewDto> reviewDtoAfter = reviewService.getReviewById(reviewId);
            assertThat(reviewDtoAfter).isPresent();
            assertThat(reviewDtoAfter.get().likesCount()).isEqualTo(likesCount);
            assertThat(reviewDtoAfter.get().dislikesCount()).isEqualTo(dislikesCount + 1);
        }

        @Test
        @WithMockUser(username = "user", password = "user", roles = {})
        public void givenReview_whenDislikeUndislike_thenDislikeCountIsConstant() {
            // Given
            Product product = productService
                .addProduct(Product.builder().name("testProduct").build());
            Review review = Review.builder()
                .productId(product.getId())
                .rating(5)
                .content("")
                .build();
            Optional<ReviewDto> reviewDto = productService.addReview(review);
            assertThat(reviewDto).isPresent();
            int likesCount = reviewDto.get().likesCount();
            int dislikesCount = reviewDto.get().dislikesCount();

            // When
            long reviewId = reviewDto.get().reviewId();
            reviewService.dislikeReview(reviewId);
            reviewService.undislikeReview(reviewId);

            // Then
            Optional<ReviewDto> reviewDtoAfter = reviewService.getReviewById(reviewId);
            assertThat(reviewDtoAfter).isPresent();
            assertThat(reviewDtoAfter.get().likesCount()).isEqualTo(likesCount);
            assertThat(reviewDtoAfter.get().dislikesCount()).isEqualTo(dislikesCount);
        }

        @Test
        @WithMockUser(username = "user", password = "user", roles = {})
        public void givenReview_whenDoubleDislike_thenLikeCountIsIncreasedByOne() {
            // Given
            Product product = productService
                .addProduct(Product.builder().name("testProduct").build());
            Review review = Review.builder()
                .productId(product.getId())
                .rating(5)
                .content("")
                .build();
            Optional<ReviewDto> reviewDto = productService.addReview(review);
            assertThat(reviewDto).isPresent();
            int likesCount = reviewDto.get().likesCount();
            int dislikesCount = reviewDto.get().dislikesCount();

            // When
            long reviewId = reviewDto.get().reviewId();
            reviewService.dislikeReview(reviewId);
            reviewService.dislikeReview(reviewId);

            // Then
            Optional<ReviewDto> reviewDtoAfter = reviewService.getReviewById(reviewId);
            assertThat(reviewDtoAfter).isPresent();
            assertThat(reviewDtoAfter.get().dislikesCount()).isEqualTo(dislikesCount + 1);
        }

        @Test
        @WithMockUser(username = "user", password = "user", roles = {})
        public void givenReview_whenLikeDislike_thenIsDisliked() {
            // Given
            Product product = productService
                .addProduct(Product.builder().name("testProduct").build());
            Review review = Review.builder()
                .productId(product.getId())
                .rating(5)
                .content("")
                .build();
            Optional<ReviewDto> reviewDto = productService.addReview(review);
            assertThat(reviewDto).isPresent();
            int likesCount = reviewDto.get().likesCount();
            int dislikesCount = reviewDto.get().dislikesCount();

            // When
            long reviewId = reviewDto.get().reviewId();
            reviewService.likeReview(reviewId);
            reviewService.dislikeReview(reviewId);

            // Then
            Optional<ReviewDto> reviewDtoAfter = reviewService.getReviewById(reviewId);
            assertThat(reviewDtoAfter).isPresent();
            assertThat(reviewDtoAfter.get().likesCount()).isEqualTo(likesCount);
            assertThat(reviewDtoAfter.get().dislikesCount()).isEqualTo(dislikesCount + 1);
        }

        @Test
        @WithMockUser(username = "user", password = "user", roles = {})
        public void givenReview_whenDislikeLike_thenIsDisliked() {
            // Given
            Product product = productService
                .addProduct(Product.builder().name("testProduct").build());
            Review review = Review.builder()
                .productId(product.getId())
                .rating(5)
                .content("")
                .build();
            Optional<ReviewDto> reviewDto = productService.addReview(review);
            assertThat(reviewDto).isPresent();
            int likesCount = reviewDto.get().likesCount();
            int dislikesCount = reviewDto.get().dislikesCount();

            // When
            long reviewId = reviewDto.get().reviewId();
            reviewService.dislikeReview(reviewId);
            reviewService.likeReview(reviewId);

            // Then
            Optional<ReviewDto> reviewDtoAfter = reviewService.getReviewById(reviewId);
            assertThat(reviewDtoAfter).isPresent();
            assertThat(reviewDtoAfter.get().likesCount()).isEqualTo(likesCount + 1);
            assertThat(reviewDtoAfter.get().dislikesCount()).isEqualTo(dislikesCount);
        }
    }
}
