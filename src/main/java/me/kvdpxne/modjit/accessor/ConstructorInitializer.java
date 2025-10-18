package me.kvdpxne.modjit.accessor;

/**
 * Defines the contract for creating new instances of a class through reflection by invoking constructors.
 * <p>
 * This interface abstracts the underlying {@link java.lang.reflect.Constructor} object, providing a simplified API for
 * object instantiation while handling reflection complexities such as accessibility management, exception translation,
 * and parameter passing. Implementations are responsible for managing constructor accessibility and converting
 * reflection-specific exceptions into the library's standardized exception hierarchy.
 * </p>
 * <p>
 * Typical usage involves obtaining a {@code ConstructorInitializer} instance from the
 * {@link me.kvdpxne.modjit.Reflection} utility class and using it to create object instances without direct interaction
 * with the reflection API.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public interface ConstructorInitializer {

  /**
   * Creates a new instance of the target class by invoking the underlying constructor with the specified parameters.
   * <p>
   * This method handles the complete constructor invocation process, including accessibility management and exception
   * translation. The constructor is temporarily made accessible if necessary, with its original accessibility state
   * restored after invocation regardless of success or failure.
   * </p>
   * <p>
   * If the constructor execution throws an exception, it is wrapped in a
   * {@link me.kvdpxne.modjit.exception.ReflectionException} with the original exception as the cause. This includes
   * exceptions thrown by the constructor logic itself, which are accessible via
   * {@link java.lang.reflect.InvocationTargetException}.
   * </p>
   *
   * @param parameters an array of objects representing the arguments to pass to the constructor; may be {@code null}
   *   to indicate no arguments or an empty array for a no-argument constructor; array elements must match the
   *   constructor's parameter types in order and assignment compatibility
   * @return a new instance of the target class created by invoking the constructor; never {@code null} unless the
   *   constructor explicitly returns {@code null}
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the underlying constructor is not accessible
   *   and cannot be made accessible due to security restrictions imposed by the Java runtime environment or security
   *   manager
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs during the construction process,
   *   including but not limited to: instantiation failure of abstract classes, parameter type mismatches, incorrect
   *   number of parameters, or exceptions thrown by the constructor logic itself; the cause chain preserves the
   *   original exception
   * @throws java.lang.IllegalArgumentException if the parameters array length does not match the constructor's
   *   parameter count, or if parameter values are not assignment-compatible with the formal parameter types
   */
  Object newInstance(
    Object[] parameters
  );

  /**
   * Creates a new instance of the target class by invoking the underlying constructor with no parameters.
   * <p>
   * This is a convenience method equivalent to calling {@link #newInstance(Object[])} with {@code null} or an empty
   * parameter array. It provides simplified access to default constructors or constructors that take no arguments.
   * </p>
   * <p>
   * Example usage:
   * <pre>{@code
   * ConstructorInitializer initializer = Reflection.getConstructor(MyClass.class);
   * MyClass instance = (MyClass) initializer.newInstance();
   * }</pre>
   * </p>
   *
   * @return a new instance of the target class created by invoking the no-argument constructor; never {@code null}
   *   unless the constructor explicitly returns {@code null}
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the underlying constructor is not accessible
   *   and cannot be made accessible due to security restrictions
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs during the construction process,
   *   including but not limited to: instantiation failure of abstract classes, invocation issues, or if the class does
   *   not have an accessible no-argument constructor; the cause chain preserves the original exception
   * @throws java.lang.ExceptionInInitializerError if the constructor initialization triggers an exception
   */
  default Object newInstance() {
    return this.newInstance(null);
  }
}
