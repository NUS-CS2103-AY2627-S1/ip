# MARY

MARY is a JavaFX task chatbot: keep to-dos, deadlines, and events in one
conversation. The desktop app and optional console share the same commands and
saved task file.

## Meet MARY

MARY is your purr-sonal task assistant: a playful, encouraging cat companion.
The desktop uses cream backgrounds, plum chat bubbles, peach accents, a friendly
sans-serif heading, and a smiling cat emblem. Command syntax and saved data are unchanged.
Java packages, `mary.jar`, and `mary-data.txt` retain their original names.

![MARY desktop](docs/Ui.png)

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. Set the **Gradle JVM** to JDK 25 and reload the Gradle project so JavaFX
   dependencies are downloaded. Run the Gradle `run` task or run
   `src/main/java/mary/Launcher.java` (`mary.Launcher`) in IntelliJ.
   Keep the project root as the run configuration's working directory.
1. A resizable MARY chat window opens. Type a command and press **Enter** or
   click **Send**. All replies, including errors, appear in the conversation.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Use the desktop chatbot

From the project root with Java 25 configured:

```powershell
.\gradlew.bat run
```

- Try `todo read book`, `list`, `mark 1`, `unmark 1`, `delete 1`, or `find book`.
- Use `deadline return book /by 2/12/2026 1800` or
  `event meeting /from 2/12/2026 1400 /to 2/12/2026 1600` for dated tasks.
- `on 2/12/2026` finds deadlines and events on that date. Dates use day/month/year
  and a 24-hour time without a colon.
- Expand **Cat's guide / commands & examples** for a reminder. The suggestion buttons
  only fill the input; they do not execute commands or overwrite a draft.
- Replies wrap and the transcript scrolls. Use `list` to get the full-list task
  numbers before marking or deleting (search-result numbers are separate).
- `bye` ends the session and displays the farewell. Click **Close** or close
  the window afterward. Closing the window directly is also safe: each successful
  task-changing command saves immediately.

Input and replies are local, not sent to an online service. Conversation history
is not saved; tasks are loaded when you reopen the app. Use one app instance at a
time to avoid concurrent edits to the same file.

## Sort tasks

Enter `sort` to reorder and display the whole list chronologically:

- Deadlines use their due date/time; events use their start date/time.
- Earliest dates come first, with todos (no date) at the end.
- Equal times and todos keep their relative order; completion status is unchanged.
- The new order is saved and retained after restarting. Use the displayed task
  numbers for subsequent `mark`, `unmark`, and `delete` commands.
- `sort` takes no arguments. An empty list is left unchanged without creating a file.

If saving fails, MARY reports the error and leaves the list in its original order.
Correct the file problem and retry.

## Recover from errors

- Leading/trailing spaces and repeated command separators are accepted, including tabs.
  Commands remain lowercase and must match whole words (`todo`, not `todoSomething`).
- Errors explain missing arguments, repeated or misplaced date markers, invalid task
  numbers, and impossible dates. Event end times must be strictly after their start.
- Exact duplicate task details (type, case-sensitive description, and dates) are rejected,
  even if the existing task is completed. Different dates or types are allowed.
  Existing duplicate records from older versions are still loaded.
- Pipes (`|`) and embedded control characters cannot be saved in descriptions.
  Ordinary punctuation and Unicode text are supported.
- Missing data files start an empty list; missing parent folders are created on save.
  A failed load disables saving to protect the original data. Back up and repair the
  reported file, or move it aside, then restart MARY. Do not delete it without a backup.
- Failed add/delete/mark/unmark/sort saves leave the original in-memory state unchanged.
  Check permissions, disk space, and other programs holding the file open before retrying.
  Saves use a temporary file and atomic replacement; unsupported filesystems report an
  error instead of attempting a potentially destructive overwrite. The configured data
  file must not be a symbolic link. Continue to use only one MARY instance at a time.

## Find tasks

