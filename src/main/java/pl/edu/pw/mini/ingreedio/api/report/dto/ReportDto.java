package pl.edu.pw.mini.ingreedio.api.report.dto;

import lombok.Builder;

@Builder
public record ReportDto(Long reportId,
                        Long userId,
                        Long reviewId,
                        String content) {

}
