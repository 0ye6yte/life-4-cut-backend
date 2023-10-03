package com.onebyte.life4cut.album.controller.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record UpdatePictureRequest(
    @Nullable String content,
    @Nullable List<@NotBlank String> tags,
    @Nullable LocalDate picturedAt) {

  @Nullable
  public LocalDateTime getPicturedAt() {
    if (picturedAt() == null) {
      return null;
    }

    return picturedAt().atTime(0, 0);
  }

  @Nullable
  public String getContent() {
    if (content() != null && content().isBlank()) {
      throw new IllegalArgumentException("content is blank");
    }
    return content();
  }
}
