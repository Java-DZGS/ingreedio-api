package pl.edu.pw.mini.ingreedio.api.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.edu.pw.mini.ingreedio.api.criteria.ProductFilterCriteria;
import pl.edu.pw.mini.ingreedio.api.dto.FullProductDto;
import pl.edu.pw.mini.ingreedio.api.mapper.FullProductDtoMapper;
import pl.edu.pw.mini.ingreedio.api.model.Product;
import pl.edu.pw.mini.ingreedio.api.repository.CustomizedProductRepository;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Repository
public class CustomizedProductRepositoryImpl implements CustomizedProductRepository {
    private final MongoTemplate mongoTemplate;
    private final FullProductDtoMapper fullProductDtoMapper;

    @Override
    public List<FullProductDto> filterProducts(ProductFilterCriteria criteria) {
        Query query = new Query();

        if (!Objects.isNull(criteria.getName()))
            query.addCriteria(Criteria.where("name").is(criteria.getName()));
        if (!Objects.isNull(criteria.getProvider()))
            query.addCriteria(Criteria.where("provider").is(criteria.getProvider()));
        if (!Objects.isNull(criteria.getBrand()))
            query.addCriteria(Criteria.where("brand").is(criteria.getBrand()));
        if (!Objects.isNull(criteria.getVolumeFrom()))
            query.addCriteria(Criteria.where("volume").gte(criteria.getVolumeFrom()));
        if (!Objects.isNull(criteria.getVolumeTo()))
            query.addCriteria(Criteria.where("volume").lte(criteria.getVolumeTo()));
        if (!Objects.isNull(criteria.getIngredients()))
            query.addCriteria(Criteria.where("ingredients").all((Object) criteria.getIngredients()));

        return mongoTemplate.find(query, Product.class).stream().map(fullProductDtoMapper).toList();
    }
}
