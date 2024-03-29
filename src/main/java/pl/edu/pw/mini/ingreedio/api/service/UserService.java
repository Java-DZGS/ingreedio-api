package pl.edu.pw.mini.ingreedio.api.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.model.Product;
import pl.edu.pw.mini.ingreedio.api.model.User;
import pl.edu.pw.mini.ingreedio.api.repository.ProductRepository;
import pl.edu.pw.mini.ingreedio.api.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }
}
