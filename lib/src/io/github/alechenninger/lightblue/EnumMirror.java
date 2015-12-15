package io.github.alechenninger.lightblue;

import java.util.Set;

public interface EnumMirror {
  String name();
  Set<EnumValueMirror> values();
}
