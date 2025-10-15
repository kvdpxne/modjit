package me.kvdpxne.modjit.exception;

/**
 * Thrown when a class cannot be found by its fully qualified name during a reflection operation.
 * <p>
 * This exception is typically thrown by the {@link me.kvdpxne.modjit.Reflection} utility class when attempting to load
 * a class via {@link java.lang.Class#forName(java.lang.String)} and the specified class path does not exist.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public final class ClassNotFoundReflectionException
  extends
  ReflectionException {

  /**
   * Constructs a new {@code ClassNotFoundReflectionException} with the specified class path and the underlying
   * {@link java.lang.ClassNotFoundException} as the cause.
   *
   * @param path The fully qualified name of the class that could not be found.
   * @param cause The {@code ClassNotFoundException} that was the root cause of this exception. Can be {@code null} if
   *   the cause is nonexistent or unknown.
   */
  public ClassNotFoundReflectionException(
    final String path,
    final Throwable cause
  ) {
    super("Class not found: " + path, cause);
  }
}
