package me.kvdpxne.modjit;

import me.kvdpxne.modjit.acessor.ConstructorInitializer;
import me.kvdpxne.modjit.acessor.FieldAccessor;
import me.kvdpxne.modjit.acessor.MethodInvoker;
import me.kvdpxne.modjit.cache.ClassCache;
import me.kvdpxne.modjit.cache.ConstructorCache;
import me.kvdpxne.modjit.cache.FieldCache;
import me.kvdpxne.modjit.cache.MethodCache;
import me.kvdpxne.modjit.util.Validation;

/**
 * Provides a high-level, user-friendly, and cached API for Java Reflection.
 * <p>
 * This utility class abstracts the complexities of direct reflection usage
 * (e.g., {@link java.lang.reflect.Field}, {@link java.lang.reflect.Method},
 * {@link java.lang.reflect.Constructor}). It offers methods to retrieve
 * cached {@link Class} objects, {@link FieldAccessor} instances for field
 * access, {@link MethodInvoker} instances for method invocation, and
 * {@link ConstructorInitializer} instances for object creation.
 * </p>
 * <p>
 * All reflection operations are performed through internal caches
 * ({@link ClassCache}, {@link FieldCache}, {@link MethodCache}, {@link ConstructorCache})
 * to improve performance by avoiding repeated lookups of reflection objects.
 * It also handles accessibility concerns internally using the
 * {@link me.kvdpxne.modjit.util.AccessController}.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @since 0.1.0
 * @version 0.1.0
 */
public final class Reflection {

  /**
   * The singleton instance of the class cache.
   */
  private static final ClassCache CLASSES = ClassesCacheHolder.CACHE;

  /**
   * The singleton instance of the constructor cache.
   */
  private static final ConstructorCache CONSTRUCTORS = ConstructorsCacheHolder.CACHE;

  /**
   * The singleton instance of the field cache.
   */
  private static final FieldCache FIELDS = FieldsCacheHolder.CACHE;

  /**
   * The singleton instance of the method cache.
   */
  private static final MethodCache METHODS = MethodsCacheHolder.CACHE;

  /**
   * Prevents instantiation of this utility class.
   */
  public Reflection() {
    throw new AssertionError();
  }

  /**
   * Retrieves a {@link Class} object by its fully qualified name.
   * <p>
   * The class is loaded using {@link Class#forName(String)} and cached
   * for subsequent calls with the same path.
   * </p>
   *
   * @param path The fully qualified name of the class to retrieve.
   *             Must not be {@code null} or blank.
   * @return The {@link Class} object for the specified path.
   * @throws me.kvdpxne.modjit.exception.ClassNotFoundReflectionException if the class
   *     cannot be found by the specified path.
   * @throws IllegalArgumentException if the {@code path} is blank.
   */
  public static Class<?> getClass(
    final String path
  ) {
    Validation.requireNotBlank(path, () -> "Class path cannot be blank. Provide valid path (e.g. 'entity.Player')");
    return CLASSES.getOrCompute(path);
  }

  /**
   * Retrieves a {@link FieldAccessor} for a field in the specified class,
   * optionally matching its type.
   * <p>
   * The field is looked up using {@link Class#getDeclaredField(String)}
   * and its corresponding {@code FieldAccessor} is cached for subsequent
   * calls with the same class, field name, and type combination.
   * </p>
   *
   * @param clazz     The class in which to find the field.
   *                  Must not be {@code null}.
   * @param fieldName The simple name of the field to find.
   *                  Must not be blank.
   * @param fieldType The expected type of the field. Can be {@code null}
   *                  if the type is not part of the search criteria.
   * @return A {@link FieldAccessor} for the specified field.
   * @throws me.kvdpxne.modjit.exception.FieldNotFoundReflectionException if the field
   *     is not found in the specified class with the given name and type.
   * @throws IllegalArgumentException if {@code clazz} is {@code null} or
   *     {@code fieldName} is blank.
   */
  public static FieldAccessor getField(
    final Class<?> clazz,
    final String fieldName,
    final Class<?> fieldType
  ) {
    Validation.requireNotNull(clazz, () -> "Class type cannot be null");
    Validation.requireNotBlank(fieldName, () -> "Field name cannot be blank. Provide valid name (e.g. 'playerConnection')");
    return FIELDS.getOrCompute(clazz, fieldName, fieldType);
  }

