# UI Test Plan

## Startup-error sessions

Run each startup-error case in a separate fresh Charlie process before the main session below.

### UI-STARTUP-01 — Reject a saved task with missing fields

**Aim:** Verify that a corrupted saved task reports a friendly error instead of causing an array-index failure.

**Rationale:** A deadline requires its type, status, description, and deadline fields in the save file.

Before starting Charlie, create `data/charlie.txt` with exactly this line:

```text
D | Not done | missing deadline
```

Start Charlie, then enter:

```text
todo new task
list
bye
```

**Expected startup output after the banner and greeting:**

```text
Error loading saved tasks: Invalid number of fields in saved task.
```

**Expected output after entering `todo new task`:**

```text
    ____________________________________________________________
    Cannot save tasks because saved tasks could not be loaded.
    ____________________________________________________________
```

Verify that `data/charlie.txt` still contains the original malformed line after Charlie exits.

**Expected output after entering `list`:**

```text
    ____________________________________________________________
    Your task nest is empty.
    ____________________________________________________________
```

**Expected output after entering `bye`:**

```text
    ____________________________________________________________
    Bye for now! I'll guard the task nest until you return.
    ____________________________________________________________
```

### UI-STARTUP-02 — Handle a save-file read failure

**Aim:** Verify that an I/O failure while reading the save path reports a friendly error.

**Rationale:** The program should not expose an uncaught `IOException` or Java stack trace when saved tasks cannot be read.

Before starting Charlie, remove the previous test file and create a directory named `data/charlie.txt`. Start Charlie, then enter:

```text
bye
```

**Expected startup output after the banner and greeting:**

```text
Error loading saved tasks: Could not read the saved task file.
```

**Expected output after entering `bye`:**

```text
    ____________________________________________________________
    Bye for now! I'll guard the task nest until you return.
    ____________________________________________________________
```

Remove the `data/charlie.txt` directory before preparing the next startup-error case.

### UI-STARTUP-DUPLICATE-01 — Preserve a saved file with duplicate tasks

**Aim:** Verify that duplicate saved tasks are reported and cannot be overwritten by a new task.

**Rationale:** A failed load must protect the original file, even when every individual saved line is valid.

Before starting Charlie, create `data/charlie.txt` with exactly these lines:

```text
T | Not done | repeated
T | Done | repeated
```

Start Charlie, then enter:

```text
todo new task
bye
```

**Expected startup output after the banner and greeting:**

```text
Error loading saved tasks: This task already exists in the list.
```

**Expected output after entering `todo new task`:**

```text
    ____________________________________________________________
    Cannot save tasks because saved tasks could not be loaded.
    ____________________________________________________________
```

**Expected output after entering `bye`:**

```text
    ____________________________________________________________
    Bye for now! I'll guard the task nest until you return.
    ____________________________________________________________
```

Verify that the two original saved lines are unchanged after Charlie exits.

### UI-STARTUP-STATUS-01 — Reject an unknown saved completion status

**Aim:** Verify that a corrupted status is reported instead of being silently treated as not done.

**Rationale:** Only `Done` and `Not done` are valid status values in Charlie's save format.

Before starting Charlie, create `data/charlie.txt` with exactly this line:

```text
T | Finished | invalid status
```

Start Charlie, then enter:

```text
bye
```

**Expected startup output after the banner and greeting:**

```text
Error loading saved tasks: Invalid completion status in saved task.
```

**Expected output after entering `bye`:**

```text
    ____________________________________________________________
    Bye for now! I'll guard the task nest until you return.
    ____________________________________________________________
```

### UI-STARTUP-EVENT-01 — Reject a saved event with a reversed time range

**Aim:** Verify that a saved event must end after it starts.

**Rationale:** Loading must enforce the same chronology rule as interactive event creation.

Before starting Charlie, create `data/charlie.txt` with exactly this line:

```text
E | Not done | invalid meeting | 2026-09-20T11:00 | 2026-09-20T10:00
```

Start Charlie, then enter:

```text
bye
```

**Expected startup output after the banner and greeting:**

```text
Error loading saved tasks: Event end must be after its start.
```

**Expected output after entering `bye`:**

```text
    ____________________________________________________________
    Bye for now! I'll guard the task nest until you return.
    ____________________________________________________________
```

After this case, remove the test file and create an empty directory named `data/charlie.txt` for the
following save-error case.

### UI-STORAGE-02 — Handle a save-file write failure

**Aim:** Verify that an I/O failure while saving tasks reports a friendly error.

**Rationale:** A save failure should be handled by Charlie's command loop instead of exposing a Java stack trace or terminating the program.

Using the `data/charlie.txt` directory prepared after `UI-STARTUP-EVENT-01`, start a fresh Charlie process.
Then enter:

```text
todo unsaved task
list
bye
```

**Expected startup output after the banner and greeting:**

```text
Error loading saved tasks: Could not read the saved task file.
```

**Expected output after entering `todo unsaved task`:**

```text
    ____________________________________________________________
    Cannot save tasks because saved tasks could not be loaded.
    ____________________________________________________________
```

### UI-STORAGE-03 — Keep the list empty after a failed addition

**Aim:** Verify that a task is not added to the in-memory list when it cannot be saved.

**Rationale:** The saved file and current task list should represent the same state after a failed addition.

**Input:**

```text
list
```

**Expected output:**

```text
    ____________________________________________________________
    Your task nest is empty.
    ____________________________________________________________
```

**Expected output after entering `bye`:**

```text
    ____________________________________________________________
    Bye for now! I'll guard the task nest until you return.
    ____________________________________________________________
```

After this case, remove the `data/charlie.txt` directory before continuing.

