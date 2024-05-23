package pl.edu.pw.mini.ingreedio.api.review.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.review.dto.ReportDto;
import pl.edu.pw.mini.ingreedio.api.review.exception.ReportNotFoundException;
import pl.edu.pw.mini.ingreedio.api.review.mapper.ReportDtoMapper;
import pl.edu.pw.mini.ingreedio.api.review.model.Report;
import pl.edu.pw.mini.ingreedio.api.review.repository.ReportRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {
    private final ReportRepository reportRepository;

    private final ReportDtoMapper reportDtoMapper;

    public List<ReportDto> getReports() {
        return reportRepository.findAll()
            .stream()
            .map(reportDtoMapper)
            .collect(Collectors.toList());
    }

    public ReportDto getReport(Long id) throws ReportNotFoundException {
        Optional<Report> reportOptional = reportRepository.findById(id);

        if (reportOptional.isEmpty()) {
            throw new ReportNotFoundException(id);
        }

        return reportDtoMapper.apply(reportOptional.get());
    }

    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }
}
