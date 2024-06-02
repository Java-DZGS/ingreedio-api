package pl.edu.pw.mini.ingreedio.api.user.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class UserNotFoundException extends AbstractThrowableProblem {
    public UserNotFoundException(String username) {
        super(null, "User not found", Status.NOT_FOUND,
            String.format("User not found: %s", username));
    }

    public UserNotFoundException(int id) {
        super(null, "User not found", Status.NOT_FOUND,
            String.format("User not found: %d", id));
    }
}
