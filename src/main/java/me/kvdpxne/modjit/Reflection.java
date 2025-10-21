package me.kvdpxne.modjit;

import java.lang.reflect.Modifier;
import me.kvdpxne.modjit.accessor.ConstructorInitializer;
import me.kvdpxne.modjit.accessor.FieldAccessor;
import me.kvdpxne.modjit.accessor.MethodInvoker;
import me.kvdpxne.modjit.fluent.builder.ConstructorBuilder;
import me.kvdpxne.modjit.fluent.builder.FieldBuilder;
import me.kvdpxne.modjit.fluent.builder.MethodBuilder;
import me.kvdpxne.modjit.cache.component.ClassCache;
import me.kvdpxne.modjit.cache.component.ConstructorCache;
import me.kvdpxne.modjit.cache.component.FieldCache;
import me.kvdpxne.modjit.cache.component.MethodCache;
import me.kvdpxne.modjit.util.Validation;

/**
 * Provides a high-level, thread-safe, and cached API for Java Reflection operations.
 * <p>
 * This utility class abstracts the complexities of direct reflection usage by providing a simplified interface for
 * accessing classes, fields, methods, and constructors with built-in caching for improved performance. All reflection
 * operations are performed through internal caches that avoid repeated lookups of reflection objects.
 * </p>
 * <p>
 * Key features include:
 * </p>
 * <ul>
 *   <li>Automatic caching of reflection objects for repeated access</li>
 *   <li>Thread-safe operations through concurrent data structures</li>
 *   <li>Flexible search criteria for fields, methods, and constructors</li>
 *   <li>Automatic accessibility management across Java versions</li>
 *   <li>Comprehensive exception handling with descriptive error messages</li>
 *   <li>Support for class loading with custom class loaders and initialization control</li>
 * </ul>
 * <p>
 * The class uses the Initialization-on-demand holder idiom for thread-safe lazy loading
 * of cache instances and provides extensive method overloading for common usage patterns.
 * All methods are static and the class cannot be instantiated.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @see me.kvdpxne.modjit.accessor.FieldAccessor
 * @see me.kvdpxne.modjit.accessor.MethodInvoker
 * @see me.kvdpxne.modjit.accessor.ConstructorInitializer
 * @see java.lang.reflect.Field
 * @see java.lang.reflect.Method
 * @see java.lang.reflect.Constructor
 * @since 0.1.0
 */
public final class Reflection {

  /**
   * The singleton instance of the class cache for storing and retrieving {@link Class} objects.
   * <p>
   * This cache stores class objects keyed by their fully qualified name, class loader, and initialization flag to
   * prevent redundant class loading operations.
   * </p>
   */
  private static final ClassCache CLASSES = ClassesCacheHolder.CACHE;

  /**
   * The singleton instance of the constructor cache for storing and retrieving
   * {@link me.kvdpxne.modjit.accessor.ConstructorInitializer} objects.
   * <p>
   * This cache stores constructor accessors keyed by class name, parameter types, and modifiers to optimize constructor
   * lookup performance.
   * </p>
   */
  private static final ConstructorCache CONSTRUCTORS = ConstructorsCacheHolder.CACHE;

  /**
   * The singleton instance of the field cache for storing and retrieving
   * {@link me.kvdpxne.modjit.accessor.FieldAccessor} objects.
   * <p>
   * This cache stores field accessors keyed by class name, field name, field type, and modifiers to optimize field
   * access performance.
   * </p>
   */
  private static final FieldCache FIELDS = FieldsCacheHolder.CACHE;

  /**
   * The singleton instance of the method cache for storing and retrieving
   * {@link me.kvdpxne.modjit.accessor.MethodInvoker} objects.
   * <p>
   * This cache stores method invokers keyed by class name, method name, parameter types, return type, and modifiers to
   * optimize method invocation performance.
   * </p>
   */
  private static final MethodCache METHODS = MethodsCacheHolder.CACHE;

  /**
   * Prevents instantiation of this utility class.
   * <p>
   * This constructor throws an {@link AssertionError} if invoked, enforcing the static-only nature of this utility
   * class. All methods are designed to be accessed statically without the need for instance creation.
   * </p>
   *
   * @throws AssertionError always thrown when constructor is invoked
   */
  public Reflection() {
    throw new AssertionError();
  }

  /**
   * Retrieves a {@link Class} object by its fully qualified name with specified class loader and initialization
   * control.
   * <p>
   * The class is loaded using {@link Class#forName(String, boolean, ClassLoader)} and cached for subsequent calls with
   * the same parameters. If the class cannot be found, a
   * {@link me.kvdpxne.modjit.exception.ClassNotFoundReflectionException} is thrown with the underlying
   * {@link ClassNotFoundException} as the cause.
   * </p>
   *
   * @param path the fully qualified name of the class to retrieve; must not be {@code null} or blank (e.g.,
   *   "java.lang.String", "com.example.MyClass")
   * @param classLoader the class loader to use for loading the class; may be {@code null} to use the automatic class
   *   loader resolution strategy (context class loader followed by system class loader)
   * @param initialize whether the class should be initialized during loading; when {@code true}, the class will be
   *   initialized as if by the Java Language Specification; when {@code false}, the class will be loaded but not
   *   initialized
   * @return the {@link Class} object for the specified path, either retrieved from cache or newly loaded and cached;
   *   never {@code null}
   * @throws me.kvdpxne.modjit.exception.ClassNotFoundReflectionException if the class cannot be found by the
   *   specified path using the resolved class loader; the exception includes the original
   *   {@link ClassNotFoundException} as the cause
   * @throws IllegalArgumentException if the {@code path} is blank
   * @throws LinkageError if the linkage fails during class loading
   * @throws ExceptionInInitializerError if class initialization fails when {@code initialize} is {@code true}
   * @throws SecurityException if a security manager is present and access to the class, class loader, or package is
   *   denied
   */
  public static Class<?> getClass(
    final String path,
    final ClassLoader classLoader,
    final boolean initialize
  ) {
    Validation.requireNotBlank(path, () -> "Class path cannot be blank. Provide valid path (e.g. 'entity.Player')");
    return CLASSES.getOrCompute(path, classLoader, initialize);
  }

