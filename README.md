# Myriad

Myriad is a chatbot that keeps track of your to-dos, deadlines and events. It runs as a JavaFX chat window,
and also has a text-only console mode. Tasks are saved to disk after every change.

Myriad has the personality of a *task painter*: it treats your task list as a canvas and each task as a stroke of
paint, and its GUI takes its colours from the four paints of its hand-painted avatar. Its replies are worded in
`Ui`, and its colours are set in the CSS files under `src/main/resources/css/`.

**Using Myriad?** See the [User Guide](https://ryan24chua.github.io/ip/) for every command, with examples.
This README is for developers: how to build, run and test the project, and how the code is organised.

## Prerequisites

* **JDK 25.** JavaFX 17 is downloaded by Gradle, so it need not be installed separately.
* No Gradle install is needed: use the Gradle wrapper (`./gradlew`, or `gradlew.bat` on Windows).

## Building and running

Launch the GUI straight from the source code:

```
./gradlew run
```

Build a single JAR file that includes all dependencies, at `build/libs/myriad.jar`:

```
./gradlew shadowJar
```

Run that JAR as the GUI:

```
java -jar build/libs/myriad.jar
```

Or run the same JAR as a console session, which reads commands from the terminal instead:

```
java -cp build/libs/myriad.jar myriad.Myriad
```

Both modes share one data file, `data/myriad.txt`, created in the folder Myriad is run from.

**In IntelliJ IDEA:** open the project folder, set the project SDK to JDK 25
([how](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk)), then run `myriad.Launcher` for the GUI or
`myriad.Myriad` for the console.

## Testing

Run the JUnit 5 tests:

```
./gradlew test
```

* Test report: `build/reports/tests/test/index.html`
* Coverage report (JaCoCo): `build/reports/jacoco/test/html/index.html`
* The tests run with a Chinese (zh-CN) locale, so output that wrongly depends on the computer's language
  fails on every machine. They run from `build/test-working-dir`, so no test touches the real `data/` folder.

Run the tests plus Checkstyle, which enforces the SE-EDU Java coding standard
(rules in `config/checkstyle/checkstyle.xml`):

```
./gradlew check
```

GitHub Actions runs `./gradlew check` on Ubuntu, macOS and Windows for every push and pull request
(`.github/workflows/gradle.yml`).

## Project structure

| Path | Contents |
|------|----------|
| `src/main/java/myriad/` | `Myriad` (the chatbot, and the console entry point) and `Launcher` (the GUI entry point) |
| `src/main/java/myriad/command/` | One class per command, e.g. `AddCommand`, `FindCommand`, `StatsCommand` |
| `src/main/java/myriad/parser/` | `Parser`, which turns a line of input into a command |
| `src/main/java/myriad/task/` | Task types (`ToDo`, `Deadline`, `Event`), `TaskList` and date handling (`TaskDateTime`) |
| `src/main/java/myriad/storage/` | `Storage`, which saves and loads the data file |
| `src/main/java/myriad/ui/` | Console output (`Ui`) and the JavaFX GUI (`MainWindow`, `DialogBox`, `CommandHistory`) |
| `src/main/resources/` | FXML layouts, CSS styles and images for the GUI |
| `src/test/java/myriad/` | JUnit tests, mirroring the main package structure |
| `docs/` | The User Guide, published with GitHub Pages |

## AI assistance

This project was developed with the help of AI, as cited below.

* **Tool:** [Claude Code](https://claude.com/claude-code), using Claude Opus models.
* **Used by:** Ryan Chua.
* **Extent of use:**
  * Used throughout the project as a helper rather than as the main author: explaining concepts and errors,
    suggesting designs and refactorings, reviewing code against the SE-EDU coding standard, and drafting
    tests, Javadoc comments and commit messages. All suggestions were reviewed, and edited where needed,
    before they were kept.
  * Files mostly written with Claude Code say so in a comment at the top of the file (e.g. the JavaFX GUI
    classes in `src/main/java/myriad/ui/`).
