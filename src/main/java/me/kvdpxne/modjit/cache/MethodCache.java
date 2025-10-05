package me.kvdpxne.reflection.cache;

import me.kvdpxne.reflection.acessor.MethodInvoker;
import me.kvdpxne.reflection.cache.invoker.MethodInvokerImpl;
import me.kvdpxne.reflection.cache.key.MethodKey;
import me.kvdpxne.reflection.util.AccessController;
import me.kvdpxne.reflection.util.ArrayMapper;

import java.lang.reflect.Method;
import java.util.Arrays;

public final class MethodCache extends ReflectionCache<MethodKey, MethodInvoker> {

  private MethodInvoker computeMethod(
    final Class<?> clazz,
    final String methodName,
    final Class<?>[] parameterTypes,
    final Class<?> returnType
  ) {
    final Method[] allMethods = clazz.getDeclaredMethods();
    Method method = null;
    for (final Method nextMethod : allMethods) {
      if (nextMethod.getName().equals(methodName)
        && (null == parameterTypes || Arrays.equals(nextMethod.getParameterTypes(), parameterTypes))
        && (null == returnType || nextMethod.getReturnType().equals(returnType))
      ) {
        method = nextMethod;
      }
    }
    if (null == method) {
      throw new IllegalArgumentException("No method found for " + methodName);
    }
    final boolean originalAccessible = AccessController.isAccessible(method, null);
    return new MethodInvokerImpl(method, originalAccessible, clazz.getName());
  }

  public MethodInvoker getOrCompute(
    final Class<?> clazz,
    final String methodName,
    final Class<?>[] parameterTypes,
    final Class<?> returnType
  ) {
    return this.getOrCompute(
      new MethodKey(
        clazz.getName(),
        methodName,
        null != parameterTypes
          ? ArrayMapper.mapToString(parameterTypes, Class::getName)
          : null,
        null != returnType
          ? returnType.getName()
          : null
      ),
      () -> this.computeMethod(clazz, methodName, parameterTypes, returnType)
    );
  }
}
