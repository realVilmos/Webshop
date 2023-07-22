package hu.vilmosdev.Webshop.auth;
import hu.vilmosdev.Webshop.config.CustomJwtException;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    User savedUser = repository.save(user);
    var jwtAccessToken = jwtService.generateToken(user);
    var jwtRefreshToken = jwtService.generateRefreshToken(user);

    saveTokens(user, jwtAccessToken, jwtRefreshToken);

    return AuthenticationResponse.builder()
      .accessToken(jwtAccessToken)
      .refreshToken(jwtRefreshToken)
      .build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    }catch(Exception ex){
      throw new UsernameNotFoundException(ex.getMessage());
    }

    var user = repository.findByEmail(request.getEmail()).orElseThrow();
    var jwtAccessToken = jwtService.generateToken(user);
    var jwtRefreshToken = jwtService.generateRefreshToken(user);

    saveTokens(user, jwtAccessToken, jwtRefreshToken);

    return AuthenticationResponse.builder()
      .accessToken(jwtAccessToken)
      .refreshToken(jwtRefreshToken)
      .build();
  }

  private Token buildAccessToken(User user, String jwtToken) {
    return Token.builder()
      .user(user)
      .token(jwtToken)
      .expired(false)
      .revoked(false)
      .build();
  }

  private RefreshToken buildRefreshToken(User user, String jtwRefreshToken){
    return RefreshToken.builder()
      .user(user)
      .token(jtwRefreshToken)
      .expired(false)
      .revoked(false)
      .build();
  }

  private void saveTokens(User user, String jwtAccessToken, String jwtRefreshToken){
    Token accessToken = buildAccessToken(user, jwtAccessToken);
    RefreshToken refreshToken = buildRefreshToken(user, jwtRefreshToken);
    refreshTokenRepository.save(refreshToken);
    accessToken.setRelatedTo(refreshToken);
    refreshToken.setRelatedTo(accessToken);
    tokenRepository.save(accessToken);
  }

  // Hard kijelentkeztetésre ha minden eszközről kiszeretne jelentkezni a felhasználó vagy mi akarjuk kiléptetni mindenhonnan
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

  public void revokeRefreshAndRelatedAccessToken(String jwtRefreshToken) throws CustomJwtException {
    RefreshToken refreshToken = refreshTokenRepository.findByToken(jwtRefreshToken).orElseThrow(()-> new CustomJwtException("Token not found"));
    refreshToken.setRevoked(true);
    refreshToken.setExpired(true);

    Token token = tokenRepository.findById(refreshToken.getId()).orElseThrow();
    token.setRevoked(true);
    token.setExpired(true);

    tokenRepository.save(token);
    refreshTokenRepository.save(refreshToken);

  }

  //Ez a function invalidálja a jelenlegi refresh tokent és a hozzá tartozó access tokent.
  public AuthenticationResponse refreshToken(HttpServletRequest request, HttpServletResponse response) throws CustomJwtException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if(authHeader == null || !authHeader.startsWith("Bearer ")){
      throw new CustomJwtException("Invalid or missing refresh token");
    }

    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null){
      var user = this.repository.findByEmail(userEmail).orElseThrow(()-> new UsernameNotFoundException("User not found"));
      if(jwtService.isTokenValid(refreshToken, user)){

        //Invalidálni kell a beérkező refresh tokent és a hozzá tartozó Tokent
        revokeRefreshAndRelatedAccessToken(refreshToken);

        var jwtAccessToken = jwtService.generateToken(user);
        var jwtRefreshToken = jwtService.generateRefreshToken(user);

        saveTokens(user, jwtAccessToken, jwtRefreshToken);

        return AuthenticationResponse.builder()
          .accessToken(jwtAccessToken)
          .refreshToken(jwtRefreshToken)
          .build();
      }
    }
    return null;
  }
}
