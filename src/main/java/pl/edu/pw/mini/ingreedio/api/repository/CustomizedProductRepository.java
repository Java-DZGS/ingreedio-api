package pl.edu.pw.mini.ingreedio.api.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pw.mini.ingreedio.api.criteria.ProductFilterCriteria;
import pl.edu.pw.mini.ingreedio.api.model.Product;

public interface CustomizedProductRepository {
    Page<Product> getProductsMatching(ProductFilterCriteria criteria, Pageable pageable);
}
