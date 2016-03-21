package io.github.alechenninger.lightblue;

import com.redhat.lightblue.metadata.ArrayElement;
import com.redhat.lightblue.metadata.ArrayField;
import com.redhat.lightblue.metadata.EntityAccess;
import com.redhat.lightblue.metadata.EntityInfo;
import com.redhat.lightblue.metadata.EntityMetadata;
import com.redhat.lightblue.metadata.EntitySchema;
import com.redhat.lightblue.metadata.Enum;
import com.redhat.lightblue.metadata.EnumValue;
import com.redhat.lightblue.metadata.Enums;
import com.redhat.lightblue.metadata.Field;
import com.redhat.lightblue.metadata.FieldAccess;
import com.redhat.lightblue.metadata.FieldConstraint;
import com.redhat.lightblue.metadata.Fields;
import com.redhat.lightblue.metadata.MetadataStatus;
import com.redhat.lightblue.metadata.ObjectArrayElement;
import com.redhat.lightblue.metadata.ObjectField;
import com.redhat.lightblue.metadata.SimpleArrayElement;
import com.redhat.lightblue.metadata.SimpleField;
import com.redhat.lightblue.metadata.Type;
import com.redhat.lightblue.metadata.ValueGenerator;
import com.redhat.lightblue.metadata.ValueGenerator.ValueGeneratorType;
import com.redhat.lightblue.metadata.Version;
import com.redhat.lightblue.metadata.constraints.ArrayElementIdConstraint;
import com.redhat.lightblue.metadata.constraints.ArraySizeConstraint;
import com.redhat.lightblue.metadata.constraints.EnumConstraint;
import com.redhat.lightblue.metadata.constraints.IdentityConstraint;
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
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
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

  public EntityMetadata updateMetadata(EntityMetadata original, Class<?> entity) {
    BeanMirror beanMirror = reflector.reflect(entity);

    EntityInfo info = generateInfo(beanMirror, Optional.of(original.getEntityInfo()));
    EntitySchema schema = generateSchema(beanMirror, Optional.of(original.getEntitySchema()));

    return new EntityMetadata(info, schema);
  }

  public EntityInfo generateInfo(Class<?> entity) {
    BeanMirror beanMirror = reflector.reflect(entity);
    return generateInfo(beanMirror, Optional.empty());
  }

  public EntityInfo updateInfo(EntityInfo info, Class<?> entity) {
    BeanMirror beanMirror = reflector.reflect(entity);

    if (!Objects.equals(info.getName(), beanMirror.getEntityName())) {
      throw new IllegalArgumentException("Entity name mismatch between provided entity info and "
          + "generated entity info.");
    }

    return generateInfo(beanMirror, Optional.of(info));
  }

  public EntitySchema generateSchema(Class<?> entity) {
    BeanMirror beanMirror = reflector.reflect(entity);
    return generateSchema(beanMirror, Optional.empty());
  }

  public EntitySchema updateSchema(EntitySchema schema, Class<?> entity) {
    BeanMirror beanMirror = reflector.reflect(entity);

    if (!Objects.equals(schema.getName(), beanMirror.getEntityName())) {
      throw new IllegalArgumentException("Entity name mismatch between provided entity schema and "
          + "generated entity schema.");
    }

    return generateSchema(beanMirror, Optional.of(schema));
  }

  private EntityInfo generateInfo(BeanMirror beanMirror, Optional<EntityInfo> maybeOriginal) {
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

    maybeOriginal.ifPresent(original -> {
      info.getIndexes().setIndexes(original.getIndexes().getIndexes());
      info.getHooks().setHooks(original.getHooks().getHooks());
      info.setDataStore(original.getDataStore());
      info.setDefaultVersion(original.getDefaultVersion());
    });

    return info;
  }

  private EntitySchema generateSchema(BeanMirror beanMirror, Optional<EntitySchema> maybeOriginal) {
    EntitySchema schema = new EntitySchema(beanMirror.getEntityName());
    schema.setStatus(MetadataStatus.ACTIVE);

    maybeOriginal.ifPresent(original -> {
      EntityAccess access = schema.getAccess();
      EntityAccess originalAccess = original.getAccess();

      access.getDelete().setRoles(originalAccess.getDelete());
      access.getInsert().setRoles(originalAccess.getInsert());
      access.getFind().setRoles(originalAccess.getFind());
      access.getUpdate().setRoles(originalAccess.getUpdate());

      schema.setStatusChangeLog(original.getStatusChangeLog());
      schema.setStatus(original.getStatus());
    });

    beanMirror.getVersion().ifPresent(versionMirror -> {
      Collection<String> extendsVersionsCollection = versionMirror.getExtendsVersions();
      String[] extendsVersionsArr = extendsVersionsCollection.isEmpty()
          ? null
          : extendsVersionsCollection.toArray(new String[extendsVersionsCollection.size()]);

      schema.setVersion(
          new Version(versionMirror.getVersion(), extendsVersionsArr, versionMirror.getChangelog()));
    });

    addFieldsFromBeanMirror(beanMirror, schema.getFields(),
        maybeOriginal.map(EntitySchema::getFields));

    return schema;
  }

  private void addFieldsFromBeanMirror(BeanMirror beanMirror, Fields fields,
      Optional<Fields> maybeOriginal) {
    for (FieldMirror fieldMirror : beanMirror.getFields()) {
      String name = fieldMirror.name();
      Optional<Field> maybeOriginalField = maybeOriginal.map(o -> o.getField(name));

      Field field = getFieldFromFieldMirror(fieldMirror, maybeOriginalField);
      field.setConstraints(getConstraintsForBeanField(fieldMirror));
      fieldMirror.description().ifPresent(field::setDescription);

      if (field instanceof SimpleField) {
        SimpleField simpleField = (SimpleField) field;
        getValueGeneratorForBeanField(fieldMirror).ifPresent(simpleField::setValueGenerator);
      }

      maybeOriginalField.ifPresent(original -> {
        FieldAccess access = field.getAccess();
        FieldAccess originalAccess = original.getAccess();

        access.getFind().setRoles(originalAccess.getFind());
        access.getInsert().setRoles(originalAccess.getInsert());
        access.getUpdate().setRoles(originalAccess.getUpdate());
      });

      fields.addNew(field);
    }
  }

  private Field getFieldFromFieldMirror(FieldMirror fieldMirror, Optional<Field> maybeOriginal) {
    Class<?> javaType = fieldMirror.javaType();
    String name = fieldMirror.name();

    Type type = getTypeForClass(javaType);

    if (isSimpleFieldType(type)) {
      // Ignore original in this case
      return new SimpleField(name, type);
    }

    if (ArrayType.TYPE.equals(type)) {
      Class<?> elementJavaType = fieldMirror.elementJavaType().get();
      Type arrayElementType = getTypeForClass(elementJavaType);

      if (isSimpleFieldType(arrayElementType)) {
        return new ArrayField(name, new SimpleArrayElement(arrayElementType));
      }

      if (ObjectType.TYPE.equals(arrayElementType)) {
        ObjectArrayElement arrayElement = new ObjectArrayElement();

        Optional<Fields> maybeOriginalElementFields = maybeOriginal.flatMap(original -> {
          if (!(original instanceof ArrayField)) {
            return Optional.empty();
          }

          ArrayField originalArrayField = (ArrayField) original;
          ArrayElement originalElement = originalArrayField.getElement();

          if (!(originalElement instanceof ObjectArrayElement)) {
            return Optional.empty();
          }

          ObjectArrayElement originalObjectElement = (ObjectArrayElement) originalElement;

          return Optional.of(originalObjectElement.getFields());
        });

        addFieldsFromBeanMirror(reflector.reflect(elementJavaType), arrayElement.getFields(),
            maybeOriginalElementFields);

        return new ArrayField(name, arrayElement);
      }

      throw new UnsupportedOperationException("Unsupported array element type: " + arrayElementType);
    }

    ObjectField objectField = new ObjectField(name);

    Optional<Fields> maybeOriginalObjectFields = maybeOriginal.flatMap(original -> {
      if (!(original instanceof ObjectField)) {
        return Optional.empty();
      }

      ObjectField originalObject = (ObjectField) original;

      return Optional.of(originalObject.getFields());
    });

    addFieldsFromBeanMirror(reflector.reflect(javaType), objectField.getFields(),
        maybeOriginalObjectFields);

    return objectField;
  }

  private Collection<FieldConstraint> getConstraintsForBeanField(FieldMirror fieldMirror) {
    List<FieldConstraint> constraints = new ArrayList<>();

    if (fieldMirror.isRequired()) {
      constraints.add(new RequiredConstraint());
    }

    fieldMirror.minItems().ifPresent(i -> {
      ArraySizeConstraint constraint = new ArraySizeConstraint(ArraySizeConstraint.MIN);
      constraint.setValue(i);
      constraints.add(constraint);
    });

    fieldMirror.maxItems().ifPresent(i -> {
      ArraySizeConstraint constraint = new ArraySizeConstraint(ArraySizeConstraint.MAX);
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

    if (fieldMirror.isElementIdentifying()) {
      constraints.add(new ArrayElementIdConstraint());
    }

    if (fieldMirror.javaType().isEnum()) {
      EnumConstraint enumConstraint = new EnumConstraint();
      enumConstraint.setName(fieldMirror.enumMirror().get().name());
      constraints.add(enumConstraint);
    }

    return constraints;
  }

  private Optional<ValueGenerator> getValueGeneratorForBeanField(FieldMirror fieldMirror) {
    return fieldMirror.valueGeneratorMirror().map(generatorMirror -> {
      if (generatorMirror instanceof IntSequenceMirror) {
        IntSequenceMirror intSequence = (IntSequenceMirror) generatorMirror;

        ValueGenerator generator = new ValueGenerator(ValueGeneratorType.IntSequence);
        Properties properties = generator.getProperties();

        generator.setOverwrite(intSequence.isOverwrite());
        properties.setProperty("name", intSequence.name());
        intSequence.initialValue().ifPresent(i -> {
          properties.setProperty("initialValue", i.toString());
        });

        return generator;
      }

      if (generatorMirror instanceof UuidMirror) {
        ValueGenerator generator = new ValueGenerator(ValueGeneratorType.UUID);
        generator.setOverwrite(generatorMirror.isOverwrite());

        return generator;
      }

      if (generatorMirror instanceof CurrentTimeMirror) {
        ValueGenerator generator = new ValueGenerator(ValueGeneratorType.CurrentTime);
        generator.setOverwrite(generatorMirror.isOverwrite());

        return generator;
      }

      throw new UnsupportedOperationException("Unsupported generator type " +
          generatorMirror.getClass());
    });
  }

  private Type getTypeForClass(Class<?> type) {
    if (type.equals(String.class) || type.isEnum()) {
      return StringType.TYPE;
    }

    if (type.equals(boolean.class) || type.equals(Boolean.class)) {
      return BooleanType.TYPE;
    }

    if (type.equals(Date.class) || Temporal.class.isAssignableFrom(type)) {
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
