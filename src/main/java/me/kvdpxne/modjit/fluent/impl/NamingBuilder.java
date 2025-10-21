package me.kvdpxne.modjit.fluent.impl;

import me.kvdpxne.modjit.fluent.NamingSteps;
import me.kvdpxne.modjit.util.Buildable;
import me.kvdpxne.modjit.util.Validation;

public class NamingBuilder
  implements
  NamingSteps<NamingBuilder>,
  Buildable<String> {

  protected String name;

  @Override
  public NamingBuilder withName(
    final String name
  ) {
    Validation.requireNotBlank(name, () -> "");
    this.name = name;
    return this;
  }

  @Override
  public String build() {
    return this.name;
  }
}
