package me.kvdpxne.modjit.cache.key;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a key used for caching method invokers in the reflection library.
 * <p>
 * This class holds the identifying information for a specific method:
 * the name of the class it belongs to, the method's name, the names of its
 * parameter types, and the name of its return type. It is designed to be used
 * as a key in a map-based cache, providing appropriate implementations of
 * {@link #equals(Object)} and {@link #hashCode()}.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public final class MethodKey {

  /**
   * The fully qualified name of the class declaring the method.
   */
  private final String className;

  /**
   * The simple name of the method.
   */
  private final String methodName;

  /**
   * An array of strings representing the fully qualified names of the
   * method's parameter types. Can be {@code null} if parameter types
   * are not specified or relevant for caching.
   */
  private final String[] parameterTypes;

  /**
   * The fully qualified name of the method's return type. Can be {@code null}
   * if the return type was not specified during key creation.
   */
  private final String returnType;

  /**
   * Constructs a new {@code MethodKey} with the specified class name,
   * method name, parameter type names, and return type name.
   *
   * @param className      The fully qualified name of the declaring class.
   *                       Must not be {@code null}.
   * @param methodName     The simple name of the method. Must not be {@code null}.
   * @param parameterTypes An array of strings representing the parameter
   *                       type names. Can be {@code null} if parameter types
   *                       are not relevant for caching.
   * @param returnType     The fully qualified name of the method's return type.
   *                       Can be {@code null} if the return type is not relevant
   *                       for caching.
   */
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

  /**
   * Gets the fully qualified name of the class declaring the method.
   *
   * @return The class name.
   */
  public String getClassName() {
    return this.className;
  }

  /**
   * Gets the simple name of the method.
   *
   * @return The method name.
   */
  public String getMethodName() {
    return this.methodName;
  }

  /**
   * Gets the array of strings representing the fully qualified names of the
   * method's parameter types.
   *
   * @return The parameter type names array. Can be {@code null} if parameter
   * types are not specified.
   */
  public String[] getParameterTypes() {
    return this.parameterTypes;
  }

  /**
   * Gets the fully qualified name of the method's return type.
   *
   * @return The return type name, or {@code null} if not specified.
   */
  public String getReturnType() {
    return this.returnType;
  }

  /**
   * Compares this {@code MethodKey} with another object for equality.
   * Two instances are considered equal if their class names, method names,
   * parameter type arrays (compared deeply), and return types are all equal
   * according to {@link Objects#equals(Object, Object)}.
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
    final MethodKey methodKey = (MethodKey) o;
    return Objects.equals(this.className, methodKey.className)
      && Objects.equals(this.methodName, methodKey.methodName)
      && Objects.deepEquals(this.parameterTypes, methodKey.parameterTypes)
      && Objects.equals(this.returnType, methodKey.returnType);
  }

  /**
   * Returns the hash code value for this {@code MethodKey}.
   * The hash code is computed based on the class name, method name,
   * the hash code of the parameter types array (using {@link Arrays#hashCode(Object[])}),
   * and the return type.
   *
   * @return the hash code value for this instance
   */
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
