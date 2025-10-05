package me.kvdpxne.reflection.cache.key;

import java.util.Arrays;
import java.util.Objects;

public final class ConstructorKey {

  private final String className;
  private final String[] parameterTypes;

  public ConstructorKey(final String className, final String[] parameterTypes) {
    this.className = className;
    this.parameterTypes = parameterTypes;
  }

  public String getClassName() {
    return this.className;
  }

  public String[] getParameterTypes() {
    return this.parameterTypes;
  }

  @Override
  public boolean equals(
    final Object o
  ) {
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    final ConstructorKey that = (ConstructorKey) o;
    return Objects.equals(this.className, that.className)
      && Objects.deepEquals(this.parameterTypes, that.parameterTypes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
      this.className,
      Arrays.hashCode(this.parameterTypes)
    );
  }
}
