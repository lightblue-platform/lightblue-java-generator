package com.redhat.lightblue.generator;

import java.util.Optional;

public interface IntSequenceMirror extends ValueGeneratorMirror {
  Optional<Integer> initialValue();
  String name();
}
