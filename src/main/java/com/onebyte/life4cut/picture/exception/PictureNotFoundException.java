package com.onebyte.life4cut.picture.exception;

import com.onebyte.life4cut.common.exception.CustomException;
import com.onebyte.life4cut.common.exception.ErrorCode;

public class PictureNotFoundException extends CustomException {

  public PictureNotFoundException() {
    super(ErrorCode.PICTURE_NOT_FOUND);
  }
}
