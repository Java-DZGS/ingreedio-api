package pl.edu.pw.mini.ingreedio.api.product.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class IngredientNotFoundException extends AbstractThrowableProblem {
    public IngredientNotFoundException(long id) {
        super(null, "Ingredient not found", Status.NOT_FOUND,
            String.format("Ingredient not found: %d", id));
    }
}
