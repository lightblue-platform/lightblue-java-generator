package io.github.alechenninger.lightblue;

import java.lang.reflect.Field;
import java.util.Optional;

public class SimpleEnumValueMirror implements EnumValueMirror {
  private final Field field;
  private final Class enumClass;

  public SimpleEnumValueMirror(Field field, Class enumClass) {
    this.field = field;
    this.enumClass = enumClass;
  }

  @Override
  public String name() {
    return Enum.valueOf(enumClass, field.getName()).toString();
  }

  @Override
  public Optional<String> description() {
    return Optional.ofNullable(field.getAnnotation(Description.class))
        .map(Description::value);
  }
}
