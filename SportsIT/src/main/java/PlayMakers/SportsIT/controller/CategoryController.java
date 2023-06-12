package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoryService categoryService;
    @GetMapping("/all")
    public ResponseEntity<Object> getAllCategory() {
        Map<String, Object> result = new HashMap<>();
        result.put("result", categoryService.findAll());
        result.put("success", true);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addCategory(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        String category = String.valueOf(data.get("category"));
        String name = String.valueOf(data.get("name"));
        result.put("result", categoryService.create(category, name));
        result.put("success", true);

        return ResponseEntity.ok(result);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteCategory(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        String category = String.valueOf(data.get("category"));
        categoryService.delete(category);
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
