# UI Test Plan

This file is the source of truth for command-driven UI regression tests.
Update it whenever the console UI changes.

Run console regression tests with:

```bash
./gradlew test --tests friday.ConsoleUiRegressionTest
```

Select Java 25.0.3.fx-zulu first using `sdk use java 25.0.3.fx-zulu`. The JUnit regression test
reads every case in this file, starts Friday in its own temporary directory, and compares its output
exactly. It never reads or overwrites real task data. The original cases also test startup without a
data folder.
The runner starts each child JVM with UTF-8 standard output and error to match its UTF-8 pipe reader,
including on Windows. The special-character and contact cases verify Unicode survives output and restart.
The following test-only directives in Inputs are consumed by the JUnit runner, not sent to Friday:

- `@restart` ends the current input stream (EOF) and starts Friday again with the same data folder.
- `@file RECORD` supplies a line of the initial save file before startup.
- `@directory` creates a directory instead of the save file, to test a read failure.
- `@block-save` blocks the destination with a nonempty directory after startup, to test a write failure.
- `@contact-file RECORD` supplies an initial contact record before startup.
- `@contact-directory` creates a directory instead of the contact file.
- `@block-contact-save` blocks the contact destination after startup to test a write failure.

The JUnit runner launches `friday.Friday`; it does not depend on a default-package entry point.
JUnit tests mirror their production packages under Gradle's standard `src/test/java` root.

The runner does not supply expected output; all console expectations remain in this plan.

Parser, task-list, storage, and date/time JUnit checks run with the same JDK from the project root:

```bash
./gradlew test
```

JUnit results appear under `build/reports/tests/test/`; storage tests use JUnit `@TempDir`.
The UI runner uses JUnit's platform-specific temporary directory and the selected Java runtime.
On Windows, use `gradlew.bat` for Gradle.

## Gradle build and launch verification (A-Gradle)

With Java **25.0.3.fx-zulu** selected, run `./gradlew --version` and `./gradlew clean build`.
Expect Gradle 9.6.1, the selected Java 25.0.3 JVM, and `BUILD SUCCESSFUL`.
After A-JUnit, `test` must discover and run the JUnit suite (or report `UP-TO-DATE` when unchanged).
Use `./gradlew test --rerun-tasks` to force a fresh run, and keep running the separate UI regressions above.

`./gradlew --quiet --console=plain run` launches the JavaFX chat window. Enter `hello`, then `bye`
in that window for a manual GUI smoke check. Exact console comparisons use the `Program command`
above, which launches `friday.Friday`. The shared response API is also covered by `FridayTest`;
console regression results do not claim native GUI interaction coverage.

## D-Contacts acceptance verification

The contact cases below cover lifecycle/restarts, literal search, invalid commands, escaped text,
and independent storage recovery. [The acceptance report](d-contacts-verification.md) maps the
agreed specification to JUnit and console evidence. The test runner's fixture directives are
test-only; they are not supported chatbot commands.

## Test Case: Add and list todos
- Aim: Verify that `todo` adds ToDo tasks and that `list` shows the ToDo prefix.
- Inputs:
```text
todo borrow book
todo buy bread
list
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Task logged. Here's the next objective:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
Task logged. Here's the next objective:
  [T][ ] buy bread
Now you have 2 tasks in the list.
____________________________________________________________
Your task briefing:
Use the number shown here with mark/unmark.
1.[T][ ] borrow book
2.[T][ ] buy bread
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Invalid todo format
- Aim: Verify that `todo` shows an error message when the description is missing.
- Inputs:
```text
todo
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
The description of a todo cannot be empty.
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Unknown command
- Aim: Verify that unrecognized input produces a clear error message.
- Inputs:
```text
blah
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Command not recognized. Type help for available commands.
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Delete a task
- Aim: Verify that `delete` removes the selected task and shifts the remaining tasks up.
- Inputs:
```text
todo borrow book
deadline return book /by 2019-12-01
event project meeting /from 2019-12-02 14:00 /to 2019-12-02 16:00
delete 3
list
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Task logged. Here's the next objective:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
Task logged. Here's the next objective:
  [D][ ] return book (by: Dec 01 2019)
Now you have 2 tasks in the list.
____________________________________________________________
Task logged. Here's the next objective:
  [E][ ] project meeting (from: Dec 02 2019, 14:00 to: Dec 02 2019, 16:00)
Now you have 3 tasks in the list.
____________________________________________________________
Task removed from your agenda:
  [E][ ] project meeting (from: Dec 02 2019, 14:00 to: Dec 02 2019, 16:00)
