package com.onebyte.life4cut.album.controller.dto;

import com.onebyte.life4cut.picture.repository.dto.PictureDetailResult;
import com.onebyte.life4cut.picture.service.dto.PictureDetailInSlot;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record GetPicturesInSlotResponse(List<List<PictureInSlot>> pictures) {

  public static GetPicturesInSlotResponse of(List<PictureDetailInSlot> picturesBySlot) {
    if (picturesBySlot.isEmpty()) {
      return new GetPicturesInSlotResponse(new ArrayList<>());
    }

    Long lastPage = picturesBySlot.get(0).page();

    List<List<PictureInSlot>> pictures = new ArrayList<>();
    List<PictureInSlot> currentPictureInSlots = new ArrayList<>();
    for (PictureDetailInSlot pictureDetailInSlot : picturesBySlot) {
      if (!lastPage.equals(pictureDetailInSlot.page())) {
        pictures.add(currentPictureInSlots);
        currentPictureInSlots = new ArrayList<>();
        lastPage = pictureDetailInSlot.page();
      }

      currentPictureInSlots.add(PictureInSlot.of(pictureDetailInSlot));
    }

    pictures.add(currentPictureInSlots);

    return new GetPicturesInSlotResponse(pictures);
  }

  record PictureInSlot(
      @Nullable Long pictureId,
      @Nullable String path,
      @Nullable String content,
      @Nonnull String layout,
      @Nonnull String location,
      @Nullable LocalDateTime picturedAt,
      @Nonnull List<String> tagNames) {

    private static PictureInSlot of(PictureDetailInSlot pictureDetailInSlot) {
      if (pictureDetailInSlot.picture().isEmpty()) {
        return new PictureInSlot(
            null,
            null,
            null,
            pictureDetailInSlot.slotLayout().name(),
            pictureDetailInSlot.slotLocation().name(),
            null,
            Collections.emptyList());
      }

      PictureDetailResult pictureDetailResult = pictureDetailInSlot.picture().get();
      return new PictureInSlot(
          pictureDetailResult.pictureId(),
          pictureDetailResult.path(),
          pictureDetailResult.content(),
          pictureDetailInSlot.slotLayout().name(),
          pictureDetailInSlot.slotLocation().name(),
          pictureDetailResult.picturedAt(),
          pictureDetailResult.tagNames());
    }
  }
}
