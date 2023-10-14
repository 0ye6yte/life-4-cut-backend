package com.onebyte.life4cut.picture.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.onebyte.life4cut.album.domain.Album;
import com.onebyte.life4cut.album.domain.UserAlbum;
import com.onebyte.life4cut.album.domain.vo.UserAlbumRole;
import com.onebyte.life4cut.album.exception.AlbumNotFoundException;
import com.onebyte.life4cut.album.exception.UserAlbumRolePermissionException;
import com.onebyte.life4cut.album.repository.AlbumRepositoryImpl;
import com.onebyte.life4cut.album.repository.SlotRepositoryImpl;
import com.onebyte.life4cut.album.repository.UserAlbumRepositoryImpl;
import com.onebyte.life4cut.common.constants.S3Env;
import com.onebyte.life4cut.config.JpaConfiguration;
import com.onebyte.life4cut.fixture.AlbumFixtureFactory;
import com.onebyte.life4cut.fixture.PictureFixtureFactory;
import com.onebyte.life4cut.fixture.PictureTagFixtureFactory;
import com.onebyte.life4cut.fixture.PictureTagRelationFixtureFactory;
import com.onebyte.life4cut.fixture.UserAlbumFixtureFactory;
import com.onebyte.life4cut.picture.domain.Picture;
import com.onebyte.life4cut.picture.domain.PictureTag;
import com.onebyte.life4cut.picture.domain.PictureTagRelation;
import com.onebyte.life4cut.picture.domain.vo.PictureTagName;
import com.onebyte.life4cut.picture.exception.PictureNotFoundException;
import com.onebyte.life4cut.picture.repository.PictureRepositoryImpl;
import com.onebyte.life4cut.picture.repository.PictureTagRelationRepositoryImpl;
import com.onebyte.life4cut.pictureTag.repository.PictureTagRepositoryImpl;
import com.onebyte.life4cut.support.fileUpload.FileUploadResponse;
import com.onebyte.life4cut.support.fileUpload.FileUploader;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@DataJpaTest
@Import({JpaConfiguration.class})
@ComponentScan(basePackages = {"com.onebyte.life4cut.fixture"})
public class PictureServiceIntTest {

  private FileUploader fileUploader = mock(FileUploader.class);
  private final S3Env s3Env = new S3Env("test");
  private PictureService pictureService;
  private PictureRepositoryImpl pictureRepository;
  private EntityManager entityManager;
  @Autowired private PictureFixtureFactory pictureFixtureFactory;
  @Autowired private AlbumFixtureFactory albumFixtureFactory;
  @Autowired private UserAlbumFixtureFactory userAlbumFixtureFactory;
  @Autowired private PictureTagFixtureFactory pictureTagFixtureFactory;
  @Autowired private PictureTagRelationFixtureFactory pictureTagRelationFixtureFactory;

  @Autowired
  public PictureServiceIntTest(EntityManager entityManager, JPAQueryFactory query) {
    this.entityManager = entityManager;
    this.pictureRepository = new PictureRepositoryImpl(entityManager, query);
    this.pictureService =
        new PictureService(
            new SlotRepositoryImpl(query),
            new AlbumRepositoryImpl(query),
            new UserAlbumRepositoryImpl(query),
            new PictureTagRepositoryImpl(entityManager, query),
            new PictureTagRelationRepositoryImpl(entityManager),
            pictureRepository,
            fileUploader,
            s3Env);
  }

  @Nested
  class UpdatePicture {

    @Test
    @DisplayName("해당하는 사진이 없는 경우 PictureNotFoundException이 발생한다")
    void pictureNotFound() {
      // given
      Long authorId = 1L;
      Long albumId = 1L;
      Long pictureId = 1L;
      LocalDateTime now = LocalDateTime.now();
      String content = null;
      List<String> tags = null;
      LocalDateTime picturedAt = null;
      MultipartFile image = null;

      // when
      Throwable throwable =
          catchThrowable(
              () ->
                  pictureService.updatePicture(
                      authorId, albumId, pictureId, now, content, tags, picturedAt, image));

      // then
      assertThat(throwable).isInstanceOf(PictureNotFoundException.class);
    }

