package hu.vilmosdev.Webshop.Item.Category;

import java.util.Set;

public interface CategoryWithoutParent {
  Long getId();
  String getName();

  Set<CategoryWithoutParent> getSubCategories();
}
