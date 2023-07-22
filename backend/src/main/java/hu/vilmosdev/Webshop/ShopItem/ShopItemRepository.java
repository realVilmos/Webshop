package hu.vilmosdev.Webshop.ShopItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShopItemRepository extends JpaRepository<ShopItem, Long> {
  //Agyrák volt amire ez végre működni tetszett
  //@Query(value = "SELECT si FROM shop_item si WHERE si.categories IN :categories")
  List<ShopItem> findByCategoriesIn(@Param("categories") List<String> categories);
}