  /**
   * Retrieves a {@link FieldAccessor} for a field in the specified class,
   * without specifying the field type.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getField(Class, String, Class)} with {@code fieldType} as {@code null}.
   * </p>
   *
   * @param clazz     The class in which to find the field.
   *                  Must not be {@code null}.
   * @param fieldName The simple name of the field to find.
   *                  Must not be blank.
   * @return A {@link FieldAccessor} for the specified field.
   * @throws me.kvdpxne.modjit.exception.FieldNotFoundReflectionException if the field
   *     is not found in the specified class with the given name.
   * @throws IllegalArgumentException if {@code clazz} is {@code null} or
   *     {@code fieldName} is blank.
   */
  public static FieldAccessor getField(
    final Class<?> clazz,
    final String fieldName
  ) {
    return getField(clazz, fieldName, null);
  }

  /**
   * Retrieves a {@link MethodInvoker} for a method in the specified class,
   * matching its name, parameter types, and return type.
   * <p>
   * The method is looked up using {@link Class#getDeclaredMethod(String, Class[])}
   * (or similar logic for declared methods) and its corresponding
   * {@code MethodInvoker} is cached for subsequent calls with the same
   * class, method name, parameter types, and return type combination.
   * </p>
   *
   * @param clazz          The class in which to find the method.
   *                       Must not be {@code null}.
   * @param methodName     The simple name of the method to find.
   *                       Must not be blank.
   * @param parameterTypes An array of {@link Class} objects representing the
   *                       parameter types of the method. Can be {@code null}
   *                       if the method takes no parameters or if parameter
   *                       types are not part of the search criteria.
   * @param returnType     The expected return type of the method. Can be
   *                       {@code null} if the return type is not part of the
   *                       search criteria.
   * @return A {@link MethodInvoker} for the specified method.
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if the method
   *     is not found in the specified class with the given name, parameter
   *     types, and return type.
   * @throws IllegalArgumentException if {@code clazz} is {@code null} or
   *     {@code methodName} is blank.
   */
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

