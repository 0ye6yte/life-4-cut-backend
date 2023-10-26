package com.onebyte.life4cut.picture.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onebyte.life4cut.album.domain.vo.UserAlbumRole;
import com.onebyte.life4cut.album.exception.AlbumDoesNotHaveSlotException;
import com.onebyte.life4cut.album.exception.AlbumNotFoundException;
import com.onebyte.life4cut.album.exception.UserAlbumRolePermissionException;
import com.onebyte.life4cut.album.repository.AlbumRepository;
import com.onebyte.life4cut.album.repository.UserAlbumRepository;
import com.onebyte.life4cut.common.constants.S3Env;
import com.onebyte.life4cut.fixture.AlbumFixtureFactory;
import com.onebyte.life4cut.fixture.PictureTagFixtureFactory;
import com.onebyte.life4cut.fixture.SlotFixtureFactory;
import com.onebyte.life4cut.fixture.UserAlbumFixtureFactory;
import com.onebyte.life4cut.picture.domain.Picture;
import com.onebyte.life4cut.picture.domain.PictureTag;
import com.onebyte.life4cut.picture.domain.PictureTagRelation;
import com.onebyte.life4cut.picture.domain.vo.PictureTagName;
import com.onebyte.life4cut.picture.repository.PictureRepository;
import com.onebyte.life4cut.picture.repository.PictureTagRelationRepository;
import com.onebyte.life4cut.pictureTag.repository.PictureTagRepository;
import com.onebyte.life4cut.slot.domain.Slot;
import com.onebyte.life4cut.slot.exception.SlotNotFoundException;
import com.onebyte.life4cut.slot.repository.SlotRepository;
import com.onebyte.life4cut.support.fileUpload.FileUploadResponse;
import com.onebyte.life4cut.support.fileUpload.FileUploader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

class PictureServiceTest {

  private final AlbumRepository albumRepository = mock(AlbumRepository.class);
  private final SlotRepository slotRepository = mock(SlotRepository.class);
  private final UserAlbumRepository userAlbumRepository = mock(UserAlbumRepository.class);
  private final PictureTagRepository pictureTagRepository = mock(PictureTagRepository.class);
  private final PictureTagRelationRepository pictureTagRelationRepository =
      mock(PictureTagRelationRepository.class);
  private final PictureRepository pictureRepository = mock(PictureRepository.class);
  private final FileUploader fileUploader = mock(FileUploader.class);
  private final S3Env s3Env = new S3Env("test");
  private final PictureService pictureService =
      new PictureService(
          slotRepository,
          albumRepository,
          userAlbumRepository,
          pictureTagRepository,
          pictureTagRelationRepository,
          pictureRepository,
          fileUploader,
          s3Env);
  private final AlbumFixtureFactory albumFixtureFactory = new AlbumFixtureFactory();
  private final UserAlbumFixtureFactory userAlbumFixtureFactory = new UserAlbumFixtureFactory();
  private final SlotFixtureFactory slotFixtureFactory = new SlotFixtureFactory();
  private final PictureTagFixtureFactory pictureTagFixtureFactory = new PictureTagFixtureFactory();

  @Nested
  class CreateInSlot {

    @Test
    @DisplayName("앨범이 없으면 AlbumNotFoundException이 발생한다")
    void createInSlot() {
      // given
      Long authorId = 1L;
      Long albumId = 1L;
      Long slotId = 1L;
      String content = "content";
      LocalDateTime picturedAt = LocalDateTime.of(2023, 9, 23, 1, 1, 1);
      List<String> tags = List.of("tag1", "tag2");
      MultipartFile image =
          new MockMultipartFile(
              "image.png", "original-name.png", MimeTypeUtils.IMAGE_PNG_VALUE, "test".getBytes());

      when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

      // when
      Throwable throwable =
          catchThrowable(
              () ->
                  pictureService.createInSlot(
                      authorId, albumId, slotId, content, picturedAt, tags, image));

      // then
      assertThat(throwable).isInstanceOf(AlbumNotFoundException.class).hasNoCause();
    }

