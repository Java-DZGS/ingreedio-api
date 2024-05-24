package pl.edu.pw.mini.ingreedio.api.review;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.mini.ingreedio.api.auth.exception.NotLoggedInException;
import pl.edu.pw.mini.ingreedio.api.auth.service.AuthService;
import pl.edu.pw.mini.ingreedio.api.review.dto.ReportDto;
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

    /* todo: issue #100
    @PostMapping("/{id}/likes")
    public ResponseEntity<Void> likeReview(@PathVariable Long id) {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/likes")
    public ResponseEntity<Void> dislikeReview(@PathVariable Long id) {
        return ResponseEntity.ok().build();
    }
    */

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
}
