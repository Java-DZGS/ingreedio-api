package pl.edu.pw.mini.ingreedio.api.auth.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class RoleNotFoundException extends AbstractThrowableProblem {
    public RoleNotFoundException(String role) {
        super(null, "Role not found", Status.NOT_FOUND, String.format("Role not found: %s", role));
    }
}
