package me.kvdpxne.modjit.accessor.builder;

import java.lang.reflect.Modifier;
import me.kvdpxne.modjit.Reflection;
import me.kvdpxne.modjit.accessor.ConstructorInitializer;
import me.kvdpxne.modjit.util.Buildable;
import me.kvdpxne.modjit.util.Validation;

/**
 * 18.10.2025 18:12
 *
 * @author kvdpxne
 * @version 0.1.0
 * @since 0.1.0
 */
public final class ConstructorBuilder
  implements
  Buildable<ConstructorInitializer> {

  private Class<?> clazz;
  private Class<?>[] parameterTypes;
  private int modifiers;

  public ConstructorBuilder() {
  }

  public ConstructorBuilder inClass(
    final Class<?> clazz
  ) {
    Validation.requireNotNull(clazz, () -> "Class type cannot be null.");
    this.clazz = clazz;
    return this;
  }

  public ConstructorBuilder inClass(
    final String clazz
  ) {
    this.clazz = Reflection.getClass(clazz);
    return this;
  }

  public ConstructorBuilder withParameterTypes(
    final Class<?>[] parameterTypes
  ) {
    Validation.requireNotNull(parameterTypes, () -> "Parameter types cannot be null.");
    this.parameterTypes = parameterTypes;
    return this;
  }

  public ConstructorBuilder withParameterTypes(
    final String[] parameterTypes
  ) {
    Validation.requireNotNull(parameterTypes, () -> "Parameter types cannot be null.");
    final Class<?>[] parameterTypesArray = new Class[parameterTypes.length];
    for (int i = 0; parameterTypes.length > i; ++i) {
      parameterTypesArray[i] = Reflection.getClass(parameterTypes[i]);
    }
    this.parameterTypes = parameterTypesArray;
    return this;
  }

  public ConstructorBuilder withModifiers(
    final int modifiers
  ) {
    Validation.require(0 == (modifiers & ~Modifier.constructorModifiers()), () -> "Invalid constructor modifiers specified.");
    this.modifiers = modifiers;
    return this;
  }

  @Override
  public ConstructorInitializer build() {
    return Reflection.getConstructor(
      this.clazz,
      this.parameterTypes,
      this.modifiers
    );
  }
}
