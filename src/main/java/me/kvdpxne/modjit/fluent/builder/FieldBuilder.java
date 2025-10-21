package me.kvdpxne.modjit.fluent.builder;

import me.kvdpxne.modjit.Reflection;
import me.kvdpxne.modjit.accessor.FieldAccessor;
import me.kvdpxne.modjit.fluent.ClassSteps;
import me.kvdpxne.modjit.fluent.ModifiersSteps;
import me.kvdpxne.modjit.fluent.NamingSteps;
import me.kvdpxne.modjit.fluent.StrivingSteps;
import me.kvdpxne.modjit.fluent.impl.ModifiersBuilder;
import me.kvdpxne.modjit.fluent.impl.NamingBuilder;
import me.kvdpxne.modjit.fluent.impl.StrivingBuilder;
import me.kvdpxne.modjit.util.Buildable;

/**
 * 18.10.2025 18:12
 *
 * @author kvdpxne
 * @version 0.1.0
 * @since 0.1.0
 */
public class FieldBuilder
  implements
  ClassSteps<FieldBuilder>,
  NamingSteps<FieldBuilder>,
  StrivingSteps<FieldBuilder>,
  ModifiersSteps<FieldBuilder>,
  Buildable<FieldAccessor> {

  protected ClassBuilder classBuilder;
  protected NamingBuilder namingBuilder;
  protected StrivingBuilder strivingBuilder;
  protected ModifiersBuilder modifiersBuilder;

  public FieldBuilder() {
    this.classBuilder = new ClassBuilder();
    this.namingBuilder = new NamingBuilder();
    this.strivingBuilder = new StrivingBuilder();
    this.modifiersBuilder = new ModifiersBuilder();
  }

  @Override
  public FieldBuilder withPath(
    final String path
  ) {
    this.classBuilder.withPath(path);
    return this;
  }

  @Override
  public FieldBuilder withClassLoader(
    final ClassLoader classloader
  ) {
    this.classBuilder.withClassLoader(classloader);
    return this;
  }

  @Override
  public FieldBuilder withInitialize(
    final boolean initialize
  ) {
    this.classBuilder.withInitialize(initialize);
    return this;
  }

  @Override
  public FieldBuilder withName(
    final String name
  ) {
    this.namingBuilder.withName(name);
    return this;
  }

  @Override
  public FieldBuilder withType(
    final Class<?> type
  ) {
    this.strivingBuilder.withType(type);
    return this;
  }

  @Override
  public FieldBuilder withModifiers(
    final int modifiers
  ) {
    this.modifiersBuilder.withModifiers(modifiers);
    return this;
  }

  @Override
  public FieldAccessor build() {
    return Reflection.getField(
      this.classBuilder.build(),
      this.namingBuilder.build(),
      this.strivingBuilder.build(),
      this.modifiersBuilder.build()
    );
  }
}
