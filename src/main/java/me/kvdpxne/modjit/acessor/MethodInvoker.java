package me.kvdpxne.modjit.acessor;

/**
 * Defines the contract for invoking methods on objects using reflection.
 * Implementations of this interface handle the invocation of methods,
 * abstracting the underlying {@link java.lang.reflect.Method} object.
 * <p>
 * This interface provides methods to invoke a target method with or without
 * parameters on a given object instance.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @since 0.1.0
 * @version 0.1.0
 */
public interface MethodInvoker {

  /**
   * Invokes the underlying method on the specified target object with the
   * provided parameters.
   *
   * @param target     The object on which to invoke the method. For static
   *                   methods, this parameter can be {@code null}.
   * @param parameters An array of objects representing the arguments to pass
   *                   to the method. If the method takes no arguments,
   *                   an empty array ({@code new Object[0]}) should be passed.
   *                   Passing {@code null} will result in a single argument
   *                   of {@code null} being passed to the method.
   * @return The return value of the method invocation. If the method has a
   *     {@code void} return type, this will be {@code null}.
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the
   *     underlying method is not accessible and cannot be made accessible.
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs
   *     during the method invocation, such as an exception thrown by the
   *     invoked method itself.
   */
  Object invoke(
    Object target,
    Object[] parameters
  );

  /**
   * Invokes the underlying method on the specified target object with no
   * parameters.
   * <p>
   * This is a convenience method equivalent to calling
   * {@code invoke(target, null)} or {@code invoke(target, new Object[0])}.
   * </p>
   *
   * @param target The object on which to invoke the method. For static
   *               methods, this parameter can be {@code null}.
   * @return The return value of the method invocation. If the method has a
   *     {@code void} return type, this will be {@code null}.
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the
   *     underlying method is not accessible and cannot be made accessible.
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs
   *     during the method invocation, such as an exception thrown by the
   *     invoked method itself.
   */
  default Object invoke(
    Object target
  ) {
    return this.invoke(target, null);
  }
}