    @Test
    @DisplayName("앨범내 권한이 없으면 UserAlbumNotFoundException이 발생한다")
    void noUserAlbum() {
      // given
      Long authorId = 1L;
      Long albumId = 1L;
      Long slotId = 1L;
      String content = "content";
      LocalDateTime picturedAt = LocalDateTime.of(2023, 9, 23, 1, 1, 1);
      List<String> tags = List.of("tag1", "tag2");
      MultipartFile image =
          new MockMultipartFile(
              "image.png", "original-name.png", MimeTypeUtils.IMAGE_PNG_VALUE, "test".getBytes());

      when(albumRepository.findById(albumId))
          .thenReturn(
              Optional.of(
                  albumFixtureFactory.make(
                      (entity, builder) -> {
                        builder.set("id", albumId);
                        builder.setNull("deletedAt");
                      })));

      // when
      Throwable throwable =
          catchThrowable(
              () ->
                  pictureService.createInSlot(
                      authorId, albumId, slotId, content, picturedAt, tags, image));

      // then
      assertThat(throwable).isInstanceOf(UserAlbumRolePermissionException.class).hasNoCause();
    }

    @Test
    @DisplayName("guest 권한인 경우 UserAlbumNotFoundException이 발생한다")
    void guest() {
      // given
      Long authorId = 1L;
      Long albumId = 1L;
      Long slotId = 1L;
      String content = "content";
      LocalDateTime picturedAt = LocalDateTime.of(2023, 9, 23, 1, 1, 1);
      List<String> tags = List.of("tag1", "tag2");
      MultipartFile image =
          new MockMultipartFile(
              "image.png", "original-name.png", MimeTypeUtils.IMAGE_PNG_VALUE, "test".getBytes());

      when(albumRepository.findById(albumId))
          .thenReturn(
              Optional.of(
                  albumFixtureFactory.make(
                      (entity, builder) -> {
                        builder.set("id", albumId);
                        builder.setNull("deletedAt");
                      })));

      when(userAlbumRepository.findByUserIdAndAlbumId(authorId, albumId))
          .thenReturn(
              Optional.of(
                  userAlbumFixtureFactory.make(
                      (entity, builder) -> {
                        builder.set("id", 1L);
                        builder.set("userId", authorId);
                        builder.set("albumId", albumId);
                        builder.set("role", UserAlbumRole.GUEST);
                        builder.setNull("deletedAt");
                      })));

      // when
      Throwable throwable =
          catchThrowable(
              () ->
                  pictureService.createInSlot(
                      authorId, albumId, slotId, content, picturedAt, tags, image));

      // then
      assertThat(throwable).isInstanceOf(UserAlbumRolePermissionException.class).hasNoCause();
    }

