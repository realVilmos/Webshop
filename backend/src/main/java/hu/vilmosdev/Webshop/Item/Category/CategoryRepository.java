package hu.vilmosdev.Webshop.Item.Category;

import hu.vilmosdev.Webshop.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  Category findByName(String name);
}
