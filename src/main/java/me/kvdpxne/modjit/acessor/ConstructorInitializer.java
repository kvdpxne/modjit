package me.kvdpxne.modjit.acessor;

/**
 * Defines the contract for initializing new instances of a class using reflection.
 * Implementations of this interface handle the invocation of constructors,
 * abstracting the underlying {@link java.lang.reflect.Constructor} object.
 * <p>
 * This interface provides methods to create new instances with or without
 * constructor parameters.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @since 0.1.0
 * @version 0.1.0
 */
public interface ConstructorInitializer {

  /**
   * Creates a new instance of the target class by invoking the underlying
   * constructor with the specified parameters.
   *
   * @param parameters An array of objects representing the arguments to pass
   *                   to the constructor. If the constructor takes no arguments,
   *                   an empty array or {@code null} can be passed.
   * @return A new instance of the target class.
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the
   *     underlying constructor is not accessible and cannot be made accessible.
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs
   *     during the construction process, such as instantiation failure or
   *     invocation issues.
   */
  Object newInstance(
    Object[] parameters
  );

  /**
   * Creates a new instance of the target class by invoking the underlying
   * constructor with no parameters.
   * <p>
   * This is a convenience method equivalent to calling
   * {@code newInstance(null)} or {@code newInstance(new Object[0])}.
   * </p>
   *
   * @return A new instance of the target class.
   * @throws me.kvdpxne.modjit.exception.ReflectionSecurityException if the
   *     underlying constructor is not accessible and cannot be made accessible.
   * @throws me.kvdpxne.modjit.exception.ReflectionException if an error occurs
   *     during the construction process, such as instantiation failure or
   *     invocation issues.
   */
  default Object newInstance() {
    return this.newInstance(null);
  }
}