  /**
   * Retrieves a {@link Class} object by its fully qualified name using the specified class loader with default
   * initialization.
   * <p>
   * This is a convenience method equivalent to calling {@link #getClass(String, ClassLoader, boolean)} with
   * {@code initialize} as {@code true}.
   * </p>
   *
   * @param path the fully qualified name of the class to retrieve; must not be {@code null} or blank
   * @param classLoader the class loader to use for loading the class; may be {@code null} to use automatic class
   *   loader resolution
   * @return the {@link Class} object for the specified path; never {@code null}
   * @throws me.kvdpxne.modjit.exception.ClassNotFoundReflectionException if the class cannot be found by the
   *   specified path
   * @throws IllegalArgumentException if the {@code path} is blank
   * @throws SecurityException if access to the class is denied
   */
  public static Class<?> getClass(
    final String path,
    final ClassLoader classLoader
  ) {
    return Reflection.getClass(path, classLoader, true);
  }

  /**
   * Retrieves a {@link Class} object by its fully qualified name with specified initialization control using the
   * default class loader.
   * <p>
   * This is a convenience method equivalent to calling {@link #getClass(String, ClassLoader, boolean)} with
   * {@code classLoader} as {@code null}.
   * </p>
   *
   * @param path the fully qualified name of the class to retrieve; must not be {@code null} or blank
   * @param initialize whether the class should be initialized during loading
   * @return the {@link Class} object for the specified path; never {@code null}
   * @throws me.kvdpxne.modjit.exception.ClassNotFoundReflectionException if the class cannot be found by the
   *   specified path
   * @throws IllegalArgumentException if the {@code path} is blank
   * @throws SecurityException if access to the class is denied
   */
  public static Class<?> getClass(
    final String path,
    final boolean initialize
  ) {
    return Reflection.getClass(path, null, initialize);
  }

