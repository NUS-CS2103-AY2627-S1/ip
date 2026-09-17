# Wobble

Wobble is a JavaFX task companion with a small robotic personality. It stores ToDos,
deadlines, and events, then lets you search, review, complete, and remove them. Tasks
are saved automatically in `data/wobble.txt` relative to the directory from which
Wobble is started.

The application starts with the JavaFX GUI. The complete command reference is in the
[Wobble User Guide](docs/README.md), and the GUI also displays it when you enter `help`.

## Requirements

- JDK 25
- IntelliJ IDEA with Gradle support (for IDE development)
- A terminal for running the Gradle wrapper commands below

The Gradle wrapper downloads the required Gradle version automatically, so a separate
Gradle installation is not required.

## Run Wobble

### From IntelliJ IDEA

1. Open this repository as a Gradle project.
2. Set the project SDK and the Gradle JVM to JDK 25. In IntelliJ, the Gradle JVM is
   under `Settings` or `Preferences` > `Build, Execution, Deployment` > `Build Tools`
   > `Gradle`.
3. Run `wobble.gui.Launcher` to start the JavaFX interface. The window can be resized;
   enter a command in the input field and press `Enter` or click `Send`.
4. Enter `help` to display the supported commands and date/time formats. Enter `bye`
   to run Wobble's shutdown sequence and close the application.

For the console-only interface, run `wobble.Wobble.main()` from IntelliJ. Both
interfaces use the same task file and command behavior.

### From the terminal

Check that the terminal is using Java 25 before running Gradle:

```bash
java -version
```

On macOS with SDKMAN, switch Java versions in the same terminal session if necessary:

```bash
sdk use java 25.0.3.fx-zulu
java -version
```

Start the GUI with:

```bash
./gradlew run
```

## Build and test

Run the full local verification suite:

```bash
./gradlew check
```

This compiles the project, runs the JUnit tests, and runs Checkstyle on production and
test code.

## Build and run the fat JAR

Create a fresh executable fat JAR containing the JavaFX runtime dependencies:

```bash
./gradlew clean shadowJar
```

The output is `build/libs/wobble.jar`. Run it from the project root with Java 25:

```bash
java -jar build/libs/wobble.jar
```

The JAR entry point is `wobble.gui.Launcher`. JavaFX is bundled for macOS, Windows,
and Linux; the JAR should therefore be run with a Java 25 runtime on the target machine.

## Continuous integration

GitHub Actions is configured in `.github/workflows/wobble.yml`. Every push and pull
request runs `./gradlew check` on Ubuntu, macOS, and Windows using Java 25.

For command formats, validation rules, persistence behavior, and examples, see the
[Wobble User Guide](docs/README.md).
