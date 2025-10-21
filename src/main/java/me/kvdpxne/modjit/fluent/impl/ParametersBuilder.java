package me.kvdpxne.modjit.fluent.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import me.kvdpxne.modjit.util.Buildable;
import me.kvdpxne.modjit.util.Validation;

public class ParametersBuilder
  implements
  Buildable<Class<?>[]> {

  protected List<Class<?>> classes;

  public ParametersBuilder() {
    this.classes = new ArrayList<>();
  }

  public ParametersBuilder addAll(
    final Collection<Class<?>> classes
  ) {
    Validation.requireNotNull(classes, () -> "");
    this.classes.addAll(classes);
    return this;
  }

  public ParametersBuilder add(
    final Class<?> clazz
  ) {
    Validation.requireNotNull(clazz, () -> "Class type cannot be null.");
    this.classes.add(clazz);
    return this;
  }

  @Override
  public Class<?>[] build() {
    return this.classes.toArray(new Class[0]);
  }
}