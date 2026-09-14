# Penguin UI Test Plan

These tests exercise the chatbot through its console input and output.

The JavaFX interface should also be checked manually after visual changes:

- The header displays Pip's name, subtitle, mountain illustration, and tagline.
- Penguin messages appear as pale left-aligned bubbles with a circular avatar.
- User messages appear as blue right-aligned bubbles with a circular avatar.
- `What's on my iceberg?` submits `list`, while the other suggestion chips place an
  editable command template in the composer.
- Enter and the Waddle button both submit commands, clear the composer, and keep
  the newest response visible.
- The layout remains usable at the minimum 680 x 650 window size.

For the packaged project, build the executable JAR with `./gradlew shadowJar`
and launch it using Java 25, as described in Test 34.

Before running a session, use an empty `data/penguin.txt` or a separate temporary working directory so saved tasks from another session do not affect the expected state.

## Test 1: Greeting and exit

Aim: Verify that the chatbot displays its greeting and exits on `bye`.

Input:

```text
bye
```

Expected output: The greeting is displayed, followed by `Bye. Hope to see you again soon!`.

## Test 2: Add a task

Aim: Verify that a task is added to the task list.

Input:

```text
todo read book
bye
```

Expected output: The chatbot confirms that `read book` was added.

## Test 3: List tasks

Aim: Verify that stored tasks are displayed in insertion order.

Input:

```text
todo read book
todo return book
list
bye
```

Expected output: The list contains `read book` as task 1 and `return book` as task 2.

## Test 4: Mark and unmark a task

Aim: Verify that a task can be marked done and then marked incomplete.

Input:

```text
todo read book
mark 1
unmark 1
bye
```

Expected output: The chatbot reports the task as marked, then unmarked.

## Test 5: Create typed tasks

Aim: Verify that user input creates to-do, deadline, and event tasks with their date/time details.

Input:

```text
todo borrow book
deadline return book /by 2099-12-26 1800
event project meeting /from 2099-12-26 1400 /to 2099-12-26 1600
list
bye
```

Expected output: The list contains `[T]`, `[D]`, and `[E]` task markers, with the deadline and event displaying their saved date/time values.

## Test 6: Invalid mark input preserves task state

Aim: Verify that invalid task numbers produce an error and do not change existing tasks.

Input:

```text
todo read book
mark abc
list
mark 99
list
bye
```

Expected output, in order:

- The task is added.
- A valid-task-number error is displayed for `mark abc`.
- The list still shows `1. [T][ ] read book`.
- An invalid-index error is displayed for `mark 99`.
- The list still shows `1. [T][ ] read book`.

## Test 7: Invalid unmark input preserves completed state

Aim: Verify that invalid unmark commands do not undo a valid completion.

Input:

```text
todo read book
mark 1
unmark abc
list
bye
```

Expected output, in order:

- The task is marked done.
- A valid-task-number error is displayed for `unmark abc`.
- The list still shows `1. [T][X] read book`.

## Test 8: Empty input does not add a task

Aim: Verify that an empty command is rejected without changing the task count.

Input:

```text

list
bye
```

Expected output, in order:

- An empty-input error is displayed.
- The chatbot reports that the task list is empty.

## Test 9: Invalid typed-task input is rejected

Aim: Verify that incomplete Level-4 commands are rejected without changing the task list.

Input:

```text
todo valid task
deadline return book
event project meeting
list
todo another task
list
bye
```

Expected output: The incomplete deadline and event produce error messages. The task list remains unchanged after each invalid command.

## Test 10: Empty and unknown commands are rejected

Aim: Verify that empty descriptions and unknown commands do not add tasks.

Input:

```text
todo
blah
list
bye
```

Expected output, in order:

- The empty to-do description produces an error.
- The unknown command produces an error.
- The chatbot reports that the task list is empty.

## Test 11: Malformed separators are rejected

Aim: Verify that missing and duplicate date/time separators are rejected.

Input:

