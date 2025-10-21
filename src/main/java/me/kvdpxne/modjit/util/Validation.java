package me.kvdpxne.modjit.util;

import java.util.function.Supplier;

/**
 * Provides utility methods for validating arguments and state conditions.
 * <p>
 * This class offers static methods to check common preconditions, such as ensuring an object is not {@code null} or a
 * string is not blank. If a validation fails, an appropriate exception (typically {@link IllegalArgumentException}) is
 * thrown with a descriptive message.
 * </p>
 * <p>
 * All validation methods use lazy message evaluation through {@link Supplier} to avoid unnecessary string construction
 * when the validation condition passes.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public final class Validation {

  /**
   * Prevents instantiation of this utility class.
   *
   * @throws AssertionError always thrown when constructor is invoked
   */
  private Validation() {
    throw new AssertionError();
  }

  /**
   * Checks if the provided string is blank.
   * <p>
   * A string is considered blank if it is empty or contains only whitespace characters. This method handles differences
   * in the {@link String#isBlank()} method availability across Java versions. For Java 11 and above, it would use the
   * native {@code isBlank()} method, but this implementation manually checks for whitespace characters to maintain Java
   * 8 compatibility.
   * </p>
   * <p>
   * This method performs a character-by-character check using {@link Character#isWhitespace(char)} to determine if all
   * characters in the string are whitespace.
   * </p>
   *
   * @param str the string to check for blankness; must not be {@code null}
   * @return {@code true} if the string is blank (empty or all whitespace), {@code false} otherwise
   * @throws NullPointerException if the provided string is {@code null}
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

  private static String getProvidedMessageOrDefault(
    final Supplier<String> messageProvider
  ) {
    if (null == messageProvider) {
      throw new NullPointerException("The message provider cannot be null.");
    }
    String message = messageProvider.get();
    if (null == message || Validation.isBlank(message)) {
      message = "The error message has not been described";
    }
    return message;
  }

  /**
   * Validates that the given condition is {@code true}.
   * <p>
   * If the condition is {@code false}, an {@link IllegalArgumentException} is thrown with the message provided by the
   * {@code message} supplier. The message supplier is only invoked if the condition is not met, providing efficient
   * error message generation.
   * </p>
   * <p>
   * Example usage:
   * <pre>{@code
   * Validation.require(index >= 0, () -> "Index must be non-negative: " + index);
   * }</pre>
   * </p>
   *
   * @param condition the boolean condition to validate
   * @param message a supplier for the error message string; must not be {@code null}
   * @throws IllegalArgumentException if the condition is {@code false}
   * @throws NullPointerException if the {@code message} supplier is {@code null}
   */
  public static void require(
    final boolean condition,
    final Supplier<String> message
  ) {
    if (!condition) {
      throw new IllegalArgumentException(
        Validation.getProvidedMessageOrDefault(message)
      );
    }
  }

  /**
   * Validates that the given object is not {@code null}.
   * <p>
   * This is a convenience method equivalent to calling {@link #require(boolean, Supplier)} with {@code null != object}.
   * If the object is {@code null}, an {@link IllegalArgumentException} is thrown with the message provided by the
   * {@code message} supplier.
   * </p>
   * <p>
   * Example usage:
   * <pre>{@code
   * Validation.requireNotNull(user, () -> "User object cannot be null");
   * }</pre>
   * </p>
   *
   * @param object the object to check for {@code null}
   * @param message a supplier for the error message string; must not be {@code null}
   * @throws IllegalArgumentException if the object is {@code null}
   * @throws NullPointerException if the {@code message} supplier is {@code null}
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
   * {@link #isBlank(String)} method for the check. If the string is {@code null} or blank, an
   * {@link IllegalArgumentException} is thrown with the message provided by the {@code message} supplier.
   * </p>
   * <p>
   * Example usage:
   * <pre>{@code
   * Validation.requireNotBlank(username, () -> "Username cannot be null or blank");
   * }</pre>
   * </p>
   *
   * @param target the string to check for {@code null} and blankness
   * @param message a supplier for the error message string; must not be {@code null}
   * @throws IllegalArgumentException if the string is {@code null} or blank
   * @throws NullPointerException if the {@code message} supplier is {@code null}
   */
  public static void requireNotBlank(
    final String target,
    final Supplier<String> message
  ) {
    Validation.require(null != target && !Validation.isBlank(target), message);
  }

  public static void check(
    final boolean condition,
    final Supplier<String> message
  ) {
    if (!condition) {
      throw new IllegalStateException(
        Validation.getProvidedMessageOrDefault(message)
      );
    }
  }

  public static void checkNotNull(
    final Object object,
    final Supplier<String> message
  ) {
    Validation.check(null != object, message);
  }
}
