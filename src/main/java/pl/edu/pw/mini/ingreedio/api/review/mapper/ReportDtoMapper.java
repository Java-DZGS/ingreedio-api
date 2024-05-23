package pl.edu.pw.mini.ingreedio.api.review.mapper;

import java.util.function.Function;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.review.dto.ReportDto;
import pl.edu.pw.mini.ingreedio.api.review.model.Report;

@Service
public class ReportDtoMapper implements Function<Report, ReportDto> {
    @Override
    public ReportDto apply(Report report) {
        return ReportDto.builder()
            .reportId(report.getId())
            .userId(report.getUser().getId())
            .reviewId(report.getReview().getId())
            .content(report.getContent())
            .build();
    }
}
