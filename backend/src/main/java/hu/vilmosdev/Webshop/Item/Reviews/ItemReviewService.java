package hu.vilmosdev.Webshop.Item.Reviews;

import hu.vilmosdev.Webshop.Item.Item;
import hu.vilmosdev.Webshop.Item.ItemRepository;
import hu.vilmosdev.Webshop.Item.ItemService;
import hu.vilmosdev.Webshop.Item.NotFoundException;
import hu.vilmosdev.Webshop.user.Address;
import hu.vilmosdev.Webshop.user.User;
import hu.vilmosdev.Webshop.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ItemReviewService {
  private final ItemRepository itemRepo;
  private final ItemReviewRepository reviewRepository;
  private final UserRepository userRepository;

  private static final Logger logger = LoggerFactory.getLogger(ItemReviewService.class);
  public ResponseEntity<?> saveReview(Long itemId, ItemReview review) {
    try{
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if(auth instanceof UsernamePasswordAuthenticationToken){
        User userDetails = (User) auth.getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());
        review.setUser(user);

        Item item = itemRepo.getReferenceById(itemId);
        item.addReview(review);
        itemRepo.save(item);
      }else{
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not valid authorization");
      }
      return ResponseEntity.ok().body("{\"message\": \"creation_successful\"}");
    }catch(NoSuchElementException e){
      throw new NotFoundException(e.getMessage());
    }catch (RuntimeException e) {
      logger.error("Error when saving review: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error when creating review: ", e);
    }
  }

  public Page<List<ItemReview>> findByItemId(Long itemId, Pageable pageable) {
    return reviewRepository.findByItemId(itemId, pageable);
  }
}
