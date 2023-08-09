package hu.vilmosdev.Webshop.auth;
import hu.vilmosdev.Webshop.config.CustomJwtException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
    if(service.doesUserExist(request.getEmail())){
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    service.register(request);

    return ResponseEntity.ok().build();

  }

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
    return ResponseEntity.ok(service.authenticate(request));
  }

  @GetMapping("/refresh-token")
  public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request) throws CustomJwtException{
    AuthenticationResponse response = service.refreshToken(request);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new ResponseEntity<>(response, headers, HttpStatus.OK);
  }

  @GetMapping("/verify")
  public ResponseEntity<String> verifyUser(@Param("code") String code) {
    if (service.verify(code)) {
      return ResponseEntity.ok().body("{\"message\": \"verify_success\"}");
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"verify_fail\"}");
    }
  }
}
