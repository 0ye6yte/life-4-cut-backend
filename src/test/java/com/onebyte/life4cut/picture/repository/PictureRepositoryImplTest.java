package com.onebyte.life4cut.picture.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.onebyte.life4cut.common.annotation.RepositoryTest;
import com.onebyte.life4cut.fixture.PictureFixtureFactory;
import com.onebyte.life4cut.picture.domain.Picture;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@RepositoryTest(PictureRepositoryImpl.class)
class PictureRepositoryImplTest {
  @Autowired private PictureRepositoryImpl pictureRepositoryImpl;
  @Autowired private PictureFixtureFactory pictureFixtureFactory;

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
}
