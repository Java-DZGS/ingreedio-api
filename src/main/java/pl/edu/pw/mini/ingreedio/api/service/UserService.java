package pl.edu.pw.mini.ingreedio.api.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.model.AuthInfo;
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

    public boolean likeProduct(Integer userId, Long productId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Set<Long> likedProducts = user.getLikedProducts();
            if (!likedProducts.contains(productId)) {
                likedProducts.add(productId);
                user.setLikedProducts(likedProducts);
                userRepository.save(user);
            }
            return true;
        }
        return false;
    }

    public boolean unlikeProduct(Integer userId, Long productId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Set<Long> likedProducts = user.getLikedProducts();
            if (likedProducts.contains(productId)) {
                likedProducts.remove(productId);
                user.setLikedProducts(likedProducts);
                userRepository.save(user);
            }
            return true;
        }
        return false;
    }
}