```text
deadline task /by Sunday /by Monday
event meeting /from Monday
event meeting /from Monday /to
event meeting /from Monday /to 4pm /to 5pm
list
bye
```

Expected output: Each malformed command produces an error, and the task list remains empty.

## Test 12: Deadline validation identifies the correct error

Aim: Verify that deadline errors distinguish a missing description, separator, and date/time.

Input:

```text
deadline
deadline submit report
deadline /by Sunday
deadline submit report /by
bye
```

Expected output, in order:

- The empty deadline description produces an error.
- The missing `/by` separator produces an error.
- The missing deadline description produces an error.
- The missing deadline date/time produces an error.

## Test 13: Delete a task and renumber the list

Aim: Verify that deletion removes the selected task, reports the deleted task, and renumbers remaining tasks.

Input:

```text
todo read book
todo return book
todo buy bread
delete 2
list
bye
```

Expected output: The chatbot reports that `return book` was removed. The final list contains `read book` as task 1 and `buy bread` as task 2, and does not contain `return book`.

## Test 14: Invalid deletion preserves the list

Aim: Verify that invalid deletion commands do not remove or alter tasks.

Input:

```text
todo read book
delete
list
delete abc
list
delete 0
list
delete 99
list
bye
```

Expected output: Each invalid deletion produces an appropriate error. Every list still contains exactly `1. [T][ ] read book`.

## Test 15: Delete each task type

Aim: Verify that to-dos, deadlines, and events can all be deleted correctly.

Input:

```text
todo borrow book
deadline return book /by 2099-12-26 1800
event project meeting /from 2099-12-26 1400 /to 2099-12-26 1600
delete 1
list
delete 1
list
delete 1
list
bye
```

Expected output: Each deletion reports the selected task, and the final list is empty.

## Test 16: Enum markers survive state changes

Aim: Verify that marking, unmarking, and deleting tasks do not change their enum-based type markers.

Input:

```text
todo read book
deadline return book /by 2099-12-26 1800
event meeting /from 2099-12-26 1400 /to 2099-12-26 1600
mark 1
unmark 2
delete 1
list
bye
```

Expected output: The final list contains the deadline with `[D][ ]` and the event with `[E][ ]`. The deleted to-do is absent.

## Test 17: Case-insensitive typed commands retain enum types

Aim: Verify that uppercase command keywords and separators still create the correct enum-based task types.

Input:

```text
TODO buy groceries
DEADLINE pay bills /BY 2099-12-26 1800
EVENT dentist /FROM 2099-12-26 1400 /TO 2099-12-26 1600
list
bye
```

Expected output: The list displays `[T][ ]`, `[D][ ]`, and `[E][ ]` in that order.

## Test 18: Command dispatch

Aim: Verify that supported commands are dispatched correctly.

Input:

```text
todo read book
list
mark 1
unmark 1
delete 1
list
bye
```

Expected output: Each command performs its intended action, the deleted task is reported correctly, and the final list is empty.

## Test 19: Commands with invalid arguments

Aim: Verify that recognized commands still validate their arguments.

Input:

```text
list now
mark
delete abc
bye now
list
bye
```

Expected output: Each invalid command produces a specific error. The chatbot remains running until the final valid `bye`, and the task list remains empty.

## Test 20: Save and reload all task types

Aim: Verify that tasks are saved after changes and restored when the chatbot starts again.

First session input:

```text
todo read book
deadline return book /by 2099-12-26 1800
event meeting /from 2099-12-26 1400 /to 2099-12-26 1600
mark 1
bye
```

Restart the chatbot, then enter:

```text
list
bye
```

Expected output after restart:

```text
1. [T][X] read book
2. [D][ ] return book (by: 26 Dec 2099, 6:00PM)
3. [E][ ] meeting (from: 26 Dec 2099, 2:00PM to: 26 Dec 2099, 4:00PM)
```

## Test 21: Save after deletion and unmarking

Aim: Verify that deletion and unmarking are persisted across restarts.

First session input:

