package pl.edu.pw.mini.ingreedio.api.ingredient.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.edu.pw.mini.ingreedio.api.ingredient.model.Ingredient;
import pl.edu.pw.mini.ingreedio.api.user.model.User;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findById(Long id);

    List<Ingredient> findAllByIdIn(Set<Long> ids);

    @Query(value = """
            select i from Ingredient i
            where function('string_matches_query', i.name, :query) > 0
            order by function('string_matches_query', i.name, :query) desc,
                i.name
        """)
    Page<Ingredient> findIngredientsMatchingQuery(Pageable pageable, String[] query);

    @Query(value = """
            select i from Ingredient i
            where function('string_matches_query', i.name, :query) > 0
                and not (:skipAllergens = true and i in (
                    select allergens from User u
                    join u.allergens allergens
                    where u = :user
                ))
            order by function('string_matches_query', i.name, :query) desc,
                case when (i in (
                    select allergens from User u
                    join u.allergens allergens
                    where u = :user
                )) then 1 else 0 end asc,
                case when (i in (
                    select likes from User u
                    join u.likedIngredients likes
                    where u = :user
                )) then 1 else 0 end desc,
                i.name
        """)
    Page<Ingredient> findIngredientsMatchingQuery(Pageable pageable, String[] query, User user,
                                                  boolean skipAllergens);
}
