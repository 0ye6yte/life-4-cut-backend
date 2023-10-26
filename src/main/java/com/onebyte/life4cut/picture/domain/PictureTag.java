package com.onebyte.life4cut.picture.domain;

import com.onebyte.life4cut.common.entity.BaseEntity;
import com.onebyte.life4cut.picture.domain.vo.PictureTagName;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
    name = "picture_tag",
    indexes = {
      @Index(name = "idx_picture_tag_1", columnList = "album_id,name", unique = true),
      @Index(name = "idx_picture_tag_2", columnList = "author_id")
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class PictureTag extends BaseEntity {

  @Nonnull
  @Column(nullable = false, name = "album_id")
  private Long albumId;

  @Nonnull
  @Column(nullable = false, name = "author_id")
  private Long authorId;

  @Nonnull @Embedded private PictureTagName name;

  @Nullable
  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @Nonnull
  public static PictureTag create(
      @Nonnull final Long albumId, @Nonnull final Long authorId, @Nonnull final String name) {
    PictureTag pictureTag = new PictureTag();
    pictureTag.albumId = albumId;
    pictureTag.authorId = authorId;
    pictureTag.name = PictureTagName.of(name);
    return pictureTag;
  }

  public void restoreIfRequired() {
    if (isDeleted()) {
      restore();
    }
  }

  private boolean isDeleted() {
    return deletedAt != null;
  }

  private void restore() {
    deletedAt = null;
  }
}
