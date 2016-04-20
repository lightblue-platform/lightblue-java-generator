package com.redhat.lightblue.generator;

/**
 * Exception thrown while trying to reflect on some code to determine values.
 */
public class MirrorException extends RuntimeException {
  public MirrorException(Throwable throwable) {
    super(throwable);
  }
}
