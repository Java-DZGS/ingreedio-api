package pl.edu.pw.mini.ingreedio.api.product.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class ProductNotFoundException extends AbstractThrowableProblem {
    public ProductNotFoundException(long id) {
        super(null, "Product not found", Status.NOT_FOUND,
            String.format("Product not found %d", id));
    }
}
