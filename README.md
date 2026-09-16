# Arin

Arin is a desktop task companion for to-dos, deadlines, and events.
Type commands in its JavaFX chat window to track, find, and sort tasks.
Successful changes are saved automatically and restored on the next launch.

[Download the latest JAR](https://github.com/Goh-Wei-Chong/ip/releases/latest).

Read the [published User Guide](https://goh-wei-chong.github.io/ip/) for
setup, all commands, and current limitations, or view the
[guide in this repository](docs/README.md).

The instructions below are for developers building the project.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. Run the Gradle `run` task to open the GUI with its generated native resources.
   For console-only development, run `arin.Arin.main()` instead.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Building and running the JAR

Use JDK 25 to build the project. From the project root, run:

```bash
java -version
./gradlew clean check shadowJar
```

On Windows, use `gradlew.bat shadowJar`. The
[Shadow JAR task](https://se-education.org/guides/tutorials/gradle.html#creating-jar-files)
creates `build/libs/arin-all.jar` with the JavaFX dependencies included.
Use this bundled JAR for distribution; the ordinary `jar` task does not
include those dependencies. To force a fresh build, add `--rerun-tasks`.

Run the generated JAR with Java 25:

```bash
java -jar build/libs/arin-all.jar
```

For development, use `java -ea -jar build/libs/arin-all.jar` to enable
Java assertions. Gradle's `run` and `test` tasks enable them automatically.
Assertions check internal assumptions about already validated task data;
ordinary command errors are still handled through user-facing messages.

The application opens Arin's interactive chat window. See the [User Guide](docs/README.md) for its
full-window screenshot and commands.
JavaFX does not need to be installed separately, but Java 25 is required.

The same JAR includes JavaFX classes and native libraries for x86-64 Windows
and Linux, plus both Intel and Apple-silicon macOS. `NativeLibraries` extracts
the matching binaries into a temporary directory before JavaFX starts.
Native selection follows OpenJFX's
[documented source-level library search order](https://github.com/openjdk/jfx/blob/jfx17/modules/javafx.graphics/src/main/java/com/sun/glass/utils/NativeLibLoader.java).
This prevents identically named Mac binaries from overwriting one another.
Linux requires a graphical desktop and GTK 3. Other CPU/OS combinations are
not bundled. No separate JavaFX SDK is required.

## Sorting tasks (C-Sort extension)

The task commands work in the GUI and in the optional console. After
building the bundled JAR, start the console with Java 25:

```bash
java -ea -cp build/libs/arin-all.jar arin.Arin
```

Use either of these commands:

- `sort name`: sort all tasks alphabetically by description, ignoring case.
- `sort deadline`: put deadlines first, ordered from earliest to latest.
  To-dos and events follow in their existing order. Event times are free text
  and are not interpreted as deadline dates.

Both commands keep ties in their current relative order and preserve task
details and completion status. The sorted list is shown immediately and
written to `data/arin.txt`. Task numbers change to match this order, so use
the newly displayed numbers for `mark`, `unmark`, and `delete`. New tasks
are still appended; run `sort` with an option again when needed.

For example, enter these commands one at a time:

```text
todo buy milk
deadline return book /by 2026-09-15
deadline submit report /by 2026-09-11
sort deadline
mark 1
sort name
bye
```

After `sort deadline`, task 1 is `submit report`, task 2 is `return book`,
and task 3 is `buy milk`. Thus, `mark 1` completes `submit report`.
Missing or unsupported options (such as `sort` or `sort date`) produce a
usage message without changing the list.

This implements the course's
[C-Sort extension](https://nus-cs2103-ay2627-s1.github.io/website/projectDuke/index.html#c-sort).
