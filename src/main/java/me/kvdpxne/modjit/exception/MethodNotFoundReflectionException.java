package me.kvdpxne.modjit.exception;

import java.util.Arrays;

/**
 * Thrown when a method cannot be found in a specified class with the
 * requested name, parameter types, and optional return type during a
 * reflection operation.
 * <p>
 * This exception is typically thrown by the {@link me.kvdpxne.modjit.Reflection}
 * utility class when attempting to retrieve a method via
 * {@link Class#getDeclaredMethod(String, Class...)} (or similar logic for
 * declared methods) and no matching method is found in the class.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @since 0.1.0
 * @version 0.1.0
 */
public final class MethodNotFoundReflectionException
  extends
  ReflectionException {

  /**
   * Constructs a new {@code MethodNotFoundReflectionException} with the
   * specified class name, method name, parameter types, and return type
   * that were not found.
   *
   * @param className      The fully qualified name of the class in which the
   *                       method was sought.
   * @param methodName     The simple name of the method that could not be found.
   * @param parameterTypes An array of {@link Class} objects representing the
   *                       expected parameter types of the method. Can be
   *                       {@code null} if parameter types were not part of the
   *                       search criteria.
   * @param returnType     The expected return type of the method. Can be
   *                       {@code null} if the return type was not part of the
   *                       search criteria.
   */
  public MethodNotFoundReflectionException(
    final String className,
    final String methodName,
    final Class<?>[] parameterTypes,
    final Class<?> returnType
  ) {
    super(
      "Method '" + methodName + "' not found in " + className
        + (null != parameterTypes ? "(expected parameters: " + Arrays.toString(parameterTypes) + ")" : "")
        + (null != returnType ? "(expected return type: " + returnType.getName() + ")" : "")
    );
  }
}
