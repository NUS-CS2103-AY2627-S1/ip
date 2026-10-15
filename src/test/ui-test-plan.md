# AM CLI UI Test Plan

## 1. Purpose and scope

This plan documents black-box tests for the current command-line implementation of AM. It covers the console envelope, command parsing, task-list operations, date handling, persistence, restart behavior, and corrupted-data handling.

The project has Gradle and JUnit tests. Execute the console scenarios manually or from an external harness. The edit scenarios also include a GUI smoke check through the existing text input.

## 2. Current implementation contract

- Use Java 25 to compile and run the application.
- The entry point is `am.Am`; it reads one command per standard-input line.
- Each scenario should end with bye. EOF without bye is not a supported test path because the loop calls Scanner.nextLine().
- Start a fresh process for every scenario. Persistence scenarios intentionally use the same isolated working directory across two processes.
- The application uses the relative path ./data/AM.txt.
- User task numbers are one-based; the parser converts them to zero-based indexes.
- Every response is printed between 65-underscore separator lines. Response text is indented by four spaces, including every line of a multi-line response.
- Startup contains the seven-line AM banner, My name is AM., and What do you want?. Farewell is You may leave, but I will be here..
- An empty list or past response shows `I don't see anything` between the separators.
- A find response contains only the matching task lines, with no header. No matches produce `I don't see anything` between the separators.

### Supported commands

| Command | Current behavior |
| --- | --- |
| todo <name> | Adds a todo task. The name is trimmed and may contain spaces. |
| deadline <name> /by <date-or-date-time> | Adds a deadline task. |
| event <name> /from <date-or-date-time> /to <date-or-date-time> | Adds an event task. |
| list | Displays all tasks in insertion order. |
| past | Displays deadline/event tasks whose end time is before LocalDateTime.now(). Todo tasks are never past. |
| find <keyword> | Displays tasks whose descriptions contain the keyword, case-insensitively, while retaining their original task numbers. |
| mark <number> | Marks the selected task done. |
| unmark <number> | Marks the selected task not done. |
| delete <number> | Removes the selected task. |
| edit <number> [/name <name>] [/by <value>] [/from <value>] [/to <value>] | Updates supplied fields and preserves the task number, type, completion, and omitted details. |
| bye | Prints the farewell response and terminates. |

### Date formats and display

Structured task values accept either:

- yyyy-MM-dd, meaning 11:59 pm for a deadline, midnight for an event start, and 11:59 pm for an event end; or
- yyyy-MM-dd HHmm in 24-hour time, such as 2026-08-28 1400.

Dates display as MMM dd yyyy h:mm a, such as Aug 28 2026 2:00 pm. Invalid date values produce When is that? and do not add a task.

### Persistence format

Each saved task occupies one line:

~~~
T | 0-or-1 | name
D | 0-or-1 | name | yyyy-MM-ddTHH:mm
E | 0-or-1 | name | yyyy-MM-ddTHH:mm | yyyy-MM-ddTHH:mm
~~~

The data directory and file are created on the first successful mutation that calls Storage.save(). Starting with no data file and only issuing list/bye does not create the file.

## 3. Console UI scenarios

For each scenario, compare response bodies and verify the process exit status. Unless stated otherwise, begin with no data directory and no ./data/AM.txt.

### UI-00: Verify startup and farewell envelope

Input:

~~~
bye
~~~

Verify this structure:

~~~
<separator>
<seven indented banner lines>
    My name is AM.
    What do you want?
<separator>
<separator>
    You may leave, but I will be here.
<separator>
~~~

The banner characters, separator length, four-space indentation, response ordering, and successful exit must be preserved.

### UI-01: List an empty task list

Input:

~~~
list
bye
~~~

The list response body is `I don't see anything`. The farewell response is You may leave, but I will be here..

### UI-02: Add and list a todo

Input:

~~~
todo buy bread
list
bye
~~~

Expected bodies:

~~~
added: [T][ ] buy bread
Now you have 1 tasks in the list
~~~

~~~
1. [T][ ] buy bread
~~~

### UI-03: Mark and unmark a task

Input:

~~~
todo return book
mark 1
unmark 1
list
bye
~~~

Expected state-change bodies:

~~~
Marked:
[T][X] return book
~~~

~~~
Unmarked:
[T][ ] return book
~~~

The final list contains exactly 1. [T][ ] return book.

### UI-04: Add structured tasks with supported dates

Input:

