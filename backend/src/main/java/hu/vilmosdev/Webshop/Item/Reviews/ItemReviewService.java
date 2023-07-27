package hu.vilmosdev.Webshop.ShopItem.Reviews;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ItemReviewService {

  ItemReviewRepository repo;
  public ResponseEntity saveReview(ItemReview review) {
    repo.save(review);
    return ResponseEntity.ok().build();
  }

  public Page<ItemReview> findByItemId(Long itemId, Pageable pageable) {
    return repo.findByItemId(itemId, pageable);
  }
}