  /**
   * Retrieves a {@link Class} object by its fully qualified name with default class loader and initialization.
   * <p>
   * This is a convenience method equivalent to calling {@link #getClass(String, ClassLoader, boolean)} with both
   * {@code classLoader} and {@code initialize} as {@code null} and {@code true} respectively.
   * </p>
   *
   * @param path the fully qualified name of the class to retrieve; must not be {@code null} or blank
   * @return the {@link Class} object for the specified path; never {@code null}
   * @throws me.kvdpxne.modjit.exception.ClassNotFoundReflectionException if the class cannot be found by the
   *   specified path
   * @throws IllegalArgumentException if the {@code path} is blank
   * @throws SecurityException if access to the class is denied
   */
  public static Class<?> getClass(
    final String path
  ) {
    return Reflection.getClass(path, null, true);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.FieldAccessor} for a field in the specified class with comprehensive
   * search criteria including name, type, and modifiers.
   * <p>
   * This is the primary field lookup method that provides the most flexible and precise search capabilities for
   * locating fields through reflection. The field is looked up using the class's declared fields and cached for
   * subsequent calls with the same criteria combination.
   * </p>
   * <p>
   * The search criteria are combined using logical AND - a field must match all specified non-null criteria to be
   * selected. This allows precise field resolution in complex scenarios where fields may have the same name but
   * different types, or when searching for fields with specific accessibility characteristics.
   * </p>
   * <p>
   * Search criteria behavior:
   * </p>
   * <ul>
   *   <li><strong>Field Name:</strong> When specified, matches fields with the exact name (case-sensitive).
   *       Use {@code null} to match fields regardless of name.</li>
   *   <li><strong>Field Type:</strong> When specified, matches fields with exactly matching type.
   *       Use {@code null} to match fields regardless of type.</li>
   *   <li><strong>Modifiers:</strong> When non-zero, matches fields with exactly matching modifier bits.
   *       Use {@code 0} to match fields regardless of modifiers.</li>
   * </ul>
   * <p>
   * At least one search criterion must be specified (field name, field type, or non-zero modifiers).
   * If multiple fields match the criteria, the first one encountered during reflection is returned.
   * </p>
   * <p>
   * <strong>Performance Note:</strong> This method uses cached lookup results for optimal performance
   * when the same field is accessed repeatedly. The cache key includes all specified criteria, so
   * different combinations of criteria will result in separate cache entries.
   * </p>
   * <p>
   * <strong>Accessibility Note:</strong> The returned {@link me.kvdpxne.modjit.accessor.FieldAccessor}
   * automatically handles accessibility concerns, including making private fields accessible and
   * restoring their original accessibility state after operations.
   * </p>
   * <p>
   * <strong>Example Usage:</strong>
   * </p>
   * <pre>{@code
   * // Find a field by name only
   * FieldAccessor field = Reflection.getField(
   *     MyClass.class,
   *     "privateField",
   *     null,
   *     0
   * );
   *
   * // Find a static final field by type and modifiers
   * FieldAccessor constantField = Reflection.getField(
   *     MyClass.class,
   *     null,
   *     String.class,
   *     Modifier.STATIC | Modifier.FINAL
   * );
   *
   * // Find a field with exact name and type
   * FieldAccessor exactField = Reflection.getField(
   *     MyClass.class,
   *     "userCount",
   *     int.class,
   *     0
   * );
   * }</pre>
   *
   * @param clazz the class in which to find the field; must not be {@code null}
   * @param fieldName the simple name of the field to find; may be {@code null} if field type or modifiers are
   *   specified; must not be blank if all other criteria are unspecified
   * @param fieldType the expected type of the field; may be {@code null} if field name or modifiers are specified
   * @param modifiers the required modifiers for the field, as defined by
   *   {@link java.lang.reflect.Modifier#fieldModifiers()}; use {@code 0} to ignore modifiers; multiple modifiers can be
   *   combined using bitwise OR (e.g., {@code Modifier.PRIVATE | Modifier.STATIC})
   * @return a {@link me.kvdpxne.modjit.accessor.FieldAccessor} for the specified field; never {@code null}
   * @throws me.kvdpxne.modjit.exception.FieldNotFoundReflectionException if no field in the specified class matches
   *   all the provided non-null criteria (name, type, and modifiers)
   * @throws IllegalArgumentException if any of the following conditions occur:
   *   <ul>
   *     <li>{@code clazz} is {@code null}</li>
   *     <li>all criteria are unspecified ({@code fieldName}, {@code fieldType}, and {@code modifiers}
   *         are all unspecified)</li>
   *     <li>{@code fieldName} is blank when name-based lookup is required</li>
   *     <li>{@code modifiers} contains bits that are not valid field modifiers</li>
   *   </ul>
   * @throws SecurityException if access to the class's declared fields is denied by the security manager
   * @throws NullPointerException if {@code clazz} is {@code null}
   * @see java.lang.reflect.Field
   * @see java.lang.reflect.Modifier#fieldModifiers()
   * @see me.kvdpxne.modjit.accessor.FieldAccessor
   * @see me.kvdpxne.modjit.exception.FieldNotFoundReflectionException
   * @see #getField(Class, String)
   * @see #getField(Class, Class)
   * @see #getField(Class, int)
   */
  public static FieldAccessor getField(
    final Class<?> clazz,
    final String fieldName,
    final Class<?> fieldType,
    final int modifiers
  ) {
    Validation.requireNotNull(clazz, () -> "Class type cannot be null");
    final boolean hasName = null != fieldName;
    Validation.require(hasName || null != fieldType || 0 == modifiers,
      () -> "At least one of fieldName, fieldType, or modifiers (non-zero) must be specified."
    );
    if (hasName) {
      Validation.requireNotBlank(fieldName, () -> "Field name cannot be blank. Provide valid name (e.g. 'playerConnection')");
    }
    Validation.require(0 == (modifiers & ~Modifier.fieldModifiers()), () -> "Invalid field modifiers specified.");
    return FIELDS.getOrCompute(clazz, fieldName, fieldType, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.FieldAccessor} for a field in the specified class, matching its name
   * and type.
   * <p>
   * This is a convenience method equivalent to calling {@link #getField(Class, String, Class, int)} with
   * {@code modifiers} as {@code 0}.
   * </p>
   *
   * @param clazz the class in which to find the field; must not be {@code null}
   * @param fieldName the simple name of the field to find; must not be blank
   * @param fieldType the expected type of the field; may be {@code null} if the type is not part of the search
   *   criteria
   * @return a {@link me.kvdpxne.modjit.accessor.FieldAccessor} for the specified field; never {@code null}
   * @throws me.kvdpxne.modjit.exception.FieldNotFoundReflectionException if no field matches the specified name and
   *   type in the class
   * @throws IllegalArgumentException if {@code clazz} is {@code null} or {@code fieldName} is blank
   * @throws SecurityException if access to the class's declared fields is denied
   */
  public static FieldAccessor getField(
    final Class<?> clazz,
    final String fieldName,
    final Class<?> fieldType
  ) {
    return Reflection.getField(clazz, fieldName, fieldType, 0);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.FieldAccessor} for a field in the specified class, matching its name
   * and modifiers.
   * <p>
   * This is a convenience method equivalent to calling {@link #getField(Class, String, Class, int)} with
   * {@code fieldType} as {@code null}.
   * </p>
   *
   * @param clazz the class in which to find the field; must not be {@code null}
   * @param fieldName the simple name of the field to find; must not be blank if modifiers are zero
   * @param modifiers the required modifiers for the field, as defined by
   *   {@link java.lang.reflect.Modifier#fieldModifiers()}; use {@code 0} to ignore modifiers
   * @return a {@link me.kvdpxne.modjit.accessor.FieldAccessor} for the specified field; never {@code null}
   * @throws me.kvdpxne.modjit.exception.FieldNotFoundReflectionException if no field matches the specified name and
   *   modifiers in the class
   * @throws IllegalArgumentException if {@code clazz} is {@code null}, {@code fieldName} is blank when name-based
   *   lookup is required, or {@code modifiers} contains invalid field modifier bits
   * @throws SecurityException if access to the class's declared fields is denied
   */
  public static FieldAccessor getField(
    final Class<?> clazz,
    final String fieldName,
    final int modifiers
  ) {
    return Reflection.getField(clazz, fieldName, null, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.FieldAccessor} for a field in the specified class, matching its type
   * and modifiers.
   * <p>
   * This is a convenience method equivalent to calling {@link #getField(Class, String, Class, int)} with
   * {@code fieldName} as {@code null}.
   * </p>
   *
   * @param clazz the class in which to find the field; must not be {@code null}
   * @param fieldType the expected type of the field; must not be {@code null} if modifiers are zero
   * @param modifiers the required modifiers for the field, as defined by
   *   {@link java.lang.reflect.Modifier#fieldModifiers()}; use {@code 0} to ignore modifiers
   * @return a {@link me.kvdpxne.modjit.accessor.FieldAccessor} for the specified field; never {@code null}
   * @throws me.kvdpxne.modjit.exception.FieldNotFoundReflectionException if no field matches the specified type and
   *   modifiers in the class
   * @throws IllegalArgumentException if {@code clazz} is {@code null}, {@code fieldType} is {@code null} when
   *   type-based lookup is required, or {@code modifiers} contains invalid field modifier bits
   * @throws SecurityException if access to the class's declared fields is denied
   */
  public static FieldAccessor getField(
    final Class<?> clazz,
    final Class<?> fieldType,
    final int modifiers
  ) {
    return Reflection.getField(clazz, null, fieldType, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.FieldAccessor} for a field in the specified class, matching its
   * name.
   * <p>
   * This is a convenience method equivalent to calling {@link #getField(Class, String, Class, int)} with
   * {@code fieldType} as {@code null} and {@code modifiers} as {@code 0}. This will match the first field found with
   * the given name, regardless of its type or modifiers.
   * </p>
   *
   * @param clazz the class in which to find the field; must not be {@code null}
   * @param fieldName the simple name of the field to find; must not be blank
   * @return a {@link me.kvdpxne.modjit.accessor.FieldAccessor} for the specified field; never {@code null}
   * @throws me.kvdpxne.modjit.exception.FieldNotFoundReflectionException if no field with the specified name is found
   *   in the class
   * @throws IllegalArgumentException if {@code clazz} is {@code null} or {@code fieldName} is blank
   * @throws SecurityException if access to the class's declared fields is denied
   */
  public static FieldAccessor getField(
    final Class<?> clazz,
    final String fieldName
  ) {
    return Reflection.getField(clazz, fieldName, null, 0);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.FieldAccessor} for a field in the specified class, matching its
   * type.
   * <p>
   * This is a convenience method equivalent to calling {@link #getField(Class, String, Class, int)} with
   * {@code fieldName} as {@code null} and {@code modifiers} as {@code 0}. This will match the first field found with
   * the given type, regardless of its name or modifiers.
   * </p>
   *
   * @param clazz the class in which to find the field; must not be {@code null}
   * @param fieldType the expected type of the field; must not be {@code null}
   * @return a {@link me.kvdpxne.modjit.accessor.FieldAccessor} for the specified field; never {@code null}
   * @throws me.kvdpxne.modjit.exception.FieldNotFoundReflectionException if no field with the specified type is found
   *   in the class
   * @throws IllegalArgumentException if {@code clazz} is {@code null} or {@code fieldType} is {@code null}
   * @throws SecurityException if access to the class's declared fields is denied
   */
  public static FieldAccessor getField(
    final Class<?> clazz,
    final Class<?> fieldType
  ) {
    return Reflection.getField(clazz, null, fieldType, 0);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.FieldAccessor} for a field in the specified class, matching its
   * modifiers.
   * <p>
   * This is a convenience method equivalent to calling {@link #getField(Class, String, Class, int)} with
   * {@code fieldName} and {@code fieldType} as {@code null}. This will match the first field found with the given
   * modifiers, regardless of its name or type.
   * </p>
   *
   * @param clazz the class in which to find the field; must not be {@code null}
   * @param modifiers the required modifiers for the field, as defined by
   *   {@link java.lang.reflect.Modifier#fieldModifiers()}; must not be zero
   * @return a {@link me.kvdpxne.modjit.accessor.FieldAccessor} for the specified field; never {@code null}
   * @throws me.kvdpxne.modjit.exception.FieldNotFoundReflectionException if no field with the specified modifiers is
   *   found in the class
   * @throws IllegalArgumentException if {@code clazz} is {@code null}, {@code modifiers} is zero, or
   *   {@code modifiers} contains invalid field modifier bits
   * @throws SecurityException if access to the class's declared fields is denied
   */
  public static FieldAccessor getField(
    final Class<?> clazz,
    final int modifiers
  ) {
    return Reflection.getField(clazz, null, null, modifiers);
  }

  public static FieldBuilder newFieldFinder() {
    return new FieldBuilder();
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * name, parameter types, return type, and modifiers.
   * <p>
   * This is the primary method lookup method that provides the most comprehensive search criteria for locating methods
   * through reflection. The method is looked up using the class's declared methods and cached for subsequent calls with
   * the same criteria combination.
   * </p>
   * <p>
   * The search criteria are combined using logical AND - a method must match all specified non-null criteria to be
   * selected. This allows precise method resolution in complex scenarios with method overloading, where multiple
   * methods may share the same name but differ in parameters, return type, or modifiers.
   * </p>
   * <p>
   * Search criteria behavior:
   * </p>
   * <ul>
   *   <li><strong>Method Name:</strong> When specified, matches methods with the exact name (case-sensitive).
   *       Use {@code null} to match methods regardless of name.</li>
   *   <li><strong>Parameter Types:</strong> When specified, matches methods with exactly matching
   *       parameter types in the same order. Use {@code null} to match methods regardless of parameters.</li>
   *   <li><strong>Return Type:</strong> When specified, matches methods with exactly matching return type.
   *       Use {@code null} to match methods regardless of return type.</li>
   *   <li><strong>Modifiers:</strong> When non-zero, matches methods with exactly matching modifier bits.
   *       Use {@code 0} to match methods regardless of modifiers.</li>
   * </ul>
   * <p>
   * At least one search criterion must be specified (method name, parameter types, return type, or
   * non-zero modifiers). If multiple methods match the criteria, the first one encountered during
   * reflection is returned.
   * </p>
   * <p>
   * <strong>Performance Note:</strong> This method uses cached lookup results for optimal performance
   * when the same method is accessed repeatedly. The cache key includes all specified criteria, so
   * different combinations of criteria will result in separate cache entries.
   * </p>
   * <p>
   * <strong>Example Usage:</strong>
   * </p>
   * <pre>{@code
   * // Find a method by name and parameters
   * MethodInvoker method = Reflection.getMethod(
   *     MyClass.class,
   *     "calculateTotal",
   *     new Class<?>[]{int.class, double.class},
   *     double.class,
   *     0
   * );
   *
   * // Find a static method by name and return type
   * MethodInvoker staticMethod = Reflection.getMethod(
   *     MyClass.class,
   *     "getInstance",
   *     null,
   *     MyClass.class,
   *     Modifier.STATIC
   * );
   * }</pre>
   *
   * @param clazz the class in which to find the method; must not be {@code null}
   * @param methodName the simple name of the method to find; may be {@code null} if parameter types, return type, or
   *   modifiers are specified; must not be blank if all other criteria are unspecified
   * @param parameterTypes an array of {@link Class} objects representing the parameter types of the method; may be
   *   {@code null} if the method takes no parameters or if parameter types are not part of the search criteria; empty
   *   array matches no-argument methods
   * @param returnType the expected return type of the method; may be {@code null} if the return type is not part of
   *   the search criteria
   * @param modifiers the required modifiers for the method, as defined by
   *   {@link java.lang.reflect.Modifier#methodModifiers()}; use {@code 0} to ignore modifiers; multiple modifiers can
   *   be combined using bitwise OR (e.g., {@code Modifier.PUBLIC | Modifier.STATIC})
   * @return a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method; never {@code null}
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if no method in the specified class matches
   *   all the provided non-null criteria (name, parameter types, return type, and modifiers)
   * @throws IllegalArgumentException if any of the following conditions occur:
   *   <ul>
   *     <li>{@code clazz} is {@code null}</li>
   *     <li>all criteria are unspecified ({@code methodName}, {@code parameterTypes}, {@code returnType},
   *         and {@code modifiers} are all unspecified)</li>
   *     <li>{@code methodName} is blank when name-based lookup is required</li>
   *     <li>{@code modifiers} contains bits that are not valid method modifiers</li>
   *   </ul>
   * @throws SecurityException if access to the class's declared methods is denied by the security manager
   * @throws NullPointerException if {@code clazz} is {@code null}
   * @see java.lang.reflect.Method
   * @see java.lang.reflect.Modifier
   * @see me.kvdpxne.modjit.accessor.MethodInvoker
   * @see me.kvdpxne.modjit.exception.MethodNotFoundReflectionException
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final String methodName,
    final Class<?>[] parameterTypes,
    final Class<?> returnType,
    final int modifiers
  ) {
    Validation.requireNotNull(clazz, () -> "Class type cannot be null");
    final boolean hasName = null != methodName;
    Validation.require(hasName || null != parameterTypes || null != returnType || 0 == modifiers,
      () -> "At least one of methodName, parameterTypes, returnType, or modifiers (non-zero) must be specified."
    );
    if (hasName) {
      Validation.requireNotBlank(methodName, () -> "Method name cannot be blank.");
    }
    Validation.require(0 == (modifiers & ~Modifier.methodModifiers()), () -> "Invalid method modifiers specified.");
    return METHODS.getOrCompute(clazz, methodName, parameterTypes, returnType, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * name, parameter types, and return type.
   * <p>
   * This is a convenience method equivalent to calling {@link #getMethod(Class, String, Class[], Class, int)} with
   * {@code modifiers} as {@code 0}.
   * </p>
   *
   * @param clazz the class in which to find the method; must not be {@code null}
   * @param methodName the simple name of the method to find; must not be blank
   * @param parameterTypes an array of {@link Class} objects representing the parameter types of the method; may be
   *   {@code null} if the method takes no parameters or if parameter types are not part of the search criteria
   * @param returnType the expected return type of the method; may be {@code null} if the return type is not part of
   *   the search criteria
   * @return a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method; never {@code null}
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if no method matches the specified name,
   *   parameter types, and return type in the class
   * @throws IllegalArgumentException if {@code clazz} is {@code null} or {@code methodName} is blank
   * @throws SecurityException if access to the class's declared methods is denied
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final String methodName,
    final Class<?>[] parameterTypes,
    final Class<?> returnType
  ) {
    return Reflection.getMethod(clazz, methodName, parameterTypes, returnType, 0);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * name, parameter types, and modifiers.
   * <p>
   * This is a convenience method equivalent to calling {@link #getMethod(Class, String, Class[], Class, int)} with
   * {@code returnType} as {@code null}.
   * </p>
   *
   * @param clazz the class in which to find the method; must not be {@code null}
   * @param methodName the simple name of the method to find; must not be blank if modifiers are zero
   * @param parameterTypes an array of {@link Class} objects representing the parameter types of the method; may be
   *   {@code null} if the method takes no parameters or if parameter types are not part of the search criteria
   * @param modifiers the required modifiers for the method, as defined by
   *   {@link java.lang.reflect.Modifier#methodModifiers()}; use {@code 0} to ignore modifiers
   * @return a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method; never {@code null}
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if no method matches the specified name,
   *   parameter types, and modifiers in the class
   * @throws IllegalArgumentException if {@code clazz} is {@code null}, {@code methodName} is blank when name-based
   *   lookup is required, or {@code modifiers} contains invalid method modifier bits
   * @throws SecurityException if access to the class's declared methods is denied
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final String methodName,
    final Class<?>[] parameterTypes,
    final int modifiers
  ) {
    return Reflection.getMethod(clazz, methodName, parameterTypes, null, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * name, return type, and modifiers.
   * <p>
   * This is a convenience method equivalent to calling {@link #getMethod(Class, String, Class[], Class, int)} with
   * {@code parameterTypes} as {@code null}.
   * </p>
   *
   * @param clazz the class in which to find the method; must not be {@code null}
   * @param methodName the simple name of the method to find; must not be blank if modifiers are zero
   * @param returnType the expected return type of the method; may be {@code null} if the return type is not part of
   *   the search criteria
   * @param modifiers the required modifiers for the method, as defined by
   *   {@link java.lang.reflect.Modifier#methodModifiers()}; use {@code 0} to ignore modifiers
   * @return a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method; never {@code null}
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if no method matches the specified name,
   *   return type, and modifiers in the class
   * @throws IllegalArgumentException if {@code clazz} is {@code null}, {@code methodName} is blank when name-based
   *   lookup is required, or {@code modifiers} contains invalid method modifier bits
   * @throws SecurityException if access to the class's declared methods is denied
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final String methodName,
    final Class<?> returnType,
    final int modifiers
  ) {
    return Reflection.getMethod(clazz, methodName, null, returnType, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * parameter types, return type, and modifiers.
   * <p>
   * This is a convenience method equivalent to calling {@link #getMethod(Class, String, Class[], Class, int)} with
   * {@code methodName} as {@code null}.
   * </p>
   *
   * @param clazz the class in which to find the method; must not be {@code null}
   * @param parameterTypes an array of {@link Class} objects representing the parameter types of the method; must not
   *   be {@code null} if modifiers are zero
   * @param returnType the expected return type of the method; may be {@code null} if the return type is not part of
   *   the search criteria
   * @param modifiers the required modifiers for the method, as defined by
   *   {@link java.lang.reflect.Modifier#methodModifiers()}; use {@code 0} to ignore modifiers
   * @return a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method; never {@code null}
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if no method matches the specified
   *   parameter types, return type, and modifiers in the class
   * @throws IllegalArgumentException if {@code clazz} is {@code null}, {@code parameterTypes} is {@code null} when
   *   parameter-based lookup is required, or {@code modifiers} contains invalid method modifier bits
   * @throws SecurityException if access to the class's declared methods is denied
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final Class<?>[] parameterTypes,
    final Class<?> returnType,
    final int modifiers
  ) {
    return Reflection.getMethod(clazz, null, parameterTypes, returnType, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its name
   * and parameter types.
   * <p>
   * This is a convenience method equivalent to calling {@link #getMethod(Class, String, Class[], Class, int)} with
   * {@code returnType} as {@code null} and {@code modifiers} as {@code 0}.
   * </p>
   *
   * @param clazz the class in which to find the method; must not be {@code null}
   * @param methodName the simple name of the method to find; must not be blank
   * @param parameterTypes an array of {@link Class} objects representing the parameter types of the method; may be
   *   {@code null} if the method takes no parameters or if parameter types are not part of the search criteria
   * @return a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method; never {@code null}
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if no method matches the specified name and
   *   parameter types in the class
   * @throws IllegalArgumentException if {@code clazz} is {@code null} or {@code methodName} is blank
   * @throws SecurityException if access to the class's declared methods is denied
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final String methodName,
    final Class<?>[] parameterTypes
  ) {
    return Reflection.getMethod(clazz, methodName, parameterTypes, null, 0);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its name
   * and return type.
   * <p>
   * This is a convenience method equivalent to calling {@link #getMethod(Class, String, Class[], Class, int)} with
   * {@code parameterTypes} as {@code null} and {@code modifiers} as {@code 0}.
   * </p>
   *
   * @param clazz the class in which to find the method; must not be {@code null}
   * @param methodName the simple name of the method to find; must not be blank
   * @param returnType the expected return type of the method; may be {@code null} if the return type is not part of
   *   the search criteria
   * @return a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method; never {@code null}
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if no method matches the specified name and
   *   return type in the class
   * @throws IllegalArgumentException if {@code clazz} is {@code null} or {@code methodName} is blank
   * @throws SecurityException if access to the class's declared methods is denied
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final String methodName,
    final Class<?> returnType
  ) {
    return Reflection.getMethod(clazz, methodName, null, returnType, 0);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its name
   * and modifiers.
   * <p>
   * This is a convenience method equivalent to calling {@link #getMethod(Class, String, Class[], Class, int)} with
   * {@code parameterTypes} and {@code returnType} as {@code null}.
   * </p>
   *
   * @param clazz the class in which to find the method; must not be {@code null}
   * @param methodName the simple name of the method to find; must not be blank if modifiers are zero
   * @param modifiers the required modifiers for the method, as defined by
   *   {@link java.lang.reflect.Modifier#methodModifiers()}; use {@code 0} to ignore modifiers
   * @return a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method; never {@code null}
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if no method matches the specified name and
   *   modifiers in the class
   * @throws IllegalArgumentException if {@code clazz} is {@code null}, {@code methodName} is blank when name-based
   *   lookup is required, or {@code modifiers} contains invalid method modifier bits
   * @throws SecurityException if access to the class's declared methods is denied
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final String methodName,
    final int modifiers
  ) {
    return Reflection.getMethod(clazz, methodName, null, null, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * parameter types and return type.
   * <p>
   * This is a convenience method equivalent to calling {@link #getMethod(Class, String, Class[], Class, int)} with
   * {@code methodName} as {@code null} and {@code modifiers} as {@code 0}.
   * </p>
   *
   * @param clazz the class in which to find the method; must not be {@code null}
   * @param parameterTypes an array of {@link Class} objects representing the parameter types of the method; must not
   *   be {@code null}
   * @param returnType the expected return type of the method; may be {@code null} if the return type is not part of
   *   the search criteria
   * @return a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method; never {@code null}
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if no method matches the specified
   *   parameter types and return type in the class
   * @throws IllegalArgumentException if {@code clazz} is {@code null} or {@code parameterTypes} is {@code null}
   * @throws SecurityException if access to the class's declared methods is denied
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final Class<?>[] parameterTypes,
    final Class<?> returnType
  ) {
    return Reflection.getMethod(clazz, null, parameterTypes, returnType, 0);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * parameter types and modifiers.
   * <p>
   * This is a convenience method equivalent to calling {@link #getMethod(Class, String, Class[], Class, int)} with
   * {@code methodName} and {@code returnType} as {@code null}.
   * </p>
   *
   * @param clazz the class in which to find the method; must not be {@code null}
   * @param parameterTypes an array of {@link Class} objects representing the parameter types of the method; must not
   *   be {@code null} if modifiers are zero
   * @param modifiers the required modifiers for the method, as defined by
   *   {@link java.lang.reflect.Modifier#methodModifiers()}; use {@code 0} to ignore modifiers
   * @return a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method; never {@code null}
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if no method matches the specified
   *   parameter types and modifiers in the class
   * @throws IllegalArgumentException if {@code clazz} is {@code null}, {@code parameterTypes} is {@code null} when
   *   parameter-based lookup is required, or {@code modifiers} contains invalid method modifier bits
   * @throws SecurityException if access to the class's declared methods is denied
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final Class<?>[] parameterTypes,
    final int modifiers
  ) {
    return Reflection.getMethod(clazz, null, parameterTypes, null, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * return type and modifiers.
   * <p>
   * This is a convenience method equivalent to calling {@link #getMethod(Class, String, Class[], Class, int)} with
   * {@code methodName} and {@code parameterTypes} as {@code null}.
   * </p>
   *
   * @param clazz the class in which to find the method; must not be {@code null}
   * @param returnType the expected return type of the method; must not be {@code null} if modifiers are zero
   * @param modifiers the required modifiers for the method, as defined by
   *   {@link java.lang.reflect.Modifier#methodModifiers()}; use {@code 0} to ignore modifiers
   * @return a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method; never {@code null}
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if no method matches the specified return
   *   type and modifiers in the class
   * @throws IllegalArgumentException if {@code clazz} is {@code null}, {@code returnType} is {@code null} when
   *   return-type-based lookup is required, or {@code modifiers} contains invalid method modifier bits
   * @throws SecurityException if access to the class's declared methods is denied
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final Class<?> returnType,
    final int modifiers
  ) {
    return Reflection.getMethod(clazz, null, null, returnType, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * name.
   * <p>
   * This is a convenience method equivalent to calling {@link #getMethod(Class, String, Class[], Class, int)} with
   * {@code parameterTypes} and {@code returnType} as {@code null} and {@code modifiers} as {@code 0}. This will match
   * the first method found with the given name, regardless of its signature, return type, or modifiers.
   * </p>
   * <p>
   * Use this method when you know the method name but not its exact signature, or when the method is not overloaded. If
   * multiple methods with the same name exist, the first one encountered during reflection will be returned.
   * </p>
   *
   * @param clazz the class in which to find the method; must not be {@code null}
   * @param methodName the simple name of the method to find; must not be blank
   * @return a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method; never {@code null}
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if no method with the specified name is
   *   found in the class
   * @throws IllegalArgumentException if {@code clazz} is {@code null} or {@code methodName} is blank
   * @throws SecurityException if access to the class's declared methods is denied
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final String methodName
  ) {
    return Reflection.getMethod(clazz, methodName, null, null, 0);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * parameter types.
   * <p>
   * This is a convenience method equivalent to calling {@link #getMethod(Class, String, Class[], Class, int)} with
   * {@code methodName} and {@code returnType} as {@code null} and {@code modifiers} as {@code 0}.
   * </p>
   *
   * @param clazz the class in which to find the method; must not be {@code null}
   * @param parameterTypes an array of {@link Class} objects representing the parameter types of the method; must not
   *   be {@code null}
   * @return a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method; never {@code null}
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if no method with the specified parameter
   *   types is found in the class
   * @throws IllegalArgumentException if {@code clazz} is {@code null} or {@code parameterTypes} is {@code null}
   * @throws SecurityException if access to the class's declared methods is denied
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final Class<?>[] parameterTypes
  ) {
    return Reflection.getMethod(clazz, null, parameterTypes, null, 0);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * return type.
   * <p>
   * This is a convenience method equivalent to calling {@link #getMethod(Class, String, Class[], Class, int)} with
   * {@code methodName} and {@code parameterTypes} as {@code null} and {@code modifiers} as {@code 0}.
   * </p>
   *
   * @param clazz the class in which to find the method; must not be {@code null}
   * @param returnType the expected return type of the method; must not be {@code null}
   * @return a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method; never {@code null}
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if no method with the specified return type
   *   is found in the class
   * @throws IllegalArgumentException if {@code clazz} is {@code null} or {@code returnType} is {@code null}
   * @throws SecurityException if access to the class's declared methods is denied
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final Class<?> returnType
  ) {
    return Reflection.getMethod(clazz, null, null, returnType, 0);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for a method in the specified class, matching its
   * modifiers.
   * <p>
   * This is a convenience method equivalent to calling {@link #getMethod(Class, String, Class[], Class, int)} with
   * {@code methodName}, {@code parameterTypes}, and {@code returnType} as {@code null}.
   * </p>
   *
   * @param clazz the class in which to find the method; must not be {@code null}
   * @param modifiers the required modifiers for the method, as defined by
   *   {@link java.lang.reflect.Modifier#methodModifiers()}; must not be zero
   * @return a {@link me.kvdpxne.modjit.accessor.MethodInvoker} for the specified method; never {@code null}
   * @throws me.kvdpxne.modjit.exception.MethodNotFoundReflectionException if no method with the specified modifiers
   *   is found in the class
   * @throws IllegalArgumentException if {@code clazz} is {@code null}, {@code modifiers} is zero, or
   *   {@code modifiers} contains invalid method modifier bits
   * @throws SecurityException if access to the class's declared methods is denied
   */
  public static MethodInvoker getMethod(
    final Class<?> clazz,
    final int modifiers
  ) {
    return Reflection.getMethod(clazz, null, null, null, modifiers);
  }

  public static MethodBuilder newMethodFinder() {
    return new MethodBuilder();
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.ConstructorInitializer} for a constructor in the specified class,
   * matching its parameter types and modifiers.
   * <p>
   * This is the primary constructor lookup method that provides comprehensive search capabilities for locating
   * constructors through reflection. The constructor is looked up using the class's declared constructors and cached
   * for subsequent calls with the same criteria combination.
   * </p>
   * <p>
   * The search criteria are combined using logical AND - a constructor must match all specified non-null criteria to be
   * selected. This allows precise constructor resolution in scenarios where multiple constructors exist with different
   * parameter signatures or accessibility levels.
   * </p>
   * <p>
   * Search criteria behavior:
   * </p>
   * <ul>
   *   <li><strong>Parameter Types:</strong> When specified, matches constructors with exactly
   *       matching parameter types in the same order. Use {@code null} to match constructors
   *       regardless of parameters (including no-argument constructors).</li>
   *   <li><strong>Modifiers:</strong> When non-zero, matches constructors with exactly matching
   *       modifier bits. Use {@code 0} to match constructors regardless of modifiers.</li>
   * </ul>
   * <p>
   * <strong>Performance Optimization:</strong> For classes with only one constructor and no
   * specific parameter types or modifiers specified, this method automatically selects the
   * single constructor without iterating through the constructor array, providing optimal
   * performance for common cases.
   * </p>
   * <p>
   * <strong>Accessibility Note:</strong> The returned {@link me.kvdpxne.modjit.accessor.ConstructorInitializer}
   * automatically handles accessibility concerns, including making private constructors accessible
   * and restoring their original accessibility state after object instantiation.
   * </p>
   * <p>
   * <strong>Example Usage:</strong>
   * </p>
   * <pre>{@code
   * // Find a no-argument constructor
   * ConstructorInitializer defaultConstructor = Reflection.getConstructor(
   *     MyClass.class,
   *     null,
   *     0
   * );
   *
   * // Find a public constructor with specific parameters
   * ConstructorInitializer publicConstructor = Reflection.getConstructor(
   *     MyClass.class,
   *     new Class<?>[]{String.class, int.class},
   *     Modifier.PUBLIC
   * );
   *
   * // Find a private constructor (for singleton patterns)
   * ConstructorInitializer privateConstructor = Reflection.getConstructor(
   *     MyClass.class,
   *     null,
   *     Modifier.PRIVATE
   * );
   *
   * // Find any constructor with specific parameters, regardless of accessibility
   * ConstructorInitializer anyConstructor = Reflection.getConstructor(
   *     MyClass.class,
   *     new Class<?>[]{String.class},
   *     0
   * );
   * }</pre>
   * <p>
   * <strong>Constructor Modifier Notes:</strong>
   * </p>
   * <ul>
   *   <li>Constructors cannot be declared {@code static}, {@code final}, {@code abstract},
   *       or {@code synchronized}</li>
   *   <li>Valid constructor modifiers are {@code public}, {@code protected}, {@code private},
   *       and package-private (no modifier)</li>
   *   <li>The {@link java.lang.reflect.Modifier#constructorModifiers()} method defines the
   *       valid modifier bits for constructors</li>
   * </ul>
   *
   * @param clazz the class for which to find the constructor; must not be {@code null}
   * @param parameterTypes an array of {@link Class} objects representing the parameter types of the constructor; may
   *   be {@code null} if the constructor takes no parameters or if parameter types are not part of the search criteria;
   *   empty array explicitly matches no-argument constructors
   * @param modifiers the required modifiers for the constructor, as defined by
   *   {@link java.lang.reflect.Modifier#constructorModifiers()}; use {@code 0} to ignore modifiers; multiple modifiers
   *   can be combined using bitwise OR, though typically constructors have only one access modifier
   * @return a {@link me.kvdpxne.modjit.accessor.ConstructorInitializer} for the specified constructor; never never
   *   {@code null}
   * @throws me.kvdpxne.modjit.exception.ConstructorNotFoundReflectionException if no constructor in the specified
   *   class matches the provided parameter types (if specified) and modifiers (if non-zero); the exception includes the
   *   class name and parameter types in its message for debugging purposes
   * @throws IllegalArgumentException if any of the following conditions occur:
   *   <ul>
   *     <li>{@code clazz} is {@code null}</li>
   *     <li>{@code modifiers} contains bits that are not valid constructor modifiers</li>
   *   </ul>
   * @throws SecurityException if access to the class's declared constructors is denied by the security manager
   * @throws NullPointerException if {@code clazz} is {@code null}
   * @see java.lang.reflect.Constructor
   * @see java.lang.reflect.Modifier#constructorModifiers()
   * @see me.kvdpxne.modjit.accessor.ConstructorInitializer
   * @see me.kvdpxne.modjit.exception.ConstructorNotFoundReflectionException
   * @see #getConstructor(Class)
   * @see #getConstructor(Class, Class[])
   * @see #getConstructor(Class, int)
   */
  public static ConstructorInitializer getConstructor(
    final Class<?> clazz,
    final Class<?>[] parameterTypes,
    final int modifiers
  ) {
    Validation.requireNotNull(clazz, () -> "Class type cannot be null");
    Validation.require(0 == (modifiers & ~Modifier.constructorModifiers()), () -> "Invalid constructor modifiers specified.");
    return CONSTRUCTORS.getOrCompute(clazz, parameterTypes, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.ConstructorInitializer} for a constructor in the specified class,
   * matching its parameter types.
   * <p>
   * This is a convenience method equivalent to calling {@link #getConstructor(Class, Class[], int)} with
   * {@code modifiers} as {@code 0}.
   * </p>
   * <p>
   * This method is typically used when you know the exact parameter types of the constructor but don't need to specify
   * particular modifiers. It will match constructors regardless of their accessibility modifiers (public, protected,
   * private, etc.).
   * </p>
   *
   * @param clazz the class for which to find the constructor; must not be {@code null}
   * @param parameterTypes an array of {@link Class} objects representing the parameter types of the constructor; may
   *   be {@code null} if the constructor takes no parameters; empty array matches no-argument constructors
   * @return a {@link me.kvdpxne.modjit.accessor.ConstructorInitializer} for the specified constructor; never
   *   {@code null}
   * @throws me.kvdpxne.modjit.exception.ConstructorNotFoundReflectionException if no constructor with the specified
   *   parameter types is found in the class
   * @throws IllegalArgumentException if {@code clazz} is {@code null}
   * @throws SecurityException if access to the class's declared constructors is denied
   */
  public static ConstructorInitializer getConstructor(
    final Class<?> clazz,
    final Class<?>[] parameterTypes
  ) {
    return Reflection.getConstructor(clazz, parameterTypes, 0);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.ConstructorInitializer} for a constructor in the specified class,
   * matching its modifiers.
   * <p>
   * This is a convenience method equivalent to calling {@link #getConstructor(Class, Class[], int)} with
   * {@code parameterTypes} as {@code null}.
   * </p>
   * <p>
   * This method is useful for finding constructors with specific accessibility characteristics, such as public
   * constructors ({@code Modifier.PUBLIC}) or private constructors ({@code Modifier.PRIVATE}). The method will return
   * the first constructor that matches the specified modifiers.
   * </p>
   *
   * @param clazz the class for which to find the constructor; must not be {@code null}
   * @param modifiers the required modifiers for the constructor, as defined by
   *   {@link java.lang.reflect.Modifier#constructorModifiers()}; must not be zero; multiple modifiers can be combined
   *   using bitwise OR (e.g., {@code Modifier.PUBLIC})
   * @return a {@link me.kvdpxne.modjit.accessor.ConstructorInitializer} for the specified constructor; never
   *   {@code null}
   * @throws me.kvdpxne.modjit.exception.ConstructorNotFoundReflectionException if no constructor with the specified
   *   modifiers is found in the class
   * @throws IllegalArgumentException if {@code clazz} is {@code null}, {@code modifiers} is zero, or
   *   {@code modifiers} contains invalid constructor modifier bits
   * @throws SecurityException if access to the class's declared constructors is denied
   */
  public static ConstructorInitializer getConstructor(
    final Class<?> clazz,
    final int modifiers
  ) {
    return Reflection.getConstructor(clazz, null, modifiers);
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.ConstructorInitializer} for a no-argument constructor in the
   * specified class.
   * <p>
   * This is a convenience method equivalent to calling {@link #getConstructor(Class, Class[], int)} with both
   * {@code parameterTypes} and {@code modifiers} as {@code null} and {@code 0} respectively.
   * </p>
   * <p>
   * This method searches for a constructor that takes no parameters (a default constructor) and has no specific
   * modifier requirements. It is commonly used for simple object instantiation when the class has a public or
   * accessible no-argument constructor.
   * </p>
   * <p>
   * <strong>Note:</strong> If multiple no-argument constructors exist (which is rare but possible
   * in bytecode-manipulated classes), the first one encountered during reflection will be returned.
   * </p>
   *
   * @param clazz the class for which to find the constructor; must not be {@code null}
   * @return a {@link me.kvdpxne.modjit.accessor.ConstructorInitializer} for the no-argument constructor; never
   *   {@code null}
   * @throws me.kvdpxne.modjit.exception.ConstructorNotFoundReflectionException if no no-argument constructor is found
   *   in the class
   * @throws IllegalArgumentException if {@code clazz} is {@code null}
   * @throws SecurityException if access to the class's declared constructors is denied
   * @see #getConstructor(Class, Class[])
   * @see #getConstructor(Class, int)
   */
  public static ConstructorInitializer getConstructor(
    final Class<?> clazz
  ) {
    return Reflection.getConstructor(clazz, null, 0);
  }

  public static ConstructorBuilder newConstructorFinder() {
    return new ConstructorBuilder();
  }

  /**
   * Holder class for the singleton {@link me.kvdpxne.modjit.cache.component.ClassCache} instance.
   * <p>
   * Uses the Initialization-on-demand holder idiom for thread-safe lazy loading. The cache instance is created only
   * when the holder class is first referenced.
   * </p>
   */
  private static final class ClassesCacheHolder {
    private static final ClassCache CACHE = new ClassCache();
  }

  /**
   * Holder class for the singleton {@link me.kvdpxne.modjit.cache.component.ConstructorCache} instance.
   * <p>
   * Uses the Initialization-on-demand holder idiom for thread-safe lazy loading.
   * </p>
   */
  private static final class ConstructorsCacheHolder {
    private static final ConstructorCache CACHE = new ConstructorCache();
  }

  /**
   * Holder class for the singleton {@link me.kvdpxne.modjit.cache.component.FieldCache} instance.
   * <p>
   * Uses the Initialization-on-demand holder idiom for thread-safe lazy loading.
   * </p>
   */
  private static final class FieldsCacheHolder {
    private static final FieldCache CACHE = new FieldCache();
  }

  /**
   * Holder class for the singleton {@link me.kvdpxne.modjit.cache.component.MethodCache} instance.
   * <p>
   * Uses the Initialization-on-demand holder idiom for thread-safe lazy loading.
   * </p>
   */
  private static final class MethodsCacheHolder {
    private static final MethodCache CACHE = new MethodCache();
  }
}
