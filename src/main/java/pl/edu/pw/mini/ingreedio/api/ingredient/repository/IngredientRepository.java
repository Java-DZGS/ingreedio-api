package pl.edu.pw.mini.ingreedio.api.ingredient.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.pw.mini.ingreedio.api.ingredient.model.Ingredient;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findById(Long id);

    List<Ingredient> findAllByIdIn(Set<Long> ids);

    List<Ingredient> findByNameContainingIgnoreCase(@Param("name")String name);
}
