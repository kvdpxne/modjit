package me.kvdpxne.reflection.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class AccessController {

  private static final int JAVA_VERSION = getJavaVersion();

  private static int getJavaVersion() {
    final String version = System.getProperty("java.version");
    if (null == version) {
      throw new IllegalStateException("Unable to determine Java version: system property 'java.version' is null");
    }
    final String[] parts = version.split("[._]");
    for (final String part : parts) {
      try {
        int number = Integer.parseInt(part.trim());
        if (number != 0) {
          return number;
        }
      } catch (final NumberFormatException ignored) {
      }
    }
    throw new IllegalStateException("Unable to parse Java version from string: " + version);
  }

  private static boolean isPackagePrivate(
    final AccessibleObject member
  ) {
    final int modifiers;
    if (member instanceof Field) {
      modifiers = ((Field) member).getModifiers();
    } else if (member instanceof Method) {
      modifiers = ((Method) member).getModifiers();
    } else {
      modifiers = 0;
    }
    // Check that none of PUBLIC, PROTECTED, or PRIVATE bits are set
    return 0 == (modifiers & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE));
  }

  public static boolean isAccessible(
    final AccessibleObject member,
    final Object target
  ) {
    if (9 <= JAVA_VERSION) {
      return member.canAccess(target);
    }
    // noinspection deprecation
    return member.isAccessible();
  }

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
    }
    member.setAccessible(flag);
  }
}
