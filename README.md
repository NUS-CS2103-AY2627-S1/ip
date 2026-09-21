# Auto

Auto is a task chatbot with a JavaFX graphical interface and a Singaporean
personality. Type commands in its chat window to manage todos, deadlines, and
events, search tasks by name or date, and mark tasks as complete. Auto saves
tasks automatically and shows reminders for today's scheduled tasks at startup.

## Prerequisites

- JDK 25. Run `java -version` to check the version used by your terminal.
- Command Prompt on Windows for the quickstart command below

## Build and run the fat JAR

The Shadow plugin packages Auto and all its runtime dependencies into one
executable fat JAR. From the project root, create it with:

```cmd
gradlew.bat clean shadowJar
```

On macOS or Linux, use `./gradlew clean shadowJar` instead. `clean` removes old
build output, and `shadowJar` compiles the project and creates the executable
JAR.

The generated file is located at:

```text
build/libs/auto.jar
```

Run it from the project root with:

```cmd
java -jar build\libs\auto.jar
```

On macOS or Linux, use `java -jar build/libs/auto.jar`. This opens Auto's chat
window. Enter commands in the input field and press Enter to send them.
Use `bye` to close the application. Rebuild the JAR after changing the source code.

Auto stores tasks in `data/auto.txt` relative to the directory from which the
JAR is run. Run it from the project root to use the repository's `data`
directory consistently.

## Commands

See the [User Guide](https://leewenkang-nus.github.io/ip/) for all commands,
examples, screenshots, and instructions for downloading a prebuilt JAR.

## Storage

Auto loads and saves tasks automatically using `data/auto.txt`, relative to the
directory from which the application is run. Stored text fields are Base64
encoded so task descriptions containing separators can be recovered safely.

## Source structure

`src/main/java` remains the Java source root. Classes are organized under the
base `auto` package:

```text
auto
|-- Auto.java
|-- Launcher.java
|-- Main.java
|-- command
|-- exception
|-- parser
|-- storage
|-- task
|-- ui
`-- util
```

JavaFX layouts, styles, and images are stored in `src/main/resources`.

When using IntelliJ IDEA, configure the project to use JDK 25 and mark
`src/main/java` as the source root.
