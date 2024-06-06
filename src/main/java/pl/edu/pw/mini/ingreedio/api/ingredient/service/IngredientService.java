package pl.edu.pw.mini.ingreedio.api.ingredient.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.auth.service.AuthService;
import pl.edu.pw.mini.ingreedio.api.ingredient.dto.IngredientDto;
import pl.edu.pw.mini.ingreedio.api.ingredient.mapper.IngredientDtoMapper;
import pl.edu.pw.mini.ingreedio.api.ingredient.model.Ingredient;
import pl.edu.pw.mini.ingreedio.api.ingredient.repository.IngredientRepository;
import pl.edu.pw.mini.ingreedio.api.user.model.User;
import pl.edu.pw.mini.ingreedio.api.user.service.UserService;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final UserService userService;
    private final AuthService authService;
    private final IngredientDtoMapper ingredientDtoMapper;

    @Transactional(readOnly = true)
    public Optional<IngredientDto> getIngredientById(Long id) {
        return ingredientRepository.findById(id).map(ingredientDtoMapper);
    }

    @Transactional(readOnly = true)
    public Set<Ingredient> getIngredientsByIds(Set<Long> ids) {
        return new HashSet<>(ingredientRepository.findAllByIdIn(ids));
    }

    @Transactional(readOnly = true)
    public List<IngredientDto> getIngredients(int count, String queryString, User user,
                                              boolean skipAllergens) {
        String[] query = queryString.split("\\s+");
        return ingredientRepository.findIngredientsMatchingQuery(PageRequest.of(0, count),
            query, user, skipAllergens).map(ingredientDtoMapper).toList();
    }

    @Transactional(readOnly = true)
    public List<IngredientDto> getIngredients(int count, String queryString) {
        String[] query = queryString.split("\\s+");
        return ingredientRepository.findIngredientsMatchingQuery(PageRequest.of(0, count), query)
            .map(ingredientDtoMapper).toList();
    }

    @Transactional
    public Ingredient addIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    @Transactional(readOnly = true)
    public List<IngredientDto> getLikedIngredients() {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getLikedIngredients().stream().map(ingredientDtoMapper).toList();
        }
        return List.of();
    }

    @Transactional(readOnly = true)
    public List<IngredientDto> getAllergens() {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getAllergens().stream().map(ingredientDtoMapper).toList();
        }
        return List.of();
    }

    @Transactional
    public boolean likeIngredient(Long id) {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Ingredient> ingredientOptional = ingredientRepository.findById(id);
            if (ingredientOptional.isPresent()) {
                Ingredient ingredient = ingredientOptional.get();
                userService.likeIngredient(user, ingredient);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean unlikeIngredient(Long id) {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Ingredient> ingredientOptional = ingredientRepository.findById(id);
            if (ingredientOptional.isPresent()) {
                Ingredient ingredient = ingredientOptional.get();
                userService.unlikeIngredient(user, ingredient);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean addAllergen(Long id) {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Ingredient> ingredientOptional = ingredientRepository.findById(id);
            if (ingredientOptional.isPresent()) {
                Ingredient ingredient = ingredientOptional.get();
                userService.addAllergen(user, ingredient);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean removeAllergen(Long id) {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Ingredient> ingredientOptional = ingredientRepository.findById(id);
            if (ingredientOptional.isPresent()) {
                Ingredient ingredient = ingredientOptional.get();
                userService.removeAllergen(user, ingredient);
                return true;
            }
        }
        return false;
    }
}
