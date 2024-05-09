package pl.edu.pw.mini.ingreedio.api.criteria;

import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductCriteria {
    private Set<String> ingredientsNamesToInclude;
    private Set<String> ingredientsNamesToExclude;
    private Set<String> brandsNamesToInclude;
    private Set<String> brandsNamesToExclude;
    private Set<String> providersNames;
    private Set<String> categoriesNames;
    private Integer minRating;
    private Set<String> phraseKeywords;
    private List<ProductsSortingCriteria> sortingCriteria;
    private Boolean hasMatchScoreSortCriteria;
    private Boolean liked;
}