~~~
deadline submit report /by 2026-08-28
event project meeting /from 2026-08-28 1400 /to 2026-08-28 1600
list
bye
~~~

Expected bodies:

~~~
added: [D][ ] submit report (by: Aug 28 2026 11:59 pm)
Now you have 1 tasks in the list
~~~

~~~
added: [E][ ] project meeting (from: Aug 28 2026 2:00 pm to: Aug 28 2026 4:00 pm)
Now you have 2 tasks in the list
~~~

~~~
1. [D][ ] submit report (by: Aug 28 2026 11:59 pm)
2. [E][ ] project meeting (from: Aug 28 2026 2:00 pm to: Aug 28 2026 4:00 pm)
~~~

### UI-05: Delete and renumber tasks

Input:

~~~
todo read book
todo return book
todo buy bread
delete 2
list
bye
~~~

Delete response:

~~~
Deleted:
[T][ ] return book
Now you have 2 tasks in the list
~~~

Final list:

~~~
1. [T][ ] read book
2. [T][ ] buy bread
~~~

### UI-06: Complete mixed-task workflow

Input:

~~~
todo read book
todo return book
todo buy bread
mark 1
todo borrow book
deadline submit report /by 2026-08-28
event project meeting /from 2026-08-28 1400 /to 2026-08-28 1600
unmark 1
delete 3
list
bye
~~~

Final list:

~~~
1. [T][ ] read book
2. [T][ ] return book
3. [T][ ] borrow book
4. [D][ ] submit report (by: Aug 28 2026 11:59 pm)
5. [E][ ] project meeting (from: Aug 28 2026 2:00 pm to: Aug 28 2026 4:00 pm)
~~~

This verifies insertion order, all task types, status transitions, deletion, and renumbering.

### UI-07: Display past tasks

Input:

~~~
todo ordinary task
deadline old deadline /by 2000-01-01
event old event /from 2000-01-01 /to 2000-01-02
past
bye
~~~

The past response is:

~~~
2. [D][ ] old deadline (by: Jan 01 2000 11:59 pm)
3. [E][ ] old event (from: Jan 01 2000 12:00 am to: Jan 02 2000 11:59 pm)
~~~

The original task numbers are retained; the todo is not shown.

### UI-08: Reject invalid task numbers

Input:

~~~
todo read book
mark 2
unmark 0
delete 3
list
bye
~~~

The invalid operations return:

~~~
Task 2 does not exist. Choose a task number from 1 to 1. Use list to see your tasks.
You messed up the command.
Example: unmark 1
Task 3 does not exist. Choose a task number from 1 to 1. Use list to see your tasks.
~~~

The list remains:

~~~
1. [T][ ] read book
~~~

### UI-09: Reject missing arguments, duplicate markers, and invalid dates

Input:

~~~
mark
unmark
delete
todo
deadline report
event meeting
deadline report /by 2026-08-28 /by 2026-08-29
event meeting /from 2026-08-28 /to
deadline report /by Sunday
list
bye
~~~

The first eight malformed command lines each produce `You messed up the command.` followed on a new line by `Example: ` and the example for that command in docs/README.md. The final malformed date produces `When is that?`. No task is added, list responds `I don't see anything`, and the application continues after each error.

### UI-10: Unknown commands, case, and whitespace

Input:

~~~
wat
LIST
  list

list
bye
~~~

Only the first two input lines are treated as unknown commands. Their bodies have this form:

~~~
AAAAHHHHHHHHHHHHHHH
You can't tell me to '<the exact input line>'
~~~

Both list commands are accepted and respond `I don't see anything`. The blank input produces no response or separators. Commands remain case-sensitive, while leading and trailing whitespace is accepted.

### UI-11: Save and reload after restart

Run process 1:

~~~
todo buy bread
deadline submit report /by 2026-08-28
event project meeting /from 2026-08-28 1400 /to 2026-08-28 1600
mark 1
bye
~~~

Before process 2, verify ./data/AM.txt contains exactly:

~~~
T | 1 | buy bread
D | 0 | submit report | 2026-08-28T23:59
E | 0 | project meeting | 2026-08-28T14:00 | 2026-08-28T16:00
~~~

Run process 2:

~~~
list
bye
~~~

Expected list:

~~~
1. [T][X] buy bread
2. [D][ ] submit report (by: Aug 28 2026 11:59 pm)
3. [E][ ] project meeting (from: Aug 28 2026 2:00 pm to: Aug 28 2026 4:00 pm)
~~~

### UI-12: Create the data directory and file

