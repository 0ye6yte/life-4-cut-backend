package com.onebyte.life4cut.slot.repository;

import com.onebyte.life4cut.slot.domain.Slot;
import java.util.List;
import java.util.Optional;

public interface SlotRepository {

  Optional<Slot> findById(Long id);

  List<Slot> findByAlbumId(Long albumId);
}
