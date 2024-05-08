package pl.edu.pw.mini.ingreedio.api.criteria;

import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductsCriteria {
    private Set<String> ingredientsToIncludeNames;
    private Set<String> ingredientsToExcludeNames;
    private Integer minRating;
    private Set<String> phraseKeywords;
    private List<ProductsSortingCriteria> sortingCriteria;
    private Boolean hasMatchScoreSortCriteria;
    private Boolean liked;
}
