package hu.vilmosdev.Webshop.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

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
  public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException{
    service.refreshToken(request, response);
  }

  @PostMapping
  public void logout(@RequestBody AuthenticationRequest request){
    //to implement
  }
}
