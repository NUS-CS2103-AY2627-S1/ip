# Jonk UI Test Plan

## Session configuration

- Run the test cases in the listed order in one fresh Jonk process. The task list is therefore shared between cases.
- Compile and run with the Java version required by `AGENTS.md`.
- Start the command-line test process with `java -cp build/classes/java/main jonk.Jonk`; `./gradlew run` starts
  the JavaFX application.
- Use `test/gui-test-plan.md` for the JavaFX-specific interaction checks.
- A line whose trimmed content is only Jonk's underscores frames each command response. Record it in the session transcript, but exclude it from the expected-output comparison.
- Before comparison, convert CRLF line endings to LF and expand each tab to four spaces in both actual and expected output. Do not otherwise trim or ignore whitespace or output lines.
- Before starting the session, replace `./data/jonk.txt` with the exact startup fixture below. After each case that specifies expected data-file contents, read the file and compare it exactly after converting CRLF line endings to LF.
- Stop the session immediately after the first failure.
- Check that the startup greeting includes `Type help to see the available commands.`.

**Startup data-file fixture:**

```text
T | 1 | write \| report
D | 0 | return notes \\ room | 2019-10-15

E | 1 | project demo | 2019-10-16 | 2019-10-17
```

## UI-HELP-01: Show help with loaded tasks

**Aim:** Verify that help lists every supported command, examples, date rules, and task-number guidance without rewriting stored tasks.

**Inputs:**

```text
help
```

**Expected output:**

```text
Mission guide online. Here's what Jonk can do:
help - Show this help page.
list - Show all tasks and their task numbers.
todo DESCRIPTION - Add a task without a date.
  Example: todo read book
deadline DESCRIPTION /by DATE - Add a task with a due date.
  Example: deadline return book /by 2026-09-15
event DESCRIPTION /from DATE /to DATE - Add an event with start and end dates.
  Example: event project meeting /from 2026-09-16 /to 2026-09-17
mark NUMBER - Mark a task as done. Example: mark 1
unmark NUMBER - Mark a task as not done. Example: unmark 1
delete NUMBER - Delete a task. Example: delete 1
find KEYWORD - Find tasks containing the keyword. Example: find book
bye - Close mission control.

Replace uppercase placeholders with your own values; do not type the placeholders.
Dates use yyyy-MM-dd, for example 2026-09-15.
Use task numbers from list, starting at 1, for mark, unmark, and delete.
Find matches are case-sensitive; their displayed numbers are not task numbers from list.
Descriptions and keywords may contain spaces and must not be empty.
Command words are lowercase. Type help, list, and bye without arguments.
Help does not change your tasks.
```

**Expected data file after command:**

```text
T | 1 | write \| report
D | 0 | return notes \\ room | 2019-10-15

E | 1 | project demo | 2019-10-16 | 2019-10-17
```

## UI-HELP-02: Reject help arguments

**Aim:** Verify that unsupported help arguments produce a usage error and preserve stored tasks.

**Inputs:**

```text
help todo
```

**Expected output:**

```text
Extra signal detected. Type help on its own to open the mission guide.
```

**Expected data file after command:**

```text
T | 1 | write \| report
D | 0 | return notes \\ room | 2019-10-15

E | 1 | project demo | 2019-10-16 | 2019-10-17
```

## UI-LOAD-01: List loaded tasks

**Aim:** Verify that startup loading recreates all task types and completion states, ignores blank lines, and decodes escaped pipe and backslash characters.

**Inputs:**

```text
list
```

**Expected output:**

```text
Flight plan, coming right up:
    1.[T][X] write | report
    2.[D][ ] return notes \ room (by: Oct 15 2019)
    3.[E][X] project demo (from: Oct 16 2019 to: Oct 17 2019)
```

## UI-LOAD-02: Delete the loaded event

**Aim:** Verify that the loaded event behaves like a normal task and remove it before the existing add-task cases.

**Inputs:**

```text
delete 3
```

**Expected output:**

