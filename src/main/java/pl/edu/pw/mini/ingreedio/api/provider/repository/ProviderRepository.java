package pl.edu.pw.mini.ingreedio.api.provider.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pw.mini.ingreedio.api.provider.model.Provider;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
    Optional<Provider> findById(long id);

    List<Provider> findAllByIdIn(Set<Long> ids);
}
