# Victoria

Victoria is a JavaFX task-management chatbot. It helps you record to-dos, deadlines, and events in a simple chat interface, and saves your task list automatically.

For commands and examples, see the [User Guide](docs/README.md).

## Requirements

- Java 25
- IntelliJ IDEA (recommended for development)

## Run in IntelliJ IDEA

1. Open this project folder in IntelliJ IDEA and accept the default import options.
2. Set the project SDK to **JDK 25** and the language level to **SDK default**.
3. Run `victoria.gui.Launcher` from `src/main/java/victoria/gui/Launcher.java`.

Victoria opens in a chat window. Type a command and press Enter to send it.

## Build and run the JAR

From the project root, run the following with Java 25:

```text
gradlew.bat clean shadowJar
java -jar build/libs/victoria.jar
```

On macOS or Linux, use `./gradlew clean shadowJar` instead. The executable JAR is created at `build/libs/victoria.jar`.

## Data

Victoria saves tasks automatically in `data/victoria.txt` and restores them when it starts again. Keep this file if you want to retain your task list when moving to another computer.
