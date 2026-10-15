# AI Contribution Log

This file records the work Codex performed on this project. It is organized according to the requested feature levels.

## Level 1 (Echo)

Instruction:

Improve the skeletal version of Duke so that it echos commands entered by the user, and exits when the user types the command `bye`.

What Codex did:

- Coding contribution: I turned the skeletal program into an interactive console application by adding a loop that waits for user input, checks each command, and responds before reading the next line. I also separated repeated output formatting into helper methods so the greeting, echo, and goodbye messages use the same response-box style.
- Reworked `Serina.java` so the program reads user input continuously.
- Added a greeting message at startup.
- Added command echoing for normal user input.
- Added `bye` handling so the program prints the goodbye message and exits.
- Added helper methods for printing response boxes consistently.

Files changed:

- `src/main/java/Serina.java`

## Level 2 (Add, List)

Instruction:

Add the ability to store whatever text entered by the user and display them back to the user when requested.

What Codex did:

- Coding contribution: I added simple in-memory storage using a fixed-size task collection and a counter to track how many tasks are currently stored. I also updated the command handling so `list` is treated as a special command, while other input is added to the list and shown again with numbering.
- Added an in-memory task list.
- Added support for the `list` command.
- Made ordinary input get stored as a task.
- Made `list` print stored tasks in numbered order.
- Kept all data in memory only, with no file saving.
- Added a 100-task limit.
- Added exception handling so Serina prints `"You've reached the maximum number of tasks."`, then prints the normal goodbye message and exits when the limit is reached.

Files changed:

- `src/main/java/Serina.java`

## Level 3 (Mark as Done)

Instruction:

Add the ability to mark tasks as done. Optionally, add the ability to change the status back to not done.

What Codex did:

- Coding contribution: I introduced a `Task` object so each task could store both its description and completion status. I then added command parsing for `mark <number>` and `unmark <number>`, with validation to safely convert the user's one-based task number into the correct array index.
- Created a `Task` class to represent each task.
- Changed the task list from storing raw strings to storing `Task` objects.
- Added done and not-done state to each task.
- Added support for `mark <number>`.
- Added support for `unmark <number>`.
- Added task-number validation for mark and unmark commands.
- Updated task display to show `[X]` for done and `[ ]` for not done.
- Verified that repeated `mark` and `unmark` commands repeat the normal confirmation message.

Files changed:

- `src/main/java/Serina.java`
- `src/main/java/Task.java`

## Level 4 (ToDo, Event, DeadLine)

Instruction:

Add support for tracking three types of tasks:

- ToDos: tasks without any date/time attached.
- Deadlines: tasks that need to be done before a specific date/time.
- Events: tasks that start at a specific date/time and end at a specific date/time.

What Codex did:

- Coding contribution: I refactored the task model into an inheritance structure where `Task` holds the shared description and done status, while `Todo`, `Deadline`, and `Event` provide their own type icons and extra display details. I also added parsing logic that creates the correct subclass while storing every item through the shared `Task` type.
- Refactored `Task` into an abstract base class.
- Added a `Todo` subclass for tasks without date or time information.
- Added a `Deadline` subclass for tasks with `/by` information.
- Added an `Event` subclass for tasks with `/from` and `/to` information.
- Kept all tasks stored through the shared `Task` type so `Todo`, `Deadline`, and `Event` objects could be handled polymorphically.
- Added parsing for `todo`, `deadline`, and `event` commands.
- Kept date and time values as plain strings.
- Updated list, mark, and unmark output to show task type icons such as `[T]`, `[D]`, and `[E]`.
- Added simple format validation for deadline and event commands.

Files changed:

- `src/main/java/Serina.java`
- `src/main/java/Task.java`
- `src/main/java/Todo.java`
- `src/main/java/Deadline.java`
- `src/main/java/Event.java`

## Level 5 (Handle Errors)

Instruction:

Teach the chatbot to deal with errors such as incorrect inputs entered by the user. Use exceptions to handle errors, including a custom exception class named to match the chatbot.

What Codex did:

