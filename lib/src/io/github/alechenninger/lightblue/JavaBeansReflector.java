package io.github.alechenninger.lightblue;

public class JavaBeansReflector implements Reflector {
  @Override
  public BeanMirror reflect(Class<?> bean) {
    return new JavaBeansBeanMirror(bean, this);
  }
}
