# DisciTrack

DisciTrack is a desktop task manager with a motivational coach personality. It helps users organise todos,
deadlines, and events, search their schedule, group tasks with tags, and keep their progress saved automatically.

![DisciTrack task board](docs/Ui.png)

## User Guide

Read the [DisciTrack User Guide](https://ngweijien35.github.io/ip/) for all commands, GUI controls, examples,
and data-recovery instructions.

## Setting up in IntelliJ

### Prerequisites

- JDK 25
- A recent version of IntelliJ IDEA

### Setup

1. Open IntelliJ. If another project is open, select **File → Close Project** first.
2. Select **Open**, choose this project directory, and accept the default import settings.
3. Configure the project to use **JDK 25** and set the project language level to **SDK default**. See
   [IntelliJ's SDK setup guide](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk) if needed.
4. Open `src/main/java/discitrack/gui/Launcher.java` and run `Launcher.main()`.

Keep `src/main/java` as the source root. Gradle and IntelliJ rely on this standard project structure.

## Building and running

From the project root, build the application with:

```text
./gradlew clean shadowJar
```

On Windows PowerShell, use:

```text
.\gradlew.bat clean shadowJar
```

The cross-platform fat JAR is created at `build/libs/discitrack.jar`. Run it with Java 25:

```text
java -jar build/libs/discitrack.jar
```

Run the automated tests and Checkstyle checks with:

```text
./gradlew check
```
