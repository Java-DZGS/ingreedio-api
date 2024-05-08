package pl.edu.pw.mini.ingreedio.api.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pw.mini.ingreedio.api.model.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findById(Long id);

    List<Ingredient> findAllByIdIn(Set<Long> ids);
}
