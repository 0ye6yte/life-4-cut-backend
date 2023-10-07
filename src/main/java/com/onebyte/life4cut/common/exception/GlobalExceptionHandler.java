package com.onebyte.life4cut.common.exception;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<CustomErrorResponse> handleRuntimeError(final CustomException e) {
    log.error("Custom Exception");
    return makeResponseEntity(e.getMessage(), e.getStatus());
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<CustomErrorResponse> handleAuthenticationException(
      final AuthenticationException e) {
    log.error("AuthenticationException Exception");
    return makeResponseEntity(e.getMessage(), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(JwtException.class)
  public ResponseEntity<CustomErrorResponse> handleJwtException(final JwtException e) {
    log.error("JwtException Exception");
    return makeResponseEntity(e.getMessage(), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<CustomErrorResponse> handleAccessDeniedException(
      final AccessDeniedException e) {
    log.error("AccessDeniedException Exception");
    return makeResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<CustomErrorResponse> handleInternalServerError(final RuntimeException e) {
    log.error("Uncontrolled Exception ", e);
    return makeResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ResponseEntity<CustomErrorResponse> makeResponseEntity(
      String message, HttpStatus httpStatus) {
    CustomErrorResponse response = new CustomErrorResponse(message);
    return new ResponseEntity<>(response, httpStatus);
  }
}
