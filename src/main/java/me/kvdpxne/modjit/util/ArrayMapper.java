package me.kvdpxne.modjit.util;

import java.util.function.Function;

/**
 * Provides utility methods for transforming arrays.
 * <p>
 * This class offers static methods to map the elements of an array from one type to another using a provided mapping
 * function.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public final class ArrayMapper {

  /**
   * Prevents instantiation of this utility class.
   */
  private ArrayMapper() {
    throw new AssertionError();
  }

  /**
   * Maps each element of the source array to a new array of strings using the provided mapper function.
   * <p>
   * The resulting array will have the same length as the source array. Each element {@code source[i]} is transformed by
   * applying the {@code mapper} function, resulting in {@code result[i] = mapper.apply(source[i])}.
   * </p>
   *
   * @param <T> The type of elements in the source array.
   * @param source The source array to map. Must not be {@code null}.
   * @param mapper The function to apply to each element of the source array. Must not be {@code null}.
   * @return A new array of strings, where each element is the result of applying the {@code mapper} function to the
   *   corresponding element in the source array.
   * @throws java.lang.NullPointerException if {@code source} or {@code mapper} is {@code null}.
   */
  public static <T> String[] mapToString(
    final T[] source,
    final Function<T, String> mapper
  ) {
    final int size = source.length;
    final String[] result = new String[size];
    for (int i = 0; size > i; ++i) {
      result[i] = mapper.apply(source[i]);
    }
    return result;
  }
}
