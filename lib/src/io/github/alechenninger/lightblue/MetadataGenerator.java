package io.github.alechenninger.lightblue;

import com.redhat.lightblue.metadata.ArrayField;
import com.redhat.lightblue.metadata.EntityInfo;
import com.redhat.lightblue.metadata.EntityMetadata;
import com.redhat.lightblue.metadata.EntitySchema;
import com.redhat.lightblue.metadata.Enum;
import com.redhat.lightblue.metadata.EnumValue;
import com.redhat.lightblue.metadata.Enums;
import com.redhat.lightblue.metadata.Field;
import com.redhat.lightblue.metadata.FieldConstraint;
import com.redhat.lightblue.metadata.Fields;
import com.redhat.lightblue.metadata.MetadataStatus;
import com.redhat.lightblue.metadata.ObjectArrayElement;
import com.redhat.lightblue.metadata.ObjectField;
import com.redhat.lightblue.metadata.SimpleArrayElement;
import com.redhat.lightblue.metadata.SimpleField;
import com.redhat.lightblue.metadata.Type;
import com.redhat.lightblue.metadata.Version;
import com.redhat.lightblue.metadata.constraints.EnumConstraint;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MetadataGenerator {
  private final Reflector reflector;

  public MetadataGenerator(Reflector reflector) {
    this.reflector = reflector;
  }

  public EntityMetadata generateMetadata(Class<?> entity) {
    EntityInfo info = generateInfo(entity);
    EntitySchema schema = generateSchema(entity);
    return new EntityMetadata(info, schema);
  }

  public EntityInfo generateInfo(Class<?> entity) {
    BeanMirror beanMirror = reflector.reflect(entity);
    EntityInfo info = new EntityInfo(beanMirror.getEntityName());
    Enums enums = info.getEnums();

    for (FieldMirror fieldMirror : beanMirror.getFields()) {
      if (fieldMirror.javaType().isEnum()) {
        EnumMirror enumMirror = fieldMirror.enumMirror().get();

        Enum generatedEnum = new Enum(enumMirror.name());
        Set<EnumValue> enumValues = new HashSet<>();

        for (EnumValueMirror enumValueMirror : enumMirror.values()) {
          String enumName = enumValueMirror.name();
          String enumDescription = enumValueMirror.description().orElse(null);
          enumValues.add(new EnumValue(enumName, enumDescription));
        }

        generatedEnum.setValues(enumValues);
        enums.addEnum(generatedEnum);
      }
    }

    return info;
  }

  public EntitySchema generateSchema(Class<?> entity) {
    BeanMirror beanMirror = reflector.reflect(entity);
    EntitySchema schema = new EntitySchema(beanMirror.getEntityName());
    schema.setStatus(MetadataStatus.ACTIVE);

    VersionMirror versionMirror  = beanMirror.getVersion();
    Collection<String> extendsVersionsCollection = versionMirror.getExtendsVersions();
    String[] extendsVersionsArr = extendsVersionsCollection.isEmpty()
        ? null
        : extendsVersionsCollection.toArray(new String[extendsVersionsCollection.size()]);

    schema.setVersion(
        new Version(versionMirror.getVersion(), extendsVersionsArr, versionMirror.getChangelog()));

    addLightblueFieldsForClass(beanMirror, schema.getFields());

    return schema;
  }

  private void addLightblueFieldsForClass(BeanMirror beanMirror, Fields fields) {
    for (FieldMirror fieldMirror : beanMirror.getFields()) {
      Field field = getLightblueFieldForBeanField(fieldMirror);
      field.setConstraints(getConstraintsForBeanField(fieldMirror));
      fieldMirror.description().ifPresent(d -> field.getProperties().put("description", d));

      fields.addNew(field);
    }
  }

  private Field getLightblueFieldForBeanField(FieldMirror fieldMirror) {
    Class<?> javaType = fieldMirror.javaType();

    Type type = getTypeForClass(javaType);

    if (isSimpleFieldType(type)) {
      return new SimpleField(fieldMirror.name(), type);
    }

    if (ArrayType.TYPE.equals(type)) {
      Class<?> elementJavaType = fieldMirror.elementJavaType().get();
      Type arrayElementType = getTypeForClass(elementJavaType);

      if (isSimpleFieldType(arrayElementType)) {
        return new ArrayField(fieldMirror.name(), new SimpleArrayElement(arrayElementType));
      }

      if (ObjectType.TYPE.equals(arrayElementType)) {
        ObjectArrayElement arrayElement = new ObjectArrayElement();
        addLightblueFieldsForClass(reflector.reflect(elementJavaType), arrayElement.getFields());
        return new ArrayField(fieldMirror.name(), arrayElement);
      }

      throw new UnsupportedOperationException("Unsupported array element type: " + arrayElementType);
    }

    ObjectField objectField = new ObjectField(fieldMirror.name());
    addLightblueFieldsForClass(reflector.reflect(javaType), objectField.getFields());

    return objectField;
  }

  private Collection<FieldConstraint> getConstraintsForBeanField(FieldMirror fieldMirror) {
    List<FieldConstraint> constraints = new ArrayList<>();

    if (fieldMirror.isRequired()) {
      constraints.add(new RequiredConstraint());
    }

    fieldMirror.minItems().ifPresent(i -> {
      MinMaxConstraint constraint = new MinMaxConstraint(MinMaxConstraint.MIN);
      constraint.setValue(i);
      constraints.add(constraint);
    });

    fieldMirror.minLength().ifPresent(l ->
        constraints.add(new StringLengthConstraint(StringLengthConstraint.MINLENGTH, l)));

    fieldMirror.maxLength().ifPresent(l ->
        constraints.add(new StringLengthConstraint(StringLengthConstraint.MAXLENGTH, l)));

    if (fieldMirror.isIdentifying()) {
      constraints.add(new IdentityConstraint());
    }

    if (fieldMirror.javaType().isEnum()) {
      EnumConstraint enumConstraint = new EnumConstraint();
      enumConstraint.setName(fieldMirror.enumMirror().get().name());
      constraints.add(enumConstraint);
    }

    return constraints;
  }

  private Type getTypeForClass(Class<?> type) {
    if (type.equals(String.class) || type.isEnum()) {
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
