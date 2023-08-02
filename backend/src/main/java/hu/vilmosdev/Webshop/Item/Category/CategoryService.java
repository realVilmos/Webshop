package hu.vilmosdev.Webshop.Item.Category;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;

  @Transactional
  public Category createCategory(String name, Category parent) {
    Category category = new Category();
    category.setName(name);
    category.setParent(parent);
    return categoryRepository.save(category);
  }

  @Transactional
  public Category addSubcategory(Category parent, Category subcategory) {
    parent.getSubcategories().add(subcategory);
    subcategory.setParent(parent);
    return categoryRepository.save(parent);
  }

  public Optional<Category> findById(Long id) {
    return categoryRepository.findById(id);
  }
}
