# Olaf project

Olaf is a desktop chatbot built as a greenfield Java project. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.

## Running with Gradle

Prerequisite: JDK 25.

1. Open a terminal in the project root folder containing `build.gradle`.
1. Run `java --version` and confirm that it reports Java 25.
1. Launch Olaf with the command for your operating system and terminal:

   | Environment | Command |
   |---|---|
   | Windows PowerShell | `.\gradlew.bat run` |
   | Windows Command Prompt | `gradlew.bat run` |
   | macOS/Linux with Bash, Zsh, Fish, or PowerShell | `./gradlew run` |
   | Git Bash or WSL | `./gradlew run` |

   If you are using an IDE, run the matching command in its integrated terminal.
1. After Olaf's window displays its welcome message, enter `bye` and press **Enter** or click
   **Send** to verify that it runs and exits normally.

On macOS or Linux, if the terminal reports that `gradlew` is not executable, run
`chmod +x gradlew` and then retry `./gradlew run`.

In Visual Studio Code, use `Java: Configure Java Runtime` from the Command Palette to select
**JDK 25**. Do not launch Olaf with Code Runner's **Run Code** command, the Java **Run** link,
or `F5`; use the Gradle wrapper command above so the complete project is built and launched.

## Running tests

From the project root, run the test suite with the command for your operating system and terminal:

| Environment | Command |
|---|---|
| Windows PowerShell | `.\gradlew.bat test` |
| Windows Command Prompt | `gradlew.bat test` |
| macOS/Linux with Bash, Zsh, Fish, or PowerShell | `./gradlew test` |
| Git Bash or WSL | `./gradlew test` |

The suite tests command parsing, task operations, rescheduling, persistence, and task encoding,
including escaped characters, malformed storage records, original task numbers in search results,
and recovery from simulated write and file-replacement failures. A successful run ends with
`BUILD SUCCESSFUL`, and the HTML report is generated at
`build/reports/tests/test/index.html`.

On Windows, if Gradle cannot load `GradleWorkerMain`, retry with
`.\gradlew.bat test '-Dfile.encoding=COMPAT'` while still using JDK 25.

The GUI accepts the same text commands as the original console interface. Olaf stores ToDos,
deadlines, and events across application sessions. Add them with
`todo <description>`, `deadline <description> /by <yyyy-MM-dd>`, or
`event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>`. Enter dates in `yyyy-MM-dd`
format; Olaf displays them in `MMM dd yyyy` format.
Enter `list` to display the stored tasks, `mark <task number>` to mark a task as done,
`unmark <task number>` to mark a task as not done, `delete <task number>` to remove a task,
`find <keyword>` to display tasks whose descriptions contain the keyword (ignoring case),
`reschedule` to change a deadline or event's dates as described below,
or `bye` to exit. A typical session looks like this:

Olaf loads tasks from `data/olaf.txt` when it starts and automatically saves the file whenever
the task list changes. The `data` directory and file are created on the first task change.

   The scrolling conversation displays the same responses as this example:

   ```
     ___  _        __
    / _ \| | __ _ / _|
   | | | | |/ _` | |_
   | |_| | | (_| |  _|
   \___/|_|\__,_|_|

   Hello! I'm Olaf. What can I do for you?
   ____________________________________________________________
   todo read book
   ____________________________________________________________
    Got it. I've added this task:
      [T][ ] read book
    Now you have 1 task in the list.
   ____________________________________________________________
   deadline return book /by 2019-10-15
   ____________________________________________________________
    Got it. I've added this task:
      [D][ ] return book (by: Oct 15 2019)
    Now you have 2 tasks in the list.
   ____________________________________________________________
   event project meeting /from 2019-10-20 /to 2019-10-21
   ____________________________________________________________
    Got it. I've added this task:
      [E][ ] project meeting (from: Oct 20 2019 to: Oct 21 2019)
    Now you have 3 tasks in the list.
   ____________________________________________________________
   list
   ____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] read book
    2.[D][ ] return book (by: Oct 15 2019)
    3.[E][ ] project meeting (from: Oct 20 2019 to: Oct 21 2019)
   ____________________________________________________________
   find book
   ____________________________________________________________
    Here are the matching tasks in your list:
    1.[T][ ] read book
    2.[D][ ] return book (by: Oct 15 2019)
   ____________________________________________________________
   mark 2
   ____________________________________________________________
    Nice! I've marked this task as done:
      [D][X] return book (by: Oct 15 2019)
   ____________________________________________________________
   list
   ____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] read book
    2.[D][X] return book (by: Oct 15 2019)
    3.[E][ ] project meeting (from: Oct 20 2019 to: Oct 21 2019)
   ____________________________________________________________
   unmark 2
   ____________________________________________________________
    OK, I've marked this task as not done yet:
      [D][ ] return book (by: Oct 15 2019)
   ____________________________________________________________
   delete 1
   ____________________________________________________________
    Noted. I've removed this task:
      [T][ ] read book
    Now you have 2 tasks in the list.
   ____________________________________________________________
   list
   ____________________________________________________________
    Here are the tasks in your list:
    1.[D][ ] return book (by: Oct 15 2019)
    2.[E][ ] project meeting (from: Oct 20 2019 to: Oct 21 2019)
   ____________________________________________________________
   bye
   ____________________________________________________________
   Bye. Hope to see you again soon!
   ____________________________________________________________
   ```

## Rescheduling tasks

Use `list` to find the task's number, then enter the appropriate command in the chat window or console:

| Task type | Format | Example |
|---|---|---|
| Deadline | `reschedule <task number> /by <yyyy-MM-dd>` | `reschedule 2 /by 2026-09-20` |
| Event | `reschedule <task number> /from <yyyy-MM-dd> /to <yyyy-MM-dd>` | `reschedule 3 /from 2026-09-20 /to 2026-09-22` |

For example, rescheduling a deadline named `return book` produces:

```text
 OK, I've rescheduled this task:
   [D][ ] return book (by: Sep 20 2026)