Now you have 2 tasks in the list.
____________________________________________________________
Your task briefing:
Use the number shown here with mark/unmark.
1.[T][ ] borrow book
2.[D][ ] return book (by: Dec 01 2019)
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Add and list deadlines
- Aim: Verify that `deadline` adds Deadline tasks and shows the by-date.
- Inputs:
```text
deadline return book /by 2019-12-01
list
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Task logged. Here's the next objective:
  [D][ ] return book (by: Dec 01 2019)
Now you have 1 task in the list.
____________________________________________________________
Your task briefing:
Use the number shown here with mark/unmark.
1.[D][ ] return book (by: Dec 01 2019)
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Invalid deadline format
- Aim: Verify that `deadline` shows an error message when `/by` is missing.
- Inputs:
```text
deadline return book
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Invalid deadline format. Use: deadline DESCRIPTION /by DEADLINE
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Add and list events
- Aim: Verify that `event` adds Event tasks and shows the from/to times.
- Inputs:
```text
event project meeting /from 2019-12-02 14:00 /to 2019-12-02 16:00
list
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Task logged. Here's the next objective:
  [E][ ] project meeting (from: Dec 02 2019, 14:00 to: Dec 02 2019, 16:00)
Now you have 1 task in the list.
____________________________________________________________
Your task briefing:
Use the number shown here with mark/unmark.
1.[E][ ] project meeting (from: Dec 02 2019, 14:00 to: Dec 02 2019, 16:00)
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Invalid event format
- Aim: Verify that `event` shows an error message when `/from` or `/to` is missing.
- Inputs:
```text
event project meeting /from 2019-12-02 14:00
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Invalid event format. Use: event DESCRIPTION /from START /to END
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Mark and unmark a todo
- Aim: Verify that `mark` and `unmark` update the done status of a ToDo.
- Inputs:
```text
todo borrow book
mark 1
unmark 1
list
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Task logged. Here's the next objective:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
Objective complete. Nicely handled:
  [T][X] borrow book
____________________________________________________________
Task reopened. Back on the radar:
  [T][ ] borrow book
____________________________________________________________
Your task briefing:
Use the number shown here with mark/unmark.
1.[T][ ] borrow book
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Invalid mark format
- Aim: Verify that `mark` shows an error message when no task number is provided.
- Inputs:
```text
mark
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Invalid mark format. Use: mark TASK_NUMBER
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Invalid mark number
- Aim: Verify that `mark` shows an error message when the task number is out of range.
- Inputs:
```text
todo borrow book
mark 2
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Task logged. Here's the next objective:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
The task number is invalid.
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Invalid delete format
- Aim: Verify that `delete` shows an error message when no task number is provided.
- Inputs:
```text
delete
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Invalid delete format. Use: delete TASK_NUMBER
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Unmarking an already unmarked task
- Aim: Verify that `unmark` reports when the task is already not done.
- Inputs:
```text
todo borrow book
unmark 1
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Task logged. Here's the next objective:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
This task is already not marked as done.
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Invalid unmark format
- Aim: Verify that `unmark` shows an error message when no task number is provided.
- Inputs:
```text
unmark
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Invalid unmark format. Use: unmark TASK_NUMBER
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Invalid unmark number
- Aim: Verify that `unmark` shows an error message when the task number is out of range.
- Inputs:
```text
todo borrow book
unmark 2
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Task logged. Here's the next objective:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
The task number is invalid.
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Invalid delete number
- Aim: Verify that `delete` shows an error message when the task number is out of range.
- Inputs:
```text
todo borrow book
delete 2
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Task logged. Here's the next objective:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
The task number is invalid.
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Help command
- Aim: Verify that `help` lists every supported command and the accepted date formats.
- Inputs:
```text
help
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Command briefing:
Tasks:
  todo DESCRIPTION
  deadline DESCRIPTION /by DEADLINE
  event DESCRIPTION /from START /to END
  list
  find KEYWORD
  on yyyy-MM-dd
  mark TASK_NUMBER
  unmark TASK_NUMBER
  delete TASK_NUMBER
Dates: yyyy-MM-dd, yyyy-MM-dd HH:mm, or d/M/yyyy HHmm
Contacts:
  contact add NAME /phone PHONE [/email EMAIL]
  contact add NAME /email EMAIL
  contact list
  contact find KEYWORD
  contact delete NUMBER
Session: hello | thanks | help | bye
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Autosave all changes across restarts
- Aim: Verify addition, mark, unmark, deletion, and deletion of the last task persist across restarts without bye.
- Inputs:
```text
todo read book
deadline return book /by 2019-12-01
event meeting /from 2019-12-02 14:00 /to 2019-12-02 16:00
mark 2
@restart
list
unmark 2
delete 1
@restart
list
delete 2
delete 1
@restart
list
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Task logged. Here's the next objective:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
Task logged. Here's the next objective:
  [D][ ] return book (by: Dec 01 2019)
