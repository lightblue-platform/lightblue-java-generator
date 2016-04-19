package com.redhat.lightblue.generator.javabeans;

import com.redhat.lightblue.generator.AnnotationCurrentTimeMirror;
import com.redhat.lightblue.generator.AnnotationIntSequenceMirror;
import com.redhat.lightblue.generator.AnnotationUuidMirror;
import com.redhat.lightblue.generator.CurrentTime;
import com.redhat.lightblue.generator.Description;
import com.redhat.lightblue.generator.ElementIdentity;
import com.redhat.lightblue.generator.EnumMirror;
import com.redhat.lightblue.generator.FieldMirror;
import com.redhat.lightblue.generator.Identity;
import com.redhat.lightblue.generator.IntSequence;
import com.redhat.lightblue.generator.MaxItems;
import com.redhat.lightblue.generator.MaxLength;
import com.redhat.lightblue.generator.MinItems;
import com.redhat.lightblue.generator.MinLength;
import com.redhat.lightblue.generator.Reflector;
import com.redhat.lightblue.generator.Required;
import com.redhat.lightblue.generator.Uuid;
import com.redhat.lightblue.generator.ValueGeneratorMirror;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
  public boolean isElementIdentifying() {
    return property.getWriteMethod().isAnnotationPresent(ElementIdentity.class) ||
        property.getReadMethod().isAnnotationPresent(ElementIdentity.class);
  }

  @Override
  public Optional<Integer> minItems() {
    MinItems minItems = property.getWriteMethod().getAnnotation(MinItems.class);
    return Optional.ofNullable(minItems)
        .map(MinItems::value);
  }

  @Override
  public Optional<Integer> maxItems() {
    MaxItems maxItems = property.getWriteMethod().getAnnotation(MaxItems.class);
    return Optional.ofNullable(maxItems)
        .map(MaxItems::value);
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
    if (!javaType().isEnum()) {
      return Optional.empty();
    }

    return Optional.of(new JavaBeansEnumMirror(javaType()));
  }

  @Override
  public Optional<ValueGeneratorMirror> valueGeneratorMirror() {
    Method writeMethod = property.getWriteMethod();

    Uuid uuid = writeMethod.getAnnotation(Uuid.class);

    if (uuid != null) {
      return Optional.of(new AnnotationUuidMirror(uuid));
    }

    IntSequence intSequence = writeMethod.getAnnotation(IntSequence.class);

    if (intSequence != null) {
      return Optional.of(new AnnotationIntSequenceMirror(intSequence));
    }

    CurrentTime currentTime = writeMethod.getAnnotation(CurrentTime.class);

    if (currentTime != null) {
      return Optional.of(new AnnotationCurrentTimeMirror(currentTime));
    }

    return Optional.empty();
  }
}
