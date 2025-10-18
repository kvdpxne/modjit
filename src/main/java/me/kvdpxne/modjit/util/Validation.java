package me.kvdpxne.modjit.util;

import java.util.function.Supplier;

/**
 * Provides utility methods for validating arguments and state.
 * <p>
 * This class offers static methods to check common preconditions, such as ensuring an object is not {@code null} or a
 * string is not blank. If a validation fails, an appropriate exception (typically
 * {@link java.lang.IllegalArgumentException}) is thrown.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public final class Validation {

  /**
   * Prevents instantiation of this utility class.
   */
  private Validation() {
    throw new AssertionError();
  }

  /**
   * Checks if the provided string is blank.
   * <p>
   * A string is considered blank if it is empty or contains only whitespace characters. This method handles differences
   * in the {@link java.lang.String#isBlank()} method availability across Java versions. For Java 11 and above, it uses
   * the native {@code isBlank()} method. For earlier versions, it manually checks for whitespace characters.
   * </p>
   *
   * @param str the string to check for blankness
   * @return {@code true} if the string is blank, {@code false} otherwise
   * @throws java.lang.NullPointerException if the provided string is {@code null}
   */
  private static boolean isBlank(
    final String str
  ) {
    int size = 0;
    for (int i = 0; str.length() > i; ++i) {
      if (Character.isWhitespace(str.charAt(i))) {
        ++size;
      }
    }
    return str.length() == size;
  }

  /**
   * Validates that the given condition is {@code true}.
   * <p>
   * If the condition is {@code false}, an {@link java.lang.IllegalArgumentException} is thrown with the message
   * provided by the {@code message} supplier. The message supplier is only invoked if the condition is not met.
   * </p>
   *
   * @param condition the boolean condition to validate
   * @param message a supplier for the error message string. Must not be {@code null}.
   * @throws java.lang.IllegalArgumentException if the condition is {@code false}.
   * @throws java.lang.NullPointerException if the {@code message} supplier is {@code null}.
   */
  public static void require(
    final boolean condition,
    final Supplier<String> message
  ) {
    if (!condition) {
      if (null == message) {
        throw new NullPointerException("The message provider cannot be null");
      }
      String content = message.get();
      if (null == content || Validation.isBlank(content)) {
        content = "The error message has not been described";
      }
      throw new IllegalArgumentException(content);
    }
  }

  /**
   * Validates that the given object is not {@code null}.
   * <p>
   * This is a convenience method equivalent to calling {@link #require(boolean, java.util.function.Supplier)} with
   * {@code null != object}.
   * </p>
   *
   * @param object the object to check for {@code null}
   * @param message a supplier for the error message string. Must not be {@code null}.
   * @throws java.lang.IllegalArgumentException if the object is {@code null}.
   * @throws java.lang.NullPointerException if the {@code message} supplier is {@code null}.
   */
  public static void requireNotNull(
    final Object object,
    final Supplier<String> message
  ) {
    Validation.require(null != object, message);
  }

  /**
   * Validates that the given string is not {@code null} and is not blank.
   * <p>
   * A blank string is one that is empty or contains only whitespace characters. This uses the internal
   * {@link #isBlank(java.lang.String)} method for the check.
   * </p>
   *
   * @param target the string to check for {@code null} and blankness
   * @param message a supplier for the error message string. Must not be {@code null}.
   * @throws java.lang.IllegalArgumentException if the string is {@code null} or blank.
   * @throws java.lang.NullPointerException if the {@code message} supplier is {@code null}.
   */
  public static void requireNotBlank(
    final String target,
    final Supplier<String> message
  ) {
    Validation.require(null != target && !Validation.isBlank(target), message);
  }
}