```text
Mission scrubbed from the flight plan:
    [E][X] project demo (from: Oct 16 2019 to: Oct 17 2019)
Flight plan now holds 2 missions.
```

**Expected data file after command:**

```text
T | 1 | write \| report
D | 0 | return notes \\ room | 2019-10-15
```

## UI-LOAD-03: Delete the loaded deadline

**Aim:** Verify that the loaded deadline behaves like a normal task and remove it before the existing add-task cases.

**Inputs:**

```text
delete 2
```

**Expected output:**

```text
Mission scrubbed from the flight plan:
    [D][ ] return notes \ room (by: Oct 15 2019)
Flight plan now holds 1 mission.
```

**Expected data file after command:**

```text
T | 1 | write \| report
```

## UI-LOAD-04: Delete the loaded todo

**Aim:** Verify that the loaded todo behaves like a normal task and leave an empty list for the existing cases.

**Inputs:**

```text
delete 1
```

**Expected output:**

```text
Mission scrubbed from the flight plan:
    [T][X] write | report
Flight plan now holds 0 missions.
```

**Expected data file after command:** The file is empty (zero bytes).

## UI-HELP-03: Show help with an empty list

**Aim:** Verify that help remains available after all tasks have been deleted and accepts leading spaces.

**Inputs:**

```text
  help
```

**Expected output:**

```text
Mission guide online. Here's what Jonk can do:
help - Show this help page.
list - Show all tasks and their task numbers.
todo DESCRIPTION - Add a task without a date.
  Example: todo read book
deadline DESCRIPTION /by DATE - Add a task with a due date.
  Example: deadline return book /by 2026-09-15
event DESCRIPTION /from DATE /to DATE - Add an event with start and end dates.
  Example: event project meeting /from 2026-09-16 /to 2026-09-17
mark NUMBER - Mark a task as done. Example: mark 1
unmark NUMBER - Mark a task as not done. Example: unmark 1
delete NUMBER - Delete a task. Example: delete 1
find KEYWORD - Find tasks containing the keyword. Example: find book
bye - Close mission control.

Replace uppercase placeholders with your own values; do not type the placeholders.
Dates use yyyy-MM-dd, for example 2026-09-15.
Use task numbers from list, starting at 1, for mark, unmark, and delete.
Find matches are case-sensitive; their displayed numbers are not task numbers from list.
Descriptions and keywords may contain spaces and must not be empty.
Command words are lowercase. Type help, list, and bye without arguments.
Help does not change your tasks.
```

**Expected data file after command:** The file is empty (zero bytes).

## UI-HELP-04: List tasks after help

**Aim:** Verify that viewing help did not add sample tasks or otherwise change the empty task list.

**Inputs:**

```text
list
```

**Expected output:**

```text
Flight plan, coming right up:
```

## UI-01: Add a todo

**Aim:** Verify that a todo with a non-empty description is added as the first task.

**Inputs:**

```text
todo read book
```

**Expected output:**

```text
Mission logged:
    [T][ ] read book
Flight plan now holds 1 mission.
```

**Expected data file after command:**

```text
T | 0 | read book
```

## UI-02: Add a deadline

**Aim:** Verify that a deadline accepts an ISO date and displays it in a human-readable format.

**Inputs:**

```text
deadline return book /by 2019-12-02
```

**Expected output:**

```text
Mission logged:
    [D][ ] return book (by: Dec 2 2019)
Flight plan now holds 2 missions.
```

**Expected data file after command:**

```text
T | 0 | read book
D | 0 | return book | 2019-12-02
```

## UI-03: Add an event

**Aim:** Verify that an event accepts ISO dates and displays them in a human-readable format.

**Inputs:**

```text
event project meeting /from 2019-12-03 /to 2019-12-04
```

**Expected output:**

```text
Mission logged:
    [E][ ] project meeting (from: Dec 3 2019 to: Dec 4 2019)
Flight plan now holds 3 missions.
```

**Expected data file after command:**

