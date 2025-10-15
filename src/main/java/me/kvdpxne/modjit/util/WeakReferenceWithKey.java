package me.kvdpxne.modjit.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * A {@link java.lang.ref.WeakReference} that holds an additional key object.
 * <p>
 * This class extends {@code WeakReference} to associate a strong reference to a key object with the weakly referenced
 * value. This is particularly useful in scenarios where a {@link java.util.WeakHashMap} cannot be used directly, such
 * as when a {@link java.util.concurrent.ConcurrentMap} is required for the cache. The key allows the cached entry to be
 * identified and removed when the value is garbage collected.
 * </p>
 * <p>
 * This implementation also overrides {@link #equals(java.lang.Object)} and {@link #hashCode()} to be based solely on
 * the key, allowing instances to be used as keys in maps or elements in sets where identity is determined by the
 * associated key.
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
   * The key object associated with the weakly referenced value. This key is held by a strong reference.
   */
  private final K key;

  /**
   * Creates a new {@code WeakReferenceWithKey} that weakly references the given {@code referent} and is associated with
   * the given {@code key}.
   *
   * @param key the key object to associate with the referent
   * @param referent the object to be weakly referenced
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
   *
   * @return the key object
   */
  public K getKey() {
    return this.key;
  }

  /**
   * Compares this {@code WeakReferenceWithKey} with another object for equality. Two instances are considered equal if
   * they are of the same class and their keys are equal according to
   * {@link java.util.Objects#equals(java.lang.Object, java.lang.Object)}.
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
   * Returns the hash code value for this {@code WeakReferenceWithKey}. The hash code is computed based on the key
   * object using {@link java.util.Objects#hashCode(java.lang.Object)}.
   *
   * @return the hash code value for this instance
   */
  @Override
  public int hashCode() {
    return Objects.hashCode(this.key);
  }
}
