package me.kvdpxne.reflection.cache;

import me.kvdpxne.reflection.acessor.ConstructorInitializer;
import me.kvdpxne.reflection.cache.invoker.ConstructorInitializerImpl;
import me.kvdpxne.reflection.cache.key.ConstructorKey;
import me.kvdpxne.reflection.exception.ConstructorNotFoundReflectionException;
import me.kvdpxne.reflection.util.AccessController;
import me.kvdpxne.reflection.util.ArrayMapper;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public final class ConstructorCache
  extends
  ReflectionCache<ConstructorKey, ConstructorInitializer> {

  private ConstructorInitializer computeConstructor(
    final Class<?> clazz,
    final Class<?>[] parameterTypes
  ) {
    final Constructor<?>[] allConstructors = clazz.getDeclaredConstructors();
    Constructor<?> constructor = null;
    for (final Constructor<?> nextConstructor : allConstructors) {
      if (Arrays.equals(nextConstructor.getParameterTypes(), parameterTypes)) {
        constructor = nextConstructor;
      }
    }
    if (null == constructor) {
      throw new ConstructorNotFoundReflectionException(clazz.getName(), parameterTypes);
    }
    final boolean originalAccessible = AccessController.isAccessible(constructor, null);
    return new ConstructorInitializerImpl(constructor, originalAccessible, clazz.getName());
  }

  public ConstructorInitializer getOrCompute(
    final Class<?> clazz,
    final Class<?>[] parameterTypes
  ) {
    return this.getOrCompute(
      new ConstructorKey(
        clazz.getName(),
        ArrayMapper.mapToString(parameterTypes, Class::getName)
      ),
      () -> this.computeConstructor(clazz, parameterTypes)
    );
  }
}
