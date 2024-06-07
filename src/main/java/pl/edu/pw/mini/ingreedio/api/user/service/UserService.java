package pl.edu.pw.mini.ingreedio.api.user.service;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.auth.exception.UserAlreadyExistsException;
import pl.edu.pw.mini.ingreedio.api.auth.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.auth.service.AuthInfoMangerService;
import pl.edu.pw.mini.ingreedio.api.review.model.Review;
import pl.edu.pw.mini.ingreedio.api.user.exception.UserNotFoundException;
import pl.edu.pw.mini.ingreedio.api.user.model.User;
import pl.edu.pw.mini.ingreedio.api.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthInfoMangerService authInfoMangerService;

    @Transactional(readOnly = true)
    public User getUser(Authentication authentication) {
        AuthInfo info = (AuthInfo) authentication.getPrincipal();
        return getUserById(info.getUser().getId());
    }

    @Transactional
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
    public User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) throws UsernameNotFoundException {
        return authInfoMangerService.getByUsername(username).getUser();
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void handleProductDeletion(long productId) {
        userRepository.productDeleted(productId);
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
    public List<Review> getUserReviews(User user) {
        return user.getReviews();
    }
}
