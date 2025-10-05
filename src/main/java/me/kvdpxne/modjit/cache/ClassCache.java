package me.kvdpxne.reflection.cache;

import me.kvdpxne.reflection.exception.ClassNotFoundReflectionException;

public final class ClassCache extends ReflectionCache<String, Class<?>> {

  /**
   *
   */
  public Class<?> getOrCompute(final String path) {
    return this.getOrCompute(path, () -> {
      try {
        return Class.forName(path);
      } catch (final ClassNotFoundException exception) {
        throw new ClassNotFoundReflectionException(path, exception);
      }
    });
  }
}
