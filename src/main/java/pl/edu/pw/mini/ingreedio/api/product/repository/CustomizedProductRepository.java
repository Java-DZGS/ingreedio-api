package pl.edu.pw.mini.ingreedio.api.product.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pw.mini.ingreedio.api.product.criteria.ProductCriteria;
import pl.edu.pw.mini.ingreedio.api.product.model.ProductDocument;

public interface CustomizedProductRepository {
    Page<ProductDocument> getProductsMatchingCriteria(ProductCriteria criteria, Pageable pageable);
}
