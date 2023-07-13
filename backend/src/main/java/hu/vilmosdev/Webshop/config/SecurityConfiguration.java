package hu.vilmosdev.Webshop.config;
import hu.vilmosdev.Webshop.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
      .csrf(AbstractHttpConfigurer::disable)
      .authorizeHttpRequests((authorizeHttpRequests) ->
        authorizeHttpRequests
          .requestMatchers("/api/auth/**", "/api/items/")
          .permitAll()

          .requestMatchers("/api/admin/**").hasRole(Role.ADMIN.name())


          .anyRequest()
          .authenticated()
      )
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
}
