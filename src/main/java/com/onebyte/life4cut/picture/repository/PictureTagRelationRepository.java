package com.onebyte.life4cut.picture.repository;

import com.onebyte.life4cut.picture.domain.PictureTagRelation;
import java.util.List;

public interface PictureTagRelationRepository {

  PictureTagRelation save(PictureTagRelation pictureTagRelation);

  List<PictureTagRelation> saveAll(Iterable<PictureTagRelation> pictureTagRelations);

  List<PictureTagRelation> findByPictureId(Long pictureId);
}
