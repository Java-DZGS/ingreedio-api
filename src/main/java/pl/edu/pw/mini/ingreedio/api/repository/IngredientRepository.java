package pl.edu.pw.mini.ingreedio.api.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import pl.edu.pw.mini.ingreedio.api.model.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findById(Long id);

    List<Ingredient> findByNameContainingIgnoreCase(@Param("name")String name);
}
