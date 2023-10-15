package com.onebyte.life4cut.picture.service.dto;

import com.onebyte.life4cut.picture.repository.dto.PictureDetailResult;
import com.onebyte.life4cut.slot.domain.vo.SlotLayout;
import com.onebyte.life4cut.slot.domain.vo.SlotLocation;
import jakarta.annotation.Nonnull;
import java.util.Optional;

public record PictureDetailInSlot(
    @Nonnull Long slotId,
    @Nonnull Long page,
    @Nonnull SlotLayout slotLayout,
    @Nonnull SlotLocation slotLocation,
    @Nonnull Optional<PictureDetailResult> picture) {}
