package com.onebyte.life4cut.fixture;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.onebyte.life4cut.album.domain.Album;
import jakarta.persistence.EntityManager;
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

  @Override
  public ArbitraryBuilder<Album> getBuilder(BiConsumer<Album, ArbitraryBuilder<Album>> builder) {
    return fixtureMonkey.giveMeBuilder(Album.class).thenApply(builder);
  }
}
