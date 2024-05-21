package pl.edu.pw.mini.ingreedio.api.category.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.category.model.Category;
import pl.edu.pw.mini.ingreedio.api.category.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Set<Category> getCategoriesByIds(Set<Long> ids) {
        return new HashSet<>(categoryRepository.findAllByIdIn(ids));
    }

}
