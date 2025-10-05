package me.kvdpxne.reflection.cache;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public abstract class ReflectionCache<K, V> {

  private final ConcurrentMap<K, Reference<V>> cache = new ConcurrentHashMap<>();

  private final ReferenceQueue<V> queue = new ReferenceQueue<>();

  private void cleanup() {
    Reference<?> reference;
    while (true) {
      reference = this.queue.poll();
      if (null == reference) {
        break;
      }
      for (final Map.Entry<K, Reference<V>> entry : this.cache.entrySet()) {
        if (entry.getValue().equals(reference)) {
          this.cache.remove(entry.getKey());
        }
      }
    }
  }

  protected V getOrCompute(
    final K intrigant,
    final Supplier<V> compute
  ) {
    this.cleanup();
    final V result = this.cache
      .computeIfAbsent(intrigant, (final K key) -> new WeakReference<>(compute.get()))
      .get();
    if (null == result) {
      throw new IllegalStateException("Cached value was garbage collected unexpectedly");
    }
    return result;
  }
}
