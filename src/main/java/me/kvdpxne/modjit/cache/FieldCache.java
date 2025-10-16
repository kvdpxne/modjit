package me.kvdpxne.modjit.cache;

import java.lang.reflect.Field;
import me.kvdpxne.modjit.accessor.FieldAccessor;
import me.kvdpxne.modjit.cache.invoker.FieldAccessorImpl;
import me.kvdpxne.modjit.cache.key.FieldKey;
import me.kvdpxne.modjit.exception.FieldNotFoundReflectionException;
import me.kvdpxne.modjit.util.AccessController;

/**
 * A specialized {@link me.kvdpxne.modjit.cache.ReflectionCache} for caching
 * {@link me.kvdpxne.modjit.accessor.FieldAccessor} objects. It computes and caches
 * {@link me.kvdpxne.modjit.cache.invoker.FieldAccessorImpl} instances based on a
 * {@link me.kvdpxne.modjit.cache.key.FieldKey}.
 * <p>
 * This cache ensures that the lookup and preparation of fields via {@link java.lang.Class#getDeclaredFields()} and the
 * creation of the corresponding {@link me.kvdpxne.modjit.accessor.FieldAccessor} are performed only once for a given
 * class, field name, optional field type, and modifiers, improving performance for repeated accesses.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public final class FieldCache
  extends
  ReflectionCache<FieldKey, FieldAccessor> {

  /**
   * Checks if a given field matches the specified search criteria.
   * <p>
   * This helper method evaluates whether a candidate {@link java.lang.reflect.Field} object matches the provided name,
   * type, and modifiers. A criterion is considered matching if the corresponding input parameter is null (or zero for
   * modifiers) or if the field's attribute equals the specified value. For example, if {@code name} is null, the name
   * check is skipped. If {@code type} is non-null, it's compared using
   * {@link java.lang.Class#equals(java.lang.Object)}.
   * </p>
   *
   * @param field The candidate {@link java.lang.reflect.Field} to check.
   * @param name The expected field name. Can be {@code null} to skip name matching.
   * @param type The expected field type. Can be {@code null} to skip type matching.
   * @param modifiers The required modifiers. Use {@code 0} to skip modifier matching.
   * @param nullName {@code true} if {@code name} was {@code null}.
   * @param nullType {@code true} if {@code type} was {@code null}.
   * @param emptyModifiers {@code true} if {@code modifiers} was {@code 0}.
   * @return {@code true} if the field matches all specified non-null/zero criteria, {@code false} otherwise.
   */
  private boolean checkConditions(
    final Field field,
    final String name,
    final Class<?> type,
    final int modifiers,
    final boolean nullName,
    final boolean nullType,
    final boolean emptyModifiers
  ) {
    // noinspection MagicConstant
    return (nullName || field.getName().equals(name))
      && (nullType || field.getType().equals(type))
      && (emptyModifiers || field.getModifiers() == modifiers);
  }

  /**
   * Looks up a declared field within the specified class that matches the given field name, optional field type, and
   * optional modifiers, and creates a new {@link me.kvdpxne.modjit.cache.invoker.FieldAccessorImpl} for it.
   * <p>
   * This method iterates through all declared fields of the class and compares their names, types (if provided), and
   * modifiers (if non-zero). If a matching field is found, it determines the original accessibility state of the field
   * and wraps it in a new {@code FieldAccessorImpl}. If no matching field is found, a
   * {@link me.kvdpxne.modjit.exception.FieldNotFoundReflectionException} is thrown.
   * </p>
   *
   * @param clazz The class in which to search for the field. Must not be {@code null}.
   * @param name The simple name of the field to find. Can be {@code null} if name is not part of the search
   *   criteria.
   * @param type The expected type of the field. Can be {@code null} if the type is not part of the search criteria.
   * @param modifiers The required modifiers for the field. Use {@code 0} to ignore modifiers.
   * @return A new {@link me.kvdpxne.modjit.cache.invoker.FieldAccessorImpl} instance wrapping the found field.
   * @throws me.kvdpxne.modjit.exception.FieldNotFoundReflectionException if no field with the specified name (and
   *   type/modifiers if provided/non-zero) is found in the class.
   */
  private FieldAccessor computeField(
    final Class<?> clazz,
    final String name,
    final Class<?> type,
    final int modifiers
  ) {
    final Field[] allFields = clazz.getDeclaredFields();
    Field field = null;
    final boolean emptyModifiers = 0 == modifiers,
      nullType = null == type,
      nullName = null == name;
    for (final Field nextField : allFields) {
      if (this.checkConditions(nextField, name, type, modifiers,
        nullName, nullType, emptyModifiers)
      ) {
        field = nextField;
        break;
      }
    }
    if (null == field) {
      throw new FieldNotFoundReflectionException(clazz.getName(), name, type);
    }
    final boolean originalAccessible = AccessController.isAccessible(field, null);
    return new FieldAccessorImpl(field, originalAccessible, clazz.getName());
  }

  /**
   * Retrieves a {@link me.kvdpxne.modjit.accessor.FieldAccessor} from the cache or computes it if not present.
   * <p>
   * The cache key is constructed using the class name, field name, the field type name (if provided), and the
   * modifiers. The computation is performed by the internal
   * {@link #computeField(java.lang.Class, java.lang.String, java.lang.Class, int)} method.
   * </p>
   *
   * @param clazz The class for which to retrieve or compute the field accessor. Must not be {@code null}.
   * @param name The simple name of the field to access. Can be {@code null} if name is not part of the search
   *   criteria.
   * @param type The expected type of the field. Can be {@code null} if the type is not part of the search criteria.
   * @param modifiers The required modifiers for the field. Use {@code 0} to ignore modifiers.
   * @return The cached or newly computed {@link me.kvdpxne.modjit.accessor.FieldAccessor} for the specified field.
   * @throws me.kvdpxne.modjit.exception.FieldNotFoundReflectionException if no field with the specified name (and
   *   type/modifiers if provided/non-zero) is found in the class.
   * @throws java.lang.NullPointerException if {@code clazz} is {@code null}.
   */
  public FieldAccessor getOrCompute(
    final Class<?> clazz,
    final String name,
    final Class<?> type,
    final int modifiers
  ) {
    return this.getOrCompute(
      new FieldKey(
        clazz.getName(),
        name,
        null != type
          ? type.getName()
          : null,
        modifiers
      ),
      () -> this.computeField(clazz, name, type, modifiers)
    );
  }
}
