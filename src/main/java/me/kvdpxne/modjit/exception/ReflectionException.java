package me.kvdpxne.modjit.exception;

/**
 * The base exception class for errors that occur during reflection operations
 * within the {@code modjit} library.
 * <p>
 * This exception extends {@link RuntimeException} and serves as the superclass
 * for more specific reflection-related exceptions like
 * {@link ClassNotFoundReflectionException}, {@link FieldNotFoundReflectionException},
 * {@link MethodNotFoundReflectionException}, and {@link ReflectionSecurityException}.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @since 0.1.0
 * @version 0.1.0
 */
public class ReflectionException
  extends
  RuntimeException {

  /**
   * Constructs a new {@code ReflectionException} with the specified
   * detail message and cause.
   *
   * @param message The detail message string.
   * @param cause   The cause of this exception. Can be {@code null} if the
   *                cause is nonexistent or unknown.
   */
  public ReflectionException(
    final String message,
    final Throwable cause
  ) {
    super(message, cause);
  }

  /**
   * Constructs a new {@code ReflectionException} with the specified
   * detail message.
   *
   * @param message The detail message string.
   */
  public ReflectionException(
    final String message
  ) {
    super(message);
  }
}
