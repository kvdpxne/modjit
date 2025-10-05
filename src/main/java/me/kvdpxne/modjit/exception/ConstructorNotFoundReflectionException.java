package me.kvdpxne.reflection.exception;

import java.util.Arrays;

public class ConstructorNotFoundReflectionException extends ReflectionException {

  public ConstructorNotFoundReflectionException(
    final String className,
    final Class<?>[] parameterTypes
  ) {
    super("Constructor not found for " + className + " with parameters: " + Arrays.toString(parameterTypes));
  }
}
