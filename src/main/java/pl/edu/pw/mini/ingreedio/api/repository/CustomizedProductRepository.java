package pl.edu.pw.mini.ingreedio.api.repository;

import java.util.List;
import pl.edu.pw.mini.ingreedio.api.criteria.ProductFilterCriteria;
import pl.edu.pw.mini.ingreedio.api.dto.ProductDto;

public interface CustomizedProductRepository {
    List<ProductDto> getProductsMatching(ProductFilterCriteria criteria);
}