```text
todo read book
todo return book
mark 1
unmark 1
delete 2
bye
```

Restart the chatbot, then enter:

```text
list
bye
```

Expected output after restart:

```text
1. [T][ ] read book
```

## Test 22: Missing storage file

Aim: Verify that the chatbot starts with an empty task list when the storage file does not exist.

Setup: Run the chatbot from a fresh temporary directory with no `data/penguin.txt`.

Input:

```text
list
bye
```

Expected output:

```text
Pip: Your task list is empty! Your iceberg is clear.
```

## Test 23: Corrupted storage data

Aim: Verify that malformed saved data produces an error instead of silently creating incorrect tasks.

Setup: Place each malformed line below in `data/penguin.txt`, one test at a time:

```text
T | 2 | read book
D | 0 | return book
E | 0 | meeting | Monday
X | 0 | unknown task
```

Expected output: The chatbot reports a specific loading error and does not silently treat malformed data as a valid task.

## Test 24: Reserved delimiter in a description

Aim: Verify that a description containing the persistence delimiter is rejected or safely handled instead of being truncated or reloaded as a different task.

Input:

```text
todo read | book
todo read|book
list
bye
```

Expected output: The chatbot rejects the reserved `|` character with a clear error, both when entered with spaces (`read | book`) and without spaces (`read|book`). It must not silently save one description and reload a different one.

## Test 25: Find tasks occurring on a date

Aim: Verify that `on` displays deadlines and events occurring on the requested date, while excluding to-dos.

Input:

```text
todo read book
deadline submit report /by 2099-12-26 1800
event project meeting /from 2099-12-26 1400 /to 2099-12-26 1600
on 2099-12-26
mark 2
list
bye
```

Expected output: The matching deadline and event are displayed as tasks 2 and
3. The to-do is not displayed in the date results. `mark 2` marks the deadline,
and the later complete list retains the original numbering.

## Test 26: No tasks on requested date

Aim: Verify that a non-matching date produces a clear message without changing the task list.

Input:

```text
deadline submit report /by 2099-12-26 1800
on 2099-12-27
list
bye
```

Expected output: The chatbot reports that no deadlines or events occur on the requested date, and the original deadline remains unchanged.

## Test 27: Invalid and out-of-range search dates

Aim: Distinguish malformed dates from dates with invalid calendar values.

Input:

```text
on tomorrow
on 2099-13-26
on 2099-12-26 extra
bye
```

Expected output: The first and third commands produce format/argument errors. The second produces a date-out-of-range error. No task is added or changed.

## Test 28: Reject past deadlines and events

Aim: Verify that date/time values before today are rejected.

Input:

```text
deadline old report /by 2000-01-01 1800
event old meeting /from 2000-01-01 1400 /to 2000-01-01 1600
list
bye
```

Expected output: Both commands are rejected with clear past-date errors, and the task list remains empty.

## Test 29: Event start and end ordering

Aim: Verify that events must end after they start.

Input:

```text
event invalid meeting /from 2099-12-26 1800 /to 2099-12-26 1400
list
event zero duration /from 2099-12-26 1800 /to 2099-12-26 1800
list
bye
```

Expected output: Both events are rejected with a start-before-end error. The
task list remains empty after each invalid command.

## Test 30: Date/time persistence and search after restart

Aim: Verify that LocalDateTime values survive persistence and remain searchable.

First session input:

```text
deadline submit report /by 2099-12-26 1800
event project meeting /from 2099-12-26 1400 /to 2099-12-26 1600
bye
```

Restart the chatbot and enter:

```text
on 2099-12-26
bye
```

Expected output: The restored deadline and event are displayed by the `on` command with their correct date/time values.

## Test 31: Ongoing event may start before today

Aim: Verify that an event which started before today is accepted when its end is today or later.

Input:

```text
event ongoing project /from 2026-08-19 1400 /to 2099-12-26 1600
list
bye
```

Expected output: The event is added successfully. Its start date may be before today because its end date is in the future.

## Test 32: Command-object workflow regression

