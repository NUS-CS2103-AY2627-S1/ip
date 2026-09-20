# Goop project template

This is a project template for the Goop chatbot. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/goop/Goop.java` file, right-click it, and choose `Run Goop.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
   ____________________________________________________________
     ____
    / ___| ___   ___  _ __
   | |  _ / _ \ / _ \| '_ \
   | |_| | (_) | (_) | |_) |
    \____|\___/ \___/| .__/
                     |_|
   Hello! I'm Goop.
   What can I do for you?
   ____________________________________________________________
   Bye. Hope to see you again soon!
   ____________________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Creating and running the fat JAR

The Shadow plugin packages Goop and its runtime dependencies into one executable
fat JAR. Use Java 25 when building and running it.

From the project root, create the JAR with:

```shell
./gradlew clean shadowJar
```

On Windows, use:

```shell
gradlew.bat clean shadowJar
```

The generated file is located at `build/libs/duke.jar`. Run it from the project
root with:

```shell
java -jar build/libs/duke.jar
```

On Windows, the equivalent command is:

```shell
java -jar build\libs\duke.jar
```

The application stores its task data relative to the directory from which the
JAR is run. The generated `build` directory is ignored by Git, so the JAR should
not be committed.

## Task priorities

Assign a priority to an existing task using its number from `list`:

```text
priority 1 high
priority 2 medium
priority 3 low
priority 1 none
```

Use `none` to clear a priority. Numeric levels `1`, `2`, and `3` are aliases
for `high`, `medium`, and `low` (for example, `priority 2 1` marks task 2 high).
Priority labels are case-insensitive. New tasks start without a priority.

Assigned priorities appear in both `list` and `find`, for example
`[T][ ] [high] read book`. Task order and numbering remain unchanged.
Priorities are saved across restarts; existing data files load without migration.

## Input validation and recovery

- Leading/trailing whitespace, tabs, and repeated spaces are normalized to single
  spaces, including within descriptions. Commands remain case-sensitive.
- `list` and `bye` take no arguments. Required parameters must be present, and
  `/by`, `/from`, and `/to` must appear only once in their respective commands.
  Event `/from` must precede `/to`.
- Descriptions may contain punctuation, Unicode, pipes, and backslashes. Embedded
  line breaks and control characters are rejected to keep saved records readable.
- Duplicate tasks are rejected even if the existing task is completed or has a
  different priority. A duplicate has the same type, case-sensitive description
  (ignoring repeated whitespace), and schedule. Equivalent numeric schedule
  formats count as the same schedule. Delete the existing task to add it again.

### Event dates and times

For validated event schedules, use `d/M/yyyy` or `yyyy-MM-dd`, optionally followed
by a 24-hour `HHmm` time. Both endpoints must include dates when either does.
Time-only ranges accept formats such as `1400`, `14:00`, `2pm`, and `2:30 pm`.
The end must be strictly later than the start. Use full dates for overnight events:

```text
event maintenance /from 2026-09-15 2300 /to 2026-09-16 0100
```

Impossible numeric dates and times are rejected. Legacy free-form schedules such
as `Mon 2pm` are still accepted, but their chronological order cannot be reliably
checked. Identical start/end text is rejected. Use numeric dates and times when
you need calendar validation.

### Saved-data problems

A missing data file starts an empty list. Unreadable files, invalid UTF-8,
malformed records, duplicate tasks, and invalid event schedules produce errors.
After a load failure, saving is disabled so the original file cannot be
overwritten by an empty list. Fix the reported record or file permissions, or
move the file aside, then restart Goop.

Saves write a temporary file beside the data file and atomically replace it only
after writing succeeds. If writing or replacement fails, the previous file and
in-memory task list are preserved. The destination must be a writable regular
file (not a symbolic link), and its folder must permit creating files. Filesystems
without atomic replacement support report a save error instead of risking the
existing data.
