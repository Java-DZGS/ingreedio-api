package pl.edu.pw.mini.ingreedio.api.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.pw.mini.ingreedio.api.model.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Query("select rt from RefreshToken rt where rt.expirationDate <= :expirationDate")
    List<RefreshToken> findAllExpiredTokens(@Param("expirationDate") Instant expiration);
}
