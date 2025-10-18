package me.kvdpxne.modjit.cache.key;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a key for caching method accessors within the reflection library.
 * <p>
 * This class encapsulates the identifying attributes of a specific method: the fully qualified name of the declaring
 * class, the simple name of the method, an array of strings representing the fully qualified names of its parameter
 * types, the fully qualified name of its return type, and its modifiers. It is designed for use as a key in a map-based
 * cache, providing consistent implementations of {@link #equals(Object)} and {@link #hashCode()}.
 * </p>
 * <p>
 * The key considers all five components when determining equality and hash code, ensuring that methods with different
 * names, parameter signatures, return types, or modifiers are treated as distinct cache entries even when declared in
 * the same class. This allows for precise method lookup based on various combinations of search criteria.
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
   * can be {@code null} if parameter types were not part of the caching criteria, indicating that parameter type
   * matching was not required for the cache lookup.
   */
  private final String[] parameterTypes;

  /**
   * The fully qualified name of the return type of the associated method. This can be {@code null} if the return type
   * was not part of the caching criteria, indicating that return type matching was not required for the cache lookup.
   */
  private final String returnType;

  /**
   * The modifiers of the associated method, as defined by {@link java.lang.reflect.Modifier}.
   */
  private final int modifiers;

  /**
   * Constructs a new {@code MethodKey} instance with the specified method attributes.
   *
   * @param className the fully qualified name of the class declaring the method; must not be {@code null}
   * @param methodName the simple name of the method; must not be {@code null}
   * @param parameterTypes an array of strings representing the fully qualified names of the method's parameter types;
   *   can be {@code null} if parameter types are not part of the caching criteria
   * @param returnType the fully qualified name of the method's return type; can be {@code null} if the return type is
   *   not part of the caching criteria
   * @param modifiers the modifiers of the method, as defined by {@link java.lang.reflect.Modifier}
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
   * Compares this {@code MethodKey} with another object for equality.
   * <p>
   * Two instances are considered equal if their class names, method names, parameter type arrays (compared deeply using
   * {@link Arrays#deepEquals(Object[], Object[])}), return types, and modifiers are all equal according to
   * {@link Objects#equals(Object, Object)}. A {@code null} parameter types array or return type is considered equal
   * only to another {@code null} value.
   * </p>
   *
   * @param o the object to compare with
   * @return {@code true} if the objects are equal based on class name, method name, parameter types, return type, and
   *   modifiers; {@code false} otherwise
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
   * Returns the hash code value for this {@code MethodKey}.
   * <p>
   * The hash code is computed based on the class name, method name, the hash code of the parameter types array (using
   * {@link Arrays#hashCode(Object[])}), the return type, and the modifiers using {@link Objects#hash(Object...)}. This
   * ensures that methods with different attributes produce different hash codes, making them suitable for use as keys
   * in hash-based collections.
   * </p>
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
