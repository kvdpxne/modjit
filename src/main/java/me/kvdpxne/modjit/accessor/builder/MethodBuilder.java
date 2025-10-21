package me.kvdpxne.modjit.accessor.builder;

import java.lang.reflect.Modifier;
import me.kvdpxne.modjit.Reflection;
import me.kvdpxne.modjit.accessor.MethodInvoker;
import me.kvdpxne.modjit.util.Buildable;
import me.kvdpxne.modjit.util.Validation;

/**
 * 18.10.2025 18:08
 *
 * @author kvdpxne
 * @version 0.1.0
 * @since 0.1.0
 */
public class MethodBuilder
  implements
  Buildable<MethodInvoker> {

  private Class<?> clazz;
  private String methodName;
  private Class<?>[] parameterTypes;
  private Class<?> returnType;
  private int modifiers;

  public MethodBuilder() {
  }

  public MethodBuilder inClass(
    final Class<?> clazz
  ) {
    Validation.requireNotNull(clazz, () -> "Class type cannot be null.");
    this.clazz = clazz;
    return this;
  }

  public MethodBuilder inClass(
    final String clazz
  ) {
    this.clazz = Reflection.getClass(clazz);
    return this;
  }

  public MethodBuilder withMethodName(
    final String methodName
  ) {
    Validation.requireNotBlank(methodName, () -> "Method name cannot be blank.");
    this.methodName = methodName;
    return this;
  }

  public MethodBuilder withParameterTypes(
    final Class<?>[] parameterTypes
  ) {
    Validation.requireNotNull(parameterTypes, () -> "Parameter types cannot be null.");
    this.parameterTypes = parameterTypes;
    return this;
  }

  public MethodBuilder withParameterTypes(
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

  public MethodBuilder withReturnType(
    final Class<?> returnType
  ) {
    Validation.requireNotNull(returnType, () -> "Return type cannot be null.");
    this.returnType = returnType;
    return this;
  }

  public MethodBuilder withReturnType(
    final String returnType
  ) {
    this.returnType = Reflection.getClass(returnType);
    return this;
  }

  public MethodBuilder withModifiers(
    final int modifiers
  ) {
    Validation.require(0 == (modifiers & ~Modifier.methodModifiers()), () -> "Invalid method modifiers specified.");
    this.modifiers = modifiers;
    return this;
  }

  @Override
  public MethodInvoker build() {
    return Reflection.getMethod(
      this.clazz,
      this.methodName,
      this.parameterTypes,
      this.returnType,
      this.modifiers
    );
  }
}
