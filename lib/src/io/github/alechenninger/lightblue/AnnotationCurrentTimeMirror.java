package io.github.alechenninger.lightblue;

public class AnnotationCurrentTimeMirror implements CurrentTimeMirror {
  private final CurrentTime currentTime;

  public AnnotationCurrentTimeMirror(CurrentTime currentTime) {
    this.currentTime = currentTime;
  }

  @Override
  public boolean isOverwrite() {
    return currentTime.overwrite();
  }
}
