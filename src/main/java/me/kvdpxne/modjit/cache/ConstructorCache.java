package me.kvdpxne.modjit.cache;

import me.kvdpxne.modjit.acessor.ConstructorInitializer;
import me.kvdpxne.modjit.cache.invoker.ConstructorInitializerImpl;
import me.kvdpxne.modjit.cache.key.ConstructorKey;
import me.kvdpxne.modjit.exception.ConstructorNotFoundReflectionException;
import me.kvdpxne.modjit.util.AccessController;
import me.kvdpxne.modjit.util.ArrayMapper;
import java.lang.reflect.Constructor;
import java.util.Arrays;

/**
 * A specialized {@link ReflectionCache} for caching {@link ConstructorInitializer}
 * objects. It computes and caches {@link ConstructorInitializerImpl} instances
 * based on a {@link ConstructorKey}.
 * <p>
 * This cache ensures that the lookup and preparation of constructors via
 * {@link Class#getDeclaredConstructors()} and the creation of the corresponding
 * {@link ConstructorInitializer} are performed only once for a given class
 * and parameter signature, improving performance for repeated accesses.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @since 0.1.0
 * @version 0.1.0
 */
public final class ConstructorCache
  extends
  ReflectionCache<ConstructorKey, ConstructorInitializer> {

  /**
   * Looks up a declared constructor within the specified class that matches
   * the given parameter types and creates a new {@link ConstructorInitializerImpl}
   * for it.
   * <p>
   * This method iterates through all declared constructors of the class
   * and compares their parameter types using {@link Arrays#equals(Object[], Object[])}.
   * If a match is found, it determines the original accessibility state of
   * the constructor and wraps it in a new {@code ConstructorInitializerImpl}.
   * If no matching constructor is found, a
   * {@link ConstructorNotFoundReflectionException} is thrown.
   * </p>
   *
   * @param clazz          The class in which to search for the constructor.
   * @param parameterTypes An array of {@link Class} objects representing the
   *                       parameter types of the desired constructor. Can be
   *                       {@code null} if the constructor takes no parameters.
   * @return A new {@link ConstructorInitializerImpl} instance wrapping the
   *     found constructor.
   * @throws ConstructorNotFoundReflectionException if no constructor with the
   *     specified parameter types is found in the class.
   */
  private ConstructorInitializer computeConstructor(
    final Class<?> clazz,
    final Class<?>[] parameterTypes
  ) {
    final Constructor<?>[] allConstructors = clazz.getDeclaredConstructors();
    Constructor<?> constructor = null;
    for (final Constructor<?> nextConstructor : allConstructors) {
      if (Arrays.equals(nextConstructor.getParameterTypes(), parameterTypes)) {
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
   * Retrieves a {@link ConstructorInitializer} from the cache or computes it
   * if not present.
   * <p>
   * The cache key is constructed using the class name and the names of the
   * parameter types, obtained via {@link ArrayMapper#mapToString(Object[], java.util.function.Function)}.
   * The computation is performed by the internal {@link #computeConstructor(Class, Class[])}
   * method.
   * </p>
   *
   * @param clazz          The class for which to retrieve or compute the
   *                       constructor accessor. Must not be {@code null}.
   * @param parameterTypes An array of {@link Class} objects representing the
   *                       parameter types of the desired constructor. Can be
   *                       {@code null} if the constructor takes no parameters.
   * @return The cached or newly computed {@link ConstructorInitializer} for
   *     the specified constructor.
   * @throws ConstructorNotFoundReflectionException if no constructor with the
   *     specified parameter types is found in the class.
   * @throws NullPointerException if {@code clazz} is {@code null}.
   */
  public ConstructorInitializer getOrCompute(
    final Class<?> clazz,
    final Class<?>[] parameterTypes
  ) {
    return this.getOrCompute(
      new ConstructorKey(
        clazz.getName(),
        ArrayMapper.mapToString(parameterTypes, Class::getName)
      ),
      () -> this.computeConstructor(clazz, parameterTypes)
    );
  }
}
