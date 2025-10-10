package me.kvdpxne.modjit.cache.key;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a key used for caching constructor accessors in the reflection library.
 * <p>
 * This class holds the identifying information for a specific constructor:
 * the name of the class it belongs to and the names of its parameter types.
 * It is designed to be used as a key in a map-based cache, providing appropriate
 * implementations of {@link #equals(Object)} and {@link #hashCode()}.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @since 0.1.0
 * @version 0.1.0
 */
public final class ConstructorKey {

  /**
   * The fully qualified name of the class declaring the constructor.
   */
  private final String className;

  /**
   * An array of strings representing the fully qualified names of the
   * constructor's parameter types. Can be {@code null} if the constructor
   * takes no parameters.
   */
  private final String[] parameterTypes;

  /**
   * Constructs a new {@code ConstructorKey} with the specified class name
   * and parameter type names.
   *
   * @param className      The fully qualified name of the declaring class.
   *                       Must not be {@code null}.
   * @param parameterTypes An array of strings representing the parameter
   *                       type names. Can be {@code null} if the constructor
   *                       takes no parameters.
   */
  public ConstructorKey(
    final String className,
    final String[] parameterTypes
  ) {
    this.className = className;
    this.parameterTypes = parameterTypes;
  }

  /**
   * Gets the fully qualified name of the class declaring the constructor.
   *
   * @return The class name.
   */
  public String getClassName() {
    return this.className;
  }

  /**
   * Gets the array of strings representing the fully qualified names of the
   * constructor's parameter types.
   *
   * @return The parameter type names array. Can be {@code null} if the
   *     constructor takes no parameters.
   */
  public String[] getParameterTypes() {
    return this.parameterTypes;
  }

  /**
   * Compares this {@code ConstructorKey} with another object for equality.
   * Two instances are considered equal if their class names are equal and
   * their parameter type arrays are deeply equal (using {@link Arrays#deepEquals(Object[], Object[])}).
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
    final ConstructorKey that = (ConstructorKey) o;
    return Objects.equals(this.className, that.className)
      && Objects.deepEquals(this.parameterTypes, that.parameterTypes);
  }

  /**
   * Returns the hash code value for this {@code ConstructorKey}.
   * The hash code is computed based on the class name and the hash code
   * of the parameter types array (using {@link Arrays#hashCode(Object[])}).
   *
   * @return the hash code value for this instance
   */
  @Override
  public int hashCode() {
    return Objects.hash(
      this.className,
      Arrays.hashCode(this.parameterTypes)
    );
  }
}
