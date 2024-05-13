package pl.edu.pw.mini.ingreedio.api.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.pw.mini.ingreedio.api.product.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findById(Long id);

    @Query("SELECT r FROM Review r WHERE r.user.id = :userId AND r.productId = :productId")
    Optional<Review> findByUserIdAndProductId(@Param("userId") Long userId,
                                              @Param("productId") Long productId);

    @Query("SELECT r FROM Review r WHERE r.productId = :productId")
    List<Review> getProductReviews(@Param("productId") Long productId);
}
