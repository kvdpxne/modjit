package me.kvdpxne.modjit.fluent.builder;

import java.util.Collection;
import me.kvdpxne.modjit.Reflection;
import me.kvdpxne.modjit.accessor.MethodInvoker;
import me.kvdpxne.modjit.fluent.ClassSteps;
import me.kvdpxne.modjit.fluent.ModifiersSteps;
import me.kvdpxne.modjit.fluent.NamingSteps;
import me.kvdpxne.modjit.fluent.ParametersSteps;
import me.kvdpxne.modjit.fluent.StrivingSteps;
import me.kvdpxne.modjit.fluent.impl.ModifiersBuilder;
import me.kvdpxne.modjit.fluent.impl.NamingBuilder;
import me.kvdpxne.modjit.fluent.impl.ParametersBuilder;
import me.kvdpxne.modjit.fluent.impl.StrivingBuilder;
import me.kvdpxne.modjit.util.Buildable;

/**
 * 18.10.2025 18:08
 *
 * @author kvdpxne
 * @version 0.1.0
 * @since 0.1.0
 */
public class MethodBuilder
  implements
  ClassSteps<MethodBuilder>,
  NamingSteps<MethodBuilder>,
  ParametersSteps<MethodBuilder>,
  StrivingSteps<MethodBuilder>,
  ModifiersSteps<MethodBuilder>,
  Buildable<MethodInvoker> {

  protected ClassBuilder classBuilder;
  protected NamingBuilder namingBuilder;
  protected ParametersBuilder parametersBuilder;
  protected StrivingBuilder strivingBuilder;
  protected ModifiersBuilder modifiersBuilder;

  public MethodBuilder() {
    this.classBuilder = new ClassBuilder();
    this.namingBuilder = new NamingBuilder();
    this.parametersBuilder = new ParametersBuilder();
    this.strivingBuilder = new StrivingBuilder();
    this.modifiersBuilder = new ModifiersBuilder();
  }

  @Override
  public MethodBuilder withPath(
    final String path
  ) {
    this.classBuilder.withPath(path);
    return this;
  }

  @Override
  public MethodBuilder withClassLoader(
    final ClassLoader classloader
  ) {
    this.classBuilder.withClassLoader(classloader);
    return this;
  }

  @Override
  public MethodBuilder withInitialize(
    final boolean initialize
  ) {
    this.classBuilder.withInitialize(initialize);
    return this;
  }

  @Override
  public MethodBuilder withName(
    final String name
  ) {
    this.namingBuilder.withName(name);
    return this;
  }

  @Override
  public MethodBuilder withParameterTypes(
    final Collection<Class<?>> parameterTypes
  ) {
    this.parametersBuilder.addAll(parameterTypes);
    return this;
  }

  @Override
  public MethodBuilder withParameterType(
    final Class<?> parameterType
  ) {
    this.parametersBuilder.add(parameterType);
    return this;
  }

  @Override
  public MethodBuilder withType(
    final Class<?> type
  ) {
    this.strivingBuilder.withType(type);
    return this;
  }

  @Override
  public MethodBuilder withModifiers(
    final int modifiers
  ) {
    this.modifiersBuilder.withModifiers(modifiers);
    return this;
  }

  @Override
  public MethodInvoker build() {
    return Reflection.getMethod(
      this.classBuilder.build(),
      this.namingBuilder.build(),
      this.parametersBuilder.build(),
      this.strivingBuilder.build(),
      this.modifiersBuilder.build()
    );
  }
}
