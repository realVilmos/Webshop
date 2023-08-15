package hu.vilmosdev.Webshop.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.vilmosdev.Webshop.auth.AuthenticationService;
import hu.vilmosdev.Webshop.config.JwtService;
import org.junit.Before;
import org.junit.BeforeClass;
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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class UserControllerTest {
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
  private AuthenticationService authenticationService;

  @Autowired
  private UserRepository userRepository;

  private static String testJwtToken;
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
      .enabled(true)
      .email(username)
      .firstname("John")
      .lastname("Doe")
      .role(role)
      .build();
  }

  private String createUserDetails(){
    User user = generateUserDetails("nonAdminUser", Role.USER);
    user = userRepository.save(user);
    String jwtRefresh = jwtService.generateRefreshToken(user);
    String jwtAccess = jwtService.generateToken(user);
    testJwtToken = jwtAccess;
    authenticationService.saveTokens(user, jwtAccess, jwtRefresh);
    return jwtAccess;
  }

  @Test
  public void testPostingShippingAddress() throws Exception{
    BillingAddressRequest billingAddressRequest = BillingAddressRequest.builder()
      .city("Pécs")
      .county("Baranya")
      .phoneNumber("+12345678910")
      .street("Napfény utca 21.")
      .postalCode(6789)
      .build();

    System.out.println(testJwtToken);

    mockMvc.perform(
      MockMvcRequestBuilders
        .post("/api/user/add-shipping-address")
        .header("Authorization", "Bearer " + testJwtToken)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(billingAddressRequest))
      )
      .andExpect(status().isOk())
      .andDo(document("User/posting-shipping-address"));
  }
  @Test
  public void testPostingBillingAddress() throws Exception{
    createUserDetails();

    BillingAddressRequest billingAddressRequest = BillingAddressRequest.builder()
      .city("Pécs")
      .companyName("Yes")
      .county("Baranya")
      .phoneNumber("+12345678910")
      .street("Napfény utca 21.")
      .postalCode(6789)
      .taxNumber("1234512345")
      .build();

    mockMvc.perform(
        MockMvcRequestBuilders
          .post("/api/user/add-billing-address")
          .header("Authorization", "Bearer " + testJwtToken)
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .content(objectMapper.writeValueAsString(billingAddressRequest))
      )
      .andExpect(status().isOk())
      .andDo(document("User/posting-billing-address"));
  }
  @Test
  public void testGettingShippingAddresses() throws Exception{
    mockMvc.perform(
        MockMvcRequestBuilders
          .get("/api/user/shipping-addresses")
          .header("Authorization", "Bearer " + testJwtToken)
      )
      .andExpect(status().isOk())
      .andDo(document("User/getting-shipping-address"));
  }
  @Test
  public void testGettingBillingAddresses() throws Exception{
    mockMvc.perform(
        MockMvcRequestBuilders
          .get("/api/user/billing-addresses")
          .header("Authorization", "Bearer " + testJwtToken)
      )
      .andExpect(status().isOk())
      .andDo(document("User/getting-billing-address"));
  }
  @Test
  public void testGettingUserDetails() throws Exception{
    mockMvc.perform(
        MockMvcRequestBuilders
          .get("/api/user/get-details")
          .header("Authorization", "Bearer " + testJwtToken)
      )
      .andExpect(status().isOk())
      .andDo(document("User/getting-userDetails"));
  }
}
