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
@Getter
@Table(
    name = "picture_tag_relation",
    indexes = {
      @Index(name = "idx_picture_tag_relation_1", columnList = "picture_id,tag_id", unique = true),
      @Index(name = "idx_picture_tag_relation_2", columnList = "album_id"),
      @Index(name = "idx_picture_tag_relation_3", columnList = "tag_id")
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class PictureTagRelation extends BaseEntity {
  @Nonnull
  @Column(nullable = false, name = "picture_id")
  private Long pictureId;

  @Nonnull
  @Column(nullable = false, name = "album_id")
  private Long albumId;

  @Nonnull
  @Column(nullable = false, name = "tag_id")
  private Long tagId;

  @Nullable
  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @Nonnull
  public static PictureTagRelation create(
      @Nonnull Long pictureId, @Nonnull Long albumId, @Nonnull Long tagId) {
    PictureTagRelation pictureTagRelation = new PictureTagRelation();
    pictureTagRelation.pictureId = pictureId;
    pictureTagRelation.albumId = albumId;
    pictureTagRelation.tagId = tagId;
    return pictureTagRelation;
  }

  public void restoreIfRequired() {
    if (isDeleted()) {
      restore();
    }
  }

  public void delete(@Nonnull LocalDateTime deletedAt) {
    if (isDeleted()) {
      return;
    }

    this.deletedAt = deletedAt;
  }

  private boolean isDeleted() {
    return deletedAt != null;
  }

  private void restore() {
    deletedAt = null;
  }
}
