package me.kvdpxne.modjit.cache;

import me.kvdpxne.modjit.exception.ClassNotFoundReflectionException;

/**
 * A specialized {@link ReflectionCache} for caching {@link Class} objects
 * loaded by their string-based fully qualified names.
 * <p>
 * This cache ensures that class loading operations via {@link Class#forName(String)}
 * are performed only once for a given class path, improving performance for
 * repeated lookups. It handles {@link ClassNotFoundException} by wrapping it
 * in a {@link ClassNotFoundReflectionException}.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @since 0.1.0
 * @version 0.1.0
 */
public final class ClassCache
  extends
  ReflectionCache<String, Class<?>> {

  /**
   * Retrieves a {@link Class} object from the cache or computes it by loading
   * the class using its fully qualified name if it is not already cached.
   * <p>
   * The cache key is the {@code path} string itself. If the class is not found,
   * a {@link ClassNotFoundReflectionException} is thrown.
   * </p>
   *
   * @param path The fully qualified name of the class to retrieve or load.
   *             Must not be {@code null} or blank.
   * @return The {@link Class} object for the specified path.
   * @throws ClassNotFoundReflectionException if the class cannot be found
   *     by the specified path.
   * @throws NullPointerException if the {@code path} is {@code null}.
   */
  public Class<?> getOrCompute(
    final String path
  ) {
    return this.getOrCompute(path, () -> {
      try {
        return Class.forName(path);
      } catch (final ClassNotFoundException exception) {
        throw new ClassNotFoundReflectionException(path, exception);
      }
    });
  }
}
