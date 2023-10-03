package com.onebyte.life4cut.fixture;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.onebyte.life4cut.album.domain.UserAlbum;
import jakarta.persistence.EntityManager;
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

  @Override
  public ArbitraryBuilder<UserAlbum> getBuilder(
      BiConsumer<UserAlbum, ArbitraryBuilder<UserAlbum>> builder) {
    return fixtureMonkey.giveMeBuilder(UserAlbum.class).thenApply(builder);
  }
}
