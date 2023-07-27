package hu.vilmosdev.Webshop.ShopItem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
  Page<Item> findByCategoryIn(List<String> categories, Pageable pageable);
  Page<Item> findByCategoryInAndVendorIn(List<String> categories, List<String> vendors, Pageable pageable);
  Page<Item> findByVendorIn(List<String> vendors, Pageable pageable);
  @Query("SELECT i FROM ShopItem i ORDER BY RANDOM()")
  Page<Item> findRandom(Pageable pageable);
}
