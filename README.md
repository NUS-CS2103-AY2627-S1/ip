# Green Chonk

Green Chonk is a part-bear, part-pear JavaFX task companion with a focused chat interface and a playful personality. It keeps the original command-line interface as a testable fallback, saves typed tasks between sessions, and exits when the user types bye.

See the [Green Chonk User Guide](https://lzc-nus.github.io/ip/) for the
download instructions and complete command reference.

The current version supports todos, deadlines, and events. Deadlines and events accept ISO dates such as `2026-08-28`, day/month/year dates such as `28/8/2026`, and English text dates such as `28 Aug 2026`. They display in a friendly form such as `Aug 28 2026`. Each task type displays its own icon alongside its completion status. The `list` command displays numbered tasks, `find KEYWORD` searches task descriptions, `schedule DATE` finds deadlines and events occurring on a date, and `edit` changes one task detail without recreating the task. The `mark NUMBER` command completes a task, `unmark NUMBER` makes it incomplete again, and `delete NUMBER` removes it. Use `help` to see every command and its format inside the application. Invalid commands produce a specific correction instead of terminating the program. The `bye` command ends the conversation regardless of capitalization.

## Requirements

- Java Development Kit (JDK) 25
- IntelliJ IDEA (optional)

On macOS with SDKMAN, select the required Java version before building:

~~~bash
sdk use java 25.0.3.fx-zulu
~~~

## Run in IntelliJ IDEA

1. Open this project directory in IntelliJ IDEA.
2. Configure the project SDK and language level to JDK 25.
3. Open src/main/java/greenchonk/gui/Launcher.java.
4. Create an Application run configuration for `greenchonk.gui.Launcher`.
5. Add `--enable-native-access=javafx.graphics` to its **VM options**.
6. Run the configuration.

## Build and run with Gradle

From the project root:

~~~bash
./gradlew clean build
./gradlew run
~~~

The Gradle wrapper downloads the required Gradle version and project dependencies automatically on first use.

To run the original command-line interface instead:

~~~bash
./gradlew runCli
~~~

## Run automated tests

Run the complete local quality gate from the project root:

~~~bash
./gradlew clean check
~~~

The suite covers the command loop, command execution and rollback, parser
validation, storage round trips and failures, task-domain behavior, and
command-line feedback. JaCoCo enforces at least 90% line and branch coverage
of non-GUI code. The JavaFX presentation layer is covered by the manual cases
in `test/ui-test-plan.md`.

Open `build/reports/jacoco/test/html/index.html` after the check to inspect the
interactive coverage report.

## Run static analysis

Check all production and test code against the SE-EDU Java coding standard:

~~~bash
./gradlew checkstyleMain checkstyleTest
~~~

The regular `build` task also runs these Checkstyle checks before producing an artifact.

## Package as an executable JAR

Create the distributable fat JAR from the project root:

~~~bash
./gradlew clean shadowJar
~~~

The generated file is `build/libs/greenchonk.jar`. To test the package as a user would receive it, copy the JAR
into an empty folder, open a terminal in that folder, and run the following command to launch the GUI:

~~~bash
java --enable-native-access=javafx.graphics -jar "greenchonk.jar"
~~~

Keep the generated JAR out of Git; Gradle can reproduce it from the committed source and build configuration.

The program keeps reading commands until the user enters `bye` in any capitalization. Enter `help` at any time to see every available command. Add tasks with `todo DESCRIPTION`, `deadline DESCRIPTION /by DATE`, or `event DESCRIPTION /from START /to END`. Command words ignore capitalization, and harmless leading, trailing, or repeated whitespace is accepted. Each scheduled-task parameter can appear only once and event parameters must use `/from` before `/to`. Enter dates as `yyyy-MM-dd`, `d/M/yyyy`, or `d MMM yyyy`; slash dates use day/month/year and text months are English and case-insensitive. Invalid calendar dates are rejected without storing a task. An event's ending date must be the same as or later than its starting date. Green Chonk displays valid dates as `MMM dd yyyy`. Exact duplicate tasks are rejected, even if the existing task has a different completion status. Use `list` to display every task. Use `find KEYWORD` for a case-insensitive substring search of task descriptions; matching results retain their order and original task numbers. Use `schedule DATE` to display deadlines due and events in progress on that date. Scheduled results keep their original task numbers so they can be edited, marked, unmarked, or deleted directly. Edit one detail with `edit NUMBER /description DESCRIPTION`, `edit NUMBER /by DATE`, `edit NUMBER /from START_DATE`, or `edit NUMBER /to END_DATE`. The `/by` field applies only to deadlines, while `/from` and `/to` apply only to events. Each edit preserves the task's completion status and all details not named in the command. Use `mark NUMBER` to complete a task, `unmark NUMBER` to reverse that status, and `delete NUMBER` to remove one. The remaining tasks are renumbered automatically. If a command is incomplete, unknown, or refers to a task that does not exist, Green Chonk explains how to correct it and continues running without changing the task list. Tasks and their canonical ISO dates are saved atomically in `data/greenchonk.txt` whenever the list changes and restored the next time the program starts. This prevents a failed or interrupted write from replacing the last complete data file. The folder and file are created automatically on first use. If stored data is malformed, Green Chonk reports the affected line and refuses to overwrite the file until it is fixed or moved and the app is restarted. Run the program in an interactive terminal to see the thinking animation overwrite the dots in place. If the output is redirected to a file or captured by a tool, the carriage-return characters may appear as separate frames instead.

## Example interaction

The dots are animated in place during a real run, then resolve into centered messages:
Lines beginning with `>` represent user input; Green Chonk does not print the `>` prompt.

~~~text
_____________________________________________________________

                 Green Chonk is waking up...
       Hello! I'm Green Chonk—part bear, part pear.
        Hand me your tasks, and I'll keep them safe.
          Type help whenever you need a paw.

_____________________________________________________________
> todo buy milk
Chomped and packed this task:
  [T][ ] buy milk
Green Chonk is now carrying 1 task.
> help
Here are the commands Green Chonk understands:
  todo DESCRIPTION
  deadline DESCRIPTION /by DATE
  event DESCRIPTION /from START_DATE /to END_DATE
  list
  find KEYWORD
  schedule DATE
  edit TASK_NUMBER /description DESCRIPTION
  edit TASK_NUMBER /by DATE
  edit TASK_NUMBER /from START_DATE
  edit TASK_NUMBER /to END_DATE
  mark TASK_NUMBER
  unmark TASK_NUMBER
  delete TASK_NUMBER
  help
  bye
Dates: yyyy-MM-dd, d/M/yyyy, or d MMM yyyy (e.g., 2026-08-28, 28/8/2026, 28 Aug 2026).
> deadline finish report /by 2026-08-28
Chomped and packed this task:
  [D][ ] finish report (by: Aug 28 2026)
Green Chonk is now carrying 2 tasks.
> event project meeting /from 2026-08-29 /to 2026-08-30
Chomped and packed this task:
  [E][ ] project meeting (from: Aug 29 2026 to: Aug 30 2026)
Green Chonk is now carrying 3 tasks.
> list
Here are the tasks Green Chonk is carrying:
1.[T][ ] buy milk
2.[D][ ] finish report (by: Aug 28 2026)
3.[E][ ] project meeting (from: Aug 29 2026 to: Aug 30 2026)
> schedule 2026-08-29
Here are the tasks scheduled for 2026-08-29:
3.[E][ ] project meeting (from: Aug 29 2026 to: Aug 30 2026)
> mark 2
High paw! Green Chonk marked this task as done:
  [D][X] finish report (by: Aug 28 2026)
> list
Here are the tasks Green Chonk is carrying:
1.[T][ ] buy milk
2.[D][X] finish report (by: Aug 28 2026)
3.[E][ ] project meeting (from: Aug 29 2026 to: Aug 30 2026)
> find report
Here are the matching tasks in your list:
2.[D][X] finish report (by: Aug 28 2026)
> edit 2 /description submit final report
Green Chonk updated this task:
  Before: [D][X] finish report (by: Aug 28 2026)
  After:  [D][X] submit final report (by: Aug 28 2026)
> edit 3 /to 2026-09-01
Green Chonk updated this task:
  Before: [E][ ] project meeting (from: Aug 29 2026 to: Aug 30 2026)
  After:  [E][ ] project meeting (from: Aug 29 2026 to: Sep 01 2026)
> unmark 2
OK, Green Chonk marked this task as not done yet:
  [D][ ] submit final report (by: Aug 28 2026)
> delete 2
Noted. Green Chonk removed this task:
  [D][ ] submit final report (by: Aug 28 2026)
Green Chonk is now carrying 2 tasks.
> list
Here are the tasks Green Chonk is carrying:
1.[T][ ] buy milk
2.[E][ ] project meeting (from: Aug 29 2026 to: Sep 01 2026)
> todo
Oops! Green Chonk couldn't chomp that:
  A todo needs a description. Try: todo buy milk
> roll away
Oops! Green Chonk couldn't chomp that:
  I don't recognize "roll away". Try todo, deadline, event, list, find, schedule, edit, mark, unmark, delete, help, or bye.
> bye

_____________________________________________________________
 Task backpack zipped! I'm rolling off for now. See you soon!
_____________________________________________________________
~~~

## Project structure

~~~text
src/
└── main/
    ├── java/
    │   └── greenchonk/
    │       ├── GreenChonk.java
    │       ├── command/
    │       │   ├── AddCommand.java
    │       │   ├── Command.java
    │       │   ├── DeleteCommand.java
    │       │   ├── EditCommand.java
    │       │   ├── ExitCommand.java
    │       │   ├── FindCommand.java
    │       │   ├── HelpCommand.java
    │       │   ├── ListCommand.java
    │       │   ├── ScheduleCommand.java
    │       │   └── UpdateStatusCommand.java
    │       ├── exception/
    │       │   ├── GreenChonkException.java
    │       │   └── StorageException.java
    │       ├── gui/
    │       │   ├── DialogBox.java
    │       │   ├── Launcher.java
    │       │   ├── Main.java
    │       │   └── MainWindow.java
    │       ├── parser/
    │       │   └── Parser.java
    │       ├── storage/
    │       │   ├── Storage.java
    │       │   └── TaskCodec.java
    │       ├── task/
    │       │   ├── Deadline.java
    │       │   ├── Event.java
    │       │   ├── Task.java
    │       │   ├── TaskDate.java
    │       │   ├── TaskList.java
    │       │   ├── TaskStatus.java
    │       │   └── Todo.java
    │       └── ui/
    │           └── Ui.java
    └── resources/
        ├── images/
        │   ├── green-chonk.png
        │   └── green-chonk-transparent.png
        └── view/
            ├── DialogBox.fxml
            ├── MainWindow.fxml
            └── styles.css
~~~

Keep Java source files under src/main/java, which is the source directory expected by the project setup.

## Acknowledgements

This project builds on the [SE-EDU Duke project
template](https://github.com/se-edu/duke). See the product [User
Guide](docs/README.md#acknowledgements) for the complete tool and asset
credits.
