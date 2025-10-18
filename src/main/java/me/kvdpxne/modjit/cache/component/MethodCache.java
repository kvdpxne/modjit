package me.kvdpxne.modjit.cache.component;

import java.lang.reflect.Method;
import java.util.Arrays;
import me.kvdpxne.modjit.accessor.MethodInvoker;
import me.kvdpxne.modjit.accessor.impl.MethodInvokerImpl;
import me.kvdpxne.modjit.cache.ReflectionCache;
import me.kvdpxne.modjit.cache.key.MethodKey;
import me.kvdpxne.modjit.exception.MethodNotFoundReflectionException;
import me.kvdpxne.modjit.util.AccessController;
import me.kvdpxne.modjit.util.ArrayMapper;

/**
 * A specialized cache for storing and retrieving {@link me.kvdpxne.modjit.accessor.MethodInvoker} objects that provide
 * access to class methods through reflection.
 * <p>
 * This cache extends {@link me.kvdpxne.modjit.cache.ReflectionCache} to provide efficient, thread-safe storage of
 * method invokers. It ensures that method lookup operations via {@link java.lang.Class#getDeclaredMethods()} and the
 * creation of corresponding {@link me.kvdpxne.modjit.accessor.MethodInvoker} instances are performed only once for a
 * given combination of class, method name, parameter types, return type, and modifiers.
 * </p>
 * <p>
 * The cache supports highly flexible method lookup criteria, allowing searches by name alone, parameter types alone,
 * return type alone, modifiers alone, or any combination thereof. This enables precise method resolution even in
 * complex class hierarchies with overloaded methods. All cached method invokers are wrapped in
 * {@link me.kvdpxne.modjit.accessor.impl.MethodInvokerImpl} instances that manage accessibility state and exception
 * translation.
 * </p>
 * <p>
 * This implementation uses weak references to cached method invokers, allowing them to be garbage collected when no
 * longer strongly referenced, while maintaining performance benefits for frequently invoked methods.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @see me.kvdpxne.modjit.cache.ReflectionCache
 * @see me.kvdpxne.modjit.cache.key.MethodKey
 * @see me.kvdpxne.modjit.accessor.MethodInvoker
 * @since 0.1.0
 */
public final class MethodCache
  extends
  ReflectionCache<MethodKey, MethodInvoker> {

  /**
   * Determines whether a candidate method matches the specified search criteria.
   * <p>
   * This helper method evaluates a {@link java.lang.reflect.Method} against the provided name, parameter types, return
   * type, and modifier requirements. Each criterion is optional - if name is {@code null}, name matching is skipped; if
   * parameter types are {@code null}, parameter type matching is skipped; if return type is {@code null}, return type
   * matching is skipped; if modifiers are zero, modifier matching is skipped. This allows flexible method lookup based
   * on partial criteria.
   * </p>
   *
   * @param method the candidate {@link java.lang.reflect.Method} to evaluate; must not be {@code null}
   * @param name the expected name of the method; may be {@code null} to skip name matching
   * @param parameterTypes the expected parameter types for the method; may be {@code null} to skip parameter type
   *   matching
   * @param returnType the expected return type of the method; may be {@code null} to skip return type matching
   * @param modifiers the required modifiers for the method; use {@code 0} to skip modifier matching
   * @param nullName {@code true} if {@code name} was {@code null}, indicating name matching should be skipped
   * @param nullParameterTypes {@code true} if {@code parameterTypes} was {@code null}, indicating parameter type
   *   matching should be skipped
   * @param nullReturnType {@code true} if {@code returnType} was {@code null}, indicating return type matching should
   *   be skipped
   * @param emptyModifiers {@code true} if {@code modifiers} was {@code 0}, indicating modifier matching should be
   *   skipped
   * @return {@code true} if the method matches all specified non-null criteria; {@code false} if any specified
   *   criterion does not match
   */
  private static boolean checkConditions(
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
   * Looks up a declared method and creates a corresponding method invoker.
   * <p>
   * This method searches through all declared methods of the specified class to find one that matches the given name,
   * parameter types, return type, and modifiers. If no matching method is found, a
   * {@link me.kvdpxne.modjit.exception.MethodNotFoundReflectionException} is thrown.
   * </p>
   * <p>
   * The search criteria are combined using logical AND - a method must match all specified non-null criteria to be
   * selected. This allows precise method resolution in scenarios with method overloading, where multiple methods share
   * the same name but differ in parameters or return type.
   * </p>
   * <p>
   * The method determines the original accessibility state of the method and creates a
   * {@link me.kvdpxne.modjit.accessor.impl.MethodInvokerImpl} that will manage accessibility during method invocation
   * operations.
   * </p>
   *
   * @param clazz the class in which to search for the method; must not be {@code null}
   * @param name the simple name of the method to find; may be {@code null} to match methods regardless of name
   * @param parameterTypes an array of {@link java.lang.Class} objects representing the expected parameter types of
   *   the method; may be {@code null} to match methods regardless of parameter types
   * @param returnType the expected return type of the method; may be {@code null} to match methods regardless of
   *   return type
   * @param modifiers the required modifiers for the method; use {@code 0} to match methods regardless of modifiers
   * @return a new {@link me.kvdpxne.modjit.accessor.impl.MethodInvokerImpl} instance wrapping the found method; never
   *   {@code null}
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if no method with the specified name (if
   *   provided), parameter types (if provided), return type (if provided), and modifiers (if non-zero) is found in the
   *   class
   * @throws java.lang.NullPointerException if {@code clazz} is {@code null}
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
      if (MethodCache.checkConditions(nextMethod, name, parameterTypes, returnType, modifiers,
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
   * Retrieves a method invoker from the cache or computes and caches it if not present.
   * <p>
   * This method provides thread-safe access to method invokers, ensuring that only one thread computes the method
   * invoker for a given key combination concurrently. The cache key is constructed from the class name, method name,
   * parameter type names (if provided), return type name (if provided), and modifiers.
   * </p>
   * <p>
   * Parameter types and return type are converted to their fully qualified names for the cache key, allowing proper
   * distinction between methods with different signatures. The actual method lookup is performed by the internal
   * {@link #computeMethod(java.lang.Class, java.lang.String, java.lang.Class[], java.lang.Class, int)} method.
   * </p>
   *
   * @param clazz the class for which to retrieve or compute the method invoker; must not be {@code null}
   * @param name the simple name of the method to access; may be {@code null} to match methods regardless of name
   * @param parameterTypes an array of {@link java.lang.Class} objects representing the expected parameter types of
   *   the method; may be {@code null} to match methods regardless of parameter types
   * @param returnType the expected return type of the method; may be {@code null} to match methods regardless of
   *   return type
   * @param modifiers the required modifiers for the method; use {@code 0} to match methods regardless of modifiers
   * @return the cached or newly computed {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method;
   *   never {@code null}
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if no method with the specified name (if
   *   provided), parameter types (if provided), return type (if provided), and modifiers (if non-zero) is found in the
   *   class
   * @throws java.lang.NullPointerException if {@code clazz} is {@code null}
   * @throws java.lang.SecurityException if access to the class's declared methods is denied
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
