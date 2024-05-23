package pl.edu.pw.mini.ingreedio.api.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pw.mini.ingreedio.api.review.model.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

}
