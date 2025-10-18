package me.kvdpxne.modjit.accessor.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import me.kvdpxne.modjit.accessor.MethodInvoker;
import me.kvdpxne.modjit.exception.ReflectionException;
import me.kvdpxne.modjit.exception.ReflectionSecurityException;
import me.kvdpxne.modjit.util.AccessController;

/**
 * An implementation of {@link me.kvdpxne.modjit.accessor.MethodInvoker} that wraps a {@link java.lang.reflect.Method}
 * and provides the logic for invoking methods using reflection.
 * <p>
 * This class handles the complete method invocation process, including accessibility management, parameter passing,
 * return value handling, exception translation, and state restoration. It ensures that the original accessibility state
 * of the method is preserved and restored after each invocation attempt, preventing security warnings and potential
 * accessibility leaks.
 * </p>
 * <p>
 * The implementation immediately enables method accessibility upon instantiation and manages the accessibility
 * lifecycle through try-finally blocks to guarantee proper state restoration even when exceptions occur during method
 * execution.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @see me.kvdpxne.modjit.accessor.MethodInvoker
 * @see java.lang.reflect.Method
 * @since 0.1.0
 */
public final class MethodInvokerImpl
  implements
  MethodInvoker {

  /**
   * The underlying {@link java.lang.reflect.Method} object being wrapped and managed.
   * <p>
   * This field holds the reflection method that will be used to invoke method calls. The method is made accessible upon
   * initialization of this wrapper and its accessibility state is carefully managed throughout the object lifecycle.
   * </p>
   */
  private final Method method;

  /**
   * The original accessibility state of the method before any modification by this wrapper.
   * <p>
   * This value is captured when the wrapper is created and used to restore the method's accessibility state after each
   * invocation. This prevents "illegal reflective access" warnings and maintains security integrity by not permanently
   * altering the method's accessibility.
   * </p>
   */
  private final boolean originalAccessible;

  /**
   * The fully qualified name of the class declaring the method.
   * <p>
   * This field is used for constructing descriptive error messages when exceptions occur during method invocation. It
   * provides context about which class and method failed, aiding in debugging and error reporting.
   * </p>
   */
  private final String className;

  /**
   * Constructs a new method invoker wrapper.
   * <p>
   * This constructor immediately enables accessibility for the underlying method using
   * {@link me.kvdpxne.modjit.util.AccessController#setAccessible(java.lang.reflect.AccessibleObject, boolean)} to
   * ensure subsequent calls to {@link #invoke(Object, Object[])} can proceed without additional accessibility checks.
   * The original accessibility state is preserved for later restoration.
   * </p>
   * <p>
   * The accessibility enablement is safe because the state will be restored in the finally block during each method
   * invocation, preventing permanent accessibility changes.
   * </p>
   *
   * @param method the {@link java.lang.reflect.Method} to wrap and manage; must not be {@code null}
   * @param originalAccessible the original accessibility state of the method before any modification; used to restore
   *   the method's original state after invocations
   * @param className the fully qualified name of the class declaring the method; must not be {@code null}; used for
   *   error message context
   * @throws java.lang.NullPointerException if either {@code method} or {@code className} is {@code null}
   */
  public MethodInvokerImpl(
    final Method method,
    final boolean originalAccessible,
    final String className
  ) {
    this.method = method;
    this.originalAccessible = originalAccessible;
    this.className = className;
    // Immediately enable access to constructor
    // This is safe because we'll restore state in finally block during invocation
    AccessController.setAccessible(this.method, true);
  }

  /**
   * Invokes the underlying method on the specified target object with the provided parameters.
   * <p>
   * This method performs the complete method invocation process, handling parameter passing, return value processing,
   * exception translation, and accessibility management. The method is invoked with the specified parameters, and the
   * method ensures proper cleanup regardless of success or failure.
   * </p>
   * <p>
   * The method handles both no-argument methods (when parameters are {@code null} or empty) and parameterized methods.
   * After the invocation attempt, the method's accessibility state is restored to its original value if it was not
   * originally accessible.
   * </p>
   * <p>
   * Exceptions thrown during method execution are translated into the library's standardized exception hierarchy, with
   * special handling for common reflection-related error conditions and exceptions thrown by the method logic itself.
   * </p>
   *
   * @param target the object on which to invoke the method; for static methods, this parameter can be {@code null};
   *   for instance methods, must be a non-null instance of the class declaring the method
   * @param parameters an array of objects representing the arguments to pass to the method; may be {@code null} to
   *   invoke a no-argument method, or an empty array for explicit no-argument invocation; must match the method's
   *   parameter types in order and assignment compatibility
   * @return the return value from the method invocation; may be {@code null} if the method returns {@code null} or has
   *   a {@code void} return type
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the underlying method is not accessible and
   *   cannot be made accessible during invocation due to security manager restrictions or Java module system
   *   constraints
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs during the method invocation; common
   *   causes include:
   *   <ul>
   *     <li>Exceptions thrown by the method logic itself (wrapped as the cause)</li>
   *     <li>Parameter type mismatches or incorrect parameter counts</li>
   *     <li>Target object incompatibility with the method's declaring class</li>
   *     <li>Generic array creation issues</li>
   *   </ul>
   * @throws java.lang.NullPointerException if the method is an instance method and the target object is {@code null}
   * @throws java.lang.IllegalArgumentException if the number of actual and formal parameters differ, if unwrapping
   *   conversion for primitive types fails, or if the target object is not an instance of the class or interface
   *   declaring the underlying method
   * @throws java.lang.ExceptionInInitializerError if the method initialization triggers an exception
   */
  @Override
  public Object invoke(
    final Object target,
    final Object[] parameters
  ) {
    try {
      if (null == parameters || 0 == parameters.length) {
        return this.method.invoke(target);
      }
      return this.method.invoke(target, parameters);
    } catch (final InvocationTargetException exception) {
      final Throwable cause = exception.getCause();
      throw new ReflectionException(
        "Error invoking method '" + this.method.getName() + "' in " + this.className,
        null != cause ? cause : exception
      );
    } catch (final IllegalAccessException exception) {
      throw new ReflectionSecurityException(
        "Illegal access to method '" + this.method.getName() + "' in " + this.className,
        exception
      );
    } finally {
      // CRITICAL: Always restore original accessibility state
      // Prevents security leaks and "illegal reflective access" warnings
      if (!this.originalAccessible) {
        AccessController.setAccessible(this.method, false);
      }
    }
  }

  /**
   * Compares this method invoker with another object for equality.
   * <p>
   * Two {@code MethodInvokerImpl} instances are considered equal if they wrap the same underlying method (as determined
   * by {@link java.lang.reflect.Method#equals(Object)}), have the same original accessibility state, and are associated
   * with the same class name.
   * </p>
   * <p>
   * This equality implementation ensures that method invokers can be properly compared and used in hash-based
   * collections, maintaining consistency with the wrapped method's identity and configuration.
   * </p>
   *
   * @param o the object to compare with this method invoker for equality; may be {@code null}
   * @return {@code true} if the specified object is a {@code MethodInvokerImpl} that wraps the same method, has the
   *   same original accessibility state, and the same class name; {@code false} otherwise
   */
  @Override
  public boolean equals(
    final Object o
  ) {
    if (null == o || this.getClass() != o.getClass()) {
      return false;
    }
    final MethodInvokerImpl that = (MethodInvokerImpl) o;
    return this.originalAccessible == that.originalAccessible
      && Objects.equals(this.method, that.method)
      && Objects.equals(this.className, that.className);
  }

  /**
   * Returns a hash code value for this method invoker.
   * <p>
   * The hash code is computed based on the underlying method, the original accessibility state, and the class name.
   * This implementation ensures that equal objects have equal hash codes, making instances suitable for use as keys in
   * hash-based collections.
   * </p>
   * <p>
   * The hash code computation uses {@link java.util.Objects#hash(Object...)} to combine the relevant fields, providing
   * a well-distributed hash value that reflects the object's identity and configuration.
   * </p>
   *
   * @return a hash code value for this method invoker
   */
  @Override
  public int hashCode() {
    return Objects.hash(
      this.method,
      this.originalAccessible,
      this.className
    );
  }
}
