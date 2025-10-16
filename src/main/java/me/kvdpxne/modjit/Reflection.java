package me.kvdpxne.modjit;

import java.lang.reflect.Modifier;
import me.kvdpxne.modjit.accessor.ConstructorInitializer;
import me.kvdpxne.modjit.accessor.FieldAccessor;
import me.kvdpxne.modjit.accessor.MethodInvoker;
import me.kvdpxne.modjit.cache.ClassCache;
import me.kvdpxne.modjit.cache.ConstructorCache;
import me.kvdpxne.modjit.cache.FieldCache;
import me.kvdpxne.modjit.cache.MethodCache;
import static me.kvdpxne.modjit.util.Validation.require;
import static me.kvdpxne.modjit.util.Validation.requireNotBlank;
import static me.kvdpxne.modjit.util.Validation.requireNotNull;

/**
 * Provides a high-level, user-friendly, and cached API for Java Reflection.
 * <p>
 * This utility class abstracts the complexities of direct reflection usage (e.g., {@link java.lang.reflect.Field},
 * {@link java.lang.reflect.Method}, {@link java.lang.reflect.Constructor}). It offers methods to retrieve cached
 * {@link java.lang.Class} objects, {@link me.kvdpxne.modjit.accessor.FieldAccessor} instances for field access,
 * {@link me.kvdpxne.modjit.accessor.MethodInvoker} instances for method invocation, and
 * {@link me.kvdpxne.modjit.accessor.ConstructorInitializer} instances for object creation.
 * </p>
 * <p>
 * All reflection operations are performed through internal caches ({@link me.kvdpxne.modjit.cache.ClassCache},
 * {@link me.kvdpxne.modjit.cache.FieldCache}, {@link me.kvdpxne.modjit.cache.MethodCache},
 * {@link me.kvdpxne.modjit.cache.ConstructorCache}) to improve performance by avoiding repeated lookups of reflection
 * objects. It also handles accessibility concerns internally using the
 * {@link me.kvdpxne.modjit.util.AccessController}.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
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
   * Retrieves a {@link java.lang.Class} object by its fully qualified name.
   * <p>
   * The class is loaded using {@link java.lang.Class#forName(java.lang.String)} and cached for subsequent calls with
   * the same path.
   * </p>
   *
   * @param path The fully qualified name of the class to retrieve. Must not be {@code null} or blank.
   * @return The {@link java.lang.Class} object for the specified path.
   * @throws me.kvdpxne.modjit.exception.ClassNotFoundReflectionException if the class cannot be found by the
   *   specified path.
   * @throws java.lang.IllegalArgumentException if the {@code path} is blank.
   */
  public static Class<?> getClass(
    final String path
  ) {
    requireNotBlank(path, () -> "Class path cannot be blank. Provide valid path (e.g. 'entity.Player')");
    return CLASSES.getOrCompute(path);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.FieldAccessor} for a field in the specified class, optionally
   * matching its type and modifiers.
   * <p>
   * The field is looked up using {@link java.lang.Class#getDeclaredField(java.lang.String)} and its corresponding
   * {@code FieldAccessor} is cached for subsequent calls with the same class, field name, and type combination.
   * </p>
   *
   * @param clazz The class in which to find the field. Must not be {@code null}.
   * @param fieldName The simple name of the field to find. Must not be blank if {@code fieldType} and
   *   {@code modifiers} are both unspecified.
   * @param fieldType The expected type of the field. Can be {@code null} if the type is not part of the search
   *   criteria.
   * @param modifiers The required modifiers for the field, as defined by
   *   {@link java.lang.reflect.Modifier#fieldModifiers()}. Use {@code 0} to ignore modifiers.
   * @return A {@link me.kvdpxne.modjit.accessor.FieldAccessor} for the specified field.
   * @throws me.kvdpxne.modjit.exception.FieldNotFoundReflectionException if the field is not found in the specified
   *   class with the given name, type, and modifiers.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null}, {@code fieldName} is blank when
   *   name-based lookup is required, or {@code modifiers} contains invalid field modifier bits.
   */
  public static FieldAccessor getField(
    final Class<?> clazz,
    final String fieldName,
    final Class<?> fieldType,
    final int modifiers
  ) {
    requireNotNull(clazz, () -> "Class type cannot be null");
    final boolean hasName = null != fieldName;
    require(hasName || null != fieldType || 0 == modifiers,
      () -> "At least one of fieldName, fieldType, or modifiers (non-zero) must be specified."
    );
    if (hasName) {
      requireNotBlank(fieldName, () -> "Field name cannot be blank. Provide valid name (e.g. 'playerConnection')");
    }
    require(0 == (modifiers & ~Modifier.fieldModifiers()), () -> "Invalid field modifiers specified.");
    return FIELDS.getOrCompute(clazz, fieldName, fieldType, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.FieldAccessor} for a field in the specified class, matching its name
   * and type.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getField(java.lang.Class, java.lang.String, java.lang.Class, int)} with {@code modifiers} as {@code 0}.
   * </p>
   *
   * @param clazz The class in which to find the field. Must not be {@code null}.
   * @param fieldName The simple name of the field to find. Must not be blank.
   * @param fieldType The expected type of the field. Can be {@code null} if the type is not part of the search
   *   criteria.
   * @return A {@link me.kvdpxne.modjit.accessor.FieldAccessor} for the specified field.
   * @throws me.kvdpxne.modjit.exception.FieldNotFoundReflectionException if the field is not found in the specified
   *   class with the given name and type.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null} or {@code fieldName} is blank.
   */
  public static FieldAccessor getField(
    final Class<?> clazz,
    final String fieldName,
    final Class<?> fieldType
  ) {
    return getField(clazz, fieldName, fieldType, 0);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.FieldAccessor} for a field in the specified class, matching its name
   * and modifiers.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getField(java.lang.Class, java.lang.String, java.lang.Class, int)} with {@code fieldType} as {@code null}.
   * </p>
   *
   * @param clazz The class in which to find the field. Must not be {@code null}.
   * @param fieldName The simple name of the field to find. Must not be blank if modifiers are zero.
   * @param modifiers The required modifiers for the field, as defined by
   *   {@link java.lang.reflect.Modifier#fieldModifiers()}. Use {@code 0} to ignore modifiers.
   * @return A {@link me.kvdpxne.modjit.accessor.FieldAccessor} for the specified field.
   * @throws me.kvdpxne.modjit.exception.FieldNotFoundReflectionException if the field is not found in the specified
   *   class with the given name and modifiers.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null}, {@code fieldName} is blank when
   *   name-based lookup is required, or {@code modifiers} contains invalid field modifier bits.
   */
  public static FieldAccessor getField(
    final Class<?> clazz,
    final String fieldName,
    final int modifiers
  ) {
    return getField(clazz, fieldName, null, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.FieldAccessor} for a field in the specified class, matching its type
   * and modifiers.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getField(java.lang.Class, java.lang.String, java.lang.Class, int)} with {@code fieldName} as {@code null}.
   * </p>
   *
   * @param clazz The class in which to find the field. Must not be {@code null}.
   * @param fieldType The expected type of the field. Must not be {@code null} if modifiers are zero.
   * @param modifiers The required modifiers for the field, as defined by
   *   {@link java.lang.reflect.Modifier#fieldModifiers()}. Use {@code 0} to ignore modifiers.
   * @return A {@link me.kvdpxne.modjit.accessor.FieldAccessor} for the specified field.
   * @throws me.kvdpxne.modjit.exception.FieldNotFoundReflectionException if the field is not found in the specified
   *   class with the given type and modifiers.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null}, {@code fieldType} is {@code null}
   *   when type-based lookup is required, or {@code modifiers} contains invalid field modifier bits.
   */
  public static FieldAccessor getField(
    final Class<?> clazz,
    final Class<?> fieldType,
    final int modifiers
  ) {
    return getField(clazz, null, fieldType, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.FieldAccessor} for a field in the specified class, matching its
   * name.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getField(java.lang.Class, java.lang.String, java.lang.Class, int)} with {@code fieldType} as {@code null}
   * and {@code modifiers} as {@code 0}.
   * </p>
   *
   * @param clazz The class in which to find the field. Must not be {@code null}.
   * @param fieldName The simple name of the field to find. Must not be blank.
   * @return A {@link me.kvdpxne.modjit.accessor.FieldAccessor} for the specified field.
   * @throws me.kvdpxne.modjit.exception.FieldNotFoundReflectionException if the field is not found in the specified
   *   class with the given name.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null} or {@code fieldName} is blank.
   */
  public static FieldAccessor getField(
    final Class<?> clazz,
    final String fieldName
  ) {
    return getField(clazz, fieldName, null, 0);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.FieldAccessor} for a field in the specified class, matching its
   * type.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getField(java.lang.Class, java.lang.String, java.lang.Class, int)} with {@code fieldName} as {@code null}
   * and {@code modifiers} as {@code 0}.
   * </p>
   *
   * @param clazz The class in which to find the field. Must not be {@code null}.
   * @param fieldType The expected type of the field.
   * @return A {@link me.kvdpxne.modjit.accessor.FieldAccessor} for the specified field.
   * @throws me.kvdpxne.modjit.exception.FieldNotFoundReflectionException if the field is not found in the specified
   *   class with the given type.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null}.
   */
  public static FieldAccessor getField(
    final Class<?> clazz,
    final Class<?> fieldType
  ) {
    return getField(clazz, null, fieldType, 0);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.FieldAccessor} for a field in the specified class, matching its
   * modifiers.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getField(java.lang.Class, java.lang.String, java.lang.Class, int)} with {@code fieldName} and
   * {@code fieldType} as {@code null}.
   * </p>
   *
   * @param clazz The class in which to find the field. Must not be {@code null}.
   * @param modifiers The required modifiers for the field, as defined by
   *   {@link java.lang.reflect.Modifier#fieldModifiers()}. Must not be zero.
   * @return A {@link me.kvdpxne.modjit.accessor.FieldAccessor} for the specified field.
   * @throws me.kvdpxne.modjit.exception.FieldNotFoundReflectionException if the field is not found in the specified
   *   class with the given modifiers.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null}, {@code modifiers} is zero, or
   *   {@code modifiers} contains invalid field modifier bits.
   */
  public static FieldAccessor getField(
    final Class<?> clazz,
    final int modifiers
  ) {
    return getField(clazz, null, null, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * name, parameter types, return type, and modifiers.
   * <p>
   * The method is looked up using {@link java.lang.Class#getDeclaredMethod(java.lang.String, java.lang.Class[])} (or
   * similar logic for declared methods) and its corresponding {@code MethodInvoker} is cached for subsequent calls with
   * the same class, method name, parameter types, return type, and modifier combination.
   * </p>
   *
   * @param clazz The class in which to find the method. Must not be {@code null}.
   * @param methodName The simple name of the method to find. Must not be blank if {@code parameterTypes},
   *   {@code returnType}, and {@code modifiers} are all unspecified.
   * @param parameterTypes An array of {@link java.lang.Class} objects representing the parameter types of the method.
   *   Can be {@code null} if the method takes no parameters or if parameter types are not part of the search criteria.
   * @param returnType The expected return type of the method. Can be {@code null} if the return type is not part of
   *   the search criteria.
   * @param modifiers The required modifiers for the method, as defined by
   *   {@link java.lang.reflect.Modifier#methodModifiers()}. Use {@code 0} to ignore modifiers.
   * @return A {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method.
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if the method is not found in the specified
   *   class with the given name, parameter types, return type, and modifiers.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null}, {@code methodName} is blank when
   *   name-based lookup is required, or {@code modifiers} contains invalid method modifier bits.
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final String methodName,
    final Class<?>[] parameterTypes,
    final Class<?> returnType,
    final int modifiers
  ) {
    requireNotNull(clazz, () -> "Class type cannot be null");
    final boolean hasName = null != methodName;
    require(hasName || null != parameterTypes || null != returnType || 0 == modifiers,
      () -> "At least one of methodName, parameterTypes, returnType, or modifiers (non-zero) must be specified."
    );
    if (hasName) {
      requireNotBlank(methodName, () -> "Method name cannot be blank.");
    }
    require(0 == (modifiers & ~Modifier.methodModifiers()), () -> "Invalid method modifiers specified.");
    return METHODS.getOrCompute(clazz, methodName, parameterTypes, returnType, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * name, parameter types, and return type.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getMethod(java.lang.Class, java.lang.String, java.lang.Class[], java.lang.Class, int)} with
   * {@code modifiers} as {@code 0}.
   * </p>
   *
   * @param clazz The class in which to find the method. Must not be {@code null}.
   * @param methodName The simple name of the method to find. Must not be blank.
   * @param parameterTypes An array of {@link java.lang.Class} objects representing the parameter types of the method.
   *   Can be {@code null} if the method takes no parameters or if parameter types are not part of the search criteria.
   * @param returnType The expected return type of the method. Can be {@code null} if the return type is not part of
   *   the search criteria.
   * @return A {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method.
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if the method is not found in the specified
   *   class with the given name, parameter types, and return type.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null} or {@code methodName} is blank.
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final String methodName,
    final Class<?>[] parameterTypes,
    final Class<?> returnType
  ) {
    return getMethod(clazz, methodName, parameterTypes, returnType, 0);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * name, parameter types, and modifiers.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getMethod(java.lang.Class, java.lang.String, java.lang.Class[], java.lang.Class, int)} with
   * {@code returnType} as {@code null}.
   * </p>
   *
   * @param clazz The class in which to find the method. Must not be {@code null}.
   * @param methodName The simple name of the method to find. Must not be blank if modifiers are zero.
   * @param parameterTypes An array of {@link java.lang.Class} objects representing the parameter types of the method.
   *   Can be {@code null} if the method takes no parameters or if parameter types are not part of the search criteria.
   * @param modifiers The required modifiers for the method, as defined by
   *   {@link java.lang.reflect.Modifier#methodModifiers()}. Use {@code 0} to ignore modifiers.
   * @return A {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method.
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if the method is not found in the specified
   *   class with the given name, parameter types, and modifiers.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null}, {@code methodName} is blank when
   *   name-based lookup is required, or {@code modifiers} contains invalid method modifier bits.
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final String methodName,
    final Class<?>[] parameterTypes,
    final int modifiers
  ) {
    return getMethod(clazz, methodName, parameterTypes, null, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * name, return type, and modifiers.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getMethod(java.lang.Class, java.lang.String, java.lang.Class[], java.lang.Class, int)} with
   * {@code parameterTypes} as {@code null}.
   * </p>
   *
   * @param clazz The class in which to find the method. Must not be {@code null}.
   * @param methodName The simple name of the method to find. Must not be blank if modifiers are zero.
   * @param returnType The expected return type of the method. Can be {@code null} if the return type is not part of
   *   the search criteria.
   * @param modifiers The required modifiers for the method, as defined by
   *   {@link java.lang.reflect.Modifier#methodModifiers()}. Use {@code 0} to ignore modifiers.
   * @return A {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method.
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if the method is not found in the specified
   *   class with the given name, return type, and modifiers.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null}, {@code methodName} is blank when
   *   name-based lookup is required, or {@code modifiers} contains invalid method modifier bits.
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final String methodName,
    final Class<?> returnType,
    final int modifiers
  ) {
    return getMethod(clazz, methodName, null, returnType, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * parameter types, return type, and modifiers.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getMethod(java.lang.Class, java.lang.String, java.lang.Class[], java.lang.Class, int)} with
   * {@code methodName} as {@code null}.
   * </p>
   *
   * @param clazz The class in which to find the method. Must not be {@code null}.
   * @param parameterTypes An array of {@link java.lang.Class} objects representing the parameter types of the method.
   *   Must not be {@code null} if modifiers are zero.
   * @param returnType The expected return type of the method. Can be {@code null} if the return type is not part of
   *   the search criteria.
   * @param modifiers The required modifiers for the method, as defined by
   *   {@link java.lang.reflect.Modifier#methodModifiers()}. Use {@code 0} to ignore modifiers.
   * @return A {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method.
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if the method is not found in the specified
   *   class with the given parameter types, return type, and modifiers.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null}, {@code parameterTypes} is
   *   {@code null} when parameter-based lookup is required, or {@code modifiers} contains invalid method modifier
   *   bits.
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final Class<?>[] parameterTypes,
    final Class<?> returnType,
    final int modifiers
  ) {
    return getMethod(clazz, null, parameterTypes, returnType, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its name
   * and parameter types.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getMethod(java.lang.Class, java.lang.String, java.lang.Class[], java.lang.Class, int)} with
   * {@code returnType} as {@code null} and {@code modifiers} as {@code 0}.
   * </p>
   *
   * @param clazz The class in which to find the method. Must not be {@code null}.
   * @param methodName The simple name of the method to find. Must not be blank.
   * @param parameterTypes An array of {@link java.lang.Class} objects representing the parameter types of the method.
   *   Can be {@code null} if the method takes no parameters or if parameter types are not part of the search criteria.
   * @return A {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method.
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if the method is not found in the specified
   *   class with the given name and parameter types.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null} or {@code methodName} is blank.
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final String methodName,
    final Class<?>[] parameterTypes
  ) {
    return getMethod(clazz, methodName, parameterTypes, null, 0);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its name
   * and return type.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getMethod(java.lang.Class, java.lang.String, java.lang.Class[], java.lang.Class, int)} with
   * {@code parameterTypes} as {@code null} and {@code modifiers} as {@code 0}.
   * </p>
   *
   * @param clazz The class in which to find the method. Must not be {@code null}.
   * @param methodName The simple name of the method to find. Must not be blank.
   * @param returnType The expected return type of the method. Can be {@code null} if the return type is not part of
   *   the search criteria.
   * @return A {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method.
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if the method is not found in the specified
   *   class with the given name and return type.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null} or {@code methodName} is blank.
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final String methodName,
    final Class<?> returnType
  ) {
    return getMethod(clazz, methodName, null, returnType, 0);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its name
   * and modifiers.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getMethod(java.lang.Class, java.lang.String, java.lang.Class[], java.lang.Class, int)} with
   * {@code parameterTypes} and {@code returnType} as {@code null}.
   * </p>
   *
   * @param clazz The class in which to find the method. Must not be {@code null}.
   * @param methodName The simple name of the method to find. Must not be blank if modifiers are zero.
   * @param modifiers The required modifiers for the method, as defined by
   *   {@link java.lang.reflect.Modifier#methodModifiers()}. Use {@code 0} to ignore modifiers.
   * @return A {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method.
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if the method is not found in the specified
   *   class with the given name and modifiers.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null}, {@code methodName} is blank when
   *   name-based lookup is required, or {@code modifiers} contains invalid method modifier bits.
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final String methodName,
    final int modifiers
  ) {
    return getMethod(clazz, methodName, null, null, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * parameter types and return type.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getMethod(java.lang.Class, java.lang.String, java.lang.Class[], java.lang.Class, int)} with
   * {@code methodName} as {@code null} and {@code modifiers} as {@code 0}.
   * </p>
   *
   * @param clazz The class in which to find the method. Must not be {@code null}.
   * @param parameterTypes An array of {@link java.lang.Class} objects representing the parameter types of the method.
   *   Must not be {@code null}.
   * @param returnType The expected return type of the method. Can be {@code null} if the return type is not part of
   *   the search criteria.
   * @return A {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method.
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if the method is not found in the specified
   *   class with the given parameter types and return type.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null} or {@code parameterTypes} is
   *   {@code null}.
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final Class<?>[] parameterTypes,
    final Class<?> returnType
  ) {
    return getMethod(clazz, null, parameterTypes, returnType, 0);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * parameter types and modifiers.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getMethod(java.lang.Class, java.lang.String, java.lang.Class[], java.lang.Class, int)} with
   * {@code methodName} and {@code returnType} as {@code null}.
   * </p>
   *
   * @param clazz The class in which to find the method. Must not be {@code null}.
   * @param parameterTypes An array of {@link java.lang.Class} objects representing the parameter types of the method.
   *   Must not be {@code null} if modifiers are zero.
   * @param modifiers The required modifiers for the method, as defined by
   *   {@link java.lang.reflect.Modifier#methodModifiers()}. Use {@code 0} to ignore modifiers.
   * @return A {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method.
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if the method is not found in the specified
   *   class with the given parameter types and modifiers.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null}, {@code parameterTypes} is
   *   {@code null} when parameter-based lookup is required, or {@code modifiers} contains invalid method modifier
   *   bits.
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final Class<?>[] parameterTypes,
    final int modifiers
  ) {
    return getMethod(clazz, null, parameterTypes, null, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * return type and modifiers.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getMethod(java.lang.Class, java.lang.String, java.lang.Class[], java.lang.Class, int)} with
   * {@code methodName} and {@code parameterTypes} as {@code null}.
   * </p>
   *
   * @param clazz The class in which to find the method. Must not be {@code null}.
   * @param returnType The expected return type of the method. Must not be {@code null} if modifiers are zero.
   * @param modifiers The required modifiers for the method, as defined by
   *   {@link java.lang.reflect.Modifier#methodModifiers()}. Use {@code 0} to ignore modifiers.
   * @return A {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method.
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if the method is not found in the specified
   *   class with the given return type and modifiers.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null}, {@code returnType} is {@code null}
   *   when return-type-based lookup is required, or {@code modifiers} contains invalid method modifier bits.
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final Class<?> returnType,
    final int modifiers
  ) {
    return getMethod(clazz, null, null, returnType, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * name.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getMethod(java.lang.Class, java.lang.String, java.lang.Class[], java.lang.Class, int)} with
   * {@code parameterTypes} and {@code returnType} as {@code null} and {@code modifiers} as {@code 0}. This will match
   * the first method found with the given name, regardless of its signature.
   * </p>
   *
   * @param clazz The class in which to find the method. Must not be {@code null}.
   * @param methodName The simple name of the method to find. Must not be blank.
   * @return A {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method.
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if the method is not found in the specified
   *   class with the given name.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null} or {@code methodName} is blank.
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final String methodName
  ) {
    return getMethod(clazz, methodName, null, null, 0);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * parameter types.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getMethod(java.lang.Class, java.lang.String, java.lang.Class[], java.lang.Class, int)} with
   * {@code methodName} and {@code returnType} as {@code null} and {@code modifiers} as {@code 0}.
   * </p>
   *
   * @param clazz The class in which to find the method. Must not be {@code null}.
   * @param parameterTypes An array of {@link java.lang.Class} objects representing the parameter types of the method.
   *   Must not be {@code null}.
   * @return A {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method.
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if the method is not found in the specified
   *   class with the given parameter types.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null} or {@code parameterTypes} is
   *   {@code null}.
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final Class<?>[] parameterTypes
  ) {
    return getMethod(clazz, null, parameterTypes, null, 0);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * return type.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getMethod(java.lang.Class, java.lang.String, java.lang.Class[], java.lang.Class, int)} with
   * {@code methodName} and {@code parameterTypes} as {@code null} and {@code modifiers} as {@code 0}.
   * </p>
   *
   * @param clazz The class in which to find the method. Must not be {@code null}.
   * @param returnType The expected return type of the method.
   * @return A {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method.
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if the method is not found in the specified
   *   class with the given return type.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null}.
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final Class<?> returnType
  ) {
    return getMethod(clazz, null, null, returnType, 0);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * modifiers.
   * <p>
   * This is a convenience method equivalent to calling
   * {@link #getMethod(java.lang.Class, java.lang.String, java.lang.Class[], java.lang.Class, int)} with
   * {@code methodName}, {@code parameterTypes}, and {@code returnType} as {@code null}.
   * </p>
   *
   * @param clazz The class in which to find the method. Must not be {@code null}.
   * @param modifiers The required modifiers for the method, as defined by
   *   {@link java.lang.reflect.Modifier#methodModifiers()}. Must not be zero.
   * @return A {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method.
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if the method is not found in the specified
   *   class with the given modifiers.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null}, {@code modifiers} is zero, or
   *   {@code modifiers} contains invalid method modifier bits.
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final int modifiers
  ) {
    return getMethod(clazz, null, null, null, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.ConstructorInitializer} for a constructor in the specified class,
   * matching its parameter types and modifiers.
   * <p>
   * The constructor is looked up using {@link java.lang.Class#getDeclaredConstructor(java.lang.Class[])} (or similar
   * logic for declared constructors) and its corresponding {@code ConstructorInitializer} is cached for subsequent
   * calls with the same class, parameter types, and modifier combination.
   * </p>
   *
   * @param clazz The class for which to find the constructor. Must not be {@code null}.
   * @param parameterTypes An array of {@link java.lang.Class} objects representing the parameter types of the
   *   constructor. Can be {@code null} if the constructor takes no parameters.
   * @param modifiers The required modifiers for the constructor, as defined by
   *   {@link java.lang.reflect.Modifier#constructorModifiers()}. Use {@code 0} to ignore modifiers.
   * @return A {@link me.kvdpxne.modjit.accessor.ConstructorInitializer} for the specified constructor.
   * @throws me.kvdpxne.modjit.exception.ConstructorNotFoundReflectionException if the constructor is not found in the
   *   specified class with the given parameter types and modifiers.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null}, {@code parameterTypes} is
   *   {@code null} when modifiers are zero, or {@code modifiers} contains invalid constructor modifier bits.
   */
  public static ConstructorInitializer getConstructor(
    final Class<?> clazz,
    final Class<?>[] parameterTypes,
    final int modifiers
  ) {
    requireNotNull(clazz, () -> "Class type cannot be null");
    require(null != parameterTypes || 0 != modifiers,
      () -> "Either parameterTypes or a non-zero modifiers value must be specified."
    );
    require(0 == (modifiers & ~Modifier.constructorModifiers()), () -> "Invalid constructor modifiers specified.");
    return CONSTRUCTORS.getOrCompute(clazz, parameterTypes, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.ConstructorInitializer} for a constructor in the specified class,
   * matching its parameter types.
   * <p>
   * This is a convenience method equivalent to calling {@link #getConstructor(java.lang.Class, java.lang.Class[], int)}
   * with {@code modifiers} as {@code 0}.
   * </p>
   *
   * @param clazz The class for which to find the constructor. Must not be {@code null}.
   * @param parameterTypes An array of {@link java.lang.Class} objects representing the parameter types of the
   *   constructor. Can be {@code null} if the constructor takes no parameters.
   * @return A {@link me.kvdpxne.modjit.accessor.ConstructorInitializer} for the specified constructor.
   * @throws me.kvdpxne.modjit.exception.ConstructorNotFoundReflectionException if the constructor is not found in the
   *   specified class with the given parameter types.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null}.
   */
  public static ConstructorInitializer getConstructor(
    final Class<?> clazz,
    final Class<?>[] parameterTypes
  ) {
    return getConstructor(clazz, parameterTypes, 0);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.ConstructorInitializer} for a constructor in the specified class,
   * matching its modifiers.
   * <p>
   * This is a convenience method equivalent to calling {@link #getConstructor(java.lang.Class, java.lang.Class[], int)}
   * with {@code parameterTypes} as {@code null}.
   * </p>
   *
   * @param clazz The class for which to find the constructor. Must not be {@code null}.
   * @param modifiers The required modifiers for the constructor, as defined by
   *   {@link java.lang.reflect.Modifier#constructorModifiers()}. Must not be zero.
   * @return A {@link me.kvdpxne.modjit.accessor.ConstructorInitializer} for the specified constructor.
   * @throws me.kvdpxne.modjit.exception.ConstructorNotFoundReflectionException if the constructor is not found in the
   *   specified class with the given modifiers.
   * @throws java.lang.IllegalArgumentException if {@code clazz} is {@code null}, {@code modifiers} is zero, or
   *   {@code modifiers} contains invalid constructor modifier bits.
   */
  public static ConstructorInitializer getConstructor(
    final Class<?> clazz,
    final int modifiers
  ) {
    return getConstructor(clazz, null, modifiers);
  }

  /**
   * Holder class for the singleton {@link me.kvdpxne.modjit.cache.ClassCache} instance. Uses the
   * Initialization-on-demand holder idiom for thread-safe lazy loading.
   */
  private static final class ClassesCacheHolder {
    private static final ClassCache CACHE = new ClassCache();
  }

  /**
   * Holder class for the singleton {@link me.kvdpxne.modjit.cache.ConstructorCache} instance. Uses the
   * Initialization-on-demand holder idiom for thread-safe lazy loading.
   */
  private static final class ConstructorsCacheHolder {
    private static final ConstructorCache CACHE = new ConstructorCache();
  }

  /**
   * Holder class for the singleton {@link me.kvdpxne.modjit.cache.FieldCache} instance. Uses the
   * Initialization-on-demand holder idiom for thread-safe lazy loading.
   */
  private static final class FieldsCacheHolder {
    private static final FieldCache CACHE = new FieldCache();
  }

  /**
   * Holder class for the singleton {@link me.kvdpxne.modjit.cache.MethodCache} instance. Uses the
   * Initialization-on-demand holder idiom for thread-safe lazy loading.
   */
  private static final class MethodsCacheHolder {
    private static final MethodCache CACHE = new MethodCache();
  }
}
