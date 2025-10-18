package me.kvdpxne.modjit.cache.key;

import java.util.Objects;

/**
 * Represents a key for caching {@link java.lang.Class} objects within the reflection library.
 * <p>
 * This class encapsulates the identifying attributes required to uniquely identify a class loading operation: the fully
 * qualified class path, the {@link ClassLoader} used for loading, and the initialization flag that determines whether
 * the class should be initialized during loading.
 * </p>
 * <p>
 * It is designed for use as a key in map-based caches, providing consistent implementations of {@link #equals(Object)}
 * and {@link #hashCode()} that consider all three components. The class loader comparison uses identity comparison via
 * {@link System#identityHashCode(Object)} to ensure different class loader instances are treated as distinct keys even
 * if they would normally be considered equal.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public final class ClassKey {

  /**
   * The fully qualified name of the class to be loaded (e.g., "java.lang.String").
   */
  private final String path;

  /**
   * The class loader used to load the class, or {@code null} if using the system class loader or context class loader.
   */
  private final ClassLoader classLoader;

  /**
   * Whether the class should be initialized during loading, as specified in
   * {@link Class#forName(String, boolean, ClassLoader)}.
   */
  private final boolean initialize;

  /**
   * Constructs a new {@code ClassKey} instance with the specified class loading parameters.
   *
   * @param path the fully qualified name of the class to be loaded; must not be {@code null}
   * @param classLoader the class loader used to load the class; may be {@code null} to indicate the system class
   *   loader or context class loader should be used
   * @param initialize {@code true} if the class should be initialized during loading, {@code false} otherwise
   */
  public ClassKey(
    final String path,
    final ClassLoader classLoader,
    final boolean initialize
  ) {
    this.path = path;
    this.classLoader = classLoader;
    this.initialize = initialize;
  }

  /**
   * Compares this {@code ClassKey} with another object for equality.
   * <p>
   * Two instances are considered equal if they have the same class path, the same class loader (using identity
   * comparison), and the same initialization flag.
   * </p>
   *
   * @param o the object to compare with
   * @return {@code true} if the objects are equal based on path, class loader identity, and initialization flag;
   *   {@code false} otherwise
   */
  @Override
  public boolean equals(
    final Object o
  ) {
    if (null == o || this.getClass() != o.getClass()) {
      return false;
    }
    final ClassKey that = (ClassKey) o;
    return this.initialize == that.initialize
      && Objects.equals(this.path, that.path)
      && Objects.equals(this.classLoader, that.classLoader);
  }

  /**
   * Returns the hash code value for this {@code ClassKey}.
   * <p>
   * The hash code is computed based on the class path, the identity hash code of the class loader (using
   * {@link System#identityHashCode(Object)}), and the initialization flag. This ensures that different class loader
   * instances produce different hash codes even if they contain the same classes.
   * </p>
   *
   * @return the hash code value for this instance
   */
  @Override
  public int hashCode() {
    return Objects.hash(
      this.path,
      System.identityHashCode(this.classLoader),
      this.initialize
    );
  }
}
