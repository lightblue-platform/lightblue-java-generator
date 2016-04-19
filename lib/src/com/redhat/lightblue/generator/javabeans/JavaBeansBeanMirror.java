package com.redhat.lightblue.generator.javabeans;

import com.redhat.lightblue.generator.AnnotationVersionMirror;
import com.redhat.lightblue.generator.BeanMirror;
import com.redhat.lightblue.generator.EntityName;
import com.redhat.lightblue.generator.FieldMirror;
import com.redhat.lightblue.generator.MirrorException;
import com.redhat.lightblue.generator.Reflector;
import com.redhat.lightblue.generator.Transient;
import com.redhat.lightblue.generator.VersionMirror;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
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
    return Optional.ofNullable(bean.getAnnotation(EntityName.class))
        .map(EntityName::value)
        .orElse(Introspector.decapitalize(bean.getSimpleName()));
  }

  @Override
  public Optional<VersionMirror> getVersion() {
    AnnotationVersionMirror versionMirror = new AnnotationVersionMirror(bean);

    if (versionMirror.isVersionAnnotationPresent()) {
      return Optional.of(versionMirror);
    }

    return Optional.empty();
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
