package pl.edu.pw.mini.ingreedio.api.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pw.mini.ingreedio.api.product.criteria.ProductCriteria;
import pl.edu.pw.mini.ingreedio.api.product.model.Product;

public interface CustomizedProductRepository {
    Page<Product> getProductsMatchingCriteria(ProductCriteria criteria, Pageable pageable);
}
