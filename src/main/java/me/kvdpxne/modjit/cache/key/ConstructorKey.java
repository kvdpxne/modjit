package me.kvdpxne.modjit.cache.key;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a key for caching constructor accessors within the reflection library.
 * <p>
 * This class encapsulates the identifying attributes of a specific constructor: the fully qualified name of the
 * declaring class, the string representation of its parameter types (if any), and its modifiers. It is designed for use
 * as a key in a map-based cache, providing consistent implementations of {@link #equals(Object)} and
 * {@link #hashCode()}.
 * </p>
 * <p>
 * The key considers all three components when determining equality and hash code, ensuring that constructors with
 * different parameter types or modifiers are treated as distinct cache entries even when declared in the same class.
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
   * This can be {@code null} if the constructor takes no parameters or if parameter types were not specified in the
   * cache lookup.
   */
  private final String[] parameterTypes;

  /**
   * The modifiers of the associated constructor, as defined by {@link java.lang.reflect.Modifier}.
   */
  private final int modifiers;

  /**
   * Constructs a new {@code ConstructorKey} instance with the specified constructor attributes.
   *
   * @param className the fully qualified name of the class declaring the constructor; must not be {@code null}
   * @param parameterTypes an array of strings representing the fully qualified names of the constructor's parameter
   *   types; can be {@code null} if the constructor takes no parameters or if parameter types were not specified in the
   *   cache lookup
   * @param modifiers the modifiers of the constructor, as defined by {@link java.lang.reflect.Modifier}
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
   * Compares this {@code ConstructorKey} with another object for equality.
   * <p>
   * Two instances are considered equal if their class names are equal, their parameter type arrays are deeply equal
   * (using {@link Arrays#deepEquals(Object[], Object[])}), and their modifiers are equal.
   * </p>
   *
   * @param o the object to compare with
   * @return {@code true} if the objects are equal based on class name, parameter types, and modifiers; {@code false}
   *   otherwise
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
   * Returns the hash code value for this {@code ConstructorKey}.
   * <p>
   * The hash code is computed based on the class name, the hash code of the parameter types array (using
   * {@link Arrays#hashCode(Object[])}), and the modifiers. This ensures that constructors with different parameter
   * signatures or modifiers produce different hash codes.
   * </p>
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
