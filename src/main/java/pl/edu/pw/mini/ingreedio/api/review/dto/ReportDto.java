package pl.edu.pw.mini.ingreedio.api.review.dto;

import lombok.Builder;

@Builder
public record ReportDto(Long reportId,
                        long userId,
                        Long reviewId,
                        String content) {

}
