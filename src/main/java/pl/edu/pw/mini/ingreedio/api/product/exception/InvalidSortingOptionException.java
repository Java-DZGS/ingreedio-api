package pl.edu.pw.mini.ingreedio.api.product.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class InvalidSortingOptionException extends AbstractThrowableProblem {
    public InvalidSortingOptionException(String option) {
        super(null, "Sorting option is invalid",
            Status.BAD_REQUEST,
            "Sorting option [" + option + "] has not been recognized");
    }

}