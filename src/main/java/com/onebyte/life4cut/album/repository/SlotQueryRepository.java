package com.onebyte.life4cut.album.repository;

import com.onebyte.life4cut.album.domain.Slot;

import java.util.Optional;

public interface SlotQueryRepository {

    Optional<Slot> findById(Long id);
}
