# Gronk

Gronk is a cheerful, mighty task keeper. Add to the task pile, crush completed
tasks, and hear a signature **GRONK!** with every reply. The GUI uses large
green battle cries, forest-green command bubbles, and distinct red error cards.

Visit the [Gronk product website](https://aston-ish.github.io/ip/) or read the
[User Guide](docs/README.md) for installation, commands, examples,
and troubleshooting. The updated GUI release is awaiting publication; use the
build instructions below for the current app. The existing
[GitHub release](https://github.com/aston-ish/ip/releases) is the earlier console version.

## Build and verify

Use **Java 25**, then run `./gradlew clean test checkstyleMain checkstyleTest shadowJar`
(Windows: `.\gradlew.bat clean test checkstyleMain checkstyleTest shadowJar`).
The distributable is `build/libs/topaz.jar`; start it with `java -jar build/libs/topaz.jar`.
JavaFX GUI tests require a graphical desktop (or Xvfb on Linux).

The command-line test plan is in [test/ui-test-plan.md](test/ui-test-plan.md).
Use a disposable save file when testing. Final verification and outstanding
submission steps are recorded in [test/final-audit.md](test/final-audit.md).

## Credits

This project builds on the [SE-EDU Duke starter](https://github.com/se-edu/duke)
and its [JavaFX tutorial](https://se-education.org/guides/tutorials/javaFx.html),
including the launcher, FXML controller, and dialog structure. Original starter
contributors are retained in [CONTRIBUTORS.md](CONTRIBUTORS.md).
Codex assisted the final review, search-numbering fix, GUI tests, user guide,
and release verification. The student remains responsible for reviewing the work.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. Run `topaz.ui.Launcher` from `src/main/java/topaz/ui/Launcher.java` for the GUI,
   or `topaz.Topaz` for the command-line interface. The greeting is:
   ```text
   GRONK!
   I'm Gronk, your mighty task keeper.
   Give Gronk a task. We crush it together!
   ```

The existing `data/Topaz.txt` save file and `topaz.dataFile` option are retained
so existing tasks continue to load.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Command validation

Leading and trailing spaces, repeated spaces, and tabs are accepted. Commands
must occupy one line; control characters and the save-file delimiter `|` are
not allowed in task details. `list` and `bye` take no arguments. Task numbers
use decimal digits and must refer to a task in the current list.

For `deadline`, `event`, and `duration`, slash-prefixed words are reserved for
parameters. Supply `/by`, `/from` then `/to`, or `/for` exactly once, with a
value and a task description. Dates use `yyyy-MM-dd`, `d/M/yyyy HHmm`, or an
ISO local date-time such as `2026-12-07T14:00`.

Events must end strictly after they start; date-only endpoints mean midnight.
Impossible dates and times are rejected. Duplicate tasks are rejected when
type, description, and dates or duration match. Description comparison ignores
case and repeated spaces, and completion status does not make a task unique.
Different dates, durations, or task types remain valid separate tasks.

## Storage errors and recovery

A missing save file starts an empty list. Other read failures leave the file
untouched: check the reported line for malformed or duplicate tasks, restore a
backup if needed, and retry the command in the GUI (or restart the CLI).
Save files must be UTF-8; a leading UTF-8 byte-order mark is accepted.

Changes are written to a temporary file in the same directory and atomically
replace the original only after writing finishes. A failed save rolls back
the command in memory. Check permissions, free disk space, file locks, and
`topaz.dataFile` if an error appears. Save targets must be regular files;
read-only files and symbolic-link targets are not replaced. If the file system
cannot provide atomic replacement, use a local data folder. An interrupted
save can leave a harmless `gronk-*.tmp` file beside the original.
