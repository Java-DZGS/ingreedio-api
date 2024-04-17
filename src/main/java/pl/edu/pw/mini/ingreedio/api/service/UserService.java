package pl.edu.pw.mini.ingreedio.api.service;

import java.util.List;
import java.util.Optional;
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
}
