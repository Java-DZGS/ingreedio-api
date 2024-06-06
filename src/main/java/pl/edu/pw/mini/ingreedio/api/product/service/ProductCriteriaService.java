package pl.edu.pw.mini.ingreedio.api.product.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.brand.model.Brand;
import pl.edu.pw.mini.ingreedio.api.brand.service.BrandService;
import pl.edu.pw.mini.ingreedio.api.category.model.Category;
import pl.edu.pw.mini.ingreedio.api.category.service.CategoryService;
import pl.edu.pw.mini.ingreedio.api.ingredient.model.Ingredient;
import pl.edu.pw.mini.ingreedio.api.ingredient.service.IngredientService;
import pl.edu.pw.mini.ingreedio.api.product.criteria.ProductCriteria;
import pl.edu.pw.mini.ingreedio.api.product.criteria.ProductSortingCriteria;
import pl.edu.pw.mini.ingreedio.api.product.criteria.SortingBy;
import pl.edu.pw.mini.ingreedio.api.product.exception.InvalidSortingOptionException;
import pl.edu.pw.mini.ingreedio.api.provider.model.Provider;
import pl.edu.pw.mini.ingreedio.api.provider.service.ProviderService;

@Service
@AllArgsConstructor
public class ProductCriteriaService {
    private final IngredientService ingredientService;
    private final ProviderService providerService;
    private final BrandService brandService;
    private final CategoryService categoryService;

    public ProductCriteria getProductsCriteria(Optional<Set<Long>> ingredientsToExclude,
                                               Optional<Set<Long>> ingredientsToInclude,
                                               Optional<Integer> minRating,
                                               Optional<String> phrase,
                                               Optional<List<String>> sortBy,
                                               Optional<Boolean> liked,
                                               Optional<Set<Long>> providers,
                                               Optional<Set<Long>> brandsToExclude,
                                               Optional<Set<Long>> brandsToInclude,
                                               Optional<Set<Long>> categories)
        throws InvalidSortingOptionException {

        var builder = ProductCriteria.builder();
        builder.hasMatchScoreSortCriteria(false);

        ingredientsToExclude.ifPresent(
            ingredients -> builder.ingredientsNamesToExclude(ingredientService
                .getIngredientsByIds(ingredients)
                .stream()
                .map(Ingredient::getName)
                .collect(Collectors.toSet())));

        ingredientsToInclude.ifPresent(
            ingredients -> builder.ingredientsNamesToInclude(ingredientService
                .getIngredientsByIds(ingredients)
                .stream()
                .map(Ingredient::getName)
                .collect(Collectors.toSet())));

        minRating.ifPresent(builder::minRating);

        phrase.ifPresent(phraseString ->
            builder.phraseKeywords(Arrays.stream(
                    phraseString.replaceAll("%20+", " ")
                        .trim()
                        .split(" "))
                .collect(Collectors.toSet())));

        liked.ifPresent(builder::liked);

        providers.ifPresent(
            providerIds -> builder.providersNames(providerService
                .getProvidersByIds(providerIds)
                .stream()
                .map(Provider::getName)
                .collect(Collectors.toSet())));

        brandsToExclude.ifPresent(
            brandIds -> builder.brandsNamesToExclude(brandService
                .getBrandsByIds(brandIds)
                .stream()
                .map(Brand::getName)
                .collect(Collectors.toSet())));

        brandsToInclude.ifPresent(
            brandIds -> builder.brandsNamesToInclude(brandService
                .getBrandsByIds(brandIds)
                .stream()
                .map(Brand::getName)
                .collect(Collectors.toSet())));

        categories.ifPresent(
            categoryIds -> builder.categoriesNames(categoryService
                .getCategoriesByIds(categoryIds)
                .stream()
                .map(Category::getName)
                .collect(Collectors.toSet()))
        );

        sortBy.ifPresent(sortingSignatures -> {
            List<ProductSortingCriteria> sortingCriteriaList =
                sortingSignatures.stream()
                    .map(this::getProductsSortingCriteria)
                    .toList();

            Optional<ProductSortingCriteria> foundCriteria = sortingCriteriaList.stream()
                .filter(criteria -> criteria.byField().getFieldName().equals("matchScore"))
                .findAny();

            builder.sortingCriteria(sortingCriteriaList);
            builder.hasMatchScoreSortCriteria(foundCriteria.isPresent());
        });

        return builder.build();
    }

    public ProductSortingCriteria getProductsSortingCriteria(String sortingOption) throws
        InvalidSortingOptionException {
        if (sortingOption.length() < 3) {
            throw new InvalidSortingOptionException(sortingOption);
        }

        char orderChar = sortingOption.charAt(0);
        if (orderChar != 'a' && orderChar != 'd') {
            throw new InvalidSortingOptionException(sortingOption);
        }

        Sort.Direction order = orderChar == 'a' ? Sort.Direction.ASC : Sort.Direction.DESC;

        String sortingByField = sortingOption.substring(2);
        SortingBy sortingBy = SortingBy
            .fromKebabCode(sortingByField)
            .orElseThrow(() -> new InvalidSortingOptionException(sortingOption));

        return new ProductSortingCriteria(order, sortingBy);
    }
}
