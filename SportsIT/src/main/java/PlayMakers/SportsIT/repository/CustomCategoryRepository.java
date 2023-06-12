package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.CustomCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomCategoryRepository extends JpaRepository<CustomCategory, Long> {
    void deleteById(Long customCategoryId);
}
