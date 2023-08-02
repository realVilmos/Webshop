package hu.vilmosdev.Webshop.Item.Category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items/category")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;
  @PostMapping
  public ResponseEntity<Category> createCategory(@RequestParam String name,
                                                 @RequestParam(required = false) Long parentId) {
    Category parent = parentId != null ? categoryService.findById(parentId).orElse(null) : null;
    Category newCategory = categoryService.createCategory(name, parent);
    return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
  }

  @PostMapping("/{parentId}/subcategories")
  public ResponseEntity<Category> addSubcategory(@PathVariable Long parentId, @RequestParam String name) {
    Category parent = categoryService.findById(parentId)
      .orElseThrow(() -> new RuntimeException("Parent category not found"));
    Category subcategory = categoryService.createCategory(name, parent);
    categoryService.addSubcategory(parent, subcategory);
    return new ResponseEntity<>(subcategory, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
    Category category = categoryService.findById(id)
      .orElseThrow(() -> new RuntimeException("Category not found"));
    return new ResponseEntity<>(category, HttpStatus.OK);
  }
}
