package pl.edu.pw.mini.ingreedio.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pw.mini.ingreedio.api.model.Product;

import java.util.Optional;
import java.util.stream.Stream;


@Repository
public interface ProductRepository extends MongoRepository<Product, Long> {
    Optional<Product> findById(Long id);

    Stream<Product> findByNameContaining(String name);

    Stream<Product> findByVolumeBetween(Integer from, Integer to);

    Stream<Product> findByIngredientsContaining(String ingredient);

    Stream<Product> findByProvider(String provider);

    Stream<Product> findByBrand(String provider);


}
