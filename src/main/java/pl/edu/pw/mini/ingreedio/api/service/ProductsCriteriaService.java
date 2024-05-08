package pl.edu.pw.mini.ingreedio.api.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.criteria.ProductsCriteria;
import pl.edu.pw.mini.ingreedio.api.criteria.ProductsSortingCriteria;
import pl.edu.pw.mini.ingreedio.api.dto.IngredientDto;

@Service
public class ProductsCriteriaService {
    IngredientService ingredientService;

    public ProductsCriteria getProductsCriteria(Optional<Set<Long>> ingredientsToExclude,
                                                Optional<Set<Long>> ingredientsToInclude,
                                                Optional<Integer> minRating,
                                                Optional<String> phrase,
                                                Optional<List<String>> sortBy,
                                                Optional<Boolean> liked) {
        var builder = ProductsCriteria.builder();

        ingredientsToExclude.ifPresent(
            ingredients -> {
                builder.ingredientsToExcludeNames(ingredientService
                    .getIngredientsByIds(ingredients)
                    .stream()
                    .map(IngredientDto::name)
                    .collect(Collectors.toSet()));
            });

        ingredientsToInclude.ifPresent(
            ingredients -> {
                builder.ingredientsToIncludeNames(ingredientService
                    .getIngredientsByIds(ingredients)
                    .stream()
                    .map(IngredientDto::name)
                    .collect(Collectors.toSet()));
            });

        minRating.ifPresent(builder::minRating);
        phrase.ifPresent(builder::phrase);
        liked.ifPresent(builder::liked);

        sortBy.ifPresent(sortingSignatures -> {
            builder.sortingCriteria(sortingSignatures.stream()
                .map(this::getProductsSortingCriteria)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList()));
        });

        return builder.build();
    }

    public Optional<ProductsSortingCriteria> getProductsSortingCriteria(String sortingSignature) {
        if (sortingSignature.length() < 3) {
            return Optional.empty();
        }

        char orderChar = sortingSignature.charAt(0);
        Sort.Direction order;
        switch (orderChar) {
            case 'a':
                order = Sort.Direction.ASC;
                break;
            case 'd':
                order = Sort.Direction.DESC;
                break;
            default:
                return Optional.empty();
        }

        String sortBy = sortingSignature.substring(2);
        String byField;
        switch (sortBy) {
            case "rating":
                byField = "rating";
                break;
            case "rate-count":
                byField = "rateCount";
                break;
            case "opinions-count":
                byField = "opinionsCount";
                break;
            case "match-score":
                byField = "matchScore";
                break;
            default:
                return Optional.empty();
        }

        return Optional.of(new ProductsSortingCriteria(order, byField));
    }
}