### UI-STORAGE-01 — Start without a save file

**Aim:** Verify that Charlie starts with an empty task list when no save file exists.

**Rationale:** A missing save file represents a first-time user and must not be treated as a loading error.

Before starting Charlie, ensure that `data/charlie.txt` does not exist. Then enter:

```text
list
bye
```

**Expected output after entering `list`:**

```text
    ____________________________________________________________
    Your task nest is empty.
    ____________________________________________________________
```

**Expected output after entering `bye`:**

```text
    ____________________________________________________________
    Bye for now! I'll guard the task nest until you return.
    ____________________________________________________________
```

### UI-COMMAND-EXIT-01 — Terminate after the exit command

**Aim:** Verify that Charlie terminates immediately after responding to `bye`.

**Rationale:** Extracting exit behavior into `ExitCommand` must preserve both the farewell and the exit signal.

Run this case in a separate fresh Charlie process with no save file. Enter:

```text
bye
```

**Expected output after entering `bye`:**

```text
    ____________________________________________________________
    Bye for now! I'll guard the task nest until you return.
    ____________________________________________________________
```

**Expected process state:** Charlie terminates without waiting for another command.

## Save-failure consistency session

Run the cases in this section in one fresh Charlie process before the main session. First, create `data/charlie.txt` with exactly these lines:

```text
T | Not done | stable task
T | Done | completed task
```

Start Charlie and wait until it has loaded the tasks. Then move the save file aside and create an empty directory named `data/charlie.txt`. This forces subsequent save attempts to fail while preserving the two tasks already loaded in memory.

### UI-STORAGE-CONSISTENCY-STARTUP — Load tasks before forcing save failures

**Aim:** Verify that the consistency session starts normally with its two-task fixture.

**Expected startup output:**

```text
    ____________________________________________________________
      ____ _   _    _    ____  _     ___ _____
     / ___| | | |  / \  |  _ \| |   |_ _| ____|
    | |   | |_| | / _ \ | |_) | |    | ||  _|
    | |___|  _  |/ ___ \|  _ <| |___ | || |___
     \____|_| |_/_/   \_\_| \_\_____|___|_____|
    Oh, hello! I'm Charlie.
    Ready to tackle some tasks together?
    ____________________________________________________________
```

### UI-STORAGE-CONSISTENCY-01 — Reject marking when saving fails

**Input:**

```text
mark 1
```

**Expected output:**

```text
    ____________________________________________________________
    Could not save tasks.
    ____________________________________________________________
```

### UI-STORAGE-CONSISTENCY-02 — Keep status unchanged after failed marking

**Input:**

```text
list
```

**Expected output:**

```text
    ____________________________________________________________
    Here's what's currently in our task nest:
    1.[T][ ] stable task
    2.[T][X] completed task
    ____________________________________________________________
```

### UI-STORAGE-CONSISTENCY-03 — Reject unmarking when saving fails

**Input:**

```text
unmark 2
```

**Expected output:**

```text
    ____________________________________________________________
    Could not save tasks.
    ____________________________________________________________
```

### UI-STORAGE-CONSISTENCY-04 — Keep status unchanged after failed unmarking

**Input:**

```text
list
```

**Expected output:**

```text
    ____________________________________________________________
    Here's what's currently in our task nest:
    1.[T][ ] stable task
    2.[T][X] completed task
    ____________________________________________________________
```

### UI-STORAGE-CONSISTENCY-05 — Reject deletion when saving fails

**Input:**

```text
delete 1
```

**Expected output:**

```text
    ____________________________________________________________
    Could not save tasks.
    ____________________________________________________________
```

### UI-STORAGE-CONSISTENCY-06 — Keep tasks after failed deletion

**Input:**

```text
list
```

**Expected output:**

```text
    ____________________________________________________________
    Here's what's currently in our task nest:
    1.[T][ ] stable task
    2.[T][X] completed task
    ____________________________________________________________
```

### UI-STORAGE-CONSISTENCY-07 — Reject updating when saving fails

**Input:**

```text
update 1 description changed task
```

**Expected output:**

```text
    ____________________________________________________________
    Could not save tasks.
    ____________________________________________________________
```

### UI-STORAGE-CONSISTENCY-08 — Keep task details unchanged after failed updating

**Input:**

```text
list
```

**Expected output:**

```text
    ____________________________________________________________
    Here's what's currently in our task nest:
    1.[T][ ] stable task
    2.[T][X] completed task
    ____________________________________________________________
```

### UI-STORAGE-CONSISTENCY-09 — Exit the consistency session

**Input:**

```text
bye
```

**Expected output:**

```text
    ____________________________________________________________
    Bye for now! I'll guard the task nest until you return.
    ____________________________________________________________
```

After this session, remove the `data/charlie.txt` directory before preparing the main session fixture.

## Session setup

- Required Java version: Java 25
- Compile command: `.\gradlew.bat classes`
- Run command: `java -cp build/classes/java/main charlie.Charlie`
- Before starting Charlie, create `data/charlie.txt` with exactly these lines:

```text
T | Done | loaded todo
D | Not done | loaded deadline | 2026-09-18
E | Done | loaded event | 2026-09-18T09:00 | 2026-09-18T10:00
```

- Run all test cases below in one fresh program session and in the listed order.
- Compare output exactly, except that CRLF and LF line endings are considered equivalent and trailing spaces at the end of a line are ignored.
- Each expected-output block starts with the horizontal line printed after entering the command and ends with that command's final horizontal line.
- Console input echo is part of the transcript but is not part of the expected application output.

## UI-STARTUP-03 — Display the startup greeting

