package com.onebyte.life4cut.picture.repository.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PictureDetailResult(
    Long pictureId, String content, String path, LocalDateTime picturedAt, String rawTagNames) {

  public List<String> tagNames() {
    return List.of(rawTagNames.split(","));
  }
}
