package pl.edu.pw.mini.ingreedio.api.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.model.Ingredient;
import pl.edu.pw.mini.ingreedio.api.model.User;
import pl.edu.pw.mini.ingreedio.api.repository.AuthRepository;
import pl.edu.pw.mini.ingreedio.api.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthRepository authRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }


    public Optional<User> getUserByUsername(String username) {
        return authRepository.findByUsername(username).map(AuthInfo::getUser);
    }

    public void likeIngredient(User user, Ingredient ingredient) {
        Set<Ingredient> likedIngredients = user.getLikedIngredients();
        likedIngredients.add(ingredient);
        user.setLikedIngredients(likedIngredients);
        userRepository.save(user);
    }

    public void unlikeIngredient(User user, Ingredient ingredient) {
        Set<Ingredient> likedIngredients = user.getLikedIngredients();
        likedIngredients.remove(ingredient);
        user.setLikedIngredients(likedIngredients);
        userRepository.save(user);
    }

    public void addAllergen(User user, Ingredient ingredient) {
        Set<Ingredient> allergens = user.getAllergens();
        allergens.add(ingredient);
        user.setAllergens(allergens);
        userRepository.save(user);
    }

    public void removeAllergen(User user, Ingredient ingredient) {
        Set<Ingredient> allergens = user.getAllergens();
        allergens.remove(ingredient);
        user.setAllergens(allergens);
        userRepository.save(user);
    }
}
