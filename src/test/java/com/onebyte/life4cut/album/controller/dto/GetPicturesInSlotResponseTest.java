package com.onebyte.life4cut.album.controller.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.onebyte.life4cut.album.controller.dto.GetPicturesInSlotResponse.PictureInSlot;
import com.onebyte.life4cut.picture.repository.dto.PictureDetailResult;
import com.onebyte.life4cut.picture.service.dto.PictureDetailInSlot;
import com.onebyte.life4cut.slot.domain.vo.SlotLayout;
import com.onebyte.life4cut.slot.domain.vo.SlotLocation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class GetPicturesInSlotResponseTest {

  @Nested
  class Of {
    @Test
    @DisplayName("page단위로 그룹핑하여 결과를 반환하는가")
    void groupByPage() {
      // given
      List<PictureDetailInSlot> pictureDetailInSlots =
          List.of(
              new PictureDetailInSlot(
                  1L, 1L, SlotLayout.LONG_VERTICAL, SlotLocation.LEFT, Optional.empty()),
              new PictureDetailInSlot(
                  2L,
                  1L,
                  SlotLayout.LONG_VERTICAL,
                  SlotLocation.RIGHT,
                  Optional.of(
                      new PictureDetailResult(
                          1L,
                          "content",
                          "path",
                          LocalDateTime.of(2023, 10, 14, 11, 52, 0),
                          "tag1,tag2"))),
              new PictureDetailInSlot(
                  3L, 2L, SlotLayout.FAT_HORIZONTAL, SlotLocation.LEFT, Optional.empty()),
              new PictureDetailInSlot(
                  4L,
                  5L,
                  SlotLayout.FAT_HORIZONTAL,
                  SlotLocation.LEFT,
                  Optional.of(
                      new PictureDetailResult(
                          2L,
                          "content",
                          "path",
                          LocalDateTime.of(2023, 10, 14, 11, 52, 0),
                          "tag3,tag7"))),
              new PictureDetailInSlot(
                  5L, 10L, SlotLayout.LONG_VERTICAL, SlotLocation.LEFT, Optional.empty()),
              new PictureDetailInSlot(
                  5L, 10L, SlotLayout.LONG_VERTICAL, SlotLocation.RIGHT, Optional.empty()));

      // when
      GetPicturesInSlotResponse result = GetPicturesInSlotResponse.of(pictureDetailInSlots);

      // then
      List<List<PictureInSlot>> pictures = result.pictures();
      assertThat(pictures).hasSize(4);

      List<PictureInSlot> page1 = pictures.get(0);
      assertThat(page1).hasSize(2);
      assertThat(page1.get(0).pictureId()).isNull();
      assertThat(page1.get(0).path()).isNull();
      assertThat(page1.get(0).content()).isNull();
      assertThat(page1.get(0).layout()).isEqualTo("LONG_VERTICAL");
      assertThat(page1.get(0).location()).isEqualTo("LEFT");
      assertThat(page1.get(0).picturedAt()).isNull();
      assertThat(page1.get(0).tagNames()).isEmpty();

      assertThat(page1.get(1).pictureId()).isEqualTo(1L);
      assertThat(page1.get(1).path()).isEqualTo("path");
      assertThat(page1.get(1).content()).isEqualTo("content");
      assertThat(page1.get(1).layout()).isEqualTo("LONG_VERTICAL");
      assertThat(page1.get(1).location()).isEqualTo("RIGHT");
      assertThat(page1.get(1).picturedAt()).isEqualTo(LocalDateTime.of(2023, 10, 14, 11, 52, 0));
      assertThat(page1.get(1).tagNames()).containsExactly("tag1", "tag2");

      List<PictureInSlot> page2 = pictures.get(1);
      assertThat(page2).hasSize(1);
      assertThat(page2.get(0).pictureId()).isNull();
      assertThat(page2.get(0).path()).isNull();
      assertThat(page2.get(0).content()).isNull();
      assertThat(page2.get(0).layout()).isEqualTo("FAT_HORIZONTAL");
      assertThat(page2.get(0).location()).isEqualTo("LEFT");
      assertThat(page2.get(0).picturedAt()).isNull();
      assertThat(page2.get(0).tagNames()).isEmpty();

      List<PictureInSlot> page3 = pictures.get(2);
      assertThat(page3).hasSize(1);
      assertThat(page3.get(0).pictureId()).isEqualTo(2L);

      List<PictureInSlot> page4 = pictures.get(3);
      assertThat(page4).hasSize(2);
      assertThat(page4.get(0).pictureId()).isNull();
      assertThat(page4.get(1).pictureId()).isNull();
    }
  }
}
