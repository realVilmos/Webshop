package hu.vilmosdev.Webshop.Item.Category;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;
  @PersistenceContext
  private EntityManager entityManager;

  private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

  @Transactional
  public Category createCategory(CategoryCreationRequest request) {
    try{
      Category parent = request.getParentId() != null ? findCategoryById(request.getParentId()) : null;
      Category category = new Category();
      category.setName(request.getName());
      category.setParent(parent);
      return categoryRepository.save(category);
    }catch (Exception e) {
      logger.error("Error during creation of Category: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during creation of Category: ", e);
    }
  }

  public Category findCategoryById(Long id){
    return categoryRepository.findById(id).get();
  }

  @Transactional
  public Category findById(Long id) {
    try{
      Category category = categoryRepository.findById(id).get();
      System.out.println(category);
      return category;
    }catch (Exception e) {
      logger.error("Error finding the Category: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error finding the  Category: ", e);
    }
  }

  public List<Category> getMainCategories() {
    return categoryRepository.findByParentIsNull();
  }
}
