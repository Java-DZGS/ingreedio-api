package pl.edu.pw.mini.ingreedio.api.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pw.mini.ingreedio.api.model.Product;

@Repository
public interface ProductRepository
    extends MongoRepository<Product, Long>, CustomizedProductRepository {
    Optional<Product> findById(Long id);
}
