package me.kvdpxne.modjit.cache;

import me.kvdpxne.modjit.exception.ClassNotFoundReflectionException;

/**
 * A specialized {@link me.kvdpxne.modjit.cache.ReflectionCache} for caching {@link java.lang.Class} objects loaded by
 * their string-based fully qualified names.
 * <p>
 * This cache ensures that class loading operations via {@link java.lang.Class#forName(java.lang.String)} are performed
 * only once for a given class path, improving performance for repeated lookups. It handles
 * {@link java.lang.ClassNotFoundException} by wrapping it in a
 * {@link me.kvdpxne.modjit.exception.ClassNotFoundReflectionException}.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public final class ClassCache
  extends
  ReflectionCache<String, Class<?>> {

  /**
   * Retrieves a {@link java.lang.Class} object from the cache or computes it by loading the class using its fully
   * qualified name if it is not already cached.
   * <p>
   * The cache key is the {@code path} string itself. If the class is not found, a
   * {@link me.kvdpxne.modjit.exception.ClassNotFoundReflectionException} is thrown.
   * </p>
   *
   * @param path The fully qualified name of the class to retrieve or load. Must not be {@code null} or blank.
   * @return The {@link java.lang.Class} object for the specified path.
   * @throws me.kvdpxne.modjit.exception.ClassNotFoundReflectionException if the class cannot be found by the
   *   specified path.
   * @throws java.lang.NullPointerException if the {@code path} is {@code null}.
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
