package pl.edu.pw.mini.ingreedio.api.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Integer id) {
        Optional<User> userOptional = userRepository.findById(id);
        userOptional.ifPresent(user -> Hibernate.initialize(user.getLikedProducts()));
        return userOptional;
    }

    public Optional<User> getUserByUsername(String username) {
        return authRepository.findByUsername(username).map(AuthInfo::getUser);
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
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
}
