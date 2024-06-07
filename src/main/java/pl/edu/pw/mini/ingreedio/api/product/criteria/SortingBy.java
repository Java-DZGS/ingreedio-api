package pl.edu.pw.mini.ingreedio.api.product.criteria;

import java.util.Optional;
import java.util.regex.Pattern;
import lombok.Getter;

@Getter
public enum SortingBy {
    RATING("rating"),
    RATE_COUNT("rateCount"),
    OPINIONS_COUNT("opinionsCount"),
    MATCH_SCORE("matchScore");

    private final String fieldName;

    public static Optional<SortingBy> fromCamelCode(String code) {
        for (SortingBy sortingDirection : values()) {
            if (sortingDirection.fieldName.equals(code)) {
                return Optional.of(sortingDirection);
            }
        }
        return Optional.empty();
    }

    public static Optional<SortingBy> fromKebabCode(String code) {
        String camelCode = Pattern.compile("-([a-z])")
            .matcher(code)
            .replaceAll(mr -> mr.group(1).toUpperCase());

        return fromCamelCode(camelCode);
    }

    SortingBy(String fieldName) {
        this.fieldName = fieldName;
    }
}
