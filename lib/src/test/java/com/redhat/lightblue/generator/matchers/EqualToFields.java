package com.redhat.lightblue.generator.matchers;

import com.google.common.collect.Iterables;
import com.redhat.lightblue.metadata.Field;
import com.redhat.lightblue.metadata.Fields;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EqualToFields extends TypeSafeDiagnosingMatcher<Fields> {
  private final Fields fields;

  public EqualToFields(Fields fields) {
    this.fields = fields;
  }

  @Override
  protected boolean matchesSafely(Fields item, Description mismatchDescription) {
    boolean matches = true;

    if (!Objects.equals(fields.getProperties(), item.getProperties())) {
      matches = false;
      mismatchDescription.appendText("properties were ").appendValue(item.getProperties());
    }

    List<Matcher<? super Field>> matchingAllFields = Arrays
        .stream(Iterables.toArray(fields::getFields, Field.class))
        .map(EqualToField::new)
        .collect(Collectors.toList());

    Matcher matcher = Matchers.containsInAnyOrder(matchingAllFields);
    if (!matcher.matches((Iterable) item::getFields)) {
      matches = false;
      matcher.describeMismatch(item, mismatchDescription);
    }

    return matches;
  }

  @Override
  public void describeTo(Description description) {

  }


}
