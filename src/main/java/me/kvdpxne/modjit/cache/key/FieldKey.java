package me.kvdpxne.modjit.cache.key;

import java.util.Objects;

/**
 * Represents a key for caching field accessors within the reflection library.
 * <p>
 * This class encapsulates the identifying attributes of a specific field: the fully qualified name of the declaring
 * class, the simple name of the field, the fully qualified name of its type (if specified), and its modifiers. It is
 * designed for use as a key in a map-based cache, providing consistent implementations of
 * {@link #equals(java.lang.Object)} and {@link #hashCode()}.
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
   * part of the caching criteria.
   */
  private final String fieldType;

  /**
   * The modifiers of the associated field, as defined by {@link java.lang.reflect.Modifier}.
   */
  private final int modifiers;

  /**
   * Constructs a new {@code FieldKey} instance.
   *
   * @param className The fully qualified name of the class declaring the field. Must not be {@code null}.
   * @param fieldName The simple name of the field. Must not be {@code null}.
   * @param fieldType The fully qualified name of the field's type. Can be {@code null} if the type is not part of the
   *   caching criteria.
   * @param modifiers The modifiers of the field.
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
   * Retrieves the fully qualified name of the class declaring the associated field.
   *
   * @return The class name.
   */
  public String getClassName() {
    return this.className;
  }

  /**
   * Retrieves the simple name of the associated field.
   *
   * @return The field name.
   */
  public String getFieldName() {
    return this.fieldName;
  }

  /**
   * Retrieves the fully qualified name of the type of the associated field.
   *
   * @return The field type name, or {@code null} if not specified.
   */
  public String getFieldType() {
    return this.fieldType;
  }

  /**
   * Retrieves the modifiers of the associated field.
   *
   * @return The modifiers.
   */
  public int getModifiers() {
    return this.modifiers;
  }

  /**
   * Compares this {@code FieldKey} with another object for equality. Two instances are considered equal if their class
   * names, field names, and field types are all equal according to
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
    final FieldKey that = (FieldKey) o;
    return this.modifiers == that.modifiers
      && Objects.equals(this.className, that.className)
      && Objects.equals(this.fieldName, that.fieldName)
      && Objects.equals(this.fieldType, that.fieldType);
  }

  /**
   * Returns the hash code value for this {@code FieldKey}. The hash code is computed based on the class name, field
   * name, and field type.
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
