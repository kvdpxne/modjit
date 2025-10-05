package me.kvdpxne.reflection.exception;

public class ReflectionSecurityException extends ReflectionException {

  public ReflectionSecurityException(final String message) {
    super(message);
  }

  public ReflectionSecurityException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
