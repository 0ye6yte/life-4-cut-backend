package com.onebyte.life4cut.fixture;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.onebyte.life4cut.album.domain.Album;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.function.BiConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class AlbumFixtureFactory extends DefaultFixtureFactory<Album> {

  public AlbumFixtureFactory() {}

  @Autowired
  public AlbumFixtureFactory(EntityManager entityManager) {
    super(entityManager);
  }

  public Album make(BiConsumer<Album, ArbitraryBuilder<Album>> builder) {
    return getBuilder(builder).sample();
  }

  public List<Album> makes(int count, BiConsumer<Album, ArbitraryBuilder<Album>> builder) {
    return getBuilder(builder).sampleList(count);
  }

  public Album save(BiConsumer<Album, ArbitraryBuilder<Album>> builder) {
    Album sample = getBuilder(builder).setNull("id").sample();
    entityManager.persist(sample);
    entityManager.clear();

    return sample;
  }

  public List<Album> saves(int count, BiConsumer<Album, ArbitraryBuilder<Album>> builder) {
    List<Album> samples = getBuilder(builder).setNull("id").sampleList(count);
    samples.forEach(entityManager::persist);
    entityManager.clear();

    return samples;
  }

  private ArbitraryBuilder<Album> getBuilder(BiConsumer<Album, ArbitraryBuilder<Album>> builder) {
    return fixtureMonkey.giveMeBuilder(Album.class).thenApply(builder);
  }
}
