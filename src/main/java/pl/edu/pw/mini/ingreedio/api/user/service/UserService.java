package pl.edu.pw.mini.ingreedio.api.user.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.auth.exception.UserAlreadyExistsException;
import pl.edu.pw.mini.ingreedio.api.auth.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.auth.repository.AuthInfoRepository;
import pl.edu.pw.mini.ingreedio.api.ingredient.model.Ingredient;
import pl.edu.pw.mini.ingreedio.api.review.dto.ReviewDto;
import pl.edu.pw.mini.ingreedio.api.review.mapper.ReviewDtoMapper;
import pl.edu.pw.mini.ingreedio.api.review.model.Review;
import pl.edu.pw.mini.ingreedio.api.user.model.User;
import pl.edu.pw.mini.ingreedio.api.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthInfoRepository authRepository;
    private final ReviewDtoMapper reviewDtoMapper;

    public User createUser(String displayName, String email) {
        User user = User.builder().displayName(displayName).email(email).build();

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new UserAlreadyExistsException();
        }
    }

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
    public void likeReview(User user, Review review) {
        Set<Review> likedReviews = user.getLikedReviews();
        Set<Review> dislikedReviews = user.getDislikedReviews();
        likedReviews.add(review);
        dislikedReviews.remove(review);
        user.setLikedReviews(likedReviews);
        user.setDislikedReviews(dislikedReviews);
        userRepository.save(user);
    }

    @Transactional
    public void unlikeReview(User user, Review review) {
        Set<Review> likedReviews = user.getLikedReviews();
        likedReviews.remove(review);
        user.setLikedReviews(likedReviews);
        userRepository.save(user);
    }

    @Transactional
    public void dislikeReview(User user, Review review) {
        Set<Review> dislikedReviews = user.getDislikedReviews();
        Set<Review> likedReviews = user.getLikedReviews();
        dislikedReviews.add(review);
        likedReviews.remove(review);
        user.setDislikedReviews(dislikedReviews);
        user.setLikedReviews(likedReviews);
        userRepository.save(user);
    }

    @Transactional
    public void undislikeReview(User user, Review review) {
        Set<Review> dislikedReviews = user.getDislikedReviews();
        dislikedReviews.remove(review);
        user.setDislikedReviews(dislikedReviews);
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
