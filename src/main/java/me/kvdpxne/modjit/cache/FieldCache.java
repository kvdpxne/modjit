package me.kvdpxne.modjit.cache;

import me.kvdpxne.modjit.acessor.FieldAccessor;
import me.kvdpxne.modjit.cache.invoker.FieldAccessorImpl;
import me.kvdpxne.modjit.cache.key.FieldKey;
import me.kvdpxne.modjit.exception.FieldNotFoundReflectionException;
import me.kvdpxne.modjit.util.AccessController;
import java.lang.reflect.Field;

/**
 * A specialized {@link ReflectionCache} for caching {@link FieldAccessor}
 * objects. It computes and caches {@link FieldAccessorImpl} instances based
 * on a {@link FieldKey}.
 * <p>
 * This cache ensures that the lookup and preparation of fields via
 * {@link Class#getDeclaredFields()} and the creation of the corresponding
 * {@link FieldAccessor} are performed only once for a given class, field name,
 * and optional field type, improving performance for repeated accesses.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @since 0.1.0
 * @version 0.1.0
 */
public final class FieldCache
  extends
  ReflectionCache<FieldKey, FieldAccessor> {

  /**
   * Looks up a declared field within the specified class that matches the
   * given field name and optional field type, and creates a new
   * {@link FieldAccessorImpl} for it.
   * <p>
   * This method iterates through all declared fields of the class and
   * compares their names and types. If a matching field is found, it
   * determines the original accessibility state of the field and wraps it
   * in a new {@code FieldAccessorImpl}. If no matching field is found, a
   * {@link FieldNotFoundReflectionException} is thrown.
   * </p>
   *
   * @param clazz     The class in which to search for the field.
   *                  Must not be {@code null}.
   * @param fieldName The simple name of the field to find.
   *                  Must not be blank.
   * @param fieldType The expected type of the field. Can be {@code null}
   *                  if the type is not part of the search criteria.
   * @return A new {@link FieldAccessorImpl} instance wrapping the found field.
   * @throws FieldNotFoundReflectionException if no field with the specified
   *     name (and type, if provided) is found in the class.
   */
  private FieldAccessor computeField(
    final Class<?> clazz,
    final String fieldName,
    final Class<?> fieldType
  ) {
    final Field[] allFields = clazz.getDeclaredFields();
    Field field = null;
    for (final Field nextField : allFields) {
      if (nextField.getName().equals(fieldName)
        && (null == fieldType || nextField.getType().equals(fieldType))
      ) {
        field = nextField;
        break;
      }
    }
    if (null == field) {
      throw new FieldNotFoundReflectionException(clazz.getName(), fieldName, fieldType);
    }
    final boolean originalAccessible = AccessController.isAccessible(field, null);
    return new FieldAccessorImpl(field, originalAccessible, clazz.getName());
  }

  /**
   * Retrieves a {@link FieldAccessor} from the cache or computes it if not present.
   * <p>
   * The cache key is constructed using the class name, field name, and the
   * field type name (if provided). The computation is performed by the
   * internal {@link #computeField(Class, String, Class)} method.
   * </p>
   *
   * @param clazz     The class for which to retrieve or compute the
   *                  field accessor. Must not be {@code null}.
   * @param fieldName The simple name of the field to access.
   *                  Must not be blank.
   * @param fieldType The expected type of the field. Can be {@code null}
   *                  if the type is not part of the search criteria.
   * @return The cached or newly computed {@link FieldAccessor} for the
   *     specified field.
   * @throws FieldNotFoundReflectionException if no field with the specified
   *     name (and type, if provided) is found in the class.
   * @throws NullPointerException if {@code clazz} is {@code null} or
   *     {@code fieldName} is {@code null}.
   */
  public FieldAccessor getOrCompute(
    final Class<?> clazz,
    final String fieldName,
    final Class<?> fieldType
  ) {
    return this.getOrCompute(
      new FieldKey(
        clazz.getName(),
        fieldName,
        null != fieldType
          ? fieldType.getName()
          : null
      ),
      () -> this.computeField(clazz, fieldName, fieldType)
    );
  }
}
