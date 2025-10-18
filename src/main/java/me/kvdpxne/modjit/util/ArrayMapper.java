package me.kvdpxne.modjit.util;

import java.util.function.Function;

/**
 * Provides utility methods for transforming arrays by mapping elements from one type to another.
 * <p>
 * This class offers static methods to map the elements of an array from one type to another using a provided mapping
 * function. The resulting array maintains the same length and order as the source array.
 * </p>
 * <p>
 * This utility class is designed for operations where array transformation is needed without modifying the original
 * array structure. It is particularly useful for converting arrays of objects to arrays of strings or other types.
 * </p>
 *
 * @author Łukasz Pietrzak (kvdpxne)
 * @version 0.1.0
 * @since 0.1.0
 */
public final class ArrayMapper {

  /**
   * Prevents instantiation of this utility class.
   *
   * @throws AssertionError always thrown when constructor is invoked
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
   * <p>
   * This method is useful for converting arrays of objects to string representations, such as when generating
   * diagnostic output or preparing data for serialization.
   * </p>
   * <p>
   * Example usage:
   * <pre>{@code
   * Integer[] numbers = {1, 2, 3};
   * String[] strings = ArrayMapper.mapToString(numbers, Object::toString);
   * // Result: ["1", "2", "3"]
   * }</pre>
   * </p>
   *
   * @param <T> the type of elements in the source array
   * @param source the source array to map; must not be {@code null}
   * @param mapper the function to apply to each element of the source array; must not be {@code null}
   * @return a new array of strings where each element is the result of applying the {@code mapper} function to the
   *   corresponding element in the source array
   * @throws NullPointerException if {@code source} or {@code mapper} is {@code null}
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