Aim: Verify that command parsing and execution preserve the existing behavior after extracting command classes.

Input:

```text
todo read book
deadline submit report /by 2099-12-26 1800
event project meeting /from 2026-08-19 1400 /to 2099-12-26 1600
mark 1
on 2099-12-26
unmark 1
delete 1
list
bye
```

Expected output: Each command executes successfully, the date search displays the deadline and event, deletion removes only the selected to-do, and the final list contains the deadline and event.

## Test 33: Invalid command objects do not change state

Aim: Verify that validation still occurs before command execution and invalid commands do not mutate the task list.

Input:

```text
todo read book
mark abc
deadline invalid /by 2000-01-01 1800
event invalid /from 2099-12-26 1800 /to 2099-12-26 1400
list
bye
```

Expected output: Each invalid command produces its existing specific error, and the final list still contains only the incomplete `read book` to-do.

## Test 34: Packaged application entry point

Aim: Verify that the packaged JAR launches the JavaFX application.

Setup: Run `./gradlew shadowJar`, copy `build/libs/penguin.jar` into an empty
directory, confirm Java 25 is active, and run `java -jar penguin.jar`.

Input:

```text
Launch the packaged JAR
```

Expected output: The JavaFX window opens, displays Pip's header and welcome
message, and reports no missing resource or JavaFX dependency errors.

## Test 35: Whitespace and duplicate separator validation

Aim: Verify that harmless whitespace is accepted while repeated separators are rejected.

Input:

```text
  todo   read    book
deadline report /by 2099-12-26 1800 /by 2099-12-27 1800
event meeting /from 2099-12-26 1400 /from 2099-12-26 1500 /to 2099-12-26 1600
list
bye
```

Expected output: The to-do is stored with its repeated internal spaces
normalized as `read book`. The deadline and event with duplicate separators
are rejected, and the final list contains only `read book`.

## Test 36: Corrupted record does not prevent valid records from loading

Aim: Verify that an invalid saved record is skipped while later valid records remain available.

Setup: Place the following lines in `data/penguin.txt`:

```text
T | 2 | invalid task
T | 0 | valid task
```

Input:

```text
list
bye
```

Expected output: A loading error is reported for the invalid record, and `valid task` is still displayed.

## Test 37: Persistence after restart and date search

Aim: Verify that dated tasks and completion state survive a restart and remain searchable.

First session input:

```text
todo read book
deadline submit report /by 2099-12-26 1800
event project meeting /from 2099-12-26 1400 /to 2099-12-26 1600
mark 1
bye
```

After restarting, input:

```text
list
on 2099-12-26
bye
```

Expected output: The completed to-do, deadline, and event are restored. The date search displays only the deadline and event with their date/time details.

## Test 38: Find tasks by keyword regardless of case

Aim: Verify that `find` displays only tasks whose descriptions contain the
supplied keyword or phrase, regardless of capitalization.

Input:

```text
todo unrelated
todo Read Book
todo return laptop
deadline submit report /by 2099-12-26 1800
find read book
mark 2
list
bye
```

Expected output: The search displays `Read Book` as task 2 despite the
different capitalization and excludes the other tasks. `mark 2` marks
`Read Book`, and the later list contains all four tasks with only task 2 marked.

## Test 39: Find with whitespace and missing keyword

Aim: Verify that repeated whitespace is accepted and a missing keyword is rejected without changing state.

Input:

```text
todo read book
find   read   book
find
list
bye
```

Expected output: The first search finds `read book`. The missing-keyword command shows a keyword-specific error, and the final list still contains exactly one task.

## Test 40: Find with no matching tasks

Aim: Verify that a search with no matches displays a clear message and leaves the task list unchanged.

Input:

```text
todo read book
find laptop
list
bye
```

Expected output: Penguin reports that no tasks match `laptop`, and the list still contains `read book`.

## Test 41: Malformed saved records with extra fields

Aim: Verify that saved records with unexpected fields are skipped without
creating an incorrect task.

