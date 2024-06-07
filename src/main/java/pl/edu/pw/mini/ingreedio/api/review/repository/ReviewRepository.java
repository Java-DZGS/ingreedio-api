package pl.edu.pw.mini.ingreedio.api.review.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.pw.mini.ingreedio.api.review.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findById(int id);

    @Query("SELECT r FROM Review r WHERE r.user.id = :userId AND r.productId = :productId")
    Optional<Review> findByUserIdAndProductId(@Param("userId") long userId,
                                              @Param("productId") long productId);

    @Query("SELECT r FROM Review r WHERE r.productId = :productId")
    List<Review> getProductReviews(@Param("productId") long productId);

}
