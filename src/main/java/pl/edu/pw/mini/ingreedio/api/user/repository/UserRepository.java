package pl.edu.pw.mini.ingreedio.api.user.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import pl.edu.pw.mini.ingreedio.api.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(Long id);

    @Query("SELECT u FROM User u WHERE :productId MEMBER OF u.likedProducts")
    List<User> findUsersByLikedProduct(Long productId);

    @Procedure("delete_product_user")
    void productDeleted(long id);
}
