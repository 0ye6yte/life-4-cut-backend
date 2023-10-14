package com.onebyte.life4cut.slot.exception;

import com.onebyte.life4cut.common.exception.CustomException;
import com.onebyte.life4cut.common.exception.ErrorCode;

public class SlotNotFoundException extends CustomException {

  public SlotNotFoundException() {
    super(ErrorCode.SLOT_NOT_FOUND);
  }

  public SlotNotFoundException(ErrorCode errorCode) {
    super(errorCode);
  }
}
