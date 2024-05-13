package pl.edu.pw.mini.ingreedio.api.product.exception;

public class InvalidSortingOptionException extends Exception {
    public InvalidSortingOptionException(String option) {
        super("Sorting option [" + option + "] has not been recognized");
    }

}