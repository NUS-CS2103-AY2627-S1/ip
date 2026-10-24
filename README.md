# Orbit

Orbit is a local task assistant with a JavaFX chat interface, built for the CS2103/T individual project.
It manages todos, deadlines and events, and saves successful changes to `data/orbit.txt` in the working folder.
The command-line interface remains available for terminal use and regression testing.

## Run Orbit

Prerequisite: JDK 25. Set `JAVA_HOME` to that installation.

1. Run `./gradlew run` (Windows: `gradlew.bat run`) to open the GUI.
2. Type a command and press Enter or click Send.
3. Use `bye` to close the window, or close it normally. Successful task changes are already saved.

Build a self-contained JAR with `./gradlew shadowJar`, then run `java -jar build/libs/Orbit.jar`.
JavaFX dependencies for Windows, macOS and Linux are included as prescribed by the course tutorial;
the build and GUI have been tested on Windows with JDK 25. Mac users should follow the course's
JDK distribution advisory. A JavaFX unnamed-module warning may appear and is not a startup failure.

For the text UI, run `./gradlew runCli` or `java -jar build/libs/Orbit.jar --cli`.
Both interfaces use the same data format and command processor. Do not run two instances against
the same data file at once, because each session maintains its own in-memory list.

## Commands

| Command | Example |
| --- | --- |
| Add a todo | `todo Read CS2103 notes` |
| Add a deadline | `deadline Submit iP /by 2026-09-04` |
| Add an event | `event Study group /from 2026-09-05 /to 2026-09-06` |
| List tasks | `list` |
| Mark / unmark | `mark 1` / `unmark 1` |
| Delete | `delete 1` |
| Search descriptions (case-sensitive) | `find CS2103` |
| Sort dated tasks chronologically | `sort` |
| Exit | `bye` |

Dates must use `yyyy-MM-dd`. The `sort` command orders deadlines by their due dates and events by
their start dates. An event's end date must be after its start date. Tasks on the same date keep
their existing order, and undated todos come last.
The sorted order is saved, so task numbers in later `mark` and `delete` commands use that order.
The numbers shown by `find` are result positions only. Use numbers from the full `list` when running
`mark`, `unmark`, or `delete`.

## Checking the code

With `JAVA_HOME` pointing to JDK 25, run `./gradlew check` (Windows: `gradlew.bat check`).
This runs the JUnit tests and Checkstyle against both production and test sources.
Style warnings fail the build, so they cannot be overlooked.

The configuration in `config/checkstyle/` comes from
[SE-EDU AddressBook Level 3](https://github.com/se-edu/addressbook-level3/tree/master/config/checkstyle),
as recommended by the [course Checkstyle tutorial](https://se-education.org/guides/tutorials/checkstyle.html).
Only the upstream test Javadoc exemptions are applied; application checks are not suppressed.

For real JavaFX interaction tests, run `./gradlew guiTest` with a desktop display available.
These tests exercise the packaged JAR and save a scene screenshot under `build/screenshots/`.
See [GUI test plan](test/gui-test-plan.md) and [console test plan](test/ui-test-plan.md).

## Acknowledgements

The launcher, custom dialog and FXML/controller structure are adapted from the
[SE-EDU JavaFX tutorial Parts 1–4](https://se-education.org/guides/tutorials/javaFx.html).
The tutorial was built and tested in a separate companion project before adapting Orbit.
Orbit uses styled speaker initials instead of the tutorial's image avatars.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate `src/main/java/orbit/Launcher.java`, right-click it, and choose
   `Run Launcher.main()` to open the GUI. To run the retained text interface instead, run
   `Orbit.main()`. If the setup is correct, the GUI title bar should show `Orbit | Task assistant`.
   The text interface starts with the following banner:
   ```
     ___       _     _ _
    / _ \ _ __| |__ (_) |_
   | | | | '__| '_ \| | __|
   | |_| | |  | |_) | | |_
    \___/|_|  |_.__/|_|\__|
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
