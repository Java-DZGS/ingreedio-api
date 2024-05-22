package pl.edu.pw.mini.ingreedio.api.review.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.mini.ingreedio.api.report.dto.ReportDto;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    @GetMapping
    public ResponseEntity<List<ReportDto>> getReports() {
        return ResponseEntity.ok(List.of(ReportDto.builder().build()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportDto> getReport(@PathVariable Long reportId) {
        return ResponseEntity.ok(ReportDto.builder().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ReportDto> deleteReport(@PathVariable Long reportId) {
        return ResponseEntity.ok(ReportDto.builder().build());
    }
}
