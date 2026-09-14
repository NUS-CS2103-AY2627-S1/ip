# Groot chatbot

Groot is a simple Java chatbot that saves todos, deadlines, and events between sessions. Deadline dates are validated and displayed as `MMM dd yyyy`. Enter `help`, `--help`, or `-h` to see the available commands. Invalid commands and malformed task details produce an explanatory error without ending the program.

## Command reference

| Command | Description |
| --- | --- |
| `todo DESCRIPTION` | Adds a todo task. |
| `deadline DESCRIPTION /by YYYY-MM-DD` | Adds a deadline task. |
| `event DESCRIPTION /from START /to END` | Adds an event task. |
| `list` | Displays all tasks. |
| `sort`, `sort -r`, or `sort --reverse` | Displays only deadlines by date; reverse shows latest first. |
| `find KEYWORD` | Finds tasks whose descriptions contain the keyword. |
| `mark TASK_NUMBER` | Marks a task as done. |
| `unmark TASK_NUMBER` | Marks a task as not done. |
| `delete TASK_NUMBER` | Deletes a task. |
| `help`, `--help`, or `-h` | Displays the command reference in Groot. |
| `bye` | Exits Groot. |

## Sorting deadlines

Use `sort` for earliest-first deadlines, or `sort -r` / `sort --reverse` for latest first.
Completed deadlines are included. Equal dates are ordered by description, ignoring capitalization;
reverse changes date order only. Equal dates and names keep their main-list order.

Sorting changes only this response: task order, saved data, and subsequent `list` and `find` behavior
stay unchanged. Displayed numbers are the original main-list numbers, so use those numbers with
`mark`, `unmark`, and `delete`. Run `list` or `sort` again after deleting tasks because numbers shift.
Todos and events do not appear in the sorted response.

