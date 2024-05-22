package pl.edu.pw.mini.ingreedio.api.report.mapper;

import java.util.function.Function;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.report.dto.ReportDto;
import pl.edu.pw.mini.ingreedio.api.report.model.Report;

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
