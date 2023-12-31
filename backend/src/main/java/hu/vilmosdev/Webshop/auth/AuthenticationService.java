package hu.vilmosdev.Webshop.auth;

import hu.vilmosdev.Webshop.config.CustomJwtException;
import hu.vilmosdev.Webshop.config.JwtService;
import hu.vilmosdev.Webshop.token.RefreshToken;
import hu.vilmosdev.Webshop.token.RefreshTokenRepository;
import hu.vilmosdev.Webshop.token.Token;
import hu.vilmosdev.Webshop.token.TokenRepository;
import hu.vilmosdev.Webshop.user.InvalidUserCredentialsException;
import hu.vilmosdev.Webshop.user.Role;
import hu.vilmosdev.Webshop.user.User;
import hu.vilmosdev.Webshop.user.UserRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final ConcurrentHashMap<String, ReentrantLock> userLocks = new ConcurrentHashMap<>();

  private final JavaMailSender mailSender;

  private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

  @Value("${angular-link}")
  private String angularLink;

  public boolean doesUserExist(String email){
    var savedUser = repository.findByEmail(email);
    return savedUser.isPresent();
  }
  @Transactional
  public void register(RegisterRequest request){
    try{
      String randomCode = generateRandomString(64);
      var user = User.builder()
        .firstname(request.getFirstname())
        .lastname(request.getLastname())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(Role.USER)
        .enabled(false)
        .verificationCode(randomCode)
        .build();
      repository.save(user);

      sendVerificationEmail(user);
    }
    catch (Exception e) {
      logger.error("Error during registration: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during registration: ", e);
    }
  }
  @Transactional
  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
      User savedUser = repository.findByEmail(request.getEmail()).orElseThrow();
      return getAuthenticationResponse(savedUser);
    }catch(AuthenticationException e){
      logger.error("Invalid user credentials: " + e.getMessage());
      e.printStackTrace();
      throw new InvalidUserCredentialsException("Invalid user credentials");
    }
    catch (Exception e) {
      logger.error("Error during authentication: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during authentication: ", e);
    }
  }

  private AuthenticationResponse getAuthenticationResponse(User savedUser) {
    var jwtAccessToken = jwtService.generateToken(savedUser);
    var jwtRefreshToken = jwtService.generateRefreshToken(savedUser);

    saveTokens(savedUser, jwtAccessToken, jwtRefreshToken);

    return AuthenticationResponse.builder()
      .accessToken(jwtAccessToken)
      .refreshToken(jwtRefreshToken)
      .firstName(savedUser.getFirstname())
      .lastName(savedUser.getLastname())
      .email(savedUser.getEmail())
      .role(savedUser.getRole())
      .userId(savedUser.getId())
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

  @Transactional
  public void saveTokens(User user, String jwtAccessToken, String jwtRefreshToken){
    try{
      Token accessToken = buildAccessToken(user, jwtAccessToken);
      RefreshToken refreshToken = buildRefreshToken(user, jwtRefreshToken);

      accessToken.setRelatedTo(refreshToken);
      refreshToken.setTokenEntity(accessToken);
      refreshTokenRepository.save(refreshToken);
    }catch (Exception e) {
      logger.error("Error during saving tokens: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during saving tokens: " + e.getMessage());
    }
  }

  // Hard kijelentkeztetésre ha minden eszközről kiszeretne jelentkezni a felhasználó vagy mi akarjuk kiléptetni mindenhonnan
  @Transactional
  public void revokeAllUserTokens(User user) {
    try{
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
    }catch (Exception e) {
      logger.error("Error during revocation of user tokens: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during revocation of user tokens: ", e);
    }
  }
  @Transactional
  public void revokeRefreshAndRelatedAccessToken(String jwtRefreshToken){
    try{
      RefreshToken refreshToken = refreshTokenRepository.findByToken(jwtRefreshToken).orElseThrow(()-> new CustomJwtException("Token not found"));
      refreshToken.setRevoked(true);
      refreshToken.setExpired(true);

      Token token = tokenRepository.findById(refreshToken.getId()).orElseThrow();
      token.setRevoked(true);
      token.setExpired(true);

      tokenRepository.save(token);
      refreshTokenRepository.save(refreshToken);
    }catch(Exception e){
      logger.error("Error during revoking refresh and access token: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during revoking refresh and access token: ", e);
    }
  }
  @Transactional
  public AuthenticationResponse refreshToken(HttpServletRequest request) throws CustomJwtException{
    String userEmail = null;
    try{
      final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
      final String refreshToken;
      if(authHeader == null || !authHeader.startsWith("Bearer ")){
        throw new CustomJwtException("Invalid or missing refresh token");
      }

      refreshToken = authHeader.substring(7);
      userEmail = jwtService.extractUsername(refreshToken);

      ReentrantLock userLock = userLocks.computeIfAbsent(userEmail, k -> new ReentrantLock());

      if(!userLock.tryLock()){
        throw new CustomJwtException("Another refresh request is already in progress");
      }

      if (userEmail != null){
        User user = this.repository.findByEmail(userEmail).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        if(jwtService.isTokenValid(refreshToken, user)){

          //Invalidálni kell a beérkező refresh tokent és a hozzá tartozó Tokent
          revokeRefreshAndRelatedAccessToken(refreshToken);

          return getAuthenticationResponse(user);
        }else{
          throw new CustomJwtException("Expired or Revoked token");
        }
      }else{
        throw new CustomJwtException("User is null");
      }
    }catch (CustomJwtException e){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    catch(Exception e){
      logger.error("Error during refreshing a token: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during revoking refresh and access token: ", e);
    }
    finally{
      if(userEmail != null){
        ReentrantLock userLock = userLocks.get(userEmail);
        if(userLock != null && userLock.isHeldByCurrentThread()){
          userLock.unlock();
        }
      }
    }
  }

  private void sendVerificationEmail(User user){
    try{
      String toAddress = user.getEmail();
      String fromAddress = "realvilmos@gmail.com";
      String senderName = "A webshop cég";
      String subject = "Kérlek hagyd jóvá a regisztrációt";
      String content = "Kedves [[name]],<br>"
        + "Kérlek nyomd meg az alábbi linket a regisztráláshoz:<br>"
        + "<h3><a href=\"[[URL]]\" target=\"_self\">Megerősítés</a></h3>"
        + "Köszönöm,<br>"
        + "Egy webshop cég.";

      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message);

      helper.setFrom(fromAddress, senderName);
      helper.setTo(toAddress);
      helper.setSubject(subject);

      content = content.replace("[[name]]", user.getFirstname() + " " + user.getLastname());
      String verifyURL = angularLink + "/verify?code=" + user.getVerificationCode();

      content = content.replace("[[URL]]", verifyURL);

      helper.setText(content, true);

      mailSender.send(message);
    }catch(Exception e){
      logger.error("Error during sending the verification email: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during sending the verification email: ", e);
    }
  }

  private String generateRandomString(int length) {
    String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    Random random = new Random();
    StringBuilder sb = new StringBuilder(length);

    for (int i = 0; i < length; i++) {
      int randomIndex = random.nextInt(CHARACTERS.length());
      char randomChar = CHARACTERS.charAt(randomIndex);
      sb.append(randomChar);
    }

    return sb.toString();
  }

  public boolean verify(String verificationCode) {
    try{
      User user = repository.findByVerificationCode(verificationCode);

      if (user == null || user.isEnabled()) {
        return false;
      } else {
        user.setVerificationCode(null);
        user.setEnabled(true);
        repository.save(user);

        return true;
      }
    }catch(Exception e){
      logger.error("Error during verification of user: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during verification of user: ", e);
    }
  }
}
