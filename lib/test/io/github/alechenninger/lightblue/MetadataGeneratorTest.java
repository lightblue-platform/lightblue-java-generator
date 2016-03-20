package io.github.alechenninger.lightblue;

import static io.github.alechenninger.lightblue.matchers.GeneratorMatchers.equalToFields;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.lightblue.metadata.ArrayField;
import com.redhat.lightblue.metadata.DataStore;
import com.redhat.lightblue.metadata.EntityMetadata;
import com.redhat.lightblue.metadata.EntitySchema;
import com.redhat.lightblue.metadata.Fields;
import com.redhat.lightblue.metadata.Hook;
import com.redhat.lightblue.metadata.ObjectArrayElement;
import com.redhat.lightblue.metadata.ObjectField;
import com.redhat.lightblue.metadata.SimpleArrayElement;
import com.redhat.lightblue.metadata.SimpleField;
import com.redhat.lightblue.metadata.types.BooleanType;
import com.redhat.lightblue.metadata.types.DateType;
import com.redhat.lightblue.metadata.types.IntegerType;
import com.redhat.lightblue.metadata.types.StringType;
import io.github.alechenninger.lightblue.javabeans.JavaBeansReflector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RunWith(JUnit4.class)
public class MetadataGeneratorTest {
  MetadataGenerator generator = new MetadataGenerator(new JavaBeansReflector());

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

    EntitySchema schema = generator.generateSchema(SimpleUser.class);

    assertThat(schema.getFields(), equalToFields(expectedFields));
  }

  @Test
  public void shouldGenerateObjectFieldTypes() {
    class HasInnerObject {
      public InnerObject getInnerObject() {
        return new InnerObject();
      }

      public void setInnerObject(InnerObject innerObject) {

      }

      class InnerObject {
        public String getFoo() {
          return "foo";
        }

        public void setFoo(String foo) {
        }
      }
    }

    EntitySchema expectedSchema = new EntitySchema("hasInnerObject");
    Fields expectedFields = expectedSchema.getFields();
    ObjectField expectedObjectField = new ObjectField("innerObject");
    expectedObjectField.getFields().addNew(new SimpleField("foo", StringType.TYPE));
    expectedFields.addNew(expectedObjectField);

    EntitySchema schema = generator.generateSchema(HasInnerObject.class);

    assertThat(schema.getFields(), equalToFields(expectedFields));
  }

  @Test
  public void shouldGenerateArrayFieldTypesForSimpleArrayElements() {
    class HasArray {
      public List<String> getFoobars() {
        return Arrays.asList("foo", "bars");
      }

      public void setFoobars(List<String> foobars) {

      }
    }

    EntitySchema expectedSchema = new EntitySchema("hasArray");
    ArrayField expectedArrayField = new ArrayField("foobars", new SimpleArrayElement(StringType.TYPE));
    Fields expectedFields = expectedSchema.getFields();
    expectedFields.addNew(expectedArrayField);

    EntitySchema schema = generator.generateSchema(HasArray.class);

    assertThat(schema.getFields(), equalToFields(expectedFields));
  }

  @Test
  public void shouldGenerateArrayFieldTypesForObjectArrayElements() {
    class HasObjectArray {
      public List<SomeObject> getSomeObjects() {
        return Arrays.asList(new SomeObject(), new SomeObject());
      }

      public void setSomeObjects(List<SomeObject> someObjects) {

      }

      class SomeObject {
        public Integer getSomeNumber() {
          return 42;
        }

        public void setSomeNumber(Integer someNumber) {

        }
      }
    }

    EntitySchema expectedSchema = new EntitySchema("hasObjectArray");
    ObjectArrayElement expectedArrayElement = new ObjectArrayElement();
    expectedArrayElement.getFields().addNew(new SimpleField("someNumber", IntegerType.TYPE));
    ArrayField expectedArrayField = new ArrayField("someObjects", expectedArrayElement);
    Fields expectedFields = expectedSchema.getFields();
    expectedFields.addNew(expectedArrayField);

    EntitySchema schema = generator.generateSchema(HasObjectArray.class);

    assertThat(schema.getFields(), equalToFields(expectedFields));
  }

  @Test
  public void respectsEntityNameAnnotation() {
    @EntityName("overriddenEntityName")
    class NotTheEntityName {

    }

    EntityMetadata metadata = generator.generateMetadata(NotTheEntityName.class);

    assertEquals("overriddenEntityName", metadata.getName());
    assertEquals("overriddenEntityName", metadata.getEntitySchema().getName());
  }

  @Test
  public void updatesExistingMetadata() {
    class User {
      private String _id;
      private String firstName;

      public String get_id() {
        return _id;
      }

      public void set_id(String _id) {
        this._id = _id;
      }

      public String getFirstName() {
        return firstName;
      }

      public void setFirstName(String firstName) {
        this.firstName = firstName;
      }
    }

    @EntityName("user")
    class User2 extends User {
      private String lastName;

      public String getLastName() {
        return lastName;
      }

      public void setLastName(String lastName) {
        this.lastName = lastName;
      }
    }

    EntityMetadata existing = generator.generateMetadata(User.class);
    TestDataStore existingDataStore = new TestDataStore();
    existing.setDataStore(existingDataStore);
    List<Hook> existingHooks = Collections.singletonList(new Hook("testHook"));
    existing.getHooks().setHooks(existingHooks);
    existing.getAccess().getDelete().setRoles("deleter");
    existing.getAccess().getFind().setRoles("finder");
    existing.getAccess().getInsert().setRoles("inserter");
    existing.getAccess().getUpdate().setRoles("updater");
    existing.getFields().getField("firstName").getAccess().getUpdate().setRoles("first-name-updater");
  }

  static class TestDataStore implements DataStore {

    @Override
    public String getBackend() {
      return "test";
    }
  }
}
