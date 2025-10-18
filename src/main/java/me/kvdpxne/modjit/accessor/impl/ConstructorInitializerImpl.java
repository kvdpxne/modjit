package me.kvdpxne.modjit.accessor.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import me.kvdpxne.modjit.accessor.ConstructorInitializer;
import me.kvdpxne.modjit.exception.ReflectionException;
import me.kvdpxne.modjit.exception.ReflectionSecurityException;
import me.kvdpxne.modjit.util.AccessController;

/**
 * An implementation of {@link me.kvdpxne.modjit.accessor.ConstructorInitializer} that wraps a
 * {@link java.lang.reflect.Constructor} and provides the logic for creating new instances using reflection.
 * <p>
 * This class handles the complete constructor invocation process, including accessibility management, parameter
 * passing, exception translation, and state restoration. It ensures that the original accessibility state of the
 * constructor is preserved and restored after each invocation attempt, preventing security warnings and potential
 * accessibility leaks.
 * </p>
 * <p>
 * The implementation immediately enables constructor accessibility upon instantiation and manages the accessibility
 * lifecycle through try-finally blocks to guarantee proper state restoration even when exceptions occur during object
 * construction.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @see me.kvdpxne.modjit.accessor.ConstructorInitializer
 * @see java.lang.reflect.Constructor
 * @since 0.1.0
 */
