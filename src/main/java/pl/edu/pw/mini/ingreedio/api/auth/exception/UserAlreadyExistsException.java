package pl.edu.pw.mini.ingreedio.api.auth.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class UserAlreadyExistsException extends AbstractThrowableProblem {
    public UserAlreadyExistsException() {
        super(null, "User already exists", Status.CONFLICT);
    }
}
