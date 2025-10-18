package me.kvdpxne.modjit.cache.key;

import java.util.Objects;

/**
 * Represents a key for caching field accessors within the reflection library.
 * <p>
 * This class encapsulates the identifying attributes of a specific field: the fully qualified name of the declaring
 * class, the simple name of the field, the fully qualified name of its type (if specified), and its modifiers. It is
 * designed for use as a key in a map-based cache, providing consistent implementations of {@link #equals(Object)} and
 * {@link #hashCode()}.
 * </p>
 * <p>
 * The key considers all four components when determining equality and hash code, ensuring that fields with different
 * names, types, or modifiers are treated as distinct cache entries even when declared in the same class. This allows
 * for precise field lookup based on various combinations of search criteria.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public final class FieldKey {

  /**
   * The fully qualified name of the class declaring the associated field.
   */
  private final String className;

  /**
   * The simple name of the associated field.
   */
  private final String fieldName;

  /**
   * The fully qualified name of the type of the associated field. This can be {@code null} if the field type was not
   * part of the caching criteria, indicating that field type matching was not required for the cache lookup.
   */
  private final String fieldType;

  /**
   * The modifiers of the associated field, as defined by {@link java.lang.reflect.Modifier}.
   */
  private final int modifiers;

  /**
   * Constructs a new {@code FieldKey} instance with the specified field attributes.
   *
   * @param className the fully qualified name of the class declaring the field; must not be {@code null}
   * @param fieldName the simple name of the field; must not be {@code null}
   * @param fieldType the fully qualified name of the field's type; can be {@code null} if the type is not part of the
   *   caching criteria
   * @param modifiers the modifiers of the field, as defined by {@link java.lang.reflect.Modifier}
   */
  public FieldKey(
    final String className,
    final String fieldName,
    final String fieldType,
    final int modifiers
  ) {
    this.className = className;
    this.fieldName = fieldName;
    this.fieldType = fieldType;
    this.modifiers = modifiers;
  }

  /**
   * Compares this {@code FieldKey} with another object for equality.
   * <p>
   * Two instances are considered equal if their class names, field names, field types, and modifiers are all equal
   * according to {@link Objects#equals(Object, Object)}. A {@code null} field type is considered equal only to another
   * {@code null} field type.
   * </p>
   *
   * @param o the object to compare with
   * @return {@code true} if the objects are equal based on class name, field name, field type, and modifiers;
   *   {@code false} otherwise
   */
  @Override
  public boolean equals(
    final Object o
  ) {
    if (null == o || this.getClass() != o.getClass()) {
      return false;
    }
    final FieldKey that = (FieldKey) o;
    return this.modifiers == that.modifiers
      && Objects.equals(this.className, that.className)
      && Objects.equals(this.fieldName, that.fieldName)
      && Objects.equals(this.fieldType, that.fieldType);
  }

  /**
   * Returns the hash code value for this {@code FieldKey}.
   * <p>
   * The hash code is computed based on the class name, field name, field type, and modifiers using
   * {@link Objects#hash(Object...)}. This ensures that fields with different attributes produce different hash codes,
   * making them suitable for use as keys in hash-based collections.
   * </p>
   *
   * @return the hash code value for this instance
   */
  @Override
  public int hashCode() {
    return Objects.hash(
      this.className,
      this.fieldName,
      this.fieldType,
      this.modifiers
    );
  }
}
