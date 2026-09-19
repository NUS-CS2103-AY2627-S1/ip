# Eli

Eli is a bilingual desktop task companion. It responds in English and Chinese
while helping users manage todos, deadlines, and events.

See the [User Guide](docs/README.md) for the available commands.

## Setting up in IntelliJ IDEA

Prerequisites: JDK 25 and a recent version of IntelliJ IDEA.

1. In IntelliJ IDEA, select **File → Open** and choose this project folder.
1. Configure the project SDK as JDK 25 and leave the language level as
   **SDK default**.
1. Open `src/main/java/eli/Launcher.java` and run `Launcher.main()`.

Keep `src/main/java` as the Java source root because Gradle relies on this
standard directory layout.

## Running quality checks

Run the following command from the project root:

```shell
./gradlew clean test check
```

This compiles the app, runs its automated tests, and checks the Java coding
style.
