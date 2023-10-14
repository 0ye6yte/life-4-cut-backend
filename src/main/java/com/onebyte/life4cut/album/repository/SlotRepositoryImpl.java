package com.onebyte.life4cut.album.repository;

import static com.onebyte.life4cut.album.domain.QSlot.slot;

import com.onebyte.life4cut.album.domain.Slot;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class SlotRepositoryImpl implements SlotRepository {

  private final JPAQueryFactory jpaQueryFactory;

  public SlotRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
    this.jpaQueryFactory = jpaQueryFactory;
  }

  @Override
  public Optional<Slot> findById(Long id) {
    return Optional.ofNullable(
        jpaQueryFactory.selectFrom(slot).where(slot.id.eq(id), slot.deletedAt.isNull()).fetchOne());
  }

  @Override
  public List<Slot> findByAlbumId(Long albumId) {
    return jpaQueryFactory
        .selectFrom(slot)
        .where(slot.albumId.eq(albumId), slot.deletedAt.isNull())
        .orderBy(slot.page.asc())
        .fetch();
  }
}
