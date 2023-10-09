package com.onebyte.life4cut.fixture;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class DefaultFixtureFactory<T> {

  protected EntityManager entityManager;

  protected final FixtureMonkey fixtureMonkey =
      FixtureMonkey.builder()
          .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
          .build();

  protected DefaultFixtureFactory() {}

  protected DefaultFixtureFactory(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public T make() {
    return make((t, b) -> {});
  }

  public T make(BiConsumer<T, ArbitraryBuilder<T>> builder) {
    return getBuilder(builder).sample();
  }

  public List<T> makes(int count, BiConsumer<T, ArbitraryBuilder<T>> builder) {
    return getBuilder(builder).sampleList(count);
  }

  public T save() {
    return save((t, b) -> {});
  }

  public T save(BiConsumer<T, ArbitraryBuilder<T>> builder) {
    T sample = getBuilder(builder).setNull("id").sample();
    entityManager.persist(sample);
    entityManager.flush();
    entityManager.clear();

    return sample;
  }

  public List<T> saves(int count, BiConsumer<T, ArbitraryBuilder<T>> builder) {
    List<T> samples = getBuilder(builder).setNull("id").sampleList(count);
    samples.forEach(entityManager::persist);
    entityManager.flush();
    entityManager.clear();

    return samples;
  }

  public abstract ArbitraryBuilder<T> getBuilder(BiConsumer<T, ArbitraryBuilder<T>> builder);
}
