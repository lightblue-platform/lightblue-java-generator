package io.github.alechenninger.lightblue;

import static java.util.Arrays.asList;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Optional;

public class Cli {
  private static OptionParser parser = new OptionParser();
  static {
    parser.nonOptions("Fully-qualified entity class names to generate entity metadata for.");
  }

  private static OptionSpec<String> jarOption = parser.acceptsAll(asList("j", "jar"),
      "Path to jar to search for entity classes. If not provided, the current classpath is "
          + "searched instead.")
      .withRequiredArg();

  private static final OptionSpec<Void> helpOption = parser.acceptsAll(asList("h", "?", "help"),
      "Displays this message.")
      .forHelp();

  private final OptionSet optionSet;

  public Cli(String[] args) {
    optionSet = parser.parse(args);
  }

  public boolean helpRequested() {
    return optionSet.has(helpOption);
  }

  public Optional<String> jarPath() {
    if (!optionSet.has(jarOption)) {
      return Optional.empty();
    }

    return Optional.of(optionSet.valueOf(jarOption));
  }

  public Collection<String> entityClasses() {
    return (Collection<String>) optionSet.nonOptionArguments();
  }

  public void printHelpTo(OutputStream out) throws IOException {
    PrintStream printStream = new PrintStream(out);
    printStream.println("Usage: lightblue-java-generator [-j optional/path/to/entities.jar] "
        + "com.redhat.Entity1 com.redhat.Entity2");
    printStream.println();
    parser.printHelpOn(out);
  }
}
