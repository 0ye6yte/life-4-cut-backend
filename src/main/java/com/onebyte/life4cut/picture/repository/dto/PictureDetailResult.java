package com.onebyte.life4cut.picture.repository.dto;

import jakarta.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.List;

public record PictureDetailResult(
    @Nonnull Long pictureId,
    @Nonnull String content,
    @Nonnull String path,
    @Nonnull LocalDateTime picturedAt,
    @Nonnull String rawTagNames) {

  @Nonnull
  public List<String> tagNames() {
    return List.of(rawTagNames.split(","));
  }
}
