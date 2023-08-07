package hu.vilmosdev.Webshop.config;

import hu.vilmosdev.Webshop.token.RefreshToken;
import hu.vilmosdev.Webshop.token.RefreshTokenRepository;
import hu.vilmosdev.Webshop.token.Token;
import hu.vilmosdev.Webshop.token.TokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

  enum TokenType {
    ACCESS_TOKEN,
    REFRESH_TOKEN
  }

  private final RefreshTokenRepository refreshTokenRepo;
  private final TokenRepository tokenRepositoryrepo;

  @Value("${secret_key}")
  private String SECRET_KEY;
  private static final long jwtExpiration = 86400000; //Egy nap
  //Ez lehet nagyon nagy, mivel egyszer használatos
  private static final long refreshExpiration = 31536000000L; //Egy év

  public String extractUsername(String token) throws CustomJwtException {
    return extractClaim(token, Claims::getSubject);
  }

  public String extractTokenType(String token) throws CustomJwtException {
    return extractClaim(token, claims -> claims.get("token_type", String.class));
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws CustomJwtException {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public String generateToken(UserDetails userDetails){
    Map<String, Object> claims = new HashMap<>();
    claims.put("token_type", TokenType.ACCESS_TOKEN);
    return generateToken(claims, userDetails);
  }

  public String generateToken(
    Map<String, Object> extraClaims,
    UserDetails userDetails
  ){
    return buildToken(extraClaims, userDetails, jwtExpiration);
  }

  public String generateRefreshToken(UserDetails userDetails){
    Map<String, Object> claims = new HashMap<>();
    claims.put("token_type", TokenType.REFRESH_TOKEN);
    return buildToken(claims, userDetails, refreshExpiration);
  }

  public String buildToken(
    Map<String, Object> extraClaims,
    UserDetails userDetails,
    long expiration
  ){
    return Jwts
      .builder()
      .setClaims(extraClaims)
      .setSubject(userDetails.getUsername())
      .setIssuedAt(new Date(System.currentTimeMillis()))
      .setExpiration(new Date(System.currentTimeMillis() + expiration))
      .signWith(getSignInKey(), SignatureAlgorithm.HS512)
      .compact();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) throws CustomJwtException {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenRevoked(token) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) throws CustomJwtException {
    if(!extractExpiration(token).before(new Date())){
      return false;
    }else{
      String tokenType = extractTokenType(token);
      if("ACCESS_TOKEN".equals(tokenType)){
        Token accessToken = tokenRepositoryrepo.findByToken(token).get();
        accessToken.setExpired(true);
        tokenRepositoryrepo.save(accessToken);
      }else if("REFRESH_TOKEN".equals(tokenType)){
        RefreshToken refreshToken = refreshTokenRepo.findByToken(token).get();
        refreshToken.setExpired(true);
        refreshTokenRepo.save(refreshToken);
      }
      throw new CustomJwtException("Expired token");
    }
  }

  private boolean isTokenRevoked(String token) throws CustomJwtException {
    String tokenType = extractTokenType(token);
    if("ACCESS_TOKEN".equals(tokenType)){
      return tokenRepositoryrepo.findByToken(token).orElseThrow().isRevoked();
    } else if ("REFRESH_TOKEN".equals(tokenType)) {
      return refreshTokenRepo.findByToken(token).orElseThrow().isRevoked();
    } else{
      return true;
    }
  }

  private Date extractExpiration(String token) throws CustomJwtException {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) throws CustomJwtException {
    try{
      return Jwts
        .parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    }catch(MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex){
      throw new CustomJwtException("Invalid JWT token");
    }

  }
  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
