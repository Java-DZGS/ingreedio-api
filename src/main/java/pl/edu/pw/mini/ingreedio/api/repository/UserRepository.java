package pl.edu.pw.mini.ingreedio.api.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.edu.pw.mini.ingreedio.api.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(Integer id);

    @Query("SELECT u FROM User u WHERE :productId MEMBER OF u.likedProducts")
    List<User> findUsersByLikedProduct(Long productId);
}
