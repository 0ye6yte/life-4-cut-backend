package com.onebyte.life4cut.common.web;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

  private final String message;
  private final T data;

  private ApiResponse(T data, String message) {
    this.message = message;
    this.data = data;
  }

  public static ApiResponse<EmptyResponse> OK() {
    return new ApiResponse<>(new EmptyResponse(), "OK");
  }

  public static <T> ApiResponse<T> OK(T data) {
    return new ApiResponse<>(data, "OK");
  }
}
