package me.kvdpxne.modjit.cache.key;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a key for caching method accessors within the reflection library.
 * <p>
 * This class encapsulates the identifying attributes of a specific method: the fully qualified name of the declaring
 * class, the simple name of the method, an array of strings representing the fully qualified names of its parameter
 * types, the fully qualified name of its return type, and its modifiers. It is designed for use as a key in a map-based
 * cache, providing consistent implementations of {@link #equals(java.lang.Object)} and {@link #hashCode()}.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public final class MethodKey {

  /**
   * The fully qualified name of the class declaring the associated method.
   */
  private final String className;

  /**
   * The simple name of the associated method.
   */
  private final String methodName;

  /**
   * An array of strings representing the fully qualified names of the parameter types for the associated method. This
   * can be {@code null} if parameter types were not part of the caching criteria.
   */
  private final String[] parameterTypes;

  /**
   * The fully qualified name of the return type of the associated method. This can be {@code null} if the return type
   * was not part of the caching criteria.
   */
  private final String returnType;

  /**
   * The modifiers of the associated method, as defined by {@link java.lang.reflect.Modifier}.
   */
  private final int modifiers;

  /**
   * Constructs a new {@code MethodKey} instance.
   *
   * @param className The fully qualified name of the class declaring the method. Must not be {@code null}.
   * @param methodName The simple name of the method. Must not be {@code null}.
   * @param parameterTypes An array of strings representing the fully qualified names of the method's parameter types.
   *   Can be {@code null} if parameter types are not part of the caching criteria.
   * @param returnType The fully qualified name of the method's return type. Can be {@code null} if the return type is
   *   not part of the caching criteria.
   * @param modifiers The modifiers of the method.
   */
  public MethodKey(
    final String className,
    final String methodName,
    final String[] parameterTypes,
    final String returnType,
    final int modifiers
  ) {
    this.className = className;
    this.methodName = methodName;
    this.parameterTypes = parameterTypes;
    this.returnType = returnType;
    this.modifiers = modifiers;
  }

  /**
   * Compares this {@code MethodKey} with another object for equality. Two instances are considered equal if their class
   * names, method names, parameter type arrays (compared deeply), and return types are all equal according to
   * {@link java.util.Objects#equals(java.lang.Object, java.lang.Object)}.
   *
   * @param o the object to compare with
   * @return {@code true} if the objects are equal, {@code false} otherwise
   */
  @Override
  public boolean equals(
    final Object o
  ) {
    if (null == o || this.getClass() != o.getClass()) {
      return false;
    }
    final MethodKey that = (MethodKey) o;
    return this.modifiers == that.modifiers
      && Objects.equals(this.className, that.className)
      && Objects.equals(this.methodName, that.methodName)
      && Objects.deepEquals(this.parameterTypes, that.parameterTypes)
      && Objects.equals(this.returnType, that.returnType);
  }

  /**
   * Returns the hash code value for this {@code MethodKey}. The hash code is computed based on the class name, method
   * name, the hash code of the parameter types array (using {@link java.util.Arrays#hashCode(java.lang.Object[])}), and
   * the return type.
   *
   * @return the hash code value for this instance
   */
  @Override
  public int hashCode() {
    return Objects.hash(
      this.className,
      this.methodName,
      Arrays.hashCode(this.parameterTypes),
      this.returnType,
      this.modifiers
    );
  }
}
