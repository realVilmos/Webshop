package hu.vilmosdev.Webshop.Item;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

  private final ItemService itemService;

  @GetMapping(value = "/random", params = {"page", "size"})
  public ResponseEntity<Page<ReducedItemResponse>> getItems(@RequestParam("page") int page,
                                           @RequestParam("size") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return itemService.findRandom(pageable);
  }

  @GetMapping(params = {"page", "size", "category", "vendor"})
  public ResponseEntity<Page<ReducedItemResponse>> getItemsByCategoryAndVendor(@RequestParam("page") int page,
                                                @RequestParam("size") int size,
                                                @RequestParam("category") List<Long> categoryIds,
                                                @RequestParam("vendor") List<Long> vendorIds) {
    Pageable pageable = PageRequest.of(page, size);
    return itemService.findByCategoryInAndVendorIn(categoryIds, vendorIds, pageable);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<ItemResponse> getItemById(@PathVariable Long id) {
    return itemService.findByIdForUser(id);
  }

  @GetMapping("/batch")
  public ResponseEntity<List<ItemResponse>> getItemsByIds(@RequestParam String ids) {
    List<Long> idList = Arrays.stream(ids.split(","))
      .map(Long::valueOf)
      .collect(Collectors.toList());

    List<ItemResponse> items = itemService.getItemsByIdsForUser(idList);
    return ResponseEntity.ok(items);
  }

  @GetMapping("/uploads/{filename:.+}")
  public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
    Path path = Paths.get("./uploads", filename);
    (path.toAbsolutePath());
    Resource resource = null;
    try {
      resource = new UrlResource(path.toUri());
      if (resource.exists() || resource.isReadable()) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
          "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
  }
}