    @Test
    @DisplayName("해당하는 사진이 앨범내에 존재하지 않는 경우 PictureNotFoundException이 발생한다")
    void notInAlbum() {
      // given
      Picture picture =
          pictureFixtureFactory.save(
              (entity, builder) -> {
                builder.set("albumId", 2L);
                builder.setNull("deletedAt");
              });

      Long authorId = 1L;
      Long albumId = 1L;
      Long pictureId = picture.getId();
      LocalDateTime now = LocalDateTime.now();
      String content = null;
      List<String> tags = null;
      LocalDateTime picturedAt = null;
      MultipartFile image = null;

      // when
      Throwable throwable =
          catchThrowable(
              () ->
                  pictureService.updatePicture(
                      authorId, albumId, pictureId, now, content, tags, picturedAt, image));
      // then
      assertThat(throwable).isInstanceOf(PictureNotFoundException.class);
    }

    @Test
    @DisplayName("해당하는 앨범이 없는 경우 AlbumNotFoundException이 발생한다")
    void noAlbum() {
      // given
      Picture picture =
          pictureFixtureFactory.save(
              (entity, builder) -> {
                builder.set("albumId", 1L);
                builder.setNull("deletedAt");
              });

      Long authorId = 1L;
      Long albumId = 1L;
      Long pictureId = picture.getId();
      LocalDateTime now = LocalDateTime.now();
      String content = null;
      List<String> tags = null;
      LocalDateTime picturedAt = null;
      MultipartFile image = null;

      // when
      Throwable throwable =
          catchThrowable(
              () ->
                  pictureService.updatePicture(
                      authorId, albumId, pictureId, now, content, tags, picturedAt, image));
      // then
      assertThat(throwable).isInstanceOf(AlbumNotFoundException.class);
    }

    @Test
    @DisplayName("해당하는 앨범에 권한이 없는 경우 UserAlbumRolePermissionException이 발생한다")
    void noPermission() {
      // given
      Album album =
          albumFixtureFactory.save(
              (entity, builder) -> {
                builder.setNull("deletedAt");
              });
      Picture picture =
          pictureFixtureFactory.save(
              (entity, builder) -> {
                builder.set("albumId", album.getId());
                builder.setNull("deletedAt");
              });

      Long authorId = 1L;
      Long albumId = album.getId();
      Long pictureId = picture.getId();
      LocalDateTime now = LocalDateTime.now();
      String content = null;
      List<String> tags = null;
      LocalDateTime picturedAt = null;
      MultipartFile image = null;

      // when
      Throwable throwable =
          catchThrowable(
              () ->
                  pictureService.updatePicture(
                      authorId, albumId, pictureId, now, content, tags, picturedAt, image));

      // then
      assertThat(throwable).isInstanceOf(UserAlbumRolePermissionException.class);
    }

    @Test
    @DisplayName("GUEST 권한인 경우 UserAlbumRolePermissionException이 발생한다")
    void guest() {
      // given
      Album album =
          albumFixtureFactory.save(
              (entity, builder) -> {
                builder.setNull("deletedAt");
              });
      Picture picture =
          pictureFixtureFactory.save(
              (entity, builder) -> {
                builder.set("albumId", album.getId());
                builder.setNull("deletedAt");
              });
      UserAlbum useralbum =
          userAlbumFixtureFactory.save(
              (entity, builder) -> {
                builder.set("userId", 1L);
                builder.set("role", UserAlbumRole.GUEST);
                builder.set("albumId", album.getId());
                builder.setNull("deletedAt");
              });

      Long authorId = useralbum.getUserId();
      Long albumId = album.getId();
      Long pictureId = picture.getId();
      LocalDateTime now = LocalDateTime.now();
      String content = null;
      List<String> tags = null;
      LocalDateTime picturedAt = null;
      MultipartFile image = null;

      // when
      Throwable throwable =
          catchThrowable(
              () ->
                  pictureService.updatePicture(
                      authorId, albumId, pictureId, now, content, tags, picturedAt, image));
      // then
      assertThat(throwable).isInstanceOf(UserAlbumRolePermissionException.class);
    }

