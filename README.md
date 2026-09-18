# Phin

## Updating tasks

Use `update NUMBER` with one or more fields to edit a task without deleting it.
For example, `update 3 /to 2024-03-05` changes only an event's end date, while
`update 2 /description submit final report /by 2024-03-08` changes both a
deadline's description and date. Available fields are `/description` for every
task, `/by` for deadlines, and `/from` and `/to` for events.

The task type, completion status, and all omitted fields remain unchanged.
Blank, repeated, unknown, or type-incompatible fields are rejected without
changing the task. Dates use `yyyy-MM-dd`, and an updated event end date cannot
precede its start date.

## Finding tasks 

Use `find book` to search task descriptions for a case-sensitive substring.
For example, it matches `read book` and `bookshelf`, but not `Book`.
Use `find read book` to search for the complete phrase `read book`;
surrounding whitespace is ignored, but internal spaces are significant.
Dates, task types, and completion icons are not searched. Missing or blank
keywords are rejected with usage guidance.

Matches retain their original order, type, and completion status. Results
are numbered from 1 for display only: use `list` to obtain task numbers for
`mark`, `unmark`, `delete`, and `update`. With no matches, only the matching-tasks
heading is printed. Searching never modifies tasks or writes the saved file.

Phin is a chatbot written in Java. Given below are instructions on how to set it up.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/phin/Phin.java` file, right-click it, and choose `Run Phin.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see the following output:
   ```
   ____________________________________________________________
   Phin
   I'm Phin. Apparently I have to deal with this.
   What do you want?
   ____________________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Building and running with Gradle

Install JDK 25 and set `JAVA_HOME` to its installation directory. Use the
committed Gradle wrapper; a separate Gradle installation is not needed.
The first run downloads Gradle and dependencies, so it needs internet access.

`JAVA_HOME` must point to the JDK folder, not `java.exe` or its `bin` folder.
For example, with JDK 25.0.4 installed at the default Windows location, set
it for the current PowerShell session using:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-25.0.4'
```

Adjust that path to your installed JDK. For a persistent setting, update
`JAVA_HOME` in Windows Environment Variables and reopen your terminal and IDE.

From the project root in PowerShell:

```powershell
.\gradlew.bat --version
.\gradlew.bat clean build
.\gradlew.bat --console=plain run
```

On macOS or Linux, use `./gradlew` instead of `.\gradlew.bat`.
Configure the IDE's Gradle JVM to use JDK 25, and reload the Gradle project
after opening it. The build selects a Java 25 toolchain for compilation
and running. The `run` task forwards console input to Phin.

The build runs JUnit tests for command parsing, strict dates, and task-list
operations. Run them separately with `.\gradlew.bat test`; the HTML report
is at `build/reports/tests/test/index.html`. Test sources are under
`src/test/java/phin`, matching the production package. Keep these tests
updated when the corresponding behavior changes.

These tests focus on core logic, not every public method or console message.
The UI suite additionally checks exact output and persistence across restarts.
Run it with:

```powershell
powershell -ExecutionPolicy Bypass -File .codex/skills/test-ui/scripts/run-ui-tests.ps1
```

## Building and distributing the executable JAR

With JDK 25 configured, build the executable using the supplied Shadow plugin:

```powershell
.\gradlew.bat clean test shadowJar
```

The output is `build/libs/phin.jar`. Copy just this file into an empty folder,
open a terminal in that folder, and run it with Java 25:

```powershell
java -jar "phin.jar"
```

Recipients need Java 25 but do not need Gradle or the source code. Use a terminal
rather than double-clicking the JAR. Phin creates `data/phin.txt` beside the JAR
on the first task change. Keep that data folder when replacing the JAR.

Verify the packaged app with the same UI cases, each in an isolated folder:

```powershell
powershell -ExecutionPolicy Bypass -File .codex/skills/test-ui/scripts/run-ui-tests.ps1 -JarPath build/libs/phin.jar
```

Distribute `phin.jar` as an asset on a GitHub release, not as a committed file.
Generated files under `build/` are already excluded from Git. Download the JAR
asset rather than GitHub's automatically generated source archives.

## Package layout and manual console launch

All classes belong to the `phin` package under `src/main/java/phin`.
The entry point is `phin.Phin`; `src/main/java` remains the source root.
A single package keeps this small application simple without changing class
access rules. Subpackages can be introduced when more classes warrant them.

To compile and run from the project root in PowerShell with JDK 25:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
$sources = Get-ChildItem src/main/java -Recurse -Filter '*.java' | ForEach-Object FullName
javac -d out $sources
java -cp out phin.Phin
```

## Saved tasks

For development, run Phin with the project root as the working directory;
for the distributed JAR, run it from the folder containing the JAR. Tasks are loaded from
`data/phin.txt` at startup and saved after add, mark, unmark, delete, and update commands.
The folder is created on the first save and is excluded from Git.

Records use `TYPE|STATUS|DESCRIPTION` with deadline or event time fields appended.
Types are T, D, and E; status is 0 or 1. Fields use UTF-8 URL encoding: spaces are
`+`, literal plus signs are `%2B`, and pipes are `%7C`.

If loading fails, Phin stops without changing the file. Correct or back up and
remove the damaged file before restarting. If saving fails, changes remain in
memory; fix the folder permissions and retry a change before exiting.

## Dates 

Deadlines and event endpoints accept real calendar dates in `yyyy-MM-dd`
format, for example `deadline return book /by 2019-12-02` or
`event workshop /from 2019-12-02 /to 2019-12-03`. The task list displays
`Dec 02 2019`. Dates are stored as `LocalDate`; times of day are not supported.
An event may start and end on the same day, but cannot end before it starts.
Invalid dates are rejected without adding a task.

Saved dates remain in ISO format. Level 7 files containing free-form dates
such as `Sunday` must be backed up and edited to use `yyyy-MM-dd` before
startup. Phin refuses to load invalid dates and does not overwrite that file.
