package pl.edu.pw.mini.ingreedio.api.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import pl.edu.pw.mini.ingreedio.api.model.TestDoc;

//TODO: REMOVE
public interface TestRepository extends MongoRepository<TestDoc, String> {
    Optional<TestDoc> findById(String id);
}
