package com.onebyte.life4cut.auth.handler.oauth;

import com.onebyte.life4cut.auth.domain.RefreshToken;
import com.onebyte.life4cut.auth.dto.OAuthInfo;
import com.onebyte.life4cut.auth.handler.jwt.TokenProvider;
import com.onebyte.life4cut.auth.repository.RefreshTokenRepository;
import com.onebyte.life4cut.common.exception.CustomAuthenticationException;
import com.onebyte.life4cut.user.controller.dto.UserSignInRequest;
import com.onebyte.life4cut.user.domain.User;
import com.onebyte.life4cut.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final UserService userService;
  private final RefreshTokenRepository refreshTokenRepository;
  private final TokenProvider tokenProvider;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {

    try {
      OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
      String type = token.getAuthorizedClientRegistrationId();
      OAuthInfo oAuthInfo = OAuthInfo.createOAuthInfo(type, token);

      Optional<User> findUser = userService.findUserByOAuthInfo(oAuthInfo);
      if (findUser.isEmpty()) {
        UserSignInRequest signInUser =
            UserSignInRequest.builder()
                .nickname(oAuthInfo.getEmail())
                .email(oAuthInfo.getEmail())
                .oauthId(oAuthInfo.getOauthId())
                .oauthType(oAuthInfo.getOauthType().getType())
                .build();
        User user = userService.save(signInUser);
        setTokenCookie(request, response, authentication, user);
      } else {
        User user = findUser.get();
        setTokenCookie(request, response, authentication, user);
      }

    } catch (CustomAuthenticationException e) {
      request.setAttribute("exception", e);
    }
  }

  private void setTokenCookie(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication,
      User user)
      throws IOException, ServletException {
    String accessToken = tokenProvider.createAccessToken(authentication, user.getId());
    String refreshToken = tokenProvider.createRefreshToken(authentication, user.getId());
    refreshTokenRepository.save(new RefreshToken(refreshToken, user.getId()));

    // JWT 방식
    ResponseCookie accessTokenCookie = tokenProvider.makeTokenCookie("accessToken", accessToken);
    ResponseCookie refreshTokenCookie = tokenProvider.makeTokenCookie("refreshToken", refreshToken);
    response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
    response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

    super.onAuthenticationSuccess(request, response, authentication);
  }
}
