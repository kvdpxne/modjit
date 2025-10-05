package me.kvdpxne.reflection.acessor;

public interface ConstructorInitializer {

  Object newInstance(Object[] parameters);

  default Object newInstance() {
    return this.newInstance(null);
  }
}
