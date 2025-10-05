package me.kvdpxne.reflection.util;

import java.util.function.Supplier;

public final class Validation {

  private Validation() {
    throw new AssertionError();
  }

  /**
   * @since 0.1.0
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
      if (null == content || content.isBlank()) {
        content = "The error message has not been described";
      }
      throw new IllegalArgumentException(content);
    }
  }

  public static void requireNotNull(
    final Object object,
    final Supplier<String> message
  ) {
    require(null != object, message);
  }

  public static void requireNotBlank(
    final String target,
    final Supplier<String> message
  ) {
    require(null != target && !target.isBlank(), message);
  }
}
