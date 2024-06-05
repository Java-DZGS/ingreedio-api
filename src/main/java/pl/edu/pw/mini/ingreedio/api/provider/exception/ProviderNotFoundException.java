package pl.edu.pw.mini.ingreedio.api.provider.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class ProviderNotFoundException extends AbstractThrowableProblem {
    public ProviderNotFoundException(long id) {
        super(null, "Provider not found", Status.NOT_FOUND, "Provider not found: %d.".formatted(id));
    }
}
