package me.kvdpxne.modjit.cache;

import me.kvdpxne.modjit.acessor.MethodInvoker;
import me.kvdpxne.modjit.cache.invoker.MethodInvokerImpl;
import me.kvdpxne.modjit.cache.key.MethodKey;
import me.kvdpxne.modjit.exception.MethodNotFoundReflectionException;
import me.kvdpxne.modjit.util.AccessController;
import me.kvdpxne.modjit.util.ArrayMapper;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * A specialized {@link ReflectionCache} for caching {@link MethodInvoker}
 * objects. It computes and caches {@link MethodInvokerImpl} instances based
 * on a {@link MethodKey}.
 * <p>
 * This cache ensures that the lookup and preparation of methods via
 * {@link Class#getDeclaredMethods()} and the creation of the corresponding
 * {@link MethodInvoker} are performed only once for a given class, method name,
 * parameter signature, and optional return type, improving performance for
 * repeated accesses.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @since 0.1.0
 * @version 0.1.0
 */
public final class MethodCache
  extends
  ReflectionCache<MethodKey, MethodInvoker> {

  /**
   * Looks up a declared method within the specified class that matches the
   * given method name, optional parameter types, and optional return type,
   * and creates a new {@link MethodInvokerImpl} for it.
   * <p>
   * This method iterates through all declared methods of the class and
   * compares their names, parameter types (if provided), and return type (if provided).
   * If a match is found, it determines the original accessibility state of
   * the method and wraps it in a new {@code MethodInvokerImpl}.
   * If no matching method is found, a
   * {@link MethodNotFoundReflectionException} is thrown.
   * </p>
   *
   * @param clazz          The class in which to search for the method.
   *                       Must not be {@code null}.
   * @param methodName     The simple name of the method to find.
   *                       Must not be blank.
   * @param parameterTypes An array of {@link Class} objects representing the
   *                       expected parameter types of the method. Can be
   *                       {@code null} if parameter types are not part of the
   *                       search criteria.
   * @param returnType     The expected return type of the method. Can be
   *                       {@code null} if the return type is not part of the
   *                       search criteria.
   * @return A new {@link MethodInvokerImpl} instance wrapping the found method.
   * @throws MethodNotFoundReflectionException if no method with the specified
   *     name, parameter types (if provided), and return type (if provided)
   *     is found in the class.
   */
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
        break;
      }
    }
    if (null == method) {
      throw new MethodNotFoundReflectionException(clazz.getName(), methodName, parameterTypes, returnType);
    }
    final boolean originalAccessible = AccessController.isAccessible(method, null);
    return new MethodInvokerImpl(method, originalAccessible, clazz.getName());
  }

  /**
   * Retrieves a {@link MethodInvoker} from the cache or computes it if not present.
   * <p>
   * The cache key is constructed using the class name, method name, the
   * names of the parameter types (if provided), and the return type name (if provided).
   * The computation is performed by the internal {@link #computeMethod(Class, String, Class[], Class)}
   * method.
   * </p>
   *
   * @param clazz          The class for which to retrieve or compute the
   *                       method invoker. Must not be {@code null}.
   * @param methodName     The simple name of the method to access.
   *                       Must not be blank.
   * @param parameterTypes An array of {@link Class} objects representing the
   *                       expected parameter types of the method. Can be
   *                       {@code null} if parameter types are not part of the
   *                       search criteria.
   * @param returnType     The expected return type of the method. Can be
   *                       {@code null} if the return type is not part of the
   *                       search criteria.
   * @return The cached or newly computed {@link MethodInvoker} for the
   *     specified method.
   * @throws MethodNotFoundReflectionException if no method with the specified
   *     name, parameter types (if provided), and return type (if provided)
   *     is found in the class.
   * @throws NullPointerException if {@code clazz} is {@code null}, or
   *     {@code methodName} is {@code null}.
   */
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