Now you have 2 tasks in the list.
____________________________________________________________
Task logged. Here's the next objective:
  [E][ ] meeting (from: Dec 02 2019, 14:00 to: Dec 02 2019, 16:00)
Now you have 3 tasks in the list.
____________________________________________________________
Objective complete. Nicely handled:
  [D][X] return book (by: Dec 01 2019)
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Your task briefing:
Use the number shown here with mark/unmark.
1.[T][ ] read book
2.[D][X] return book (by: Dec 01 2019)
3.[E][ ] meeting (from: Dec 02 2019, 14:00 to: Dec 02 2019, 16:00)
____________________________________________________________
Task reopened. Back on the radar:
  [D][ ] return book (by: Dec 01 2019)
____________________________________________________________
Task removed from your agenda:
  [T][ ] read book
Now you have 2 tasks in the list.
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Your task briefing:
Use the number shown here with mark/unmark.
1.[D][ ] return book (by: Dec 01 2019)
2.[E][ ] meeting (from: Dec 02 2019, 14:00 to: Dec 02 2019, 16:00)
____________________________________________________________
Task removed from your agenda:
  [E][ ] meeting (from: Dec 02 2019, 14:00 to: Dec 02 2019, 16:00)
Now you have 1 task in the list.
____________________________________________________________
Task removed from your agenda:
  [D][ ] return book (by: Dec 01 2019)
Now you have 0 tasks in the list.
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Your task briefing:
Use the number shown here with mark/unmark.
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Load an existing save file
- Aim: Verify that all three task types and their completed statuses load from disk in order.
- Inputs:
```text
@file T|1|read book
@file D|1|return book|2019-12-01T00:00
@file E|1|meeting|2019-12-02T14:00|2019-12-02T16:00
list
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Your task briefing:
Use the number shown here with mark/unmark.
1.[T][X] read book
2.[D][X] return book (by: Dec 01 2019)
3.[E][X] meeting (from: Dec 02 2019, 14:00 to: Dec 02 2019, 16:00)
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Preserve special characters
- Aim: Verify Unicode, pipes, and backslashes survive a save and restart unchanged.
- Inputs:
```text
todo café | C:\notes
@restart
list
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Task logged. Here's the next objective:
  [T][ ] café | C:\notes
Now you have 1 task in the list.
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Your task briefing:
Use the number shown here with mark/unmark.
1.[T][ ] café | C:\notes
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Protect a corrupted save file
- Aim: Verify malformed data produces a warning, no partial list is loaded, and session edits do not overwrite the original file.
- Inputs:
```text
@file T|0|valid first record
@file broken record
list
todo session only
@restart
list
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
Warning: I couldn't load data/friday.txt. Check the file and restart; saving is disabled to protect existing data.
____________________________________________________________
____________________________________________________________
Your task briefing:
Use the number shown here with mark/unmark.
____________________________________________________________
Task logged. Here's the next objective:
  [T][ ] session only
Now you have 1 task in the list.
Warning: This change is only in memory; saving is disabled until you fix the file and restart.
____________________________________________________________
FRIDAY online. Let's keep your day under control.
Warning: I couldn't load data/friday.txt. Check the file and restart; saving is disabled to protect existing data.
____________________________________________________________
____________________________________________________________
Your task briefing:
Use the number shown here with mark/unmark.
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Unreadable save path
- Aim: Verify a directory at the save path produces a load warning without crashing.
- Inputs:
```text
@directory
list
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
Warning: I couldn't load data/friday.txt. Check the file and restart; saving is disabled to protect existing data.
____________________________________________________________
____________________________________________________________
Your task briefing:
Use the number shown here with mark/unmark.
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Save failure keeps the chatbot usable
- Aim: Verify a failed save warns that changes are only in memory and the session list still works.
- Inputs:
```text
@block-save
todo session only
list
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Task logged. Here's the next objective:
  [T][ ] session only
Now you have 1 task in the list.
Warning: I couldn't save data/friday.txt. Your changes are only in memory; check the folder and file permissions.
____________________________________________________________
Your task briefing:
Use the number shown here with mark/unmark.
1.[T][ ] session only
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Parse timed deadlines and preserve times across restarts
- Aim: Verify day-first HHmm and ISO HH:mm inputs produce readable dates and retain actual times after loading.
- Inputs:
```text
deadline return book /by 2/12/2019 1800
deadline submit report /by 2019-12-02 09:30
@restart
list
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Task logged. Here's the next objective:
  [D][ ] return book (by: Dec 02 2019, 18:00)
