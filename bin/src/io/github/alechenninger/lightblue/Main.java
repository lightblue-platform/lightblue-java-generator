package io.github.alechenninger.lightblue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.redhat.lightblue.metadata.EntityMetadata;
import com.redhat.lightblue.metadata.parser.Extensions;
import com.redhat.lightblue.metadata.parser.JSONMetadataParser;
import com.redhat.lightblue.metadata.types.DefaultTypes;
import io.github.alechenninger.lightblue.javabeans.JavaBeansReflector;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;

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
    if (args.length < 2) {
      System.out.println("Usage: generator models.jar com.mycompany.Model1 com.mycompany.Model2 ...");
      System.exit(1);
    }

    String jarPath = args[0];
    URL jarUrl = Paths.get(jarPath).toUri().toURL();
    URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, Main.class.getClassLoader());

    for (int i = 1; i < args.length; i++) {
      String className = args[i];
      Class classForName = classLoader.loadClass(className);
      EntityMetadata metadata = generater.generateMetadata(classForName);
      JsonNode metadataJson = parser.convert(metadata);
      String prettyMetadataJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadataJson);
      Files.write(Paths.get(metadata.getName() + ".json"), prettyMetadataJson.getBytes("UTF-8"));
    }
  }
}
