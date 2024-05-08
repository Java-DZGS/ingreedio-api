package pl.edu.pw.mini.ingreedio.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pw.mini.ingreedio.api.criteria.ProductsCriteria;
import pl.edu.pw.mini.ingreedio.api.model.Product;

public interface CustomizedProductRepository {
    Page<Product> getProductsMatchingCriteria(ProductsCriteria criteria, Pageable pageable);
}
