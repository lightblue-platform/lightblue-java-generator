package io.github.alechenninger.lightblue;

import java.util.Optional;

public interface IntSequenceMirror extends ValueGeneratorMirror {
  Optional<Integer> initialValue();
  String name();
}
