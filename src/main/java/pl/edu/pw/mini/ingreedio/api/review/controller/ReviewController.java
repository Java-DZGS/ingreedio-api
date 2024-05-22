package pl.edu.pw.mini.ingreedio.api.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.mini.ingreedio.api.report.model.Report;
import pl.edu.pw.mini.ingreedio.api.review.dto.ReviewDto;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    @GetMapping("/{id}")
    public ResponseEntity<ReviewDto> getReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(ReviewDto.builder().build());
    }

    @PostMapping("/{id}/likes")
    public ResponseEntity<Void> likeReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/likes")
    public ResponseEntity<Void> dislikeReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reports")
    public ResponseEntity<Report> reportReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok().build();
    }
}
