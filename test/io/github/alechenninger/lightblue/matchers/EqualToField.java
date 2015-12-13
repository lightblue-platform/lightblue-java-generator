package io.github.alechenninger.lightblue.matchers;


import com.redhat.lightblue.metadata.ArrayElement;
import com.redhat.lightblue.metadata.ArrayField;
import com.redhat.lightblue.metadata.Field;
import com.redhat.lightblue.metadata.Fields;
import com.redhat.lightblue.metadata.ObjectArrayElement;
import com.redhat.lightblue.metadata.ObjectField;
import com.redhat.lightblue.metadata.SimpleArrayElement;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.Objects;

public class EqualToField extends TypeSafeDiagnosingMatcher<Field> {
  private final Field field;

  public EqualToField(Field field) {
    this.field = field;
  }

  @Override
  protected boolean matchesSafely(Field item, Description mismatchDescription) {
    boolean matches = true;

    if (!Objects.equals(field.getName(), item.getName())) {
      matches = false;
      mismatchDescription.appendText("field name was ").appendValue(item.getName());
    }

    if (!Objects.equals(field.getType(), item.getType())) {
      matches = false;
      mismatchDescription.appendValue("type was ").appendValue(item.getType());
    }

    if (field instanceof ObjectField) {
      ObjectField objectField = (ObjectField) field;

      EqualToFields equalToFields = new EqualToFields(objectField.getFields());
      if (!equalToFields.matchesSafely(((ObjectField) item).getFields(), mismatchDescription)) {
        matches = false;
      }
    }

    if (field instanceof ArrayField) {
      ArrayField arrayField = (ArrayField) field;
      ArrayField arrayItem = (ArrayField) item;

      ArrayElement element = arrayField.getElement();
      ArrayElement itemElement = arrayItem.getElement();

      if (!Objects.equals(element.getType(), itemElement.getType())) {
        matches = false;
      }

      if (element instanceof ObjectArrayElement) {
        Fields elementFields = ((ObjectArrayElement) element).getFields();
        Fields itemFields = ((ObjectArrayElement) itemElement).getFields();

        if (!new EqualToFields(elementFields).matchesSafely(itemFields, mismatchDescription)) {
          matches = false;
        }
      }
    }

    // TODO: Check other stuff about fields

    return matches;
  }

  @Override
  public void describeTo(Description description) {

  }
}