```text
T | 0 | read book
D | 0 | return book | 2019-12-02
E | 0 | project meeting | 2019-12-03 | 2019-12-04
```

## UI-04: Reject an empty todo description

**Aim:** Verify that a todo without a description is rejected and not counted.

**Inputs:**

```text
todo
```

**Expected output:**

```text
A todo mission needs a description.
```

## UI-05: Reject an empty deadline value

**Aim:** Verify that a deadline without a `/by` value is rejected and not counted.

**Inputs:**

```text
deadline return book /by
```

**Expected output:**

```text
A deadline mission needs a non-empty /by date.
```

## UI-06: Reject an empty event value

**Aim:** Verify that an event with an empty `/from` value is rejected and not counted.

**Inputs:**

```text
event project meeting /from /to 2019-12-04
```

**Expected output:**

```text
An event mission needs non-empty /from and /to dates.
```

## UI-DATE-01: Reject an impossible deadline date

**Aim:** Verify that an invalid calendar date is rejected without terminating the chatbot or adding a task.

**Inputs:**

```text
deadline invalid date /by 2019-02-29
```

**Expected output:**

```text
Navigation dates must use yyyy-MM-dd format.
```

## UI-DATE-02: Reject an event date in the wrong format

**Aim:** Verify that a non-ISO event date is rejected without terminating the chatbot or adding a task.

**Inputs:**

```text
event invalid date /from 2019/12/03 /to 2019-12-04
```

**Expected output:**

```text
Navigation dates must use yyyy-MM-dd format.
```

## UI-07: List tasks after invalid inputs

**Aim:** Verify that the five invalid tasks were not added to the task list.

**Inputs:**

```text
list
```

**Expected output:**

```text
Flight plan, coming right up:
    1.[T][ ] read book
    2.[D][ ] return book (by: Dec 2 2019)
    3.[E][ ] project meeting (from: Dec 3 2019 to: Dec 4 2019)
```

## UI-FIND-01: Find multiple matching tasks

**Aim:** Verify that `find` displays only tasks whose descriptions contain the keyword and numbers the matches
from one in their original order.

**Inputs:**

```text
find book
```

**Expected output:**

```text
Scanner results—matching missions:
    1.[T][ ] read book
    2.[D][ ] return book (by: Dec 2 2019)
```

## UI-FIND-02: Find no matching tasks

**Aim:** Verify that `find` displays an empty matching-task list when no description contains the keyword.

**Inputs:**

```text
find library
```

**Expected output:**

```text
Scanner results—matching missions:
```

## UI-FIND-CASE: Preserve case-sensitive search

**Aim:** Verify that stream-based filtering keeps the existing case-sensitive matching behavior.

**Inputs:**

```text
find Book
```

**Expected output:**

```text
Scanner results—matching missions:
```

## UI-FIND-03: Reject find without a keyword

**Aim:** Verify that `find` requires a non-empty keyword and does not terminate the chatbot when it is missing.

**Inputs:**

```text
find
```

**Expected output:**

```text
Send a keyword for Jonk to scan.
```

## UI-08: Mark a task

**Aim:** Verify that the first task can be marked as done.

**Inputs:**

```text
mark 1
```

**Expected output:**

```text
Touchdown! Mission complete:
    [T][X] read book
```

**Expected data file after command:**

```text
T | 1 | read book
D | 0 | return book | 2019-12-02
E | 0 | project meeting | 2019-12-03 | 2019-12-04
```

## UI-09: Unmark a task

**Aim:** Verify that the first task can be marked as not done again.

**Inputs:**

```text
unmark 1
```

**Expected output:**

```text
Course corrected. Mission active again:
    [T][ ] read book
```

**Expected data file after command:**

```text
T | 0 | read book
D | 0 | return book | 2019-12-02
E | 0 | project meeting | 2019-12-03 | 2019-12-04
```

## UI-10: Reject an unknown command

**Aim:** Verify that an unrecognized command is rejected without terminating the chatbot.

**Inputs:**

