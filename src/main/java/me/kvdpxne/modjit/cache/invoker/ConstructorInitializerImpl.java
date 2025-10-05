package me.kvdpxne.reflection.cache.invoker;

import me.kvdpxne.reflection.acessor.ConstructorInitializer;
import me.kvdpxne.reflection.exception.ReflectionException;
import me.kvdpxne.reflection.exception.ReflectionSecurityException;
import me.kvdpxne.reflection.util.AccessController;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class ConstructorInitializerImpl implements ConstructorInitializer {

  private final Constructor<?> constructor;
  private final boolean originalAccessible;
  private final String className;

  public ConstructorInitializerImpl(
    final Constructor<?> constructor,
    final boolean originalAccessible,
    final String className
  ) {
    this.constructor = constructor;
    this.originalAccessible = originalAccessible;
    this.className = className;

    // Immediately enable access to constructor
    // This is safe because we'll restore state in finally block during invocation
    AccessController.setAccessible(this.constructor, true);
  }


  @Override
  public Object newInstance(final Object[] parameters) {
    try {
      if (null == parameters) {
        return this.constructor.newInstance();
      }
      return this.constructor.newInstance(parameters);
    } catch (InvocationTargetException exception) {
      final Throwable cause = exception.getCause();
      throw new ReflectionException(
        "Error constructing " + this.className,
        null != cause ? cause : exception
      );
    } catch (final InstantiationException exception) {
      throw new ReflectionException(
        "Cannot instantiate abstract class " + this.className,
        exception
      );
    } catch (final IllegalAccessException exception) {
      throw new ReflectionSecurityException(
        "Illegal access to constructor of " + this.className,
        exception
      );
    } finally {
      // CRITICAL: Always restore original accessibility state
      // Prevents security leaks and "illegal reflective access" warnings
      if (!this.originalAccessible) {
        AccessController.setAccessible(this.constructor, false);
      }
    }
  }
}
