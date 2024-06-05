package pl.edu.pw.mini.ingreedio.api.category.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class CategoryNotFoundException extends AbstractThrowableProblem {
    public CategoryNotFoundException(long id) {
        super(null, "Category not found", Status.NOT_FOUND,
            "Category not found: %d.".formatted(id));
    }
}