```text
blah
```

**Expected output:**

```text
Signal unclear. Type help to open the mission guide.
```

## UI-11: Reject blank input

**Aim:** Verify that pressing Enter without a command is rejected without terminating the chatbot.

**Inputs:** Press Enter without typing anything; the fenced block below intentionally contains an empty line.

```text

```

**Expected output:**

```text
Signal unclear. Type help to open the mission guide.
```

## UI-12: Reject mark without a task number

**Aim:** Verify that `mark` requires a task number and does not terminate the chatbot when it is missing.

**Inputs:**

```text
mark
```

**Expected output:**

```text
Mission control needs exactly one task number.
```

## UI-13: Reject a non-numeric mark task number

**Aim:** Verify that `mark` rejects a task number that is not a whole number.

**Inputs:**

```text
mark abc
```

**Expected output:**

```text
Task coordinates must be a whole number.
```

## UI-14: Reject mark task number zero

**Aim:** Verify that `mark` rejects zero because task numbers start from one.

**Inputs:**

```text
mark 0
```

**Expected output:**

```text
That mission number is not on the flight plan.
```

## UI-15: Reject mark beyond the list size

**Aim:** Verify that `mark` rejects task number four when the list contains only three tasks.

**Inputs:**

```text
mark 4
```

**Expected output:**

```text
That mission number is not on the flight plan.
```

## UI-16: Reject unmark without a task number

**Aim:** Verify that `unmark` requires a task number and does not terminate the chatbot when it is missing.

**Inputs:**

```text
unmark
```

**Expected output:**

```text
Mission control needs exactly one task number.
```

## UI-17: Reject a non-numeric unmark task number

**Aim:** Verify that `unmark` rejects a task number that is not a whole number.

**Inputs:**

```text
unmark abc
```

**Expected output:**

```text
Task coordinates must be a whole number.
```

## UI-18: Reject unmark task number zero

**Aim:** Verify that `unmark` rejects zero because task numbers start from one.

**Inputs:**

```text
unmark 0
```

**Expected output:**

```text
That mission number is not on the flight plan.
```

## UI-19: Reject unmark beyond the list size

**Aim:** Verify that `unmark` rejects task number four when the list contains only three tasks.

**Inputs:**

```text
unmark 4
```

**Expected output:**

```text
That mission number is not on the flight plan.
```

## UI-20: Mark the last task

**Aim:** Verify that the highest valid task number can be marked as done.

**Inputs:**

```text
mark 3
```

**Expected output:**

```text
Touchdown! Mission complete:
    [E][X] project meeting (from: Dec 3 2019 to: Dec 4 2019)
```

**Expected data file after command:**

```text
T | 0 | read book
D | 0 | return book | 2019-12-02
E | 1 | project meeting | 2019-12-03 | 2019-12-04
```

## UI-21: Unmark the last task

**Aim:** Verify that the highest valid task number can be marked as not done again.

**Inputs:**

```text
unmark 3
```

**Expected output:**

```text
Course corrected. Mission active again:
    [E][ ] project meeting (from: Dec 3 2019 to: Dec 4 2019)
```

**Expected data file after command:**

```text
T | 0 | read book
D | 0 | return book | 2019-12-02
E | 0 | project meeting | 2019-12-03 | 2019-12-04
```

## UI-22: Delete a task

**Aim:** Verify that a valid task number deletes the corresponding task and updates the task count.

**Inputs:**

```text
delete 2
```

**Expected output:**

```text
Mission scrubbed from the flight plan:
    [D][ ] return book (by: Dec 2 2019)
Flight plan now holds 2 missions.
```

**Expected data file after command:**

```text
T | 0 | read book
E | 0 | project meeting | 2019-12-03 | 2019-12-04
```

## UI-23: List tasks after deletion

**Aim:** Verify that the deleted task is absent and later tasks are renumbered.

**Inputs:**

```text
list
```

**Expected output:**

