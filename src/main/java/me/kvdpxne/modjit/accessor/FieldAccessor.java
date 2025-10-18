package me.kvdpxne.modjit.accessor;

/**
 * Defines the contract for accessing and manipulating field values through reflection.
 * <p>
 * This interface abstracts the underlying {@link java.lang.reflect.Field} object, providing a simplified API for
 * reading and writing field values while handling reflection complexities such as accessibility management, type
 * checking, and exception translation. Implementations manage field accessibility and convert reflection-specific
 * exceptions into the library's standardized exception hierarchy.
 * </p>
 * <p>
 * The interface supports both instance fields (non-static) and static fields. For static field operations, the target
 * parameter can be {@code null}. All operations automatically handle accessibility concerns and restore the original
 * accessibility state after completion.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public interface FieldAccessor {

  /**
   * Retrieves the value of the underlying field from the specified target object.
   * <p>
   * This method reads the field's current value, automatically handling accessibility if required. For static fields,
   * the target parameter is ignored and can be {@code null}. For instance fields, the target object must be an instance
   * of the class declaring the field.
   * </p>
   * <p>
   * The field is temporarily made accessible if necessary, with its original accessibility state restored after the
   * operation completes, regardless of success or failure.
   * </p>
   *
   * @param target the object from which to read the field value; for static fields, this parameter can be
   *   {@code null}; for instance fields, must be a non-null instance of the declaring class
   * @return the current value of the field in the specified object; may be {@code null} if the field contains a null
   *   value or is of a primitive type (which will be returned as wrapped type)
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the underlying field is not accessible and
   *   cannot be made accessible due to security restrictions imposed by the Java runtime environment or security
   *   manager
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs during field access, such as the
   *   target object being incompatible with the field's declaring class
   * @throws java.lang.NullPointerException if the field is an instance field and the target object is {@code null}
   * @throws java.lang.IllegalArgumentException if the target object is not an instance of the class or interface
   *   declaring the underlying field
   */
  Object get(
    Object target
  );

  /**
   * Retrieves the value of a static field.
   * <p>
   * This is a convenience method equivalent to calling {@link #get(Object)} with a {@code null} target. It provides
   * simplified access to static field values without requiring a target object instance.
   * </p>
   *
   * @return the current value of the static field; may be {@code null} if the field contains a null value or is of a
   *   primitive type (which will be returned as wrapped type)
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the underlying field is not accessible and
   *   cannot be made accessible due to security restrictions
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs during field access
   */
  default Object get() {
    return this.get(null);
  }

  /**
   * Sets the value of the underlying field on the specified target object.
   * <p>
   * This method assigns a new value to the field, automatically handling accessibility if required. For static fields,
   * the target parameter is ignored and can be {@code null}. For instance fields, the target object must be an instance
   * of the class declaring the field.
   * </p>
   * <p>
   * The value must be assignment-compatible with the field's declared type. Primitive values should be provided as
   * their corresponding wrapper types. The field is temporarily made accessible if necessary, with its original
   * accessibility state restored after operation completion.
   * </p>
   *
   * @param target the object on which to set the field value; for static fields, this parameter can be {@code null};
   *   for instance fields, must be a non-null instance of the declaring class
   * @param value the new value to assign to the field; must be assignment-compatible with the field's declared type;
   *   may be {@code null} for reference types
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the underlying field is not accessible and
   *   cannot be made accessible due to security restrictions
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs during field assignment, such as the
   *   target object being incompatible with the field's declaring class
   * @throws java.lang.NullPointerException if the field is an instance field and the target object is {@code null}
   * @throws java.lang.IllegalArgumentException if the target object is not an instance of the class or interface
   *   declaring the underlying field, or if the value type is not assignment-compatible with the field's declared type
   */
  void set(
    Object target,
    Object value
  );

  /**
   * Sets the value of a static field.
   * <p>
   * This is a convenience method equivalent to calling {@link #set(Object, Object)} with a {@code null} target and the
   * specified value. It provides simplified assignment to static fields without requiring a target object instance.
   * </p>
   *
   * @param value the new value to assign to the static field; must be assignment-compatible with the field's declared
   *   type; may be {@code null} for reference types
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the underlying field is not accessible and
   *   cannot be made accessible due to security restrictions
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs during field assignment
   * @throws java.lang.IllegalArgumentException if the value type is not assignment-compatible with the field's
   *   declared type
   */
  default void set(
    Object value
  ) {
    this.set(null, value);
  }

  /**
   * Sets the value of the underlying field to {@code null} on the specified target object.
   * <p>
   * This is a convenience method equivalent to calling {@link #set(Object, Object)} with the specified target and a
   * {@code null} value. It provides explicit null assignment semantics for clarity.
   * </p>
   *
   * @param target the object on which to set the field value to {@code null}; for static fields, this parameter can
   *   be {@code null}; for instance fields, must be a non-null instance of the declaring class
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the underlying field is not accessible and
   *   cannot be made accessible due to security restrictions
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs during field assignment
   * @throws java.lang.NullPointerException if the field is an instance field and the target object is {@code null}
   * @throws java.lang.IllegalArgumentException if the target object is not an instance of the class or interface
   *   declaring the underlying field, or if the field is of primitive type (which cannot be set to {@code null})
   */
  default void unset(
    Object target
  ) {
    this.set(target, null);
  }

  /**
   * Sets the value of a static field to {@code null}.
   * <p>
   * This is a convenience method equivalent to calling {@link #set(Object, Object)} with a {@code null} target and a
   * {@code null} value. It provides explicit null assignment for static fields.
   * </p>
   *
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the underlying field is not accessible and
   *   cannot be made accessible due to security restrictions
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs during field assignment
   * @throws java.lang.IllegalArgumentException if the field is of primitive type (which cannot be set to
   *   {@code null})
   */
  default void unset() {
    this.set(null, null);
  }
}