    @Test
    @DisplayName("변경할 이미지가 존재하는 경우 사진을 변경한다")
    void updateImage() {
      // given
      Album album =
          albumFixtureFactory.save(
              (entity, builder) -> {
                builder.setNull("deletedAt");
              });
      Picture picture =
          pictureFixtureFactory.save(
              (entity, builder) -> {
                builder.set("albumId", album.getId());
                builder.set("path", "originKey");
                builder.setNull("deletedAt");
              });
      UserAlbum useralbum =
          userAlbumFixtureFactory.save(
              (entity, builder) -> {
                builder.set("userId", 1L);
                builder.set("role", UserAlbumRole.HOST);
                builder.set("albumId", album.getId());
                builder.setNull("deletedAt");
              });

      Long authorId = useralbum.getUserId();
      Long albumId = album.getId();
      Long pictureId = picture.getId();
      LocalDateTime now = LocalDateTime.now();
      String content = null;
      List<String> tags = null;
      LocalDateTime picturedAt = null;
      MultipartFile image =
          new MockMultipartFile("image", "image.jpg", "image/jpeg", "image".getBytes());

      when(fileUploader.upload(Mockito.any())).thenReturn(new FileUploadResponse("key"));

      // when
      pictureService.updatePicture(
          authorId, albumId, pictureId, now, content, tags, picturedAt, image);

      // then
      Picture savedPicture = pictureRepository.findById(pictureId).get();
      assertThat(savedPicture.getPath()).isNotEqualTo(picture.getPath());
    }

    @Test
    @DisplayName("변경할 설명이 있는 경우 사진 설명을 수정한다")
    void updateContent() {
      // given
      Album album =
          albumFixtureFactory.save(
              (entity, builder) -> {
                builder.setNull("deletedAt");
              });
      Picture picture =
          pictureFixtureFactory.save(
              (entity, builder) -> {
                builder.set("albumId", album.getId());
                builder.set("content", "originContent");
                builder.setNull("deletedAt");
              });
      UserAlbum useralbum =
          userAlbumFixtureFactory.save(
              (entity, builder) -> {
                builder.set("userId", 1L);
                builder.set("role", UserAlbumRole.HOST);
                builder.set("albumId", album.getId());
                builder.setNull("deletedAt");
              });

      Long authorId = useralbum.getUserId();
      Long albumId = album.getId();
      Long pictureId = picture.getId();
      LocalDateTime now = LocalDateTime.now();
      String content = "updateContent";
      List<String> tags = null;
      LocalDateTime picturedAt = null;
      MultipartFile image = null;

      // when
      pictureService.updatePicture(
          authorId, albumId, pictureId, now, content, tags, picturedAt, image);

      // then
      Picture savedPicture = pictureRepository.findById(pictureId).get();
      assertThat(savedPicture.getContent()).isEqualTo(content);
      assertThat(savedPicture.getPath()).isEqualTo(picture.getPath());
      assertThat(savedPicture.getPicturedAt()).isEqualTo(picture.getPicturedAt());
    }

    @Test
    @DisplayName("변경할 찍은 날짜가 있는 경우 찍은 날짜를 변경한다.")
    void updatePicturedAt() {
      // given
      Album album =
          albumFixtureFactory.save(
              (entity, builder) -> {
                builder.setNull("deletedAt");
              });
      Picture picture =
          pictureFixtureFactory.save(
              (entity, builder) -> {
                builder.set("albumId", album.getId());
                builder.set("picturedAt", LocalDateTime.of(2021, 1, 1, 0, 0));
                builder.setNull("deletedAt");
              });
      UserAlbum useralbum =
          userAlbumFixtureFactory.save(
              (entity, builder) -> {
                builder.set("userId", 1L);
                builder.set("role", UserAlbumRole.HOST);
                builder.set("albumId", album.getId());
                builder.setNull("deletedAt");
              });

      Long authorId = useralbum.getUserId();
      Long albumId = album.getId();
      Long pictureId = picture.getId();
      LocalDateTime now = LocalDateTime.now();
      String content = null;
      List<String> tags = null;
      LocalDateTime picturedAt = LocalDateTime.of(2023, 10, 3, 0, 0, 0);
      MultipartFile image = null;

      // when
      pictureService.updatePicture(
          authorId, albumId, pictureId, now, content, tags, picturedAt, image);

      // then
      Picture savedPicture = pictureRepository.findById(pictureId).get();
      assertThat(savedPicture.getPicturedAt()).isEqualTo(picturedAt);
      assertThat(savedPicture.getContent()).isEqualTo(picture.getContent());
      assertThat(savedPicture.getPath()).isEqualTo(picture.getPath());
    }

