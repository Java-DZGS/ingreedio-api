package pl.edu.pw.mini.ingreedio.api.repository.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.edu.pw.mini.ingreedio.api.criteria.ProductFilterCriteria;
import pl.edu.pw.mini.ingreedio.api.dto.ProductDto;
import pl.edu.pw.mini.ingreedio.api.mapper.ProductDtoMapper;
import pl.edu.pw.mini.ingreedio.api.model.Product;
import pl.edu.pw.mini.ingreedio.api.repository.CustomizedProductRepository;

@RequiredArgsConstructor
@Repository
public class CustomizedProductRepositoryImpl implements CustomizedProductRepository {
    private final MongoTemplate mongoTemplate;
    private final ProductDtoMapper productDtoMapper;

    @Override
    public List<ProductDto> getProductsMatching(ProductFilterCriteria criteria) {
        Query query = new Query();

        if (criteria.getProvider() != null) {
            query.addCriteria(Criteria.where("provider").is(criteria.getProvider()));
        }
        if (criteria.getBrand() != null) {
            query.addCriteria(Criteria.where("brand").is(criteria.getBrand()));
        }
        if (criteria.getVolumeFrom() != null) {
            query.addCriteria(Criteria.where("volume").gte(criteria.getVolumeFrom()));
        }
        if (criteria.getVolumeTo() != null) {
            query.addCriteria(Criteria.where("volume").lte(criteria.getVolumeTo()));
        }
        if (criteria.getIngredients() != null) {
            query.addCriteria(
                Criteria.where("ingredients").all((Object) criteria.getIngredients()));
        }

        return mongoTemplate.find(query, Product.class).stream().map(productDtoMapper).toList();
    }
}
