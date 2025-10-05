package me.kvdpxne.reflection.cache.invoker;

import me.kvdpxne.reflection.acessor.FieldAccessor;
import me.kvdpxne.reflection.exception.ReflectionSecurityException;
import me.kvdpxne.reflection.util.AccessController;

import java.lang.reflect.Field;

public final class FieldAccessorImpl implements FieldAccessor {

  private final Field field;
  private final boolean originalAccessible;
  private final String className;

  public FieldAccessorImpl(
    final Field field,
    final boolean originalAccessible,
    final String className
  ) {
    this.field = field;
    this.originalAccessible = originalAccessible;
    this.className = className;

    // Immediately enable access to constructor
    // This is safe because we'll restore state in finally block during invocation
    AccessController.setAccessible(this.field, true);
  }

  @Override
  public Object get(
    final Object target
  ) {
    try {
      return this.field.get(target);
    } catch (final IllegalAccessException exception) {
      throw new ReflectionSecurityException(
        "Illegal access to field '" + this.field.getName() + "' in " + this.className,
        exception
      );
    } finally {
      // CRITICAL: Always restore original accessibility state
      // Prevents security leaks and "illegal reflective access" warnings
      if (!this.originalAccessible) {
        AccessController.setAccessible(this.field, false);
      }
    }
  }

  @Override
  public void set(
    final Object target,
    final Object value
  ) {
    try {
      this.field.set(target, value);
    } catch (final IllegalAccessException exception) {
      throw new ReflectionSecurityException(
        "Illegal access to field '" + this.field.getName() + "' in " + this.className,
        exception
      );
    } finally {
      // CRITICAL: Always restore original accessibility state
      // Prevents security leaks and "illegal reflective access" warnings
      if (!this.originalAccessible) {
        AccessController.setAccessible(this.field, false);
      }
    }
  }
}