**Aim:** Verify that Charlie displays its banner and greeting when the main test session starts.

**Rationale:** Extracting console interactions into a `Ui` class must preserve the startup presentation exactly.

**Input:** Start Charlie and wait for the initial output. Do not enter a command yet.

**Expected output:**

```text
    ____________________________________________________________
      ____ _   _    _    ____  _     ___ _____
     / ___| | | |  / \  |  _ \| |   |_ _| ____|
    | |   | |_| | / _ \ | |_) | |    | ||  _|
    | |___|  _  |/ ___ \|  _ <| |___ | || |___
     \____|_| |_/_/   \_\_| \_\_____|___|_____|
    Oh, hello! I'm Charlie.
    Ready to tackle some tasks together?
    ____________________________________________________________
```

## UI-LOAD-01 — List tasks loaded at startup

**Aim:** Verify that Charlie restores todos, deadlines, events, and their completion states from the save file.

**Rationale:** Listing all preloaded task types confirms that startup loading reconstructs each saved field correctly.

**Input:**

```text
list
```

**Expected output:**

```text
    ____________________________________________________________
    Here's what's currently in our task nest:
    1.[T][X] loaded todo
    2.[D][ ] loaded deadline (by: Sep 18 2026)
    3.[E][X] loaded event (from: Sep 18 2026, 9:00 AM to: Sep 18 2026, 10:00 AM)
    ____________________________________________________________
```

## UI-UPDATE-ERROR-01 — Reject a deadline field for an event

**Aim:** Verify that an event rejects a field belonging only to deadlines.

**Rationale:** Field-format validation is insufficient when the selected task type does not own that field.

**Input:**

```text
update 3 deadline 2026-09-20
```

**Expected output:**

```text
    ____________________________________________________________
    An event does not have a deadline field.
    ____________________________________________________________
```

## UI-UPDATE-DEADLINE-01 — Update a deadline date

**Aim:** Verify that `update` replaces a Deadline's date.

**Rationale:** A successful replacement must retain the Deadline's description, completion status, and list position.

**Input:**

```text
update 2 deadline 2026-09-19
```

**Expected output:**

```text
    ____________________________________________________________
    All changed! Here's the updated task:
      [D][ ] loaded deadline (by: Sep 19 2026)
    ____________________________________________________________
```

## UI-UPDATE-EVENT-01 — Update an event start date-time

**Aim:** Verify that `update` replaces an Event's start date-time.

**Rationale:** Replacing the start should retain the Event's other fields and completed status.

**Input:**

```text
update 3 from 2026-09-18 0830
```

**Expected output:**

```text
    ____________________________________________________________
    All changed! Here's the updated task:
      [E][X] loaded event (from: Sep 18 2026, 8:30 AM to: Sep 18 2026, 10:00 AM)
    ____________________________________________________________
```

## UI-UPDATE-EVENT-02 — Update an event end date-time

**Aim:** Verify that `update` replaces an Event's end date-time.

**Rationale:** Replacing the end should retain the previously updated start and the completed status.

**Input:**

```text
update 3 to 2026-09-18 1030
```

**Expected output:**

```text
    ____________________________________________________________
    All changed! Here's the updated task:
      [E][X] loaded event (from: Sep 18 2026, 8:30 AM to: Sep 18 2026, 10:30 AM)
    ____________________________________________________________
```

## UI-UPDATE-TYPES-01 — List tasks after type-specific updates

**Aim:** Verify that Deadline and Event replacements remain in their original list positions.

**Rationale:** Confirmation messages alone do not prove that the replacements were retained by `TaskList`.

**Input:**

```text
list
```

**Expected output:**

```text
    ____________________________________________________________
    Here's what's currently in our task nest:
    1.[T][X] loaded todo
    2.[D][ ] loaded deadline (by: Sep 19 2026)
    3.[E][X] loaded event (from: Sep 18 2026, 8:30 AM to: Sep 18 2026, 10:30 AM)
    ____________________________________________________________
```

## UI-LOAD-02 — Remove the loaded event

**Aim:** Remove the preloaded event as part of returning to an empty list for the existing test sequence.

**Rationale:** This also verifies that a task reconstructed from disk behaves like a newly created task.

**Input:**

```text
delete 3
```

**Expected output:**

```text
    ____________________________________________________________
    I've cleared this task from our trail:
      [E][X] loaded event (from: Sep 18 2026, 8:30 AM to: Sep 18 2026, 10:30 AM)
    There are now 2 tasks in our nest.
    ____________________________________________________________
```

## UI-LOAD-03 — Remove the loaded deadline

**Aim:** Remove the preloaded deadline before running the original empty-list test sequence.

**Rationale:** Deleting a loaded deadline checks that its list position and stored data are valid.

**Input:**

```text
delete 2
```

**Expected output:**

```text
    ____________________________________________________________
    I've cleared this task from our trail:
      [D][ ] loaded deadline (by: Sep 19 2026)
    There is now 1 task in our nest.
    ____________________________________________________________
```

## UI-LOAD-04 — Remove the loaded todo

**Aim:** Return to an empty task list before running the original test sequence.

**Rationale:** Removing the final loaded task also verifies that saving an empty task list clears the save file.

**Input:**

```text
delete 1
```

**Expected output:**

```text
    ____________________________________________________________
    I've cleared this task from our trail:
      [T][X] loaded todo
    There are now 0 tasks in our nest.
    ____________________________________________________________
```

## UI-COMMAND-INDEX-01 — Reject marking when the task list is empty

**Aim:** Verify that `mark` reports an empty task list before any new tasks are added.

**Rationale:** Moving range validation into `TaskList` must preserve the dedicated empty-list error.