```text
Flight plan, coming right up:
    1.[T][ ] read book
    2.[E][ ] project meeting (from: Dec 3 2019 to: Dec 4 2019)
```

## UI-24: Reject delete without a task number

**Aim:** Verify that `delete` requires a task number and does not terminate the chatbot when it is missing.

**Inputs:**

```text
delete
```

**Expected output:**

```text
Mission control needs exactly one task number.
```

## UI-25: Reject a non-numeric delete task number

**Aim:** Verify that `delete` rejects a task number that is not a whole number.

**Inputs:**

```text
delete abc
```

**Expected output:**

```text
Task coordinates must be a whole number.
```

## UI-26: Reject delete task number zero

**Aim:** Verify that `delete` rejects zero because task numbers start from one.

**Inputs:**

```text
delete 0
```

**Expected output:**

```text
That mission number is not on the flight plan.
```

## UI-27: Reject delete beyond the list size

**Aim:** Verify that `delete` rejects task number three when the list contains only two tasks.

**Inputs:**

```text
delete 3
```

**Expected output:**

```text
That mission number is not on the flight plan.
```

## UI-MORE-01: Reject an overflowing task number

**Aim:** Verify that a number larger than Java's integer range produces a friendly error.

**Inputs:**

```text
mark 2147483648
```

**Expected output:**

```text
Task coordinates must be a whole number.
```

## UI-MORE-02: Reject extra list arguments

**Aim:** Verify that list does not silently accept unsupported arguments.

**Inputs:**

```text
list extra
```

**Expected output:**

```text
Signal unclear. Type help to open the mission guide.
```

## UI-MORE-03: Add Unicode and escaped characters with extra command whitespace

**Aim:** Verify that leading and repeated command spaces are accepted and Unicode, pipes, and backslashes
are preserved in the task description and correctly escaped in storage.

**Inputs:**

```text
  todo   复习 🚀 | notes \ draft
```

**Expected output:**

```text
Mission logged:
    [T][ ] 复习 🚀 | notes \ draft
Flight plan now holds 3 missions.
```

**Expected data file after command:**

```text
T | 0 | read book
E | 0 | project meeting | 2019-12-03 | 2019-12-04
T | 0 | 复习 🚀 \| notes \\ draft
```

## UI-MORE-04: Find a Unicode phrase

**Aim:** Verify that a multiword Unicode keyword matches the description and numbers the result from one.

**Inputs:**

```text
find 复习 🚀
```

**Expected output:**

```text
Scanner results—matching missions:
    1.[T][ ] 复习 🚀 | notes \ draft
```

## UI-MORE-05: Mark the Unicode task

**Aim:** Verify that the task number from list can mark the new third task.

**Inputs:**

```text
mark 3
```

**Expected output:**

```text
Touchdown! Mission complete:
    [T][X] 复习 🚀 | notes \ draft
```

## UI-MORE-06: Mark an already completed task

**Aim:** Verify that repeating mark keeps the task completed without adding or changing other tasks.

**Inputs:**

```text
mark 3
```

**Expected output:**

```text
Touchdown! Mission complete:
    [T][X] 复习 🚀 | notes \ draft
```

**Expected data file after command:**

```text
T | 0 | read book
E | 0 | project meeting | 2019-12-03 | 2019-12-04
T | 1 | 复习 🚀 \| notes \\ draft
```

## UI-MORE-07: Delete the Unicode task

**Aim:** Verify that deletion preserves the two earlier tasks and their order.

**Inputs:**

```text
delete 3
```

**Expected output:**

```text
Mission scrubbed from the flight plan:
    [T][X] 复习 🚀 | notes \ draft
Flight plan now holds 2 missions.
```

**Expected data file after command:**

```text
T | 0 | read book
E | 0 | project meeting | 2019-12-03 | 2019-12-04
```

## UI-28: Exit the program

**Aim:** Verify that the program exits with its farewell message.

**Inputs:**

```text
bye
```

**Expected output:**

```text
    Mission control signing off. Clear skies, explorer!
```
