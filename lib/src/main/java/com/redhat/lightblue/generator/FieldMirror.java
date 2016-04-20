package com.redhat.lightblue.generator;

import java.util.Collection;
import java.util.Optional;

public interface FieldMirror {
  String name();

  Class<?> javaType();

  Optional<String> description();

  boolean isRequired();

  boolean isIdentifying();

  boolean isElementIdentifying();

  Optional<Integer> minItems();

  Optional<Integer> maxItems();

  Optional<Integer> minLength();

  Optional<Integer> maxLength();

  Collection<FieldMirror> objectFields();

  Optional<Class<?>> elementJavaType();

  Optional<EnumMirror> enumMirror();

  Optional<ValueGeneratorMirror> valueGeneratorMirror();
}
