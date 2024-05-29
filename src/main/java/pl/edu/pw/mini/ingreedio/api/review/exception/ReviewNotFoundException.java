package pl.edu.pw.mini.ingreedio.api.review.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class ReviewNotFoundException extends AbstractThrowableProblem {
    public ReviewNotFoundException(long id) {
        super(null, "Review not found", Status.NOT_FOUND,
            String.format("Review not found %d", id));
    }
}
