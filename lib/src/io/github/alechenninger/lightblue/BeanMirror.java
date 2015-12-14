package io.github.alechenninger.lightblue;

import java.util.Collection;

public interface BeanMirror {
  String getEntityName();
  VersionMirror getVersion();
  Collection<FieldMirror> getFields();
}
