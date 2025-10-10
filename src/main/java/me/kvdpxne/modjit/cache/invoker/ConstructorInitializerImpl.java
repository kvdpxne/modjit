package me.kvdpxne.modjit.cache.invoker;

import java.util.Objects;
import me.kvdpxne.modjit.acessor.ConstructorInitializer;
import me.kvdpxne.modjit.exception.ReflectionException;
import me.kvdpxne.modjit.exception.ReflectionSecurityException;
import me.kvdpxne.modjit.util.AccessController;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * An implementation of {@link ConstructorInitializer} that wraps a
 * {@link java.lang.reflect.Constructor} and provides the logic for
 * creating new instances using reflection.
 * <p>
 * This class handles the invocation of the underlying constructor,
 * manages its accessibility using the {@link AccessController},
 * and translates reflection-specific exceptions into library-specific
 * exceptions like {@link ReflectionSecurityException} or
 * {@link ReflectionException}.
 * </p>
 * <p>
 * It ensures that the original accessibility state of the constructor
 * is restored after each invocation attempt, preventing security
 * warnings and potential leaks.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @since 0.1.0
 * @version 0.1.0
 */
public final class ConstructorInitializerImpl
  implements
  ConstructorInitializer {

  /**
   * The underlying {@link Constructor} object being wrapped.
   */
  private final Constructor<?> constructor;

  /**
   * The original accessibility state of the constructor before any
   * manipulation by this library. This state is restored after each
   * invocation to prevent security warnings.
   */
  private final boolean originalAccessible;

  /**
   * The fully qualified name of the class declaring the constructor.
   * Used for constructing error messages.
   */
  private final String className;

  /**
   * Constructs a new {@code ConstructorInitializerImpl}.
   * <p>
   * It immediately sets the underlying constructor accessible using
   * {@link AccessController#setAccessible(java.lang.reflect.AccessibleObject, boolean)}
   * so that subsequent calls to {@link #newInstance(Object[])} can proceed.
   * The original accessibility state is stored for later restoration.
   * </p>
   *
   * @param constructor       The {@link Constructor} to wrap. Must not be {@code null}.
   * @param originalAccessible {@code true} if the constructor was originally
   *                          accessible, {@code false} otherwise.
   * @param className         The name of the class declaring the constructor.
   *                          Used for error messages. Must not be {@code null}.
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
   * Creates a new instance of the target class by invoking the underlying
   * constructor with the specified parameters.
   * <p>
   * It uses {@link Constructor#newInstance(Object...)} to perform the
   * instantiation. If the {@code parameters} array is {@code null},
   * it calls the constructor with no arguments. Otherwise, it passes
   * the provided parameters.
   * </p>
   * <p>
   * After the invocation attempt (successful or not), it restores the
   * constructor's accessibility to its original state if it was not
   * originally accessible.
   * </p>
   *
   * @param parameters An array of objects representing the arguments to pass
   *                   to the constructor. If the constructor takes no arguments,
   *                   an empty array ({@code new Object[0]}) should be passed.
   *                   Passing {@code null} will result in a call to the
   *                   no-argument constructor.
   * @return A new instance of the target class.
   * @throws ReflectionSecurityException if the underlying constructor is
   *     not accessible and cannot be made accessible during invocation.
   * @throws ReflectionException if an error occurs during the construction
   *     process, such as instantiation failure of an abstract class or an
   *     exception thrown by the invoked constructor itself.
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
   * Compares this {@code ConstructorInitializerImpl} with another object for equality.
   * Two instances are considered equal if they wrap the same underlying
   * {@link Constructor}, have the same original accessibility state, and
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
    final ConstructorInitializerImpl that = (ConstructorInitializerImpl) o;
    return this.originalAccessible == that.originalAccessible
      && Objects.equals(this.constructor, that.constructor)
      && Objects.equals(this.className, that.className);
  }

  /**
   * Returns the hash code value for this {@code ConstructorInitializerImpl}.
   * The hash code is computed based on the underlying constructor, the
   * original accessibility state, and the class name.
   *
   * @return the hash code value for this instance
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
