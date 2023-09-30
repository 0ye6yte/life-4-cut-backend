package com.onebyte.life4cut.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

@Getter
public class CustomAuthenticationException extends AuthenticationException {

  private final HttpStatus status = HttpStatus.UNAUTHORIZED;

  public CustomAuthenticationException(ErrorCode errorCode) {
    super(errorCode.getMessage());
  }
}

