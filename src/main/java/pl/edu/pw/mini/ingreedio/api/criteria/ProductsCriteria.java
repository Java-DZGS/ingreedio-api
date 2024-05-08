package pl.edu.pw.mini.ingreedio.api.criteria;

import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import pl.edu.pw.mini.ingreedio.api.dto.IngredientDto;

@Getter
@Builder
public class ProductsCriteria {
    private Set<String> ingredientsToIncludeNames;
    private Set<String> ingredientsToExcludeNames;
    private Integer minRating;
    private String phrase;
    private List<ProductsSortingCriteria> sortingCriteria;
    private Boolean liked;
}
