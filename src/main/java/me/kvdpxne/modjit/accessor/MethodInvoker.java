package me.kvdpxne.modjit.accessor;

/**
 * Defines the contract for invoking methods through reflection.
 * <p>
 * This interface abstracts the underlying {@link java.lang.reflect.Method} object, providing a simplified API
 * for method invocation while handling reflection complexities such as accessibility management, parameter
 * passing, return value handling, and exception translation. Implementations manage method accessibility
 * and convert reflection-specific exceptions into the library's standardized exception hierarchy.
 * </p>
 * <p>
 * The interface supports both instance methods (non-static) and static methods. For static method invocations,
 * the target parameter can be {@code null}. All invocations automatically handle accessibility concerns
 * and restore the original accessibility state after completion.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public interface MethodInvoker {

  /**
   * Invokes the underlying method on the specified target object with the provided parameters.
   * <p>
   * This method executes the method with the given parameters, automatically handling accessibility
   * if required. For static methods, the target parameter is ignored and can be {@code null}. For
   * instance methods, the target object must be an instance of the class declaring the method.
   * </p>
   * <p>
   * The method is temporarily made accessible if necessary, with its original accessibility state
   * restored after the invocation completes, regardless of success or failure. If the method execution
   * throws an exception, it is wrapped in a {@link me.kvdpxne.modjit.exception.ReflectionException}
   * with the original exception as the cause.
   * </p>
   *
   * @param target the object on which to invoke the method; for static methods, this parameter can be
   *        {@code null}; for instance methods, must be a non-null instance of the declaring class
   * @param parameters an array of objects representing the arguments to pass to the method; may be
   *        {@code null} to indicate no arguments or an empty array for a no-argument method; array
   *        elements must match the method's parameter types in order and assignment compatibility
   * @return the return value from the method invocation; may be {@code null} if the method returns
   *         {@code null} or has a {@code void} return type
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the underlying method is not
   *         accessible and cannot be made accessible due to security restrictions imposed by the
   *         Java runtime environment or security manager
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs during method invocation,
   *         including exceptions thrown by the method itself; the cause chain preserves the original
   *         exception thrown by the method logic
   * @throws java.lang.NullPointerException if the method is an instance method and the target object
   *         is {@code null}
   * @throws java.lang.IllegalArgumentException if the target object is not an instance of the class
   *         or interface declaring the underlying method, if the parameters array length does not
   *         match the method's parameter count, or if parameter values are not assignment-compatible
   *         with the formal parameter types
   */
  Object invoke(
    Object target,
    Object[] parameters
  );

  /**
   * Invokes the underlying method on the specified target object with no parameters.
   * <p>
   * This is a convenience method equivalent to calling {@link #invoke(Object, Object[])} with the
   * specified target and {@code null} parameters. It provides simplified access to no-argument methods.
   * </p>
   *
   * @param target the object on which to invoke the method; for static methods, this parameter can be
   *        {@code null}; for instance methods, must be a non-null instance of the declaring class
   * @return the return value from the method invocation; may be {@code null} if the method returns
   *         {@code null} or has a {@code void} return type
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the underlying method is not
   *         accessible and cannot be made accessible due to security restrictions
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs during method invocation,
   *         including exceptions thrown by the method itself
   * @throws java.lang.NullPointerException if the method is an instance method and the target object
   *         is {@code null}
   * @throws java.lang.IllegalArgumentException if the target object is not an instance of the class
   *         or interface declaring the underlying method, or if the method requires parameters
   */
  default Object invoke(
    Object target
  ) {
    return this.invoke(target, null);
  }

  /**
   * Invokes a static method with the provided parameters.
   * <p>
   * This is a convenience method equivalent to calling {@link #invoke(Object, Object[])} with a
   * {@code null} target and the specified parameters. It provides simplified invocation of static
   * methods without requiring a target object instance.
   * </p>
   *
   * @param parameters an array of objects representing the arguments to pass to the static method;
   *        may be {@code null} to indicate no arguments or an empty array for a no-argument method;
   *        array elements must match the method's parameter types in order and assignment compatibility
   * @return the return value from the static method invocation; may be {@code null} if the method
   *         returns {@code null} or has a {@code void} return type
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the underlying method is not
   *         accessible and cannot be made accessible due to security restrictions
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs during method invocation,
   *         including exceptions thrown by the method itself
   * @throws java.lang.IllegalArgumentException if the parameters array length does not match the
   *         method's parameter count, or if parameter values are not assignment-compatible with
   *         the formal parameter types
   */
  default Object invoke(
    Object[] parameters
  ) {
    return this.invoke(null, parameters);
  }

  /**
   * Invokes a static method with no parameters.
   * <p>
   * This is a convenience method equivalent to calling {@link #invoke(Object, Object[])} with both
   * target and parameters as {@code null}. It provides the simplest form of static method invocation
   * for no-argument methods.
   * </p>
   *
   * @return the return value from the static method invocation; may be {@code null} if the method
   *         returns {@code null} or has a {@code void} return type
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the underlying method is not
   *         accessible and cannot be made accessible due to security restrictions
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs during method invocation,
   *         including exceptions thrown by the method itself
   * @throws java.lang.IllegalArgumentException if the method requires parameters
   */
  default Object invoke() {
    return this.invoke(null, null);
  }
}