Setup: Place `T | 0 | read book | unexpected` in `data/penguin.txt`.

Input:

```text
list
bye
```

Expected output: Penguin reports a loading error for the malformed record,
does not display it as a valid task, and exits normally after `bye`.

## Test 42: Date search on an event boundary

Aim: Verify that an event is returned when the requested date is exactly its
start date, and is excluded on a date outside its range.

Input:

```text
event conference /from 2099-12-31 0900 /to 2099-12-31 1700
on 2099-12-31
on 2100-01-01
bye
```

Expected output: The event appears for `2099-12-31`, but no event appears for
`2100-01-01`.

## Test 43: GUI sends a valid command

Aim: Verify that the GUI displays both the user's command and Penguin's
response when a valid command is submitted.

Input through the GUI:

```text
todo read book
```

Expected output: The GUI displays the submitted command and confirms that
`read book` was added.

## Test 44: GUI rejects blank input

Aim: Verify that blank or whitespace-only input produces an error and does not
change the task list.

Input through the GUI:

```text
<whitespace only>
```

Expected output: The GUI displays a Penguin dialog containing
`Please input a task.` and does not create a task.

## Test 45: GUI handles invalid commands

Aim: Verify that invalid commands display an error and keep the GUI running.

Input through the GUI:

```text
unknown command
list
```

Expected output: The GUI displays an unknown-command error, then successfully
displays the empty task list when `list` is submitted.

## Test 46: GUI bye command exits

Aim: Verify that a valid `bye` command displays the farewell and closes the
GUI.

Input through the GUI:

```text
bye
```

Expected output: The GUI displays `Bye. Hope to see you again soon!` and then
closes.

## Test 47: GUI does not print responses to the console

Aim: Verify that GUI responses are shown in dialog boxes without unwanted
console output.

Input through the GUI:

```text
todo read book
```

Expected output: The response appears in the GUI and no duplicate response is
printed in the terminal.

## Test 48: Styled GUI displays welcome message

Aim: Verify that the GUI applies its visual styling and displays Penguin's
welcome message when it starts.

Input through the GUI:

```text
Launch the application
```

Expected output: The GUI uses the themed background, rounded input and Waddle
button, styled dialog bubbles, and displays Pip's welcome message.

## Test 49: Responsive chat layout and message animation

Aim: Verify that long messages remain inside the chat area and new messages
fade and slide into view without overlapping existing messages.

Input through the GUI:

```text
find a very long keyword that does not exist
```

Expected output: The response wraps within the chat window, the input controls
remain visible, and the new dialog enters with a short fade-and-slide animation.

## Test 50: Assertions preserve normal command behaviour

Aim: Verify that enabled development assertions do not interfere with valid
commands.

Input through the application:

```text
todo read book
list
bye
```

Expected output: The task is added, displayed, and the application exits
normally without an assertion error.

## Test 51: Undo the most recent task change

Aim: Verify that `undo` restores the previous task-list state.

Input:

```text
todo read book
mark 1
undo
list
bye
```

Expected output: The task is restored to incomplete status and remains in the
list as `1. [T][ ] read book`.

## Test 52: Undo without a previous change

Aim: Verify that `undo` reports an error when there is no history.

Input:

```text
undo
bye
```

Expected output: Penguin reports that there is nothing to undo and exits
normally.

## Test 53: Undo creation, deletion, and unmarking

Aim: Verify that undo restores task creation, deletion, and completion state.

Input:

```text
todo read book
todo buy milk
delete 1
undo
mark 1
unmark 1
undo
list
bye
```

Expected output: Both tasks are restored, and `read book` is displayed as
completed after undoing the unmark operation.

## Test 54: Undo history is limited to five changes

Aim: Verify that only the five most recent changes can be undone.

Input:

```text
todo task 1
todo task 2
todo task 3
todo task 4
todo task 5
todo task 6
undo
undo
undo
undo
undo
undo
list
bye
```

Expected output: The sixth `undo` reports that there is nothing to undo, and
the list contains only `task 1`.

