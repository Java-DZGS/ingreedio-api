package pl.edu.pw.mini.ingreedio.api.brand.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.brand.exception.BrandNotFoundException;
import pl.edu.pw.mini.ingreedio.api.brand.model.Brand;
import pl.edu.pw.mini.ingreedio.api.brand.repository.BrandRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {
    private final BrandRepository brandRepository;

    public Brand getBrandById(long id) {
        return brandRepository.findById(id).orElseThrow(() -> new BrandNotFoundException(id));
    }

    public Set<Brand> getBrandsByIds(Set<Long> ids) {
        return new HashSet<>(brandRepository.findAllByIdIn(ids));
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }
}