    @Test
    @DisplayName("slot이 없는 경우 SlotNotFoundException이 발생한다")
    void noSlot() {
      // given
      Long authorId = 1L;
      Long albumId = 1L;
      Long slotId = 1L;
      String content = "content";
      LocalDateTime picturedAt = LocalDateTime.of(2023, 9, 23, 1, 1, 1);
      List<String> tags = List.of("tag1", "tag2");
      MultipartFile image =
          new MockMultipartFile(
              "image.png", "original-name.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());

      when(albumRepository.findById(albumId))
          .thenReturn(
              Optional.of(
                  albumFixtureFactory.make(
                      (entity, builder) -> {
                        builder.set("id", albumId);
                        builder.setNull("deletedAt");
                      })));

      when(userAlbumRepository.findByUserIdAndAlbumId(authorId, albumId))
          .thenReturn(
              Optional.of(
                  userAlbumFixtureFactory.make(
                      (entity, builder) -> {
                        builder.set("id", 1L);
                        builder.set("userId", authorId);
                        builder.set("albumId", albumId);
                        builder.set("role", UserAlbumRole.MEMBER);
                        builder.setNull("deletedAt");
                      })));

      when(slotRepository.findById(slotId)).thenReturn(Optional.empty());

      // when
      Throwable throwable =
          catchThrowable(
              () ->
                  pictureService.createInSlot(
                      authorId, albumId, slotId, content, picturedAt, tags, image));

      // then
      assertThat(throwable).isInstanceOf(SlotNotFoundException.class).hasNoCause();
    }

    @Test
    @DisplayName("slot과 album이 매칭되지 않는 경우 AlbumDoesNotHaveSlotException이 발생한다")
    void invalidSlot() {
      // given
      Long authorId = 1L;
      Long albumId = 1L;
      Long slotId = 1L;
      String content = "content";
      LocalDateTime picturedAt = LocalDateTime.of(2023, 9, 23, 1, 1, 1);
      List<String> tags = List.of("tag1", "tag2");
      MultipartFile image =
          new MockMultipartFile(
              "image.png", "original-name.png", MimeTypeUtils.IMAGE_PNG_VALUE, "test".getBytes());

      when(albumRepository.findById(albumId))
          .thenReturn(
              Optional.of(
                  albumFixtureFactory.make(
                      (entity, builder) -> {
                        builder.set("id", albumId);
                        builder.setNull("deletedAt");
                      })));

      when(userAlbumRepository.findByUserIdAndAlbumId(authorId, albumId))
          .thenReturn(
              Optional.of(
                  userAlbumFixtureFactory.make(
                      (entity, builder) -> {
                        builder.set("id", 1L);
                        builder.set("userId", authorId);
                        builder.set("albumId", albumId);
                        builder.set("role", UserAlbumRole.MEMBER);
                        builder.setNull("deletedAt");
                      })));

      when(slotRepository.findById(slotId))
          .thenReturn(
              Optional.of(
                  slotFixtureFactory.make(
                      (entity, builder) -> {
                        builder.set("id", slotId);
                        builder.set("albumId", albumId + 1);
                        builder.setNull("deletedAt");
                      })));

      // when
      Throwable throwable =
          catchThrowable(
              () ->
                  pictureService.createInSlot(
                      authorId, albumId, slotId, content, picturedAt, tags, image));

      // then
      assertThat(throwable).isInstanceOf(AlbumDoesNotHaveSlotException.class).hasNoCause();
    }

    @Test
    @DisplayName("필요한 경우 태그를 복구, 추가하며 사진을 저장한다")
    void save() {
      // given
      Long authorId = 1L;
      Long albumId = 1L;
      Long slotId = 1L;
      String content = "content";
      LocalDateTime picturedAt = LocalDateTime.of(2023, 9, 23, 1, 1, 1);
      List<String> tags = List.of("existTag", "newTag", "deletedTag");
      MultipartFile image =
          new MockMultipartFile(
              "image.png", "original-name.png", MimeTypeUtils.IMAGE_PNG_VALUE, "test".getBytes());

      when(albumRepository.findById(albumId))
          .thenReturn(
              Optional.of(
                  albumFixtureFactory.make(
                      (entity, builder) -> {
                        builder.set("id", albumId);
                        builder.setNull("deletedAt");
                      })));

      when(userAlbumRepository.findByUserIdAndAlbumId(authorId, albumId))
          .thenReturn(
              Optional.of(
                  userAlbumFixtureFactory.make(
                      (entity, builder) -> {
                        builder.set("id", 1L);
                        builder.set("userId", authorId);
                        builder.set("albumId", albumId);
                        builder.set("role", UserAlbumRole.MEMBER);
                        builder.setNull("deletedAt");
                      })));

      Slot slot =
          slotFixtureFactory.make(
              (entity, builder) -> {
                builder.set("id", slotId);
                builder.set("albumId", albumId);
                builder.setNull("pictureId");
                builder.setNull("deletedAt");
              });

      when(slotRepository.findById(slotId)).thenReturn(Optional.of(slot));

      PictureTag existPictureTag =
          pictureTagFixtureFactory.make(
              (entity, builder) -> {
                builder.set("id", 1L);
                builder.set("name", PictureTagName.of(tags.get(0)));
                builder.set("albumId", albumId);
                builder.set("authorId", 10L);
                builder.setNull("deletedAt");
              });

      PictureTag deletedPictureTag =
          pictureTagFixtureFactory.make(
              (entity, builder) -> {
                builder.set("id", 2L);
                builder.set("name", PictureTagName.of(tags.get(2)));
                builder.set("albumId", albumId);
                builder.set("authorId", 11L);
                builder.set("deletedAt", LocalDateTime.now());
              });
      when(pictureTagRepository.findByNames(albumId, tags))
          .thenReturn(List.of(existPictureTag, deletedPictureTag));

      when(fileUploader.upload(any())).thenReturn(new FileUploadResponse("test"));

      when(pictureRepository.save(any()))
          .thenAnswer(
              invocation -> {
                Picture picture = invocation.getArgument(0);
                ReflectionTestUtils.setField(picture, "id", 1L);
                return picture;
              });

      ArgumentCaptor<List<PictureTag>> newPictureTagsCapture = ArgumentCaptor.forClass(List.class);
      when(pictureTagRepository.saveAll(anyCollection()))
          .thenAnswer(
              invocation -> {
                List<PictureTag> pictureTags = invocation.getArgument(0);
                for (int i = 0; i < pictureTags.size(); i++) {
                  ReflectionTestUtils.setField(pictureTags.get(i), "id", i + 10L);
                }
                return pictureTags;
              });

      ArgumentCaptor<List<PictureTagRelation>> newPictureTagRelationsCapture =
          ArgumentCaptor.forClass(List.class);
      when(pictureTagRelationRepository.saveAll(anyCollection()))
          .thenAnswer(
              invocation -> {
                List<PictureTagRelation> pictureTagRelations = invocation.getArgument(0);
                for (int i = 0; i < pictureTagRelations.size(); i++) {
                  ReflectionTestUtils.setField(pictureTagRelations.get(i), "id", i + 100L);
                }
                return pictureTagRelations;
              });

      // when
      Long pictureId =
          pictureService.createInSlot(authorId, albumId, slotId, content, picturedAt, tags, image);

      // then
      assertThat(pictureId).isEqualTo(1L);
      assertThat(slot.getPictureId()).isEqualTo(1L);

      verify(pictureTagRepository).saveAll(newPictureTagsCapture.capture());
      List<PictureTag> newPictureTags = newPictureTagsCapture.getValue();
      assertThat(newPictureTags).hasSize(1);
      assertThat(newPictureTags.get(0).getName().getValue()).isEqualTo(tags.get(1));

      verify(pictureTagRelationRepository).saveAll(newPictureTagRelationsCapture.capture());
      List<PictureTagRelation> newPictureTagRelations = newPictureTagRelationsCapture.getValue();
      assertThat(newPictureTagRelations).hasSize(3);
      assertThat(newPictureTagRelations.get(0).getTagId()).isEqualTo(1L);
      assertThat(newPictureTagRelations.get(0).getAlbumId()).isEqualTo(albumId);
      assertThat(newPictureTagRelations.get(0).getPictureId()).isEqualTo(1L);
      assertThat(newPictureTagRelations.get(1).getTagId()).isEqualTo(2L);
      assertThat(newPictureTagRelations.get(1).getAlbumId()).isEqualTo(albumId);
      assertThat(newPictureTagRelations.get(1).getPictureId()).isEqualTo(1L);
      assertThat(newPictureTagRelations.get(2).getTagId()).isEqualTo(10L);
      assertThat(newPictureTagRelations.get(2).getAlbumId()).isEqualTo(albumId);
      assertThat(newPictureTagRelations.get(2).getPictureId()).isEqualTo(1L);

      assertThat(deletedPictureTag.getDeletedAt()).isNull();
    }
  }
}
