package hu.vilmosdev.Webshop.Item;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
public class ImageStorageService {
  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(Paths.get("uploads/"));
    } catch (IOException e) {
      throw new RuntimeException("Could not create upload directory!");
    }
  }

  public String store(String base64Image) {
      try {
        String[] image = base64Image.split(",");
        if(image.length == 1){
          return "Invalid image format";
        }

        byte[] bytes = Base64.getDecoder().decode(image[1]); // Splitting to remove "data:image/png;base64,"

        Path path = Paths.get("./uploads", UUID.randomUUID() + ".png");
        (path.toAbsolutePath());
        Files.write(path, bytes);
        return path.getFileName().toString();
      } catch (IOException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }
  }
}