**Input:**

```text
mark 1
```

**Expected output:**

```text
    ____________________________________________________________
    There are no tasks in the list.
    ____________________________________________________________
```

## UI-01 — Add a todo

**Aim:** Verify that `todo` creates an incomplete todo with the correct description and confirmation.

**Rationale:** This is the simplest task-creation command, so it establishes that task descriptions, status symbols, and task counts are displayed correctly.

**Input:**

```text
todo borrow book
```

**Expected output:**

```text
    ____________________________________________________________
    A happy little roar! I've tucked this task safely into the nest:
      [T][ ] borrow book
    There is now 1 task in our nest.
    ____________________________________________________________
```

## UI-COMMAND-ADD-01 — List tasks after adding a todo

**Aim:** Verify that listing immediately after adding a todo shows the new task.

**Rationale:** Extracting task creation into `AddCommand` must add the task to the shared `TaskList`.

**Input:**

```text
list
```

**Expected output:**

```text
    ____________________________________________________________
    Here's what's currently in our task nest:
    1.[T][ ] borrow book
    ____________________________________________________________
```

## UI-ERROR-DUPLICATE-01 — Reject a duplicate task with repeated whitespace

**Aim:** Verify that repeated spaces are normalized and do not bypass task uniqueness checks.

**Rationale:** A visually identical todo should not be stored twice because its input used extra spaces.

**Input:**

```text
  todo   borrow    book
```

**Expected output:**

```text
    ____________________________________________________________
    This task already exists in the list.
    ____________________________________________________________
```

## UI-02 — Add a deadline

**Aim:** Verify that `/by` separates the deadline description from its date or time.

**Rationale:** A deadline contains two user-provided fields, making this case necessary to check both delimiter parsing and deadline formatting.

**Input:**

```text
deadline return book /by 2026-09-20
```

**Expected output:**

```text
    ____________________________________________________________
    A happy little roar! I've tucked this task safely into the nest:
      [D][ ] return book (by: Sep 20 2026)
    There are now 2 tasks in our nest.
    ____________________________________________________________
```

## UI-03 — Add an event

**Aim:** Verify that `/from` and `/to` separate the event description, start, and end.

**Rationale:** An event uses two delimiters and three fields, so it checks the most complex task parser and its output order.

**Input:**

```text
event project meeting /from 2026-09-21 1400 /to 2026-09-23 1600
```

**Expected output:**

```text
    ____________________________________________________________
    A happy little roar! I've tucked this task safely into the nest:
      [E][ ] project meeting (from: Sep 21 2026, 2:00 PM to: Sep 23 2026, 4:00 PM)
    There are now 3 tasks in our nest.
    ____________________________________________________________
```

## UI-04 — List all tasks

**Aim:** Verify that `list` displays every task in insertion order with one-based numbering.

**Rationale:** This case uses the state created by the preceding cases to check that different task types are retained and displayed together correctly.

**Input:**

```text
list
```

**Expected output:**

```text
    ____________________________________________________________
    Here's what's currently in our task nest:
    1.[T][ ] borrow book
    2.[D][ ] return book (by: Sep 20 2026)
    3.[E][ ] project meeting (from: Sep 21 2026, 2:00 PM to: Sep 23 2026, 4:00 PM)
    ____________________________________________________________
```

## UI-ERROR-ARGUMENT-01 — Reject an argument supplied to `list`

**Aim:** Verify that a command which takes no arguments rejects accidental trailing text.

**Rationale:** Silently ignoring text can hide a user's command-format mistake.

**Input:**

```text
list extra
```

**Expected output:**

```text
    ____________________________________________________________
    The list command does not accept arguments.
    ____________________________________________________________
```

## UI-FIND-01 — Find multiple tasks using different keyword capitalization

**Aim:** Verify that `find` displays every task whose description contains the keyword regardless of capitalization.

**Rationale:** An uppercase keyword should match both lowercase book-related descriptions while omitting the unrelated event, and result numbering should be consecutive.

**Input:**

```text
find BOOK
```

**Expected output:**

```text
    ____________________________________________________________
    I sniffed around and found these matching tasks:
    1.[T][ ] borrow book
    2.[D][ ] return book (by: Sep 20 2026)
    ____________________________________________________________
```

## UI-FIND-02 — Find a task using a phrase

**Aim:** Verify that all text after `find` is used as the search phrase.

**Rationale:** Splitting the input only once preserves the complete multi-word phrase instead of searching only for its first word.

**Input:**

```text
find project meeting
```

**Expected output:**

```text
    ____________________________________________________________
    I sniffed around and found these matching tasks:
    1.[E][ ] project meeting (from: Sep 21 2026, 2:00 PM to: Sep 23 2026, 4:00 PM)
    ____________________________________________________________
```

## UI-FIND-03 — Report that no task matches

**Aim:** Verify that `find` clearly reports an unsuccessful search.

**Rationale:** A heading without a result or explanation could leave the user unsure whether the command completed.

**Input:**

```text
find missing
```

**Expected output:**

```text
    ____________________________________________________________
    I sniffed around and found these matching tasks:
    Hmm... I couldn't track down a matching task.
    ____________________________________________________________
```

## UI-FIND-04 — Reject `find` without search text

**Aim:** Verify that the `find` command requires a keyword or phrase.

**Rationale:** Missing search text must produce a friendly error instead of an array-index failure.

**Input:**

```text
find
```

**Expected output:**

```text
    ____________________________________________________________
    Please provide a keyword to find.
    ____________________________________________________________
```

## UI-FIND-05 — Keep tasks unchanged after searching

**Aim:** Verify that all original tasks remain unchanged after the keyword searches.

