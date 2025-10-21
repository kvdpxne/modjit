package me.kvdpxne.modjit.fluent.builder;

import java.util.Collection;
import me.kvdpxne.modjit.Reflection;
import me.kvdpxne.modjit.accessor.ConstructorInitializer;
import me.kvdpxne.modjit.fluent.ClassSteps;
import me.kvdpxne.modjit.fluent.ModifiersSteps;
import me.kvdpxne.modjit.fluent.ParametersSteps;
import me.kvdpxne.modjit.fluent.impl.ModifiersBuilder;
import me.kvdpxne.modjit.fluent.impl.ParametersBuilder;
import me.kvdpxne.modjit.util.Buildable;

/**
 * 18.10.2025 18:12
 *
 * @author kvdpxne
 * @version 0.1.0
 * @since 0.1.0
 */
public class ConstructorBuilder
  implements
  ClassSteps<ConstructorBuilder>,
  ParametersSteps<ConstructorBuilder>,
  ModifiersSteps<ConstructorBuilder>,
  Buildable<ConstructorInitializer> {

  protected ClassBuilder classBuilder;
  protected ParametersBuilder parametersBuilder;
  protected ModifiersBuilder modifiersBuilder;

  public ConstructorBuilder() {
    this.classBuilder = new ClassBuilder();
    this.parametersBuilder = new ParametersBuilder();
    this.modifiersBuilder = new ModifiersBuilder();
  }

  @Override
  public ConstructorBuilder withPath(
    final String path
  ) {
    this.classBuilder.withPath(path);
    return this;
  }

  @Override
  public ConstructorBuilder withClassLoader(
    final ClassLoader classloader
  ) {
    this.classBuilder.withClassLoader(classloader);
    return this;
  }

  @Override
  public ConstructorBuilder withInitialize(
    final boolean initialize
  ) {
    this.classBuilder.withInitialize(initialize);
    return this;
  }

  @Override
  public ConstructorBuilder withParameterTypes(
    final Collection<Class<?>> parameterTypes
  ) {
    this.parametersBuilder.addAll(parameterTypes);
    return this;
  }

  @Override
  public ConstructorBuilder withParameterType(
    final Class<?> parameterType
  ) {
    this.parametersBuilder.add(parameterType);
    return this;
  }

  @Override
  public ConstructorBuilder withModifiers(
    final int modifiers
  ) {
    this.modifiersBuilder.withModifiers(modifiers);
    return this;
  }

  @Override
  public ConstructorInitializer build() {
    return Reflection.getConstructor(
      this.classBuilder.build(),
      this.parametersBuilder.build(),
      this.modifiersBuilder.build()
    );
  }
}
