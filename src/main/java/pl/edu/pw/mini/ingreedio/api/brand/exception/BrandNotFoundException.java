package pl.edu.pw.mini.ingreedio.api.brand.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class BrandNotFoundException extends AbstractThrowableProblem {
    public BrandNotFoundException(long id) {
        super(null, "Brand not found", Status.NOT_FOUND, "Brand not found: %d.".formatted(id));
    }
}
