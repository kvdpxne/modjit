package me.kvdpxne.modjit.cache;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import me.kvdpxne.modjit.acessor.ConstructorInitializer;
import me.kvdpxne.modjit.cache.invoker.ConstructorInitializerImpl;
import me.kvdpxne.modjit.cache.key.ConstructorKey;
import me.kvdpxne.modjit.exception.ConstructorNotFoundReflectionException;
import me.kvdpxne.modjit.util.AccessController;
import me.kvdpxne.modjit.util.ArrayMapper;

/**
 * A specialized {@link me.kvdpxne.modjit.cache.ReflectionCache} for caching
 * {@link me.kvdpxne.modjit.acessor.ConstructorInitializer} objects. It computes and caches
 * {@link me.kvdpxne.modjit.cache.invoker.ConstructorInitializerImpl} instances based on a
 * {@link me.kvdpxne.modjit.cache.key.ConstructorKey}.
 * <p>
 * This cache ensures that the lookup and preparation of constructors via
 * {@link java.lang.Class#getDeclaredConstructors()} and the creation of the corresponding
 * {@link me.kvdpxne.modjit.acessor.ConstructorInitializer} are performed only once for a given class and parameter
 * signature, improving performance for repeated accesses.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public final class ConstructorCache
  extends
  ReflectionCache<ConstructorKey, ConstructorInitializer> {

  /**
   * Checks if a given constructor matches the specified search criteria.
   * <p>
   * This helper method evaluates whether a candidate {@link java.lang.reflect.Constructor} object matches the provided
   * parameter types and modifiers. A criterion is considered matching if the corresponding input parameter is null (or
   * zero for modifiers) or if the constructor's attribute equals the specified value. For example, if
   * {@code parameterTypes} is null, the parameter type check is skipped. If {@code parameterTypes} is non-null, it's
   * compared using {@link java.util.Arrays#equals(java.lang.Object[], java.lang.Object[])}.
   * </p>
   *
   * @param constructor The candidate {@link java.lang.reflect.Constructor} to check.
   * @param parameterTypes The expected parameter types. Can be {@code null} to skip parameter type matching.
   * @param modifiers The required modifiers. Use {@code 0} to skip modifier matching.
   * @param nullParameterTypes {@code true} if {@code parameterTypes} was {@code null}.
   * @param emptyModifiers {@code true} if {@code modifiers} was {@code 0}.
   * @return {@code true} if the constructor matches all specified non-null/zero criteria, {@code false} otherwise.
   */
  private boolean checkConditions(
    final Constructor<?> constructor,
    final Class<?>[] parameterTypes,
    final int modifiers,
    final boolean nullParameterTypes,
    final boolean emptyModifiers
  ) {
    // noinspection MagicConstant
    return (nullParameterTypes || Arrays.equals(constructor.getParameterTypes(), parameterTypes))
      && (emptyModifiers || constructor.getModifiers() == modifiers);
  }

  /**
   * Looks up a declared constructor within the specified class that matches the given parameter types and modifiers,
   * and creates a new {@link me.kvdpxne.modjit.cache.invoker.ConstructorInitializerImpl} for it.
   * <p>
   * This method iterates through all declared constructors of the class and compares their parameter types (if
   * provided) and modifiers (if non-zero). If a match is found, it determines the original accessibility state of the
   * constructor and wraps it in a new {@code ConstructorInitializerImpl}. If no matching constructor is found, a
   * {@link me.kvdpxne.modjit.exception.ConstructorNotFoundReflectionException} is thrown.
   * </p>
   *
   * @param clazz The class in which to search for the constructor. Must not be {@code null}.
   * @param parameterTypes An array of {@link java.lang.Class} objects representing the expected parameter types of
   *   the constructor. Can be {@code null} if parameter types are not part of the search criteria.
   * @param modifiers The required modifiers for the constructor. Use {@code 0} to ignore modifiers.
   * @return A new {@link me.kvdpxne.modjit.cache.invoker.ConstructorInitializerImpl} instance wrapping the found
   *   constructor.
   * @throws me.kvdpxne.modjit.exception.ConstructorNotFoundReflectionException if no constructor with the specified
   *   parameter types (if provided) and modifiers (if non-zero) is found in the class.
   */
  private ConstructorInitializer computeConstructor(
    final Class<?> clazz,
    final Class<?>[] parameterTypes,
    final int modifiers
  ) {
    final Constructor<?>[] allConstructors = clazz.getDeclaredConstructors();
    Constructor<?> constructor = null;
    final boolean emptyModifiers = 0 == modifiers,
      nullParameterTypes = null == parameterTypes;
    for (final Constructor<?> nextConstructor : allConstructors) {
      if (this.checkConditions(nextConstructor, parameterTypes, modifiers,
        nullParameterTypes, emptyModifiers)
      ) {
        constructor = nextConstructor;
        break;
      }
    }
    if (null == constructor) {
      throw new ConstructorNotFoundReflectionException(clazz.getName(), parameterTypes);
    }
    final boolean originalAccessible = AccessController.isAccessible(constructor, null);
    return new ConstructorInitializerImpl(constructor, originalAccessible, clazz.getName());
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.acessor.ConstructorInitializer} from the cache or computes it if not present.
   * <p>
   * The cache key is constructed using the class name, the names of the parameter types (if provided), and the
   * modifiers. The computation is performed by the internal
   * {@link #computeConstructor(java.lang.Class, java.lang.Class[], int)} method.
   * </p>
   *
   * @param clazz The class for which to retrieve or compute the constructor accessor. Must not be {@code null}.
   * @param parameterTypes An array of {@link java.lang.Class} objects representing the expected parameter types of
   *   the constructor. Can be {@code null} if parameter types are not part of the search criteria.
   * @param modifiers The required modifiers for the constructor. Use {@code 0} to ignore modifiers.
   * @return The cached or newly computed {@link me.kvdpxne.modjit.acessor.ConstructorInitializer} for the specified
   *   constructor.
   * @throws me.kvdpxne.modjit.exception.ConstructorNotFoundReflectionException if no constructor with the specified
   *   parameter types (if provided) and modifiers (if non-zero) is found in the class.
   * @throws java.lang.NullPointerException if {@code clazz} is {@code null}.
   */
  public ConstructorInitializer getOrCompute(
    final Class<?> clazz,
    final Class<?>[] parameterTypes,
    final int modifiers
  ) {
    return this.getOrCompute(
      new ConstructorKey(
        clazz.getName(),
        null != parameterTypes
          ? ArrayMapper.mapToString(parameterTypes, Class::getName)
          : null,
        modifiers
      ),
      () -> this.computeConstructor(clazz, parameterTypes, modifiers)
    );
  }
}
