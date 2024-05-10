package pl.edu.pw.mini.ingreedio.api.criteria;

import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Builder
public record ProductCriteria(
        Set<String> ingredientsNamesToInclude,
        Set<String> ingredientsNamesToExclude,
        Set<String> brandsNamesToInclude,
        Set<String> brandsNamesToExclude,
        Set<String> providersNames,
        Set<String> categoriesNames,
        Integer minRating,
        Set<String> phraseKeywords,
        List<ProductsSortingCriteria> sortingCriteria,
        Boolean hasMatchScoreSortCriteria,
        Boolean liked
) {}
