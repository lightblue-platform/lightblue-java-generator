package com.redhat.lightblue.generator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Backs a field by a lightblue value generator which generates integers in a continuous, atomically
 * incrementing sequence.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface IntSequence {
  String name();

  /**
   * By default, leaves initial value up to lightblue controller implementation.
   *
   * <p>Setting this to {@link #INITIAL_VALUE_UNSET} is equivalent to not setting an initial value.
   *
   * <p>As of 3/19/2016, lightblue-mongo's default is 1.
   */
  int initialValue() default INITIAL_VALUE_UNSET;

  boolean overwrite() default false;

  int INITIAL_VALUE_UNSET = Integer.MAX_VALUE;
}