Enter `find book` to find descriptions containing `book`, ignoring letter case.
Partial words match too (for example, `notebook`). Multiple words are treated as
one phrase: `find read book`. Dates and completion/type markers are not searched.
An empty keyword shows usage help, and a search with no matches says so.

Results are numbered from 1 within the search results, as in the example below.
Use `list` to see the full-list numbers needed by `mark`, `unmark`, or `delete`.
Searching never changes tasks or saved data.

```text
find book
____________________________________________________________
 Look what I sniffed out:
 1.[T][X] read book
 2.[D][ ] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
```

## Package structure

Packages group classes by responsibility. `src/main/java` remains the source root;
package directories sit underneath it.

```text
src/main/java/mary/
├── Launcher.java, Mary.java
├── task/       Task, Todo, Deadline, Event, TaskList, TaskType, TaskStatus
├── command/    Command, AddCommand, DeleteCommand, ExitCommand, ListCommand,
│               MarkCommand, OnCommand, FindCommand, SortCommand, UnknownCommand, CommandType
├── parser/     Parser
├── storage/    Storage
├── ui/         Ui, MaryApplication, ChatWindow, Personality
└── exception/  MaryException, ErrorType
```

For example, `mary.task.Deadline` extends `mary.task.Task`. Classes in other
packages use explicit imports to refer to these types. Enums live beside the
classes they describe.

## Build and run the JAR

The executable fat JAR is **`build/libs/mary.jar`**. Shadow is configured in
`build.gradle` using `com.gradleup.shadow`, and the application entry point is
`mary.Launcher`. It packages JavaFX 25.0.2 with native libraries for
**Windows x64, Linux x64, and Intel macOS** in the same JAR, regardless of the
build machine. For Apple Silicon using an ARM Java installation, build with
`-PjavafxMacPlatform=mac-aarch64` instead; this replaces the Intel Mac libraries
and still includes Windows/Linux x64. Do not mix Intel and ARM Mac libraries in
one flat JAR: their filenames collide. Linux ARM and Windows ARM are not bundled.
JUnit is test-only and is not bundled. The ordinary, non-fat `jar` task is disabled.

`shadowJar` does not run the tests. To run JUnit tests and build the JAR:

```powershell
.\gradlew.bat check shadowJar
```

Run the application with Java 25:

```powershell
java -jar .\build\libs\mary.jar
```

Native access is enabled in the JAR manifest; no extra JVM flag is required for
`java -jar`. The `check` task includes `verifyJar`, which checks the native
libraries for all three operating systems and the launch manifest.

You can copy `mary.jar` to another folder or a supported-platform computer with
Java 25 installed; Gradle and the source files are not needed to run it.
Tasks are stored in `mary-data.txt` in the **working directory from which you
launch Java**, not necessarily beside the JAR. Run from the project root to
keep using your existing project task data.

JavaFX extracts its native libraries into `.mary/javafx-cache` in that working
directory; its fallback temporary directory is `.mary/tmp`. The launcher overrides
external cache settings and checks these folders before JavaFX starts. If they
cannot be safely created locally, startup reports an error instead of using your
home folder or the system temporary directory. Use `mary.Launcher` (or the JAR),
not `MaryApplication` directly, so this setup runs before JavaFX initialization.

Task saves, temporary save files, and caches are confined to the launch working
folder and its subfolders. Paths resolving outside it (including `../` escapes,
outside absolute paths, and directory links/junctions pointing outside) are rejected.
Choose a writable working folder; placing the JAR elsewhere does not change where
data is written. Tests use local `build/tmp` fixtures and separate child-process
working folders to exercise the same rule, without a test-only bypass.

For the original console interface, append `--cli`:

```powershell
java -jar .\build\libs\mary.jar --cli
```

Alternatively use `.\gradlew.bat run --args="--cli"` or run `mary.Mary` directly.

On macOS/Linux, use `./gradlew check shadowJar` and forward slashes in paths.

### Linux / WSL launch requirements

