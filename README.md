# Tuesday

Tuesday is a JavaFX task-management chatbot. Add todos, deadlines, and events; find tasks; and track completion through text commands.

See the [user guide](docs/README.md) for commands, date/time formats, and duplicate-task handling.

## Requirements

- JDK 25.
- A graphical desktop for running the app and JavaFX tests.

## Run and check the project

Run these commands from the project directory in PowerShell:

```powershell
.\gradlew.bat run
.\gradlew.bat check
.\gradlew.bat shadowJar
```

`run` opens Tuesday, `check` runs tests and Checkstyle, and `shadowJar` creates `build/libs/tuesday.jar` with its dependencies. Launch that JAR with:

```powershell
java -jar build/libs/tuesday.jar
```

On macOS or Linux, use `./gradlew` in place of `.\gradlew.bat`.

## Set up in IntelliJ IDEA

1. Open this project directory and import the Gradle project.
2. Set the project SDK and Gradle JVM to JDK 25, and use the SDK default language level.
3. Run the Gradle `run` task, or run `Tuesday.main()` in `src/main/java/tuesday/Tuesday.java`.
4. The Tuesday window opens with a text field and Send button.

Keep `src/main/java` and `src/test/java` as the source roots. Classes are grouped under the `tuesday` package, and layouts are in `src/main/resources/view`.

Task changes are saved to `data/Tuesday.txt`, relative to the directory from which you launch the app.
