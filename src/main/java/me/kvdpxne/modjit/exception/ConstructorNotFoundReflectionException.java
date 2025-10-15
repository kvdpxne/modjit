package me.kvdpxne.modjit.exception;

import java.util.Arrays;

/**
 * Thrown when a constructor cannot be found in a specified class with the requested parameter types during a reflection
 * operation.
 * <p>
 * This exception is typically thrown by the {@link me.kvdpxne.modjit.Reflection} utility class when attempting to
 * retrieve a constructor via {@link java.lang.Class#getDeclaredConstructor(java.lang.Class...)} and no matching
 * constructor is found in the class.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public final class ConstructorNotFoundReflectionException
  extends
  ReflectionException {

  /**
   * Constructs a new {@code ConstructorNotFoundReflectionException} with the specified class name and parameter types
   * that were not found.
   *
   * @param className The fully qualified name of the class in which the constructor was sought.
   * @param parameterTypes An array of {@link java.lang.Class} objects representing the parameter types of the
   *   requested constructor. Can be {@code null} if no specific parameters were requested (e.g., for a default
   *   constructor lookup that failed).
   */
  public ConstructorNotFoundReflectionException(
    final String className,
    final Class<?>[] parameterTypes
  ) {
    super("Constructor not found for " + className + " with parameters: " + Arrays.toString(parameterTypes));
  }
}
