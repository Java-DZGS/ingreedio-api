package pl.edu.pw.mini.ingreedio.api.provider.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.category.exception.CategoryNotFoundException;
import pl.edu.pw.mini.ingreedio.api.provider.model.Provider;
import pl.edu.pw.mini.ingreedio.api.provider.repository.ProviderRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProviderService {
    private final ProviderRepository providerRepository;

    public Provider getProviderById(long id) {
        return providerRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public Set<Provider> getProvidersByIds(Set<Long> ids) {
        return new HashSet<>(providerRepository.findAllByIdIn(ids));
    }

}
