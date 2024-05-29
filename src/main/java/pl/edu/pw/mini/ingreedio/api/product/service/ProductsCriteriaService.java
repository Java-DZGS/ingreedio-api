package pl.edu.pw.mini.ingreedio.api.product.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.ingredient.dto.IngredientDto;
import pl.edu.pw.mini.ingreedio.api.ingredient.service.IngredientService;
import pl.edu.pw.mini.ingreedio.api.product.criteria.ProductCriteria;
import pl.edu.pw.mini.ingreedio.api.product.criteria.ProductsSortingCriteria;

@Service
@AllArgsConstructor
public class ProductsCriteriaService {
    private final IngredientService ingredientService;

    public ProductCriteria getProductsCriteria(Optional<Set<Long>> ingredientsToExclude,
                                               Optional<Set<Long>> ingredientsToInclude,
                                               Optional<Integer> minRating,
                                               Optional<String> phrase,
                                               Optional<List<String>> sortBy,
                                               Optional<Boolean> liked) {

        // TODO: provider, brand, category (add arguments too)
        var builder = ProductCriteria.builder();
        builder.hasMatchScoreSortCriteria(false);

        ingredientsToExclude.ifPresent(
            ingredients -> {
                builder.ingredientsNamesToExclude(ingredientService
                    .getIngredientsByIds(ingredients)
                    .stream()
                    .map(IngredientDto::name)
                    .collect(Collectors.toSet()));
            });

        ingredientsToInclude.ifPresent(
            ingredients -> {
                builder.ingredientsNamesToInclude(ingredientService
                    .getIngredientsByIds(ingredients)
                    .stream()
                    .map(IngredientDto::name)
                    .collect(Collectors.toSet()));
            });

        minRating.ifPresent(builder::minRating);

        phrase.ifPresent(phraseString ->
            builder.phraseKeywords(Arrays.stream(
                phraseString.replaceAll("%20+", " ")
                    .trim()
                    .split(" "))
                .collect(Collectors.toSet())));

        liked.ifPresent(builder::liked);

        sortBy.ifPresent(sortingSignatures -> {
            List<ProductsSortingCriteria> sortingCriteriaList =
                sortingSignatures.stream()
                .map(this::getProductsSortingCriteria)
                .flatMap(Optional::stream)
                .toList();

            Optional<ProductsSortingCriteria> foundCriteria = sortingCriteriaList.stream()
                .filter(criteria -> criteria.byField().equals("matchScore"))
                .findAny();

            builder.sortingCriteria(sortingCriteriaList);
            builder.hasMatchScoreSortCriteria(foundCriteria.isPresent());
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
