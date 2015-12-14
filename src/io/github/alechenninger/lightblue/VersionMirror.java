package io.github.alechenninger.lightblue;

import java.util.Collection;
import java.util.Optional;

public interface VersionMirror {
  String getVersion();
  String getChangelog();
  Collection<String> getExtendsVersions();
}
