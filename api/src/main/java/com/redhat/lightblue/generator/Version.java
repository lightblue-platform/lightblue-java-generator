package com.redhat.lightblue.generator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.NoSuchElementException;
import java.util.Optional;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Version {
  String value();

  /**
   * <strong>This doesn't work yet!</strong>
   *
   * <p>Uses "Implementation-Version" defined in package manifest for the annotated entity if it is
   * set, otherwise falls back to {@link #value()}.
   * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/versioning/spec/versioning2.html#wp89936">
   *   Package Version Specification</a>
   * @see <a href="https://maven.apache.org/shared/maven-archiver/examples/manifest.html">
   *   Maven Archiver Plugin</a>
   */
  boolean preferImplementationVersion() default false;

  String changelog();

  String[] extendsVersions() default {};

  abstract class FromAnnotation {
    public static String onEntity(Class<?> entityClass) {
      Version annotation = entityClass.getAnnotation(Version.class);

      if (annotation == null) {
        throw new NoSuchElementException("No Version annotation on class: " + entityClass);
      }

      String implementationVersion = entityClass.getPackage().getImplementationVersion();

      if (annotation.preferImplementationVersion() && implementationVersion != null) {
        return implementationVersion;
      }

      return annotation.value();
    }
  }
}
