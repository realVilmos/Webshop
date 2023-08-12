package hu.vilmosdev.Webshop.generic;
import lombok.Data;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class SearchResult<T> {
  private List<T> foundItems;
  private List<Long> idsNotFound;

  public SearchResult(List<T> foundItems, List<Long> allRequestedIds, Function<T, Long> idExtractor){
    this.foundItems = foundItems;

    Set<Long> foundIds = foundItems.stream()
      .map(idExtractor)
      .collect(Collectors.toSet());

    this.idsNotFound = allRequestedIds.stream().filter(id -> !foundIds.contains(id)).collect(Collectors.toList());
  }
}
