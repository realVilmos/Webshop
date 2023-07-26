package hu.vilmosdev.Webshop.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.vilmosdev.Webshop.user.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
  @JsonProperty("email")
  private String email;
  @JsonProperty("first_name")
  private String firstName;
  @JsonProperty("last_name")
  private String lastName;
  @Enumerated(EnumType.STRING)
  @JsonProperty("role")
  private Role role;


  @JsonProperty("access_token")
  private String accessToken;
  @JsonProperty("refresh_token")
  private String refreshToken;
}
