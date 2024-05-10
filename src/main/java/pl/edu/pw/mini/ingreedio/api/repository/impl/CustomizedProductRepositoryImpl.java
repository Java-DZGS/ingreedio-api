package pl.edu.pw.mini.ingreedio.api.repository.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import pl.edu.pw.mini.ingreedio.api.criteria.ProductCriteria;
import pl.edu.pw.mini.ingreedio.api.model.Product;
import pl.edu.pw.mini.ingreedio.api.repository.CustomizedProductRepository;

@RequiredArgsConstructor
@Repository
public class CustomizedProductRepositoryImpl implements CustomizedProductRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Product> getProductsMatchingCriteria(ProductCriteria productCriteria,
                                                     Pageable pageable) {

        Pattern phraseKeywordsRegExp = null;
        if (productCriteria.getPhraseKeywords() != null) {
            phraseKeywordsRegExp = Pattern.compile("\\b"
                + String.join("|\\b", productCriteria.getPhraseKeywords()),
                Pattern.CASE_INSENSITIVE);
        }

        // Stage 1: Filter the products basing on
        // the ingredients, brand, provider, category, phrase, rating,

        // Note: If a product contains ingredient to exclude and ingredient
        // to include then the product is excluded (exclude has priority over include)
        Criteria ingredientsFilteringCriteria = new Criteria().andOperator(
            productCriteria.getIngredientsNamesToInclude() != null
                && !productCriteria.getIngredientsNamesToInclude().isEmpty()
                ? Criteria.where("ingredients").all(productCriteria.getIngredientsNamesToInclude())
                : new Criteria(),
            productCriteria.getIngredientsNamesToExclude() != null
                && !productCriteria.getIngredientsNamesToExclude().isEmpty()
                ? Criteria.where("ingredients").nin(productCriteria.getIngredientsNamesToExclude())
                : new Criteria()
        );

        Criteria brandsFilteringCriteria = new Criteria();
        if (productCriteria.getBrandsNamesToInclude() != null
                && !productCriteria.getBrandsNamesToInclude().isEmpty()) {
            brandsFilteringCriteria = Criteria.where("brand")
                .in(productCriteria.getBrandsNamesToInclude());
        } else if (productCriteria.getBrandsNamesToExclude() != null
                && !productCriteria.getBrandsNamesToExclude().isEmpty()) {
            brandsFilteringCriteria = Criteria.where("brand")
                .nin(productCriteria.getBrandsNamesToExclude());
        }

        Criteria providerFilteringCriteria = productCriteria.getProvidersNames() != null
            && !productCriteria.getProvidersNames().isEmpty()
            ? Criteria.where("provider").in(productCriteria.getProvidersNames())
            : new Criteria();

        Criteria categoriesFilteringCriteria = productCriteria.getCategoriesNames() != null
            && !productCriteria.getCategoriesNames().isEmpty()
            ? Criteria.where("categories").in(productCriteria.getCategoriesNames())
            : new Criteria();

        Criteria ratingFilteringCriteria = productCriteria.getMinRating() != null
            ? Criteria.where("rating").gte(productCriteria.getMinRating())
            : new Criteria();
        Criteria phraseFilteringCriteria = new Criteria();

        if (phraseKeywordsRegExp != null) {
            phraseFilteringCriteria = new Criteria().orOperator(
                Criteria.where("name").regex(phraseKeywordsRegExp),
                Criteria.where("brand").regex(phraseKeywordsRegExp),
                Criteria.where("shortDescription").regex(phraseKeywordsRegExp)
            );
        }

        // Final operations List
        List<AggregationOperation> finalQueryOperations = new ArrayList<>();

        AggregationOperation filteringOperation = Aggregation.match(new Criteria().andOperator(
            ingredientsFilteringCriteria,
            brandsFilteringCriteria,
            providerFilteringCriteria,
            categoriesFilteringCriteria,
            ratingFilteringCriteria,
            phraseFilteringCriteria
        ));

        finalQueryOperations.add(filteringOperation);

        // Stage 2: Prepare match score for each product (if there is match score sort operation)
        if (productCriteria.getHasMatchScoreSortCriteria() != null
            && productCriteria.getHasMatchScoreSortCriteria()
            && phraseKeywordsRegExp != null) {

            String addMatchScoreQuery =
                "{\n"
                + "  \"$addFields\": {\n"
                + "    \"matchScore\": {\n"
                + "      \"$add\": [\n"
                + "        { \"$multiply\": [{ \"$size\": { \"$regexFindAll\": "
                + "{ \"input\": \"$shortDescription\", \"regex\": /"
                    + phraseKeywordsRegExp.pattern()
                + "/, \"options\": 'i' } } }, 5] },\n"
                + "        { \"$multiply\": [{ \"$size\": { \"$regexFindAll\": "
                + "{ \"input\": \"$brand\", \"regex\": /" + phraseKeywordsRegExp.pattern()
                + "/, \"options\": 'i' } } }, 10] },\n"
                + "        { \"$multiply\": [{ \"$size\": { \"$regexFindAll\": "
                + "{ \"input\": \"$name\", \"regex\": /" + phraseKeywordsRegExp.pattern()
                + "/, \"options\": 'i' } } }, 15] }\n"
                + "      ]\n"
                + "    }\n"
                + "  }\n"
                + "}\n";

            finalQueryOperations.add(new CustomQueryAggregationOperation(addMatchScoreQuery));
        }

        // Stage 3: Sort the resultant products
        if (productCriteria.getSortingCriteria() != null) {
            finalQueryOperations.addAll(productCriteria.getSortingCriteria()
                .stream()
                .map(option -> Aggregation.sort(option.order(), option.byField()))
                .toList());
        }

        // Stage 4: Perform pagination on the final products list
        finalQueryOperations.add(Aggregation.skip(
            (long) pageable.getPageSize() * pageable.getPageNumber()));
        finalQueryOperations.add(Aggregation.limit(pageable.getPageSize()));

        try {
            // Query 1: Find total product count (only filtering is required)
            Aggregation totalProductsCountAggregation = Aggregation.newAggregation(
                filteringOperation,
                Aggregation.group().count().as("totalProductsCount")
            );

            Integer totalProductsCount =
                (Integer) mongoTemplate.aggregate(totalProductsCountAggregation,
                        "products", Map.class)
                    .getMappedResults().getFirst().get("totalProductsCount");

            // Query 2: Get products basing on the criteria
            Aggregation productsAggregation = Aggregation
                .newAggregation(finalQueryOperations.toArray(new AggregationOperation[0]));

            List<Product> products = mongoTemplate.aggregate(productsAggregation,
                    "products", Product.class)
                .getMappedResults();

            return new PageImpl<>(
                products,
                pageable,
                totalProductsCount == null ? 0 : totalProductsCount);
        } catch (NoSuchElementException noSuchElementException) {
            return new PageImpl<>(
                Collections.emptyList(),
                pageable,
                0
            );
        }

    }
}