    @Test
    @DisplayName("변경할 태그가 있는 경우 새롭게 태그를 추가/삭제/복구한다.")
    void updateTag() {
      // given
      Album album =
          albumFixtureFactory.save(
              (entity, builder) -> {
                builder.setNull("deletedAt");
              });
      Picture picture =
          pictureFixtureFactory.save(
              (entity, builder) -> {
                builder.set("albumId", album.getId());
                builder.setNull("deletedAt");
              });
      UserAlbum useralbum =
          userAlbumFixtureFactory.save(
              (entity, builder) -> {
                builder.set("userId", 1L);
                builder.set("role", UserAlbumRole.HOST);
                builder.set("albumId", album.getId());
                builder.setNull("deletedAt");
              });

      PictureTag pictureTag1 =
          pictureTagFixtureFactory.save(
              (entity, builder) -> {
                builder.set("albumId", album.getId());
                builder.set("authorId", useralbum.getUserId());
                builder.set("name", PictureTagName.of("tag1"));
                builder.setNull("deletedAt");
              });

      PictureTag pictureTag2 =
          pictureTagFixtureFactory.save(
              (entity, builder) -> {
                builder.set("albumId", album.getId());
                builder.set("authorId", useralbum.getUserId());
                builder.set("name", PictureTagName.of("tag2"));
                builder.setNull("deletedAt");
              });

      PictureTagRelation relationToDelete =
          pictureTagRelationFixtureFactory.save(
              (entity, builder) -> {
                builder.set("pictureId", picture.getId());
                builder.set("albumId", album.getId());
                builder.set("tagId", pictureTag1.getId());
                builder.setNull("deletedAt");
              });
      PictureTagRelation relationToRestore =
          pictureTagRelationFixtureFactory.save(
              (entity, builder) -> {
                builder.set("pictureId", picture.getId());
                builder.set("albumId", album.getId());
                builder.set("tagId", pictureTag2.getId());
                builder.set("deletedAt", LocalDateTime.now());
              });

      Long authorId = useralbum.getUserId();
      Long albumId = album.getId();
      Long pictureId = picture.getId();
      LocalDateTime now = LocalDateTime.now();
      String content = null;
      List<String> tags = List.of(pictureTag2.getName().getValue(), "newTag");
      LocalDateTime picturedAt = null;
      MultipartFile image = null;

      // when
      pictureService.updatePicture(
          authorId, albumId, pictureId, now, content, tags, picturedAt, image);

      // then
      Picture savedPicture = entityManager.find(Picture.class, pictureId);
      assertThat(savedPicture.getPicturedAt()).isEqualTo(picture.getPicturedAt());
      assertThat(savedPicture.getContent()).isEqualTo(picture.getContent());
      assertThat(savedPicture.getPath()).isEqualTo(picture.getPath());

      assertThat(
              entityManager
                  .createQuery("SELECT pt FROM PictureTag pt", PictureTag.class)
                  .getResultStream()
                  .map(PictureTag::getName))
          .contains(PictureTagName.of("newTag"));

      entityManager
          .createQuery("select p from PictureTagRelation p", PictureTagRelation.class)
          .getResultList()
          .forEach(
              relation -> {
                assertThat(relation.getPictureId()).isEqualTo(pictureId);
                if (relation.getId().equals(relationToDelete.getId())) {
                  assertThat(relation.getDeletedAt()).isNotNull();
                } else if (relation.getId().equals(relationToRestore.getId())) {
                  assertThat(relation.getDeletedAt()).isNull();
                } else {
                  assertThat(relation.getDeletedAt()).isNull();
                }
              });
    }
  }
}
