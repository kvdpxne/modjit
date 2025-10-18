package me.kvdpxne.modjit.cache.component;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import me.kvdpxne.modjit.accessor.ConstructorInitializer;
import me.kvdpxne.modjit.accessor.impl.ConstructorInitializerImpl;
import me.kvdpxne.modjit.cache.ReflectionCache;
import me.kvdpxne.modjit.cache.key.ConstructorKey;
import me.kvdpxne.modjit.exception.ConstructorNotFoundReflectionException;
import me.kvdpxne.modjit.util.AccessController;
import me.kvdpxne.modjit.util.ArrayMapper;

/**
 * A specialized cache for storing and retrieving {@link me.kvdpxne.modjit.accessor.ConstructorInitializer} objects that
 * provide access to class constructors through reflection.
 * <p>
 * This cache extends {@link me.kvdpxne.modjit.cache.ReflectionCache} to provide efficient, thread-safe storage of
 * constructor accessors. It ensures that constructor lookup operations via
 * {@link java.lang.Class#getDeclaredConstructors()} and the creation of corresponding
 * {@link me.kvdpxne.modjit.accessor.ConstructorInitializer} instances are performed only once for a given combination
 * of class, parameter types, and modifiers.
 * </p>
 * <p>
 * The cache automatically handles constructor accessibility management and provides optimized lookup strategies,
 * including special handling for single-constructor classes to improve performance. All cached constructor accessors
 * are wrapped in {@link me.kvdpxne.modjit.accessor.impl.ConstructorInitializerImpl} instances that manage accessibility
 * state and exception translation.
 * </p>
 * <p>
 * This implementation uses weak references to cached constructor accessors, allowing them to be garbage collected when
 * no longer strongly referenced, while maintaining performance benefits for frequently accessed constructors.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @see me.kvdpxne.modjit.cache.ReflectionCache
 * @see me.kvdpxne.modjit.cache.key.ConstructorKey
 * @see me.kvdpxne.modjit.accessor.ConstructorInitializer
 * @since 0.1.0
 */
public final class ConstructorCache
  extends
  ReflectionCache<ConstructorKey, ConstructorInitializer> {

  /**
   * Determines whether a candidate constructor matches the specified search criteria.
   * <p>
   * This helper method evaluates a {@link java.lang.reflect.Constructor} against the provided parameter types and
   * modifier requirements. Each criterion is optional - if parameter types are {@code null}, parameter matching is
   * skipped; if modifiers are zero, modifier matching is skipped. This allows flexible constructor lookup based on
   * partial criteria.
   * </p>
   *
   * @param constructor the candidate {@link java.lang.reflect.Constructor} to evaluate; must not be {@code null}
   * @param parameterTypes the expected parameter types for the constructor; may be {@code null} to skip parameter
   *   type matching
   * @param modifiers the required modifiers for the constructor; use {@code 0} to skip modifier matching
   * @param nullParameterTypes {@code true} if {@code parameterTypes} was {@code null}, indicating parameter type
   *   matching should be skipped
   * @param emptyModifiers {@code true} if {@code modifiers} was {@code 0}, indicating modifier matching should be
   *   skipped
   * @return {@code true} if the constructor matches all specified non-null criteria; {@code false} if any specified
   *   criterion does not match
   */
  private static boolean checkConditions(
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
   * Looks up a declared constructor and creates a corresponding constructor initializer.
   * <p>
   * This method searches through all declared constructors of the specified class to find one that matches the given
   * parameter types and modifiers. If no matching constructor is found, a
   * {@link me.kvdpxne.modjit.exception.ConstructorNotFoundReflectionException} is thrown.
   * </p>
   * <p>
   * For performance optimization, if the class has only one constructor and no specific parameter types or modifiers
   * are specified, that single constructor is automatically selected without iterating through the constructor array.
   * </p>
   * <p>
   * The method determines the original accessibility state of the constructor and creates a
   * {@link me.kvdpxne.modjit.accessor.impl.ConstructorInitializerImpl} that will manage accessibility during object
   * instantiation.
   * </p>
   *
   * @param clazz the class in which to search for the constructor; must not be {@code null}
   * @param parameterTypes an array of {@link java.lang.Class} objects representing the expected parameter types of
   *   the constructor; may be {@code null} to match constructors regardless of parameter types
   * @param modifiers the required modifiers for the constructor; use {@code 0} to match constructors regardless of
   *   modifiers
   * @return a new {@link me.kvdpxne.modjit.accessor.impl.ConstructorInitializerImpl} instance wrapping the found
   *   constructor; never {@code null}
   * @throws me.kvdpxne.modjit.exception.ConstructorNotFoundReflectionException if no constructor with the specified
   *   parameter types (if provided) and modifiers (if non-zero) is found in the class
   * @throws java.lang.NullPointerException if {@code clazz} is {@code null}
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
    if (1 == allConstructors.length && nullParameterTypes && emptyModifiers) {
      constructor = allConstructors[0];
    } else {
      for (final Constructor<?> nextConstructor : allConstructors) {
        if (ConstructorCache.checkConditions(nextConstructor, parameterTypes, modifiers,
          nullParameterTypes, emptyModifiers)
        ) {
          constructor = nextConstructor;
          break;
        }
      }
      if (null == constructor) {
        throw new ConstructorNotFoundReflectionException(clazz.getName(), parameterTypes);
      }
    }
    final boolean originalAccessible = AccessController.isAccessible(constructor, null);
    return new ConstructorInitializerImpl(constructor, originalAccessible, clazz.getName());
  }

  /**
   * Retrieves a constructor initializer from the cache or computes and caches it if not present.
   * <p>
   * This method provides thread-safe access to constructor accessors, ensuring that only one thread computes the
   * constructor initializer for a given key combination concurrently. The cache key is constructed from the class name,
   * parameter type names (if provided), and modifiers.
   * </p>
   * <p>
   * Parameter types are converted to their fully qualified names for the cache key, allowing proper distinction between
   * constructors with different parameter signatures. The actual constructor lookup is performed by the internal
   * {@link #computeConstructor(java.lang.Class, java.lang.Class[], int)} method.
   * </p>
   *
   * @param clazz the class for which to retrieve or compute the constructor accessor; must not be {@code null}
   * @param parameterTypes an array of {@link java.lang.Class} objects representing the expected parameter types of
   *   the constructor; may be {@code null} to match constructors regardless of parameter types
   * @param modifiers the required modifiers for the constructor; use {@code 0} to match constructors regardless of
   *   modifiers
   * @return the cached or newly computed {@link me.kvdpxne.modjit.accessor.ConstructorInitializer} for the specified
   *   constructor; never {@code null}
   * @throws me.kvdpxne.modjit.exception.ConstructorNotFoundReflectionException if no constructor with the specified
   *   parameter types (if provided) and modifiers (if non-zero) is found in the class
   * @throws java.lang.NullPointerException if {@code clazz} is {@code null}
   * @throws java.lang.SecurityException if access to the class's declared constructors is denied
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
