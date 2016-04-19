package com.redhat.lightblue.generator;

import java.util.Optional;

public interface EnumValueMirror {
  String name();
  Optional<String> description();
}
