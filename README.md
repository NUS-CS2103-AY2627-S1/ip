# Puyo

Puyo is a penguin-themed desktop task manager built with Java and JavaFX. Type commands in its chat interface to keep track of tasks, deadlines, and events.

This repository contains my CS2103T individual project (iP).

## Features

- Add todos, deadlines, and events.
- List tasks and mark them as completed or incomplete.
- Find tasks by a keyword in their description.
- View deadlines and events for a particular date, including multi-day events.
- Delete tasks you no longer need.
- Save tasks locally and load them when Puyo starts.

## Using Puyo

1. Install **Java 25** and check that `java -version` reports version 25.
2. Download `puyo.jar` from the [latest release](https://github.com/fikoww/ip/releases/latest).
3. Put the JAR in a folder where you want to keep your tasks. Open a terminal in that folder and run:

   ```bash
   java -jar puyo.jar
   ```

Enter `todo read chapter 6`, then `list` to see your first task. Press Enter or click **Send** after each command.

See the [User Guide](docs/README.md) for all commands, date formats, examples, and troubleshooting. Puyo saves tasks to `data/puyo.txt` relative to the folder from which it is launched; use the same folder each time to load the same tasks.

## Building from source

Use **JDK 25**. Clone or download this repository and open a terminal in its root folder. The included Gradle wrapper downloads Gradle and dependencies on the first build, so an internet connection is required for that build.

On Windows PowerShell:

```powershell
java -version
.\gradlew.bat run
```

On macOS or Linux:

```bash
java -version
./gradlew run
```

In IntelliJ IDEA, open the project folder as a Gradle project and set both the **Project SDK** and **Gradle JVM** to JDK 25. The GUI entry point is `puyo.Launcher`.

### Tests and packaging

| Task | Windows PowerShell | macOS / Linux |
| --- | --- | --- |
| Run automated tests | `.\gradlew.bat test` | `./gradlew test` |
| Run tests and Checkstyle | `.\gradlew.bat check` | `./gradlew check` |
| Build the runnable JAR | `.\gradlew.bat clean shadowJar` | `./gradlew clean shadowJar` |

The build produces `build/libs/puyo.jar`, containing the application and its dependencies. Copy it to an empty folder and run `java -jar puyo.jar` there to check that it starts and saves tasks without relying on files in the repository. Test the same JAR on the other operating systems you intend to support before releasing it.

## Project structure

| Location | Contents |
| --- | --- |
| `src/main/java/puyo/` | Application coordination and JavaFX GUI classes. |
| `src/main/java/parser/` | Command and date parsing. |
| `src/main/java/command/` | Command implementations. |
| `src/main/java/task/` | Task models and task-list operations. |
| `src/main/java/storage/` | Loading and saving task data. |
| `src/main/java/ui/` | Text input and response formatting. |
| `src/main/resources/` | FXML layouts, CSS, and avatar images. |
| `src/test/java/` | Automated tests. |
| `docs/` | User Guide and product screenshot. |

## Acknowledgements

### Libraries and development tools

- [JavaFX / OpenJFX](https://openjfx.io/) is used to build Puyo's graphical user interface.
- [JUnit](https://junit.org/) is used for automated testing.
- [Shadow Gradle Plugin](https://gradleup.com/shadow/) is used to package Puyo and its dependencies into a runnable JAR.
- [Checkstyle](https://checkstyle.org/) is used to check compliance with coding standards.

### Images

- Penguin avatar (`puyo.png`): [source page](https://storage.googleapis.com/dskaigdjhfmhqe/apparel-with-penguin-logo.html).
- User avatar (`user.png`): [black-and-white boy illustration on Magnific](https://www.magnific.com/premium-vector/black-white-boy-illustration-doodle-artwork_176795745.htm).

These sources are also credited beside the GUI screenshot in the User Guide.

### AI assistance

[fikoww](https://github.com/fikoww) used ChatGPT to help explain the codebase and course requirements, prepare the README and User Guide, and implement improvements to command parsing, date validation, and schedule filtering with related tests. This disclosure covers the assistance used for this finalization work.
