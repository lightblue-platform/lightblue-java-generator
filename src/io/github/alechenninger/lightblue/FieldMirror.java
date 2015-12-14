package io.github.alechenninger.lightblue;

import java.util.Collection;
import java.util.Optional;

public interface FieldMirror {
  String name();

  Class<?> javaType();

  Optional<String> description();

  boolean isRequired();

  boolean isIdentifying();

  Optional<Integer> minItems();

  Optional<Integer> minLength();

  Optional<Integer> maxLength();

  Collection<FieldMirror> childBeanFields();

  Optional<Class<?>> elementJavaType();

  /*
  Optional<> valueGeneratorType();
   */
}
