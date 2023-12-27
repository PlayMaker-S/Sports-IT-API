package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.competitions.domain.Category;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.service.CategoryService;
import PlayMakers.SportsIT.service.CompetitionService;
import PlayMakers.SportsIT.service.CustomCategoryService;
import PlayMakers.SportsIT.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoryService categoryService;
    private final CustomCategoryService customCategoryService;
    private final MemberService memberService;
    private final CompetitionService competitionService;

    /**
     * 모든 카테고리를 가져온다.
     * @return 카테고리 리스트
     */
    @GetMapping("/all")
    public ResponseEntity<Object> getAllCategory() {
        Map<String, Object> result = new HashMap<>();
        result.put("result", categoryService.findAll());
        result.put("success", true);
        return ResponseEntity.ok(result);
    }

    /**
     * 특정 유저의 관심 종목을 가져온다.
     * @param uid 유저 아이디
     * @return
     */
    @GetMapping("/member/{uid}")
    public ResponseEntity<Object> getCategoriesByMember(@PathVariable Long uid) {
        Map<String, Object> result = new HashMap<>();
        result.put("result", memberService.getCategoriesByUid(uid));
        result.put("success", true);
        return ResponseEntity.ok(result);
    }

    /**
     * 특정 대회의 종목을 가져온다.
     * @param competitionId
     * @return
     */
    @GetMapping("/competition/{competitionId}")
    public ResponseEntity<Object> getCategoriesByCompetition(@PathVariable Long competitionId){
        Map<String, Object> result = new HashMap<>();
        result.put("result", competitionService.getCategoriesByCompetition(competitionId));
        result.put("success", true);
        return ResponseEntity.ok(result);
    }

    /**
     * 운동 종목을 추가한다.
     * @param data category, name
     * @return
     */
    @PostMapping("/add")
    public ResponseEntity<Object> addCategory(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        String category = String.valueOf(data.get("category"));
        String name = String.valueOf(data.get("name"));
        result.put("result", categoryService.create(category, name));
        result.put("success", true);

        return ResponseEntity.ok(result);
    }

    /**
     * 운동 종목을 삭제한다.
     * @param data category
     * @return
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteCategory(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        String category = String.valueOf(data.get("category"));
        categoryService.delete(category);
        result.put("success", true);
        return ResponseEntity.ok(result);
    }

    /**
     * 커스텀 운동 종목을 조회한다.
     * @return
     */
    @GetMapping("/customCategory/all")
    public ResponseEntity<Object> getAllCustomCategories() {
        Map<String, Object> result = new HashMap<>();
        result.put("result", customCategoryService.getAll());
        result.put("success", true);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/customCategory")
    public ResponseEntity<Object> addCustomCategory(@RequestBody Map<String, Object> data,
                                                    @AuthenticationPrincipal User user) {
        Map<String, Object> result = new HashMap<>();
        Member member;
        if (user == null) member = null;
        else member = memberService.findOne(user.getUsername());
        String name = data.get("name").toString();
        result.put("result", customCategoryService.create(name, member));
        result.put("success", true);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/replace")
    public ResponseEntity<Object> replaceCustomCategoryToCategory(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        Long customCategoryId = Long.parseLong(String.valueOf(data.get("customCategoryId")));
        String name = String.valueOf(data.get("name"));
        String categoryId = String.valueOf(data.get("categoryId"));
        Category newCategory = customCategoryService.addCategoryFromCustomCategory(customCategoryId, name, categoryId);
        if (newCategory == null) {
            result.put("success", false);
            result.put("message", "새 카테고리를 추가할 수 없습니다.");
            return ResponseEntity.badRequest().body(result);
        }
        result.put("result", newCategory);
        result.put("success", true);
        return ResponseEntity.ok(result);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(result);
    }
}
