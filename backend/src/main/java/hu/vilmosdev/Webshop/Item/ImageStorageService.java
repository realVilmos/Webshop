package hu.vilmosdev.Webshop.Item;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class ImageStorageService {
  private final String UPLOAD_DIR = "uploads/";
  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(Paths.get(UPLOAD_DIR));
    } catch (IOException e) {
      throw new RuntimeException("Could not create upload directory!");
    }
  }

  public String store(String base64Images) {
      try {
        byte[] bytes = Base64.getDecoder().decode(base64Images.split(",")[1]); // Splitting to remove "data:image/png;base64,"

        Path path = Paths.get("C:", "Users\\kecsk\\OneDrive\\Documents\\Web4CV\\Webshop\\backend", "uploads", UUID.randomUUID().toString() + ".png");
        System.out.println(path.toAbsolutePath());
        Files.write(path, bytes);
        return path.getFileName().toString();
      } catch (IOException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }
  }
}
