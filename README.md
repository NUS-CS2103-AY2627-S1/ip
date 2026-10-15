# Windy

Windy is a JavaFX task manager with a chat-style interface. Use commands to add to-dos, deadlines, and events; track their completion; search tasks; and undo or redo a change. Tasks are saved between runs.

![Windy desktop interface](docs/Ui.png)

See the [User Guide](docs/README.md) for commands and examples.

## Requirements

- JDK 25
- An up-to-date version of IntelliJ IDEA, if running from the IDE

## Run Windy

From the project root, with JDK 25 selected:

```bash
./gradlew run
```

On Windows (PowerShell):

```powershell
.\gradlew.bat run
```

In IntelliJ IDEA, open the project directory, configure the project SDK as JDK 25, then run the `main` method in `src/main/java/windy/Launcher.java`. The Gradle application entry point is also `windy.Launcher`.

Type a command into Windy's input box and press Enter or click **Send**. For example, `todo read a book` adds a to-do. Windy stores tasks in `data/windy.txt` relative to the directory from which it is run; the file is created automatically when a task changes. Avoid editing this file while Windy is open.

The console interface is available separately through `windy.Windy`; the normal Gradle `run` task starts the GUI.

## Build and test

Run the Gradle build (including JUnit tests and Checkstyle) with JDK 25:

```bash
./gradlew --no-daemon build
```

On Windows (PowerShell):

```powershell
.\gradlew.bat --no-daemon build
```

The [CI workflow](.github/workflows/ci.yml) runs this build on every push and pull request, and can also be started from GitHub Actions. It checks Linux, Windows, and macOS. Console UI test plans and interactive GUI tests are not run by CI.
