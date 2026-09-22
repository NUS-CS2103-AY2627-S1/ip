# Oz

Oz is a task manager that accepts short text commands. It supports todos,
deadlines, events, keyword and date searches, and weekly recurring events. Tasks
are saved locally in `data/oz.txt` and restored the next time Oz starts.

See the [user guide](docs/README.md) for detailed instructions on recurring
events.

## Prerequisites

- JDK 25
- A recent version of IntelliJ IDEA

## Running Oz

### IntelliJ IDEA

1. Open this repository as an IntelliJ project.
2. Set the project SDK to JDK 25 and the project language level to `SDK default`.
3. Allow IntelliJ to import the Gradle project.
4. Run `oz.Launcher` to start the graphical interface, or run `oz.Oz` to use the
   command-line interface.

### Gradle

On Windows, run:

```powershell
.\gradlew.bat run
```

On macOS or Linux, run:

```bash
./gradlew run
```

The Gradle `run` task starts the command-line interface.

To start the graphical interface on Windows, run:

```powershell
.\gradlew.bat runGui
```

On macOS or Linux, run `./gradlew runGui` instead.

## Building and running the JAR

Build a JAR containing Oz and its runtime dependencies with:

```powershell
.\gradlew.bat shadowJar
```

On macOS or Linux, use `./gradlew shadowJar` instead. The generated JAR is
located at `build/libs/oz.jar`.

Run the JAR using Java 25:

```bash
java -jar build/libs/oz.jar
```

The JAR starts the command-line interface and saves tasks in `data/oz.txt`
relative to the directory from which it is run.

## Commands

| Command | Format | Example |
| --- | --- | --- |
| Add a todo | `todo <description>` | `todo read book` |
| Add a deadline | `deadline <description> /by <date-time>` | `deadline submit report /by 2026-10-02 1800` |
| Add an event | `event <description> /from <start> /to <end>` | `event lecture /from 2026-10-02 1400 /to 2026-10-02 1600` |
| Add a recurring event | `recurring <description> /on <date> /start <time> /end <time> /every <interval> week\|weeks [/until <date>]` | `recurring tutorial /on 2026-10-02 /start 1000 /end 1200 /every 2 weeks` |
| List all tasks | `list` | `list` |
| List tasks on a date | `on <date>` | `on 2026-10-02` |
| Find tasks | `find <keyword>` | `find report` |
| Mark a task | `mark <number>` | `mark 1` |
| Mark a recurring occurrence | `mark <number> /on <date>` | `mark 2 /on 2026-10-16` |
| Unmark a task | `unmark <number>` | `unmark 1` |
| Delete a task | `delete <number>` | `delete 1` |
| Exit | `bye` | `bye` |

Task numbers used by `mark`, `unmark`, and `delete` refer to the numbers shown
by `list`.

### Date and time formats

Dates can use either hyphens (`-`) or slashes (`/`). The supported formats are:

- `yyyy-MM-dd`, for example `2026-10-02`
- `dd-MM-yyyy`, for example `02-10-2026`
- `yyyy/MM/dd`, for example `2026/10/02`
- `dd/MM/yyyy`, for example `02/10/2026`

Times use the 24-hour clock and can be written as `HHmm` or `HH:mm`, for example
`1400` or `14:00`. When a command requires both a date and a time, separate them
with a space, for example `2026-10-02 14:00`.

## Testing and code quality

Run the test suite with:

```powershell
.\gradlew.bat test
```

Generate the JaCoCo coverage report with:

```powershell
.\gradlew.bat test jacocoTestReport
```

The HTML coverage report is generated at
`build/reports/jacoco/test/html/index.html`.

Run Checkstyle with:

```powershell
.\gradlew.bat checkstyleMain checkstyleTest
```

## Acknowledgements and third-party software

This project was created from the
[SE-EDU Duke project](https://github.com/se-edu/duke).

### AI-assisted work

Google Antigravity with Gemini 3.8 Flash and OpenAI Codex with
GPT-5.6 Sol extensively throughout the project. These tools assisted with code
completion, implementation, testing, debugging, refactoring, documentation, and
code-quality reviews. All AI-assisted output was reviewed, adapted, and tested
before being included in the project.

### Libraries and development tools

- [JaCoCo](https://www.jacoco.org/jacoco/) is used to generate test-coverage
  reports. Its use has been approved for this course project.

JUnit, JavaFX, Checkstyle, and Gradle Shadow were included as part of the course
project setup. No other third-party libraries are currently used.

### Image Sources
- [Professor Pig](https://static.wikia.nocookie.net/angrybirds/images/c/ce/Professor_pig_240.png/revision/latest/scale-to-width/360?cb=20130419123316)
- [Pig](https://static.wikia.nocookie.net/angrybirds/images/c/ce/Sp.png/revision/latest?cb=20221015150751)
- [Background](https://preview.redd.it/made-some-angry-birds-trilogy-wallpapers-v0-27txqipna3ig1.png?width=1080&crop=smart&auto=webp&s=ef1319c623782001d22704b94f7a75796aee8220)
