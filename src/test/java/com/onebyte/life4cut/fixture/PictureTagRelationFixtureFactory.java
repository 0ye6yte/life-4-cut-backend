package com.onebyte.life4cut.fixture;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.onebyte.life4cut.picture.domain.PictureTagRelation;
import jakarta.persistence.EntityManager;
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

  @Override
  public ArbitraryBuilder<PictureTagRelation> getBuilder(
      BiConsumer<PictureTagRelation, ArbitraryBuilder<PictureTagRelation>> builder) {
    return fixtureMonkey.giveMeBuilder(PictureTagRelation.class).thenApply(builder);
  }
}
