package com.onebyte.life4cut.fixture;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.onebyte.life4cut.album.domain.Slot;
import jakarta.persistence.EntityManager;
import java.util.function.BiConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class SlotFixtureFactory extends DefaultFixtureFactory<Slot> {

  public SlotFixtureFactory() {}

  @Autowired
  public SlotFixtureFactory(EntityManager entityManager) {
    super(entityManager);
  }

  @Override
  public ArbitraryBuilder<Slot> getBuilder(BiConsumer<Slot, ArbitraryBuilder<Slot>> builder) {
    return fixtureMonkey.giveMeBuilder(Slot.class).thenApply(builder);
  }
}