public final class ConstructorInitializerImpl
  implements
  ConstructorInitializer {

  /**
   * The underlying {@link java.lang.reflect.Constructor} object being wrapped and managed.
   * <p>
   * This field holds the reflection constructor that will be used to create new object instances. The constructor is
   * made accessible upon initialization of this wrapper and its accessibility state is carefully managed throughout the
   * object lifecycle.
   * </p>
   */
  private final Constructor<?> constructor;

  /**
   * The original accessibility state of the constructor before any modification by this wrapper.
   * <p>
   * This value is captured when the wrapper is created and used to restore the constructor's accessibility state after
   * each invocation. This prevents "illegal reflective access" warnings and maintains security integrity by not
   * permanently altering the constructor's accessibility.
   * </p>
   */
  private final boolean originalAccessible;

  /**
   * The fully qualified name of the class declaring the constructor.
   * <p>
   * This field is used for constructing descriptive error messages when exceptions occur during object instantiation.
   * It provides context about which class and constructor failed, aiding in debugging and error reporting.
   * </p>
   */
  private final String className;

  /**
   * Constructs a new constructor initializer wrapper.
   * <p>
   * This constructor immediately enables accessibility for the underlying constructor using
   * {@link me.kvdpxne.modjit.util.AccessController#setAccessible(java.lang.reflect.AccessibleObject, boolean)} to
   * ensure subsequent calls to {@link #newInstance(Object[])} can proceed without additional accessibility checks. The
   * original accessibility state is preserved for later restoration.
   * </p>
   * <p>
   * The accessibility enablement is safe because the state will be restored in the finally block during each
   * constructor invocation, preventing permanent accessibility changes.
   * </p>
   *
   * @param constructor the {@link java.lang.reflect.Constructor} to wrap and manage; must not be {@code null}
   * @param originalAccessible the original accessibility state of the constructor before any modification; used to
   *   restore the constructor's original state after invocations
   * @param className the fully qualified name of the class declaring the constructor; must not be {@code null}; used
   *   for error message context
   * @throws java.lang.NullPointerException if either {@code constructor} or {@code className} is {@code null}
   */
  public ConstructorInitializerImpl(
    final Constructor<?> constructor,
    final boolean originalAccessible,
    final String className
  ) {
    this.constructor = constructor;
    this.originalAccessible = originalAccessible;
    this.className = className;
    // Immediately enable access to constructor
    // This is safe because we'll restore state in finally block during invocation
    AccessController.setAccessible(this.constructor, true);
  }

  /**
   * Creates a new instance of the target class by invoking the underlying constructor.
   * <p>
   * This method performs the complete object instantiation process, handling parameter passing, exception translation,
   * and accessibility management. The constructor is invoked with the specified parameters, and the method ensures
   * proper cleanup regardless of success or failure.
   * </p>
   * <p>
   * The method handles both no-argument constructors (when parameters are {@code null} or empty) and parameterized
   * constructors. After the invocation attempt, the constructor's accessibility state is restored to its original value
   * if it was not originally accessible.
   * </p>
   * <p>
   * Exceptions thrown during construction are translated into the library's standardized exception hierarchy, with
   * special handling for common reflection-related error conditions.
   * </p>
   *
   * @param parameters an array of objects representing the arguments to pass to the constructor; may be {@code null}
   *   to invoke a no-argument constructor, or an empty array for explicit no-argument invocation; must match the
   *   constructor's parameter types in order and assignment compatibility
   * @return a new instance of the target class created by invoking the constructor; never {@code null} unless the
   *   constructor explicitly returns {@code null} (which is not typical for constructors)
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the underlying constructor is not accessible
   *   and cannot be made accessible during invocation due to security manager restrictions or Java module system
   *   constraints
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs during the construction process;
   *   common causes include:
   *   <ul>
   *     <li>Exceptions thrown by the constructor logic itself (wrapped as the cause)</li>
   *     <li>Instantiation of abstract classes</li>
   *     <li>Parameter type mismatches or incorrect parameter counts</li>
   *     <li>Generic array creation issues</li>
   *   </ul>
   * @throws java.lang.IllegalArgumentException if the number of actual and formal parameters differ, or if unwrapping
   *   conversion for primitive types fails
   * @throws java.lang.InstantiationException if the class that declares the underlying constructor represents an
   *   abstract class (wrapped in ReflectionException)
   * @throws java.lang.ExceptionInInitializerError if the constructor initialization triggers an exception
   */
  @Override
  public Object newInstance(
    final Object[] parameters
  ) {
    try {
      if (null == parameters || 0 == parameters.length) {
        return this.constructor.newInstance();
      }
      return this.constructor.newInstance(parameters);
    } catch (InvocationTargetException exception) {
      final Throwable cause = exception.getCause();
      throw new ReflectionException(
        "Error constructing " + this.className,
        null != cause ? cause : exception
      );
    } catch (final InstantiationException exception) {
      throw new ReflectionException(
        "Cannot instantiate abstract class " + this.className,
        exception
      );
    } catch (final IllegalAccessException exception) {
      throw new ReflectionSecurityException(
        "Illegal access to constructor of " + this.className,
        exception
      );
    } finally {
      // CRITICAL: Always restore original accessibility state
      // Prevents security leaks and "illegal reflective access" warnings
      if (!this.originalAccessible) {
        AccessController.setAccessible(this.constructor, false);
      }
    }
  }

  /**
   * Compares this constructor initializer with another object for equality.
   * <p>
   * Two {@code ConstructorInitializerImpl} instances are considered equal if they wrap the same underlying constructor
   * (as determined by {@link java.lang.reflect.Constructor#equals(Object)}), have the same original accessibility
   * state, and are associated with the same class name.
   * </p>
   * <p>
   * This equality implementation ensures that constructor initializers can be properly compared and used in hash-based
   * collections, maintaining consistency with the wrapped constructor's identity and configuration.
   * </p>
   *
   * @param o the object to compare with this constructor initializer for equality; may be {@code null}
   * @return {@code true} if the specified object is a {@code ConstructorInitializerImpl} that wraps the same
   *   constructor, has the same original accessibility state, and the same class name; {@code false} otherwise
   */
  @Override
  public boolean equals(
    final Object o
  ) {
    if (null == o || this.getClass() != o.getClass()) {
      return false;
    }
    final ConstructorInitializerImpl that = (ConstructorInitializerImpl) o;
    return this.originalAccessible == that.originalAccessible
      && Objects.equals(this.constructor, that.constructor)
      && Objects.equals(this.className, that.className);
  }

  /**
   * Returns a hash code value for this constructor initializer.
   * <p>
   * The hash code is computed based on the underlying constructor, the original accessibility state, and the class
   * name. This implementation ensures that equal objects have equal hash codes, making instances suitable for use as
   * keys in hash-based collections.
   * </p>
   * <p>
   * The hash code computation uses {@link java.util.Objects#hash(Object...)} to combine the relevant fields, providing
   * a well-distributed hash value that reflects the object's identity and configuration.
   * </p>
   *
   * @return a hash code value for this constructor initializer
   */
  @Override
  public int hashCode() {
    return Objects.hash(
      this.constructor,
      this.originalAccessible,
      this.className
    );
  }
}
