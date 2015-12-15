package io.github.alechenninger.lightblue.javabeans;

import io.github.alechenninger.lightblue.BeanMirror;
import io.github.alechenninger.lightblue.Reflector;

public class JavaBeansReflector implements Reflector {
  @Override
  public BeanMirror reflect(Class<?> bean) {
    return new JavaBeansBeanMirror(bean, this);
  }
}
