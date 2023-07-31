package hu.vilmosdev.Webshop.config;
import hu.vilmosdev.Webshop.token.RefreshToken;
import hu.vilmosdev.Webshop.token.RefreshTokenRepository;
import hu.vilmosdev.Webshop.token.Token;
import hu.vilmosdev.Webshop.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  private final TokenRepository tokenRepository;
  private final RefreshTokenRepository refreshTokenRepository;

  private static final Logger logger = LoggerFactory.getLogger(LogoutService.class);

  @Override
  public void logout(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication
    ){
    try{
      final String authHeader = request.getHeader("Authorization");
      final String jwt;

      if(authHeader == null || !authHeader.startsWith("Bearer ")){
        return;
      }

      jwt = authHeader.substring(7);

      Token accessToken = tokenRepository.findByToken(jwt).orElseThrow();
      accessToken.setExpired(true);
      accessToken.setRevoked(true);
      tokenRepository.save(accessToken);

      RefreshToken refreshToken = refreshTokenRepository.findById(accessToken.getId()).orElseThrow();
      refreshToken.setExpired(true);
      refreshToken.setRevoked(true);
      refreshTokenRepository.save(refreshToken);

      SecurityContextHolder.clearContext();
    }catch (Exception e) {
      logger.error("Error during logging out: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during logging out: ", e);
    }


  }
}
