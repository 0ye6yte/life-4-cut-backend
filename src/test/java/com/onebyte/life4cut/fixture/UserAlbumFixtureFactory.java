package com.onebyte.life4cut.fixture;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.onebyte.life4cut.album.domain.UserAlbum;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.function.BiConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class UserAlbumFixtureFactory extends DefaultFixtureFactory<UserAlbum> {

  public UserAlbumFixtureFactory() {}

  @Autowired
  public UserAlbumFixtureFactory(EntityManager entityManager) {
    super(entityManager);
  }

  public UserAlbum make(BiConsumer<UserAlbum, ArbitraryBuilder<UserAlbum>> builder) {
    return getBuilder(builder).sample();
  }

  public List<UserAlbum> makes(
      int count, BiConsumer<UserAlbum, ArbitraryBuilder<UserAlbum>> builder) {
    return getBuilder(builder).sampleList(count);
  }

  public UserAlbum save(BiConsumer<UserAlbum, ArbitraryBuilder<UserAlbum>> builder) {
    UserAlbum sample = getBuilder(builder).setNull("id").sample();
    entityManager.persist(sample);
    entityManager.clear();

    return sample;
  }

  public List<UserAlbum> saves(
      int count, BiConsumer<UserAlbum, ArbitraryBuilder<UserAlbum>> builder) {
    List<UserAlbum> samples = getBuilder(builder).setNull("id").sampleList(count);
    samples.forEach(entityManager::persist);
    entityManager.clear();

    return samples;
  }

  private ArbitraryBuilder<UserAlbum> getBuilder(
      BiConsumer<UserAlbum, ArbitraryBuilder<UserAlbum>> builder) {
    return fixtureMonkey.giveMeBuilder(UserAlbum.class).thenApply(builder);
  }
}
