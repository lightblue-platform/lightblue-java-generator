package io.github.alechenninger.lightblue.javabeans;

import io.github.alechenninger.lightblue.Description;
import io.github.alechenninger.lightblue.EnumMirror;
import io.github.alechenninger.lightblue.FieldMirror;
import io.github.alechenninger.lightblue.Identity;
import io.github.alechenninger.lightblue.MaxLength;
import io.github.alechenninger.lightblue.MinItems;
import io.github.alechenninger.lightblue.MinLength;
import io.github.alechenninger.lightblue.Reflector;
import io.github.alechenninger.lightblue.Required;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class JavaBeansFieldMirror implements FieldMirror {
  private final PropertyDescriptor property;
  private final Reflector reflector;

  public JavaBeansFieldMirror(PropertyDescriptor propertyDescriptor, Reflector reflector) {
    this.property = propertyDescriptor;
    this.reflector = reflector;
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
    Description description = property.getWriteMethod().getAnnotation(Description.class);
    return Optional.ofNullable(description)
        .map(Description::value);
  }

  @Override
  public boolean isRequired() {
    return property.getWriteMethod().isAnnotationPresent(Required.class);
  }

  @Override
  public boolean isIdentifying() {
    return property.getWriteMethod().isAnnotationPresent(Identity.class) ||
        property.getReadMethod().isAnnotationPresent(Identity.class);
  }

  @Override
  public Optional<Integer> minItems() {
    MinItems minItems = property.getWriteMethod().getAnnotation(MinItems.class);
    return Optional.ofNullable(minItems)
        .map(MinItems::value);
  }

  @Override
  public Optional<Integer> minLength() {
    MinLength minLength = property.getWriteMethod().getAnnotation(MinLength.class);
    return Optional.ofNullable(minLength)
        .map(MinLength::value);
  }

  @Override
  public Optional<Integer> maxLength() {
    MaxLength maxLength = property.getWriteMethod().getAnnotation(MaxLength.class);
    return Optional.ofNullable(maxLength)
        .map(MaxLength::value);
  }

  @Override
  public Collection<FieldMirror> objectFields() {
    return reflector.reflect(property.getPropertyType()).getFields();
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

  @Override
  public Optional<EnumMirror> enumMirror() {
    return null;
  }
}
