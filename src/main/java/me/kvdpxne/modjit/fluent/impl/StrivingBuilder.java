package me.kvdpxne.modjit.fluent.impl;

import me.kvdpxne.modjit.fluent.StrivingSteps;
import me.kvdpxne.modjit.util.Buildable;

public class StrivingBuilder
  implements
  StrivingSteps<StrivingBuilder>,
  Buildable<Class<?>> {

  protected Class<?> type;

  @Override
  public StrivingBuilder withType(
    final Class<?> type
  ) {
    this.type = type;
    return this;
  }

  @Override
  public Class<?> build() {
    return this.type;
  }
}
