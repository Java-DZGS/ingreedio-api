package pl.edu.pw.mini.ingreedio.api.brand.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.brand.model.Brand;
import pl.edu.pw.mini.ingreedio.api.brand.repository.BrandRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {
    private final BrandRepository brandRepository;

    public Optional<Brand> getBrandById(Long id) {
        return brandRepository.findById(id);
    }

    public Set<Brand> getBrandsByIds(Set<Long> ids) {
        return new HashSet<>(brandRepository.findAllByIdIn(ids));
    }

}
