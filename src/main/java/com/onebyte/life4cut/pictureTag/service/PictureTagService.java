package com.onebyte.life4cut.pictureTag.service;

import com.onebyte.life4cut.album.exception.UserAlbumRolePermissionException;
import com.onebyte.life4cut.album.repository.UserAlbumRepository;
import com.onebyte.life4cut.picture.domain.PictureTag;
import com.onebyte.life4cut.pictureTag.repository.PictureTagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PictureTagService {

  private final PictureTagRepository pictureTagRepository;
  private final UserAlbumRepository userAlbumRepository;

  @Transactional(readOnly = true)
  public List<PictureTag> searchTags(final Long albumId, final Long userId, final String keyword) {
    userAlbumRepository
        .findByUserIdAndAlbumId(userId, albumId)
        .orElseThrow(UserAlbumRolePermissionException::new);

    return pictureTagRepository.search(albumId, keyword);
  }
}
