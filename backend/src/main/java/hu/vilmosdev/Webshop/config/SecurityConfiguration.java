package hu.vilmosdev.Webshop.config;
import hu.vilmosdev.Webshop.user.Role;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

  private final JwtAuthenticationFilter jwtAuthFilter;
  private final AuthenticationProvider authenticationProvider;
  private final LogoutService logoutHandler;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
      .cors(withDefaults())
      .csrf(AbstractHttpConfigurer::disable)
      .authorizeHttpRequests((authorizeHttpRequests) ->
        authorizeHttpRequests
          .requestMatchers("/api/auth/**").permitAll()
          .requestMatchers(HttpMethod.GET,"/api/items/**").permitAll()
          .requestMatchers("/api/admin/**").hasRole(Role.ADMIN.name())
          .anyRequest()
          .authenticated()
      ).exceptionHandling(exceptionHandlingConfigurer -> {
        exceptionHandlingConfigurer.authenticationEntryPoint((request, response, authException) -> {
          response.setStatus(HttpStatus.UNAUTHORIZED.value());
          response.getWriter().write("User not authenticated");
        });
        exceptionHandlingConfigurer.accessDeniedHandler((request, response, authException) -> {
          response.setStatus(HttpStatus.FORBIDDEN.value());
          response.getWriter().write("You do not have permission to access this resource");
        });
      })
      .sessionManagement((sessionManagement) ->
        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authenticationProvider(authenticationProvider)
      .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
      .logout(httpSecurityLogoutConfigurer -> {
        httpSecurityLogoutConfigurer.logoutUrl("/api/auth/logout").addLogoutHandler(logoutHandler)
          .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext());
      });
    return http.build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowCredentials(true);
    corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
    corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
      "Accept", "Authorization", "Origin, Accept", "X-Requested-With",
      "Access-Control-Request-Method", "Access-Control-Request-Headers"));
    corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization",
      "Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
    corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfiguration);
    return source;
  }
}
