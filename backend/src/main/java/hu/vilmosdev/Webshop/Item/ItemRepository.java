package hu.vilmosdev.Webshop.Item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
  Page<Item> findByCategoryId(Long category_id, Pageable pageable);
  Page<Item> findByCategoryInAndVendorIn(List<String> categories, List<String> vendors, Pageable pageable);
  Page<Item> findByVendorIn(List<String> vendors, Pageable pageable);
  @Query("SELECT i FROM Item i ORDER BY RANDOM()")
  Page<Item> findRandom(Pageable pageable);

}
