package com.onebyte.life4cut.config;

import com.onebyte.life4cut.auth.filter.JwtAuthenticationFilter;
import com.onebyte.life4cut.auth.handler.ClientAccessDeniedHandler;
import com.onebyte.life4cut.auth.handler.ClientAuthenticationEntryPoint;
import com.onebyte.life4cut.auth.handler.jwt.TokenProvider;
import com.onebyte.life4cut.auth.handler.oauth.OAuth2LoginFailureHandler;
import com.onebyte.life4cut.auth.handler.oauth.OAuth2LoginSuccessHandler;
import com.onebyte.life4cut.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  private final OAuth2UserService oAuth2UserService;
  private final OAuth2LoginSuccessHandler oAuthLoginSuccessHandler;
  private final OAuth2LoginFailureHandler oAuthLoginFailureHandler;
  private final TokenProvider tokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;
  private final ClientAuthenticationEntryPoint clientAuthenticationEntryPoint;
  private final ClientAccessDeniedHandler clientAccessDeniedHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(
            new JwtAuthenticationFilter(tokenProvider, refreshTokenRepository),
            UsernamePasswordAuthenticationFilter.class)
        .oauth2Login(
            (oauth2) ->
                oauth2
                    // 로그인 페이지
                    //                        .loginPage("frontend-login-page")
                    .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                    .successHandler(oAuthLoginSuccessHandler)
                    .failureHandler(oAuthLoginFailureHandler))
        .exceptionHandling(
            exception ->
                exception
                    .authenticationEntryPoint(clientAuthenticationEntryPoint)
                    .accessDeniedHandler(clientAccessDeniedHandler))
        .build();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring()
        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()) // static에 있는 모든 정적 자원 허용
        .requestMatchers("/images/**", "/js/**", "/webjars/**")
        .requestMatchers(
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/docs/openapi3.yaml",
            "/docs/openapi3.json",
            "/docs/swagger",
            "/docs/swagger-ui/**");
  }
}
