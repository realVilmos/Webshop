package hu.vilmosdev.Webshop;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.vilmosdev.Webshop.Item.*;
import hu.vilmosdev.Webshop.Item.Category.Category;
import hu.vilmosdev.Webshop.Item.Category.CategoryRepository;
import hu.vilmosdev.Webshop.Item.Reviews.ItemReview;
import hu.vilmosdev.Webshop.user.Role;
import hu.vilmosdev.Webshop.user.User;
import hu.vilmosdev.Webshop.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class WebshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebshopApplication.class, args);
	}

  @Bean
  CommandLineRunner commandLineRunner(CategoryRepository categoryRepository, VendorRepository vendorRepository, ItemRepository itemRepository, UserRepository userRepository, PasswordEncoder passwordEncoder){
    return args -> {
      ObjectMapper objectMapper = new ObjectMapper();
      InputStream is = new FileInputStream("categories_json.txt");
      List<Map<String, Object>> dataList = objectMapper.readValue(is, new TypeReference<List<Map<String, Object>>>(){});

      Map<Long, Category> tempCategories = new HashMap<>();

      for (Map<String, Object> data : dataList) {
        Long id = Long.parseLong(data.get("id").toString());
        String name = (String) data.get("name");
        Long parentId = data.get("parent_id") == null ? null : Long.parseLong(data.get("parent_id").toString());

        Category category = Category.builder().id(id).name(name).build();

        if (parentId != null) {
          Category parent = tempCategories.get(parentId);
          category.setParent(parent);
        }

        tempCategories.put(id, category);
      }
      categoryRepository.saveAll(tempCategories.values());

      Vendor vendor = Vendor.builder()
        .bankDetails("11111111-111111111-111111111")
        .email("logistics@vilmosdev.hu")
        .legalName("Vilmosdev Logistics")
        .phone("+12345678910")
        .registrationNumber("69420")
        .build();

      vendor = vendorRepository.save(vendor);

      User user = User.builder()
        .email("Sample@User.com")
        .enabled(true)
        .role(Role.ADMIN)
        .firstname("Sample")
        .lastname("User")
        .password(passwordEncoder.encode("32w987ue"))
        .build();

      user = userRepository.save(user);

      Item item = Item.builder()
        .category(categoryRepository.getReferenceById(3L))
        .name("Samsung Galaxy Note 7")
        .description("The phone that is LIT!")
        .dimensions("153.5 x 73.9 x 7.9 mm")
        .weight(169)
        .quantityInStock(2)
        .vendor(vendorRepository.getReferenceById(1L))
        .manufacturer("Samsung")
        .reviews(
          List.of(
            ItemReview.builder()
              .reviewDate(LocalDate.now())
              .comment("The best bomb ever created")
              .rating(5)
              .user(user)
              .build()
          )
        )
        .images(List.of(new ItemImage("atomic-bomb.png")))
        .itemPrices(
          List.of(
            ItemPrice.builder()
              .originalPrice(299990L)
              .isOnSale(false)
              .build()
          )
        )
        .vendor(vendor)
        .build();
      itemRepository.save(item);

      Item item2 = Item.builder()
        .category(categoryRepository.getReferenceById(36L))
        .name("Breaking bad the game")
        .description("I am the one who knocks")
        .dimensions("10 x 11 x 12 mm")
        .weight(69)
        .quantityInStock(40)
        .vendor(vendorRepository.getReferenceById(1L))
        .manufacturer("Baking bread kft.")
        .reviews(new ArrayList<>())
        .images(List.of(new ItemImage("hank.png"), new ItemImage("saul.png")))
        .itemPrices(
          List.of(
            ItemPrice.builder()
              .originalPrice(6990L)
              .isOnSale(false)
              .build()
          )
        )
        .vendor(vendor)
        .build();
      itemRepository.save(item2);

    };
  }

}
