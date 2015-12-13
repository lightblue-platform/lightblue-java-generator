package io.github.alechenninger.lightblue;

import static io.github.alechenninger.lightblue.matchers.GeneratorMatchers.equalToFields;
import static org.hamcrest.MatcherAssert.assertThat;

import com.redhat.lightblue.metadata.ArrayField;
import com.redhat.lightblue.metadata.EntitySchema;
import com.redhat.lightblue.metadata.Fields;
import com.redhat.lightblue.metadata.ObjectArrayElement;
import com.redhat.lightblue.metadata.ObjectField;
import com.redhat.lightblue.metadata.SimpleArrayElement;
import com.redhat.lightblue.metadata.SimpleField;
import com.redhat.lightblue.metadata.types.BooleanType;
import com.redhat.lightblue.metadata.types.DateType;
import com.redhat.lightblue.metadata.types.IntegerType;
import com.redhat.lightblue.metadata.types.StringType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(JUnit4.class)
public class SchemaGeneratorTest {
  SchemaGenerator generator = new SchemaGenerator();

  @Test
  public void shouldGenerateSimpleFieldTypes() {
    class SimpleUser {
      private String name;
      private Integer age;
      private Date birthday;
      private Boolean cool;

      public Boolean getCool() {
        return cool;
      }

      public void setCool(Boolean cool) {
        this.cool = cool;
      }

      public Date getBirthday() {
        return birthday;
      }

      public void setBirthday(Date birthday) {
        this.birthday = birthday;
      }

      public Integer getAge() {
        return age;
      }

      public void setAge(Integer age) {
        this.age = age;
      }

      public String getName() {
        return name;
      }

      public void setName(String name) {
        this.name = name;
      }
    }

    EntitySchema expectedSchema = new EntitySchema("simpleUser");
    Fields expectedFields = expectedSchema.getFields();
    expectedFields.addNew(new SimpleField("age", IntegerType.TYPE));
    expectedFields.addNew(new SimpleField("birthday", DateType.TYPE));
    expectedFields.addNew(new SimpleField("cool", BooleanType.TYPE));
    expectedFields.addNew(new SimpleField("name", StringType.TYPE));

    EntitySchema schema = generator.getSchema(SimpleUser.class);

    assertThat(schema.getFields(), equalToFields(expectedFields));
  }

  @Test
  public void shouldGenerateObjectFieldTypes() {
    class HasInnerObject {
      public InnerObject getInnerObject() {
        return new InnerObject();
      }

      class InnerObject {
        public String getFoo() {
          return "";
        }
      }
    }

    EntitySchema expectedSchema = new EntitySchema("hasInnerObject");
    Fields expectedFields = expectedSchema.getFields();
    ObjectField expectedObjectField = new ObjectField("innerObject");
    expectedObjectField.getFields().addNew(new SimpleField("foo", StringType.TYPE));
    expectedFields.addNew(expectedObjectField);

    EntitySchema schema = generator.getSchema(HasInnerObject.class);

    assertThat(schema.getFields(), equalToFields(expectedFields));
  }

  @Test
  public void shouldGenerateArrayFieldTypesForSimpleArrayElements() {
    class HasArray {
      public List<String> getFoobars() {
        return Arrays.asList("foobar1", "foobar2");
      }
    }

    EntitySchema expectedSchema = new EntitySchema("hasArray");
    ArrayField expectedArrayField = new ArrayField("foobars", new SimpleArrayElement(StringType.TYPE));
    Fields expectedFields = expectedSchema.getFields();
    expectedFields.addNew(expectedArrayField);

    EntitySchema schema = generator.getSchema(HasArray.class);

    assertThat(schema.getFields(), equalToFields(expectedFields));
  }

  @Test
  public void shouldGenerateArrayFieldTypesForObjectArrayElements() {
    class HasObjectArray {
      public List<SomeObject> getSomeObjects() {
        return Arrays.asList(new SomeObject(), new SomeObject());
      }

      class SomeObject {
        public Integer getSomeNumber() {
          return 42;
        }
      }
    }

    EntitySchema expectedSchema = new EntitySchema("hasObjectArray");
    ObjectArrayElement expectedArrayElement = new ObjectArrayElement();
    expectedArrayElement.getFields().addNew(new SimpleField("someNumber", IntegerType.TYPE));
    ArrayField expectedArrayField = new ArrayField("someObjects", expectedArrayElement);
    Fields expectedFields = expectedSchema.getFields();
    expectedFields.addNew(expectedArrayField);

    EntitySchema schema = generator.getSchema(HasObjectArray.class);

    assertThat(schema.getFields(), equalToFields(expectedFields));
  }
}