Start without ./data/ and run:

~~~
todo buy bread
bye
~~~

Verify successful exit, creation of data/AM.txt, and:

~~~
T | 0 | buy bread
~~~

### UI-13: Persist every mutation

Run:

~~~
todo first task
todo second task
mark 1
unmark 1
delete 2
bye
~~~

The final file contains only:

~~~
T | 0 | first task
~~~

### UI-14: Handle an unknown saved record

Create ./data/AM.txt containing:

~~~
not a valid task record
~~~

Start the application. It prints only the handled body What did you do to my memory?, does not print the startup banner or enter the command loop, exits successfully, and leaves the file unchanged. Storage.load() should report the affected line number in the exception cause when inspected by a unit test or debugger.

### UI-15: Reject repeated structured markers

Input:

~~~
deadline duplicate /by 2026-08-28 /by 2026-08-29
event repeated /from 2026-08-28 /to 2026-08-28 1600 /to 2026-08-28 1700
list
bye
~~~

Both structured commands return `You messed up the command.` followed on a new line by their deadline/event example from docs/README.md. The list remains empty. Also verify that an unknown marker added to an otherwise valid deadline or event receives the same syntax response and leaves the list empty.

### UI-16: Find tasks by keyword

Input:

~~~
todo read book
todo buy bread
deadline return book /by 2026-08-28
find BOOK
find missing
bye
~~~

The first `find` response is:

~~~
1. [T][ ] read book
3. [D][ ] return book (by: Aug 28 2026 11:59 pm)
~~~

The search is case-insensitive, matches text within task descriptions, and retains the original task numbers. The second `find` response is `I don't see anything` between the separators. Neither response contains a header.

### UI-17: Edit details and preserve task identity

Start with no saved tasks. Input:

~~~
todo buy bread
deadline submit report /by 2026-09-11
event project meeting /from 2026-09-10 1400 /to 2026-09-10 1600
mark 3
edit 3 /to 1800
find meeting
edit 3 /name planning
edit 1 /name buy milk
edit 2 /by 2026-09-12
list
bye
~~~

In an English locale, the first edit response body is:

~~~
Edited:
3. [E][X] project meeting (from: Sep 10 2026 2:00 PM to: Sep 10 2026 6:00 PM)
~~~

The find result retains number 3, and the following edit targets that task.
The final list is:

~~~
1. [T][ ] buy milk
2. [D][ ] submit report (by: Sep 12 2026 11:59 PM)
3. [E][X] planning (from: Sep 10 2026 2:00 PM to: Sep 10 2026 6:00 PM)
~~~

Restart in the same working directory and verify this list survives unchanged.
The complete saved file has exactly these records, in this order:

~~~
T | 0 | buy milk
D | 0 | submit report | 2026-09-12T23:59
E | 1 | planning | 2026-09-10T14:00 | 2026-09-10T18:00
~~~

Each changed edit calls the existing Storage.save() to replace the whole file.
It does not append a second copy of the list or add new storage fields.

### UI-18: Reject invalid edits and skip unchanged edits

Begin with the final list from UI-17. Verify these exact response bodies:

| Input | Response |
| --- | --- |
| edit 3 /to 1800 | No changes. |
| edit 3 | You messed up the command.<br>Example: edit 1 /name buy milk |
| edit 3 /name | You messed up the command.<br>Example: edit 1 /name buy milk |
| edit 3 /to 1700 /to 1800 | You messed up the command.<br>Example: edit 1 /name buy milk |
| edit 3 /until 1800 | You messed up the command.<br>Example: edit 1 /name buy milk |
| edit 1 /by 2026-09-12 | You messed up the command.<br>Example: edit 1 /name buy milk |
| edit 3 /type todo | You messed up the command.<br>Example: edit 1 /name buy milk |
| edit 3 /name inspect /tmp | You messed up the command.<br>Example: edit 1 /name buy milk |
| edit 3 /to 2400 | When is that? |
| edit 3 /to 900 | When is that? |
| edit 3 /to 2026-02-30 | When is that? |
| edit 3 /to 1300 | When is that? |
| edit 4 /name missing | Task 4 does not exist. Choose a task number from 1 to 3. Use list to see your tasks. |
| edit 0 /name missing | You messed up the command.<br>Example: edit 1 /name buy milk |

The list and file remain unchanged after every command. A no-op does not call
save. Continue with list and bye to verify errors do not terminate processing.

### UI-19: Date boundaries, multiple fields, and GUI input

