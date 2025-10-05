package me.kvdpxne.reflection;

import me.kvdpxne.reflection.acessor.ConstructorInitializer;
import me.kvdpxne.reflection.acessor.FieldAccessor;
import me.kvdpxne.reflection.acessor.MethodInvoker;
import me.kvdpxne.reflection.cache.ClassCache;
import me.kvdpxne.reflection.cache.ConstructorCache;
import me.kvdpxne.reflection.cache.FieldCache;
import me.kvdpxne.reflection.cache.MethodCache;
import me.kvdpxne.reflection.util.Validation;

public final class Reflection {

  private static final ClassCache CLASSES = ClassesCacheHolder.CACHE;

  private static final ConstructorCache CONSTRUCTORS = ConstructorsCacheHolder.CACHE;

  private static final FieldCache FIELDS = FieldsCacheHolder.CACHE;

  private static final MethodCache METHODS = MethodsCacheHolder.CACHE;

  public Reflection() {
    throw new AssertionError("");
  }

  public static Class<?> getClass(
    final String path
  ) {
    Validation.requireNotBlank(path, () -> "Class path cannot be blank. Provide valid path (e.g. 'entity.Player')");
    return CLASSES.getOrCompute(path);
  }

  public static FieldAccessor getField(
    final Class<?> clazz,
    final String fieldName,
    final Class<?> fieldType
  ) {
    Validation.requireNotNull(clazz, () -> "Class type cannot be null");
    Validation.requireNotBlank(fieldName, () -> "Field name cannot be blank. Provide valid name (e.g. 'playerConnection')");
    return FIELDS.getOrCompute(clazz, fieldName, fieldType);
  }

  public static FieldAccessor getField(
    final Class<?> clazz,
    final String fieldName
  ) {
    return getField(clazz, fieldName, null);
  }

  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final String methodName,
    final Class<?>[] parameterTypes,
    final Class<?> returnType
  ) {
    Validation.requireNotNull(clazz, () -> "Class type cannot be null");
    Validation.requireNotBlank(methodName, () -> "Method name cannot be blank.");
    return METHODS.getOrCompute(clazz, methodName, parameterTypes, returnType);
  }

  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final String methodName,
    final Class<?>[] parameterTypes
  ) {
    return getMethod(clazz, methodName, parameterTypes, null);
  }

  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final String methodName,
    final Class<?> returnType
  ) {
    return getMethod(clazz, methodName, null, returnType);
  }

  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final String methodName
  ) {
    return getMethod(clazz, methodName, null, null);
  }

  public static ConstructorInitializer getConstructor(
    final Class<?> clazz,
    final Class<?>[] parameterTypes
  ) {
    Validation.requireNotNull(clazz, () -> "Class type cannot be null");
    return CONSTRUCTORS.getOrCompute(clazz, parameterTypes);
  }

  public static ConstructorInitializer getConstructor(
    final Class<?> clazz
  ) {
    return getConstructor(clazz, null);
  }

  private static final class ClassesCacheHolder {
    private static final ClassCache CACHE = new ClassCache();
  }

  private static final class ConstructorsCacheHolder {
    private static final ConstructorCache CACHE = new ConstructorCache();
  }

  private static final class FieldsCacheHolder {
    private static final FieldCache CACHE = new FieldCache();
  }

  private static final class MethodsCacheHolder {
    private static final MethodCache CACHE = new MethodCache();
  }
}
