package com.onebyte.life4cut.slot.domain;

import com.onebyte.life4cut.album.domain.Album;
import com.onebyte.life4cut.common.entity.BaseEntity;
import com.onebyte.life4cut.slot.domain.vo.SlotLayout;
import com.onebyte.life4cut.slot.domain.vo.SlotLocation;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class Slot extends BaseEntity {
  @Nonnull
  @Column(nullable = false)
  private Long albumId;

  @Nullable @Column private Long pictureId;

  @Nonnull
  @Column(nullable = false)
  private Long page;

  @Nonnull
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private SlotLayout layout;

  @Nonnull
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private SlotLocation location;

  @Nullable @Column private LocalDateTime deletedAt;

  public void addPicture(Long pictureId) {
    this.pictureId = pictureId;
  }

  public boolean isIn(@Nonnull Album album) {
    return albumId.equals(album.getId());
  }
}
