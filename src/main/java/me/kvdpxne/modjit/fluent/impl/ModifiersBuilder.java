package me.kvdpxne.modjit.fluent.impl;

import java.lang.reflect.Modifier;
import me.kvdpxne.modjit.fluent.ModifiersSteps;
import me.kvdpxne.modjit.util.Buildable;
import me.kvdpxne.modjit.util.Validation;


public class ModifiersBuilder
  implements
  ModifiersSteps<ModifiersBuilder>,
  Buildable<Integer> {

  protected int modifiers;

  public ModifiersBuilder() {
    this.modifiers = 0;
  }

  @Override
  public ModifiersBuilder withModifiers(
    final int modifiers
  ) {
    Validation.require(0 == (modifiers & ~Modifier.fieldModifiers()), () -> "Invalid field modifiers specified.");
    this.modifiers = modifiers;
    return this;
  }

  @Override
  public Integer build() {
    return this.modifiers;
  }
}
