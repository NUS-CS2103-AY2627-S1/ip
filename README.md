# Megia

Megia is a Java 25 task manager with a command-line interface and a JavaFX
chatbot interface. It supports todos, deadlines, and events, with localized and
corrective error messages for invalid input.

## Features

- Add todos, deadlines, and events
- List stored tasks
- Mark and unmark tasks
- English and Chinese localization
- A JavaFX chatbot transcript with responsive task cards
- User-selectable profile image with local persistence
- Specific error messages that explain how to correct invalid commands

## Commands

| Command | Example |
|---|---|
| `help` | `help` |
| `todo <description>` | `todo borrow a book` |
| `deadline <description> /by <date and time>` | `deadline submit report /by 2019-12-02 1800` |
| `event <description> /on <date> /from <time> /to <time>` | `event meeting /on 2019-12-02 /from 1400 /to 1600` |
| `event <description> /from <date and time> /to <date and time>` | `event conference /from 2019-12-02 1400 /to 2019-12-03 1600` |
| `list` | `list` |
| `list <date>` | `list 2/12/2019` |
| `find <query>` | `find borrow a book` |
| `mark <task number>` | `mark 1` |
| `unmark <task number>` | `unmark 1` |
| `delete <task number>` | `delete 1` |
| `bye` | `bye` |

Dates use `YYYY-MM-DD` or day-first `D/M/YYYY`; times use 24-hour `HHmm`.
Deadlines and event endpoints are persisted as ISO local date-times.
Task descriptions may contain commas and quotation marks, but not line breaks.

`list <date>` shows deadlines on that date and events spanning that calendar
date. Todos do not appear in date-filtered lists, and task numbers remain the
same as in an unfiltered list.

`find <query>` lists every task whose description contains the query. Matching
is literal and case-sensitive; spaces and special characters are supported, and
task numbers remain the same as in an unfiltered list.

## Requirements

- JDK 25
- A recent version of IntelliJ IDEA, or a terminal with Java configured

## Running in IntelliJ IDEA

1. Open the repository in IntelliJ IDEA.
2. Configure the project SDK and language level to Java 25.
3. Open `src/main/java/megia/Megia.java`.
4. Run `Megia.main()`.

Keep `src/main/java` as the Java source root.

## Running from the terminal

```bash
javac --release 25 -d out $(find src/main/java -name '*.java')
java --class-path out:src/main/resources megia.Megia
```

## Running the JavaFX chatbot

Use the Gradle `runGui` task from a Java 25 environment:

```bash
./gradlew runGui
```

The chatbot opens a scrollable conversation. Enter the same commands listed
above, or use a starter action. Select the command-help action or enter `help`
to see the complete command and date/time guide in the active language. The
language selector switches between English
and Chinese immediately and remembers the choice through Java Preferences. The
profile-image button accepts a local PNG, JPG, GIF, or BMP image.

## Running the standalone GUI JAR

Build the distributable fat JAR with Java 25:

```bash
./gradlew shadowJar
```

The output is `build/libs/megia.jar`. Launch it from any working directory with:

```bash
java -jar /path/to/megia.jar
```

The JAR uses `megia.ui.GuiLauncher` as its entry point and includes the JavaFX
classes and native libraries for these Java 25 platforms:

- Windows x86-64
- macOS x86-64 and ARM64
- Linux x86-64 and ARM64

The application stores `task_storage.csv` in the current working directory, so
running the JAR outside the repository keeps task data outside the checkout.
The packaged native libraries are included for these architectures; GUI
execution evidence on each platform is tracked separately.

## Running the console tests

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py \
  --plan test/ui-test-plan.md
```

The test runner compiles the project using Java 25 and executes each console
test in a fresh process.

## Running the verification suite

Run the standard unit tests and Checkstyle with:

```bash
./gradlew check
```

On a desktop-capable environment, run the JavaFX smoke test separately:

```bash
./gradlew guiTest
```

## Localization

The initial language comes from `src/main/resources/application.properties`:

```properties
language=en
```

Use `en` for English or `cn` for Chinese. After the first GUI or console run,
the shared Java Preferences value takes precedence. Missing, invalid, or
unavailable preferences fall back to English.

## Scope boundaries

Megia uses the deterministic command language shown in the command table. The
chatbot does not provide free-form natural-language intent recognition, remote
AI, voice input, streaming responses, persistent chat transcripts, cloud sync,
themes, or a calendar dashboard.

## AI use declaration
- Coding harness/agent: Codex CLI
- Used to discuss error-planning architecture and plan increment Level-5
  - Used to review and suggest improvements to iterate on Level-5 requirements
- Used to give flavour to chatbot responses
- Used to review and tweak JavaDoc comments
- Used to expand the console UI tests with `test-ui` skill
