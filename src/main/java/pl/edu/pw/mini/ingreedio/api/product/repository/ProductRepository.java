package pl.edu.pw.mini.ingreedio.api.product.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pw.mini.ingreedio.api.product.model.ProductDocument;

@Repository
public interface ProductRepository
    extends MongoRepository<ProductDocument, Long>, CustomizedProductRepository {
    Optional<ProductDocument> findById(Long id);

    void deleteAll();
}
