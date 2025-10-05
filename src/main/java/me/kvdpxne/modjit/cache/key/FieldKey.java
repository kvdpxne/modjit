package me.kvdpxne.reflection.cache.key;

import java.util.Objects;

public final class FieldKey {

  private final String className;
  private final String fieldName;
  private final String fieldType;

  public FieldKey(
    final String className,
    final String fieldName,
    final String fieldType
  ) {
    this.className = className;
    this.fieldName = fieldName;
    this.fieldType = fieldType;
  }

  public String getClassName() {
    return this.className;
  }

  public String getFieldName() {
    return this.fieldName;
  }

  public String getFieldType() {
    return this.fieldType;
  }

  @Override
  public boolean equals(
    final Object o
  ) {
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    final FieldKey fieldKey = (FieldKey) o;
    return Objects.equals(this.className, fieldKey.className)
      && Objects.equals(this.fieldName, fieldKey.fieldName)
      && Objects.equals(this.fieldType, fieldKey.fieldType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
      this.className,
      this.fieldName,
      this.fieldType
    );
  }
}