```

Both event dates are required, and the end must be on or after the start. Earlier, later, past,
and unchanged dates are accepted. Enter exact dates in `yyyy-MM-dd` format; relative delays such
as `tomorrow` or `3 days` are unsupported. Commands and date markers ignore letter case.

Rescheduling preserves the task's description, completion status, type, and list position.
Completed tasks stay completed; use `unmark <task number>` to reopen them. ToDos have no dates
and cannot be rescheduled. Both `list` and `find` display numbers from the full task list.
For example, if `find book` displays tasks `2` and `4`, use `mark 4` to mark the second matching task.
Deleting a task renumbers later tasks; run `list` or `find` again to see their current numbers.

Changes are saved automatically and remain after restarting Olaf. Invalid commands show an
error and leave the tasks unchanged.

## Handling usage errors

Olaf handles the following ten error cases. Correct the input or storage problem and retry as indicated.

| Error | Response and recovery |
|---|---|
| Empty or whitespace-only command | Prompts for a command, including when the input uses Unicode spaces. The GUI disables Send for ordinary blank input. |
| Arguments after `list` or `bye` | Explains that these commands accept no arguments. `bye extra` does not close Olaf. |
| Missing description, keyword, or date field | Names the missing information and shows the command syntax. |
| Invalid task number | Distinguishes missing, noninteger, and oversized numbers; shows the valid range or explains that the list is empty. |
| Invalid date | Identifies the deadline, event start, or event end date and distinguishes incorrect `yyyy-MM-dd` formatting from an impossible calendar date. |
| Event ends before it starts | Rejects the new event or reschedule. Same-day events and valid past dates are accepted. |
| Line breaks in a command | Rejects the entire command before saving; enter one command on a single line. |
| Using a search-result number for the wrong task | Prevents this confusion by showing the full-list task numbers in `find`. |
| Malformed or unreadable saved data | Stops startup with the file path, reason, and line number when available. Repair the file or restore a backup, then restart. |
| Failed or interrupted save | Rejects the change and keeps the previous tasks. Check permissions, free space, and the storage location, then retry. Olaf remains usable. |

Olaf never resets or skips malformed saved records automatically. Back up `data/olaf.txt` before
editing it. The file must be readable UTF-8 text. Existing reversed event ranges from older versions
remain loadable; use `reschedule` to correct them.

Each change is written to a temporary file beside `data/olaf.txt` and then replaces the saved file
atomically. The active task list changes only after saving succeeds. If the filesystem does not
support atomic replacement, Olaf rejects the save; use a local filesystem that supports it.
Temporary files are removed after handled failures when possible. A forced process termination
can leave an `olaf-*.tmp` file, which Olaf does not load as task data. Keep only one Olaf session
open for a given data file; coordinating simultaneous writers is not supported.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.


## AI Use Declaration

As my intended career path is NOT related to software engineering, I choose AI-5 as my AI use level, where I get Codex to do the tasks, and then I myself review the results.
