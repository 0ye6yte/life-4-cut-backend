package com.onebyte.life4cut.picture.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.onebyte.life4cut.common.annotation.RepositoryTest;
import com.onebyte.life4cut.fixture.PictureFixtureFactory;
import com.onebyte.life4cut.fixture.PictureTagFixtureFactory;
import com.onebyte.life4cut.fixture.PictureTagRelationFixtureFactory;
import com.onebyte.life4cut.picture.domain.Picture;
import com.onebyte.life4cut.picture.domain.PictureTag;
import com.onebyte.life4cut.picture.domain.vo.PictureTagName;
import com.onebyte.life4cut.picture.repository.dto.PictureDetailResult;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@RepositoryTest(PictureRepositoryImpl.class)
class PictureRepositoryImplTest {
  @Autowired private PictureRepositoryImpl pictureRepositoryImpl;
  @Autowired private PictureFixtureFactory pictureFixtureFactory;
  @Autowired private PictureTagFixtureFactory pictureTagFixtureFactory;
  @Autowired private PictureTagRelationFixtureFactory pictureTagRelationFixtureFactory;

  @Nested
  class FindById {

    @Test
    @DisplayName("아이디에 해당하는 사진을 조회한다")
    void findById() {
      // given
      Picture picture =
          pictureFixtureFactory.save(
              (entity, builder) -> {
                builder.setNull("deletedAt");
              });

      // when
      Picture actual = pictureRepositoryImpl.findById(picture.getId()).get();

      // then
      assertThat(actual.getId()).isEqualTo(picture.getId());
    }

    @Test
    @DisplayName("아이디에 해당하는 사진이 없으면 빈 Optional을 반환한다")
    void empty() {
      // given

      // when
      Optional<Picture> actual = pictureRepositoryImpl.findById(1L);

      // then
      assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("삭제된 사진은 조회되지 않는다")
    void deletedPicture() {
      // given
      Picture picture =
          pictureFixtureFactory.save(
              (entity, builder) -> {
                builder.set("deletedAt", LocalDateTime.now());
              });

      // when
      Optional<Picture> actual = pictureRepositoryImpl.findById(picture.getId());

      // then
      assertThat(actual).isEmpty();
    }
  }

  @Nested
  class FindDetailByIds {

    @Test
    @DisplayName("아이디에 해당하는 사진을 조회한다")
    void findDetailByIds() {
      // given
      String content = "사진 내용";
      String path = "/result/1/2/3";
      LocalDateTime picturedAt = LocalDateTime.of(2023, 10, 14, 0, 0, 0);
      Long albumId = 1L;

      Picture picture =
          pictureFixtureFactory.save(
              (entity, builder) -> {
                builder.set("albumId", albumId);
                builder.set("content", content);
                builder.set("path", path);
                builder.set("picturedAt", picturedAt);
                builder.setNull("deletedAt");
              });

      PictureTag tag1 =
          pictureTagFixtureFactory.save(
              (entity, builder) -> {
                builder.set("albumId", albumId);
                builder.set("name", PictureTagName.of("태그1"));
                builder.setNull("deletedAt");
              });

      PictureTag tag2 =
          pictureTagFixtureFactory.save(
              (entity, builder) -> {
                builder.set("albumId", albumId);
                builder.set("name", PictureTagName.of("태그2"));
                builder.setNull("deletedAt");
              });

      pictureTagRelationFixtureFactory.save(
          (entity, builder) -> {
            builder.set("pictureId", picture.getId());
            builder.set("albumId", albumId);
            builder.set("tagId", tag1.getId());
            builder.setNull("deletedAt");
          });

      pictureTagRelationFixtureFactory.save(
          (entity, builder) -> {
            builder.set("pictureId", picture.getId());
            builder.set("albumId", albumId);
            builder.set("tagId", tag2.getId());
            builder.setNull("deletedAt");
          });

      // when
      List<PictureDetailResult> results =
          pictureRepositoryImpl.findDetailByIds(List.of(picture.getId()));

      // then
      assertThat(results.size()).isEqualTo(1);
      PictureDetailResult result = results.get(0);
      assertThat(result.pictureId()).isEqualTo(picture.getId());
      assertThat(result.content()).isEqualTo(picture.getContent());
      assertThat(result.path()).isEqualTo(picture.getPath());
      assertThat(result.picturedAt()).isEqualTo(picture.getPicturedAt());
      assertThat("태그1").isIn(result.tagNames());
      assertThat("태그2").isIn(result.tagNames());
    }

    @Test
    @DisplayName("아이디에 해당하는 사진을 조회하며 삭제된 태그는 조회하지 않는다")
    void findDetailByIdsWithDeleteTag() {
      // given
      String content = "사진 내용";
      String path = "/result/1/2/3";
      LocalDateTime picturedAt = LocalDateTime.of(2023, 10, 14, 0, 0, 0);
      Long albumId = 1L;

      Picture picture =
          pictureFixtureFactory.save(
              (entity, builder) -> {
                builder.set("albumId", albumId);
                builder.set("content", content);
                builder.set("path", path);
                builder.set("picturedAt", picturedAt);
                builder.setNull("deletedAt");
              });

      PictureTag tag1 =
          pictureTagFixtureFactory.save(
              (entity, builder) -> {
                builder.set("albumId", albumId);
                builder.set("name", PictureTagName.of("태그1"));
                builder.set("deletedAt", LocalDateTime.now());
              });

      PictureTag tag2 =
          pictureTagFixtureFactory.save(
              (entity, builder) -> {
                builder.set("albumId", albumId);
                builder.set("name", PictureTagName.of("태그2"));
                builder.setNull("deletedAt");
              });

      pictureTagRelationFixtureFactory.save(
          (entity, builder) -> {
            builder.set("pictureId", picture.getId());
            builder.set("albumId", albumId);
            builder.set("tagId", tag1.getId());
            builder.setNull("deletedAt");
          });

      pictureTagRelationFixtureFactory.save(
          (entity, builder) -> {
            builder.set("pictureId", picture.getId());
            builder.set("albumId", albumId);
            builder.set("tagId", tag2.getId());
            builder.setNull("deletedAt");
          });

      // when
      List<PictureDetailResult> results =
          pictureRepositoryImpl.findDetailByIds(List.of(picture.getId()));

      // then
      assertThat(results.size()).isEqualTo(1);
      PictureDetailResult result = results.get(0);
      assertThat(result.pictureId()).isEqualTo(picture.getId());
      assertThat(result.content()).isEqualTo(picture.getContent());
      assertThat(result.path()).isEqualTo(picture.getPath());
      assertThat(result.picturedAt()).isEqualTo(picture.getPicturedAt());
      assertThat("태그1").isNotIn(result.tagNames());
      assertThat("태그2").isIn(result.tagNames());
    }
  }
}
