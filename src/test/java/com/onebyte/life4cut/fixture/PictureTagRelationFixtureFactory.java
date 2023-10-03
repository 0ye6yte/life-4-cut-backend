package com.onebyte.life4cut.fixture;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.onebyte.life4cut.picture.domain.PictureTagRelation;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.function.BiConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class PictureTagRelationFixtureFactory extends DefaultFixtureFactory<PictureTagRelation> {

  public PictureTagRelationFixtureFactory() {}

  @Autowired
  public PictureTagRelationFixtureFactory(EntityManager entityManager) {
    super(entityManager);
  }

  public PictureTagRelation make(
      BiConsumer<PictureTagRelation, ArbitraryBuilder<PictureTagRelation>> builder) {
    return getBuilder(builder).sample();
  }

  public List<PictureTagRelation> makes(
      int count, BiConsumer<PictureTagRelation, ArbitraryBuilder<PictureTagRelation>> builder) {
    return getBuilder(builder).sampleList(count);
  }

  public PictureTagRelation save(
      BiConsumer<PictureTagRelation, ArbitraryBuilder<PictureTagRelation>> builder) {
    PictureTagRelation sample = getBuilder(builder).setNull("id").sample();
    entityManager.persist(sample);
    entityManager.clear();

    return sample;
  }

  public List<PictureTagRelation> saves(
      int count, BiConsumer<PictureTagRelation, ArbitraryBuilder<PictureTagRelation>> builder) {
    List<PictureTagRelation> samples = getBuilder(builder).setNull("id").sampleList(count);
    samples.forEach(entityManager::persist);
    entityManager.clear();

    return samples;
  }

  private ArbitraryBuilder<PictureTagRelation> getBuilder(
      BiConsumer<PictureTagRelation, ArbitraryBuilder<PictureTagRelation>> builder) {
    return fixtureMonkey.giveMeBuilder(PictureTagRelation.class).thenApply(builder);
  }
}