Now you have 1 task in the list.
____________________________________________________________
Task logged. Here's the next objective:
  [D][ ] submit report (by: Dec 02 2019, 09:30)
Now you have 2 tasks in the list.
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Your task briefing:
Use the number shown here with mark/unmark.
1.[D][ ] return book (by: Dec 02 2019, 18:00)
2.[D][ ] submit report (by: Dec 02 2019, 09:30)
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Reject impossible deadline dates and times
- Aim: Verify invalid calendar dates, times, and vague text are rejected without adding tasks.
- Inputs:
```text
deadline task /by 2019-02-29
deadline task /by 2019-02-29 18:00
deadline task /by 31/4/2019 1800
deadline task /by 2019-12-02 24:00
deadline task /by Sunday
list
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Invalid date/time. Use yyyy-MM-dd, yyyy-MM-dd HH:mm, or d/M/yyyy HHmm (e.g., 2/12/2019 1800).
____________________________________________________________
Invalid date/time. Use yyyy-MM-dd, yyyy-MM-dd HH:mm, or d/M/yyyy HHmm (e.g., 2/12/2019 1800).
____________________________________________________________
Invalid date/time. Use yyyy-MM-dd, yyyy-MM-dd HH:mm, or d/M/yyyy HHmm (e.g., 2/12/2019 1800).
____________________________________________________________
Invalid date/time. Use yyyy-MM-dd, yyyy-MM-dd HH:mm, or d/M/yyyy HHmm (e.g., 2/12/2019 1800).
____________________________________________________________
Invalid date/time. Use yyyy-MM-dd, yyyy-MM-dd HH:mm, or d/M/yyyy HHmm (e.g., 2/12/2019 1800).
____________________________________________________________
Your task briefing:
Use the number shown here with mark/unmark.
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Reject invalid event endpoints and backwards intervals
- Aim: Verify both endpoints are validated and an event cannot end before it starts.
- Inputs:
```text
event meeting /from bad /to 2019-12-02 16:00
event meeting /from 2019-12-02 14:00 /to 2019-12-02 18:60
event meeting /from 2019-12-02 16:00 /to 2019-12-02 14:00
list
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Invalid date/time. Use yyyy-MM-dd, yyyy-MM-dd HH:mm, or d/M/yyyy HHmm (e.g., 2/12/2019 1800).
____________________________________________________________
Invalid date/time. Use yyyy-MM-dd, yyyy-MM-dd HH:mm, or d/M/yyyy HHmm (e.g., 2/12/2019 1800).
____________________________________________________________
An event cannot end before it starts.
____________________________________________________________
Your task briefing:
Use the number shown here with mark/unmark.
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Accept leap days and equal event endpoints
- Aim: Verify real leap days, date-only events, and equal event endpoints survive saving and loading.
- Inputs:
```text
deadline leap day /by 29/2/2024 1800
event conference /from 2024-02-29 /to 2024-03-01
event reminder /from 29/2/2024 1800 /to 2024-02-29 18:00
@restart
list
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Task logged. Here's the next objective:
  [D][ ] leap day (by: Feb 29 2024, 18:00)
Now you have 1 task in the list.
____________________________________________________________
Task logged. Here's the next objective:
  [E][ ] conference (from: Feb 29 2024 to: Mar 01 2024)
Now you have 2 tasks in the list.
____________________________________________________________
Task logged. Here's the next objective:
  [E][ ] reminder (from: Feb 29 2024, 18:00 to: Feb 29 2024, 18:00)
Now you have 3 tasks in the list.
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Your task briefing:
Use the number shown here with mark/unmark.
1.[D][ ] leap day (by: Feb 29 2024, 18:00)
2.[E][ ] conference (from: Feb 29 2024 to: Mar 01 2024)
3.[E][ ] reminder (from: Feb 29 2024, 18:00 to: Feb 29 2024, 18:00)
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Protect older saves with ambiguous dates
- Aim: Verify a Level 7 date such as Sunday is not guessed or overwritten and the chatbot remains usable.
- Inputs:
```text
@file T|0|keep this task
@file D|0|return book|Sunday
list
todo session only
@restart
list
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
Warning: I couldn't load data/friday.txt. Check the file and restart; saving is disabled to protect existing data.
____________________________________________________________
____________________________________________________________
Your task briefing:
Use the number shown here with mark/unmark.
____________________________________________________________
Task logged. Here's the next objective:
  [T][ ] session only
