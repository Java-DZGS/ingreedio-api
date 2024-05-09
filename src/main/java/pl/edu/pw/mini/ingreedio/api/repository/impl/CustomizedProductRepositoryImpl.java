package pl.edu.pw.mini.ingreedio.api.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import pl.edu.pw.mini.ingreedio.api.criteria.ProductsCriteria;
import pl.edu.pw.mini.ingreedio.api.model.Product;
import pl.edu.pw.mini.ingreedio.api.repository.CustomizedProductRepository;

@RequiredArgsConstructor
@Repository
public class CustomizedProductRepositoryImpl implements CustomizedProductRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Product> getProductsMatchingCriteria(ProductsCriteria productsCriteria,
                                                     Pageable pageable) {
        // Operations List
        List<AggregationOperation> finalQueryOperations = new ArrayList<>();

        String phraseKeywordsRegExp = "";
        if (productsCriteria.getPhraseKeywords() != null) {
            phraseKeywordsRegExp = "\\b"
                + String.join("|\\b", productsCriteria.getPhraseKeywords());
        }

        // Stage 1: Filter the products basing on
        // the ingredients, brand, provider, category, phrase, rating,
        Criteria ingredientsFilteringCriteria = new Criteria().andOperator(
            productsCriteria.getIngredientsNamesToInclude() != null
                && !productsCriteria.getIngredientsNamesToInclude().isEmpty()
                ? Criteria.where("ingredients").all(productsCriteria.getIngredientsNamesToInclude())
                : new Criteria(),
            productsCriteria.getIngredientsNamesToExclude() != null
                && !productsCriteria.getIngredientsNamesToExclude().isEmpty()
                ? Criteria.where("ingredients").nin(productsCriteria.getIngredientsNamesToExclude())
                : new Criteria()
        );

        Criteria brandsFilteringCriteria = new Criteria().andOperator(
            productsCriteria.getBrandsNamesToInclude() != null
                && !productsCriteria.getBrandsNamesToInclude().isEmpty()
                ? Criteria.where("brand").in(productsCriteria.getBrandsNamesToInclude())
                : new Criteria(),
            productsCriteria.getBrandsNamesToExclude() != null
                && !productsCriteria.getBrandsNamesToExclude().isEmpty()
                ? Criteria.where("brand").nin(productsCriteria.getBrandsNamesToExclude())
                : new Criteria()
        );

        Criteria providerFilteringCriteria = productsCriteria.getProvidersNames() != null
            && !productsCriteria.getProvidersNames().isEmpty()
            ? Criteria.where("provider").in(productsCriteria.getProvidersNames())
            : new Criteria();

        Criteria categoriesFilteringCriteria = productsCriteria.getCategoriesNames() != null
            && !productsCriteria.getCategoriesNames().isEmpty()
            ? Criteria.where("categories").in(productsCriteria.getCategoriesNames())
            : new Criteria();

        Criteria ratingFilteringCriteria = productsCriteria.getMinRating() != null
            ? Criteria.where("rating").gte(productsCriteria.getMinRating())
            : new Criteria();

        Criteria phraseFilteringCriteria = new Criteria();
        if (productsCriteria.getPhraseKeywords() != null) {
            phraseFilteringCriteria = new Criteria().orOperator(
                Criteria.where("name").regex(phraseKeywordsRegExp),
                Criteria.where("brand").regex(phraseKeywordsRegExp),
                Criteria.where("shortDescription").regex(phraseKeywordsRegExp)
            );
        }

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
        if (productsCriteria.getHasMatchScoreSortCriteria() != null
            && productsCriteria.getHasMatchScoreSortCriteria()
            && productsCriteria.getPhraseKeywords() != null) {

            String addMatchScoreQuery =
                "{\n"
                + "  \"$addFields\": {\n"
                + "    \"matchScore\": {\n"
                + "      \"$add\": [\n"
                + "        { \"$multiply\": [{ \"$size\": { \"$regexFindAll\": "
                + "{ \"input\": \"$shortDescription\", \"regex\": /" + phraseKeywordsRegExp
                + "/ } } }, 5] },\n"
                + "        { \"$multiply\": [{ \"$size\": { \"$regexFindAll\": "
                + "{ \"input\": \"$brand\", \"regex\": /" + phraseKeywordsRegExp
                + "/ } } }, 10] },\n"
                + "        { \"$multiply\": [{ \"$size\": { \"$regexFindAll\": "
                + "{ \"input\": \"$name\", \"regex\": /" + phraseKeywordsRegExp
                + "/ } } }, 15] }\n"
                + "      ]\n"
                + "    }\n"
                + "  }\n"
                + "}\n";

            finalQueryOperations.add(new CustomQueryAggregationOperation(addMatchScoreQuery));
        }

        // Stage 3: Sort the resultant products
        if (productsCriteria.getSortingCriteria() != null) {
            finalQueryOperations.addAll(productsCriteria.getSortingCriteria()
                .stream()
                .map(option -> Aggregation.sort(option.order(), option.byField()))
                .toList());
        }

        // Stage 4: Perform pagination on the final products list
        finalQueryOperations.add(Aggregation.skip(
            (long) pageable.getPageSize() * pageable.getPageNumber()));
        finalQueryOperations.add(Aggregation.limit(pageable.getPageSize()));

        // First query: Find total product count (only filtering is required)
        Aggregation totalProductsCountAggregation = Aggregation.newAggregation(
            filteringOperation,
            Aggregation.group().count().as("totalProductsCount")
        );

        Integer totalProductsCount =
            (Integer) mongoTemplate.aggregate(totalProductsCountAggregation,
                    "products", Map.class)
                .getMappedResults().getFirst().get("totalProductsCount");

        // Second query: Get products basing on the criteria
        Aggregation productsAggregation = Aggregation
            .newAggregation(finalQueryOperations.toArray(new AggregationOperation[0]));

        List<Product> products = mongoTemplate.aggregate(productsAggregation,
                "products", Product.class)
            .getMappedResults();

        return new PageImpl<>(
            products,
            pageable,
            totalProductsCount == null ? 0 : totalProductsCount);
    }
}
