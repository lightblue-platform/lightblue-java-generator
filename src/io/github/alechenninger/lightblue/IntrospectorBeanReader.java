package io.github.alechenninger.lightblue;

import com.redhat.lightblue.metadata.Version;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class IntrospectorBeanReader implements BeanReader {
  @Override
  public String getEntityName(Class<?> bean) {
    return Introspector.decapitalize(bean.getSimpleName());
  }

  @Override
  public Version getEntityVersion(Class<?> bean) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<BeanField> readBeanFields(Class<?> bean) {
    try {
      BeanInfo info = Introspector.getBeanInfo(bean, Object.class);
      PropertyDescriptor[] properties = info.getPropertyDescriptors();

      return Arrays.stream(properties)
          .map(this::newBeanField)
          .collect(Collectors.toList());
    } catch (IntrospectionException e) {
      throw new BeanReaderException(e);
    }
  }

  private IntrospectorBeanField newBeanField(PropertyDescriptor property) {
    return new IntrospectorBeanField(property, this);
  }
}
