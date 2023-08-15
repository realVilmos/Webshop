package hu.vilmosdev.Webshop.payment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.vilmosdev.Webshop.Item.ItemRepository;
import hu.vilmosdev.Webshop.Orders.ChargeRequest;
import hu.vilmosdev.Webshop.Orders.PaymentIntentResponse;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class PaymentControllerTest {
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

  @Before
  public void setUp(){
    MockitoAnnotations.openMocks(this);
    this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
      .apply(documentationConfiguration(this.restDocumentation))
      .apply(springSecurity())
      .build();
  }

  @Test
  public void FullTestGettingPaymentIntent() throws Exception {
    String jwtAccess = createUserDetails();
    MvcResult result = mockMvc.perform(createCharge(jwtAccess))
      .andExpect(status().isOk())
      .andDo(document("Payment/creating-payment-intent"))
      .andReturn();

    String responseContent = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();
    PaymentIntentResponse paymentIntent = mapper.readValue(responseContent, PaymentIntentResponse.class);

    String id = paymentIntent.getId();

    mockMvc.perform(
      MockMvcRequestBuilders.get("/api/payment/get-payment-reference?intentId=" + id)
        .header("Authorization", "Bearer " + jwtAccess))
        .andExpect(status().isOk())
        .andDo(document("Payment/getting-payment-reference")
      );
  }

  public User generateUserDetails(String username, Role role){
    return User.builder()
      .enabled(true)
      .email(username)
      .role(role)
      .build();
  }

  private String createUserDetails(){
    User user = generateUserDetails("nonAdminUser", Role.USER);
    user = userRepository.save(user);
    String jwtRefresh = jwtService.generateRefreshToken(user);
    String jwtAccess = jwtService.generateToken(user);
    authenticationService.saveTokens(user, jwtAccess, jwtRefresh);
    return jwtAccess;
  }

  private MockHttpServletRequestBuilder createCharge(String jwtAccess) throws JsonProcessingException {
    ChargeRequest chargeRequest = ChargeRequest.builder()
      .billingAddressId(1L)
      .shippingAddressId(1L)
      .itemQuantities(new HashMap<>(){{
        put(1L, 2);
      }})
      .build();

    System.out.println(objectMapper.writeValueAsString(chargeRequest));

    return MockMvcRequestBuilders.post("/api/payment/create-payment-intent")
      .header("Authorization", "Bearer " + jwtAccess)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(chargeRequest));
  }
}
