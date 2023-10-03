package com.onebyte.life4cut.picture.repository;

import com.onebyte.life4cut.picture.domain.Picture;
import java.util.Optional;

public interface PictureRepository {

  Picture save(Picture picture);

  Optional<Picture> findById(Long pictureId);
}