Now you have 1 task in the list.
Warning: This change is only in memory; saving is disabled until you fix the file and restart.
____________________________________________________________
FRIDAY online. Let's keep your day under control.
Warning: I couldn't load data/friday.txt. Check the file and restart; saving is disabled to protect existing data.
____________________________________________________________
____________________________________________________________
Your task briefing:
Use the number shown here with mark/unmark.
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Find dated tasks with original list numbers
- Aim: Verify date queries include completed events, inclusive event boundary dates, and exact deadline dates while excluding todos and preserving task numbers after a restart.
- Inputs:
```text
@file T|0|buy bread
@file D|0|return book|2019-12-02T18:00
@file E|1|conference|2019-12-01T10:00|2019-12-03T00:00
@file D|0|later task|2019-12-04T00:00
on 2019-12-02
mark 2
@restart
on 2019-12-02
on 2019-12-01
on 2019-12-03
on 2019-12-04
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Scheduled deadlines and events on Dec 02 2019:
Use the number shown here with mark/unmark.
2.[D][ ] return book (by: Dec 02 2019, 18:00)
3.[E][X] conference (from: Dec 01 2019, 10:00 to: Dec 03 2019)
____________________________________________________________
Objective complete. Nicely handled:
  [D][X] return book (by: Dec 02 2019, 18:00)
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Scheduled deadlines and events on Dec 02 2019:
Use the number shown here with mark/unmark.
2.[D][X] return book (by: Dec 02 2019, 18:00)
3.[E][X] conference (from: Dec 01 2019, 10:00 to: Dec 03 2019)
____________________________________________________________
Scheduled deadlines and events on Dec 01 2019:
Use the number shown here with mark/unmark.
3.[E][X] conference (from: Dec 01 2019, 10:00 to: Dec 03 2019)
____________________________________________________________
Scheduled deadlines and events on Dec 03 2019:
Use the number shown here with mark/unmark.
3.[E][X] conference (from: Dec 01 2019, 10:00 to: Dec 03 2019)
____________________________________________________________
Scheduled deadlines and events on Dec 04 2019:
Use the number shown here with mark/unmark.
4.[D][ ] later task (by: Dec 04 2019)
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Date queries with no matching tasks
- Aim: Verify empty lists and lists containing only todos report no matching deadlines or events.
- Inputs:
```text
on 2019-12-02
todo buy bread
on 2019-12-02
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Scheduled deadlines and events on Dec 02 2019:
Use the number shown here with mark/unmark.
Schedule clear. No deadlines or events on this date.
____________________________________________________________
Task logged. Here's the next objective:
  [T][ ] buy bread
Now you have 1 task in the list.
____________________________________________________________
Scheduled deadlines and events on Dec 02 2019:
Use the number shown here with mark/unmark.
Schedule clear. No deadlines or events on this date.
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Invalid date query formats
- Aim: Verify on requires a valid ISO calendar date without a time, and errors do not terminate the chatbot.
- Inputs:
```text
on
on Sunday
on 2019-02-29
on 2019-12-02 18:00
on 2/12/2019
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Invalid date. Use: on yyyy-MM-dd (e.g., on 2019-12-02).
____________________________________________________________
Invalid date. Use: on yyyy-MM-dd (e.g., on 2019-12-02).
____________________________________________________________
Invalid date. Use: on yyyy-MM-dd (e.g., on 2019-12-02).
____________________________________________________________
Invalid date. Use: on yyyy-MM-dd (e.g., on 2019-12-02).
____________________________________________________________
Invalid date. Use: on yyyy-MM-dd (e.g., on 2019-12-02).
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Save only when completion changes
- Aim: Verify repeated mark/unmark commands do not attempt a save, while actual status changes still try to save and retain their in-memory result after a write failure.
- Inputs:
```text
@file T|1|finished task
@file T|0|pending task
@block-save
mark 1
unmark 2
mark 2
unmark 1
list
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Objective complete. Nicely handled:
  [T][X] finished task
____________________________________________________________
This task is already not marked as done.
____________________________________________________________
Objective complete. Nicely handled:
  [T][X] pending task
Warning: I couldn't save data/friday.txt. Your changes are only in memory; check the folder and file permissions.
____________________________________________________________
Task reopened. Back on the radar:
  [T][ ] finished task
