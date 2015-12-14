package io.github.alechenninger.lightblue;

public class SchemaGenerationException extends RuntimeException {
  public SchemaGenerationException(String message) {
    super(message);
  }

  public SchemaGenerationException(String message, Throwable cause) {
    super(message, cause);
  }

  public SchemaGenerationException(Throwable cause) {
    super(cause);
  }
}
