package hu.vilmosdev.Webshop.Item.Reviews;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemReviewRepository extends JpaRepository<ItemReview, Long> {
  @Query("SELECT r FROM ItemReview r WHERE r.item.id = :itemId ORDER BY r.reviewDate ASC")
  Page<ItemReview> findByItemId(@Param("itemId") Long itemId, Pageable pageable);
}
