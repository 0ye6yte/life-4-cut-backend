package com.onebyte.life4cut.fixture;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.onebyte.life4cut.picture.domain.PictureTag;
import jakarta.persistence.EntityManager;
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

  @Override
  public ArbitraryBuilder<PictureTag> getBuilder(
      BiConsumer<PictureTag, ArbitraryBuilder<PictureTag>> builder) {
    return fixtureMonkey.giveMeBuilder(PictureTag.class).thenApply(builder);
  }
}
