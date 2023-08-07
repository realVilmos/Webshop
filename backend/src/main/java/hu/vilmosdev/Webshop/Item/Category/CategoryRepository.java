package hu.vilmosdev.Webshop.Item.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  Category findByName(String name);

  List<Category> findByParentIsNull();

  @EntityGraph(attributePaths = {"subCategories"})
  Optional<Category> findById(Long id);
}