**Rationale:** `find` is a read-only command and must not alter task contents, status, order, or persistence.

**Input:**

```text
list
```

**Expected output:**

```text
    ____________________________________________________________
    Here's what's currently in our task nest:
    1.[T][ ] borrow book
    2.[D][ ] return book (by: Sep 20 2026)
    3.[E][ ] project meeting (from: Sep 21 2026, 2:00 PM to: Sep 23 2026, 4:00 PM)
    ____________________________________________________________
```

## UI-DATE-01 — Find a deadline on an exact date

**Aim:** Verify that `on` prints a deadline whose date exactly matches the requested date.

**Rationale:** Deadline dates represent a single day rather than a date range.

**Input:**

```text
on 2026-09-20
```

**Expected output:**

```text
    ____________________________________________________________
    Here's what I found for 2026-09-20:
    1.[D][ ] return book (by: Sep 20 2026)
    ____________________________________________________________
```

## UI-DATE-02 — Find an event spanning the requested date

**Aim:** Verify that `on` prints an event when the requested date falls within its date range.

**Rationale:** The event starts on September 21 and ends on September 23, so it should include September 22 even though neither endpoint is on that date.

**Input:**

```text
on 2026-09-22
```

**Expected output:**

```text
    ____________________________________________________________
    Here's what I found for 2026-09-22:
    1.[E][ ] project meeting (from: Sep 21 2026, 2:00 PM to: Sep 23 2026, 4:00 PM)
    ____________________________________________________________
```

## UI-DATE-03 — Report no matching dated tasks

**Aim:** Verify that `on` reports when no deadline or event occurs on the requested date.

**Rationale:** Printing only a heading could leave the user unsure whether the search completed successfully.

**Input:**

```text
on 2026-09-19
```

**Expected output:**

```text
    ____________________________________________________________
    Here's what I found for 2026-09-19:
    Hmm... I couldn't track down anything for this date.
    ____________________________________________________________
```

## UI-DATE-04 — Reject `on` without a date

**Aim:** Verify that `on` requires exactly one date argument.

**Rationale:** Accessing a missing argument must not cause an array-index failure.

**Input:**

```text
on
```

**Expected output:**

```text
    ____________________________________________________________
    Please provide exactly one date in yyyy-MM-dd format.
    ____________________________________________________________
```

## UI-DATE-05 — Reject extra `on` arguments

**Aim:** Verify that `on` rejects input after its single date argument.

**Rationale:** Silently ignoring extra input can hide a user's typing mistake.

**Input:**

```text
on 2026-09-20 extra
```

**Expected output:**

```text
    ____________________________________________________________
    Please provide exactly one date in yyyy-MM-dd format.
    ____________________________________________________________
```

## UI-DATE-06 — Reject a weekday name

**Aim:** Verify that `on` rejects a weekday that does not identify one exact calendar date.

**Rationale:** `Sunday` is ambiguous because it does not state which week's Sunday is intended.

**Input:**

```text
on Sunday
```

**Expected output:**

```text
    ____________________________________________________________
    Date must be a valid date in yyyy-MM-dd format.
    ____________________________________________________________
```

## UI-DATE-07 — Reject an impossible calendar date

**Aim:** Verify that `on` rejects a correctly shaped but nonexistent date.

**Rationale:** Calendar validation must prevent dates such as February 30 from being searched.

**Input:**

```text
on 2026-02-30
```

**Expected output:**

```text
    ____________________________________________________________
    Date must be a valid date in yyyy-MM-dd format.
    ____________________________________________________________
```

## UI-COMMAND-ON-01 — Keep tasks unchanged after date searches

**Aim:** Verify that listing after the date searches displays every original task unchanged.

**Rationale:** Extracting date-search behavior into `OnCommand` must keep the operation read-only.

**Input:**

```text
list
```

**Expected output:**

```text
    ____________________________________________________________
    Here's what's currently in our task nest:
    1.[T][ ] borrow book
    2.[D][ ] return book (by: Sep 20 2026)
    3.[E][ ] project meeting (from: Sep 21 2026, 2:00 PM to: Sep 23 2026, 4:00 PM)
    ____________________________________________________________
```

## UI-05 — Reject an invalid command

**Aim:** Verify that an unknown command displays a helpful error without terminating Charlie.

**Rationale:** Custom exceptions should be handled inside the input loop so the chatbot remains available after invalid input.

**Input:**

```text
hello
```

**Expected output:**

```text
    ____________________________________________________________
    Oh! I don't recognize that command yet.
    ____________________________________________________________
```

## UI-06 — Continue after an invalid command

**Aim:** Verify that Charlie still accepts commands after handling an invalid command.

**Rationale:** Printing an error is insufficient if the exception causes the input loop to end.

**Input:**

```text
list
```

**Expected output:**

```text
    ____________________________________________________________
    Here's what's currently in our task nest:
    1.[T][ ] borrow book
    2.[D][ ] return book (by: Sep 20 2026)
    3.[E][ ] project meeting (from: Sep 21 2026, 2:00 PM to: Sep 23 2026, 4:00 PM)
    ____________________________________________________________
```

## UI-07 — Mark a task as done

**Aim:** Verify that `mark` changes the selected task's status to done and displays the updated task.

**Rationale:** Marking changes existing task state, so the response must identify the selected task and show its completed status symbol.

**Input:**

```text
mark 2
```

**Expected output:**

```text
    ____________________________________________________________
    Tiny victory roar! This task is done:
      [D][X] return book (by: Sep 20 2026)
    ____________________________________________________________
```

## UI-COMMAND-MARK-01 — List tasks after marking

**Aim:** Verify that listing immediately after `mark` shows the selected task as done.

