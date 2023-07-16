package hu.vilmosdev.Webshop.auth;

import hu.vilmosdev.Webshop.config.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;
  private final LogoutService logoutService;

  /*
  Példa a register használatára JSON-ban
  {
    "firstname": "Vilmos",
    "lastname": "Bognár",
    "email": "valami@email.asd",
    "password": "1234"
  }

  Ha sikeres a register -> 200 status code
  Ha létezik a felhasználó -> 409 conflict
   */

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){
    if(service.doesUserExist(request.getEmail())){
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    return ResponseEntity.ok(service.register(request));

  }

  /*
  Példa az authenticate használatára JSON-ban
  {
    "email": "valami@email.asd",
    "password": "1234"
  }

  Ha sikeres a register -> 200 status code
  Ha nem létezik a felhasználó/rosszak az adatok -> 403 fobidden
   */

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
    return ResponseEntity.ok(service.authenticate(request));
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException{
    return ResponseEntity.ok(service.refreshToken(request, response));
  }
}
