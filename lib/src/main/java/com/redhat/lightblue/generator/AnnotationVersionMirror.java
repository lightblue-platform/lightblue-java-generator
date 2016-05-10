package com.redhat.lightblue.generator;

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

public class AnnotationVersionMirror implements VersionMirror {
  private final Class<?> bean;
  private final Optional<Version> version;

  public AnnotationVersionMirror(Class<?> bean) {
    this.bean = Objects.requireNonNull(bean, "bean");
    this.version = Optional.ofNullable(bean.getAnnotation(Version.class));
  }

  public boolean isVersionAnnotationPresent() {
    return version.isPresent();
  }

  @Override
  public String getVersion() {
    Version annotation = version.orElseThrow(() -> new MirrorException(
            new NoSuchElementException("No version annotation present")));

    if (annotation.preferImplementationVersion()) {
      return Optional.ofNullable(bean.getPackage().getImplementationVersion())
          .orElse(annotation.value());
    }

    return annotation.value();
  }

  @Override
  public String getChangelog() {
    return version.map(Version::changelog)
        .orElseThrow(() -> new MirrorException(
            new NoSuchElementException("No version annotation present")));
  }

  @Override
  public Collection<String> getExtendsVersions() {
    return Arrays.asList(version.map(Version::extendsVersions).orElse(new String[0]));
  }
}
