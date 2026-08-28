# UI Test Plan

This file is the source of truth for command-driven UI regression tests.
Update it whenever the console UI changes.

Program command: `python3 test/run-ui-session.py`

Run the test-ui skill with Java 25.0.3.fx-zulu selected using `sdk use java 25.0.3.fx-zulu`.
The session helper compiles and runs each case in its own temporary directory, so tests never
read or overwrite real task data. The original cases also test startup without a data folder.
The following test-only directives in Inputs are consumed by the helper, not sent to Friday:

- `@restart` ends the current input stream (EOF) and starts Friday again with the same data folder.
- `@file RECORD` supplies a line of the initial save file before startup.
- `@directory` creates a directory instead of the save file, to test a read failure.
- `@block-save` blocks the destination with a nonempty directory after startup, to test a write failure.

The helper does not supply expected output; all console expectations remain in this plan.

Parser, task-list, storage, and date/time checks (including command syntax, task numbering, invalid records, and failed writes)
can also be run with the same JDK from the project root:

```bash
python3 test/run-unit-tests.py
```

Both test runners use Python's platform-specific temporary directory and require `java` and `javac` on `PATH`.
On Windows, use `python` or `py -3` if your Python installation does not provide `python3`.

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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
Got it. I've added this task:
  [T][ ] buy bread
Now you have 2 tasks in the list.
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[T][ ] borrow book
2.[T][ ] buy bread
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Sir, description of a todo cannot be empty.
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Sir, I don't know what you are saying :-(
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Dec 01 2019)
Now you have 2 tasks in the list.
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Dec 02 2019, 14:00 to: Dec 02 2019, 16:00)
Now you have 3 tasks in the list.
____________________________________________________________
Noted. I've removed this task:
  [E][ ] project meeting (from: Dec 02 2019, 14:00 to: Dec 02 2019, 16:00)
