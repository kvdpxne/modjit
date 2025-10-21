package me.kvdpxne.modjit.accessor.builder;

import java.lang.reflect.Modifier;
import me.kvdpxne.modjit.Reflection;
import me.kvdpxne.modjit.accessor.FieldAccessor;
import me.kvdpxne.modjit.util.Buildable;
import me.kvdpxne.modjit.util.Validation;

/**
 * 18.10.2025 18:12
 *
 * @author kvdpxne
 * @version 0.1.0
 * @since 0.1.0
 */
public class FieldBuilder
  implements
  Buildable<FieldAccessor> {

  private Class<?> clazz;
  private String fieldName;
  private Class<?> fieldType;
  private int modifiers;

  public FieldBuilder() {
  }

  public FieldBuilder inClass(
    final Class<?> clazz
  ) {
    Validation.requireNotNull(clazz, () -> "Class type cannot be null.");
    this.clazz = clazz;
    return this;
  }

  public FieldBuilder inClass(
    final String clazz
  ) {
    this.clazz = Reflection.getClass(clazz);
    return this;
  }

  public FieldBuilder setFieldName(
    final String fieldName
  ) {
    Validation.requireNotBlank(fieldName, () -> "Field name cannot be blank.");
    this.fieldName = fieldName;
    return this;
  }

  public FieldBuilder setFieldType(
    final Class<?> fieldType
  ) {
    Validation.requireNotNull(fieldType, () -> "Field type cannot be null.");
    this.fieldType = fieldType;
    return this;
  }

  public FieldBuilder setFieldType(
    final String fieldType
  ) {
    this.fieldType = Reflection.getClass(fieldType);
    return this;
  }

  public FieldBuilder setModifiers(
    final int modifiers
  ) {
    Validation.require(0 == (modifiers & ~Modifier.fieldModifiers()), () -> "Invalid field modifiers specified.");
    this.modifiers = modifiers;
    return this;
  }

  @Override
  public FieldAccessor build() {
    return Reflection.getField(
      this.clazz,
      this.fieldName,
      this.fieldType,
      this.modifiers
    );
  }
}