Using the UI-17 list, submit:

~~~
edit 3 /to 2000-01-01 1500 /name old meeting /from 2000-01-01 1500
past
edit 3 /from 2000-01-01 /to 2000-01-01
bye
~~~

Both edits succeed and preserve completion. The first allows equal endpoints
and validates the final interval together. Past includes task 3 with number 3.
The second uses midnight for the start and 23:59 for the end.

Also verify an overnight event's time-only end edit keeps its existing end date,
not the start date. Explicit times clear seconds; omitted timestamps preserve
their original precision. These boundaries are covered by TaskEditTest.

Repeat UI-17 and UI-18 through the JavaFX text input. Responses appear in new
bubbles; errors leave input usable. Historical list bubbles are not refreshed.
No new controls, reordering, or type conversion are expected.

### UI-20: Save failure handling

Use AmEditTest to inject a failure into the existing Storage.save() method,
without changing real user file permissions. The edit response is:

~~~
I couldn't access my memory.
~~~

The live list restores the original task and continues to accept commands. A
failure before writing also leaves the file unchanged. A failure after
truncation may leave a partial file; disk rollback is not part of the existing
save contract. Test that the live task is still unchanged in that case.

## 4. Acceptance test matrix

| ID | Priority | Scenario | Pass condition |
| --- | --- | --- | --- |
| CLI-01 | High | UI-00 | Startup and farewell envelopes have the expected separators, indentation, banner, and successful exit. |
| CLI-02 | High | UI-01 | A new process lists no tasks and exits after bye. |
| CLI-03 | High | UI-02 | Todo creation, count, marker, and numbering are correct. |
| CLI-04 | High | UI-03 | Mark and unmark affect only the selected task. |
| CLI-05 | High | UI-04 | Date-only and date-time structured inputs are accepted and displayed in the current format. |
| CLI-06 | High | UI-05 | Deletion removes the selected task and renumbers later tasks. |
| CLI-07 | High | UI-06 | The mixed workflow preserves order, types, status, details, and count. |
| CLI-08 | Medium | UI-07 | Past returns only expired deadline/event tasks with original list numbers. |
| CLI-09 | High | UI-08 | Invalid indexes produce errors and do not mutate the list. |
| CLI-10 | High | UI-09 | Missing arguments, duplicate markers, and invalid dates are rejected without adding tasks. |
| CLI-11 | Medium | UI-10 | Unknown input does not terminate the process; supported commands continue to work. |
| CLI-12 | High | UI-11 | Tasks survive restart with order, type, details, and completion status intact. |
| CLI-13 | High | UI-12 | Missing parent directories and data files are created on save. |
| CLI-14 | High | UI-13 | Add, mark, unmark, and delete mutations are persisted. |
| CLI-15 | High | UI-14 | A corrupted record is handled without overwriting the file. |
| CLI-16 | Medium | UI-15 | Duplicate structured markers are rejected and do not mutate the list. |
| CLI-17 | High | UI-16 | Find returns case-insensitive description matches with original task numbers and `I don't see anything` for no matches. |
| CLI-18 | High | UI-17 | Edits preserve omitted fields, type, completion, order, and count; full-file saving survives restart. |
| CLI-19 | High | UI-18 | Invalid edits leave state unchanged; unchanged edits return No changes. without saving. |
| CLI-20 | High | UI-19 | Multi-field and time-only edits follow date rules; GUI text input uses the same behavior. |
| CLI-21 | High | UI-20 | Save failures report the storage error and preserve the live task list. |

## 5. Parser and model checks

These checks can be implemented directly against CommandParser.parse, Task, and TaskList without launching the CLI.

### Parser: accepted inputs

| Input | Expected result |
| --- | --- |
| bye | ByeCommand |
| list | ListCommand |
| past | PastCommand |
| mark 2 | MarkCommand, internal index 1 |
| unmark 2 | UnmarkCommand, internal index 1 |
| delete 2 | DeleteTaskCommand, internal index 1 |
| todo borrow book | AddTaskCommand with a TodoTask named borrow book |
| deadline submit report /by 2026-08-28 | AddTaskCommand with deadline 2026-08-28T23:59 |
| event meeting /from 2026-08-28 1400 /to 2026-08-28 1600 | AddTaskCommand with the expected two LocalDateTime values |
| find borrow book | FindCommand with keyword borrow book |
| edit 3 /to 1800 | EditCommand with full-list task number 3 and a time-only end update |

### Parser: rejected and boundary inputs

