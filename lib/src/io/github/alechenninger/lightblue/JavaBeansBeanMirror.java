package io.github.alechenninger.lightblue;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class JavaBeansBeanMirror implements BeanMirror {
  private final Class<?> bean;
  private final Reflector reflector;

  public JavaBeansBeanMirror(Class<?> bean, Reflector reflector) {
    this.bean = bean;
    this.reflector = reflector;
  }

  @Override
  public String getEntityName() {
    return Introspector.decapitalize(bean.getSimpleName());
  }

  @Override
  public VersionMirror getVersion() {
    return new AnnotationVersionMirror(bean);
  }

  @Override
  public Collection<FieldMirror> getFields() {
    try {
      BeanInfo info = Introspector.getBeanInfo(bean, Object.class);
      PropertyDescriptor[] properties = info.getPropertyDescriptors();

      return Arrays.stream(properties)
          .filter(p -> !p.getReadMethod().isAnnotationPresent(Transient.class))
          .map(this::newFieldMirror)
          .collect(Collectors.toList());
    } catch (IntrospectionException e) {
      throw new MirrorException(e);
    }
  }

  private JavaBeansFieldMirror newFieldMirror(PropertyDescriptor property) {
    return new JavaBeansFieldMirror(property, reflector);
  }
}
