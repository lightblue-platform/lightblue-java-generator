package com.redhat.lightblue.generator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * By default, entity name will be derived from the class name, however occasionally it is desirable
 * to name the class differently than the entity name exactly. This provides a way to override that
 * computed name.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface EntityName {
  String value();
}
