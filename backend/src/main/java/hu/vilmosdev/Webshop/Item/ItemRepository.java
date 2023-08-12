package hu.vilmosdev.Webshop.Item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
  Page<Item> findByCategory_IdIn(List<Long> categoryIds, Pageable pageable);
  @Query("SELECT i FROM Item i where i.manufacturer IN :manufacturers")
  Page<Item> findByManufacturerIn(@Param("manufacturers") List<String> manufacturers, Pageable pageable);
  Page<Item> findByCategory_IdInAndManufacturerIn(List<Long> categoryIds, List<String> manufacturers, Pageable pageable);
  @Query("SELECT i FROM Item i ORDER BY RANDOM()")
  Page<Item> findRandom(Pageable pageable);
}
