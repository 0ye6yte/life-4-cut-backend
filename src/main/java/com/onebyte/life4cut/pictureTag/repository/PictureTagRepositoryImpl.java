package com.onebyte.life4cut.pictureTag.repository;

import static com.onebyte.life4cut.picture.domain.QPictureTag.pictureTag;
import static org.springframework.util.StringUtils.hasText;

import com.onebyte.life4cut.picture.domain.PictureTag;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PictureTagRepositoryImpl implements PictureTagRepository {

  private final EntityManager em;
  private final JPAQueryFactory query;

  public List<PictureTag> findByNames(@Nonnull Long albumId, @Nonnull List<String> names) {
    if (names.isEmpty()) {
      return Collections.emptyList();
    }

    return query
        .selectFrom(pictureTag)
        .where(pictureTag.albumId.eq(albumId), pictureTag.name.value.in(names))
        .fetch();
  }

  @Override
  public List<PictureTag> saveAll(Iterable<PictureTag> pictureTags) {
    List<PictureTag> results = new ArrayList<>();

    for (PictureTag pictureTag : pictureTags) {
      results.add(save(pictureTag));
    }

    return results;
  }

  @Override
  public PictureTag save(PictureTag pictureTag) {
    em.persist(pictureTag);
    return pictureTag;
  }

  @Override
  public List<PictureTag> search(@Nonnull Long albumId, @Nullable String keyword) {
    return query
        .selectFrom(pictureTag)
        .where(
            pictureTag.albumId.eq(albumId), containsKeyword(keyword), pictureTag.deletedAt.isNull())
        .fetch();
  }

  @Nullable
  private BooleanExpression containsKeyword(@Nullable String keyword) {
    if (!hasText(keyword)) {
      return null;
    }

    return pictureTag.name.value.contains(keyword);
  }
}
