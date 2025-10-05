package me.kvdpxne.reflection.cache.key;

import java.util.Arrays;
import java.util.Objects;

public final class MethodKey {

  private final String className;
  private final String methodName;
  private final String[] parameterTypes;
  private final String returnType;

  public MethodKey(
    final String className,
    final String methodName,
    final String[] parameterTypes,
    final String returnType
  ) {
    this.className = className;
    this.methodName = methodName;
    this.parameterTypes = parameterTypes;
    this.returnType = returnType;
  }

  public String getClassName() {
    return this.className;
  }

  public String getMethodName() {
    return this.methodName;
  }

  public String[] getParameterTypes() {
    return this.parameterTypes;
  }

  public String getReturnType() {
    return this.returnType;
  }

  @Override
  public boolean equals(
    final Object o
  ) {
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    final MethodKey methodKey = (MethodKey) o;
    return Objects.equals(this.className, methodKey.className)
      && Objects.equals(this.methodName, methodKey.methodName)
      && Objects.deepEquals(this.parameterTypes, methodKey.parameterTypes)
      && Objects.equals(this.returnType, methodKey.returnType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
      this.className,
      this.methodName,
      Arrays.hashCode(this.parameterTypes),
      this.returnType
    );
  }
}
