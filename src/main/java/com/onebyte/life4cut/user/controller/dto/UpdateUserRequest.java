package com.onebyte.life4cut.user.controller.dto;

public record UpdateUserRequest(String nickname) {

  public UpdateUserRequest(String nickname) {
    this.nickname = nickname;
  }

  public String nickname() {
    return this.nickname;
  }
}
