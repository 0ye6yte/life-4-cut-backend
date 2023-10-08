package com.onebyte.life4cut.picture.service;

import com.onebyte.life4cut.album.domain.Album;
import com.onebyte.life4cut.album.domain.Slot;
import com.onebyte.life4cut.album.domain.UserAlbum;
import com.onebyte.life4cut.album.exception.AlbumDoesNotHaveSlotException;
import com.onebyte.life4cut.album.exception.AlbumNotFoundException;
import com.onebyte.life4cut.album.exception.SlotNotFoundException;
import com.onebyte.life4cut.album.exception.UserAlbumRolePermissionException;
import com.onebyte.life4cut.album.repository.AlbumRepository;
import com.onebyte.life4cut.album.repository.SlotRepository;
import com.onebyte.life4cut.album.repository.UserAlbumRepository;
import com.onebyte.life4cut.common.constants.S3Env;
import com.onebyte.life4cut.picture.domain.Picture;
import com.onebyte.life4cut.picture.domain.PictureTag;
import com.onebyte.life4cut.picture.domain.PictureTagRelation;
import com.onebyte.life4cut.picture.exception.PictureNotFoundException;
import com.onebyte.life4cut.picture.repository.PictureRepository;
import com.onebyte.life4cut.picture.repository.PictureTagRelationRepository;
import com.onebyte.life4cut.pictureTag.repository.PictureTagRepository;
import com.onebyte.life4cut.support.fileUpload.FileUploadResponse;
import com.onebyte.life4cut.support.fileUpload.FileUploader;
import com.onebyte.life4cut.support.fileUpload.MultipartFileUploadRequest;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PictureService {

  private final SlotRepository slotRepository;
  private final AlbumRepository albumRepository;
  private final UserAlbumRepository userAlbumRepository;
  private final PictureTagRepository pictureTagRepository;
  private final PictureTagRelationRepository pictureTagRelationRepository;
  private final PictureRepository pictureRepository;
  private final FileUploader fileUploader;
  private final S3Env s3Env;

  @Transactional
  public Long createInSlot(
      @Nonnull Long authorId,
      @Nonnull Long albumId,
      @Nonnull Long slotId,
      @Nonnull String content,
      @Nonnull LocalDateTime picturedAt,
      @Nonnull List<String> tags,
      @Nonnull MultipartFile image) {
    Album album = albumRepository.findById(albumId).orElseThrow(AlbumNotFoundException::new);
    UserAlbum userAlbum =
        userAlbumRepository
            .findByUserIdAndAlbumId(authorId, albumId)
            .orElseThrow(UserAlbumRolePermissionException::new);
    if (userAlbum.isGuest()) {
      throw new UserAlbumRolePermissionException();
    }

    Slot slot = slotRepository.findById(slotId).orElseThrow(SlotNotFoundException::new);
    if (!slot.isIn(album)) {
      throw new AlbumDoesNotHaveSlotException();
    }

    List<PictureTag> pictureTags = pictureTagRepository.findByNames(albumId, tags);
    List<PictureTag> newPictureTags =
        tags.stream()
            .filter(
                tag ->
                    pictureTags.stream()
                        .noneMatch(pictureTag -> pictureTag.getName().getValue().equals(tag)))
            .map(tag -> PictureTag.create(albumId, authorId, tag))
            .toList();

    FileUploadResponse response =
        fileUploader.upload(MultipartFileUploadRequest.of(image, s3Env.bucket()));

    Picture picture = Picture.create(authorId, albumId, response.key(), content, picturedAt);

    pictureRepository.save(picture);
    slot.addPicture(picture.getId());

    pictureTagRepository.saveAll(newPictureTags);
    pictureTags.forEach(PictureTag::restoreIfRequired);

    List<PictureTagRelation> newPictureTagRelations =
        Stream.concat(pictureTags.stream(), newPictureTags.stream())
            .map(
                pictureTag ->
                    PictureTagRelation.create(picture.getId(), albumId, pictureTag.getId()))
            .toList();
    pictureTagRelationRepository.saveAll(newPictureTagRelations);

    return picture.getId();
  }

  @Transactional
  public void updatePicture(
      @Nonnull final Long authorId,
      @Nonnull final Long albumId,
      @Nonnull final Long pictureId,
      @Nonnull final LocalDateTime now,
      @Nullable final String content,
      @Nullable final List<String> tags,
      @Nullable final LocalDateTime picturedAt,
      @Nullable final MultipartFile image) {
    Picture picture =
        pictureRepository.findById(pictureId).orElseThrow(PictureNotFoundException::new);

    if (!picture.isIn(albumId)) {
      throw new PictureNotFoundException();
    }

    albumRepository.findById(albumId).orElseThrow(AlbumNotFoundException::new);
    UserAlbum userAlbum =
        userAlbumRepository
            .findByUserIdAndAlbumId(authorId, albumId)
            .orElseThrow(UserAlbumRolePermissionException::new);
    if (userAlbum.isGuest()) {
      throw new UserAlbumRolePermissionException();
    }

    String key = uploadImageIfRequired(image);

    picture.updateIfRequired(content, picturedAt, key);

    if (tags != null) {
      List<PictureTag> pictureTags = pictureTagRepository.findByNames(albumId, tags);
      List<PictureTag> newPictureTags =
          tags.stream()
              .filter(
                  tag ->
                      pictureTags.stream()
                          .noneMatch(pictureTag -> pictureTag.getName().getValue().equals(tag)))
              .map(tag -> PictureTag.create(albumId, authorId, tag))
              .toList();

      pictureTagRepository.saveAll(newPictureTags);
      pictureTags.forEach(PictureTag::restoreIfRequired);

      List<PictureTagRelation> pictureTagRelations =
          pictureTagRelationRepository.findByPictureId(pictureId);

      List<PictureTagRelation> newPictureTagRelations =
          Stream.concat(pictureTags.stream(), newPictureTags.stream())
              .filter(
                  pictureTag ->
                      pictureTagRelations.stream()
                          .noneMatch(
                              pictureTagRelation ->
                                  pictureTagRelation.getTagId().equals(pictureTag.getId())))
              .map(
                  pictureTag ->
                      PictureTagRelation.create(
                          picture.getId(), picture.getAlbumId(), pictureTag.getId()))
              .toList();

      List<PictureTagRelation> pictureTagRelationsToRestoreIfRequired =
          pictureTagRelations.stream()
              .filter(
                  pictureTagRelation ->
                      pictureTags.stream()
                          .anyMatch(
                              pictureTag ->
                                  pictureTagRelation.getTagId().equals(pictureTag.getId())))
              .toList();

      List<PictureTagRelation> pictureTagRelationsToDelete =
          pictureTagRelations.stream()
              .filter(
                  pictureTagRelation ->
                      pictureTagRelationsToRestoreIfRequired.stream()
                          .noneMatch(
                              newPictureTagRelation ->
                                  pictureTagRelation
                                      .getTagId()
                                      .equals(newPictureTagRelation.getTagId())))
              .toList();

      pictureTagRelationRepository.saveAll(newPictureTagRelations);
      pictureTagRelationsToRestoreIfRequired.forEach(PictureTagRelation::restoreIfRequired);
      pictureTagRelationsToDelete.forEach(pictureTagRelation -> pictureTagRelation.delete(now));
    }
  }

  @Nullable
  private String uploadImageIfRequired(@Nullable MultipartFile image) {
    if (image == null) {
      return null;
    }

    return fileUploader.upload(MultipartFileUploadRequest.of(image, s3Env.bucket())).key();
  }
}
