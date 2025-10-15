package me.kvdpxne.modjit.cache.key;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a key for caching constructor accessors within the reflection library.
 * <p>
 * This class encapsulates the identifying attributes of a specific constructor: the fully qualified name of the
 * declaring class, the string representation of its parameter types (if any), and its modifiers. It is designed for use
 * as a key in a map-based cache, providing consistent implementations of {@link #equals(java.lang.Object)} and
 * {@link #hashCode()}.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public final class ConstructorKey {

  /**
   * The fully qualified name of the class declaring the associated constructor.
   */
  private final String className;

  /**
   * An array of strings representing the fully qualified names of the parameter types for the associated constructor.
   * This can be {@code null} if the constructor takes no parameters.
   */
  private final String[] parameterTypes;

  /**
   * The modifiers of the associated constructor, as defined by {@link java.lang.reflect.Modifier}.
   */
  private final int modifiers;

  /**
   * Constructs a new {@code ConstructorKey} instance.
   *
   * @param className The fully qualified name of the class declaring the constructor. Must not be {@code null}.
   * @param parameterTypes An array of strings representing the fully qualified names of the constructor's parameter
   *   types. Can be {@code null} if the constructor takes no parameters.
   * @param modifiers The modifiers of the constructor.
   */
  public ConstructorKey(
    final String className,
    final String[] parameterTypes,
    final int modifiers
  ) {
    this.className = className;
    this.parameterTypes = parameterTypes;
    this.modifiers = modifiers;
  }

  /**
   * Retrieves the fully qualified name of the class declaring the associated constructor.
   *
   * @return The class name.
   */
  public String getClassName() {
    return this.className;
  }

  /**
   * Retrieves the array of strings representing the fully qualified names of the associated constructor's parameter
   * types.
   *
   * @return The parameter type names array. Can be {@code null} if the constructor takes no parameters.
   */
  public String[] getParameterTypes() {
    return this.parameterTypes;
  }

  /**
   * Retrieves the modifiers of the associated constructor.
   *
   * @return The modifiers.
   */
  public int getModifiers() {
    return this.modifiers;
  }

  /**
   * Compares this {@code ConstructorKey} with another object for equality. Two instances are considered equal if their
   * class names are equal and their parameter type arrays are deeply equal (using
   * {@link java.util.Arrays#deepEquals(java.lang.Object[], java.lang.Object[])}).
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
    return this.modifiers == that.modifiers
      && Objects.equals(this.className, that.className)
      && Objects.deepEquals(this.parameterTypes, that.parameterTypes);
  }

  /**
   * Returns the hash code value for this {@code ConstructorKey}. The hash code is computed based on the class name and
   * the hash code of the parameter types array (using {@link java.util.Arrays#hashCode(java.lang.Object[])}).
   *
   * @return the hash code value for this instance
   */
  @Override
  public int hashCode() {
    return Objects.hash(
      this.className,
      Arrays.hashCode(this.parameterTypes),
      this.modifiers
    );
  }
}
