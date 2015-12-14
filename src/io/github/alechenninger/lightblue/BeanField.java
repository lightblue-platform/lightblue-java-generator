package io.github.alechenninger.lightblue;

import java.util.Collection;
import java.util.Optional;

public interface BeanField {
  String name();

  Class<?> javaType();

  Optional<String> description();

  boolean isRequired();

  boolean isIdentifying();

  Optional<Integer> minItems();

  Optional<Integer> minLength();

  Optional<Integer> maxLength();

  Collection<BeanField> childBeanFields();

  Optional<Class<?>> elementJavaType();

  /*
  Optional<> valueGeneratorType();
   */
}
