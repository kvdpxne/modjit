package me.kvdpxne.reflection.acessor;

public interface MethodInvoker {

  Object invoke(Object target, Object[] parameters);

  default Object invoke(Object target) {
    return this.invoke(target, new Object[0]);
  }
}
