package pl.edu.pw.mini.ingreedio.api.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pw.mini.ingreedio.api.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}
