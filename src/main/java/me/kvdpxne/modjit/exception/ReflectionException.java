package me.kvdpxne.reflection.exception;

public class ReflectionException extends RuntimeException {

  public ReflectionException(final String message) {
    super(message);
  }

  public ReflectionException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public ReflectionException(final Throwable cause) {
    super(cause);
  }
}
