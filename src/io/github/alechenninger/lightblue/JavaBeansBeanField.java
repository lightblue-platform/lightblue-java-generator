package io.github.alechenninger.lightblue;

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Optional;

public class JavaBeansBeanField implements BeanField {
  private final PropertyDescriptor property;
  private final BeanReader beanReader;

  public JavaBeansBeanField(PropertyDescriptor propertyDescriptor, BeanReader beanReader) {
    this.property = propertyDescriptor;
    this.beanReader = beanReader;
  }

  @Override
  public String name() {
    return property.getName();
  }

  @Override
  public Class<?> javaType() {
    return property.getPropertyType();
  }

  @Override
  public Optional<String> description() {
    Description description = property.getReadMethod().getAnnotation(Description.class);
    return Optional.ofNullable(description)
        .map(Description::value);
  }

  @Override
  public boolean isRequired() {
    return property.getReadMethod().isAnnotationPresent(Required.class);
  }

  @Override
  public Optional<Integer> minItems() {
    MinItems minItems = property.getReadMethod().getAnnotation(MinItems.class);
    return Optional.ofNullable(minItems)
        .map(MinItems::value);
  }

  @Override
  public Optional<Integer> minLength() {
    MinLength minLength = property.getReadMethod().getAnnotation(MinLength.class);
    return Optional.ofNullable(minLength)
        .map(MinLength::value);
  }

  @Override
  public Optional<Integer> maxLength() {
    MaxLength maxLength = property.getReadMethod().getAnnotation(MaxLength.class);
    return Optional.ofNullable(maxLength)
        .map(MaxLength::value);
  }

  @Override
  public Collection<BeanField> childBeanFields() {
    return beanReader.readBeanFields(property.getPropertyType());
  }

  @Override
  public Optional<Class<?>> elementJavaType() {
    java.lang.reflect.Type propertyType = property.getReadMethod().getGenericReturnType();

    if (!(propertyType instanceof ParameterizedType)) {
      return Optional.empty();
    }

    ParameterizedType genericPropertyType = (ParameterizedType) propertyType;

    return Optional.of((Class<?>) genericPropertyType.getActualTypeArguments()[0]);
  }
}
