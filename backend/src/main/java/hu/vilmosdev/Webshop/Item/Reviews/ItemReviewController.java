package hu.vilmosdev.Webshop.Item.Reviews;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items/{itemId}/reviews")
@RequiredArgsConstructor
public class ItemReviewController {

  private final ItemReviewService service;
  @PostMapping
  public ResponseEntity createReview(@PathVariable Long itemId, @RequestBody ItemReview review) {
    return service.saveReview(itemId, review);
  }

  @GetMapping
  public Page<List<ItemReview>> getReviews(@PathVariable Long itemId, @RequestParam("page") int page, @RequestParam("size") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return service.findByItemId(itemId, pageable);
  }
}
