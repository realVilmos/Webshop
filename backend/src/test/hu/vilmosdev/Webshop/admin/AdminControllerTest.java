package hu.vilmosdev.Webshop.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hu.vilmosdev.Webshop.Item.Category.Category;
import hu.vilmosdev.Webshop.Item.Category.CategoryCreationRequest;
import hu.vilmosdev.Webshop.Item.ItemCreationRequest;
import hu.vilmosdev.Webshop.Item.ItemRepository;
import hu.vilmosdev.Webshop.Item.Vendor;
import hu.vilmosdev.Webshop.Item.VendorRepository;
import hu.vilmosdev.Webshop.auth.AuthenticationService;
import hu.vilmosdev.Webshop.config.JwtAuthenticationFilter;
import hu.vilmosdev.Webshop.config.JwtService;
import hu.vilmosdev.Webshop.token.RefreshTokenRepository;
import hu.vilmosdev.Webshop.token.TokenRepository;
import hu.vilmosdev.Webshop.user.Role;
import hu.vilmosdev.Webshop.user.User;
import hu.vilmosdev.Webshop.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class AdminControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private RestDocumentationContextProvider restDocumentation;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private TokenRepository tokenRepository;

  @Autowired
  private AuthenticationService authenticationService;

  @Autowired
  private ItemRepository itemRespository;

  @Autowired
  VendorRepository vendorRepository;
  @Autowired
  private WebApplicationContext context;

  @Autowired
  RefreshTokenRepository refreshTokenRepository;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Before
  public void setup(){
    objectMapper.registerModule(new JavaTimeModule());
    MockitoAnnotations.openMocks(this);
    this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
      .apply(documentationConfiguration(this.restDocumentation))
      .apply(springSecurity())
      .build();
  }

  public User generateUserDetails(String username, Role role){
    return User.builder()
      .id(1L)
      .enabled(true)
      .email(username)
      .role(role)
      .build();
  }
  @Test
  public void testNonAdminTryingToCreateVendor() throws Exception {
    User user = generateUserDetails("nonAdminUser", Role.USER);
    user = userRepository.save(user);
    String jwtRefresh = jwtService.generateRefreshToken(user);
    String jwtAccess = jwtService.generateToken(user);

    authenticationService.saveTokens(user, jwtAccess, jwtRefresh);

    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/admin/create-vendor")
      .header("Authorization", "Bearer " + jwtAccess)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(getVendorRequest()));

    mockMvc.perform(requestBuilder)
      .andExpect(status().isForbidden())
      .andDo(document("Admin/Non-admin user trying to create a vendor"));

  }

  @Test
  public void testUnauthenticatedAdminTryingToCreateVendor() throws Exception {
    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/admin/create-vendor")
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(getVendorRequest()));

    mockMvc.perform(requestBuilder)
      .andExpect(status().isUnauthorized())
      .andDo(document("Admin/Non-authenticated user trying to access resource"));
  }

  @Test
  public void testAdminTryingToCreateVendor() throws Exception {
    User user = generateUserDetails("AdminUser", Role.ADMIN);
    user = userRepository.save(user);
    String jwtRefresh = jwtService.generateRefreshToken(user);
    String jwtAccess = jwtService.generateToken(user);

    authenticationService.saveTokens(user, jwtAccess, jwtRefresh);

    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/admin/create-vendor")
      .header("Authorization", "Bearer " + jwtAccess)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(getVendorRequest()));

    mockMvc.perform(requestBuilder)
      .andExpect(status().isOk())
      .andDo(document("Admin/Admin trying to create a vendor",
        requestFields(
          fieldWithPath("id").description("not required, nor will be included"),
          fieldWithPath("email").description("The email address of the vendor"),
          fieldWithPath("legalName").description("legal name of vendor"),
          fieldWithPath("phone").description("Phone number of vendor"),
          fieldWithPath("website").description("Website of vendor"),
          fieldWithPath("registrationNumber").description("Registration number of vendor"),
          fieldWithPath("bankDetails").description("Any details about the bank, e.g. the name of the bank, bank number, where and how to transfer, etc.")
      )));
  }

  @Test
  public void testAdminTryingToCreateCategory() throws Exception {
    User user = generateUserDetails("AdminUser", Role.ADMIN);
    user = userRepository.save(user);
    String jwtRefresh = jwtService.generateRefreshToken(user);
    String jwtAccess = jwtService.generateToken(user);

    authenticationService.saveTokens(user, jwtAccess, jwtRefresh);

    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/admin/create-category")
      .header("Authorization", "Bearer " + jwtAccess)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(getCategoryRequest()));

    mockMvc.perform(requestBuilder)
      .andExpect(status().isOk())
      .andDo(document("Admin/Admin trying to create a category",
        requestFields(
          fieldWithPath("parentId").description("Can be left null. If no parents are set, it's a 'main category'. Otherwise it will be the child of the given parent"),
          fieldWithPath("name").description("The email address of the vendor")
        )));
  }

  @Test
  public void testAdminTryingToCreateItem() throws Exception {

    User user = generateUserDetails("AdminUser", Role.ADMIN);
    user = userRepository.save(user);
    String jwtRefresh = jwtService.generateRefreshToken(user);
    String jwtAccess = jwtService.generateToken(user);

    authenticationService.saveTokens(user, jwtAccess, jwtRefresh);

    Category category = Category.builder()
      .name("Electronics")
      .id(1L)
      .build();
    Vendor vendor = getVendorRequest();
    vendor.setId(1L);

    ItemCreationRequest item = ItemCreationRequest.builder()
      .category(category.getId())
      .description("A description")
      .originalPrice(6990L)
      .isOnSale(true)
      .salePrice(6490L)
      .saleEndDate(LocalDate.now())
      .dimensions("90cm*80cm*70cm")
      .manufacturer("The only Manufacturer ltd.")
      .name("A very nice name")
      .weight(900)
      .quantityInStock(4)
      .images(new ArrayList<>())
      .vendor(vendor.getId())
      .build();

    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/admin/create-item")
      .header("Authorization", "Bearer " + jwtAccess)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(item));

    mockMvc.perform(requestBuilder)
      .andExpect(status().isOk())
      .andDo(document("Admin/Admin trying to create an item",
        requestFields(
          fieldWithPath("category").description("The category of the item given by it's Id"),
          fieldWithPath("description").description("The description of the item"),
          fieldWithPath("originalPrice").description("Current price if the item is not on sale. If the product is on sale, it will show as old price"),
          fieldWithPath("onSale").description("Indicates if the item is on sale"),
          fieldWithPath("salePrice").description("Can be left on null if not on sale. If on sale, this is the sale price."),
          fieldWithPath("saleEndDate").description("When the sale promotion ends."),
          fieldWithPath("dimensions").description("The dimensions of the product. You can dynamically give this value as string."),
          fieldWithPath("manufacturer").description("The Manufacturer of the product."),
          fieldWithPath("name").description("The name of the product. This will be displayed everywhere"),
          fieldWithPath("weight").description("The weight of the product in grams."),
          fieldWithPath("quantityInStock").description("How many we have in storage or this product."),
          fieldWithPath("images").description("A list or images in base64. These will be saved in png and can be requested later separately."),
          fieldWithPath("vendor").description("The vendor of the item, given by it's Id")
        )));
  }

  private CategoryCreationRequest getCategoryRequest() {
    return CategoryCreationRequest.builder()
      .name("Electronics")
      .parentId(null)
      .build();
  }


  private Vendor getVendorRequest(){
    return Vendor.builder()
      .legalName("vilmosdev logistics kft.")
      .bankDetails("11111111-11111111-1111111")
      .email("example.logistrics@vilmosdev.hu")
      .phone("+123456789")
      .registrationNumber("666666666666")
      .website("vilmosdev.hu")
      .build();
  }
}
