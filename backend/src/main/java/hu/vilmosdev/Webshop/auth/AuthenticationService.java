package hu.vilmosdev.Webshop.auth;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.vilmosdev.Webshop.config.JwtService;
import hu.vilmosdev.Webshop.token.RefreshToken;
import hu.vilmosdev.Webshop.token.RefreshTokenRepository;
import hu.vilmosdev.Webshop.token.Token;
import hu.vilmosdev.Webshop.token.TokenRepository;
import hu.vilmosdev.Webshop.user.Role;
import hu.vilmosdev.Webshop.user.User;
import hu.vilmosdev.Webshop.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public boolean doesUserExist(String email){
    var savedUser = repository.findByEmail(email);
    return savedUser.isPresent();
  }

  public AuthenticationResponse register(RegisterRequest request) {
    var user = User.builder()
      .firstname(request.getFirstname())
      .lastname(request.getLastname())
      .email(request.getEmail())
      .password(passwordEncoder.encode(request.getPassword()))
      .role(Role.USER)
      .build();

    var savedUser = repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    saveAccessToken(savedUser, jwtToken);
    saveRefreshToken(savedUser, refreshToken);
    return AuthenticationResponse.builder()
      .accessToken(jwtToken)
      .refreshToken(refreshToken)
      .build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    var user = repository.findByEmail(request.getEmail()).orElseThrow();
    System.out.println(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveAccessToken(user, jwtToken);
    return AuthenticationResponse.builder()
      .accessToken(jwtToken)
      .refreshToken(refreshToken)
      .build();
  }

  private void saveAccessToken(User user, String jwtToken) {
    var token = Token.builder()
      .user(user)
      .token(jwtToken)
      .expired(false)
      .revoked(false)
      .build();
    tokenRepository.save(token);
  }

  private void saveRefreshToken(User user, String jtwRefreshToken){
    var token = RefreshToken.builder()
      .user(user)
      .token(jtwRefreshToken)
      .expired(false)
      .revoked(false)
      .build();
    refreshTokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validAccessUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    var validRefreshUserTokens = refreshTokenRepository.findAllValidTokenByUser(user.getId());
    if (validAccessUserTokens.isEmpty() && validRefreshUserTokens.isEmpty())
      return;

    validAccessUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validAccessUserTokens);

    validRefreshUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    refreshTokenRepository.saveAll(validRefreshUserTokens);
  }

  public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException{
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if(authHeader == null || !authHeader.startsWith("Bearer ")){
      return;
    }

    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null){
      var user = this.repository.findByEmail(userEmail).orElseThrow();
      if(jwtService.isTokenValid(refreshToken, user)){
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveAccessToken(user, accessToken);
        saveRefreshToken(user, refreshToken);
        var authResponse = AuthenticationResponse.builder()
          .accessToken(accessToken)
          .refreshToken(refreshToken)
          .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }



}
