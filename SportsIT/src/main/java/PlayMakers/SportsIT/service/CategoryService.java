package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.Category;
import PlayMakers.SportsIT.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category findByName(String name) {
        return categoryRepository.findByName(name).orElse(null);
    }
    public Category findById(String category) {
        return categoryRepository.findById(category).orElse(null);
    }
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category create(String category, String name) {
        Category newCategory = Category.builder()
                .category(category)
                .name(name)
                .build();
        return categoryRepository.save(newCategory);
    }

    public void delete(String category) {
        categoryRepository.deleteById(category);
    }
}
