package me.kvdpxne.reflection.util;

import java.util.function.Function;

public final class ArrayMapper {

  public static <T> String[] mapToString(
    final T[] source,
    final Function<T, String> mapper
  ) {
    final int size = source.length;
    final String[] result = new String[size];
    for (int i = 0; i < size; i++) {
      result[i] = mapper.apply(source[i]);
    }
    return result;
  }
}
