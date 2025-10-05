package me.kvdpxne.reflection.acessor;

public interface FieldAccessor {

  Object get(Object target);

  void set(Object target, Object value);

  default void setNull(Object target) {
    this.set(target, null);
  }
}
