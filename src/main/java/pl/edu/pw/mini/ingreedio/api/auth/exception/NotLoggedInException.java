package pl.edu.pw.mini.ingreedio.api.auth.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class NotLoggedInException extends AbstractThrowableProblem {
    public NotLoggedInException() {
        super(null, "Not logged in", Status.NOT_FOUND, "Not logged in.");
    }
}
