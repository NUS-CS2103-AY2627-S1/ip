# Nudge

![Nudge task manager showing deadlines sorted by date](docs/Ui.png)

Nudge is a friendly desktop task assistant that helps you keep track of todos,
deadlines, and events through a simple command-based interface. It saves your tasks
automatically and responds with concise, encouraging feedback.

[Read the User Guide](https://advaitio.github.io/ip/) |
[Download the latest release](https://github.com/advaitio/ip/releases/latest)

## Features

- Add todos, deadlines, and events.
- List, mark, unmark, and delete tasks.
- Find tasks by description keyword.
- Sort deadlines chronologically.
- Preserve tasks between sessions in a human-readable data file.
- Use the same task-management features through the JavaFX or console interface.

## Quick start

Nudge requires JDK 25.

1. Download `nudge.jar` from the
   [latest release](https://github.com/advaitio/ip/releases/latest).
1. Place the JAR in the folder from which you want to run Nudge.
1. Open a terminal in that folder and run:

   ```shell
   java -jar nudge.jar
   ```

Nudge stores your tasks in `data/nudge.txt`, relative to the folder from which you
launch the application. See the [User Guide](https://advaitio.github.io/ip/) for the
complete command reference and examples.

## Developer setup

Prerequisites: JDK 25 and a recent version of IntelliJ IDEA.

1. Open the repository folder in IntelliJ IDEA.
1. Configure the project to use **JDK 25** and set the project language level to
   **SDK default**.
1. Run the JavaFX interface from the terminal:

   ```shell
   ./gradlew run
   ```

Useful development commands:

```shell
./gradlew check
./gradlew shadowJar
```

`check` runs the automated tests and Checkstyle. `shadowJar` creates the executable
fat JAR at `build/libs/nudge.jar`.

The console interface is retained for automated UI testing. To run it directly from
IntelliJ IDEA, run the `nudge.Nudge` main class in
`src/main/java/nudge/Nudge.java`.

Keep `src/main/java` as the source root. Some project tools, including Gradle and the
course grading scripts, expect the Java source files to remain under this path.

## Acknowledgements

- OpenAI Codex was used to assist with the implementation, testing, debugging, and
  documentation of this project. All suggestions were reviewed, adapted, and tested
  by the author.
