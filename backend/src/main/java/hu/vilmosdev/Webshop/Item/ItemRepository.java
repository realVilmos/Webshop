package hu.vilmosdev.Webshop.Item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
  Page<Item> findByCategoryIdIn(List<Long> categoryIds, Pageable pageable);
  Page<Item> findByCategoryInAndVendorIn(List<Long> categoryIds, List<Long> vendorIds, Pageable pageable);
  Page<Item> findByVendorIdIn(List<Long> vendorIds, Pageable pageable);
  @Query("SELECT i FROM Item i ORDER BY RANDOM()")
  Page<Item> findRandom(Pageable pageable);

}
