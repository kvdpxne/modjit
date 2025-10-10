package me.kvdpxne.modjit.cache.key;

import java.util.Objects;

/**
 * Represents a key used for caching field accessors in the reflection library.
 * <p>
 * This class holds the identifying information for a specific field:
 * the name of the class it belongs to, the field's name, and optionally,
 * the field's type name. It is designed to be used as a key in a map-based
 * cache, providing appropriate implementations of {@link #equals(Object)}
 * and {@link #hashCode()}.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @since 0.1.0
 * @version 0.1.0
 */
public final class FieldKey {

  /**
   * The fully qualified name of the class declaring the field.
   */
  private final String className;

  /**
   * The simple name of the field.
   */
  private final String fieldName;

  /**
   * The fully qualified name of the field's type. Can be {@code null}
   * if the field type was not specified during key creation.
   */
  private final String fieldType;

  /**
   * Constructs a new {@code FieldKey} with the specified class name,
   * field name, and optional field type name.
   *
   * @param className The fully qualified name of the declaring class.
   *                  Must not be {@code null}.
   * @param fieldName The simple name of the field. Must not be {@code null}.
   * @param fieldType The fully qualified name of the field's type.
   *                  Can be {@code null} if the type is not relevant for caching.
   */
  public FieldKey(
    final String className,
    final String fieldName,
    final String fieldType
  ) {
    this.className = className;
    this.fieldName = fieldName;
    this.fieldType = fieldType;
  }

  /**
   * Gets the fully qualified name of the class declaring the field.
   *
   * @return The class name.
   */
  public String getClassName() {
    return this.className;
  }

  /**
   * Gets the simple name of the field.
   *
   * @return The field name.
   */
  public String getFieldName() {
    return this.fieldName;
  }

  /**
   * Gets the fully qualified name of the field's type.
   *
   * @return The field type name, or {@code null} if not specified.
   */
  public String getFieldType() {
    return this.fieldType;
  }

  /**
   * Compares this {@code FieldKey} with another object for equality.
   * Two instances are considered equal if their class names, field names,
   * and field types are all equal according to {@link Objects#equals(Object, Object)}.
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
    final FieldKey fieldKey = (FieldKey) o;
    return Objects.equals(this.className, fieldKey.className)
      && Objects.equals(this.fieldName, fieldKey.fieldName)
      && Objects.equals(this.fieldType, fieldKey.fieldType);
  }

  /**
   * Returns the hash code value for this {@code FieldKey}.
   * The hash code is computed based on the class name, field name, and field type.
   *
   * @return the hash code value for this instance
   */
  @Override
  public int hashCode() {
    return Objects.hash(
      this.className,
      this.fieldName,
      this.fieldType
    );
  }
}
