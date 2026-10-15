# Nova

Nova is a JavaFX chatbot for managing todos, deadlines, events, and tags.

## Setting up in IntelliJ IDEA

Prerequisite: JDK 25.

1. Open IntelliJ IDEA and select `File` > `Open`.
2. Select this project directory and accept the default prompts.
3. Configure the project to use **JDK 25** as described [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).
   Set **Project language level** to `SDK default`.
4. Open `src/main/java/nova/gui/Launcher.java`, right-click it, and select
   `Run 'Launcher.main()'`.

You can also run Nova from the project root with Gradle:

```text
.\gradlew run
```

**Warning:** Keep `src/main/java` as the Java source root. Do not rename this
folder or move Java files outside it, because development tools such as Gradle
expect this standard project layout.

## Documentation

See the [full User Guide](docs/README.md) for Nova's commands and usage.