Warning: I couldn't save data/friday.txt. Your changes are only in memory; check the folder and file permissions.
____________________________________________________________
Your task briefing:
Use the number shown here with mark/unmark.
1.[T][ ] finished task
2.[T][X] pending task
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Task-number syntax and range errors
- Aim: Verify malformed numbers retain command-specific errors, out-of-range numbers do not change tasks, valid integer spellings still work, and input errors do not end the session.
- Inputs:
```text
todo read book
mark nope
mark -1
unmark 0
delete 2147483648
delete -2
mark +1
unmark 01
list
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Task logged. Here's the next objective:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
Invalid mark format. Use: mark TASK_NUMBER
____________________________________________________________
Invalid mark format. Use: mark TASK_NUMBER
____________________________________________________________
The task number is invalid.
____________________________________________________________
Invalid delete format. Use: delete TASK_NUMBER
____________________________________________________________
The task number is invalid.
____________________________________________________________
Objective complete. Nicely handled:
  [T][X] read book
____________________________________________________________
Task reopened. Back on the radar:
  [T][ ] read book
____________________________________________________________
Your task briefing:
Use the number shown here with mark/unmark.
1.[T][ ] read book
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Greeting and thanks output
- Aim: Verify the extracted UI preserves both ASCII banners, blank lines, and separators exactly as before the refactor.
- Inputs:
```text
hello
thanks
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
At your service. What's the next task?
____________________________________________________________
Anytime. Organization is rather my specialty.
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Find descriptions and retain task numbers
- Aim: Include all task types and completed tasks, preserve original numbers, and allow marking a result.
- Inputs:
```text
@file T|0|buy bread
@file T|1|read book
@file D|1|return book|2019-12-02T00:00
@file E|0|book club|2019-12-02T14:00|2019-12-02T16:00
find book
mark 4
@restart
find book
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Task scan complete. Here's what matches:
Use the number shown here with mark/unmark.
2.[T][X] read book
3.[D][X] return book (by: Dec 02 2019)
4.[E][ ] book club (from: Dec 02 2019, 14:00 to: Dec 02 2019, 16:00)
____________________________________________________________
Objective complete. Nicely handled:
  [E][X] book club (from: Dec 02 2019, 14:00 to: Dec 02 2019, 16:00)
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Task scan complete. Here's what matches:
Use the number shown here with mark/unmark.
2.[T][X] read book
3.[D][X] return book (by: Dec 02 2019)
4.[E][X] book club (from: Dec 02 2019, 14:00 to: Dec 02 2019, 16:00)
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Find literal phrases and case-sensitive substrings
- Aim: Trim surrounding whitespace, preserve case and internal spaces, and ignore display-only date fields.
- Inputs:
```text
@file T|0|read book
@file T|0|bookshelf
@file T|0|Read Book
@file T|0|literal .* 读书
@file D|0|return notes|2019-12-02T00:00
find   read book
find Book
find book
find .*
find 读书
find Dec
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Task scan complete. Here's what matches:
Use the number shown here with mark/unmark.
1.[T][ ] read book
____________________________________________________________
Task scan complete. Here's what matches:
Use the number shown here with mark/unmark.
3.[T][ ] Read Book
____________________________________________________________
Task scan complete. Here's what matches:
Use the number shown here with mark/unmark.
1.[T][ ] read book
2.[T][ ] bookshelf
____________________________________________________________
Task scan complete. Here's what matches:
Use the number shown here with mark/unmark.
4.[T][ ] literal .* 读书
____________________________________________________________
Task scan complete. Here's what matches:
Use the number shown here with mark/unmark.
4.[T][ ] literal .* 读书
____________________________________________________________
Task scan complete. Here's what matches:
Use the number shown here with mark/unmark.
Task scan complete. No matches found.
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Find empty list and invalid keywords
- Aim: Report empty results and malformed find commands without terminating the command loop.
- Inputs:
```text
find book
find
find
findbook
FIND book
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Task scan complete. Here's what matches:
Use the number shown here with mark/unmark.
Task scan complete. No matches found.
____________________________________________________________
Invalid find format. Use: find KEYWORD
____________________________________________________________
Invalid find format. Use: find KEYWORD
____________________________________________________________
Command not recognized. Type help for available commands.
____________________________________________________________
Command not recognized. Type help for available commands.
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Find does not attempt to save
- Aim: Search remains usable with a blocked save destination and produces no save warning.
- Inputs:
```text
@file T|0|read book
@block-save
find book
find missing
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Task scan complete. Here's what matches:
Use the number shown here with mark/unmark.
1.[T][ ] read book
____________________________________________________________
Task scan complete. Here's what matches:
Use the number shown here with mark/unmark.
Task scan complete. No matches found.
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Contact lifecycle and restart
- Aim: Verify all contact detail combinations, numbering, search, deletion, and restart persistence.
- Inputs:
```text
contact list
contact add Alice Tan /phone 91234567 /email Alice@example.com
contact add Bob /email bob@example.com
contact add Carol /phone 81234567
contact find EXAMPLE
contact delete 2
@restart
contact list
contact find 8123
contact delete 1
contact delete 1
contact list
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Your contact directory is empty.
____________________________________________________________
Contact logged. Here's the entry:
  Alice Tan (phone: 91234567, email: Alice@example.com)
Now you have 1 contact in the list.
____________________________________________________________
Contact logged. Here's the entry:
  Bob (phone: -, email: bob@example.com)
Now you have 2 contacts in the list.
____________________________________________________________
Contact logged. Here's the entry:
  Carol (phone: 81234567, email: -)
Now you have 3 contacts in the list.
____________________________________________________________
Contact scan complete. Here's what matches:
Use the number shown here with contact delete.
1.Alice Tan (phone: 91234567, email: Alice@example.com)
2.Bob (phone: -, email: bob@example.com)
____________________________________________________________
Contact removed from your directory:
  Bob (phone: -, email: bob@example.com)
Now you have 2 contacts in the list.
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Your contact directory:
Use the number shown here with contact delete.
1.Alice Tan (phone: 91234567, email: Alice@example.com)
2.Carol (phone: 81234567, email: -)
____________________________________________________________
Contact scan complete. Here's what matches:
Use the number shown here with contact delete.
2.Carol (phone: 81234567, email: -)
____________________________________________________________
Contact removed from your directory:
  Alice Tan (phone: 91234567, email: Alice@example.com)
Now you have 1 contact in the list.
____________________________________________________________
Contact removed from your directory:
  Carol (phone: 81234567, email: -)
Now you have 0 contacts in the list.
____________________________________________________________
Your contact directory is empty.
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Contact invalid inputs preserve state
- Aim: Verify exact errors and unchanged contacts after invalid operations.
- Inputs:
```text
contact
contact edit Alice
contact list extra
contact add
contact add Alice /phone
contact add Alice /phone 91234567 /phone 81234567
contact add Alice /address home
contact add Alice /phone 12345678
contact add Alice /email alice@
contact add Alice /phone 91234567
contact add ALICE /email other@example.com
contact find
contact delete 0
contact delete 2
contact delete 2147483648
contact list
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Invalid contact command. Use: contact add, contact list, contact find, or contact delete.
____________________________________________________________
Invalid contact command. Use: contact add, contact list, contact find, or contact delete.
____________________________________________________________
Invalid contact command. Use: contact add, contact list, contact find, or contact delete.
____________________________________________________________
Invalid contact add format. Use: contact add NAME /phone PHONE [/email EMAIL] or contact add NAME /email EMAIL.
____________________________________________________________
Invalid contact add format. Use: contact add NAME /phone PHONE [/email EMAIL] or contact add NAME /email EMAIL.
____________________________________________________________
Invalid contact add format. Use: contact add NAME /phone PHONE [/email EMAIL] or contact add NAME /email EMAIL.
____________________________________________________________
Invalid contact add format. Use: contact add NAME /phone PHONE [/email EMAIL] or contact add NAME /email EMAIL.
____________________________________________________________
Invalid phone number. Use 8 digits starting with 6, 8, or 9.
____________________________________________________________
Invalid email address. Use a format like alice@example.com.
____________________________________________________________
Contact logged. Here's the entry:
  Alice (phone: 91234567, email: -)