**Rationale:** Extracting mark behavior into `MarkCommand` must retain the mutation in `TaskList`.

**Input:**

```text
list
```

**Expected output:**

```text
    ____________________________________________________________
    Here's what's currently in our task nest:
    1.[T][ ] borrow book
    2.[D][X] return book (by: Sep 20 2026)
    3.[E][ ] project meeting (from: Sep 21 2026, 2:00 PM to: Sep 23 2026, 4:00 PM)
    ____________________________________________________________
```

## UI-08 — Unmark a task

**Aim:** Verify that `unmark` changes the selected task's status back to not done and displays the updated task.

**Rationale:** Unmarking is the inverse state transition and must restore the incomplete status symbol for the same task.

**Input:**

```text
unmark 2
```

**Expected output:**

```text
    ____________________________________________________________
    That's okay - this task isn't quite ready yet:
      [D][ ] return book (by: Sep 20 2026)
    ____________________________________________________________
```

## UI-COMMAND-UNMARK-01 — List tasks after unmarking

**Aim:** Verify that listing immediately after `unmark` shows the selected task as not done.

**Rationale:** Extracting unmark behavior into `UnmarkCommand` must retain the mutation in `TaskList`.

**Input:**

```text
list
```

**Expected output:**

```text
    ____________________________________________________________
    Here's what's currently in our task nest:
    1.[T][ ] borrow book
    2.[D][ ] return book (by: Sep 20 2026)
    3.[E][ ] project meeting (from: Sep 21 2026, 2:00 PM to: Sep 23 2026, 4:00 PM)
    ____________________________________________________________
```

## UI-09 — Delete the third task

**Aim:** Verify that `delete 3` removes the selected event and reports the reduced task count.

**Rationale:** This directly checks the successful deletion behavior supplied by the user, including one-based indexing and the deleted task's exact representation.

**Input:**

```text
delete 3
```

**Expected output:**

```text
    ____________________________________________________________
    I've cleared this task from our trail:
      [E][ ] project meeting (from: Sep 21 2026, 2:00 PM to: Sep 23 2026, 4:00 PM)
    There are now 2 tasks in our nest.
    ____________________________________________________________
```

## UI-COMMAND-DELETE-01 — Reject deleting the removed position again

**Aim:** Verify that deleting task 3 again fails because only two tasks remain.

**Rationale:** Extracting deletion into `DeleteCommand` must update the size of the shared `TaskList`.

**Input:**

```text
delete 3
```

**Expected output:**

```text
    ____________________________________________________________
    Please enter a task number from 1 to 2.
    ____________________________________________________________
```

## UI-10 — List tasks after deletion

**Aim:** Verify that the deleted task is absent and the remaining tasks keep consecutive one-based numbering.

**Rationale:** A confirmation alone does not prove that deletion updated the stored list correctly.

**Input:**

```text
list
```

**Expected output:**

```text
    ____________________________________________________________
    Here's what's currently in our task nest:
    1.[T][ ] borrow book
    2.[D][ ] return book (by: Sep 20 2026)
    ____________________________________________________________
```

## UI-11 — Reject an empty command

**Aim:** Verify that submitting an empty line displays a helpful error.

**Rationale:** Empty input must be handled without terminating Charlie or producing inconsistent response separators.

**Input:** Submit one empty line.

```text

```

**Expected output:**

```text
    ____________________________________________________________
    Um... please give me a command first.
    ____________________________________________________________
```

## UI-12 — Reject a todo without a description

**Aim:** Verify that `todo` requires a description.

**Rationale:** A task without a description cannot convey useful work to the user.

**Input:**

```text
todo
```

**Expected output:**

```text
    ____________________________________________________________
    The task description cannot be empty.
    ____________________________________________________________
```

## UI-13 — Reject a deadline without `/by`

**Aim:** Verify that a deadline requires its `/by` delimiter.

**Rationale:** Without `/by`, Charlie cannot separate the task description from its deadline.

**Input:**

```text
deadline return book
```

**Expected output:**

```text
    ____________________________________________________________
    A deadline must include /by followed by a date.
    ____________________________________________________________
```

## UI-14 — Reject a deadline without a description

**Aim:** Verify that a deadline requires text before `/by`.

**Rationale:** Supplying a date alone does not describe a task.

**Input:**

```text
deadline /by 2026-09-20
```

**Expected output:**

```text
    ____________________________________________________________
    Description cannot be empty.
    ____________________________________________________________
```

## UI-15 — Reject a deadline without a date

**Aim:** Verify that a deadline requires text after `/by`.

**Rationale:** An empty deadline value would create an incomplete Deadline object.

**Input:**

```text
deadline return book /by
```

**Expected output:**

```text
    ____________________________________________________________
    Deadline cannot be empty.
    ____________________________________________________________
```

## UI-ERROR-DELIMITER-01 — Reject a repeated deadline delimiter

**Aim:** Verify that a deadline accepts exactly one `/by` field.

**Rationale:** Repeated fields are ambiguous and should not be interpreted as part of the date.

**Input:**

```text
deadline return book /by 2026-09-20 /by 2026-09-21
```

**Expected output:**

```text
    ____________________________________________________________
    A deadline must include /by exactly once.
    ____________________________________________________________
```

## UI-ERROR-DESCRIPTION-01 — Reject the save-file delimiter in a description

**Aim:** Verify that task descriptions cannot contain Charlie's `|` save-file delimiter.

**Rationale:** Accepting the delimiter would make the task impossible to load reliably later.

**Input:**

```text
todo review A | B
```

**Expected output:**

```text
    ____________________________________________________________
    A task description cannot contain |.
    ____________________________________________________________
```

