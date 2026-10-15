# EVIL LARRY

EVIL LARRY is a task-management chatbot developed as a greenfield Java
project. Given below are instructions on how to use it.

## Building and running with Gradle

This project uses the Gradle Wrapper, so you do not need to install Gradle
globally. From the project root, use:

```bash
./gradlew build
./gradlew run --console=plain
```

The first command compiles the project and runs its automated tests. The second
command starts EVIL LARRY. Gradle downloads the project's declared Gradle
version on the first run and reuses it afterwards.

## Creating and running a fat JAR

Create a fresh fat JAR with the Shadow plugin from the project root:

```bash
./gradlew clean shadowJar
```

The generated file is `build/libs/larry.jar`. It contains EVIL LARRY's compiled
classes and all runtime dependencies, and its manifest identifies
`larry.Launcher` as the entry point.

Run the JAR using Java 25:

```bash
java -jar build/libs/larry.jar
```

EVIL LARRY reads and writes `data/larry.txt` relative to the directory where
this command is run. If the file or its parent directory does not exist, EVIL
LARRY creates them when the first task is saved. Enter `bye` to exit the
application.

### Platform requirements

Publish only `build/libs/larry.jar`. The build follows the
[SE-EDU JavaFX Gradle configuration](https://se-education.org/guides/tutorials/javaFxPart1.html)
and bundles the Windows, macOS, and Linux JavaFX dependencies.

On macOS (both Intel and Apple Silicon), use the course-required **Zulu
Java 25 JDK+FX**, following the
[Mac installation guide](https://se-education.org/guides/tutorials/javaInstallationMac.html).
After installation, select it in the terminal before building or running:

```bash
sdk use java 25.0.3.fx-zulu
java -version
java -jar build/libs/larry.jar
```

The JavaFX-enabled runtime is part of the macOS prerequisites. A generic
ARM64 OpenJDK installation without JavaFX is not equivalent and can fail to
load the JAR's Intel Mac native libraries. There is no separate ARM64 JAR.
Windows and Linux users should use x64 Java 25; native ARM64 support on
those operating systems is not provided by this build.

## Command reference

Commands are lowercase and case-sensitive. Task numbers are one-based.

| Command | Purpose |
| --- | --- |
| `todo DESCRIPTION` | Add a todo. |
| `deadline DESCRIPTION /by DATE_TIME` | Add a deadline. |
| `event DESCRIPTION /from DATE_TIME /to DATE_TIME` | Add an event whose end is after its start. |
| `list` | Show all tasks. |
| `find KEYWORD` | Find tasks by description. |
| `on DATE` | Show dated tasks occurring on a date. |
| `mark INDEX` | Mark a task as done. |
| `unmark INDEX` | Mark a task as not done. |
| `edit INDEX /description DESCRIPTION` | Change any task's description. |
| `edit INDEX /by DATE_TIME` | Change a deadline's due date and time. |
| `edit INDEX /from DATE_TIME` | Change an event's start date and time. |
| `edit INDEX /to DATE_TIME` | Change an event's end date and time. |
| `delete INDEX` | Delete a task. |
| `bye` | Exit Larry. |

Date-time values accept a time such as `0930` or `9:30`, optionally paired
with a day-month date such as `6-9`, `6/9/2026`, or `6-9-2026`. See the
[user guide](docs/README.md#editing-one-task-field-edit) for edit examples and
validation details.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/larry/Larry.java` file, right-click it, and choose `Run Larry.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
   ██╗      █████╗ ██████╗ ██████╗ ██╗   ██╗
   ██║     ██╔══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝
   ██║     ███████║██████╔╝██████╔╝ ╚████╔╝
   ██║     ██╔══██║██╔══██╗██╔══██╗  ╚██╔╝
   ███████╗██║  ██║██║  ██║██║  ██║   ██║
   ╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Credits and acknowledgements

- This project is based on the NUS CS2103T individual project starter template.
- The JavaFX GUI structure, including `MainWindow`, `DialogBox`, and the FXML
  views, is adapted from the
  [SE-EDU JavaFX tutorial](https://se-education.org/guides/tutorials/javaFx.html).
- EVIL LARRY's display typeface is
  [Unutterable](https://www.fontspace.com/unutterable-font-f85237), designed by
  [GGBotNet](https://ggbot.net). It is distributed under the SIL Open Font
  License, Version 1.1; a copy is included with the bundled font.
