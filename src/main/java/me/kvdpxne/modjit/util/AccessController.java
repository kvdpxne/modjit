package me.kvdpxne.modjit.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Provides utility methods for managing the accessibility of reflection objects
 * ({@link java.lang.reflect.AccessibleObject AccessibleObject}, such as
 * {@link Field}, {@link Method}, or {@link java.lang.reflect.Constructor}).
 * <p>
 * This class handles differences in accessibility checks and setting across
 * different Java versions, particularly versions 9 and above, to ensure
 * consistent behavior and prevent "illegal reflective access" warnings.
 * It includes logic to detect the current Java version and manage the
 * accessibility state of reflection members accordingly.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @since 0.1.0
 * @version 0.1.0
 */
public final class AccessController {

  /**
   * The detected Java version of the runtime environment.
   * This value is determined once during class loading by {@link #getJavaVersion()}.
   */
  static final int JAVA_VERSION = getJavaVersion();

  /**
   * Prevents instantiation of this utility class.
   */
  private AccessController() {
    throw new AssertionError();
  }

  /**
   * Detects the major Java version of the current runtime environment.
   * <p>
   * It retrieves the value of the {@code java.version} system property
   * and parses the first numeric part found (e.g., "11.0.1" -> 11, "1.8.0_202" -> 8).
   * </p>
   *
   * @return The major Java version number (e.g., 8, 11, 17).
   * @throws IllegalStateException if the {@code java.version} system property
   *     is {@code null} or cannot be parsed into a valid number.
   */
  private static int getJavaVersion() {
    final String version = System.getProperty("java.version");
    if (null == version) {
      throw new IllegalStateException("Unable to determine Java version: system property 'java.version' is null");
    }
    final String[] parts = version.split("[._]");
    for (final String part : parts) {
      try {
        int number = Integer.parseInt(part.trim());
        if (0 != number) { // Handles cases like "1.8" where the first part might be 1, then 8
          return number;
        }
      } catch (final NumberFormatException ignored) {
      }
    }
    throw new IllegalStateException("Unable to parse Java version from string: " + version);
  }

  /**
   * Checks if the given reflection member is package-private (package access).
   * <p>
   * A member is considered package-private if its modifiers do not include
   * the {@code public}, {@code protected}, or {@code private} flags.
   * </p>
   *
   * @param member The reflection member ({@link Field} or {@link Method})
   *               to check for package-private access.
   * @return {@code true} if the member is package-private, {@code false} otherwise.
   *         Returns {@code false} if the member is not a {@link Field} or {@link Method}.
   */
  private static boolean isPackagePrivate(
    final AccessibleObject member
  ) {
    final int modifiers;
    if (member instanceof Field) {
      modifiers = ((Field) member).getModifiers();
    } else if (member instanceof Method) {
      modifiers = ((Method) member).getModifiers();
    } else {
      // Constructors are implicitly public, protected, private, or package-private,
      // but this specific check is primarily for Field/Method.
      // For other AccessibleObjects, default to false.
      modifiers = 0;
    }
    // Check that none of PUBLIC, PROTECTED, or PRIVATE bits are set
    return 0 == (modifiers & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE));
  }

  /**
   * Checks if the given reflection member is accessible for the specified target object.
   * <p>
   * This method handles the difference between pre-Java 9 and Java 9+ APIs.
   * On Java 9 and later, it uses {@link AccessibleObject#canAccess(Object)}.
   * On earlier versions, it uses {@link AccessibleObject#isAccessible()}.
   * </p>
   *
   * @param member The reflection member to check for accessibility.
   * @param target The object on which the member will be accessed (relevant for Java 9+).
   *               Can be {@code null} for static members or when checking general accessibility.
   * @return {@code true} if the member is accessible, {@code false} otherwise.
   */
  public static boolean isAccessible(
    final AccessibleObject member,
    final Object target
  ) {
    if (9 <= JAVA_VERSION) {
      // noinspection Since15
      return member.canAccess(target);
    }
    return member.isAccessible();
  }

  /**
   * Sets the accessibility flag of the given reflection member.
   * <p>
   * This method handles special cases for Java 12 and above, where setting
   * accessibility for package-private members might require reflection on the
   * {@code setAccessible} method itself due to module system restrictions.
   * For other cases, it directly calls {@link AccessibleObject#setAccessible(boolean)}.
   * </p>
   *
   * @param member The reflection member whose accessibility is to be set.
   * @param flag   {@code true} to set the member accessible, {@code false} otherwise.
   * @throws RuntimeException if setting accessibility for a package-private member
   *     on Java 12+ fails due to reflection restrictions.
   */
  public static void setAccessible(
    final AccessibleObject member,
    final boolean flag
  ) {
    if (12 <= JAVA_VERSION && flag && isPackagePrivate(member)) {
      // Special handling for Java 12+ package-private members
      try {
        member.getClass()
          .getDeclaredMethod("setAccessible", boolean.class)
          .invoke(member, true);
      } catch (final ReflectiveOperationException exception) {
        throw new RuntimeException(
          "Cannot make package-private member accessible in Java " + JAVA_VERSION,
          exception
        );
      }
      return;
    }
    member.setAccessible(flag);
  }
}
