package me.kvdpxne.modjit.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import me.kvdpxne.modjit.exception.ReflectionSecurityException;

/**
 * Provides utility methods for managing the accessibility of reflection objects ({@link AccessibleObject}, such as
 * {@link Field}, {@link Method}, or {@link java.lang.reflect.Constructor}).
 * <p>
 * This class handles differences in accessibility checks and setting across different Java versions, particularly
 * versions 9 and above, to ensure consistent behavior and prevent "illegal reflective access" warnings. It includes
 * logic to detect the current Java version and manage the accessibility state of reflection members accordingly.
 * </p>
 * <p>
 * For Java 9 and later, this class uses {@link AccessibleObject#canAccess(Object)} via reflection to check
 * accessibility. For earlier versions, it falls back to {@link AccessibleObject#isAccessible()}. Special handling is
 * provided for package-private members in Java 12 and above where direct accessibility setting may be restricted by the
 * module system.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public final class AccessController {

  /**
   * The detected Java version of the runtime environment. This value is determined once during class loading by
   * {@link #getSystemJavaVersion()} and cached for subsequent calls.
   */
  private static int javaVersion = 0;

  /**
   * Cached reference to the {@code canAccess} method from {@link AccessibleObject} for Java 9+. This is initialized on
   * first use and reused to avoid repeated reflection lookups.
   */
  private static Method canAccessMethod = null;

  /**
   * Prevents instantiation of this utility class.
   *
   * @throws AssertionError always thrown when constructor is invoked
   */
  private AccessController() {
    throw new AssertionError();
  }

  /**
   * Detects the major Java version of the current runtime environment.
   * <p>
   * Retrieves the value of the {@code java.version} system property and parses the first numeric part found. Examples:
   * </p>
   * <ul>
   *   <li>"11.0.1" → 11</li>
   *   <li>"1.8.0_202" → 8</li>
   *   <li>"17" → 17</li>
   * </ul>
   *
   * @return the major Java version number (e.g., 8, 11, 17)
   * @throws IllegalStateException if the {@code java.version} system property is {@code null} or cannot be parsed
   *   into a valid number
   */
  private static int getSystemJavaVersion() {
    try {
      final Runtime runtime = Runtime.getRuntime();
      final Object version = Runtime.class.getMethod("version").invoke(runtime);
      // noinspection unchecked
      final List<Integer> parts = (List<Integer>) version.getClass().getMethod("version").invoke(version);
      return parts.get(0);
    } catch (final Exception ignore) {
      final String version = System.getProperty("java.version");
      if (null == version) {
        throw new IllegalStateException("Unable to determine Java version: system property 'java.version' is null");
      }
      final String[] parts = version.split("[._]");
      for (final String part : parts) {
        try {
          final int number = Integer.parseInt(part.trim());
          if (0 != number) { // Handles cases like "1.8" where the first part might be 1, then 8
            return number;
          }
        } catch (final NumberFormatException ignored) {
          // Continue to next part if current part is not a number
        }
      }
      throw new IllegalStateException("Unable to parse Java version from string: " + version);
    }
  }

  /**
   * Returns the major Java version of the current runtime environment.
   * <p>
   * The version is detected once and cached for subsequent calls. The detection uses the {@code java.version} system
   * property and parses the major version number.
   * </p>
   *
   * @return the major Java version number (e.g., 8, 11, 17)
   * @throws IllegalStateException if the Java version cannot be determined from the system properties
   */
  public static int getJavaVersion() {
    if (0 == AccessController.javaVersion) {
      AccessController.javaVersion = AccessController.getSystemJavaVersion();
    }
    return AccessController.javaVersion;
  }

  /**
   * Checks if the given reflection member is package-private (package access).
   * <p>
   * A member is considered package-private if its modifiers do not include the {@code public}, {@code protected}, or
   * {@code private} flags.
   * </p>
   *
   * @param member the reflection member ({@link Field} or {@link Method}) to check for package-private access
   * @return {@code true} if the member is package-private, {@code false} otherwise; returns {@code false} if the member
   *   is not a {@link Field} or {@link Method}
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
   * This method handles the difference between pre-Java 9 and Java 9+ APIs:
   * </p>
   * <ul>
   *   <li>On Java 9 and later: uses {@link AccessibleObject#canAccess(Object)} via reflection</li>
   *   <li>On earlier versions: uses {@link AccessibleObject#isAccessible()}</li>
   * </ul>
   *
   * @param member the reflection member to check for accessibility
   * @param target the object on which the member will be accessed (relevant for Java 9+); can be {@code null} for
   *   static members or when checking general accessibility
   * @return {@code true} if the member is accessible, {@code false} otherwise
   * @throws ReflectionSecurityException if reflection fails on Java 9+ (e.g., method not found or invocation error)
   */
  public static boolean isAccessible(
    final AccessibleObject member,
    final Object target
  ) {
    if (9 <= AccessController.getJavaVersion()) {
      try {
        if (null != AccessController.canAccessMethod) {
          return (boolean) AccessController.canAccessMethod.invoke(member, target);
        }
        // noinspection JavaReflectionMemberAccess
        final Method canAccessMethod = AccessibleObject.class.getMethod("canAccess", Object.class);
        AccessController.canAccessMethod = canAccessMethod;
        return (boolean) canAccessMethod.invoke(member, target);
      } catch (final NoSuchMethodException exception) {
        throw new ReflectionSecurityException(
          "Method 'canAccess(Object)' not found in AccessibleObject (expected in Java 9+)",
          exception
        );
      } catch (final ReflectiveOperationException exception) {
        throw new ReflectionSecurityException(
          "Failed to invoke 'canAccess(Object)' on AccessibleObject",
          exception
        );
      }
    }
    return member.isAccessible();
  }

  /**
   * Sets the accessibility flag of the given reflection member.
   * <p>
   * This method handles special cases for different Java versions:
   * </p>
   * <ul>
   *   <li>Java 11 and below: directly calls {@link AccessibleObject#setAccessible(boolean)}</li>
   *   <li>Java 12 and above: uses reflection to set accessibility for package-private members
   *       to handle module system restrictions</li>
   * </ul>
   *
   * @param member the reflection member whose accessibility is to be set
   * @param flag {@code true} to set the member accessible, {@code false} otherwise
   * @throws RuntimeException if setting accessibility for a package-private member on Java 12+ fails due to
   *   reflection restrictions
   */
  public static void setAccessible(
    final AccessibleObject member,
    final boolean flag
  ) {
    if (12 > AccessController.getJavaVersion() || !flag || !AccessController.isPackagePrivate(member)) {
      member.setAccessible(flag);
      return;
    }
    // Special handling for Java 12+ package-private members
    try {
      member.getClass()
        .getDeclaredMethod("setAccessible", boolean.class)
        .invoke(member, true);
    } catch (final ReflectiveOperationException exception) {
      throw new RuntimeException(
        "Cannot make package-private member accessible in Java " + AccessController.javaVersion,
        exception
      );
    }
  }
}
