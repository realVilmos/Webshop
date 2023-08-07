package hu.vilmosdev.Webshop.Item.Category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items/category")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;
  @GetMapping("/{id}")
  public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
    Category category = categoryService.findById(id);
    System.out.println(category);
    return ResponseEntity.ok(category);
  }

  @GetMapping("/main-categories")
  public ResponseEntity<List<Category>> getMainCategories(){
    return ResponseEntity.ok().body(categoryService.getMainCategories());
  }
}
