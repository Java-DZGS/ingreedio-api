package pl.edu.pw.mini.ingreedio.api.review;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import pl.edu.pw.mini.ingreedio.api.auth.exception.NotLoggedInException;
import pl.edu.pw.mini.ingreedio.api.auth.service.AuthService;
import pl.edu.pw.mini.ingreedio.api.product.service.ProductService;
import pl.edu.pw.mini.ingreedio.api.review.dto.ReportDto;
import pl.edu.pw.mini.ingreedio.api.review.dto.ReviewDto;
import pl.edu.pw.mini.ingreedio.api.review.dto.ReviewRequestDto;
import pl.edu.pw.mini.ingreedio.api.review.model.Review;
import pl.edu.pw.mini.ingreedio.api.review.service.ReviewService;
import pl.edu.pw.mini.ingreedio.api.user.model.User;
import pl.edu.pw.mini.ingreedio.api.user.service.UserService;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;
    private final AuthService authService;
    private final ProductService productService;

    @Operation(summary = "Like a review",
        description = "Likes a user written (non-empty) review based on the provided review ID.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PreAuthorize("hasAuthority('LIKE_REVIEW')")
    @PostMapping("/{id}/likes")
    public ResponseEntity<Void> likeReview(@PathVariable Long id) {
        reviewService.likeReview(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Unlikes a review",
        description = "Unlikes a user written (non-empty) review based on the provided review ID.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PreAuthorize("hasAuthority('LIKE_REVIEW')")
    @DeleteMapping("/{id}/likes")
    public ResponseEntity<Void> unlikeReview(@PathVariable Long id) {
        reviewService.unlikeReview(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Dislike a review",
        description = "Dislikes a user written (non-empty) review based on the provided review ID.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PreAuthorize("hasAuthority('LIKE_REVIEW')")
    @PostMapping("/{id}/dislikes")
    public ResponseEntity<Void> dislikeReview(@PathVariable Long id) {
        reviewService.dislikeReview(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Undislikes a review",
        description = "Undislikes a user written (non-empty) review "
                      + "based on the provided review ID.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PreAuthorize("hasAuthority('LIKE_REVIEW')")
    @DeleteMapping("/{id}/dislikes")
    public ResponseEntity<Void> undislikeReview(@PathVariable Long id) {
        reviewService.undislikeReview(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Report a review",
        description = "Reports a user written (non-empty) review based on the provided review ID.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review reported successfully",
            content = @Content(schema = @Schema(implementation = ReportDto.class))),
        @ApiResponse(responseCode = "404", description = "Review not found", content = @Content)
    })
    @PreAuthorize("hasAuthority('REPORT_REVIEW')")
    @PostMapping("/{id}/reports")
    public ReportDto reportReview(@PathVariable Long id,
                                  @RequestBody String content) {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());

        if (userOptional.isEmpty()) {
            throw new NotLoggedInException();
        }

        return reviewService.reportReview(id, userOptional.get().getId(), content);
    }

    @Operation(summary = "Edit a review",
        description = "Edits a review based on the provided review ID."
                      + "and review details.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review edited successfully",
            content = @Content(schema = @Schema(implementation = ReviewDto.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReviewDto> editReview(@PathVariable Long id,
                                                @Valid @RequestBody
                                                ReviewRequestDto reviewRequest) {
        //TODO: awful hack
        var oldReview = reviewService.getReviewById(id).get();

        Review review = Review.builder()
            .productId(oldReview.productId())
            .rating(reviewRequest.rating())
            .content(reviewRequest.content())
            .build();
        Optional<ReviewDto> reviewOptional = productService.editReview(review);
        //TODO: proper exceptions, requires refactor
        return reviewOptional.map(reviewDto -> new ResponseEntity<>(reviewDto, HttpStatus.OK))
            .orElseThrow(() -> Problem.valueOf(Status.BAD_REQUEST));
    }

    @Operation(summary = "Delete a review",
        description = "Deletes a review based on the provided review ID.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review deleted successfully",
            content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        //TODO: awful hack
        var oldReview = reviewService.getReviewById(id).get();

        boolean deleted = productService.deleteReview(oldReview.productId());
        if (!deleted) {
            throw Problem.valueOf(Status.BAD_REQUEST); //TODO: proper exceptions, requires refactor
        }
        return ResponseEntity.ok().build();
    }
}