Now you have 1 contact in the list.
____________________________________________________________
A contact with this name already exists.
____________________________________________________________
Invalid contact find format. Use: contact find KEYWORD
____________________________________________________________
The contact number is invalid.
____________________________________________________________
The contact number is invalid.
____________________________________________________________
The contact number is invalid.
____________________________________________________________
Your contact directory:
Use the number shown here with contact delete.
1.Alice (phone: 91234567, email: -)
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Contact searches are literal and read only
- Aim: Retain original numbers and avoid saving for searches, listing, or duplicate rejection.
- Inputs:
```text
@contact-file C|Alice  Tan|91234567|
@contact-file C|Bob||bob@example.com
@block-contact-save
contact find alice
contact find Alice Tan
contact find .*
contact find BOB@
contact list
contact add ALICE  TAN /phone 81234567
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Contact scan complete. Here's what matches:
Use the number shown here with contact delete.
1.Alice  Tan (phone: 91234567, email: -)
____________________________________________________________
Contact scan complete. No matches found.
____________________________________________________________
Contact scan complete. No matches found.
____________________________________________________________
Contact scan complete. Here's what matches:
Use the number shown here with contact delete.
2.Bob (phone: -, email: bob@example.com)
____________________________________________________________
Your contact directory:
Use the number shown here with contact delete.
1.Alice  Tan (phone: 91234567, email: -)
2.Bob (phone: -, email: bob@example.com)
____________________________________________________________
A contact with this name already exists.
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Corrupt contacts do not block task saving
- Aim: Reject a partial contact file, keep contact mutations in memory, and save tasks independently.
- Inputs:
```text
@contact-file C|Alice|91234567|
@contact-file broken
contact list
contact add Carol /phone 81234567
todo still saved
@restart
contact list
list
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
Warning: I couldn't load data/contacts.txt. Check the file and restart; contact saving is disabled to protect existing data.
____________________________________________________________
____________________________________________________________
Your contact directory is empty.
____________________________________________________________
Contact logged. Here's the entry:
  Carol (phone: 81234567, email: -)