- Coding contribution: I added a custom `SerinaException` class and changed the command parser so invalid user input is reported through exceptions instead of being silently accepted. I also made `Serina.java` catch these exceptions in one place and print friendly messages without crashing the chatbot.
- Added `SerinaException` for chatbot-specific errors.
- Made unknown commands such as `blah` produce `Sorry captain, could you rephrase that for me?`.
- Made empty todo commands such as `todo` produce `Sorry captain, todo descriptions can't be empty.`.
- Added clearer error messages for invalid `deadline`, `event`, `mark`, and `unmark` inputs.
- Stopped treating unrecognized input as a default todo task.
- Kept the 100-task-limit exception behavior, with Serina still printing the goodbye message before exiting.

Files changed:

- `src/main/java/Serina.java`
- `src/main/java/SerinaException.java`
- `AI.md`

## Level 6 (Delete)

Instruction:

Add support for deleting tasks from the list. Use Java Collections classes, such as `ArrayList<Task>`, for storing data.

What Codex did:

- Coding contribution: I refactored Serina's task storage from a fixed `Task[]` array plus manual counter into an `ArrayList<Task>`. This made deletion simpler because Serina can remove the selected task directly and the remaining tasks shift automatically.
- Added support for `delete <number>`.
- Added a helper method that removes and returns the selected task.
- Reused the same task-number validation logic used by `mark` and `unmark`.
- Added a delete confirmation message that shows the removed task and the updated task count.
- Kept the earlier 100-task limit behavior while using `ArrayList<Task>` for storage.

Files changed:

- `src/main/java/Serina.java`
- `AI.md`

## Level A-enums

Instruction:

Use Java enums where they are a natural fit. Amend exception handling to reflect the enum change and update the UI test cases.

What Codex did:

- Coding contribution: I introduced enums for fixed sets of values that should not be represented by loose strings or booleans. Task type icons now come from `TaskType`, task completion state now comes from `TaskStatus`, and Serina-specific error messages now come from `SerinaError`.
- Added `TaskType` for `TODO`, `DEADLINE`, and `EVENT` task icons.
- Added `TaskStatus` for `DONE` and `NOT_DONE` status icons.
- Added `SerinaError` to centralize error messages and whether an error should exit the chatbot.
- Updated `Task` so it uses `TaskStatus` instead of a boolean done flag.
- Updated `Todo`, `Deadline`, and `Event` so they return a `TaskType` instead of hardcoded type icon strings.
- Updated `SerinaException` so it stores a `SerinaError` enum value.
- Simplified exception handling in `Serina.java` so the catch block checks `e.shouldExit()` instead of catching a separate task-limit exception subclass.
- Updated `test-ui.md` with enum regression tests.

Files changed:

- `src/main/java/Serina.java`
- `src/main/java/Task.java`
- `src/main/java/Todo.java`
- `src/main/java/Deadline.java`
- `src/main/java/Event.java`
- `src/main/java/SerinaException.java`
- `src/main/java/TaskType.java`
- `src/main/java/TaskStatus.java`
- `src/main/java/SerinaError.java`
- `AI.md`
- `test-ui.md`

## Level 7 (Save)

Instruction:

Save the tasks on the hard disk automatically whenever the task list changes. For now, implement only the minimal happy path for writing to the file; loading from the file will be implemented later.

What Codex did:

- Coding contribution: I added a small storage layer that writes the current task list to `data/serina.txt` after commands that change the list. This covers adding, marking, unmarking, and deleting tasks while leaving startup loading for a later level.
- Added `Storage` to handle creating the `data` folder and writing task lines to disk.
- Added `toFileString()` behavior to `Task` so all task types share the same save format.
- Added deadline and event save fields so their `/by`, `/from`, and `/to` values are preserved in the file.
- Extended `TaskStatus` so done status saves as `1` and not-done status saves as `0`.
- Added a Serina-specific save error in `SerinaError` for unexpected write failures.
- Updated `test-ui.md` with save-file test cases.

Files changed:

- `src/main/java/Serina.java`
- `src/main/java/Task.java`
- `src/main/java/TaskStatus.java`
- `src/main/java/Deadline.java`
- `src/main/java/Event.java`
- `src/main/java/SerinaError.java`
- `src/main/java/Storage.java`
- `AI.md`
- `test-ui.md`

