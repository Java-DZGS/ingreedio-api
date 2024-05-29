package pl.edu.pw.mini.ingreedio.api.review.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class ReportEmptyReviewAttemptException extends AbstractThrowableProblem {
    public ReportEmptyReviewAttemptException() {
        super(null, "Attempt to report an empty review", Status.BAD_REQUEST,
            "You can report only reviews with content");
    }
}