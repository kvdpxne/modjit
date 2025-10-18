package me.kvdpxne.modjit.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * A {@link WeakReference} that holds an additional key object for identifying cached entries.
 * <p>
 * This class extends {@link WeakReference} to associate a strong reference to a key object with the weakly referenced
 * value. This is particularly useful in scenarios where a {@link java.util.WeakHashMap} cannot be used directly, such
 * as when a {@link java.util.concurrent.ConcurrentMap} is required for caching. The key allows the cached entry to be
 * identified and removed when the value is garbage collected.
 * </p>
 * <p>
 * This implementation overrides {@link #equals(Object)} and {@link #hashCode()} to be based solely on the key, allowing
 * instances to be used as keys in maps or elements in sets where identity is determined by the associated key. This
 * enables efficient cache cleanup when the weakly referenced value is garbage collected.
 * </p>
 * <p>
 * Typical usage involves using this class in conjunction with a {@link ReferenceQueue} to clean up cache entries when
 * their values are no longer strongly reachable.
 * </p>
 *
 * @param <K> the type of the key object
 * @param <V> the type of the referent object
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public final class WeakReferenceWithKey<K, V>
  extends
  WeakReference<V> {

  /**
   * The key object associated with the weakly referenced value. This key is held by a strong reference and is used to
   * identify the cache entry for cleanup purposes.
   */
  private final K key;

  /**
   * Creates a new {@code WeakReferenceWithKey} that weakly references the given {@code referent} and is associated with
   * the given {@code key}.
   * <p>
   * The reference will be registered with the specified queue if provided, allowing for cleanup when the referent is
   * garbage collected.
   * </p>
   *
   * @param key the key object to associate with the referent; used for identifying the cache entry
   * @param referent the object to be weakly referenced; the value being cached
   * @param queue the queue with which the reference is to be registered, or {@code null} if registration is not
   *   required
   */
  public WeakReferenceWithKey(
    final K key,
    final V referent,
    final ReferenceQueue<? super V> queue
  ) {
    super(referent, queue);
    this.key = key;
  }

  /**
   * Returns the key object associated with the weakly referenced value.
   * <p>
   * The key is held by a strong reference and persists even after the referent has been garbage collected. This allows
   * the cache to identify which entry should be removed when the reference is enqueued.
   * </p>
   *
   * @return the key object associated with this weak reference
   */
  public K getKey() {
    return this.key;
  }

  /**
   * Compares this {@code WeakReferenceWithKey} with another object for equality.
   * <p>
   * Two instances are considered equal if they are of the same class and their keys are equal according to
   * {@link Objects#equals(Object, Object)}. The weakly referenced value is not considered in the equality check since
   * it may be garbage collected at any time.
   * </p>
   *
   * @param o the object to compare with
   * @return {@code true} if the objects are equal based on their keys, {@code false} otherwise
   */
  @Override
  public boolean equals(
    final Object o
  ) {
    if (null == o || this.getClass() != o.getClass()) {
      return false;
    }
    final WeakReferenceWithKey<?, ?> that = (WeakReferenceWithKey<?, ?>) o;
    return Objects.equals(this.key, that.key);
  }

  /**
   * Returns the hash code value for this {@code WeakReferenceWithKey}.
   * <p>
   * The hash code is computed based on the key object using {@link Objects#hashCode(Object)}. This ensures that
   * instances with equal keys have equal hash codes, making them suitable for use as keys in hash-based collections.
   * </p>
   *
   * @return the hash code value for this instance
   */
  @Override
  public int hashCode() {
    return Objects.hashCode(this.key);
  }
}
