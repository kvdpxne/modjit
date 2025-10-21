package me.kvdpxne.modjit.fluent.builder;

import me.kvdpxne.modjit.Reflection;
import me.kvdpxne.modjit.fluent.ClassSteps;
import me.kvdpxne.modjit.util.Buildable;

/**
 * 21.10.2025 19:18
 *
 * @author kvdpxne
 * @version 0.1.0
 * @since 0.1.0
 */
public class ClassBuilder
  implements
  ClassSteps<ClassBuilder>,
  Buildable<Class<?>> {

  protected String path;
  protected ClassLoader classLoader;
  protected boolean initialize;

  public ClassBuilder() {
    this.path = null;
    this.classLoader = null;
    this.initialize = true;
  }

  public ClassBuilder withPath(
    final String path
  ) {
    this.path = path;
    return this;
  }

  public ClassBuilder withClassLoader(
    final ClassLoader classLoader
  ) {
    this.classLoader = classLoader;
    return this;
  }

  public ClassBuilder withInitialize(
    final boolean initialize
  ) {
    this.initialize = initialize;
    return this;
  }

  @Override
  public Class<?> build() {
    return Reflection.getClass(
      this.path,
      this.classLoader,
      this.initialize
    );
  }
}
