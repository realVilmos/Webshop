package hu.vilmosdev.Webshop.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.vilmosdev.Webshop.Item.Item;
import hu.vilmosdev.Webshop.Item.ItemRepository;
import hu.vilmosdev.Webshop.Item.Reviews.ItemReview;
import hu.vilmosdev.Webshop.auth.AuthenticationService;
import hu.vilmosdev.Webshop.config.JwtService;
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

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class ItemReviewControllerTest {

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private RestDocumentationContextProvider restDocumentation;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private JwtService jwtService;

  @Autowired
  private ItemRepository itemRespository;

  @Autowired
  private AuthenticationService authenticationService;

  @Autowired
  private UserRepository userRepository;

  @Before
  public void setUp(){
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
  public void testWritingReview() throws Exception {
    User user = generateUserDetails("nonAdminUser", Role.USER);
    user = userRepository.save(user);
    String jwtRefresh = jwtService.generateRefreshToken(user);
    String jwtAccess = jwtService.generateToken(user);
    authenticationService.saveTokens(user, jwtAccess, jwtRefresh);

    ItemReview itemReview = ItemReview.builder()
      .comment("I liked the boom")
      .rating(5)
      .build();

    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/items/1/reviews")
      .header("Authorization", "Bearer " + jwtAccess)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(itemReview));

    mockMvc.perform(requestBuilder)
      .andExpect(status().isOk())
      .andDo(document("Review/User_writing_review"));
  }

  @Test
  public void testWritingReviewWithoutAuthorization() throws Exception {
    ItemReview itemReview = ItemReview.builder()
      .comment("I liked the boom")
      .rating(5)
      .build();

    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/items/1/reviews")
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(itemReview));

    mockMvc.perform(requestBuilder)
      .andExpect(status().isUnauthorized())
      .andDo(document("Review/testWritingReviewWithoutAuthorization"));
  }

  @Test
  public void testGetReviewsOfItemPagable() throws Exception {
    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/items/1/reviews?page=0&size=6");

    mockMvc.perform(requestBuilder)
      .andExpect(status().isOk())
      .andDo(document("Review/getting_reviewsOfItem_Pageable"));

  }
}
