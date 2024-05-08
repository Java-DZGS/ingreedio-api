package pl.edu.pw.mini.ingreedio.api.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.dto.IngredientDto;
import pl.edu.pw.mini.ingreedio.api.mapper.IngredientDtoMapper;
import pl.edu.pw.mini.ingreedio.api.model.Ingredient;
import pl.edu.pw.mini.ingreedio.api.model.User;
import pl.edu.pw.mini.ingreedio.api.repository.IngredientRepository;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final UserService userService;
    private final AuthService authService;
    private final IngredientDtoMapper ingredientDtoMapper;

    public Optional<IngredientDto> getIngredientById(Long id) {
        return ingredientRepository.findById(id).map(ingredientDtoMapper);
    }

    public Set<IngredientDto> getIngredientsByIds(Set<Long> ids) {
        return ingredientRepository.findAllByIdIn(ids).stream()
            .map(ingredientDtoMapper)
            .collect(Collectors.toSet());
    }

    public List<IngredientDto> getLikedIngredients() {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getLikedIngredients().stream().map(ingredientDtoMapper).toList();
        }
        return List.of();
    }

    public List<IngredientDto> getAllergens() {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getAllergens().stream().map(ingredientDtoMapper).toList();
        }
        return List.of();
    }

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