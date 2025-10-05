package me.kvdpxne.reflection.cache.invoker;

import me.kvdpxne.reflection.acessor.MethodInvoker;
import me.kvdpxne.reflection.exception.ReflectionException;
import me.kvdpxne.reflection.exception.ReflectionSecurityException;
import me.kvdpxne.reflection.util.AccessController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class MethodInvokerImpl implements MethodInvoker {

  private final Method method;
  private final boolean originalAccessible;
  private final String className;

  public MethodInvokerImpl(
    final Method method,
    final boolean originalAccessible,
    final String className
  ) {
    this.method = method;
    this.originalAccessible = originalAccessible;
    this.className = className;

    // Immediately enable access to constructor
    // This is safe because we'll restore state in finally block during invocation
    AccessController.setAccessible(this.method, true);
  }

  @Override
  public Object invoke(
    final Object target,
    final Object[] parameters
  ) {
    try {
      if (null == parameters) {
        return this.method.invoke(target);
      }
      return this.method.invoke(target, parameters);
    } catch (final InvocationTargetException exception) {
      final Throwable cause = exception.getCause();
      throw new ReflectionException(
        "Error invoking method '" + this.method.getName() + "' in " + this.className,
        null != cause ? cause : exception
      );
    } catch (final IllegalAccessException exception) {
      throw new ReflectionSecurityException(
        "Illegal access to method '" + this.method.getName() + "' in " + this.className,
        exception
      );
    } finally {
      // CRITICAL: Always restore original accessibility state
      // Prevents security leaks and "illegal reflective access" warnings
      if (!this.originalAccessible) {
        AccessController.setAccessible(this.method, false);
      }
    }
  }
}