## Storage (Load)

Instruction:

Load saved task data from the hard disk when the chatbot starts up, using the same file written in the previous save level.

What Codex did:

- Coding contribution: I completed the storage loop by teaching Serina to read `data/serina.txt` on startup and rebuild the in-memory task list before the user enters commands.
- Added `Storage.loadTasks()` so a missing save file starts Serina with an empty list.
- Added parsing for saved todo, deadline, and event lines.
- Added enum helpers in `TaskType` and `TaskStatus` so saved type and status values are converted back safely.
- Added constructors that allow saved tasks to be recreated with their existing done/not-done status.
- Added a Serina-specific load error in `SerinaError`; if loading fails, Serina reports the issue and continues with an empty list.
- Updated `test-ui.md` with load-file test cases.

Files changed:

- `src/main/java/Serina.java`
- `src/main/java/Storage.java`
- `src/main/java/Task.java`
- `src/main/java/Todo.java`
- `src/main/java/Deadline.java`
- `src/main/java/Event.java`
- `src/main/java/TaskType.java`
- `src/main/java/TaskStatus.java`
- `src/main/java/SerinaError.java`
- `AI.md`
- `test-ui.md`

## Storage(Edge Cases)

Instruction:

Enhance the code to handle edge cases and possible errors, while continuing to use relative file paths instead of absolute paths.

What Codex did:

