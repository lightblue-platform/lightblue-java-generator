package com.redhat.lightblue.generator;

import java.util.Optional;

public class AnnotationIntSequenceMirror implements IntSequenceMirror {
  private final IntSequence intSequence;

  public AnnotationIntSequenceMirror(IntSequence intSequence) {
    this.intSequence = intSequence;
  }

  @Override
  public Optional<Integer> initialValue() {
    return intSequence.initialValue() == IntSequence.INITIAL_VALUE_UNSET
        ? Optional.empty()
        : Optional.of(intSequence.initialValue());
  }

  @Override
  public String name() {
    return intSequence.name();
  }

  @Override
  public boolean isOverwrite() {
    return intSequence.overwrite();
  }
}