## UI-PARSER-01 — Reject an event without delimiters

**Aim:** Verify that an event command requires both `/from` and `/to` delimiters.

**Rationale:** Without the delimiters, the parser cannot separate the description, start, and end fields.

**Input:**

```text
event meeting
```

**Expected output:**

```text
    ____________________________________________________________
    Need to include /from or /to fields.
    ____________________________________________________________
```

## UI-16 — Reject an event without a start

**Aim:** Verify that an event requires text between `/from` and `/to`.

**Rationale:** An event with no starting value is incomplete.

**Input:**

```text
event meeting /from /to 2026-09-21 1600
```

**Expected output:**

```text
    ____________________________________________________________
    from/to fields cannot be empty.
    ____________________________________________________________
```

## UI-17 — Reject an event without an end

**Aim:** Verify that an event requires text after `/to`.

**Rationale:** An event with no ending value is incomplete.

**Input:**

```text
event meeting /from 2026-09-21 1400 /to
```

**Expected output:**

```text
    ____________________________________________________________
    from/to fields cannot be empty.
    ____________________________________________________________
```

## UI-18 — Reject `mark` without a task number

**Aim:** Verify that `mark` requires exactly one task number.

**Rationale:** Charlie cannot identify which task to update when the number is absent.

**Input:**

```text
mark
```

**Expected output:**

```text
    ____________________________________________________________
    Please provide exactly one task number.
    ____________________________________________________________
```

## UI-19 — Reject a non-numeric task number

**Aim:** Verify that `mark` rejects text in place of a number.

**Rationale:** Number parsing failures should become friendly CharlieException messages rather than terminate the program.

**Input:**

```text
mark abc
```

**Expected output:**

```text
    ____________________________________________________________
    Please enter a valid task number.
    ____________________________________________________________
```

## UI-20 — Reject an out-of-range task number

**Aim:** Verify that `mark` rejects a number outside the current task list.

**Rationale:** Preventing invalid array access avoids crashes and accidental updates.

**Input:**

```text
mark 999
```

**Expected output:**

```text
    ____________________________________________________________
    Please enter a task number from 1 to 2.
    ____________________________________________________________
```

## UI-COMMAND-INDEX-02 — Reject task number zero

**Aim:** Verify that `mark 0` is rejected as below the one-based task range.

**Rationale:** After range validation moves from `Parser` to `TaskList`, zero must still produce the friendly range error.

**Input:**

```text
mark 0
```

**Expected output:**

```text
    ____________________________________________________________
    Please enter a task number from 1 to 2.
    ____________________________________________________________
```

## UI-TASKLIST-01 — Reject deletion outside the task list

**Aim:** Verify that `delete` rejects a task number outside the current list.

**Rationale:** An invalid deletion must not access or change the task collection.

**Input:**

```text
delete 999
```

**Expected output:**

```text
    ____________________________________________________________
    Please enter a task number from 1 to 2.
    ____________________________________________________________
```

## UI-TASKLIST-02 — Keep tasks after an invalid deletion

**Aim:** Verify that an unsuccessful deletion does not change the task list.

**Rationale:** Rejecting an invalid index is insufficient if the attempted mutation still removes or changes a task.

**Input:**

```text
list
```

**Expected output:**

```text
    ____________________________________________________________
    Here's what's currently in our task nest:
    1.[T][ ] borrow book
    2.[D][ ] return book (by: Sep 20 2026)
    ____________________________________________________________
```

## UI-21 — Reject a deadline that is not an ISO date

**Aim:** Verify that deadline values must use the `yyyy-MM-dd` input format.

**Rationale:** Storing deadlines as `LocalDate` requires Charlie to reject text that cannot be converted into a real calendar date without terminating the program.

**Input:**

```text
deadline return book /by Sunday
```

**Expected output:**

```text
    ____________________________________________________________
    Deadline must be a valid date in yyyy-MM-dd format.
    ____________________________________________________________
```

## UI-22 — Reject an event with an invalid date-time

**Aim:** Verify that event start and end values use the `yyyy-MM-dd HHmm` format.

**Rationale:** Event values must be convertible into real `LocalDateTime` objects without terminating Charlie.

**Input:**

```text
event meeting /from Monday 2pm /to Monday 4pm
```

**Expected output:**

```text
    ____________________________________________________________
    Event dates must use the yyyy-MM-dd HHmm format.
    ____________________________________________________________
```

## UI-23 — Reject an event that does not end after it starts

**Aim:** Verify that an event's end date-time must occur after its start date-time.

**Rationale:** An event ending before or exactly when it starts does not represent a valid time interval.

**Input:**

```text
event meeting /from 2026-09-21 1600 /to 2026-09-21 1400
```

**Expected output:**

```text
    ____________________________________________________________
    Event end must be after its start.
    ____________________________________________________________
```

## UI-COMMAND-01 — Recognize a command with leading whitespace

**Aim:** Verify that command recognition continues to ignore whitespace before a command keyword.

**Rationale:** Renaming the internal command type must not change how the parser recognizes user commands.

**Input:**

```text
   list
```

**Expected output:**

```text
    ____________________________________________________________
    Here's what's currently in our task nest:
    1.[T][ ] borrow book
    2.[D][ ] return book (by: Sep 20 2026)
    ____________________________________________________________
```

## UI-COMMAND-02 — Keep the task list unchanged after listing

**Aim:** Verify that running `list` again displays the same tasks in the same order.

**Rationale:** Extracting list behavior into a command object must keep `list` as a read-only operation.

**Input:**

```text
list
```

**Expected output:**

