# Dukey

Dukey is a Java task chatbot with both CLI and JavaFX GUI entry points.

## Setup

Prerequisites:

- JDK 25
- Gradle wrapper from this repository

Open the project in IntelliJ IDEA and configure the project SDK to use JDK 25.

## Running Dukey

Run the GUI:

```bash
./gradlew run
```

Run the CLI:

```bash
./gradlew runCli
```

Run automated checks:

```bash
./gradlew check
```

Create the fat JAR:

```bash
./gradlew clean shadowJar
```

The generated JAR is `build/libs/dukey.jar`.

## Source Root

Keep `src/main/java` as the Java source root. Do not rename `src`, `main`, or `java` into Java packages.
