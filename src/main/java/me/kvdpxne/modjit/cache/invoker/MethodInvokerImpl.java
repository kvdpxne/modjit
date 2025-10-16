package me.kvdpxne.modjit.cache.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import me.kvdpxne.modjit.accessor.MethodInvoker;
import me.kvdpxne.modjit.exception.ReflectionException;
import me.kvdpxne.modjit.exception.ReflectionSecurityException;
import me.kvdpxne.modjit.util.AccessController;

/**
 * An implementation of {@link me.kvdpxne.modjit.accessor.MethodInvoker} that wraps a {@link java.lang.reflect.Method}
 * and provides the logic for invoking the method using reflection.
 * <p>
 * This class handles the invocation of the underlying method, manages its accessibility using the
 * {@link me.kvdpxne.modjit.util.AccessController}, and translates reflection-specific exceptions into library-specific
 * exceptions like {@link me.kvdpxne.modjit.exception.ReflectionSecurityException} or
 * {@link me.kvdpxne.modjit.exception.ReflectionException}.
 * </p>
 * <p>
 * It ensures that the original accessibility state of the method is restored after each invocation attempt, preventing
 * security warnings and potential leaks.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public final class MethodInvokerImpl
  implements
  MethodInvoker {

  /**
   * The underlying {@link java.lang.reflect.Method} object being wrapped.
   */
  private final Method method;

  /**
   * The original accessibility state of the method before any manipulation by this library. This state is restored
   * after each invocation to prevent security warnings.
   */
  private final boolean originalAccessible;

  /**
   * The fully qualified name of the class declaring the method. Used for constructing error messages.
   */
  private final String className;

  /**
   * Constructs a new {@code MethodInvokerImpl}.
   * <p>
   * It immediately sets the underlying method accessible using
   * {@link me.kvdpxne.modjit.util.AccessController#setAccessible(java.lang.reflect.AccessibleObject, boolean)} so that
   * subsequent calls to {@link #invoke(java.lang.Object, java.lang.Object[])} can proceed. The original accessibility
   * state is stored for later restoration.
   * </p>
   *
   * @param method The {@link java.lang.reflect.Method} to wrap. Must not be {@code null}.
   * @param originalAccessible {@code true} if the method was originally accessible, {@code false} otherwise.
   * @param className The name of the class declaring the method. Used for error messages. Must not be {@code null}.
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
   * It uses {@link java.lang.reflect.Method#invoke(java.lang.Object, java.lang.Object...)} to perform the invocation.
   * If the {@code parameters} array is {@code null}, it calls the method with no arguments. Otherwise, it passes the
   * provided parameters.
   * </p>
   * <p>
   * After the invocation attempt (successful or not), it restores the method's accessibility to its original state if
   * it was not originally accessible.
   * </p>
   *
   * @param target The object on which to invoke the method. For static methods, this parameter can be {@code null}.
   * @param parameters An array of objects representing the arguments to pass to the method. If the method takes no
   *   arguments, an empty array ({@code new Object[0]}) should be passed. Passing {@code null} will result in a call to
   *   the no-argument method.
   * @return The return value of the method invocation. If the method has a {@code void} return type, this will be
   *   {@code null}.
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the underlying method is not accessible and
   *   cannot be made accessible during invocation.
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs during the method invocation, such as
   *   an exception thrown by the invoked method itself.
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
   * Compares this {@code MethodInvokerImpl} with another object for equality. Two instances are considered equal if
   * they wrap the same underlying {@link java.lang.reflect.Method}, have the same original accessibility state, and
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
    final MethodInvokerImpl that = (MethodInvokerImpl) o;
    return this.originalAccessible == that.originalAccessible
      && Objects.equals(this.method, that.method)
      && Objects.equals(this.className, that.className);
  }

  /**
   * Returns the hash code value for this {@code MethodInvokerImpl}. The hash code is computed based on the underlying
   * method, the original accessibility state, and the class name.
   *
   * @return the hash code value for this instance
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
