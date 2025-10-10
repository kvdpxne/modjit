package me.kvdpxne.modjit.exception;

/**
 * Thrown when a field cannot be found in a specified class with the
 * requested name and optional type during a reflection operation.
 * <p>
 * This exception is typically thrown by the {@link me.kvdpxne.modjit.Reflection}
 * utility class when attempting to retrieve a field via
 * {@link Class#getDeclaredField(String)} (or similar logic) and no matching
 * field is found in the class.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @since 0.1.0
 * @version 0.1.0
 */
public final class FieldNotFoundReflectionException
  extends
  ReflectionException {

  /**
   * Constructs a new {@code FieldNotFoundReflectionException} with the
   * specified class name, field name, and optional field type that were not found.
   *
   * @param className The fully qualified name of the class in which the
   *                  field was sought.
   * @param fieldName The simple name of the field that could not be found.
   * @param fieldType The expected type of the field. Can be {@code null}
   *                  if the type was not part of the search criteria.
   */
  public FieldNotFoundReflectionException(
    final String className,
    final String fieldName,
    final Class<?> fieldType
  ) {
    super(
      "Field '" + fieldName + "' not found in " + className
        + (null != fieldType ? "(expected type: " + fieldType.getName() + ")" : "")
    );
  }
}
