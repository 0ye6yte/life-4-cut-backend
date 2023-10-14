package com.onebyte.life4cut.picture.repository;

import static com.onebyte.life4cut.picture.domain.QPicture.picture;
import static com.onebyte.life4cut.picture.domain.QPictureTag.pictureTag;
import static com.onebyte.life4cut.picture.domain.QPictureTagRelation.pictureTagRelation;
import static com.querydsl.core.types.dsl.Expressions.stringTemplate;

import com.onebyte.life4cut.picture.domain.Picture;
import com.onebyte.life4cut.picture.repository.dto.PictureDetailResult;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PictureRepositoryImpl implements PictureRepository {

  private final EntityManager em;
  private final JPAQueryFactory query;

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

  public List<PictureDetailResult> findDetailByIds(List<Long> pictureIds) {
    if (pictureIds.isEmpty()) {
      return Collections.emptyList();
    }

    return query
        .select(
            Projections.constructor(
                PictureDetailResult.class,
                picture.id,
                picture.content,
                picture.path,
                picture.picturedAt,
                stringTemplate("GROUP_CONCAT({0})", pictureTag.name).as("tags")))
        .from(picture)
        .leftJoin(pictureTagRelation)
        .on(picture.id.eq(pictureTagRelation.pictureId), pictureTagRelation.deletedAt.isNull())
        .leftJoin(pictureTag)
        .on(pictureTagRelation.tagId.eq(pictureTag.id), pictureTag.deletedAt.isNull())
        .where(picture.id.in(pictureIds), picture.deletedAt.isNull())
        .groupBy(picture.id)
        .fetch();
  }
}
