package com.redhat.lightblue.generator.javabeans;

import com.redhat.lightblue.generator.EnumMirror;
import com.redhat.lightblue.generator.EnumValueMirror;
import com.redhat.lightblue.generator.SimpleEnumValueMirror;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class JavaBeansEnumMirror implements EnumMirror {
  private final Class<?> enumClass;

  public JavaBeansEnumMirror(Class<?> enumClass) {
    if (!enumClass.isEnum()) {
      throw new IllegalArgumentException("Expected enum type but got: " + enumClass);
    }

    this.enumClass = enumClass;
  }

  @Override
  public String name() {
    return Introspector.decapitalize(enumClass.getSimpleName());
  }

  @Override
  public Set<EnumValueMirror> values() {
    Field[] fields = enumClass.getFields();
    Set<EnumValueMirror> values = new HashSet<>(fields.length);

    for (Field field : fields) {
      values.add(new SimpleEnumValueMirror(field, enumClass));
    }

    return values;
  }
}
