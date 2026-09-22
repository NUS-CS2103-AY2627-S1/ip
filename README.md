# Stewie

Stewie is a local task manager built with Java 25 and JavaFX. Manage to-dos,
deadlines, and events through a chat-style desktop interface or the command line.
Tasks are saved automatically and restored when you next open the application.

Developed as an individual project for an introductory software engineering course.

## Features

- **Three task types:** to-dos, deadlines, and events with start and end dates.
- **Task management:** list, search, edit, complete, reopen, and delete tasks.
- **Desktop interface:** Chat, My List, and Help views with interactive task cards.
- **Console interface:** manage tasks using the same core commands in a terminal.
- **Local persistence:** UTF-8 storage with duplicate detection, validation, and
  read-only recovery when saved data cannot be loaded safely.

## Getting started

### Requirements

- **JDK 25** for building and running the application.
- Internet access for the first Gradle build to download Gradle and dependencies.
- A graphical desktop environment when using the JavaFX interface.

The repository includes the Gradle Wrapper; a separate Gradle installation is
not required. Run the commands below from the repository root.

If you use SDKMAN and have the project's Zulu JDK installed, select it with:

```sh
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk use java 25.0.3.fx-zulu
```

Confirm that `java -version` reports Java 25. On Windows, set `JAVA_HOME` to your
JDK 25 installation and use `gradlew.bat` in place of `./gradlew`.

### Run the application

Start the desktop interface:

```sh
./gradlew run
```

Use **Chat** to enter commands, **My List** to manage unfinished and completed
tasks, and **Help** to view the command reference.

Start the console interface:

```sh
./gradlew --console=plain runCli
```

### Set up IntelliJ IDEA

1. Open the repository directory as a project and import the Gradle build.
2. Set the **Project SDK** and **Gradle JVM** to JDK 25.
3. Set the project language level to **SDK default** and reload the Gradle project.
4. Run the Gradle `run` task for the desktop interface or `runCli` for the console.

## Usage

Try this sequence with an empty task list:

```text
todo read chapter 3
deadline submit report /by 2026-09-25
event project workshop /from 2026-09-26 /to 2026-09-27
list
mark 1
update 2 d/2026-09-28
find report
```

This creates one task of each type, completes the reading task, moves the report
deadline, and searches for the report.

### Command reference

Replace `<...>` with your own values. Fields in `[...]` are optional; do not type
the brackets. Task numbers start at **1**. Use `list` to check the current full
list before changing a task, especially after a deletion or search.

| Action | Command | Example |
| --- | --- | --- |
| Add a to-do | `todo <description>` | `todo read chapter 3` |
| Add a deadline | `deadline <description> /by <date>` | `deadline submit report /by 2026-09-25` |
| Add an event | `event <description> /from <date> /to <date>` | `event workshop /from 2026-09-26 /to 2026-09-27` |
| List all tasks | `list` | `list` |
| Search tasks | `find <keyword> [more keywords]` | `find report workshop` |
| Complete a task | `mark <number>` | `mark 1` |
| Reopen a task | `unmark <number>` | `unmark 1` |
| Delete a task | `delete <number>` | `delete 1` |
| Edit a task | `update <number> [description] [d/<date>] [from/<date>] [to/<date>]` | `update 2 d/2026-09-28` |
| Show desktop help | `help` | `help` |
| Say goodbye | `bye` | `bye` |

`help` is available in the desktop interface. `bye` exits the console application;
in the desktop interface, it displays a farewell. Close the window to exit.

Updates require at least one replacement field. Omitted fields and completion
status remain unchanged. Use `d/` or its alias `by/` for deadline tasks, and
`from/` or `to/` for events. To-dos support description changes only. Creation
commands use `/by`, `/from`, and `/to`; update commands use `d/`, `by/`, `from/`,
and `to/`. Supply each field once, with `/from` before `/to` when creating events.

