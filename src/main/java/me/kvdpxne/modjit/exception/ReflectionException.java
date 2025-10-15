package me.kvdpxne.modjit.exception;

/**
 * The base exception class for errors that occur during reflection operations within the {@code modjit} library.
 * <p>
 * This exception extends {@link java.lang.RuntimeException} and serves as the superclass for more specific
 * reflection-related exceptions like {@link me.kvdpxne.modjit.exception.ClassNotFoundReflectionException},
 * {@link me.kvdpxne.modjit.exception.FieldNotFoundReflectionException},
 * {@link me.kvdpxne.modjit.exception.MethodNotFoundReflectionException}, and
 * {@link me.kvdpxne.modjit.exception.ReflectionSecurityException}.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public class ReflectionException
  extends
  RuntimeException {

  /**
   * Constructs a new {@code ReflectionException} with the specified detail message and cause.
   *
   * @param message The detail message string.
   * @param cause The cause of this exception. Can be {@code null} if the cause is nonexistent or unknown.
   */
  public ReflectionException(
    final String message,
    final Throwable cause
  ) {
    super(message, cause);
  }

  /**
   * Constructs a new {@code ReflectionException} with the specified detail message.
   *
   * @param message The detail message string.
   */
  public ReflectionException(
    final String message
  ) {
    super(message);
  }
}