  /**
   * Retrieves a {@link MethodInvoker} for a method in the specified class,
   * matching its name and parameter types, without specifying the return type.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getMethod(Class, String, Class[], Class)} with {@code returnType} as {@code null}.
   * </p>
   *
   * @param clazz          The class in which to find the method.
   *                       Must not be {@code null}.
   * @param methodName     The simple name of the method to find.
   *                       Must not be blank.
   * @param parameterTypes An array of {@link Class} objects representing the
   *                       parameter types of the method. Can be {@code null}
   *                       if the method takes no parameters or if parameter
   *                       types are not part of the search criteria.
   * @return A {@link MethodInvoker} for the specified method.
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if the method
   *     is not found in the specified class with the given name and parameter
   *     types.
   * @throws IllegalArgumentException if {@code clazz} is {@code null} or
   *     {@code methodName} is blank.
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final String methodName,
    final Class<?>[] parameterTypes
  ) {
    return getMethod(clazz, methodName, parameterTypes, null);
  }

  /**
   * Retrieves a {@link MethodInvoker} for a method in the specified class,
   * matching its name and return type, without specifying parameter types.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getMethod(Class, String, Class[], Class)} with {@code parameterTypes} as {@code null}.
   * </p>
   *
   * @param clazz      The class in which to find the method.
   *                 Must not be {@code null}.
   * @param methodName The simple name of the method to find.
   *                 Must not be blank.
   * @param returnType The expected return type of the method. Can be
   *                 {@code null} if the return type is not part of the
   *                 search criteria.
   * @return A {@link MethodInvoker} for the specified method.
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if the method
   *     is not found in the specified class with the given name and return
   *     type.
   * @throws IllegalArgumentException if {@code clazz} is {@code null} or
   *     {@code methodName} is blank.
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final String methodName,
    final Class<?> returnType
  ) {
    return getMethod(clazz, methodName, null, returnType);
  }

  /**
   * Retrieves a {@link MethodInvoker} for a method in the specified class,
   * matching only its name, without specifying parameter types or return type.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getMethod(Class, String, Class[], Class)} with both {@code parameterTypes}
   * and {@code returnType} as {@code null}. This will match the first method
   * found with the given name, regardless of its signature.
   * </p>
   *
   * @param clazz      The class in which to find the method.
   *                 Must not be {@code null}.
   * @param methodName The simple name of the method to find.
   *                 Must not be blank.
   * @return A {@link MethodInvoker} for the specified method.
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if the method
   *     is not found in the specified class with the given name.
   * @throws IllegalArgumentException if {@code clazz} is {@code null} or
   *     {@code methodName} is blank.
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final String methodName
  ) {
    return getMethod(clazz, methodName, null, null);
  }

  /**
   * Retrieves a {@link ConstructorInitializer} for a constructor in the
   * specified class, matching its parameter types.
   * <p>
   * The constructor is looked up using {@link Class#getDeclaredConstructor(Class[])}
   * (or similar logic for declared constructors) and its corresponding
   * {@code ConstructorInitializer} is cached for subsequent calls with the
   * same class and parameter types combination.
   * </p>
   *
   * @param clazz          The class for which to find the constructor.
   *                       Must not be {@code null}.
   * @param parameterTypes An array of {@link Class} objects representing the
   *                       parameter types of the constructor. Can be {@code null}
   *                       if the constructor takes no parameters.
   * @return A {@link ConstructorInitializer} for the specified constructor.
   * @throws me.kvdpxne.modjit.exception.ConstructorNotFoundReflectionException if the constructor
   *     is not found in the specified class with the given parameter types.
   * @throws IllegalArgumentException if {@code clazz} is {@code null}.
   */
  public static ConstructorInitializer getConstructor(
    final Class<?> clazz,
    final Class<?>[] parameterTypes
  ) {
    Validation.requireNotNull(clazz, () -> "Class type cannot be null");
    return CONSTRUCTORS.getOrCompute(clazz, parameterTypes);
  }

  /**
   * Retrieves a {@link ConstructorInitializer} for the default (no-argument)
   * constructor in the specified class.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getConstructor(Class, Class[])} with {@code parameterTypes} as {@code null}.
   * </p>
   *
   * @param clazz The class for which to find the default constructor.
   *              Must not be {@code null}.
   * @return A {@link ConstructorInitializer} for the default constructor.
   * @throws me.kvdpxne.modjit.exception.ConstructorNotFoundReflectionException if the default
   *     constructor is not found in the specified class.
   * @throws IllegalArgumentException if {@code clazz} is {@code null}.
   */
  public static ConstructorInitializer getConstructor(
    final Class<?> clazz
  ) {
    return getConstructor(clazz, null);
  }

  /**
   * Holder class for the singleton {@link ClassCache} instance.
   * Uses the Initialization-on-demand holder idiom for thread-safe lazy loading.
   */
  private static final class ClassesCacheHolder {
    private static final ClassCache CACHE = new ClassCache();
  }

  /**
   * Holder class for the singleton {@link ConstructorCache} instance.
   * Uses the Initialization-on-demand holder idiom for thread-safe lazy loading.
   */
  private static final class ConstructorsCacheHolder {
    private static final ConstructorCache CACHE = new ConstructorCache();
  }

  /**
   * Holder class for the singleton {@link FieldCache} instance.
   * Uses the Initialization-on-demand holder idiom for thread-safe lazy loading.
   */
  private static final class FieldsCacheHolder {
    private static final FieldCache CACHE = new FieldCache();
  }

  /**
   * Holder class for the singleton {@link MethodCache} instance.
   * Uses the Initialization-on-demand holder idiom for thread-safe lazy loading.
   */
  private static final class MethodsCacheHolder {
    private static final MethodCache CACHE = new MethodCache();
  }
}
