# Bingus Dingus

Bingus Dingus is a desktop task manager for todos, deadlines, and events. It
uses a simple command-based interface to help you manage your tasks.

## Prerequisites

- JDK 25
- IntelliJ IDEA (optional)

## Running the application

To run the application with Gradle:

```text
gradlew.bat run
```

To build a standalone JAR file:

```text
gradlew.bat shadowJar
```

The JAR is created at `build/libs/bingusdingus.jar`. Run it with:

```text
java -jar build/libs/bingusdingus.jar
```

Task data is saved automatically in `data/bingusdingus.txt` relative to the
application's working directory.

## Running the tests

Run the automated unit tests and Checkstyle checks with:

```text
gradlew.bat test check
```

## Setting up in IntelliJ IDEA

1. Open IntelliJ IDEA and select **Open**.
2. Select this project directory.
3. Configure the project SDK to use JDK 25 and set the project language level
   to **SDK default**.
4. To run the graphical application, open
   `src/main/java/bingusdingus/gui/Launcher.java` and run `Launcher.main()`.

If IntelliJ reports stale compilation errors after setup, reload the Gradle
project or restart the IDE.

## Documentation

See the published [Bingus Dingus User Guide](https://chunghean.github.io/ip/)
for command formats, examples, date and time formats, saving behavior, and
error handling.