Verify the exception type and message for:

- missing arguments: mark, unmark, delete, todo, deadline, and event;
- non-numeric indexes such as mark abc, unmark 1.5, and delete two;
- missing or repeated /by, /from, and /to values;
- invalid dates such as deadline report /by Sunday;
- missing find keywords such as find;
- unknown names and different command case; leading whitespace is accepted and empty input is ignored.

The parser rejects missing, non-integer, overflowing, and non-positive task numbers with the common syntax message and a command example. Positive indexes outside the list are handled by `am.Am`. Verify outer whitespace is accepted, descriptions retain internal spacing and embedded slashes, and blank input produces an EmptyCommand. Reject blank descriptions, pipes, line breaks within descriptions, and unknown or repeated structured markers.

### Task serialization

Verify delegation and round trips for:

- T | 0 | buy bread -> unfinished TodoTask;
- T | 1 | buy bread -> finished TodoTask;
- D | 0 | submit report | 2026-08-28T23:59 -> unfinished DeadlineTask;
- E | 1 | meeting | 2026-08-28T14:00 | 2026-08-28T16:00 -> finished EventTask.

Unknown type markers, invalid status values, missing fields, and extra fields should throw CorruptedDataException. Valid tasks should serialize back to the documented line format.

### Task, TaskList, and Storage

- New tasks are not done by default; mark/unmark are idempotent.
- toString() uses the correct type/status markers, name, and structured details.
- A new TaskList has length zero and an empty string representation.
- addTask appends; getTask preserves order; deleteTask removes and shifts later tasks.
- markTask, unmarkTask, and deleteTask use zero-based indexes and throw IndexOutOfBoundsException for invalid indexes.
- getPastTasks excludes todo tasks and retains original one-based numbers.
- getMatchingTask returns matching description lines, retains original one-based numbers, and returns an empty string when there are no matches.
- Storage.load() returns an empty list for a missing file.
- Storage.save() creates parent directories, creates/truncates the file, and writes one serialized line per task.
- Load/save preserves subtype, order, fields, and completion status.
- Malformed type/status/field-count records become CorruptedDataException with the affected line number.
- A failed load does not save a replacement file.

### Edit-specific automated checks

- CommandParserTest covers optional fields in any order, internal description
  spacing, embedded slashes, signed integer indexes, duplicate and unknown
  markers, empty values, case sensitivity, and leading whitespace.
- TaskEditTest covers all applicable fields, completion preservation, in-place
  replacement with unchanged list size and order, strict dates, date-only
  defaults, equal endpoints, and time-only edits that preserve the endpoint
  date and untouched precision.
- AmEditTest covers exact responses, full-list numbering after find/past,
  invalid indexes including integer limits, no-op behavior, whole-file saving,
  save/reload, and failures before and after file truncation.
- Use temporary directories for persistence tests. Set the English locale for
  exact date-output assertions and restore it afterward.
- Edit validation rejects blank descriptions, pipes, line breaks, inappropriate
  fields, and reversed final event intervals. Creation also rejects unsafe
  descriptions and malformed markers. Loading behavior remains unchanged.

## 6. Known current limitations to track

These are observations about the current implementation, not additional pass conditions:

1. DeadlineTask.fromSerialized and EventTask.fromSerialized do not wrap an invalid serialized timestamp in CorruptedDataException. Such a line can escape the handled-memory path as an uncaught DateTimeParseException.
2. The application saves after successful add, mark, unmark, and delete commands, and edits that change a task, but does not create data/AM.txt merely by starting or listing an empty list.

Add a regression test for each limitation if it is later fixed, and update the expected UI behavior at the same time.

## 7. Test execution and evidence

Run the automated suite and style checks with Java 25 from the repository root:

~~~powershell
./gradlew.bat test checkstyleMain checkstyleTest
~~~

For console scenarios, use an isolated working directory and an absolute
classpath pointing to the compiled classes. Replace the example project path below:

~~~powershell
java -cp C:\path\to\ip\build\classes\java\main am.Am
~~~

For GUI checks, run the JavaFX launcher with an isolated working directory in
your IDE. Never use the real task data for failure-injection tests.

Run each scenario in an isolated working directory. Retain the exact input script, captured output and exit status, data/AM.txt when persistence is in scope, the source revision, and any observed list mutation after rejected commands.

All High-priority scenarios should pass before release. Re-run Medium-priority scenarios whenever parser normalization, date formatting, persistence, or user-facing messages change.