Now you have 2 tasks in the list.
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[T][ ] borrow book
2.[D][ ] return book (by: Dec 01 2019)
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Dec 01 2019)
Now you have 1 task in the list.
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[D][ ] return book (by: Dec 01 2019)
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Invalid deadline format. Use: deadline DESCRIPTION /by DEADLINE
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Dec 02 2019, 14:00 to: Dec 02 2019, 16:00)
Now you have 1 task in the list.
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[E][ ] project meeting (from: Dec 02 2019, 14:00 to: Dec 02 2019, 16:00)
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Invalid event format. Use: event DESCRIPTION /from START /to END
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] borrow book
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] borrow book
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[T][ ] borrow book
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Sir, Invalid mark format. Use: mark TASK_NUMBER
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
Sir, The task number is invalid.
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Sir, Invalid delete format. Use: delete TASK_NUMBER
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
This task is already not marked as done.
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Sir, Invalid unmark format. Use: unmark TASK_NUMBER
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
Sir, The task number is invalid.
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
Sir, The task number is invalid.
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Help command
- Aim: Verify that `help` prints the project link.
- Inputs:
```text
help
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Sure. Here you go:
https://nus-cs2103-ay2627-s1.github.io/website/schedule/week2/project.html
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Dec 01 2019)
Now you have 2 tasks in the list.
____________________________________________________________
Got it. I've added this task:
  [E][ ] meeting (from: Dec 02 2019, 14:00 to: Dec 02 2019, 16:00)
Now you have 3 tasks in the list.
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: Dec 01 2019)
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[T][ ] read book
2.[D][X] return book (by: Dec 01 2019)
3.[E][ ] meeting (from: Dec 02 2019, 14:00 to: Dec 02 2019, 16:00)
____________________________________________________________
OK, I've marked this task as not done yet:
  [D][ ] return book (by: Dec 01 2019)
____________________________________________________________
Noted. I've removed this task:
  [T][ ] read book
Now you have 2 tasks in the list.
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[D][ ] return book (by: Dec 01 2019)
2.[E][ ] meeting (from: Dec 02 2019, 14:00 to: Dec 02 2019, 16:00)
____________________________________________________________
Noted. I've removed this task:
  [E][ ] meeting (from: Dec 02 2019, 14:00 to: Dec 02 2019, 16:00)
Now you have 1 task in the list.
____________________________________________________________
Noted. I've removed this task:
  [D][ ] return book (by: Dec 01 2019)
Now you have 0 tasks in the list.
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[T][X] read book
2.[D][X] return book (by: Dec 01 2019)
3.[E][X] meeting (from: Dec 02 2019, 14:00 to: Dec 02 2019, 16:00)
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] café | C:\notes
Now you have 1 task in the list.
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[T][ ] café | C:\notes
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
Warning: I couldn't load data/friday.txt. Check the file and restart; saving is disabled to protect existing data.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
____________________________________________________________
Got it. I've added this task:
  [T][ ] session only
Now you have 1 task in the list.
Warning: This change is only in memory; saving is disabled until you fix the file and restart.
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
Warning: I couldn't load data/friday.txt. Check the file and restart; saving is disabled to protect existing data.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
Warning: I couldn't load data/friday.txt. Check the file and restart; saving is disabled to protect existing data.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] session only
Now you have 1 task in the list.
Warning: I couldn't save data/friday.txt. Your changes are only in memory; check the folder and file permissions.
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[T][ ] session only
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Dec 02 2019, 18:00)
Now you have 1 task in the list.
____________________________________________________________
Got it. I've added this task:
  [D][ ] submit report (by: Dec 02 2019, 09:30)
Now you have 2 tasks in the list.
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[D][ ] return book (by: Dec 02 2019, 18:00)
2.[D][ ] submit report (by: Dec 02 2019, 09:30)
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
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
Here are the tasks in your list:
Use the number shown here with mark/unmark.
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Invalid date/time. Use yyyy-MM-dd, yyyy-MM-dd HH:mm, or d/M/yyyy HHmm (e.g., 2/12/2019 1800).
____________________________________________________________
Invalid date/time. Use yyyy-MM-dd, yyyy-MM-dd HH:mm, or d/M/yyyy HHmm (e.g., 2/12/2019 1800).
____________________________________________________________
An event cannot end before it starts.
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] leap day (by: Feb 29 2024, 18:00)
Now you have 1 task in the list.
____________________________________________________________
Got it. I've added this task:
  [E][ ] conference (from: Feb 29 2024 to: Mar 01 2024)
Now you have 2 tasks in the list.
____________________________________________________________
Got it. I've added this task:
  [E][ ] reminder (from: Feb 29 2024, 18:00 to: Feb 29 2024, 18:00)
Now you have 3 tasks in the list.
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[D][ ] leap day (by: Feb 29 2024, 18:00)
2.[E][ ] conference (from: Feb 29 2024 to: Mar 01 2024)
3.[E][ ] reminder (from: Feb 29 2024, 18:00 to: Feb 29 2024, 18:00)
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
Warning: I couldn't load data/friday.txt. Check the file and restart; saving is disabled to protect existing data.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
____________________________________________________________
Got it. I've added this task:
  [T][ ] session only
Now you have 1 task in the list.
Warning: This change is only in memory; saving is disabled until you fix the file and restart.
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
Warning: I couldn't load data/friday.txt. Check the file and restart; saving is disabled to protect existing data.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the deadlines and events on Dec 02 2019:
Use the number shown here with mark/unmark.
2.[D][ ] return book (by: Dec 02 2019, 18:00)
3.[E][X] conference (from: Dec 01 2019, 10:00 to: Dec 03 2019)
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: Dec 02 2019, 18:00)
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the deadlines and events on Dec 02 2019:
Use the number shown here with mark/unmark.
2.[D][X] return book (by: Dec 02 2019, 18:00)
3.[E][X] conference (from: Dec 01 2019, 10:00 to: Dec 03 2019)
____________________________________________________________
Here are the deadlines and events on Dec 01 2019:
Use the number shown here with mark/unmark.
3.[E][X] conference (from: Dec 01 2019, 10:00 to: Dec 03 2019)
____________________________________________________________
Here are the deadlines and events on Dec 03 2019:
Use the number shown here with mark/unmark.
3.[E][X] conference (from: Dec 01 2019, 10:00 to: Dec 03 2019)
____________________________________________________________
Here are the deadlines and events on Dec 04 2019:
Use the number shown here with mark/unmark.
4.[D][ ] later task (by: Dec 04 2019)
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the deadlines and events on Dec 02 2019:
Use the number shown here with mark/unmark.
No deadlines or events on this date.
____________________________________________________________
Got it. I've added this task:
  [T][ ] buy bread
Now you have 1 task in the list.
____________________________________________________________
Here are the deadlines and events on Dec 02 2019:
Use the number shown here with mark/unmark.
No deadlines or events on this date.
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
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
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] finished task
____________________________________________________________
This task is already not marked as done.
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] pending task
Warning: I couldn't save data/friday.txt. Your changes are only in memory; check the folder and file permissions.
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] finished task
Warning: I couldn't save data/friday.txt. Your changes are only in memory; check the folder and file permissions.
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[T][ ] finished task
2.[T][X] pending task
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
Sir, Invalid mark format. Use: mark TASK_NUMBER
____________________________________________________________
Sir, Invalid mark format. Use: mark TASK_NUMBER
____________________________________________________________
Sir, The task number is invalid.
____________________________________________________________
Sir, Invalid delete format. Use: delete TASK_NUMBER
____________________________________________________________
Sir, The task number is invalid.
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] read book
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[T][ ] read book
____________________________________________________________
Bye. Hope to see you again soon!
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
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
 ________________________________
|                                |
|  Good day to you sir!          |
|________________________________|

____________________________________________________________
 ________________________
|                        |
|  Thanks!               |
|________________________|

____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
