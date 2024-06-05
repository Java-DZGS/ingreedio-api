package pl.edu.pw.mini.ingreedio.api.provider.service;

import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.provider.exception.ProviderNotFoundException;
import pl.edu.pw.mini.ingreedio.api.provider.model.Provider;
import pl.edu.pw.mini.ingreedio.api.provider.repository.ProviderRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProviderService {
    private final ProviderRepository providerRepository;

    public Provider getProviderById(long id) {
        return providerRepository.findById(id).orElseThrow(() -> new ProviderNotFoundException(id));
    }

    public Set<Provider> getProvidersByIds(Set<Long> ids) {
        return new HashSet<>(providerRepository.findAllByIdIn(ids));
    }

}
