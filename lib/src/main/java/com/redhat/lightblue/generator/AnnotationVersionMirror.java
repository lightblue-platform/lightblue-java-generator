package com.redhat.lightblue.generator;

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

public class AnnotationVersionMirror implements VersionMirror {
  private final Optional<Version> version;

  public AnnotationVersionMirror(Class<?> bean) {
    this.version = Optional.ofNullable(bean.getAnnotation(Version.class));
  }

  public boolean isVersionAnnotationPresent() {
    return version.isPresent();
  }

  @Override
  public String getVersion() {
    return version.map(Version::value)
        .orElseThrow(() -> new MirrorException(
            new NoSuchElementException("No version annotation present")));
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
