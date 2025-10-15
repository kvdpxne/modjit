package me.kvdpxne.modjit.acessor;

/**
 * Defines the contract for accessing fields on objects using reflection. Implementations of this interface handle
 * getting and setting field values, abstracting the underlying {@link java.lang.reflect.Field} object.
 * <p>
 * This interface provides methods to get the value of a field from a target object and to set the value of a field on a
 * target object. A default method is provided for setting a field's value to {@code null}.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public interface FieldAccessor {

  /**
   * Gets the value of the underlying field from the specified target object.
   *
   * @param target The object from which to get the field's value. For static fields, this parameter can be
   *   {@code null}.
   * @return The value of the field in the specified object.
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the underlying field is not accessible and
   *   cannot be made accessible.
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs during the field access, such as a
   *   type mismatch or an illegal access error.
   */
  Object get(
    Object target
  );

  /**
   * Sets the value of the underlying field on the specified target object.
   *
   * @param target The object on which to set the field's value. For static fields, this parameter can be
   *   {@code null}.
   * @param value The new value to assign to the field.
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the underlying field is not accessible and
   *   cannot be made accessible.
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs during the field setting, such as a
   *   type mismatch or an illegal access error.
   */
  void set(
    Object target,
    Object value
  );

  /**
   * Sets the value of the underlying field on the specified target object to {@code null}.
   * <p>
   * This is a convenience method equivalent to calling {@code set(target, null)}.
   * </p>
   *
   * @param target The object on which to set the field's value to {@code null}. For static fields, this parameter can
   *   be {@code null}.
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the underlying field is not accessible and
   *   cannot be made accessible.
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs during the field setting, such as an
   *   illegal access error.
   */
  default void setNull(
    Object target
  ) {
    this.set(target, null);
  }
}
