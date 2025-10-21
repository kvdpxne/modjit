package me.kvdpxne.modjit.fluent;

public interface ClassSteps<Q> {

  Q withPath(String path);

  Q withClassLoader(ClassLoader classloader);

  Q withInitialize(boolean initialize);
}
