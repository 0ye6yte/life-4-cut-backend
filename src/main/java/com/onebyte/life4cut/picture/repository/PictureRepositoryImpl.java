package com.onebyte.life4cut.picture.repository;

import com.onebyte.life4cut.picture.domain.Picture;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PictureRepositoryImpl implements PictureRepository {

  private final EntityManager em;

  @Override
  public Picture save(Picture picture) {
    em.persist(picture);
    return picture;
  }

  @Override
  public Optional<Picture> findById(Long pictureId) {
    return em.createQuery(
            "SELECT p FROM Picture p WHERE p.id = :pictureId AND p.deletedAt IS NULL",
            Picture.class)
        .setParameter("pictureId", pictureId)
        .getResultStream()
        .findFirst();
  }
}
