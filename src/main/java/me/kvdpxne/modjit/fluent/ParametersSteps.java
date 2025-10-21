package me.kvdpxne.modjit.fluent;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ParametersSteps<Q> {

  Q withParameterTypes(Collection<Class<?>> parameterTypes);

  default Q withParameterTypes(Class<?>[] parameterTypes) {
    return this.withParameterTypes(Arrays.asList(parameterTypes));
  }

  Q withParameterType(Class<?> parameterType);

  default Q withVoidType() {
    return this.withParameterType(void.class);
  }

  default Q withBooleanType() {
    return this.withParameterType(boolean.class);
  }

  default Q withBooleanArrayType() {
    return this.withParameterType(boolean[].class);
  }

  default Q withByteType() {
    return this.withParameterType(byte.class);
  }

  default Q withByteArrayType() {
    return this.withParameterType(byte[].class);
  }

  default Q withShortType() {
    return this.withParameterType(short.class);
  }

  default Q withShortArrayType() {
    return this.withParameterType(short[].class);
  }

  default Q withIntType() {
    return this.withParameterType(int.class);
  }

  default Q withIntArrayType() {
    return this.withParameterType(int[].class);
  }

  default Q withLongType() {
    return this.withParameterType(long.class);
  }

  default Q withLongArrayType() {
    return this.withParameterType(long[].class);
  }

  default Q withFloatType() {
    return this.withParameterType(float.class);
  }

  default Q withFloatArrayType() {
    return this.withParameterType(float[].class);
  }

  default Q withDoubleType() {
    return this.withParameterType(double.class);
  }

  default Q withDoubleArrayType() {
    return this.withParameterType(double[].class);
  }

  default Q withCharType() {
    return this.withParameterType(char.class);
  }

  default Q withCharArrayType() {
    return this.withParameterType(char[].class);
  }

  default Q withStringType() {
    return this.withParameterType(String.class);
  }

  default Q withStringArrayType() {
    return this.withParameterType(String[].class);
  }

  default Q withObjectType() {
    return this.withParameterType(Object.class);
  }

  default Q withCollectionType() {
    return this.withParameterType(Collection.class);
  }

  default Q withListType() {
    return this.withParameterType(List.class);
  }

  default Q withMapType() {
    return this.withParameterType(Map.class);
  }

  default Q withSetType() {
    return this.withParameterType(Set.class);
  }
}
