package com.onebyte.life4cut.fixture;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.onebyte.life4cut.picture.domain.Picture;
import jakarta.persistence.EntityManager;
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

  @Override
  public ArbitraryBuilder<Picture> getBuilder(
      BiConsumer<Picture, ArbitraryBuilder<Picture>> builder) {
    return fixtureMonkey.giveMeBuilder(Picture.class).thenApply(builder);
  }
}
