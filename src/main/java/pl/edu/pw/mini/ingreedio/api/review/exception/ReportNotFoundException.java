package pl.edu.pw.mini.ingreedio.api.review.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class ReportNotFoundException extends AbstractThrowableProblem {
    public ReportNotFoundException(long id) {
        super(null, "Report not found", Status.NOT_FOUND,
            String.format("Report not found %d", id));
    }
}
