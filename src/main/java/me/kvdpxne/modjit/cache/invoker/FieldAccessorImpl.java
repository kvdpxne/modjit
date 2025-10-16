package me.kvdpxne.modjit.cache.invoker;

import java.lang.reflect.Field;
import java.util.Objects;
import me.kvdpxne.modjit.accessor.FieldAccessor;
import me.kvdpxne.modjit.exception.ReflectionSecurityException;
import me.kvdpxne.modjit.util.AccessController;

/**
 * An implementation of {@link me.kvdpxne.modjit.accessor.FieldAccessor} that wraps a {@link java.lang.reflect.Field}
 * and provides the logic for getting and setting field values using reflection.
 * <p>
 * This class handles the access to the underlying field, manages its accessibility using the
 * {@link me.kvdpxne.modjit.util.AccessController}, and translates reflection-specific exceptions into library-specific
 * exceptions like {@link me.kvdpxne.modjit.exception.ReflectionSecurityException}.
 * </p>
 * <p>
 * It ensures that the original accessibility state of the field is restored after each access attempt, preventing
 * security warnings and potential leaks.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public final class FieldAccessorImpl
  implements
  FieldAccessor {

  /**
   * The underlying {@link java.lang.reflect.Field} object being wrapped.
   */
  private final Field field;

  /**
   * The original accessibility state of the field before any manipulation by this library. This state is restored after
   * each access to prevent security warnings.
   */
  private final boolean originalAccessible;

  /**
   * The fully qualified name of the class declaring the field. Used for constructing error messages.
   */
  private final String className;

  /**
   * Constructs a new {@code FieldAccessorImpl}.
   * <p>
   * It immediately sets the underlying field accessible using
   * {@link me.kvdpxne.modjit.util.AccessController#setAccessible(java.lang.reflect.AccessibleObject, boolean)} so that
   * subsequent calls to {@link #get(java.lang.Object)} or {@link #set(java.lang.Object, java.lang.Object)} can proceed.
   * The original accessibility state is stored for later restoration.
   * </p>
   *
   * @param field The {@link java.lang.reflect.Field} to wrap. Must not be {@code null}.
   * @param originalAccessible {@code true} if the field was originally accessible, {@code false} otherwise.
   * @param className The name of the class declaring the field. Used for error messages. Must not be {@code null}.
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
   * Gets the value of the underlying field from the specified target object.
   * <p>
   * It uses {@link java.lang.reflect.Field#get(java.lang.Object)} to retrieve the value.
   * </p>
   * <p>
   * After the access attempt (successful or not), it restores the field's accessibility to its original state if it was
   * not originally accessible.
   * </p>
   *
   * @param target The object from which to get the field's value. For static fields, this parameter can be
   *   {@code null}.
   * @return The value of the field in the specified object.
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the underlying field is not accessible and
   *   cannot be made accessible during access.
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
   * It uses {@link java.lang.reflect.Field#set(java.lang.Object, java.lang.Object)} to assign the new value.
   * </p>
   * <p>
   * After the access attempt (successful or not), it restores the field's accessibility to its original state if it was
   * not originally accessible.
   * </p>
   *
   * @param target The object on which to set the field's value. For static fields, this parameter can be
   *   {@code null}.
   * @param value The new value to assign to the field.
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the underlying field is not accessible and
   *   cannot be made accessible during access.
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
   * Compares this {@code FieldAccessorImpl} with another object for equality. Two instances are considered equal if
   * they wrap the same underlying {@link java.lang.reflect.Field}, have the same original accessibility state, and
   * belong to the same class (based on the class name).
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
    final FieldAccessorImpl that = (FieldAccessorImpl) o;
    return this.originalAccessible == that.originalAccessible
      && Objects.equals(this.field, that.field)
      && Objects.equals(this.className, that.className);
  }

  /**
   * Returns the hash code value for this {@code FieldAccessorImpl}. The hash code is computed based on the underlying
   * field, the original accessibility state, and the class name.
   *
   * @return the hash code value for this instance
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
