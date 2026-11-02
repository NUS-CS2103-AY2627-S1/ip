# AGENTS.md

## Project overview

Megia is a single-module Java 25 task manager. It supports todos, deadlines,
events, task search, date-filtered listing, completion state, deletion, and
English/Chinese localized messages.

The application has two user interfaces that share the same command pipeline:

```text
ConsoleUi / JavaFX MainController
        -> CommandExecutor
        -> TaskParser and TaskService
        -> TaskStorage and LocalStorageService
        -> task_storage.csv
```

Important entry points:

- Console application: `megia.Megia`
- JavaFX application: `megia.ui.GuiLauncher`

The repository is not a monorepo and does not require nested `AGENTS.md` files.

## Repository layout

- `src/main/java/megia/model/`: task models and in-memory storage types.
- `src/main/java/megia/service/`: parsing, command execution, task operations,
  localization, properties, and file storage.
- `src/main/java/megia/ui/`: console UI.
- `src/gui/java/megia/ui/`: JavaFX UI and controllers.
- `src/main/resources/`: application properties, localization bundles, FXML,
  and CSS resources.
- `src/test/java/`: JUnit 5 unit and service tests.
- `test/ui-test-plan.md`: scripted console UI test cases.
- `config/checkstyle/`: Checkstyle configuration and suppressions.
- `docs/agents/`: issue-tracker, triage-label, and domain-documentation rules.

Before work involving issues or domain terminology, read the relevant files in
`docs/agents/`. Also check for a root `CONTEXT.md` and ADRs under `docs/adr/`
when they exist.

## Environment and setup

- Use JDK 25 for compilation, tests, and application runs. Verify with:
  `java --version` and `javac --version`.
- In IntelliJ IDEA, set both the project SDK and language level to Java 25.
- On macOS with SDKMAN, use `sdk use java 25.0.3.fx-zulu` when that JDK is
  installed.
- Use the Gradle wrapper supplied by the repository rather than relying on a
  system Gradle installation.
- Dependencies are resolved from Maven Central. JavaFX 25 and JUnit 5 are
  configured in `build.gradle`.

## Development commands

Run these from the repository root. On Windows use `gradlew.bat`; on Unix-like
systems use `./gradlew`.

```text
# Start the console application
gradlew.bat run
./gradlew run

# Start the JavaFX application
gradlew.bat runGui
./gradlew runGui

# Compile and run JUnit 5 tests
gradlew.bat test
./gradlew test

# Run verification, including configured Checkstyle and tests
gradlew.bat check
./gradlew check

# Run Checkstyle directly when focusing on style violations
gradlew.bat checkstyleMain checkstyleTest
./gradlew checkstyleMain checkstyleTest

# Build the distributable fat JAR at build/libs/megia.jar
gradlew.bat shadowJar
./gradlew shadowJar
```

The console application reads commands from standard input. The JavaFX task
requires a graphical environment. `build/`, `.gradle/`, `out/`, `bin/`, and
other generated files are local build artifacts and must not be committed.

## Console UI tests

The scripted tests compile with Java 25, run each case in an isolated temporary
working directory, and compare normalized output with
`test/ui-test-plan.md`.

```text
python3 .codex/skills/test-ui/scripts/run_ui_tests.py --plan test/ui-test-plan.md
```

On Windows, `py` may be used in place of `python3` when it selects Python 3.
When changing interactive behavior, update the test plan and run the complete
script. If a case fails, investigate that first; do not treat later cases as
validated.

## Supported commands and behavior

- `todo <description>` adds a todo.
- `deadline <description> /by <date and time>` adds a deadline.
- `event <description> /on <date> /from <time> /to <time>` adds a same-day
  event.
- `event <description> /from <date and time> /to <date and time>` adds an
  event with complete endpoints.
- `list` lists all tasks; `list <date>` lists deadlines on that date and
  events spanning that calendar date. Todos are excluded from date-filtered
  results, while displayed task IDs remain the original IDs.
- `find <query>` performs a literal, case-sensitive description search.
- `mark <task number>`, `unmark <task number>`, and `delete <task number>`
  mutate an existing task.
- `bye` exits the application and triggers final persistence.

Dates accept `YYYY-MM-DD` or day-first `D/M/YYYY`. Times use 24-hour `HHmm`.
Date and time values are parsed strictly, and deadline/event endpoints are
stored as ISO local date-times.

## Persistence and localization

- `src/main/resources/application.properties` controls configuration.
- The default storage path is `./task_storage.csv`.
- `task_storage.csv` is intentionally ignored by Git because it is local user
  data. Do not commit it or use it as a fixture for tests.
- Persistence uses a comma-delimited format and currently assumes task
  descriptions do not contain commas. Change the storage format and its tests
  together if that limitation must be removed.
- Set `language=en` or `language=cn` in `application.properties` to select
  localized messages. English is the fallback when configuration cannot be
  loaded.

## Code style and implementation guidance

- Follow the Checkstyle rules in `config/checkstyle/checkstyle.xml`.
- Add explanatory Javadoc to every class and to nontrivial public methods and
  fields. Keep generated code self-explanatory and comment only where the
  behavior or design is not obvious.
- Keep Java production code in the existing source roots and tests in
  `src/test/java`. Preserve the package structure under `megia`.
- Route new command behavior through the shared `CommandExecutor`, parser, and
  service layers so console and JavaFX interfaces remain consistent.
- Prefer the simplest design that satisfies the requirements. Preserve
  rollback behavior when a mutation cannot be persisted.
- Add or update focused JUnit tests for service/model behavior and update the
  console UI plan for user-visible command behavior.
- Do not introduce a new dependency or change the storage wire format without
  updating the relevant build configuration, documentation, and tests.

## Course and student context

This repository is a starter project for an introductory software engineering
course in an undergraduate computer science program. Assume the contributor is
a student with basic Java and OOP knowledge and strong IntelliJ IDEA
familiarity.

When explaining significant actions, briefly explain what was changed and why.
Keep guidance instructional and concise. Do not do work that has not been
requested, and do not commit or push changes unless explicitly asked.

## Git and contribution workflow

- Check `git status` before and after changes so unrelated work is preserved.
- Follow the repository's existing branch and commit conventions when Git work
  is explicitly requested.
- Use lightweight tags unless an annotated tag is specifically requested.
- Before proposing or creating a commit, branch, or tag, read and follow
  `.codex/skills/seedu-git-standard/SKILL.md`.
- Before changing or reviewing Java source or tests, read and follow
  `.codex/skills/seedu-java-coding-standard/SKILL.md` and audit every Java file
  in scope before handing the work back.
- Keep secrets, local configuration, generated output, and local task data out
  of Git.