Now you have 1 contact in the list.
Warning: This contact change is only in memory; contact saving is disabled until you fix the file and restart.
____________________________________________________________
Task logged. Here's the next objective:
  [T][ ] still saved
Now you have 1 task in the list.
____________________________________________________________
FRIDAY online. Let's keep your day under control.
Warning: I couldn't load data/contacts.txt. Check the file and restart; contact saving is disabled to protect existing data.
____________________________________________________________
____________________________________________________________
Your contact directory is empty.
____________________________________________________________
Your task briefing:
Use the number shown here with mark/unmark.
1.[T][ ] still saved
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Contact read failure
- Aim: Handle a directory at the contact save path while retaining a usable in-memory contact session.
- Inputs:
```text
@contact-directory
contact add Carol /phone 81234567
contact delete 1
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
Warning: I couldn't load data/contacts.txt. Check the file and restart; contact saving is disabled to protect existing data.
____________________________________________________________
____________________________________________________________
Contact logged. Here's the entry:
  Carol (phone: 81234567, email: -)
Now you have 1 contact in the list.
Warning: This contact change is only in memory; contact saving is disabled until you fix the file and restart.
____________________________________________________________
Contact removed from your directory:
  Carol (phone: 81234567, email: -)
Now you have 0 contacts in the list.
Warning: This contact change is only in memory; contact saving is disabled until you fix the file and restart.
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Contact write failure keeps session usable
- Aim: Warn on failed contact writes and preserve in-memory changes for listing and deletion.
- Inputs:
```text
@block-contact-save
contact add Carol /phone 81234567
contact list
contact delete 1
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Contact logged. Here's the entry:
  Carol (phone: 81234567, email: -)
Now you have 1 contact in the list.
Warning: I couldn't save data/contacts.txt. Your contact changes are only in memory; check the folder and file permissions.
____________________________________________________________
Your contact directory:
Use the number shown here with contact delete.
1.Carol (phone: 81234567, email: -)
____________________________________________________________
Contact removed from your directory:
  Carol (phone: 81234567, email: -)
Now you have 0 contacts in the list.
Warning: I couldn't save data/contacts.txt. Your contact changes are only in memory; check the folder and file permissions.
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Corrupt tasks do not block contact saving
- Aim: Persist contacts despite a damaged task file and verify them after restart.
- Inputs:
```text
@file broken
contact add Carol /phone 81234567
@restart
contact list
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
Warning: I couldn't load data/friday.txt. Check the file and restart; saving is disabled to protect existing data.
____________________________________________________________
____________________________________________________________
Contact logged. Here's the entry:
  Carol (phone: 81234567, email: -)
Now you have 1 contact in the list.
____________________________________________________________
FRIDAY online. Let's keep your day under control.
Warning: I couldn't load data/friday.txt. Check the file and restart; saving is disabled to protect existing data.
____________________________________________________________
____________________________________________________________
Your contact directory:
Use the number shown here with contact delete.
1.Carol (phone: 81234567, email: -)
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```

## Test Case: Contact escaped text and field order
- Aim: Preserve pipes, backslashes, Unicode, and reverse-order fields across restarts.
- Inputs:
```text
contact add Alice | \ 陈 /phone 91234567
contact add Bob /email bob@example.com /phone 81234567
@restart
contact list
bye
```
- Expected output:
```text
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Contact logged. Here's the entry:
  Alice | \ 陈 (phone: 91234567, email: -)
Now you have 1 contact in the list.
____________________________________________________________
Contact logged. Here's the entry:
  Bob (phone: 81234567, email: bob@example.com)
Now you have 2 contacts in the list.
____________________________________________________________
FRIDAY online. Let's keep your day under control.
____________________________________________________________
____________________________________________________________
Your contact directory:
Use the number shown here with contact delete.
1.Alice | \ 陈 (phone: 91234567, email: -)
2.Bob (phone: 81234567, email: bob@example.com)
____________________________________________________________
FRIDAY signing off. I'll be here when you return.
____________________________________________________________
```
