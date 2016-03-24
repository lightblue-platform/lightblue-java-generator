package io.github.alechenninger.lightblue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.redhat.lightblue.metadata.EntityInfo;
import com.redhat.lightblue.metadata.EntityMetadata;
import com.redhat.lightblue.metadata.parser.Extensions;
import com.redhat.lightblue.metadata.parser.JSONMetadataParser;
import com.redhat.lightblue.metadata.types.DefaultTypes;
import io.github.alechenninger.lightblue.javabeans.JavaBeansReflector;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class Main {
  private static Extensions extensions = new Extensions();
  static {
    extensions.addDefaultExtensions();
  }
  private static JsonNodeFactory factory = JsonNodeFactory.withExactBigDecimals(true);
  private static JSONMetadataParser parser = new JSONMetadataParser(extensions, new DefaultTypes(), factory);
  private static MetadataGenerator generater = new MetadataGenerator(new JavaBeansReflector());
  private static ObjectMapper mapper = new ObjectMapper();

  public static void main(String[] args) throws IOException, ClassNotFoundException {
    Cli cli = new Cli(args);

    if (cli.helpRequested()) {
      System.exit(1);
    }

    ClassLoader classLoader = getClassLoaderToSearch(cli.jarPath());

    if (cli.entityClasses().isEmpty()) {
      println("No entity classes provided.");
      println("Example: lightblue-java-generator optional/path/to/entities.jar com.redhat.Entity1.class com.redhat.Entity2.class");
      println("");
      println("Help output below:");
      println("");
      cli.printHelpTo(System.out);
      System.exit(1);
    }

    for (String className : cli.entityClasses()) {
      Class classForName = classLoader.loadClass(className);
      EntityInfo info = generater.generateInfo(classForName);

      Path metadataJsonPath = Paths.get(info.getName() + ".json");
      EntityMetadata metadata;

      if (Files.exists(metadataJsonPath)) {
        println(metadataJsonPath + " already exists, updating...");

        JsonNode existingJsonNode = mapper.readTree(Files.readAllBytes(metadataJsonPath));
        EntityMetadata existing = parser.parseEntityMetadata(existingJsonNode);

        metadata = generater.updateMetadata(existing, classForName);
      } else {
        metadata = generater.generateMetadata(classForName);
      }

      JsonNode metadataJson = parser.convert(metadata);
      String prettyMetadataJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadataJson);
      Files.write(metadataJsonPath, prettyMetadataJson.getBytes("UTF-8"));

      println("Wrote " + metadataJsonPath);
    }
  }

  private static ClassLoader getClassLoaderToSearch(Optional<String> maybeJarPath)
      throws MalformedURLException {
    if (maybeJarPath.isPresent()) {
      String jarPath = maybeJarPath.get();
      URL jarUrl = Paths.get(jarPath).toUri().toURL();
      return new URLClassLoader(new URL[]{jarUrl}, Main.class.getClassLoader());
    }

    return Main.class.getClassLoader();
  }

  static void println(Object line) {
    System.out.println(line);
  }
}
