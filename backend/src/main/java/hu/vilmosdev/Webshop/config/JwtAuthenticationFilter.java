package hu.vilmosdev.Webshop.config;

import hu.vilmosdev.Webshop.token.TokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final TokenRepository tokenRepository;
  private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  @Override
  protected void doFilterInternal(
    @NotNull HttpServletRequest request,
    @NotNull HttpServletResponse response,
    @NotNull FilterChain filterChain
  ) throws ServletException, IOException {
    try{
      final String authHeader = request.getHeader("Authorization");
      final String jwt;
      final String userEmail;

      if(authHeader == null || !authHeader.startsWith("Bearer ")){
        filterChain.doFilter(request, response);
        return;
      }

      jwt = authHeader.substring(7);
      userEmail = jwtService.extractUsername(jwt);

      if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){

          UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
          var isTokenValid = tokenRepository.findByToken(jwt)
            .map(t -> !t.isExpired() && !t.isRevoked()).orElse(false);

          if(jwtService.isTokenValid(jwt, userDetails) && isTokenValid){
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
          }
          filterChain.doFilter(request, response);
      }
    }catch (ExpiredJwtException e){
      logger.error(e.getMessage());
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.getWriter().write(e.getMessage());
    }
    catch (CustomJwtException | RuntimeException e) {
      e.printStackTrace();
      logger.error(e.getMessage());
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.getWriter().write(e.getMessage());
    }

  }
}
