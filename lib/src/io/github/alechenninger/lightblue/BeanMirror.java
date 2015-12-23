package io.github.alechenninger.lightblue;

import java.util.Collection;
import java.util.Optional;

public interface BeanMirror {
  String getEntityName();
  Optional<VersionMirror> getVersion();
  Collection<FieldMirror> getFields();
}
