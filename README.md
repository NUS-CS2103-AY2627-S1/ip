# SlotBot project template

## Running SlotBot

Use JDK 25. Run `./gradlew.bat run` to open the JavaFX chatbot, or
`./gradlew.bat runCli` for the retained console interface.

Enter a command and press Enter or click Send. For example:

```text
todo read book
deadline return book /by 2026-09-10
event study /from 2026-09-10 14:00 /to 2026-09-10 15:00
list
find book
reminders
mark 1
unmark 1
delete 1
bye
```

The GUI ignores blank submissions, wraps long replies, and scrolls to new
messages. `bye` shows a farewell and closes the window after one second.
Tasks are saved to `data/slotbot.txt` relative to the working directory, using
the existing format. Both interfaces use the same data when run from the same
directory. Storage warnings appear in the conversation.
The `reminders` command lists unfinished deadlines due today through the next
seven days, ordered by due date.

Build a distributable JAR with `./gradlew.bat shadowJar`, then launch it with
`java -jar build/libs/slotbot.jar`. The separate `slotbot.gui.Launcher` and
JavaFX dependencies follow the [SE-EDU JavaFX tutorial](https://se-education.org/guides/tutorials/javaFxPart1.html).
JavaFX 17.0.7 may print unnamed-module, native-access, and deprecated-Unsafe
warnings on Java 25; these do not prevent the verified Windows launch.

Run `./gradlew.bat test` for core tests and `./gradlew.bat guiTest` for the
JavaFX interaction test (requires a graphical desktop). Run
`./text-ui-test/runtest.bat` for the unchanged console regression transcript.
The console runner compiles the shared core separately from `slotbot.gui`;
Gradle compiles and checks all Java sources, including the GUI.
See [the GUI manual test plan](test/ui-test-plan.md#case-3-javafx-gui-level-10).

## Checking Java coding style

With JDK 25 selected, run `./gradlew.bat checkstyleMain checkstyleTest` on Windows
to check production and test sources. These checks also run as part of
`./gradlew.bat check`. Both errors and warnings fail the check.

The rules in `config/checkstyle/checkstyle.xml` and test Javadoc exceptions in
`config/checkstyle/suppressions.xml` come from the
[SE-EDU AddressBook Level 3 configuration](https://github.com/se-edu/addressbook-level3/tree/master/config/checkstyle).
Checkstyle catches automated style violations; continue following the project's
coding standard for requirements that need human judgment.

After a run, open `build/reports/checkstyle/main.html` or
`build/reports/checkstyle/test.html` to inspect violations. Fix the reported code
and rerun the command. See the
[SE-EDU tutorial](https://se-education.org/guides/tutorials/checkstyle.html)
for optional IntelliJ integration; use Checkstyle 11.0.0 and this project's config.

To test the Checkstyle setup itself, run
`./gradlew.bat -I test/checkstyle-regression.gradle verifyCheckstyleRules`.
This generates isolated fixtures under `build/`, checks that compliant code passes,
and verifies that deliberate style errors and warnings are detected. Diagnostics
for the invalid fixture are expected; the task must finish with `PASS` and
`BUILD SUCCESSFUL`.

This is a project template for a greenfield Java project called _SlotBot_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. Run `slotbot.gui.Launcher` for the GUI, or `slotbot.SlotBot` for the console. If the code editor shows compile errors, reload the Gradle project. The console greeting is:
   ```
   Hello! I'm SlotBot.
   Let's keep your time and tasks in order.
   ____________________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
