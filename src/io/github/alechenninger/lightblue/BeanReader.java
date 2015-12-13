package io.github.alechenninger.lightblue;

import com.redhat.lightblue.metadata.Version;

import java.util.Collection;

public interface BeanReader {
  String getEntityName(Class<?> bean);
  Version getEntityVersion(Class<?> bean);
  Collection<BeanField> readBeanFields(Class<?> bean);
}
