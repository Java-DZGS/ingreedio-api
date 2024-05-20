package pl.edu.pw.mini.ingreedio.api.product.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.auth.service.AuthService;
import pl.edu.pw.mini.ingreedio.api.product.dto.IngredientDto;
import pl.edu.pw.mini.ingreedio.api.product.mapper.IngredientDtoMapper;
import pl.edu.pw.mini.ingreedio.api.product.model.Ingredient;
import pl.edu.pw.mini.ingreedio.api.product.repository.IngredientRepository;
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
    public Set<IngredientDto> getIngredientsByIds(Set<Long> ids) {
        return ingredientRepository.findAllByIdIn(ids).stream()
            .map(ingredientDtoMapper)
            .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public List<IngredientDto> getIngredients(String query) {
        List<Ingredient> ingredients = ingredientRepository
            .findByNameContainingIgnoreCase(query);
        return ingredients.stream().map(ingredientDtoMapper)
                .collect(Collectors.toList());
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
