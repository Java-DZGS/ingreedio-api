package pl.edu.pw.mini.ingreedio.api.product.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pw.mini.ingreedio.api.product.model.Product;

@Repository
public interface ProductRepository
    extends MongoRepository<Product, Long>, CustomizedProductRepository {
    Optional<Product> findById(Long id);

    void deleteAll();
}
