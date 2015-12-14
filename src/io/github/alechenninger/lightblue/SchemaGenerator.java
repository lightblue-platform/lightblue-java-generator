package io.github.alechenninger.lightblue;

import com.redhat.lightblue.metadata.ArrayField;
import com.redhat.lightblue.metadata.EntitySchema;
import com.redhat.lightblue.metadata.Field;
import com.redhat.lightblue.metadata.FieldConstraint;
import com.redhat.lightblue.metadata.Fields;
import com.redhat.lightblue.metadata.ObjectArrayElement;
import com.redhat.lightblue.metadata.ObjectField;
import com.redhat.lightblue.metadata.SimpleArrayElement;
import com.redhat.lightblue.metadata.SimpleField;
import com.redhat.lightblue.metadata.Type;
import com.redhat.lightblue.metadata.constraints.IdentityConstraint;
import com.redhat.lightblue.metadata.constraints.MinMaxConstraint;
import com.redhat.lightblue.metadata.constraints.RequiredConstraint;
import com.redhat.lightblue.metadata.constraints.StringLengthConstraint;
import com.redhat.lightblue.metadata.types.ArrayType;
import com.redhat.lightblue.metadata.types.BigDecimalType;
import com.redhat.lightblue.metadata.types.BigIntegerType;
import com.redhat.lightblue.metadata.types.BinaryType;
import com.redhat.lightblue.metadata.types.BooleanType;
import com.redhat.lightblue.metadata.types.DateType;
import com.redhat.lightblue.metadata.types.DoubleType;
import com.redhat.lightblue.metadata.types.IntegerType;
import com.redhat.lightblue.metadata.types.ObjectType;
import com.redhat.lightblue.metadata.types.StringType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class SchemaGenerator {
  private final BeanReader beanReader;

  public SchemaGenerator(BeanReader beanReader) {
    this.beanReader = beanReader;
  }

  public EntitySchema getSchema(Class<?> entity) {
      EntitySchema schema = new EntitySchema(beanReader.getEntityName(entity));

      addLightblueFieldsForClass(entity, schema.getFields());

      return schema;
  }

  private void addLightblueFieldsForClass(Class<?> type, Fields fields) {
    for (BeanField beanField : beanReader.readBeanFields(type)) {
      Field field = getLightblueFieldForBeanField(beanField);
      field.setConstraints(getConstraintsForBeanField(beanField));
      beanField.description().ifPresent(d -> field.getProperties().put("description", d));

      fields.addNew(field);
    }
  }

  private Field getLightblueFieldForBeanField(BeanField beanField) {
    Class<?> javaType = beanField.javaType();

    Type type = getTypeForClass(javaType);

    if (isSimpleFieldType(type)) {
      return new SimpleField(beanField.name(), type);
    }

    if (ArrayType.TYPE.equals(type)) {
      Class<?> elementJavaType = beanField.elementJavaType().get();
      Type arrayElementType = getTypeForClass(elementJavaType);

      if (isSimpleFieldType(arrayElementType)) {
        return new ArrayField(beanField.name(), new SimpleArrayElement(arrayElementType));
      }

      if (ObjectType.TYPE.equals(arrayElementType)) {
        ObjectArrayElement arrayElement = new ObjectArrayElement();
        addLightblueFieldsForClass(elementJavaType, arrayElement.getFields());
        return new ArrayField(beanField.name(), arrayElement);
      }

      throw new UnsupportedOperationException("Unsupported array element type: " + arrayElementType);
    }

    ObjectField objectField = new ObjectField(beanField.name());
    addLightblueFieldsForClass(javaType, objectField.getFields());

    return objectField;
  }

  private Collection<FieldConstraint> getConstraintsForBeanField(BeanField beanField) {
    List<FieldConstraint> constraints = new ArrayList<>();

    if (beanField.isRequired()) {
      constraints.add(new RequiredConstraint());
    }

    beanField.minItems().ifPresent(i -> {
      MinMaxConstraint constraint = new MinMaxConstraint(MinMaxConstraint.MIN);
      constraint.setValue(i);
      constraints.add(constraint);
    });

    beanField.minLength().ifPresent(l ->
        constraints.add(new StringLengthConstraint(StringLengthConstraint.MINLENGTH, l)));

    beanField.maxLength().ifPresent(l ->
        constraints.add(new StringLengthConstraint(StringLengthConstraint.MAXLENGTH, l)));

    if (beanField.isIdentifying()) {
      constraints.add(new IdentityConstraint());
    }

    return constraints;
  }

  private Type getTypeForClass(Class<?> type) {
    if (type.equals(String.class)) {
      return StringType.TYPE;
    }

    if (type.equals(boolean.class) || type.equals(Boolean.class)) {
      return BooleanType.TYPE;
    }

    if (type.equals(Date.class) || type.equals(Instant.class)) {
      return DateType.TYPE;
    }

    if (type.equals(BigDecimal.class)) {
      return BigDecimalType.TYPE;
    }

    if (type.equals(BigInteger.class)) {
      return BigIntegerType.TYPE;
    }

    if (type.equals(byte[].class)) {
      return BinaryType.TYPE;
    }

    if (type.equals(double.class) || type.equals(Double.class)) {
      return DoubleType.TYPE;
    }

    if (type.equals(int.class) || type.equals(Integer.class)) {
      return IntegerType.TYPE;
    }

    if (Iterable.class.isAssignableFrom(type)) {
      return ArrayType.TYPE;
    }

    // TODO: Reference type, UUID type

    return ObjectType.TYPE;
  }

  private static boolean isSimpleFieldType(Type type) {
    return !(type.equals(ObjectType.TYPE) || type.equals(ArrayType.TYPE));
  }
}
