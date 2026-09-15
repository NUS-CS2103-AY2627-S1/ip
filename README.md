# GOAT

```
  ____   ___      _     _____
 / ___| / _ \    / \   |_   _|
| |  _ | | | |  / _ \    | |
| |_| || |_| | / ___ \   | |
 \____| \___/ /_/   \_\  |_|
```

GOAT is a chatbot that keeps track of your tasks. It runs as a JavaFX window, and the
same commands also work in a terminal. It is the individual project (iP) for CS2103/T,
grown one increment at a time from the course's project template.

## Features

| Command | What it does | Example |
|---|---|---|
| `todo DESCRIPTION` | Adds a task with no date | `todo borrow book` |
| `deadline DESCRIPTION /by WHEN` | Adds a task that is due at some point | `deadline return book /by 2026-09-18` |
| `event DESCRIPTION /from START /to END` | Adds a task that spans a period | `event project meeting /from 2026-09-18 /to 2026-09-19` |
| `list` | Shows every task, numbered, with its status | `list` |
| `mark N` | Marks task `N` as done | `mark 2` |
| `unmark N` | Marks task `N` as not done | `unmark 2` |
| `delete N` | Removes task `N` | `delete 3` |
| `on DATE` | Shows the dated tasks falling on `DATE` | `on 2019-10-15` |
| `find KEYWORD` | Shows the tasks whose description contains `KEYWORD` | `find book` |
| `bye` | Ends the conversation | `bye` |

Tasks are shown with a type marker and a status marker, so `[D][X] return book (by: Dec
02 2019)` is a completed deadline. Dates are written as `yyyy-mm-dd`, as in `/by
2019-12-02`, and are shown back in the friendlier `MMM dd yyyy` form.

Unrecognised input is reported and the conversation continues; GOAT does not exit on bad
commands.

## Prerequisites

JDK 25. On macOS, use the [course-prescribed Zulu JavaFX JDK 25](https://se-education.org/guides/tutorials/javaInstallationMac.html).
The JAR also bundles the course JavaFX dependencies for Windows, macOS and Linux. Check yours with:

```bash
java -version
```

## Running with Gradle

The Gradle wrapper is included, so Gradle itself does not need to be installed. The first
run downloads it.

```bash
./gradlew run
```

This opens the GOAT window. To compile, run the tests and assemble everything:

```bash
./gradlew build
```

## Building a runnable JAR

```bash
./gradlew shadowJar
```

This produces `build/libs/goat.jar`, which bundles its dependencies and runs on its own:

```bash
java -jar goat.jar
```

GOAT saves to `data/goat.txt` relative to the folder it is run from, so running the JAR
in a new folder starts a fresh list there.

## Running the text interface

The chatbot behind the window also runs in a terminal, which is how `text-ui-test`
exercises it. Compile through Gradle so that the JavaFX dependencies are on the
classpath, then start `Goat` rather than `Launcher`:

```bash
./gradlew compileJava
java -cp build/classes/java/main seedu.goat.Goat
```

## Running the tests

Run `./gradlew check` for the JUnit suite and Checkstyle checks.
[GitHub Actions](https://github.com/iians0n/ip/actions/workflows/verify.yml)
runs these checks on Windows and Linux with Java 25. It also downloads the
public release and checks its digest, commands, persistence, damaged-file
protection, and GUI process startup in temporary directories. This automated
startup check does not replace visual or interactive GUI testing.

`text-ui-test/runtest.sh` feeds `input.txt` to GOAT and compares the output against
`EXPECTED.TXT`, printing a diff of anything that changed:

```bash
./text-ui-test/runtest.sh
```

When an increment changes the output on purpose, review the diff and then update the
baseline with `./text-ui-test/runtest.sh --bless`.

## Setting up in an IDE

**VS Code:** install the Extension Pack for Java, open this folder, and point the Java
extension at your JDK 25 installation.

**IntelliJ:** open the project, set the SDK to **JDK 25** and the **Project language
level** to `SDK default`, then run `src/main/java/seedu/goat/Launcher.java`.

Classes are organised under `src/main/java/seedu/goat`, split into `command`,
`parser`, `storage`, `task` and `ui` subpackages. The window's layout and
stylesheets live alongside them in `src/main/resources`.

**Warning:** keep `src/main/java` as the root folder for Java files. Do not rename those
folders or move Java files outside that path, as tools such as Gradle expect to find them
there.

## Acknowledgements

This project started from the CS2103/T `ip` project template
([NUS-CS2103-AY2627-S1/ip](https://github.com/NUS-CS2103-AY2627-S1/ip)).

I used **Claude Code**, Anthropic's command-line coding agent, while building this
project. It was used to write and refactor the code for the increments in this
repository, working from the course's increment specifications and under my direction.
The course permits AI-assisted work at levels AI-2 to AI-5.


For downloads, commands, date rules and storage recovery, see the
[GOAT User Guide](https://iians0n.github.io/ip/).

For Week 6, **iians0n (Anson Ng)** used **OpenAI Codex** extensively to audit
requirements, implement error handling and storage protection, expand JUnit
tests, improve GUI readability, update documentation, and run build and
submission checks. The changes were produced in response to the owner's
instructions; this acknowledgement does not imply independent manual review
of every generated line by the owner.

The project uses **OpenJFX**, **JUnit Jupiter**, **Checkstyle**, **Gradle**, and
the **Gradle Shadow plugin**, following the course tutorials and tooling.
The JavaFX launcher, FXML layout and dialog pattern follow the
[SE-EDU JavaFX tutorial](https://se-education.org/guides/tutorials/javaFxPart1.html).
Course material reuse is exempt from per-block attribution under the course
policy; no external code snippets were copied during this Week 6 update.
The earlier avatar photographs had no recorded source/license and have been
removed from the current product. Speakers are distinguished by alignment and announced to assistive tools. The cute goat mascot in
`src/main/resources/images/goat-mascot.png` was generated with OpenAI image
generation through Codex for this project at the owner's request. It is
bundled with the app and paired with a warm cream-and-ember theme.
The dark header and composer arrangement was inspired by a friend's CHOO iP
screenshot supplied by the owner. Its artwork and code were not copied.
