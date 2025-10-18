package me.kvdpxne.modjit.cache.key;

import java.util.Objects;

/**
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public final class ClassKey {

  private final String path;

  private final ClassLoader classLoader;

  private final boolean initialize;

  public ClassKey(
    final String path,
    final ClassLoader classLoader,
    final boolean initialize
  ) {
    this.path = path;
    this.classLoader = classLoader;
    this.initialize = initialize;
  }

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

  @Override
  public int hashCode() {
    return Objects.hash(
      this.path,
      System.identityHashCode(this.classLoader),
      this.initialize
    );
  }
}
