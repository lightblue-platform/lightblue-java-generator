package io.github.alechenninger.lightblue;

public interface Reflector {
  BeanMirror reflect(Class<?> bean);
}
