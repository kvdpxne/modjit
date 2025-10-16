package me.kvdpxne.modjit.cache;

import java.lang.reflect.Method;
import java.util.Arrays;
import me.kvdpxne.modjit.accessor.MethodInvoker;
import me.kvdpxne.modjit.cache.invoker.MethodInvokerImpl;
import me.kvdpxne.modjit.cache.key.MethodKey;
import me.kvdpxne.modjit.exception.MethodNotFoundReflectionException;
import me.kvdpxne.modjit.util.AccessController;
import me.kvdpxne.modjit.util.ArrayMapper;

/**
 * A specialized {@link me.kvdpxne.modjit.cache.ReflectionCache} for caching
 * {@link me.kvdpxne.modjit.accessor.MethodInvoker} objects. It computes and caches
 * {@link me.kvdpxne.modjit.cache.invoker.MethodInvokerImpl} instances based on a
 * {@link me.kvdpxne.modjit.cache.key.MethodKey}.
 * <p>
 * This cache ensures that the lookup and preparation of methods via {@link java.lang.Class#getDeclaredMethods()} and
 * the creation of the corresponding {@link me.kvdpxne.modjit.accessor.MethodInvoker} are performed only once for a
 * given class, method name, parameter signature, and optional return type, improving performance for repeated
 * accesses.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public final class MethodCache
  extends
  ReflectionCache<MethodKey, MethodInvoker> {

  /**
   * Checks if a given method matches the specified search criteria.
   * <p>
   * This helper method evaluates whether a candidate {@link java.lang.reflect.Method} object matches the provided name,
   * parameter types, return type, and modifiers. A criterion is considered matching if the corresponding input
   * parameter is null (or zero for modifiers) or if the method's attribute equals the specified value. For example, if
   * {@code name} is null, the name check is skipped. If {@code parameterTypes} is non-null, it's compared using
   * {@link java.util.Arrays#equals(java.lang.Object[], java.lang.Object[])}.
   * </p>
   *
   * @param method The candidate {@link java.lang.reflect.Method} to check.
   * @param name The expected method name. Can be {@code null} to skip name matching.
   * @param parameterTypes The expected parameter types. Can be {@code null} to skip parameter type matching.
   * @param returnType The expected return type. Can be {@code null} to skip return type matching.
   * @param modifiers The required modifiers. Use {@code 0} to skip modifier matching.
   * @param nullName {@code true} if {@code name} was {@code null}.
   * @param nullParameterTypes {@code true} if {@code parameterTypes} was {@code null}.
   * @param nullReturnType {@code true} if {@code returnType} was {@code null}.
   * @param emptyModifiers {@code true} if {@code modifiers} was {@code 0}.
   * @return {@code true} if the method matches all specified non-null/zero criteria, {@code false} otherwise.
   */
  private boolean checkConditions(
    final Method method,
    final String name,
    final Class<?>[] parameterTypes,
    final Class<?> returnType,
    final int modifiers,
    final boolean nullName,
    final boolean nullParameterTypes,
    final boolean nullReturnType,
    final boolean emptyModifiers
  ) {
    // noinspection MagicConstant
    return (nullName || method.getName().equals(name))
      && (nullParameterTypes || Arrays.equals(method.getParameterTypes(), parameterTypes))
      && (nullReturnType || method.getReturnType().equals(returnType))
      && (emptyModifiers || method.getModifiers() == modifiers);
  }

  /**
   * Looks up a declared method within the specified class that matches the given method name, optional parameter types,
   * and optional return type, and creates a new {@link me.kvdpxne.modjit.cache.invoker.MethodInvokerImpl} for it.
   * <p>
   * This method iterates through all declared methods of the class and compares their names, parameter types (if
   * provided), and return type (if provided). If a match is found, it determines the original accessibility state of
   * the method and wraps it in a new {@code MethodInvokerImpl}. If no matching method is found, a
   * {@link me.kvdpxne.modjit.exception.MethodNotFoundReflectionException} is thrown.
   * </p>
   *
   * @param clazz The class in which to search for the method. Must not be {@code null}.
   * @param name The simple name of the method to find. Can be {@code null} if name is not part of the search
   *   criteria.
   * @param parameterTypes An array of {@link java.lang.Class} objects representing the expected parameter types of
   *   the method. Can be {@code null} if parameter types are not part of the search criteria.
   * @param returnType The expected return type of the method. Can be {@code null} if the return type is not part of
   *   the search criteria.
   * @param modifiers The required modifiers for the method. Use {@code 0} to ignore modifiers.
   * @return A new {@link me.kvdpxne.modjit.cache.invoker.MethodInvokerImpl} instance wrapping the found method.
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if no method with the specified name,
   *   parameter types (if provided), return type (if provided), and modifiers (if non-zero) is found in the class.
   */
  private MethodInvoker computeMethod(
    final Class<?> clazz,
    final String name,
    final Class<?>[] parameterTypes,
    final Class<?> returnType,
    final int modifiers
  ) {
    final Method[] allMethods = clazz.getDeclaredMethods();
    Method method = null;
    final boolean emptyModifiers = 0 == modifiers,
      nullReturnType = null == returnType,
      nullParameterTypes = null == parameterTypes,
      nullName = null == name;
    for (final Method nextMethod : allMethods) {
      if (this.checkConditions(nextMethod, name, parameterTypes, returnType, modifiers,
        nullName, nullParameterTypes, nullReturnType, emptyModifiers)
      ) {
        method = nextMethod;
        break;
      }
    }
    if (null == method) {
      throw new MethodNotFoundReflectionException(clazz.getName(), name, parameterTypes, returnType);
    }
    final boolean originalAccessible = AccessController.isAccessible(method, null);
    return new MethodInvokerImpl(method, originalAccessible, clazz.getName());
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} from the cache or computes it if not present.
   * <p>
   * The cache key is constructed using the class name, method name, the names of the parameter types (if provided), the
   * return type name (if provided), and the modifiers. The computation is performed by the internal
   * {@link #computeMethod(java.lang.Class, java.lang.String, java.lang.Class[], java.lang.Class, int)} method.
   * </p>
   *
   * @param clazz The class for which to retrieve or compute the method invoker. Must not be {@code null}.
   * @param name The simple name of the method to access. Can be {@code null} if name is not part of the search
   *   criteria.
   * @param parameterTypes An array of {@link java.lang.Class} objects representing the expected parameter types of
   *   the method. Can be {@code null} if parameter types are not part of the search criteria.
   * @param returnType The expected return type of the method. Can be {@code null} if the return type is not part of
   *   the search criteria.
   * @param modifiers The required modifiers for the method. Use {@code 0} to ignore modifiers.
   * @return The cached or newly computed {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method.
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if no method with the specified name,
   *   parameter types (if provided), return type (if provided), and modifiers (if non-zero) is found in the class.
   * @throws java.lang.NullPointerException if {@code clazz} is {@code null}.
   */
  public MethodInvoker getOrCompute(
    final Class<?> clazz,
    final String name,
    final Class<?>[] parameterTypes,
    final Class<?> returnType,
    final int modifiers
  ) {
    return this.getOrCompute(
      new MethodKey(
        clazz.getName(),
        name,
        null != parameterTypes
          ? ArrayMapper.mapToString(parameterTypes, Class::getName)
          : null,
        null != returnType
          ? returnType.getName()
          : null,
        modifiers
      ),
      () -> this.computeMethod(clazz, name, parameterTypes, returnType, modifiers)
    );
  }
}
