package me.kvdpxne.reflection.cache;

import me.kvdpxne.reflection.acessor.FieldAccessor;
import me.kvdpxne.reflection.cache.invoker.FieldAccessorImpl;
import me.kvdpxne.reflection.cache.key.FieldKey;
import me.kvdpxne.reflection.util.AccessController;

import java.lang.reflect.Field;

public final class FieldCache extends ReflectionCache<FieldKey, FieldAccessor> {

  private FieldAccessor computeField(
    final Class<?> clazz,
    final String fieldName,
    final Class<?> returnType
  ) {
    final Field[] allFields = clazz.getDeclaredFields();
    Field field = null;
    for (final Field nextField : allFields) {
      if (nextField.getName().equals(fieldName)
        && (null == returnType || nextField.getType().equals(returnType))
      ) {
        field = nextField;
      }
    }
    if (null == field) {
      throw new RuntimeException();
    }
    final boolean originalAccessible = AccessController.isAccessible(field, null);
    return new FieldAccessorImpl(field, originalAccessible, clazz.getName());
  }

  public FieldAccessor getOrCompute(
    final Class<?> clazz,
    final String fieldName,
    final Class<?> returnType
  ) {
    return this.getOrCompute(
      new FieldKey(
        clazz.getName(),
        fieldName,
        null != returnType
          ? returnType.getName()
          : null
      ),
      () -> this.computeField(clazz, fieldName, returnType)
    );
  }
}
