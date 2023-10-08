package com.onebyte.life4cut.auth.exception;

import com.onebyte.life4cut.common.exception.CustomAuthenticationException;
import com.onebyte.life4cut.common.exception.ErrorCode;

public class NotSupportOAuthType extends CustomAuthenticationException {

  public NotSupportOAuthType() {
    super(ErrorCode.NOT_SUPPORT_OAUTH_TYPE);
  }
}
