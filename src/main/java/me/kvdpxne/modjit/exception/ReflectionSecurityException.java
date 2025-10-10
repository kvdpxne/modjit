package me.kvdpxne.modjit.exception;

/**
 * Thrown when a reflection operation fails due to security restrictions,
 * such as attempting to access a member that is not accessible and cannot
 * be made accessible via {@link java.lang.reflect.AccessibleObject#setAccessible(boolean)}.
 * <p>
 * This exception typically occurs when the Java runtime's security manager
 * or module system (introduced in Java 9+) prevents the library from gaining
 * access to the requested reflection member, even after attempting to use
 * {@code setAccessible(true)}.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @since 0.1.0
 * @version 0.1.0
 */
public final class ReflectionSecurityException
  extends
  ReflectionException {

  /**
   * Constructs a new {@code ReflectionSecurityException} with the specified
   * detail message and cause.
   *
   * @param message The detail message string.
   * @param cause   The cause of this exception. Can be {@code null} if the
   *                cause is nonexistent or unknown.
   */
  public ReflectionSecurityException(
    final String message,
    final Throwable cause
  ) {
    super(message, cause);
  }

  /**
   * Constructs a new {@code ReflectionSecurityException} with the specified
   * detail message.
   *
   * @param message The detail message string.
   */
  public ReflectionSecurityException(
    final String message
  ) {
    super(message);
  }
}
