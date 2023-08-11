package hu.vilmosdev.Webshop.Item.Category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/items/category")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;
  @GetMapping("/{id}")
  public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
    Category category = categoryService.findById(id);
    (category);
    return ResponseEntity.ok(category);
  }

  @GetMapping("/main-categories")
  public ResponseEntity<List<Category>> getMainCategories(){
    return ResponseEntity.ok().body(categoryService.getMainCategories());
  }
}
