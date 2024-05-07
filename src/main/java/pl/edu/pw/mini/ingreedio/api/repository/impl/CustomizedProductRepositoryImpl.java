package pl.edu.pw.mini.ingreedio.api.repository.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.edu.pw.mini.ingreedio.api.criteria.ProductFilterCriteria;
import pl.edu.pw.mini.ingreedio.api.model.Product;
import pl.edu.pw.mini.ingreedio.api.repository.CustomizedProductRepository;

@RequiredArgsConstructor
@Repository
public class CustomizedProductRepositoryImpl implements CustomizedProductRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Product> getProductsMatching(ProductFilterCriteria criteria, Pageable pageable) {
        Query query = new Query();

        if (criteria.getName() != null) {
            String[] keywords = criteria.getName().split("\\s+");
            Criteria[] andCriteria = new Criteria[keywords.length];

            for (int i = 0; i < keywords.length; i++) {
                andCriteria[i] = new Criteria().orOperator(
                    Criteria.where("name").regex(".*" + keywords[i] + ".*", "i"),
                    Criteria.where("brand").regex(".*" + keywords[i] + ".*", "i"),
                    Criteria.where("shortDescription").regex(".*" + keywords[i] + ".*", "i"),
                    Criteria.where("longDescription").regex(".*" + keywords[i] + ".*", "i")
                );
            }
            query.addCriteria(new Criteria().andOperator(andCriteria));
        }
        if (criteria.getProvider() != null) {
            query.addCriteria(Criteria.where("provider").is(criteria.getProvider()));
        }
        if (criteria.getBrand() != null) {
            query.addCriteria(Criteria.where("brand").is(criteria.getBrand()));
        }
        if (criteria.getVolumeFrom() != null && criteria.getVolumeTo() != null) {
            query.addCriteria(
                Criteria.where("volume")
                    .gte(criteria.getVolumeFrom())
                    .lte(criteria.getVolumeTo()));
        } else if (criteria.getVolumeFrom() != null) {
            query.addCriteria(Criteria.where("volume").gte(criteria.getVolumeFrom()));
        } else if (criteria.getVolumeTo() != null) {
            query.addCriteria(Criteria.where("volume").lte(criteria.getVolumeTo()));
        }
        if (criteria.getIngredients() != null) {
            query.addCriteria(
                Criteria.where("ingredients").all((Object[]) criteria.getIngredients()));
        }

        long totalCount = mongoTemplate.count(query, Product.class);
        query.with(pageable);
        List<Product> products = mongoTemplate.find(query, Product.class);

        return new PageImpl<>(products,
            pageable,
            totalCount);
    }
}
