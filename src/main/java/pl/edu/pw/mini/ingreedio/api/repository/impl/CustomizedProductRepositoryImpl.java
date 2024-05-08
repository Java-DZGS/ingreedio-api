package pl.edu.pw.mini.ingreedio.api.repository.impl;

import java.util.ArrayList;
import java.util.Arrays;
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
        // Stage 1: filter the products basing on the phrase, rating and ingredients
        Criteria ingredientsFilteringCriteria = new Criteria().andOperator(
            productsCriteria.getIngredientsToIncludeNames() != null
                ? Criteria.where("ingredients").all(productsCriteria.getIngredientsToIncludeNames())
                : new Criteria(),
            productsCriteria.getIngredientsToExcludeNames() != null
                ? Criteria.where("ingredients").all(productsCriteria.getIngredientsToExcludeNames())
                : new Criteria()
        );

        Criteria ratingFilteringCriteria = productsCriteria.getMinRating() == null
            ? Criteria.where("rating").gte(productsCriteria.getMinRating())
            : new Criteria();

        Criteria phraseFilteringCriteria = new Criteria();
        if (productsCriteria.getPhraseKeywords() != null) {
            var test = String.join("\\b|\\b", productsCriteria.getPhraseKeywords());
            phraseFilteringCriteria = new Criteria().orOperator(
                Criteria.where("name")
                    .regex("\\b" + String.join("\\b|\\b",
                        productsCriteria.getPhraseKeywords()) + "\\b", "i"),
                Criteria.where("brand")
                    .regex("\\b" + String.join("\\b|\\b",
                        productsCriteria.getPhraseKeywords()) + "\\b", "i"),
                Criteria.where("shortDescription")
                    .regex("\\b" + String.join("\\b|\\b",
                        productsCriteria.getPhraseKeywords()) + "\\b", "i")
            );
        }

        AggregationOperation filteringOperation = Aggregation.match(new Criteria().andOperator(
            ingredientsFilteringCriteria,
            ratingFilteringCriteria,
            phraseFilteringCriteria
        ));

        // Stage 2: prepare match score for each product (if there is match score sort operation)
        // TODO

        // Stage 3: sort the resultant products, apply pagination and create the final aggregation
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(filteringOperation);

        // Apply sorting to the aggregation
        if (productsCriteria.getSortingCriteria() != null) {
            operations.addAll(productsCriteria.getSortingCriteria()
                .stream()
                .map(option -> Aggregation.sort(option.order(), option.byField()))
                .toList());
        }


        // Perform pagination
        operations.add(Aggregation.skip(
            (long) pageable.getPageSize() * pageable.getPageNumber()));
        operations.add(Aggregation.limit(pageable.getPageSize()));

        Aggregation productsAggregation = Aggregation
            .newAggregation(operations.toArray(new AggregationOperation[0]));

        Aggregation totalProductsCountAggregation = Aggregation.newAggregation(
            filteringOperation,
            Aggregation.group().count().as("totalProductsCount")
        );

        List<Product> products = mongoTemplate.aggregate(productsAggregation,
                "products", Product.class)
            .getMappedResults();

        Integer totalProductsCount =
            (Integer) mongoTemplate.aggregate(totalProductsCountAggregation,
                    "products", Map.class)
                .getMappedResults().getFirst().get("totalProductsCount");

        return new PageImpl<>(
            products,
            pageable,
            totalProductsCount == null ? 0 : totalProductsCount);
    }
}
