package com.redhat.lightblue.generator.matchers;

import com.redhat.lightblue.metadata.Field;
import com.redhat.lightblue.metadata.Fields;
import org.hamcrest.Matcher;


public class GeneratorMatchers {
  public static Matcher<? super Field> equalToField(Field field) {
    return new EqualToField(field);
  }

  public static Matcher<? super Fields> equalToFields(Fields fields) {
    return new EqualToFields(fields);
  }
}
