package hu.vilmosdev.Webshop.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.vilmosdev.Webshop.config.CustomJwtException;
import hu.vilmosdev.Webshop.user.InvalidUserCredentialsException;
import hu.vilmosdev.Webshop.user.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class AuthenticationControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private RestDocumentationContextProvider restDocumentation;

  @MockBean
  private AuthenticationService service;

  @Before
  public void setUp(){
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
      .apply(documentationConfiguration(this.restDocumentation))
      .build();
  }

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testRegisterUserAlreadyExists() throws Exception {
    RegisterRequest request = new RegisterRequest("John", "Doe", "john.doe@example.com", "password123");
    when(service.doesUserExist(request.getEmail())).thenReturn(true);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isConflict())
      .andDo(document("Registration when a user already exists with the same email"));
  }

  @Test
  public void testRegisterSuccess() throws Exception {
    RegisterRequest request = new RegisterRequest("John", "Doe", "john.doe@example.com", "password123");
    when(service.doesUserExist(request.getEmail())).thenReturn(false);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andDo(document("Successful user registration"));
  }

  @Test
  public void shouldAuthenticateSuccessfully() throws Exception {
    AuthenticationRequest request = new AuthenticationRequest("test@email.com", "password");

    // Mock the service behavior
    when(service.authenticate(request)).thenReturn(new AuthenticationResponse(1L, "john.doe@example.com", "John", "Doe", Role.USER, "an_access_token", "a_refresh_token"));

    mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/authenticate")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(request)))
      .andExpect(status().isOk())
      .andDo(document("Successful authentication"));
  }

  @Test
  public void shouldFailAuthenticationWithInvalidCredentials() throws Exception {
    AuthenticationRequest request = new AuthenticationRequest("test@email.com", "wrongpassword");

    // Mock the service behavior
    when(service.authenticate(request)).thenThrow(new InvalidUserCredentialsException("Invalid user credentials"));

    mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/authenticate")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(request)))
      .andExpect(status().isUnauthorized())
      .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidUserCredentialsException))
      .andExpect(result -> assertEquals("Invalid user credentials", Objects.requireNonNull(result.getResolvedException()).getMessage()))
      .andDo(document("Failed authentication"));
  }

  @Test
  public void refreshTokenSuccess() throws Exception {
    AuthenticationResponse response = new AuthenticationResponse(1L, "john.doe@example.com", "John", "Doe", Role.USER, "<a_new_access_token>", "<a_new_refresh_token>");

    when(service.refreshToken(any(HttpServletRequest.class))).thenReturn(response);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/refresh-token")
        .header("Authorization", "Bearer <old_refresh_token>")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andDo(print())
      .andDo(document("Successful token refresh"));
  }

  @Test
  public void refreshToken_UserNotFound() throws Exception {
    when(service.refreshToken(any(HttpServletRequest.class))).thenThrow(new UsernameNotFoundException("User not found"));

    mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/refresh-token")
        .header("Authorization", "Bearer someToken"))
      .andExpect(status().isUnauthorized())
      .andExpect(result -> assertTrue(result.getResolvedException() instanceof UsernameNotFoundException))
      .andExpect(result -> assertEquals("User not found", Objects.requireNonNull(result.getResolvedException()).getMessage()))
      .andDo(document("refreshToken_UserNotFound"));
  }

  @Test
  public void refreshToken_anotherRefreshInProgress() throws Exception {
    when(service.refreshToken(any(HttpServletRequest.class))).thenThrow(new CustomJwtException("Another refresh request is already in progress"));

    mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/refresh-token")
        .header("Authorization", "Bearer someToken"))
      .andExpect(status().isUnauthorized())
      .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomJwtException))
      .andExpect(result -> assertEquals("Another refresh request is already in progress", Objects.requireNonNull(result.getResolvedException()).getMessage()))
      .andDo(document("refreshToken_anotherRefreshInProgress"));
  }

  @Test
  public void refreshToken_invalidOrMissingToken() throws Exception {
    when(service.refreshToken(any(HttpServletRequest.class))).thenThrow(new CustomJwtException("Invalid or missing refresh token"));

    mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/refresh-token")
        .header("Authorization", "Bearer someToken"))
      .andExpect(status().isUnauthorized())
      .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomJwtException))
      .andExpect(result -> assertEquals("Invalid or missing refresh token", Objects.requireNonNull(result.getResolvedException()).getMessage()))
      .andDo(document("refreshToken_invalidOrMissingToken"));
  }

  @Test
  public void VerifyUserSuccess() throws Exception {
    String code = "someCode";
    when(service.verify(code)).thenReturn(true);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/verify")
        .param("code", code))
      .andExpect(status().isOk())
      .andExpect(MockMvcResultMatchers.content().string("{\"message\": \"verify_success\"}"))
      .andDo(document("Successful verification"));
  }

  @Test
  public void testVerifyUserFailure() throws Exception {
    String code = "invalidCode";
    when(service.verify(code)).thenReturn(false);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/verify")
        .param("code", code))
      .andExpect(status().isBadRequest())
      .andExpect(MockMvcResultMatchers.content().string("{\"message\": \"verify_fail\"}"))
      .andDo(document("Failed verification"));
  }
}
