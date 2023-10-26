package com.onebyte.life4cut.picture.repository;

import com.onebyte.life4cut.picture.domain.Picture;
import com.onebyte.life4cut.picture.repository.dto.PictureDetailResult;
import java.util.List;
import java.util.Optional;

public interface PictureRepository {

  Picture save(Picture picture);

  Optional<Picture> findById(Long pictureId);

  List<PictureDetailResult> findDetailByIds(List<Long> pictureIds);
}
