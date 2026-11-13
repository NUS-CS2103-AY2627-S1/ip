# Nova

Nova is a task-management chatbot built for CS2103T. It supports a JavaFX GUI while keeping the command-based interaction model used throughout the iP.

## Requirements

- JDK 25
- Gradle wrapper included in this repository

## Run Nova

Windows:

```text
gradlew run
```

macOS/Linux:

```text
./gradlew run
```

## Run tests

Windows:

```text
gradlew clean test
```

macOS/Linux:

```text
./gradlew clean test
```

## Build the executable JAR

Windows:

```text
gradlew clean shadowJar
```

macOS/Linux:

```text
./gradlew clean shadowJar
```

The fat JAR is generated at `build/libs/nova.jar` and includes the JavaFX dependencies required to run on supported platforms.

See `docs/README.md` for the user guide and supported commands.
