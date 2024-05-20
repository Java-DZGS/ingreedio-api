package pl.edu.pw.mini.ingreedio.api.user.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.auth.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.auth.repository.AuthRepository;
import pl.edu.pw.mini.ingreedio.api.product.dto.ReviewDto;
import pl.edu.pw.mini.ingreedio.api.product.mapper.ReviewDtoMapper;
import pl.edu.pw.mini.ingreedio.api.product.model.Ingredient;
import pl.edu.pw.mini.ingreedio.api.user.model.User;
import pl.edu.pw.mini.ingreedio.api.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final ReviewDtoMapper reviewDtoMapper;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Integer id) {
        Optional<User> userOptional = userRepository.findById(id);
        userOptional.ifPresent(user -> Hibernate.initialize(user.getLikedProducts()));
        return userOptional;
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return authRepository.findByUsername(username).map(AuthInfo::getUser);
    }

    @Transactional
    public void likeIngredient(User user, Ingredient ingredient) {
        Set<Ingredient> likedIngredients = user.getLikedIngredients();
        likedIngredients.add(ingredient);
        user.setLikedIngredients(likedIngredients);
        userRepository.save(user);
    }

    @Transactional
    public void unlikeIngredient(User user, Ingredient ingredient) {
        Set<Ingredient> likedIngredients = user.getLikedIngredients();
        likedIngredients.remove(ingredient);
        user.setLikedIngredients(likedIngredients);
        userRepository.save(user);
    }

    @Transactional
    public void addAllergen(User user, Ingredient ingredient) {
        Set<Ingredient> allergens = user.getAllergens();
        allergens.add(ingredient);
        user.setAllergens(allergens);
        userRepository.save(user);
    }

    @Transactional
    public void removeAllergen(User user, Ingredient ingredient) {
        Set<Ingredient> allergens = user.getAllergens();
        allergens.remove(ingredient);
        user.setAllergens(allergens);
        userRepository.save(user);
    }

    @Transactional
    public boolean likeProduct(Integer userId, Long productId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return false;
        }
        
        User user = userOptional.get();
        Hibernate.initialize(user.getLikedProducts());
        Set<Long> likedProducts = user.getLikedProducts();
        if (!likedProducts.contains(productId)) {
            likedProducts.add(productId);
            user.setLikedProducts(likedProducts);
            userRepository.save(user);
        }
        return true;
    }

    @Transactional
    public boolean unlikeProduct(Integer userId, Long productId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return false;
        }
        
        User user = userOptional.get();
        Hibernate.initialize(user.getLikedProducts());
        Set<Long> likedProducts = user.getLikedProducts();
        if (likedProducts.contains(productId)) {
            likedProducts.remove(productId);
            user.setLikedProducts(likedProducts);
            userRepository.save(user);
        }
        return true;
    }

    @Transactional
    public void deleteProduct(Long productId) {
        List<User> users = userRepository.findUsersByLikedProduct(productId);

        for (User user : users) {
            user.getLikedProducts().remove(productId);
        }

        userRepository.saveAll(users);
    }

    @Transactional
    public List<ReviewDto> getUserRatings(User user) {
        return user.getReviews().stream()
            .map(reviewDtoMapper)
            .collect(Collectors.toList());
    }
}
