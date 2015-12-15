package io.github.alechenninger.lightblue;

import java.lang.reflect.Field;
import java.util.Optional;

public class SimpleEnumValueMirror implements EnumValueMirror {
  private final Field field;

  public SimpleEnumValueMirror(Field field) {
    this.field = field;
  }

  @Override
  public String name() {
    return field.getName();
  }

  @Override
  public Optional<String> description() {
    return Optional.ofNullable(field.getAnnotation(Description.class))
        .map(Description::value);
  }
}
