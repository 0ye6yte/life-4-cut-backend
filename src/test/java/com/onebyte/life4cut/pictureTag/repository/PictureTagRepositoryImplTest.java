package com.onebyte.life4cut.pictureTag.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.onebyte.life4cut.common.annotation.RepositoryTest;
import com.onebyte.life4cut.fixture.PictureTagFixtureFactory;
import com.onebyte.life4cut.picture.domain.PictureTag;
import com.onebyte.life4cut.picture.domain.vo.PictureTagName;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@RepositoryTest(PictureTagRepositoryImpl.class)
class PictureTagRepositoryImplTest {

  @Autowired private PictureTagFixtureFactory pictureTagFixtureFactory;
  @Autowired private PictureTagRepositoryImpl pictureTagRepositoryImpl;

  @Nested
  class Search {

    @Test
    @DisplayName("검색어가 포함된 태그를 조회한다.")
    void searchByKeyword() {
      // given
      String keyword = "bell";
      Long albumId = 1L;

      PictureTag expectedTag =
          pictureTagFixtureFactory.save(
              (entity, builder) -> {
                builder.set("albumId", albumId);
                builder.set("name", PictureTagName.of("park bell park"));
                builder.setNull("deletedAt");
              });

      pictureTagFixtureFactory.save(
          (entity, builder) -> {
            builder.set("albumId", albumId);
            builder.set("name", PictureTagName.of("not include keyword"));
            builder.setNull("deletedAt");
          });

      // when
      var results = pictureTagRepositoryImpl.search(albumId, keyword);

      // then
      assertThat(results).hasSize(1);
      assertThat(results.get(0)).isEqualTo(expectedTag);
    }

    @Test
    @DisplayName("삭제된 태그는 조회하지 않는다")
    void notSearchDeletedTag() {
      // given
      String keyword = "bell";
      Long albumId = 1L;

      PictureTag expectedTag =
          pictureTagFixtureFactory.save(
              (entity, builder) -> {
                builder.set("albumId", albumId);
                builder.set("name", PictureTagName.of("park bell park"));
                builder.setNull("deletedAt");
              });

      pictureTagFixtureFactory.save(
          (entity, builder) -> {
            builder.set("albumId", albumId);
            builder.set("name", PictureTagName.of("park2 bell park"));
            builder.set("deletedAt", LocalDateTime.now());
          });

      // when
      var results = pictureTagRepositoryImpl.search(albumId, keyword);

      // then
      assertThat(results).hasSize(1);
      assertThat(results.get(0)).isEqualTo(expectedTag);
    }

    @Test
    @DisplayName("앨범내에 존재하는 태그만 조회한다")
    void searchInAlbum() {
      // given
      String keyword = "bell";
      Long albumId = 1L;

      PictureTag expectedTag =
          pictureTagFixtureFactory.save(
              (entity, builder) -> {
                builder.set("albumId", albumId);
                builder.set("name", PictureTagName.of("park bell park"));
                builder.setNull("deletedAt");
              });

      pictureTagFixtureFactory.save(
          (entity, builder) -> {
            builder.set("albumId", albumId + 1);
            builder.set("name", PictureTagName.of("park bell park"));
            builder.setNull("deletedAt");
          });

      // when
      var results = pictureTagRepositoryImpl.search(albumId, keyword);

      // then
      assertThat(results).hasSize(1);
      assertThat(results.get(0)).isEqualTo(expectedTag);
    }

    @Test
    @DisplayName("검색어가 없는 경우 앨범에 포함된 모든 태그를 조회한다.")
    void noKeyword() {
      // given
      Long albumId = 1L;
      String keyword = null;

      PictureTag expectedTag =
          pictureTagFixtureFactory.save(
              (entity, builder) -> {
                builder.set("albumId", albumId);
                builder.set("name", PictureTagName.of("park bell park"));
                builder.setNull("deletedAt");
              });

      pictureTagFixtureFactory.save(
          (entity, builder) -> {
            builder.set("albumId", albumId + 1);
            builder.set("name", PictureTagName.of("park bell park"));
            builder.setNull("deletedAt");
          });

      // when
      List<PictureTag> results = pictureTagRepositoryImpl.search(albumId, keyword);

      // then
      assertThat(results).hasSize(1);
      assertThat(results.get(0)).isEqualTo(expectedTag);
    }
  }
}
