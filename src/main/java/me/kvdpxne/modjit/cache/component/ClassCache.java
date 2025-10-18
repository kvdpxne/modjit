package me.kvdpxne.modjit.cache.component;

import me.kvdpxne.modjit.Reflection;
import me.kvdpxne.modjit.cache.ReflectionCache;
import me.kvdpxne.modjit.cache.key.ClassKey;
import me.kvdpxne.modjit.exception.ClassNotFoundReflectionException;

/**
 * A specialized cache for storing and retrieving {@link java.lang.Class} objects loaded by their fully qualified names
 * with configurable class loading parameters.
 * <p>
 * This cache extends {@link me.kvdpxne.modjit.cache.ReflectionCache} to provide efficient, thread-safe storage of class
 * objects. It ensures that class loading operations via {@link java.lang.Class#forName(String, boolean, ClassLoader)}
 * are performed only once for a given combination of class path, class loader, and initialization flag, significantly
 * improving performance for repeated class lookups.
 * </p>
 * <p>
 * The cache automatically handles class loader resolution by falling back to the context class loader of the current
 * thread, and ultimately to the class loader of the {@link me.kvdpxne.modjit.Reflection} class if no explicit class
 * loader is provided.
 * </p>
 * <p>
 * This implementation uses weak references to cached class objects, allowing them to be garbage collected when no
 * longer strongly referenced elsewhere, while maintaining cache performance benefits for frequently used classes.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @see me.kvdpxne.modjit.cache.ReflectionCache
 * @see me.kvdpxne.modjit.cache.key.ClassKey
 * @see java.lang.Class
 * @since 0.1.0
 */
public final class ClassCache
  extends
  ReflectionCache<ClassKey, Class<?>> {

  /**
   * Retrieves a {@link java.lang.Class} object from the cache or loads and caches it if not present.
   * <p>
   * This method performs class loading with the specified parameters, using a sophisticated class loader resolution
   * strategy. If no explicit class loader is provided, it attempts to use the current thread's context class loader,
   * falling back to the class loader of the {@link me.kvdpxne.modjit.Reflection} class if the context class loader is
   * unavailable.
   * </p>
   * <p>
   * The method is thread-safe and employs per-key locking to ensure that only one thread loads a class for a given key
   * combination concurrently, preventing redundant loading operations while maintaining thread safety.
   * </p>
   * <p>
   * If the class cannot be found by the specified path using the resolved class loader, a
   * {@link me.kvdpxne.modjit.exception.ClassNotFoundReflectionException} is thrown with the underlying
   * {@link java.lang.ClassNotFoundException} as the cause.
   * </p>
   *
   * @param path the fully qualified name of the class to retrieve or load; must not be {@code null} or blank
   *   (validated by the calling method in {@link me.kvdpxne.modjit.Reflection})
   * @param classLoader the class loader to use for loading the class; may be {@code null} to use the automatic class
   *   loader resolution strategy
   * @param initialize whether the class should be initialized during loading; when {@code true}, the class will be
   *   initialized as if by the Java Language Specification; when {@code false}, the class will be loaded but not
   *   initialized
   * @return the {@link java.lang.Class} object for the specified path, either retrieved from cache or newly loaded and
   *   cached; never {@code null}
   * @throws me.kvdpxne.modjit.exception.ClassNotFoundReflectionException if the class cannot be found by the
   *   specified path using the resolved class loader; the exception includes the original
   *   {@link java.lang.ClassNotFoundException} as the cause
   * @throws java.lang.LinkageError if the linkage fails during class loading
   * @throws java.lang.ExceptionInInitializerError if class initialization fails when {@code initialize} is
   *   {@code true}
   * @throws java.lang.SecurityException if a security manager is present and access to the class, class loader, or
   *   package is denied
   */
  public Class<?> getOrCompute(
    final String path,
    final ClassLoader classLoader,
    final boolean initialize
  ) {
    final ClassLoader foundClassLoader = null != classLoader
      ? classLoader
      : null != Thread.currentThread().getContextClassLoader()
      ? Thread.currentThread().getContextClassLoader()
      : Reflection.class.getClassLoader();

    return this.getOrCompute(
      new ClassKey(
        path,
        foundClassLoader,
        initialize
      ),
      () -> {
        try {
          return Class.forName(path, initialize, foundClassLoader);
        } catch (final ClassNotFoundException exception) {
          throw new ClassNotFoundReflectionException(path, exception);
        }
      });
  }
}
