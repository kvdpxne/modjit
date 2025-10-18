package me.kvdpxne.modjit.cache;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import me.kvdpxne.modjit.util.WeakReferenceWithKey;

/**
 * An abstract base class for implementing reflection-based caches with weak references.
 * <p>
 * This class provides a thread-safe caching mechanism using {@link ConcurrentHashMap} for storing weak references to
 * cached values. It utilizes a {@link ReferenceQueue} to track when cached values are garbage collected and
 * automatically removes the corresponding entries from the cache to prevent memory leaks.
 * </p>
 * <p>
 * The cache employs a per-key locking mechanism to ensure that only one thread computes the value for a specific key
 * concurrently, preventing redundant computations while maintaining thread safety.
 * </p>
 * <p>
 * Subclasses must implement the logic for computing the cached values by using the protected
 * {@link #getOrCompute(Object, Supplier)} method. This base class handles the concurrency, weak referencing, and
 * cleanup logic automatically.
 * </p>
 * <p>
 * The cache uses {@link WeakReferenceWithKey} to associate cache keys with weakly referenced values, enabling automatic
 * cleanup when values are garbage collected while maintaining the ability to identify which cache entry should be
 * removed.
 * </p>
 *
 * @param <K> the type of the cache key; must implement proper {@code equals()} and {@code hashCode()} methods
 * @param <V> the type of the cached value
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public abstract class ReflectionCache<K, V> {

  /**
   * A map of locks used to synchronize access to individual cache entries identified by their keys.
   * <p>
   * This prevents race conditions during the computation and storage of new values for the same key. Each key has its
   * own lock object, ensuring that only one thread can compute the value for a specific key at a time while allowing
   * concurrent computations for different keys.
   * </p>
   */
  private final ConcurrentMap<K, Object> locks = new ConcurrentHashMap<>();

  /**
   * The main cache storage mapping keys to {@link Reference} objects.
   * <p>
   * It maps keys to {@link WeakReferenceWithKey} instances which hold the cached values. Using weak references allows
   * the garbage collector to reclaim the values when they are no longer strongly reachable elsewhere, preventing memory
   * leaks while maintaining cache performance.
   * </p>
   */
  private final ConcurrentMap<K, Reference<V>> cache = new ConcurrentHashMap<>();

  /**
   * The reference queue associated with this cache for tracking garbage collected values.
   * <p>
   * When a cached value is garbage collected, its corresponding {@link WeakReferenceWithKey} is enqueued here. The
   * {@link #cleanup()} method processes this queue to remove stale entries from the {@link #cache}.
   * </p>
   */
  private final ReferenceQueue<V> queue = new ReferenceQueue<>();

  /**
   * Processes the reference queue to remove entries for values that have been garbage collected.
   * <p>
   * This method iterates through the queue and removes the corresponding key-value pair from the main cache map if the
   * reference in the map is the same as the one that was enqueued (and thus garbage collected).
   * </p>
   * <p>
   * This cleanup process is automatically called before each cache access attempt to ensure the cache does not retain
   * references to garbage collected objects.
   * </p>
   */
  private void cleanup() {
    Reference<?> reference;
    while (null != (reference = this.queue.poll())) {
      if (!(reference instanceof WeakReferenceWithKey)) {
        // Should not happen if only WeakReferenceWithKey is used, but good practice to check.
        continue;
      }
      final WeakReferenceWithKey<?, ?> withKey = (WeakReferenceWithKey<?, ?>) reference;
      // Use key from the reference to remove the entry from the cache map.
      // The check 'reference == this.cache.get(key)' is implicitly handled by ConcurrentMap.remove(key, value).
      // noinspection SuspiciousMethodCalls - withKey.getKey() is the correct key to use.
      this.cache.remove(withKey.getKey(), withKey);
    }
  }

  /**
   * Retrieves a value from the cache associated with the given key.
   * <p>
   * If the value is not present or has been garbage collected, it computes the value using the provided {@code compute}
   * supplier, stores it in the cache, and returns the new value.
   * </p>
   * <p>
   * This method is thread-safe and uses a per-key lock to ensure that only one thread computes the value for a specific
   * key concurrently. If multiple threads request the same missing key simultaneously, only one will compute the value
   * while others wait for the computation to complete.
   * </p>
   * <p>
   * The method automatically performs cleanup of garbage collected entries before attempting to access the cache to
   * ensure stale entries are removed.
   * </p>
   *
   * @param key the key whose associated value is to be returned or computed; must not be {@code null}
   * @param compute a {@link Supplier} function that computes the value if it is not found in the cache; must not be
   *   {@code null}
   * @return the cached value associated with the key, or the newly computed value if it was not present or had been
   *   garbage collected
   * @throws NullPointerException if {@code key} or {@code compute} is {@code null}
   */
  protected V getOrCompute(
    final K key,
    final Supplier<V> compute
  ) {
    // Get or create a lock object for this specific key to synchronize computation.
    final Object keyLock = this.locks.computeIfAbsent(key, (Object u0) -> new Object());
    synchronized (keyLock) {
      try {
        // Clean up any stale references before accessing the cache.
        this.cleanup();
        // Attempt to get the existing reference from the cache.
        final Reference<V> reference = this.cache.get(key);
        if (null != reference) {
          // Get the actual value from the weak reference.
          final V value = reference.get();
          if (null != value) {
            // Value is still alive, return it.
            return value;
          }
          // Value has been garbage collected, remove the stale reference.
          this.cache.remove(key, reference);
        }
        // Value was not found or was garbage collected, compute it.
        final V newValue = compute.get();
        // Wrap the new value in a WeakReferenceWithKey and associate it with the queue.
        final Reference<V> newReference = new WeakReferenceWithKey<>(key, newValue, this.queue);
        // Store the new reference in the cache.
        this.cache.put(key, newReference);
        // Return the newly computed value.
        return newValue;
      } finally {
        // Always remove the lock for this key after computation is done
        // (either successfully or via exception) to free up resources.
        this.locks.remove(key, keyLock);
      }
    }
  }
}
