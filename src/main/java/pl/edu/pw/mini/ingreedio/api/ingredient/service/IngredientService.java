package pl.edu.pw.mini.ingreedio.api.ingredient.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.ingredient.model.Ingredient;
import pl.edu.pw.mini.ingreedio.api.ingredient.repository.IngredientRepository;
import pl.edu.pw.mini.ingreedio.api.product.exception.IngredientNotFoundException;
import pl.edu.pw.mini.ingreedio.api.user.model.User;
import pl.edu.pw.mini.ingreedio.api.user.service.UserService;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<Ingredient> getIngredients(int count, String queryString, User user,
                                           boolean skipAllergens) {
        String[] query = queryString.split("\\s+");
        return ingredientRepository.findIngredientsMatchingQuery(
                PageRequest.of(0, count), query, user, skipAllergens)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<Ingredient> getIngredients(int count, String queryString) {
        String[] query = queryString.split("\\s+");
        return ingredientRepository.findIngredientsMatchingQuery(
                PageRequest.of(0, count), query)
            .toList();
    }

    @Transactional(readOnly = true)
    public Ingredient getIngredientById(long id) throws IngredientNotFoundException {
        return ingredientRepository.findById(id)
            .orElseThrow(() -> new IngredientNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Set<Ingredient> getIngredientsByIds(Set<Long> ids) {
        return new HashSet<>(ingredientRepository.findAllByIdIn(ids));
    }

    @Transactional
    public Ingredient addIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    @Transactional(readOnly = true)
    public List<Ingredient> getLikedIngredients(User user) {
        return user.getLikedIngredients().stream().toList();
    }

    @Transactional(readOnly = true)
    public List<Ingredient> getAllergens(User user) {
        return user.getAllergens().stream().toList();
    }

    @Transactional
    public void likeIngredient(long id, User user) throws IngredientNotFoundException {
        Ingredient ingredient = this.getIngredientById(id);

        userService.likeIngredient(user, ingredient);
    }

    @Transactional
    public void unlikeIngredient(long id, User user) throws IngredientNotFoundException {
        Ingredient ingredient = this.getIngredientById(id);

        userService.unlikeIngredient(user, ingredient);
    }

    @Transactional
    public void addAllergen(long id, User user) throws IngredientNotFoundException {
        Ingredient ingredient = this.getIngredientById(id);

        userService.addAllergen(user, ingredient);
    }

    @Transactional
    public void removeAllergen(long id, User user) throws IngredientNotFoundException {
        Ingredient ingredient = this.getIngredientById(id);

        userService.removeAllergen(user, ingredient);
    }
}