### Dates and input rules

Supported date formats include:

```text
2026-09-25
25/9/2026
25-9-2026
25.9.2026
25 Sep 2026
25 September 2026
Sep 25, 2026
September 25, 2026
```

Dates must be real calendar dates in years 0001–9999. Times are not supported,
and an event's end date must be later than its start date.

Command input is converted to lowercase, including descriptions, and repeated
whitespace is collapsed. Search returns tasks containing any supplied keyword.
Descriptions may contain ordinary punctuation but cannot contain `|` or control
characters. Tasks with the same type, description, and dates are duplicates,
regardless of completion status, description casing, or repeated whitespace.

## Data storage and recovery

Stewie stores tasks in `data/stewie.txt`, relative to the directory from which the
application is launched. Launch from the same directory to use the same task
file. A missing file is created on the first successful change.

| Situation | Behavior and recovery |
| --- | --- |
| Damaged or duplicate records | Stewie reports the affected line numbers and makes valid tasks available read-only. Back up the file, repair those records, and restart. |
| Unreadable file or invalid UTF-8 | Stewie opens read-only. Check the file path, permissions, and encoding, then restart. |
| Failed save | The current task list and previous saved file remain unchanged. Check write permissions, free space, and filesystem support for atomic file replacement, then retry. |
| File changed outside Stewie | Further changes are rejected. Restart to load the updated file before editing tasks. |

## Build and test

Build the application and run the standard checks with Java 25:

```sh
./gradlew build
```

Create the application JAR with its dependencies:

```sh
./gradlew shadowJar
```

The output is **`build/libs/stewie.jar`**, a single distribution file for all
the supported desktop platforms below. Upload this file to your release; users
do not need to choose an OS-specific download.

| Operating system | Supported Java 25 architectures |
| --- | --- |
| Windows | x64 (Intel/AMD) |
| macOS | x64 (Intel) and ARM64 (Apple Silicon) |
| Linux, including Ubuntu | x64 and ARM64 on a glibc-based graphical desktop |

Copy the JAR into a writable folder and launch it with:

```sh
java -jar stewie.jar
```

JavaFX and the icon dependencies for all five targets are bundled. A separate
JavaFX SDK, module-path flags, and runtime downloads are unnecessary. Java 25
must already be installed. Linux still needs the desktop libraries described
below. Native Windows ARM64, 32-bit JVMs, and musl-based Linux are unsupported.

The universal JAR stores identical files once, alongside five platform overlays.
The build enforces a 12,000,000-byte limit. The launcher combines shared files
with the matching overlay in a `.stewie-runtime-*` directory inside the folder
you launched from, then starts it with the same Java installation. This preserves
platform-specific JavaFX classes and keeps Intel and ARM native libraries separate.
The runtime directory is removed after normal exit; task data remains in
`data/stewie.txt`. The launch folder must be writable. Startup needs no network
connection and starts a second Java process internally.

