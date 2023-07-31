package hu.vilmosdev.Webshop.Item.Reviews;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items/{itemId}/reviews")
public class ItemReviewController {

  private ItemReviewService service;
  @PostMapping
  public ResponseEntity createReview(@PathVariable Long itemId, @RequestBody ItemReview review) {
    return service.saveReview(review);
  }

  @GetMapping
  public Page<ItemReview> getReviews(@PathVariable Long itemId, Pageable pageable) {
    return service.findByItemId(itemId, pageable);
  }
}