The GUI requires a working graphical session (for example WSLg), GTK 3 and its
system dependencies. Bundling JavaFX does not provide a display server. On a
headless server or a console-only smoke test, use `java -jar mary.jar --cli`.
If graphics initialization still fails, use
`java -Djavafx.verbose=true -Dprism.verbose=true -jar mary.jar` to identify missing
system libraries or display problems. The native-access warning alone is not the
cause of a `no suitable pipeline found` crash.

## Development assertions

Java `assert` statements document internal assumptions, not user-input rules.
They are disabled by default when launching Java. Enable them with
`java -ea -jar build/libs/mary.jar` (append `--cli` for console mode), or add
`-ea` to IntelliJ's VM options. Gradle's JUnit and packaged GUI smoke tasks enable
assertions explicitly. Invalid commands and corrupt files still use ordinary
exceptions, whether assertions are enabled or disabled.

| Location | Assumption and justification |
| --- | --- |
| `Mary.execute`, after parsing | A parser result is never null, even for invalid input: an `UnknownCommand` handles that case. A null result signals a broken parser contract. |
| `Storage.parseRecord`, entry | The private caller supplies a positive, one-based physical line number. This catches programmer indexing mistakes, not malformed file content. |
| `Storage.parseRecord`, event branch | After explicit type validation and the todo/deadline branches, the remaining type must be `E`. This detects future inconsistencies between validation and dispatch. |
| `ChatWindow.submit`, entry | Submission runs on the JavaFX application thread because it changes controls and session state. This catches accidentally wiring callbacks to background threads. |
| `ChatWindow.addMessage`, entry | Startup and reply rendering run on the JavaFX application thread because they modify the scene graph. This also protects future message sources outside submission. |

Assertions have no side effects and are not caught as normal chatbot errors.
An `AssertionError` indicates a programming defect that should be fixed.

## Check Java coding style

With JDK 25 configured, run these commands from the project root:

```powershell
.\gradlew.bat checkstyleMain checkstyleTest
```

Checkstyle 14.1.0 checks both production and test Java sources. The configuration
in `config/checkstyle/checkstyle.xml` and `suppressions.xml` is copied from
[AddressBook Level 3](https://github.com/se-edu/addressbook-level3/tree/master/config/checkstyle),
following the [SE-EDU setup guide](https://se-education.org/guides/tutorials/checkstyle.html).
No project-specific rule suppressions have been added. Warnings and errors both
fail the build; fix the reported source file and line rather than disabling rules.

Open `build/reports/checkstyle/main.html` and `build/reports/checkstyle/test.html`
for readable reports. XML reports are generated alongside them. Checkstyle checks
formatting and selected conventions, not correctness or every coding-standard rule.
Manual review and tests are still needed.

To run JUnit and Checkstyle together, use `.\gradlew.bat check`.
To also build the executable JAR, use `.\gradlew.bat check shadowJar`.
On macOS/Linux, replace `.\gradlew.bat` with `./gradlew`.
In IntelliJ, select JDK 25 as the Gradle JVM and reload the Gradle project;
these tasks are also available in the Gradle tool window.

## Test the GUI

The regular `check` task runs Checkstyle and non-graphical JUnit tests. To also
test real JavaFX controls, run this on a machine with a graphical desktop:

```powershell
.\gradlew.bat check guiTest shadowJar
```

`guiTest` briefly opens test windows and uses temporary task files, never your
real data. It checks command submission, errors, persistence, suggestions, exit,
wrapping and scrolling, and writes a rendered preview to
`build/reports/gui/mary.png`. Reports are in `build/reports/tests/guiTest`.
See `test/ui-test-plan.md` for the exact scenarios and console transcripts.
Use Gradle for compilation now that JavaFX is a dependency; plain `javac`
without a JavaFX classpath is no longer sufficient.

Run `./gradlew jarGuiSmoke` (Windows: `.\gradlew.bat jarGuiSmoke`) to open,
render and close the real GUI using dependencies from the fat JAR alone.
This complements `guiTest`, whose development classpath can otherwise hide
missing packaged libraries. A graphical desktop is required for both tasks.
