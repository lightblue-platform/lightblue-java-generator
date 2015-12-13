package io.github.alechenninger.lightblue;

import com.redhat.lightblue.metadata.ArrayField;
import com.redhat.lightblue.metadata.EntitySchema;
import com.redhat.lightblue.metadata.Field;
import com.redhat.lightblue.metadata.Fields;
import com.redhat.lightblue.metadata.ObjectArrayElement;
import com.redhat.lightblue.metadata.ObjectField;
import com.redhat.lightblue.metadata.SimpleArrayElement;
import com.redhat.lightblue.metadata.SimpleField;
import com.redhat.lightblue.metadata.Type;
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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Date;

public class SchemaGenerator {
  public EntitySchema getSchema(Class<?> entity) {
    try {
      EntitySchema schema = new EntitySchema(getEntityName(entity));

      addFieldsForClass(entity, schema.getFields());

      return schema;
    } catch (IntrospectionException e) {
      throw new SchemaGenerationException(e);
    }
  }

  private void addFieldsForClass(Class<?> type, Fields fields) throws IntrospectionException {
    BeanInfo beanInfo = Introspector.getBeanInfo(type, Object.class);

    for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
      fields.addNew(getFieldForProperty(property));
    }
  }

  private Field getFieldForProperty(PropertyDescriptor property) throws IntrospectionException {
    Class<?> propertyType = property.getPropertyType();

    Type type = getTypeForClass(propertyType);

    if (isSimpleFieldType(type)) {
      return new SimpleField(property.getName(), type);
    }

    if (ArrayType.TYPE.equals(type)) {
      ParameterizedType parameterizedType = (ParameterizedType)
          property.getReadMethod().getGenericReturnType();
      Class arrayElementClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
      Type arrayElementType = getTypeForClass(arrayElementClass);

      if (isSimpleFieldType(arrayElementType)) {
        return new ArrayField(property.getName(), new SimpleArrayElement(arrayElementType));
      }

      if (ObjectType.TYPE.equals(arrayElementType)) {
        ObjectArrayElement arrayElement = new ObjectArrayElement();
        addFieldsForClass(arrayElementClass, arrayElement.getFields());
        return new ArrayField(property.getName(), arrayElement);
      }

      throw new UnsupportedOperationException("Unsupported array element type: " + arrayElementType);
    }

    ObjectField objectField = new ObjectField(property.getName());
    addFieldsForClass(propertyType, objectField.getFields());

    return objectField;
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

  private static String getEntityName(Class<?> entity) {
    return Introspector.decapitalize(entity.getSimpleName());
  }
}
