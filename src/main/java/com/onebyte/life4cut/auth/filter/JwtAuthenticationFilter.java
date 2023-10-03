package com.onebyte.life4cut.auth.filter;

import com.onebyte.life4cut.auth.domain.RefreshToken;
import com.onebyte.life4cut.auth.dto.CustomUserDetails;
import com.onebyte.life4cut.auth.handler.jwt.TokenProvider;
import com.onebyte.life4cut.auth.repository.RefreshTokenRepository;
import com.onebyte.life4cut.user.exception.RefreshTokenNotValid;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final TokenProvider tokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      String accessToken = resolveAccessToken(request);
      String requestUri = request.getRequestURI();

      if (StringUtils.hasText(accessToken) && tokenProvider.validateToken(accessToken)) {
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(),
            requestUri);
      } else {
        log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestUri);
        checkRefreshToken(request, response);
      }

    } catch (Exception e) {
      request.setAttribute("exception", e);

    }

    filterChain.doFilter(request, response);
  }

  private String resolveAccessToken(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) return null;

    for (Cookie cookie : cookies) {
      String name = cookie.getName();
      if (name.equals("accessToken")) {
        return cookie.getValue();
      }
    }
    return null;
  }

  private String resolveRefreshToken(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) return null;

    for (Cookie cookie : cookies) {
      String name = cookie.getName();
      if (name.equals("refreshToken")) {
        return cookie.getValue();
      }
    }
    return null;
  }

  private void checkRefreshToken(HttpServletRequest request, HttpServletResponse response) {
    String refreshToken = resolveRefreshToken(request);
    log.info("Check Refresh Token: {}", refreshToken);
    if (refreshToken == null || refreshToken.equals("")) {
      throw new JwtException("Refresh Token이 존재하지 않습니다.");
    }

    Authentication authentication = tokenProvider.getAuthentication(refreshToken);
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    validateRefreshToken(refreshToken, userDetails, response);

    String jwt = tokenProvider.createAccessToken(authentication, userDetails.getUserId());
    log.info("new JWT TOKEN: {}", jwt);
    response.addHeader(HttpHeaders.SET_COOKIE, jwt);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  private void validateRefreshToken(
      String refreshToken, CustomUserDetails userDetails, HttpServletResponse response) {
    log.info("Validate Refresh Token");
    Optional<RefreshToken> findRefresh = refreshTokenRepository.findById(refreshToken);
    if (findRefresh.isEmpty()) {
      throw new RefreshTokenNotValid();
    }
    long userId = findRefresh.get().getUserId();
    long tokenUserId = userDetails.getUserId();

    if (userId != tokenUserId) {
      throw new RefreshTokenNotValid();
    }
  }
}
