package hu.vilmosdev.Webshop.Item;

import hu.vilmosdev.Webshop.generic.SearchResult;
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
import org.springframework.web.server.ResponseStatusException;

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

  @GetMapping()
  public ResponseEntity<?> getItemsByCategoryAndManufacturer(@RequestParam("page") int page,
                                                @RequestParam("size") int size,
                                                @RequestParam(value = "category", required = false, defaultValue = "") List<Long> categoryIds,
                                                @RequestParam(value = "manufacturer", required = false, defaultValue = "") List<String> manufacturers) {
    Pageable pageable = PageRequest.of(page, size);
    return itemService.findByCategoryInAndManufacturerIn(categoryIds, manufacturers, pageable);
  }

  @GetMapping(value = "/random", params = {"page", "size"})
  public ResponseEntity<Page<ReducedItemResponse>> getItems(@RequestParam("page") int page,
                                                            @RequestParam("size") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return itemService.findRandom(pageable);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<ItemResponse> getItemById(@PathVariable Long id) {
    return itemService.findByIdForUser(id);
  }

  @GetMapping("/batch")
  public ResponseEntity<?> getItemsByIds(@RequestParam String ids) {
    List<Long> idList = Arrays.stream(ids.split(","))
      .map(Long::valueOf)
      .collect(Collectors.toList());

    SearchResult<ItemResponse> result = new SearchResult<>(itemService.getItemsByIdsForUser(idList), idList, ItemResponse::getId);

    if (result.getFoundItems().isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("None of the provided IDs were found.");
    } else if (!result.getIdsNotFound().isEmpty()) {
      return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(result.getFoundItems());
    } else {
      return ResponseEntity.ok(result.getFoundItems());
    }
  }

  @GetMapping("/uploads/{filename:.+}")
  public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
    Path path = Paths.get("./uploads", filename);
    Resource resource = null;
    try {
      resource = new UrlResource(path.toUri());
      if (resource.exists() || resource.isReadable()) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
          "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
      }else{
        throw new NotFoundException("Could not find image");
      }
    }
    catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
  }
}
