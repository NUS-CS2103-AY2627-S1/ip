# Friday

Friday is a command-line chatbot for managing tasks, built from the course's Duke starter template.
The instructions below explain how to set up and run it.

## Setting up in IntelliJ IDEA

Prerequisites: JDK **25.0.3.fx-zulu** and an IntelliJ IDEA version that supports Java 25.

1. Open IntelliJ IDEA and choose `Open` from the welcome screen or the `File` menu.
2. Select this project's root directory and open it.
3. Go to `File` > `Project Structure` > `Project`. Set **Project SDK** to the installed
   **25.0.3.fx-zulu** JDK. If it is missing, use `Add SDK` > `Add JDK from disk` to select its installation.
   Set **Project language level** to **25**, without preview features. See the
   [IntelliJ project settings guide](https://www.jetbrains.com/help/idea/project-settings-and-structure.html).
4. Locate `src/main/java/Friday.java`, right-click it, and choose `Run Friday.main()`.
   Set the run configuration's **Working directory** to the project root so task data is saved under `data/`.
   On the first run, the console shows:

   ```text
   ____________________________________________________________
   Hello! I'm Friday.
   What can I do for you?
   ____________________________________________________________
   ```

## Markdown files in this project
Declaration: I am using AI in the capacity of
[AI-5](https://nus-cs2103-ay2627-s1.github.io/website/admin/courseExpectations.html#use-of-ai)
as specified by the course and specifically using Codex 5.4-mini.

Several `.md` files in the repository help document how to work on the project:

- `AGENTS.md` contains project-specific instructions for Codex.
- `CLAUDE.md` points to the same project instructions for Claude-style workflows.
- `CONTRIBUTORS.md` lists people who have contributed to the project.
- `docs/README.md` is the user guide template for the chatbot.
- `test/ui-test-plan.md` records the command-based UI test cases and expected output.

These files are plain text, so they are easy to read, update, and review with git.

Level-1: Friday's ASCII art banner:
```text
  _____ ____  ___ ____    _ __   __
|  ___|  _ \|_ _|  _ \  / \\ \ / /
| |_  | |_) || || | | |/ _ \\ V /
|  _| |  _ < | || |_| / ___ \| |
|_|   |_| \_\___|____/_/   \_\_|
```

Level-3: The codex and agents.md were made using codex-5.4-mini, to generate the entire skill file

Level-4: Added classes Deadline, To-do, Events using codex-5.4-mini and instructed codex to apply OOP principles such
as polymorphism and inheritance(More details given within the Friday Class)


Level-5: Added handling errors and exceptions using Codex-5.4-mini which helped me check errors that came from 
insufficient testing from Level-0 to LEvel-4 on Friday class 

## Exception handling test cases

The UI test plan includes a few checks for error handling. These help confirm that Friday gives clear feedback when the user enters invalid input:

- `todo` with no description
- Unknown commands like `blah`
- `deadline` without a `/by` field
- `event` without `/from` or `/to`
- `mark` and `unmark` without a task number
- `mark` and `unmark` with a task number that is out of range
- `delete` without a task number
- `delete` with a task number that is out of range

These cases are recorded in `test/ui-test-plan.md` and are meant to keep the chatbot from silently accepting bad input.

Level-6 : Used Codex-5.4 on the deletion code inside the Friday Class
Updated `Friday.java` to handle invalid input more safely, 
including empty `todo` commands, unknown commands, invalid `deadline` and `event` formats, 
and out-of-range task numbers for `mark`, `unmark`, and `delete`. I also added `delete` support 
for removing tasks from the list and updated `test/ui-test-plan.md` so these edge cases are covered by UI regression tests.

Also used it to update the `docs/README.md` file.

## Level 7: Save

Friday now loads tasks from `data/friday.txt` at startup and saves automatically after every task-list change.
The storage layer creates missing folders, preserves todos, deadlines, events, and completion status, and writes
through a temporary file to reduce the risk of replacing valid data with a partial save. Invalid or unreadable
files produce a warning and remain protected from overwriting. Storage and restart behavior are covered by
automated regression tests.

## Level 8: Dates and times

Deadline and event date fields are stored as `LocalDateTime` values rather than free-form strings. Friday accepts
`yyyy-MM-dd`, `yyyy-MM-dd HH:mm`, and `d/M/yyyy HHmm`, rejects impossible dates and backwards event intervals,
and displays dates with readable English month names. The `on yyyy-MM-dd` command finds deadlines and events on
a date while retaining their original task numbers. Date parsing, persistence, validation, and filtering are
covered by unit and console UI tests.

## A-MoreOOP: Iteration 1 — console UI

Extracted `Ui` to own console input, separators, greetings, task feedback, and storage warnings.
`Friday` delegates presentation to `Ui` while retaining command parsing and task operations for now;
`Storage` already handles persistence separately. Existing command output and saved data formats are unchanged.
This keeps the first refactoring step small. The next step is to extract `TaskList`, followed by `Parser`.

## A-MoreOOP: Iteration 2 — task collection

Extracted `TaskList` to own task ordering, addition, deletion, completion changes, and date filtering.
Its operations use the same one-based task numbers as the console, and status changes report whether a save
is needed. It copies the loaded list and supplies an unmodifiable list copy to `Storage`, keeping collection
changes behind its methods while reusing the existing task objects. `Friday` coordinates these operations with
`Ui` and `Storage`; command output and saved data formats remain unchanged. Focused tests cover collection
ownership, task numbering, repeated status changes, and date queries. The next step is to extract `Parser`.

## A-MoreOOP: Iteration 3 — command parser

Extracted `Parser` to identify commands, validate argument syntax, and construct tasks or parsed dates and numbers.
`Friday` now dispatches the parsed command type, checks task numbers against `TaskList`, and saves in one place
after a successful change. Existing command boundaries, whitespace rules, error messages, and storage protection
are preserved. Parser tests and console regression tests cover valid commands and invalid input.

The minimum A-MoreOOP extraction is complete: `Ui` handles interaction, `Storage` handles files, `TaskList` owns
task operations, and `Parser` handles command syntax. A stateless parser and an enum-based dispatch keep this step
small; the optional command-class hierarchy remains a separate stretch increment.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
