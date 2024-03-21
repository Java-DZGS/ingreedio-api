package pl.edu.pw.mini.ingreedio.api.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pw.mini.ingreedio.api.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(Integer id);
}
