package com.onebyte.life4cut.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionMessage {
  BAD_REQUEST("Bad Request"),
  NOT_SUPPORT_OAUTH_TYPE("지원하지 않는 OAuth Type 입니다."),
  USER_NOT_UNIQUE("OAuthInfo로 찾은 유저가 고유하지 않습니다."),
  REFRESH_TOKEN_NOT_VALID("Refresh Token이 유효하지 않습니다."),
  SAMPLE_NOT_FOUND("Sample Not Found"),
  USER_NOT_FOUND("User Not Found"),
  ALBUM_NOT_FOUND("해당하는 앨범이 존재하지 않습니다."),
  SLOT_NOT_FOUND("해당하는 슬롯이 존재하지 않습니다."),
  ALBUM_DOES_NOT_HAVE_SLOT("해당하는 앨범에 존재하지 않는 슬롯입니다."),
  USER_ALBUM_ROLE_PERMISSION("해당하는 앨범에 권한이 없습니다."),
  PICTURE_NOT_FOUND("해당하는 사진이 존재하지 않습니다."),

  INTERNAL_SERVER_ERROR("Internal Server Error"),
  FORBIDDEN("Forbidden"),
  ;

  private final String message;
}
