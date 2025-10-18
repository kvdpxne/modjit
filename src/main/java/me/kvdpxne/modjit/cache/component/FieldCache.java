package me.kvdpxne.modjit.cache.component;

import java.lang.reflect.Field;
import me.kvdpxne.modjit.accessor.FieldAccessor;
import me.kvdpxne.modjit.accessor.impl.FieldAccessorImpl;
import me.kvdpxne.modjit.cache.ReflectionCache;
import me.kvdpxne.modjit.cache.key.FieldKey;
import me.kvdpxne.modjit.exception.FieldNotFoundReflectionException;
import me.kvdpxne.modjit.util.AccessController;

/**
 * A specialized cache for storing and retrieving {@link me.kvdpxne.modjit.accessor.FieldAccessor} objects that provide
 * access to class fields through reflection.
 * <p>
 * This cache extends {@link me.kvdpxne.modjit.cache.ReflectionCache} to provide efficient, thread-safe storage of field
 * accessors. It ensures that field lookup operations via {@link java.lang.Class#getDeclaredFields()} and the creation
 * of corresponding {@link me.kvdpxne.modjit.accessor.FieldAccessor} instances are performed only once for a given
 * combination of class, field name, field type, and modifiers.
 * </p>
 * <p>
 * The cache supports flexible field lookup criteria, allowing searches by name alone, type alone, modifiers alone, or
 * any combination thereof. All cached field accessors are wrapped in
 * {@link me.kvdpxne.modjit.accessor.impl.FieldAccessorImpl} instances that manage accessibility state and exception
 * translation.
 * </p>
 * <p>
 * This implementation uses weak references to cached field accessors, allowing them to be garbage collected when no
 * longer strongly referenced, while maintaining performance benefits for frequently accessed fields.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @see me.kvdpxne.modjit.cache.ReflectionCache
 * @see me.kvdpxne.modjit.cache.key.FieldKey
 * @see me.kvdpxne.modjit.accessor.FieldAccessor
 * @since 0.1.0
 */
public final class FieldCache
  extends
  ReflectionCache<FieldKey, FieldAccessor> {

  /**
   * Determines whether a candidate field matches the specified search criteria.
   * <p>
   * This helper method evaluates a {@link java.lang.reflect.Field} against the provided name, type, and modifier
   * requirements. Each criterion is optional - if name is {@code null}, name matching is skipped; if type is
   * {@code null}, type matching is skipped; if modifiers are zero, modifier matching is skipped. This allows flexible
   * field lookup based on partial criteria.
   * </p>
   *
   * @param field the candidate {@link java.lang.reflect.Field} to evaluate; must not be {@code null}
   * @param name the expected name of the field; may be {@code null} to skip name matching
   * @param type the expected type of the field; may be {@code null} to skip type matching
   * @param modifiers the required modifiers for the field; use {@code 0} to skip modifier matching
   * @param nullName {@code true} if {@code name} was {@code null}, indicating name matching should be skipped
   * @param nullType {@code true} if {@code type} was {@code null}, indicating type matching should be skipped
   * @param emptyModifiers {@code true} if {@code modifiers} was {@code 0}, indicating modifier matching should be
   *   skipped
   * @return {@code true} if the field matches all specified non-null criteria; {@code false} if any specified criterion
   *   does not match
   */
  private static boolean checkConditions(
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
   * Looks up a declared field and creates a corresponding field accessor.
   * <p>
   * This method searches through all declared fields of the specified class to find one that matches the given name,
   * type, and modifiers. If no matching field is found, a
   * {@link me.kvdpxne.modjit.exception.FieldNotFoundReflectionException} is thrown.
   * </p>
   * <p>
   * The search criteria are combined using logical AND - a field must match all specified non-null criteria to be
   * selected. For example, if both name and type are specified, the field must match both the name and type exactly.
   * </p>
   * <p>
   * The method determines the original accessibility state of the field and creates a
   * {@link me.kvdpxne.modjit.accessor.impl.FieldAccessorImpl} that will manage accessibility during field access
   * operations.
   * </p>
   *
   * @param clazz the class in which to search for the field; must not be {@code null}
   * @param name the simple name of the field to find; may be {@code null} to match fields regardless of name
   * @param type the expected type of the field; may be {@code null} to match fields regardless of type
   * @param modifiers the required modifiers for the field; use {@code 0} to match fields regardless of modifiers
   * @return a new {@link me.kvdpxne.modjit.accessor.impl.FieldAccessorImpl} instance wrapping the found field; never
   *   {@code null}
   * @throws me.kvdpxne.modjit.exception.FieldNotFoundReflectionException if no field with the specified name (if
   *   provided), type (if provided), and modifiers (if non-zero) is found in the class
   * @throws java.lang.NullPointerException if {@code clazz} is {@code null}
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
      if (FieldCache.checkConditions(nextField, name, type, modifiers,
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
   * Retrieves a field accessor from the cache or computes and caches it if not present.
   * <p>
   * This method provides thread-safe access to field accessors, ensuring that only one thread computes the field
   * accessor for a given key combination concurrently. The cache key is constructed from the class name, field name,
   * field type name (if provided), and modifiers.
   * </p>
   * <p>
   * Field type is converted to its fully qualified name for the cache key, allowing proper distinction between fields
   * with different types. The actual field lookup is performed by the internal
   * {@link #computeField(java.lang.Class, java.lang.String, java.lang.Class, int)} method.
   * </p>
   *
   * @param clazz the class for which to retrieve or compute the field accessor; must not be {@code null}
   * @param name the simple name of the field to access; may be {@code null} to match fields regardless of name
   * @param type the expected type of the field; may be {@code null} to match fields regardless of type
   * @param modifiers the required modifiers for the field; use {@code 0} to match fields regardless of modifiers
   * @return the cached or newly computed {@link me.kvdpxne.modjit.accessor.FieldAccessor} for the specified field;
   *   never {@code null}
   * @throws me.kvdpxne.modjit.exception.FieldNotFoundReflectionException if no field with the specified name (if
   *   provided), type (if provided), and modifiers (if non-zero) is found in the class
   * @throws java.lang.NullPointerException if {@code clazz} is {@code null}
   * @throws java.lang.SecurityException if access to the class's declared fields is denied
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
