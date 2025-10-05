package me.kvdpxne.reflection.exception;

public class ClassNotFoundReflectionException extends ReflectionException {
  public ClassNotFoundReflectionException(String path, Throwable cause) {
    super("Class not found: " + path, cause);
  }
}
