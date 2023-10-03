package com.onebyte.life4cut.fixture;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.onebyte.life4cut.picture.domain.Picture;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.function.BiConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class PictureFixtureFactory extends DefaultFixtureFactory<Picture> {

  public PictureFixtureFactory() {}

  @Autowired
  public PictureFixtureFactory(EntityManager entityManager) {
    super(entityManager);
  }

  public Picture make(BiConsumer<Picture, ArbitraryBuilder<Picture>> builder) {
    return getBuilder(builder).sample();
  }

  public List<Picture> makes(int count, BiConsumer<Picture, ArbitraryBuilder<Picture>> builder) {
    return getBuilder(builder).sampleList(count);
  }

  public Picture save(BiConsumer<Picture, ArbitraryBuilder<Picture>> builder) {
    Picture sample = getBuilder(builder).setNull("id").sample();
    entityManager.persist(sample);
    entityManager.clear();

    return sample;
  }

  public List<Picture> saves(int count, BiConsumer<Picture, ArbitraryBuilder<Picture>> builder) {
    List<Picture> samples = getBuilder(builder).setNull("id").sampleList(count);
    samples.forEach(entityManager::persist);
    entityManager.clear();

    return samples;
  }

  private ArbitraryBuilder<Picture> getBuilder(
      BiConsumer<Picture, ArbitraryBuilder<Picture>> builder) {
    return fixtureMonkey.giveMeBuilder(Picture.class).thenApply(builder);
  }
}
