package com.onebyte.life4cut.fixture;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.onebyte.life4cut.album.domain.Slot;
import jakarta.persistence.EntityManager;
import java.util.List;
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

  public Slot make(BiConsumer<Slot, ArbitraryBuilder<Slot>> builder) {
    return getBuilder(builder).sample();
  }

  public List<Slot> makes(int count, BiConsumer<Slot, ArbitraryBuilder<Slot>> builder) {
    return getBuilder(builder).sampleList(count);
  }

  public Slot save(BiConsumer<Slot, ArbitraryBuilder<Slot>> builder) {
    Slot sample = getBuilder(builder).setNull("id").sample();
    entityManager.persist(sample);
    entityManager.clear();

    return sample;
  }

  public List<Slot> saves(int count, BiConsumer<Slot, ArbitraryBuilder<Slot>> builder) {
    List<Slot> samples = getBuilder(builder).setNull("id").sampleList(count);
    samples.forEach(entityManager::persist);
    entityManager.clear();

    return samples;
  }

  private ArbitraryBuilder<Slot> getBuilder(BiConsumer<Slot, ArbitraryBuilder<Slot>> builder) {
    return fixtureMonkey.giveMeBuilder(Slot.class).thenApply(builder);
  }
}