## Test 55: Null and flexible saved-data parsing

Aim: Verify that invalid direct parser input is rejected and valid saved data
with flexible delimiter spacing remains readable.

JUnit coverage:

```text
Parser.parseCommand(null) -> PenguinException
TaskCommandParser.parse(null) -> PenguinException
Parser.parseSavedTask("T|0|read book") -> [T][ ] read book
DateTimeUtil.parseDateTime(null) -> PenguinException
DateTimeUtil.parseDate(null) -> PenguinException
TaskList.addTask(null) -> AssertionError
TaskList.addLoadedTask(null) -> AssertionError
TaskList.restoreSnapshot(null) -> AssertionError
TaskCommandParser rejects reserved delimiters and reversed event times
SavedTaskParser rejects blank descriptions and reversed event times
```

Expected output: Null command and date input produce controlled
`PenguinException` values, while the saved record is parsed as a normal to-do
task.

## Test 56: Past deadlines survive restart

Aim: Verify that a valid deadline remains available after its due date has
passed and can still be loaded from storage.

Setup: Place this valid historical record in `data/penguin.txt`:

```text
D | 0 | historical deadline | 2000-01-01 1800
```

Input:

```text
list
bye
```

Expected output: The deadline is still displayed. Reload validation checks the
record format and date validity without rejecting an expired task.

## Test 57: Flexible typed-task separator whitespace

Aim: Verify that repeated whitespace around typed-task separators is accepted.

Input:

```text
deadline submit report  /by 2099-12-26 1800
event project meeting   /from 2099-12-26 1400   /to 2099-12-26 1600
list
bye
```

Expected output: Both tasks are created with their correct type markers and
date/time values.

## Test 58: Past events survive restart

Aim: Verify that a valid event remains available after its end date has passed
and can still be loaded from storage.

Setup: Place this valid historical record in `data/penguin.txt`:

```text
E | 0 | historical event | 2000-01-01 1400 | 2000-01-01 1600
```

Input:

```text
list
bye
```

Expected output: The event is still displayed. Reload validation checks its
record format and chronological ordering without rejecting an expired event.

## Test 59: GUI reports startup storage warnings

Aim: Verify that the GUI does not hide malformed-record warnings produced while
loading saved tasks.

Setup: Place these records in `data/penguin.txt`:

```text
T | 2 | invalid task
T | 0 | valid task
```

Input: Start the GUI and select the `What's on my iceberg?` suggestion.

Expected output: A Penguin dialog reports that the invalid saved task was
skipped. The later list response still contains `valid task`.

## Test 60: Failed persistence leaves task state unchanged

Aim: Verify that an in-memory change is rolled back when its updated task list
cannot be saved.

Setup: Replace `data/penguin.txt` with a directory of the same name so writes to
that path fail. Start with an empty task list.

Input:

```text
todo read book
list
bye
```

Expected output: Penguin reports the save failure. The subsequent list is still
empty because the failed addition and its undo-history entry were rolled back.

## Test 61: Pip personality remains consistent

Aim: Verify that the chatbot uses its distinctive name and penguin-themed
phrases without obscuring task results.

Input:

```text
todo read book
mark 1
unmark 1
delete 1
bye
```

Expected output: Responses use the `Pip:` prefix and the phrases `Ice work!`,
`Flipper-five!`, `No worries!`, and `Splash!`. The farewell retains the normal
goodbye and adds `Stay cool and keep making progress!`.

## Test 62: Repeated mark and unmark commands preserve undo history

Aim: Verify that marking an already completed task or unmarking an incomplete
task is rejected without adding a misleading undo-history entry.

Input:

```text
todo read book
mark 1
mark 1
undo
list
unmark 1
list
bye
```

Expected output: The second `mark` reports that the task is already marked.
`undo` then reverses the first successful `mark`, so the task is displayed as
`1. [T][ ] read book`. The following `unmark` reports that the task is already
unmarked, and the final list still displays `1. [T][ ] read book`.
