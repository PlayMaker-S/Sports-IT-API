package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.Category;
import PlayMakers.SportsIT.domain.CustomCategory;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.repository.CategoryRepository;
import PlayMakers.SportsIT.repository.CustomCategoryRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomCategoryService {
    private final CustomCategoryRepository customCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;

    public CustomCategory create(String name, Member member) {
        CustomCategory customCategory = CustomCategory.builder()
                .name(name)
                .member(member)
                .build();
        return customCategoryRepository.save(customCategory);
    }

    // 거절시
    public void delete(Long customCategoryId) {
        log.info("카테고리 삭제 : {}", customCategoryId);
        customCategoryRepository.deleteById(customCategoryId);
    }
    // 수락시
    public Category addCategoryFromCustomCategory(Long customCategoryId, String categoryName, String categoryId) {
        if (customCategoryId==null) {
            log.info("존재하지 않는 커스텀 카테고리입니다.");
            return null;
        }
        if (categoryRepository.existsById(categoryId)) {
            log.info("이미 존재하는 카테고리입니다.");
            return null;
        }
        // 카테고리 대체
        Category category = Category.builder()
                .category(categoryId)
                .name(categoryName)
                .build();

        Category saved = categoryRepository.save(category);

        // 커스텀카테고리 삭제
        customCategoryRepository.deleteById(customCategoryId);

        return saved;
    }

    public List<CustomCategory> getAll() {
        return customCategoryRepository.findAll();
    }
}
