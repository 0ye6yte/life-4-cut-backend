package com.onebyte.life4cut.fixture;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.onebyte.life4cut.picture.domain.PictureTag;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.function.BiConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class PictureTagFixtureFactory extends DefaultFixtureFactory<PictureTag> {

  public PictureTagFixtureFactory() {}

  @Autowired
  public PictureTagFixtureFactory(EntityManager entityManager) {
    super(entityManager);
  }

  public PictureTag make(BiConsumer<PictureTag, ArbitraryBuilder<PictureTag>> builder) {
    return getBuilder(builder).sample();
  }

  public List<PictureTag> makes(
      int count, BiConsumer<PictureTag, ArbitraryBuilder<PictureTag>> builder) {
    return getBuilder(builder).sampleList(count);
  }

  public PictureTag save(BiConsumer<PictureTag, ArbitraryBuilder<PictureTag>> builder) {
    PictureTag sample = getBuilder(builder).setNull("id").sample();
    entityManager.persist(sample);
    entityManager.clear();

    return sample;
  }

  public List<PictureTag> saves(
      int count, BiConsumer<PictureTag, ArbitraryBuilder<PictureTag>> builder) {
    List<PictureTag> samples = getBuilder(builder).setNull("id").sampleList(count);
    samples.forEach(entityManager::persist);
    entityManager.clear();

    return samples;
  }

  private ArbitraryBuilder<PictureTag> getBuilder(
      BiConsumer<PictureTag, ArbitraryBuilder<PictureTag>> builder) {
    return fixtureMonkey.giveMeBuilder(PictureTag.class).thenApply(builder);
  }
}
