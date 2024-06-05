package pl.edu.pw.mini.ingreedio.api.category.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pw.mini.ingreedio.api.category.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findById(long id);

    List<Category> findAllByIdIn(Set<Long> ids);
}