- Coding contribution: I tightened the persistence code so bad or unusual save-file data cannot crash Serina. The storage path remains relative as `data/serina.txt`, which keeps the app portable across computers.
- Made Serina greet first, then report any load problem before accepting commands.
- Confirmed Serina starts normally when `data/serina.txt` and the `data` folder do not exist yet.
- Made the loader ignore blank lines in the save file.
- Added validation for malformed saved tasks, unknown saved task types, invalid saved status values, missing descriptions, and missing deadline/event fields.
- Added a clear error for save files containing more than 100 tasks.
- Escaped `|` and `\` when saving task fields so user-entered text containing the file delimiter can still be saved and loaded correctly.
- Updated `test-ui.md` with edge-case persistence tests, including first-run startup without an existing save file.

Files changed:

- `src/main/java/Serina.java`
- `src/main/java/Storage.java`
- `src/main/java/Task.java`
- `src/main/java/Deadline.java`
- `src/main/java/Event.java`
- `src/main/java/TaskType.java`
- `src/main/java/TaskStatus.java`
- `src/main/java/SerinaError.java`
- `AI.md`
- `test-ui.md`

## Level 8 (Dates and Times)

Instruction:

Teach Serina how to understand dates and times instead of treating deadline and event date fields as plain strings.

What Codex did:

- Coding contribution: I changed deadline and event date fields from raw `String` values into `LocalDate` values, so Serina now parses and stores real dates.
- Added `DateParser` to centralize date parsing and display formatting.
- Updated deadline and event commands to accept dates in `yyyy-MM-dd` format.
- Updated task display so saved dates are shown in a friendlier format such as `Dec 2 2019`.
- Updated save/load behavior so dates are stored in the stable `yyyy-MM-dd` format on disk.
- Added validation for invalid date formats and event end dates that come before start dates.
- Added a `find <date>` command that lists deadlines and events occurring on a given date.
- Updated `test-ui.md` with date parsing, date formatting, invalid date, and `find` command tests.

Files changed:

- `src/main/java/serina/Serina.java`
- `src/main/java/serina/Task.java`
- `src/main/java/serina/Deadline.java`
- `src/main/java/serina/Event.java`
- `src/main/java/serina/DateParser.java`
- `src/main/java/serina/Storage.java`
- `src/main/java/serina/SerinaError.java`
- `AI.md`
- `test-ui.md`

## Level 9 (Find)

Instruction:

Give users a way to find tasks by searching for a keyword in each task description.

What Codex did:

- Changed the `find` command from the earlier date-search extension to description keyword search.
- Added case-insensitive substring matching so commands such as `find BOOK` can match `read book`.
- Kept matching focused on task descriptions rather than task types, statuses, or date metadata.
- Preserved the original task-list order when displaying multiple matches.
- Added an error for an empty `find` keyword and updated the help command.
- Added focused JUnit coverage for matching behavior and Level 9 console cases in `test-ui.md`.

Files changed:

- `src/main/java/serina/Serina.java`
- `src/main/java/serina/exception/SerinaError.java`
- `src/main/java/serina/task/TaskList.java`
- `src/main/java/serina/ui/Ui.java`
- `src/test/java/serina/task/TaskListTest.java`
- `docs/README.md`
- `tests/test-plan.md`
- `AI.md`
- `test-ui.md`

## Help Command

Instruction:

Add a `help` command that lists every command Serina understands, and mention the command in the greeting and
unknown-command error message.

What Codex did:

- Added `help` handling to Serina's command loop without changing the task list or save file.
- Added a concise description and usage format for every supported command.
- Updated the greeting so new users know that they can type `help`.
- Updated the unknown-command error so users are directed to the command list.
- Updated `test-ui.md` with the new greeting and error wording and a complete help-command test.

Files changed:

- `src/main/java/serina/Serina.java`
- `src/main/java/serina/SerinaError.java`
- `AI.md`
- `test-ui.md`

## JUnit Test Coverage

Testing target:

- Keep JUnit tests focused on approximately the top 50% highest-value methods in the codebase.
- Prioritize methods containing complex, core, or critical business logic over trivial accessors and boilerplate.
- Update the JUnit tests after every code change so the test suite continues to meet this coverage target.

What Codex did:

- Added `EventTest` in the matching `serina.task` package under Gradle's conventional `src/test/java` source tree.
- Tested inclusive event date matching at and around both date boundaries.
- Tested user-facing event formatting for both not-done and done tasks.
- Tested save-file serialization, including completion status and escaping special characters.
- Added `TaskListTest` coverage for case-insensitive keyword searches, partial matches, result ordering, surrounding
  whitespace, description-only matching, and searches with no results.

Files changed:

- `src/test/java/serina/task/EventTest.java`
- `src/test/java/serina/task/TaskListTest.java`
- `AI.md`

## Level A-Javadoc

Instruction:

Add JavaDoc header comments to the Java code, with the stretch goal of documenting all non-private classes and
methods as well as non-trivial private methods.

What Codex did:

- Audited every class and method in `src/main/java` for JavaDoc coverage.
- Retained the existing JavaDoc on all non-private classes, constructors, and methods.
- Added JavaDoc to task subtype overrides so their display, persistence, and date-matching contracts are explicit.
- Added JavaDoc to non-trivial private helpers responsible for command loading, date parsing, storage parsing,
  serialization, task-number validation, and numbered task output.
- Included parameter, return-value, and exception details where they clarify the method contract.

Files changed:

- `src/main/java/serina/Serina.java`
- `src/main/java/serina/parser/DateParser.java`
- `src/main/java/serina/storage/Storage.java`
- `src/main/java/serina/task/Deadline.java`
- `src/main/java/serina/task/Event.java`
- `src/main/java/serina/task/Task.java`
- `src/main/java/serina/task/TaskList.java`
- `src/main/java/serina/task/Todo.java`
- `src/main/java/serina/ui/Ui.java`
- `AI.md`

## Level A-Assertions

Instruction:

Use the Java assert feature to document important assumptions that should hold at various points in the code.
Justify each case.

What Codex did:

- Coding contribution: I added Java `assert` statements at internal boundaries where earlier command parsing,
  save-file validation, or object construction should already have guaranteed a safe state. These assertions document
  developer assumptions without replacing user-facing validation, because Java assertions can be disabled at runtime.
- Added command-processing assertions in `Serina.java` to document that UI commands are non-null, command text is
  trimmed before dispatch, parsed task fields are non-blank, date parsing returns a date or throws, task creation
  returns a task or throws, and event dates are chronological before an `Event` is constructed.
- Added task model assertions in `Task.java`, `Deadline.java`, and `Event.java` to document that constructed tasks
  should have non-blank descriptions, parsed statuses, parsed dates, and valid event date ranges.
- Added task-list assertions in `TaskList.java` to document that task collections contain real task objects, respect
  the 100-task maximum, change size predictably after add/delete operations, and convert validated one-based task
  numbers into valid zero-based indexes.
- Added storage assertions in `Storage.java` to document that validated save-file lines parse into tasks, each task
  serializes to exactly one line, save-file records have the expected field counts, and loaded event dates are not
  reversed.
- Added date-parser assertions in `DateParser.java` to document that parsing is called with date text and an error
  value for mapping invalid input to the correct `SerinaException`.

Files changed:

- `src/main/java/serina/Serina.java`
- `src/main/java/serina/parser/DateParser.java`
- `src/main/java/serina/storage/Storage.java`
- `src/main/java/serina/task/Deadline.java`
- `src/main/java/serina/task/Event.java`
- `src/main/java/serina/task/Task.java`
- `src/main/java/serina/task/TaskList.java`
- `AI.md`

## Code Quality Refactoring 1

Instruction:

Review the code against the CS2103 code quality guidelines, identify the single highest-priority issue, and fix it
as one stand-alone refactoring.

What Codex did:

- Coding contribution: I refactored command parsing in `Serina.java` to replace repeated raw command words and
  argument separators with named constants and small helper methods. This makes the parser easier to read and safer
  to change because command matching and argument extraction now depend on one source of truth.
- Identified repeated literals such as `mark`, `unmark`, `todo`, `/by`, `/from`, and `/to` as the highest-priority
  code quality issue because they made the parser mix command intent with low-level string-slicing details.
- Added `isCommand(...)` to centralize the rule that a command can appear alone or followed by a space and arguments.
- Added `getCommandArguments(...)` to centralize argument extraction instead of repeating `substring(...)` with
  command-word lengths throughout the parser.
- Kept the refactoring behavior-preserving; validation and user-facing responses remain unchanged.

Files changed:

- `src/main/java/serina/Serina.java`
- `AI.md`

## Code Quality Refactoring 2

Instruction:

Review the code against the CS2103 code quality guidelines and continue improving code quality using one
stand-alone refactoring per commit.

What Codex did:

- Coding contribution: I refactored the task constructors to use constructor chaining so shared initialization and
  assertions live in one constructor path per class.
- Identified duplicated constructor initialization in `Task`, `Todo`, `Deadline`, and `Event` as the next
  highest-priority code quality issue because the same default status setup and date invariant checks appeared in
  parallel constructors.
- Changed no-status constructors to delegate to the full constructor with `TaskStatus.NOT_DONE`.
- Kept the public constructor APIs unchanged so existing callers and tests continue to work.

Files changed:

- `src/main/java/serina/task/Task.java`
- `src/main/java/serina/task/Todo.java`
- `src/main/java/serina/task/Deadline.java`
- `src/main/java/serina/task/Event.java`
- `AI.md`

## Code Quality Refactoring 3

Instruction:

Review the code against the CS2103 code quality guidelines and continue improving code quality using one
stand-alone refactoring per commit.

What Codex did:

- Coding contribution: I replaced save-file parsing magic numbers and delimiter literals in `Storage.java` with
  named constants that describe the save-file format.
- Identified bare field indexes such as `0`, `1`, `3`, and `4`, plus expected field counts such as `3`, `4`, and
  `5`, as the next highest-priority issue because they forced readers to remember the serialized task layout while
  reading validation and reconstruction logic.
- Added named constants for task field indexes, expected record sizes, the escape marker, and the field delimiter.
- Kept the parsing behavior unchanged while making each validation branch describe the save-file structure more
  directly.

Files changed:

- `src/main/java/serina/storage/Storage.java`
- `AI.md`

## Code Quality Refactoring 4

Instruction:

Review the code against the CS2103 code quality guidelines and continue improving code quality using one
stand-alone refactoring per commit.

What Codex did:

- Coding contribution: I refactored `processCommand(...)` in `Serina.java` so it focuses on deciding which command
  was entered, while separate helper methods handle the details of marking, unmarking, deleting, finding, and adding
  tasks.
- Identified mixed abstraction levels in `processCommand(...)` as the next highest-priority issue because command
  dispatch was interleaved with task mutation, persistence, and response formatting details.
- Added command-specific helper methods to keep each operation small and named by its intent.
- Kept command behavior, validation, save behavior, and user-facing responses unchanged.

Files changed:

- `src/main/java/serina/Serina.java`
- `AI.md`

## Code Quality Refactoring 5

Instruction:

Review the code against the CS2103 code quality guidelines and continue improving code quality using one
stand-alone refactoring per commit.

What Codex did:

- Coding contribution: I extracted save-file validation helpers in `Storage.java` so task reconstruction reads as
  validation followed by construction, without repeating compound error checks in every switch branch.
- Identified repeated field-count and empty-field checks in `parseTask(...)` as the next highest-priority issue
  because they obscured the happy path and mixed validation mechanics with task reconstruction.
- Added `validateFieldCount(...)` to centralize record-size validation.
- Added `validateNonEmptyField(...)` to centralize required-field validation for deadline and event date fields.
- Kept malformed save-file behavior unchanged; invalid records still map to the same load error.

Files changed:

- `src/main/java/serina/storage/Storage.java`
- `AI.md`

## Level A-Streams

Instruction:

Use the Streams feature of Java in the code, if applicable.

What Codex did:

- Coding contribution: I used Java streams for collection operations that naturally read as filtering or mapping
  pipelines, while keeping ordinary loops where they still make control flow clearer.
- Refactored `TaskList.find(...)` to stream over stored tasks, filter descriptions with the existing
  case-insensitive keyword rule, and collect matches in their original order.
- Refactored `Storage.toFileLines(...)` to map each task to its save-file string with a stream before writing the
  lines to disk.
- Kept save-file parsing and command-reading loops unchanged because they involve ordered validation, checked
  exceptions, or user-interface control flow where a loop is easier to understand.

Files changed:

- `src/main/java/serina/task/TaskList.java`
- `src/main/java/serina/storage/Storage.java`
- `AI.md`

## BCD-Extension: Flexible Multi-Term Search

Instruction:

Extend `find` so users can combine partial keywords while preserving the existing command, display, and storage
behavior.

What Codex did:

- Updated `TaskList.find(...)` to split a trimmed query on whitespace and return a task only when every
  case-insensitive keyword is a substring of its description.
- Preserved single-keyword searches, one-character keywords, task-list ordering, subset numbering, empty-result
  output, and description-only matching.
- Kept search read-only, with no changes to storage, command syntax, JavaFX rendering, or dependencies.
- Updated the help text, focused JUnit coverage, test plan, and user guide for multi-term search.

Files changed:

- `src/main/java/serina/task/TaskList.java`
- `src/main/java/serina/ResponseFormatter.java`
- `src/test/java/serina/task/TaskListTest.java`
- `src/test/java/serina/SerinaTest.java`
- `tests/test-plan.md`
- `docs/README.md`
- `AI.md`

## Level A-MoreErrorHandling

Instruction:

Improve Serina so anticipated command, environment, and data errors are handled safely and explained to the
user.

What Codex did:

- Added a dedicated command parser that accepts harmless surrounding or repeated whitespace while validating
  required arguments, task numbers, parameter order, repeated date parameters, and unsupported extra arguments.
- Added strict `yyyy-MM-dd` calendar validation, including rejection of impossible dates and event end dates that
  are equal to or earlier than their start dates.
- Rejected descriptions containing control characters that would make task records unsafe to store.
- Added duplicate detection based on task type, normalized description, and dates while ignoring completion status.
- Made the 100-task limit recoverable so the user can delete a task and continue using Serina.
- Made repeated mark and unmark commands harmless no-ops with clear responses.
- Changed mutating commands to operate on deep copies and publish them only after saving succeeds, preserving the
  active task list when persistence fails.
- Changed storage writes to use a temporary file and atomic replacement so an interrupted write does not truncate
  the existing save file.
- Added validation for non-file save paths, duplicate saved records, malformed records with line-number context,
  and save files containing invalid date ranges.
- Prevented a failed load from becoming an empty writable session that could overwrite damaged or inaccessible
  data; Help and exit remain available while task-data commands explain how to recover.
- Kept invalid GUI input in the command field so the user can correct it without typing the command again.
- Made decorative images optional and gave missing essential FXML or CSS resources a clear startup failure.
- Added focused parser, date, storage, command, and rollback regression tests.

Files changed:

- `src/main/java/serina/Serina.java`
- `src/main/java/serina/ResponseFormatter.java`
- `src/main/java/serina/exception/SerinaError.java`
- `src/main/java/serina/exception/SerinaException.java`
- `src/main/java/serina/gui/MainWindow.java`
- `src/main/java/serina/gui/Main.java`
- `src/main/java/serina/parser/CommandParser.java`
- `src/main/java/serina/parser/CommandType.java`
- `src/main/java/serina/parser/DateParser.java`
- `src/main/java/serina/parser/ParsedCommand.java`
- `src/main/java/serina/storage/Storage.java`
- `src/main/java/serina/task/Deadline.java`
- `src/main/java/serina/task/Event.java`
- `src/main/java/serina/task/Task.java`
- `src/main/java/serina/task/TaskList.java`
- `src/main/java/serina/task/Todo.java`
- `src/test/java/serina/SerinaTest.java`
- `src/test/java/serina/parser/CommandParserTest.java`
- `src/test/java/serina/parser/DateParserTest.java`
- `src/test/java/serina/storage/StorageTest.java`
- `src/test/java/serina/task/TaskListTest.java`
- `docs/README.md`
- `tests/test-plan.md`
- `AI.md`

## Level A-MoreTesting

Instruction:

Write more JUnit tests to cover nearly all code that can be tested automatically, and supplement them with manual
testing for GUI and environment-specific behavior.

What Codex did:

- Added focused JUnit coverage for command parsing, strict date handling, task identity and copying, task-list
  mutation boundaries, response objects and formatting, exceptions, console input/output, and command execution.
- Expanded storage tests to cover missing and empty files, all task types, Unicode and escaped content, malformed
  records, physical line numbers, the 100-task boundary, safe replacement, temporary-file cleanup, and deterministic
  filesystem failures.
- Added command-level tests that verify read-only and repeated no-op commands do not save, while failed add, delete,
  mark, and unmark operations leave the published task list unchanged.
- Added an end-to-end persistence sequence covering todos, deadlines, events, completion state, deletion, and restart.
- Added JUnit parameterized-test support and JaCoCo HTML/XML reporting, with coverage verification for non-GUI code.
- Tagged JavaFX tests so portable tests and display-dependent tests can run independently.
- Extended continuous integration to run portable Java 25 checks on Windows, macOS, and Linux, run JavaFX tests on a
  Linux virtual display, and retain test, coverage, Checkstyle, and GUI artifacts.
- Added a manual test matrix covering operating systems, screen resolutions, display scaling, English and Chinese
  environments, long content, maximum-size task lists, and storage failures that are not portable to automate.
- Tightened save-file validation so descriptions containing only whitespace are rejected consistently.

Files changed:

- `build.gradle`
- `.github/workflows/gradle.yml`
- `src/main/java/serina/storage/Storage.java`
- `src/test/java/serina/CommandResultTest.java`
- `src/test/java/serina/ResponseFormatterTest.java`
- `src/test/java/serina/ResponseMessageTest.java`
- `src/test/java/serina/SerinaDialogueTest.java`
- `src/test/java/serina/SerinaTest.java`
- `src/test/java/serina/exception/SerinaExceptionTest.java`
- `src/test/java/serina/gui/MainWindowTest.java`
- `src/test/java/serina/parser/CommandParserTest.java`
- `src/test/java/serina/parser/DateParserTest.java`
- `src/test/java/serina/storage/StorageTest.java`
- `src/test/java/serina/task/DeadlineTest.java`
- `src/test/java/serina/task/EventTest.java`
- `src/test/java/serina/task/TaskEnumTest.java`
- `src/test/java/serina/task/TaskIdentityTest.java`
- `src/test/java/serina/task/TaskListTest.java`
- `src/test/java/serina/task/TodoTest.java`
- `src/test/java/serina/ui/UiTest.java`
- `tests/test-plan.md`
- `docs/README.md`
- `AI.md`
