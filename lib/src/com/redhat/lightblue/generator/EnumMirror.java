package com.redhat.lightblue.generator;

import java.util.Set;

public interface EnumMirror {
  String name();
  Set<EnumValueMirror> values();
}
