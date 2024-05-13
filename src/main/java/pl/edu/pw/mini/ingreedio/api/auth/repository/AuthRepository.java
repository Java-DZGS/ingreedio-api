package pl.edu.pw.mini.ingreedio.api.auth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pw.mini.ingreedio.api.auth.model.AuthInfo;

@Repository
public interface AuthRepository extends JpaRepository<AuthInfo, Long> {
    Optional<AuthInfo> findByUsername(String username);
}