```text
    ____________________________________________________________
    Here's what's currently in our task nest:
    1.[T][ ] borrow book
    2.[D][ ] return book (by: Sep 20 2026)
    ____________________________________________________________
```

## UI-UPDATE-ERROR-02 — Reject an update with a missing value

**Aim:** Verify that `update` explains its required command structure when an argument is missing.

**Rationale:** Rejecting incomplete input early prevents access to absent argument positions.

**Input:**

```text
update 1 description
```

**Expected output:**

```text
    ____________________________________________________________
    Usage: update TASK_NUMBER FIELD NEW_VALUE.
    ____________________________________________________________
```

## UI-UPDATE-ERROR-03 — Reject a non-numeric update task number

**Aim:** Verify that `update` requires a numeric task number.

**Rationale:** Number conversion failures should produce a user-friendly message instead of terminating Charlie.

**Input:**

```text
update first description changed task
```

**Expected output:**

```text
    ____________________________________________________________
    Please enter a valid task number.
    ____________________________________________________________
```

## UI-UPDATE-ERROR-04 — Reject an unsupported update field

**Aim:** Verify that `update` lists the fields users can select.

**Rationale:** Naming the supported fields helps users correct an unrecognized field without guessing.

**Input:**

```text
update 1 priority high
```

**Expected output:**

```text
    ____________________________________________________________
    Supported update fields are description, deadline, from, and to.
    ____________________________________________________________
```

## UI-UPDATE-ERROR-05 — Reject an event field for a deadline

**Aim:** Verify that a deadline rejects fields belonging only to events.

**Rationale:** A valid date-time value must not allow an incompatible field to reach task replacement.

**Input:**

```text
update 2 from 2026-09-20 0900
```

**Expected output:**

```text
    ____________________________________________________________
    A deadline does not have from or to fields.
    ____________________________________________________________
```

## UI-UPDATE-01 — Update a todo description

**Aim:** Verify that `update` replaces the selected task's description and displays the replacement.

**Rationale:** Updating should preserve the task's type, completion status, and list position while changing only the requested field.

**Input:**

```text
update 1 description borrow library book
```

**Expected output:**

```text
    ____________________________________________________________
    All changed! Here's the updated task:
      [T][ ] borrow library book
    ____________________________________________________________
```

## UI-UPDATE-02 — List tasks after updating

**Aim:** Verify that the replacement remains at the selected list position.

**Rationale:** The confirmation alone does not prove that `TaskList` retained the updated task.

**Input:**

```text
list
```

**Expected output:**

```text
    ____________________________________________________________
    Here's what's currently in our task nest:
    1.[T][ ] borrow library book
    2.[D][ ] return book (by: Sep 20 2026)
    ____________________________________________________________
```

## UI-24 — Exit after persistence checks

**Aim:** End the test session normally and verify the final persisted task list.

**Rationale:** The final file contents confirm that additions, status changes, and deletion have all been written to disk.

**Input:**

```text
bye
```

**Expected output:**

```text
    ____________________________________________________________
    Bye for now! I'll guard the task nest until you return.
    ____________________________________________________________
```

**Expected `data/charlie.txt` contents after exit:**

```text
T | Not done | borrow library book
D | Not done | return book | 2026-09-20
```

## Manual GUI compatibility tests

Run these checks using `./gradlew run` on each available operating system. These cases
cover JavaFX window behavior that is difficult to verify reliably with JUnit.

### MANUAL-GUI-01 — Resize the application window

**Aim:** Verify that the conversation remains usable at different window sizes.

1. Start Charlie and confirm that the window opens at approximately 500 by 620 pixels.
2. Resize it to its minimum width and height.
3. Enlarge it to fill a 1920 by 1080 display.
4. Add enough tasks to make the conversation taller than the window.

**Expected result:** Text wraps inside each message bubble, no horizontal scroll bar appears,
the window cannot shrink below approximately 360 by 420 pixels, the input field and Roar! button
remain visible, and the conversation scrolls to the newest message.

### MANUAL-GUI-02 — Use keyboard and mouse input

**Aim:** Verify both ways of submitting a command.

1. Enter `list` and press Enter.
2. Enter `todo mouse test` and click Roar!.

**Expected result:** Each command and response appears exactly once, the input field clears after
submission, and keyboard focus returns to the input field.

### MANUAL-GUI-03 — Exit from the graphical interface

**Aim:** Verify the JavaFX-only delayed close behavior.

1. Enter `bye`.
2. Observe the window for at least one second.

**Expected result:** Charlie displays the goodbye response, disables the input field and Roar! button,
and closes the window after approximately one second.

### MANUAL-GUI-04 — Check supported operating systems and language settings

**Aim:** Verify that display formatting does not depend on the operating system locale.

Run `list` with a saved deadline and event on each available combination of Windows, macOS, and Linux,
first with an English locale and then with a Chinese locale.

**Expected result:** Charlie starts successfully, controls and message text are readable, dates use
English month names, and saved tasks load and display identically apart from platform-native window
decoration and font rendering.

### MANUAL-GUI-05 — Display Charlie's visual identity

**Aim:** Verify that the personality-themed resources and layout are applied correctly.

1. Start Charlie and inspect the window title, input area, colors, and initial message.
2. Submit `todo visual test` and inspect both new message bubbles.
3. Hover over and press the Roar! button.

**Expected result:** The title is `Charlie's Task Nest`, the input prompt is
`Tell Charlie what to remember...`, and the interface uses the green-and-cream theme. Charlie's
messages are left-aligned beside the Charlie avatar, user messages are right-aligned beside the user
avatar, both avatars are circular and undistorted, and the Roar! button changes shade when hovered
and pressed.
