package pl.edu.pw.mini.ingreedio.api.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.model.Ingredient;
import pl.edu.pw.mini.ingreedio.api.model.User;
import pl.edu.pw.mini.ingreedio.api.repository.IngredientRepository;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final UserService userService;
    private final AuthService authService;

    public Optional<Ingredient> getIngredientById(Long id) {
        return ingredientRepository.findById(id);
    }

    public List<Ingredient> getLikedIngredients() {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getLikedIngredients().stream().toList();
        }
        return List.of();
    }

    public List<Ingredient> getAllergens() {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getAllergens().stream().toList();
        }
        return List.of();
    }

    public boolean likeIngredient(Long id) {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Set<Ingredient> likedIngredients = user.getLikedIngredients();
            Optional<Ingredient> ingredientOptional = ingredientRepository.findById(id);
            if (ingredientOptional.isPresent()) {
                Ingredient ingredient = ingredientOptional.get();
                likedIngredients.add(ingredient);
                return true;
            }
        }
        return false;
    }

    public boolean unlikeIngredient(Long id) {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Set<Ingredient> likedIngredients = user.getLikedIngredients();
            Optional<Ingredient> ingredientOptional = ingredientRepository.findById(id);
            if (ingredientOptional.isPresent()) {
                Ingredient ingredient = ingredientOptional.get();
                likedIngredients.remove(ingredient);
                return true;
            }
        }
        return false;
    }
}
