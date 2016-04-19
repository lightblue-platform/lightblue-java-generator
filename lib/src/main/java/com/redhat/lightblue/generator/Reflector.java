package com.redhat.lightblue.generator;

public interface Reflector {
  BeanMirror reflect(Class<?> bean);
}
