package PlayMakers.SportsIT.competitions.service;

import PlayMakers.SportsIT.competitions.domain.Category;
import PlayMakers.SportsIT.competitions.repository.CategoryRepository;
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
        return categoryRepository.findById(Long.parseLong(category)).orElse(null);
    }
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category create(String name) {
        Category newCategory = Category.builder()
                .name(name)
                .build();
        return categoryRepository.save(newCategory);
    }
    public void delete(String category) {
        categoryRepository.deleteById(Long.parseLong(category));
    }
}