The sort command and its flags accept case variations, extra spaces, and tabs. Other commands retain
their existing syntax. Unsupported or repeated flags produce `Oops! Use: sort [-r | --reverse].`
See the [sorting user guide](docs/README.md#sorting-deadlines-c-sort) for examples and exact output.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/groot/Groot.java` file, right-click it, and choose `Run Groot.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see the following output:
   ```
   ____________________________________________________________
          \  |  /
        ___\_|_/___
       /   /   \   \
      /   | o o |    |
     |    |  ^  |    |
     |    \ \_/ /    |
      \    '---'    /
       \  |||||||  /
        | ||||||| |
     ___|_|||||||_|___
    /     |||||||     \
   /      |||||||      \
          |||||||
         /||| |||\
        /_||| |||_\
   Hello! I'm Groot.
   What can I do for you?
   ____________________________________________________________
   todo borrow book
   ____________________________________________________________
    Got it. I've added this task:
      [T][ ] borrow book
    Now you have 1 task in the list.
   ____________________________________________________________
   deadline return book /by 2019-12-02
   ____________________________________________________________
    Got it. I've added this task:
      [D][ ] return book (by: Dec 02 2019)
    Now you have 2 tasks in the list.
   ____________________________________________________________
   event project meeting /from Mon 2pm /to 4pm
   ____________________________________________________________
    Got it. I've added this task:
      [E][ ] project meeting (from: Mon 2pm to: 4pm)
    Now you have 3 tasks in the list.
   ____________________________________________________________
   list
   ____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] borrow book
    2.[D][ ] return book (by: Dec 02 2019)
   3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
   ____________________________________________________________
   find book
   ____________________________________________________________
    Here are the matching tasks in your list:
    1.[T][ ] borrow book
    2.[D][ ] return book (by: Dec 02 2019)
   ____________________________________________________________
   mark 2
   ____________________________________________________________
    Nice! I've marked this task as done:
      [D][X] return book (by: Dec 02 2019)
   ____________________________________________________________
   list
   ____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] borrow book
    2.[D][X] return book (by: Dec 02 2019)
    3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
   ____________________________________________________________
   delete 1
   ____________________________________________________________
    Noted. I've removed this task:
      [T][ ] borrow book
    Now you have 2 tasks in the list.
   ____________________________________________________________
   bye
   ____________________________________________________________
    Bye. Hope to see you again soon!
   ____________________________________________________________
   ```

## Package structure

The Java source root remains `src/main/java`. Classes are grouped by responsibility beneath the `groot` root package:

- `groot`: application entry point and coordination;
- `groot.exception`: application-specific exceptions;
- `groot.parser`: command recognition and input parsing;
- `groot.storage`: loading and saving tasks; and
- `groot.task`: task models and task-list operations; and
- `groot.ui`: JavaFX windows and dialog components.

Keep `src/main/java` as the Java source root. Package folders must remain inside that directory so Java and project tools can find them correctly.

`CommandType` accepts a canonical command keyword followed by optional aliases through a varargs parameter. This keeps `help`, `--help`, and `-h` mapped to the same command type. `TaskList` retains its collection-based constructor for tasks loaded from storage and also provides a varargs constructor for callers that supply tasks directly.

## Building and running the fat JAR

The Shadow plugin packages Groot and its runtime dependencies into one executable fat JAR. From the project root, create a fresh JAR on macOS or Linux with:

```shell
./gradlew clean shadowJar
```

On Windows, use:

```shell
gradlew.bat clean shadowJar
```

The generated file is located at `build/libs/groot.jar`. Run it using Java 25:

```shell
java -jar build/libs/groot.jar
```

Groot resolves its `data/groot.txt` path relative to the directory from which the JAR is run. Run the command from the project root to use the project's existing `data` directory.

## Continuous integration

[Java CI](.github/workflows/gradle.yml) runs on pushes and pull requests using Linux, macOS, and Windows.
It follows the [SE-EDU workflow template](https://github.com/se-edu/duke/blob/full-template/.github/workflows/gradle.yml)
and uses Zulu JDK 25 with JavaFX. Each job validates the Gradle wrapper, runs JUnit and Checkstyle through
`check`, and builds the fat JAR with `shadowJar`. Assertions are enabled in JUnit by the Gradle configuration.

To run the same build checks locally with Java 25:

```shell
./gradlew --no-daemon check shadowJar
```

After committing and pushing the workflow to your fork, view its results in the repository's **Actions** tab
and in pull-request checks. If GitHub Actions is disabled for the fork, enable it in that tab first.
When using a classic personal access token to push workflow files, the token needs the `workflow` scope
in addition to repository access. Configure the token in your Git client; never store it in this repository.

## Assertions

Java assertions document internal assumptions and detect programming errors during development.
They do not replace validation of user commands or saved data, and their expressions have no side effects.
Enable them by adding `-ea` to the IDE's VM options or running `java -ea -jar build/libs/groot.jar`.
JUnit tests explicitly enable assertions in Gradle.

| Location | Assumption and justification |
| --- | --- |
| `Groot.executeCommand` | The command type is non-null and recognized. The parser must reject unknown input before dispatch. |
| `Parser.parseTaskIndex` | The task count is non-negative. It comes from the application's collection size, not user input. Zero remains valid for an empty list. |
| `TaskList.markAsDone` | The task is completed after marking. Task subclasses must honor this operation before the application reports success. |
| `TaskList.markAsNotDone` | The task is incomplete after unmarking. This checks that task subclasses also honor the inverse operation. |
| `TaskList.setDone` | The final status equals the requested status. Failed-save recovery depends on restoring the exact previous state. |

## AI use

This project was developed with assistance from OpenAI Codex. AI was used to:

- review requirements and suggest suitable Java designs;
- help implement task deletion, task searching, collection-based storage, command enums, and error handling;
- organize the Java classes into responsibility-based packages and update their imports;
- configure and verify Gradle fat-JAR packaging;
- create and apply project-specific SE-EDU Java and Git standard skills;
- draft and review JUnit and console UI tests, including invalid and edge-case inputs;
- diagnose a JUnit functional-interface warning and add no-match coverage for task searching;
- improve Javadoc, user-facing documentation, and Git commit messages; and
- run checks that compare the program's actual output with its expected output.

All AI-generated suggestions were reviewed before use. The source code was inspected, and the recorded UI test plan was run with Java 25 to verify the resulting behavior. The project author remains responsible for the final implementation.

The JUnit coverage target is the approximately 50% highest-value methods, prioritizing complex, core, and critical business logic over trivial accessors. After each code change, the affected classes and their JUnit tests must be reviewed and the tests updated as needed to continue meeting this target.

## GUI acknowledgements

The original JavaFX GUI was adapted from the SE-EDU JavaFX tutorial (dialog components
and FXML loading). The redesign retains that foundation and was implemented with
OpenAI Codex assistance, including the task panel, styling, and regression tests.

The existing character artwork in `src/main/resources/images/background.jpg` is retained.
Its original artist and source are not recorded in this repository; attribution remains
unresolved and must be verified by the project author. No new artwork was generated.
