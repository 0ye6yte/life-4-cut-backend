package com.onebyte.life4cut.picture.domain;

import com.onebyte.life4cut.common.entity.BaseEntity;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    indexes = {
      @Index(name = "idx_picture_1", columnList = "userId"),
      @Index(name = "idx_picture_2", columnList = "albumId")
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class Picture extends BaseEntity {

  @Nonnull
  @Column(nullable = false)
  private Long userId;

  @Nonnull
  @Column(nullable = false)
  private Long albumId;

  @Nonnull
  @Column(nullable = false, length = 500)
  private String path;

  @Nonnull
  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Nonnull
  @Column(nullable = false, name = "pictured_at")
  private LocalDateTime picturedAt;

  @Nullable
  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @Nonnull
  public static Picture create(
      @Nonnull Long userId,
      @Nonnull Long albumId,
      @Nonnull String path,
      @Nonnull String content,
      @Nonnull LocalDateTime picturedAt) {
    Picture picture = new Picture();
    picture.userId = userId;
    picture.albumId = albumId;
    picture.path = path;
    picture.content = content.trim();
    picture.picturedAt = picturedAt;
    return picture;
  }

  public boolean isIn(@Nonnull Long albumId) {
    return this.albumId.equals(albumId);
  }

  public void updateIfRequired(
      @Nullable String content, @Nullable LocalDateTime picturedAt, @Nullable String path) {
    if (content != null) {
      this.content = content.trim();
    }
    if (picturedAt != null) {
      this.picturedAt = picturedAt;
    }
    if (path != null) {
      this.path = path;
    }
  }
}