Run `python3 test/check-universal-jar.py` to inspect all bundled runtimes, then
follow the [universal startup checks](test/ui-test-plan.md#universal-release-startup-checks)
on each supported platform. `./gradlew build` also creates the universal JAR.
The smaller platform-specific JARs below remain available as optional downloads.

### Windows releases

Build the Windows x64 distribution on macOS, Linux, or Windows with Java 25:

```sh
./gradlew windowsJars
```

In Windows PowerShell, use `.\gradlew.bat windowsJars` instead. The full `build`
task also creates `build/libs/stewie-windows-x64.jar`.

Copy **stewie-windows-x64.jar** to a writable folder on the Windows computer.
Open PowerShell in that folder, confirm `java -version` reports an **x64 Java 25**
installation, and launch:

```powershell
java -jar .\stewie-windows-x64.jar
```

This JAR contains Windows JavaFX classes and x64 native DLLs, so a separate
JavaFX SDK and module-path flags are unnecessary. Use it for Intel/AMD x64
Windows computers; it is not a native Windows ARM64 distribution.
You may rename it to `stewie.jar` and use `java -jar .\stewie.jar`.
Continue launching from the same folder to retain access to `data/stewie.txt`.

The current universal `build/libs/stewie.jar` also works as the Windows download.
Older host-only builds of `stewie.jar` contain only the builder's JavaFX libraries.
If an older release
reports "JavaFX runtime components are missing", replace it with the new
Windows JAR. The release manifest uses `stewie.ui.gui.Launcher`, a plain Java
entry point that starts the bundled JavaFX application.

Before distribution, run `python3 test/check-windows-jar.py` (Windows:
`py -3 test/check-windows-jar.py`) and the
[Windows release startup checks](test/ui-test-plan.md#windows-release-startup-checks).
Archive checks on another OS do not replace a Windows GUI smoke test.

### Linux releases

Build both Linux distributions from any supported build machine, including macOS:

```sh
./gradlew linuxJars
```

`./gradlew build` also creates these JARs. Distribute the file matching the Linux
machine's architecture (`uname -m`):

| Linux architecture | Release file | Launch command |
| --- | --- | --- |
| `x86_64` (Intel/AMD) | `build/libs/stewie-linux-x64.jar` | `java -jar stewie-linux-x64.jar` |
| `aarch64` (ARM64) | `build/libs/stewie-linux-aarch64.jar` | `java -jar stewie-linux-aarch64.jar` |

Both include JavaFX 25; installing a separate JavaFX SDK or configuring a module
path is unnecessary. You can rename the matching JAR to `stewie.jar` if desired.
The current universal `stewie.jar` also bundles both Linux targets; older host-only
macOS builds of `stewie.jar` do not.

Use Java 25 for the same architecture and a glibc-based graphical Linux desktop
with GTK 3.20 or newer and X11 (or XWayland on Wayland).
The bundled ARM64 libraries require glibc 2.35 or newer (as provided by Ubuntu 22.04).
On Ubuntu 22.04, install missing desktop libraries with:

```sh
sudo apt-get update
sudo apt-get install libgtk-3-0 libgl1 libxtst6 libxi6 libxrender1 libxrandr2 fontconfig
```

Other distributions use their equivalent GTK 3, X11, OpenGL, and font packages.
The [JavaFX Linux requirements](https://openjfx.io/highlights/25/) still apply:
bundling JavaFX cannot support every Linux system. Headless servers without a
display, 32-bit CPUs, and musl-based systems such as stock Alpine are not supported
by these desktop JARs. See the [release checks](test/ui-test-plan.md#linux-release-startup-checks)
before publishing them.

Run JUnit tests, Checkstyle, and coverage reports:

```sh
./gradlew check jacocoTestReport jacocoCoreReport
```

Run the ordered console regression plan:

```sh
python3 test/run-ui-tests.py out/ui-test-session.txt
```

The console runner requires Python 3, a POSIX environment, and the SDKMAN JDK at
`~/.sdkman/candidates/java/25.0.3.fx-zulu`. It runs cases in temporary directories,
preserves your task file, and stops at the first failure. The session record
includes inputs, outputs, comparison results, standard error, and exit statuses.

See the [testing guide](test/README.md) for report locations, coverage scope, and
platform limitations, and the [UI test plan](test/ui-test-plan.md) for expected
console output and manual desktop checks.

## Project structure

```text
src/main/java/stewie/
├── Stewie.java        # Console entry point
├── model/             # Tasks, dates, and task-list operations
├── parser/            # Command parsing and input validation
├── storage/           # Task persistence and recovery
└── ui/
    ├── cli/           # Console output
    └── gui/           # JavaFX application and views
src/main/resources/    # Stylesheets and images
src/test/java/stewie/  # JUnit tests
config/checkstyle/     # Java style rules
test/                  # Test guides and console regression runner
```
