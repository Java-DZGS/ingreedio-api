package pl.edu.pw.mini.ingreedio.api.review;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.mini.ingreedio.api.review.dto.ReportDto;
import pl.edu.pw.mini.ingreedio.api.review.service.ReportService;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @Operation(summary = "Get all reports",
        description = "Get all reports raised by users for all reviews of all products.",
        security = {@SecurityRequirement(name = "Bearer Authentication")})
    @PreAuthorize("hasAuthority('GET_REPORTS')")
    @GetMapping
    public ResponseEntity<List<ReportDto>> getReports() {
        return ResponseEntity.ok(reportService.getReports());
    }

    @Operation(summary = "Get report by id",
        description = "Get reports identified by a specific id.",
        security = {@SecurityRequirement(name = "Bearer Authentication")})
    @PreAuthorize("hasAuthority('GET_REPORTS')")
    @GetMapping("/{id}")
    public ResponseEntity<ReportDto> getReport(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getReport(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a report",
        description = "Delete a report identified by a specific id.",
        security = {@SecurityRequirement(name = "Bearer Authentication")})
    @PreAuthorize("hasAuthority('DELETE_REPORT')")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.ok().build();
    }
}
