package com.redhat.lightblue.generator.javabeans;

import com.redhat.lightblue.generator.BeanMirror;
import com.redhat.lightblue.generator.Reflector;

public class JavaBeansReflector implements Reflector {
  @Override
  public BeanMirror reflect(Class<?> bean) {
    return new JavaBeansBeanMirror(bean, this);
  }
}
