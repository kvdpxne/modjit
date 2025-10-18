package me.kvdpxne.modjit.accessor.impl;

import java.lang.reflect.Field;
import java.util.Objects;
import me.kvdpxne.modjit.accessor.FieldAccessor;
import me.kvdpxne.modjit.exception.ReflectionSecurityException;
import me.kvdpxne.modjit.util.AccessController;

/**
 * An implementation of {@link me.kvdpxne.modjit.accessor.FieldAccessor} that wraps a {@link java.lang.reflect.Field}
 * and provides the logic for getting and setting field values using reflection.
 * <p>
 * This class handles the complete field access process, including accessibility management, value retrieval and
 * assignment, exception translation, and state restoration. It ensures that the original accessibility state of the
 * field is preserved and restored after each access attempt, preventing security warnings and potential accessibility
 * leaks.
 * </p>
 * <p>
 * The implementation immediately enables field accessibility upon instantiation and manages the accessibility lifecycle
 * through try-finally blocks to guarantee proper state restoration even when exceptions occur during field operations.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @see me.kvdpxne.modjit.accessor.FieldAccessor
 * @see java.lang.reflect.Field
 * @since 0.1.0
 */
public final class FieldAccessorImpl
  implements
  FieldAccessor {

  /**
   * The underlying {@link java.lang.reflect.Field} object being wrapped and managed.
   * <p>
   * This field holds the reflection field that will be used to access and modify field values. The field is made
   * accessible upon initialization of this wrapper and its accessibility state is carefully managed throughout the
   * object lifecycle.
   * </p>
   */
  private final Field field;

  /**
   * The original accessibility state of the field before any modification by this wrapper.
   * <p>
   * This value is captured when the wrapper is created and used to restore the field's accessibility state after each
   * access operation. This prevents "illegal reflective access" warnings and maintains security integrity by not
   * permanently altering the field's accessibility.
   * </p>
   */
  private final boolean originalAccessible;

  /**
   * The fully qualified name of the class declaring the field.
   * <p>
   * This field is used for constructing descriptive error messages when exceptions occur during field access
   * operations. It provides context about which class and field failed, aiding in debugging and error reporting.
   * </p>
   */
  private final String className;

  /**
   * Constructs a new field accessor wrapper.
   * <p>
   * This constructor immediately enables accessibility for the underlying field using
   * {@link me.kvdpxne.modjit.util.AccessController#setAccessible(java.lang.reflect.AccessibleObject, boolean)} to
   * ensure subsequent calls to {@link #get(Object)} or {@link #set(Object, Object)} can proceed without additional
   * accessibility checks. The original accessibility state is preserved for later restoration.
   * </p>
   * <p>
   * The accessibility enablement is safe because the state will be restored in the finally block during each field
   * access, preventing permanent accessibility changes.
   * </p>
   *
   * @param field the {@link java.lang.reflect.Field} to wrap and manage; must not be {@code null}
   * @param originalAccessible the original accessibility state of the field before any modification; used to restore
   *   the field's original state after access operations
   * @param className the fully qualified name of the class declaring the field; must not be {@code null}; used for
   *   error message context
   * @throws java.lang.NullPointerException if either {@code field} or {@code className} is {@code null}
   */
  public FieldAccessorImpl(
    final Field field,
    final boolean originalAccessible,
    final String className
  ) {
    this.field = field;
    this.originalAccessible = originalAccessible;
    this.className = className;
    // Immediately enable access to constructor
    // This is safe because we'll restore state in finally block during invocation
    AccessController.setAccessible(this.field, true);
  }

  /**
   * Retrieves the value of the underlying field from the specified target object.
   * <p>
   * This method performs the field value retrieval, handling accessibility management, exception translation, and state
   * restoration. The field value is retrieved using {@link java.lang.reflect.Field#get(Object)}, and the method ensures
   * proper cleanup regardless of success or failure.
   * </p>
   * <p>
   * The method supports both instance fields (non-static) and static fields. For static fields, the target parameter is
   * ignored and can be {@code null}. After the access attempt, the field's accessibility state is restored to its
   * original value if it was not originally accessible.
   * </p>
   *
   * @param target the object from which to read the field value; for static fields, this parameter can be
   *   {@code null}; for instance fields, must be a non-null instance of the class declaring the field
   * @return the current value of the field in the specified object; may be {@code null} if the field contains a null
   *   value or is of a primitive type (which will be returned as the corresponding wrapper type)
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the underlying field is not accessible and
   *   cannot be made accessible during access due to security manager restrictions or Java module system constraints
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs during field access; common causes
   *   include:
   *   <ul>
   *     <li>The target object is incompatible with the field's declaring class</li>
   *     <li>The field is an instance field and the target object is {@code null}</li>
   *   </ul>
   * @throws java.lang.NullPointerException if the field is an instance field and the target object is {@code null}
   * @throws java.lang.IllegalArgumentException if the target object is not an instance of the class or interface
   *   declaring the underlying field
   */
  @Override
  public Object get(
    final Object target
  ) {
    try {
      return this.field.get(target);
    } catch (final IllegalAccessException exception) {
      throw new ReflectionSecurityException(
        "Illegal access to field '" + this.field.getName() + "' in " + this.className,
        exception
      );
    } finally {
      // CRITICAL: Always restore original accessibility state
      // Prevents security leaks and "illegal reflective access" warnings
      if (!this.originalAccessible) {
        AccessController.setAccessible(this.field, false);
      }
    }
  }

  /**
   * Sets the value of the underlying field on the specified target object.
   * <p>
   * This method performs the field value assignment, handling accessibility management, exception translation, and
   * state restoration. The field value is set using {@link java.lang.reflect.Field#set(Object, Object)}, and the method
   * ensures proper cleanup regardless of success or failure.
   * </p>
   * <p>
   * The method supports both instance fields (non-static) and static fields. For static fields, the target parameter is
   * ignored and can be {@code null}. The value must be assignment-compatible with the field's declared type. After the
   * assignment attempt, the field's accessibility state is restored to its original value if it was not originally
   * accessible.
   * </p>
   *
   * @param target the object on which to set the field value; for static fields, this parameter can be {@code null};
   *   for instance fields, must be a non-null instance of the class declaring the field
   * @param value the new value to assign to the field; must be assignment-compatible with the field's declared type;
   *   may be {@code null} for reference types
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the underlying field is not accessible and
   *   cannot be made accessible during assignment due to security manager restrictions or Java module system
   *   constraints
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs during field assignment; common causes
   *   include:
   *   <ul>
   *     <li>The target object is incompatible with the field's declaring class</li>
   *     <li>The field is an instance field and the target object is {@code null}</li>
   *     <li>The value type is not assignment-compatible with the field's declared type</li>
   *   </ul>
   * @throws java.lang.NullPointerException if the field is an instance field and the target object is {@code null}
   * @throws java.lang.IllegalArgumentException if the target object is not an instance of the class or interface
   *   declaring the underlying field, or if the value type is not assignment-compatible with the field's declared type
   */
  @Override
  public void set(
    final Object target,
    final Object value
  ) {
    try {
      this.field.set(target, value);
    } catch (final IllegalAccessException exception) {
      throw new ReflectionSecurityException(
        "Illegal access to field '" + this.field.getName() + "' in " + this.className,
        exception
      );
    } finally {
      // CRITICAL: Always restore original accessibility state
      // Prevents security leaks and "illegal reflective access" warnings
      if (!this.originalAccessible) {
        AccessController.setAccessible(this.field, false);
      }
    }
  }

  /**
   * Compares this field accessor with another object for equality.
   * <p>
   * Two {@code FieldAccessorImpl} instances are considered equal if they wrap the same underlying field (as determined
   * by {@link java.lang.reflect.Field#equals(Object)}), have the same original accessibility state, and are associated
   * with the same class name.
   * </p>
   * <p>
   * This equality implementation ensures that field accessors can be properly compared and used in hash-based
   * collections, maintaining consistency with the wrapped field's identity and configuration.
   * </p>
   *
   * @param o the object to compare with this field accessor for equality; may be {@code null}
   * @return {@code true} if the specified object is a {@code FieldAccessorImpl} that wraps the same field, has the same
   *   original accessibility state, and the same class name; {@code false} otherwise
   */
  @Override
  public boolean equals(
    final Object o
  ) {
    if (null == o || this.getClass() != o.getClass()) {
      return false;
    }
    final FieldAccessorImpl that = (FieldAccessorImpl) o;
    return this.originalAccessible == that.originalAccessible
      && Objects.equals(this.field, that.field)
      && Objects.equals(this.className, that.className);
  }

  /**
   * Returns a hash code value for this field accessor.
   * <p>
   * The hash code is computed based on the underlying field, the original accessibility state, and the class name. This
   * implementation ensures that equal objects have equal hash codes, making instances suitable for use as keys in
   * hash-based collections.
   * </p>
   * <p>
   * The hash code computation uses {@link java.util.Objects#hash(Object...)} to combine the relevant fields, providing
   * a well-distributed hash value that reflects the object's identity and configuration.
   * </p>
   *
   * @return a hash code value for this field accessor
   */
  @Override
  public int hashCode() {
    return Objects.hash(
      this.field,
      this.originalAccessible,
      this.className
    );
  }
}
