# SevenSix

This project is named SevenSix. Given below are instructions on how to use it.

## Running the JavaFX GUI

Use JDK 25, then run the following command from the project root:

```bash
./gradlew run
```

The GUI accepts the same commands as the console version. Press Enter or click `Send command` to submit a
command. Tasks are saved to `data/sevensix.txt` by default.

## Getting help

Enter `help` in either interface to see all commands, their syntax, date formats,
and examples. It takes no arguments and does not change tasks or undo history.
See [the help command guide](docs/README.md#getting-help) for more details.

## Input validation and recovering from errors

Extra spaces and tabs are accepted and normalized to single spaces. Task descriptions
may contain Unicode, punctuation, pipes, and backslashes. Line breaks and control
characters are rejected because each saved task occupies one line.

- Supply `/by` exactly once for deadlines, or `/from` followed by `/to` exactly once
  each for events. Descriptions and date values cannot be empty.
- Use real calendar dates and valid times, with minute precision. For example,
  `2024-02-29` is valid but `2024-02-30` is not.
- An event must end strictly after it starts. A date without a time means midnight
  for this comparison, so a same-day event needs an explicit later end time.
- A duplicate has the same task type, case-sensitive description, and date/time
  fields, even if the existing task is marked done. Different schedules are allowed.
- Task numbers must contain digits only and refer to an existing task. `list`,
  `undo`, `bye`, and `help` do not accept additional arguments.

A missing task file starts an empty list and is created on the first successful
change. An invalid path, unreadable file, or corrupt/duplicate record produces a
startup warning in both interfaces. Valid records remain available, but saving is
disabled for that session. Back up and repair the file or correct its path and
permissions, then restart. Symbolic links at the task-file path are not supported.

Saves use a temporary file in the same folder and require atomic replacement support
from the filesystem. If writing or replacement fails, the command reports an error
and restores both the task list and the previous undo opportunity. Check file/folder
permissions and free disk space, then retry. The original data file is not truncated.

## Testing

With Java 25, run `./gradlew check` for JUnit, Checkstyle, and core and GUI coverage checks.
Run `./gradlew testChineseLocale` to repeat the JUnit suite with Chinese JVM defaults.
Use `gradlew.bat` on Windows.

See [automated testing and coverage](test/testing.md) for reports and coverage limits,
and [the manual GUI test plan](test/gui-test-plan.md) for display and OS checks.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/sevensix/SevenSix.java` file, right-click it, and choose `Run SevenSix.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see the following output:
   ```
   Welcome to SevenSix!
   ```

## Building an executable JAR

Run the following command from the project root:

```bash
./gradlew clean shadowJar
```

This creates the executable `build/libs/SevenSix.jar`. Copy that file into an empty folder and run
the chatbot with:

```bash
java -jar SevenSix.jar
```

The JAR is a generated build artifact and should not be committed to Git.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
