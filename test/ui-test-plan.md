# UI Test Plan

## Configuration

- Setup command: `javac -d build/classes src/main/java/panda/exception/PandaException.java src/main/java/panda/exception/EmptyDescriptionException.java src/main/java/panda/exception/EmptySearchTermException.java src/main/java/panda/exception/InvalidCommandException.java src/main/java/panda/exception/InvalidTaskNumberException.java src/main/java/panda/exception/InvalidUpdateException.java src/main/java/panda/exception/MissingDateTimeException.java src/main/java/panda/exception/InvalidDateException.java src/main/java/panda/exception/InvalidEventPeriodException.java src/main/java/panda/exception/DataLoadingException.java src/main/java/panda/exception/DataSavingException.java src/main/java/panda/parser/Command.java src/main/java/panda/task/TaskType.java src/main/java/panda/task/TaskStatus.java src/main/java/panda/task/Task.java src/main/java/panda/task/Todo.java src/main/java/panda/task/Deadline.java src/main/java/panda/task/Event.java src/main/java/panda/ui/Ui.java src/main/java/panda/storage/Storage.java src/main/java/panda/task/TaskList.java src/main/java/panda/parser/Parser.java src/main/java/panda/Panda.java`
- Run command: `python test/run_panda_ui.py`
- Timeout seconds: `10`

## Test Case: Mark a task as done

Aim: Verify that `mark` changes the selected task to done and that `list` displays its new status.

Input:

```text
todo read book
todo return book
mark 2
list
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] return book
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [X] return book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[T][X] return book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Load stored tasks when Panda starts

Aim: Verify that startup loading reconstructs each task subtype and restores its completion status in the ArrayList.

Run command: `python test/run_panda_ui.py --fixture test/data/ui-valid-tasks.txt`

Input:

```text
list
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019 18:00)
3.[E][ ] project meeting (from: Aug 06 2019 14:00 to: Aug 08 2019 16:00)
4.[T][X] join sports club
5.[T][ ] buy bread | milk
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Find tasks by name

Aim: Verify that `find <keyword>` performs case-insensitive substring matching across task types, preserves original task numbers, and shows only matching tasks.

Run command: `python test/run_panda_ui.py --fixture test/data/ui-valid-tasks.txt`

Input:

```text
find BOOK
find bread
find homework
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
Here are the matching tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019 18:00)
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
5.[T][ ] buy bread | milk
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Reject a find command without a keyword

Aim: Verify that `find` without a keyword reports EmptySearchTermException and does not change the task list.

Input:

```text
find
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
OOPS!!! This panda needs a search keyword after find.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Filter dated tasks by date

Aim: Verify that `list yyyy-MM-dd` shows matching deadlines and multi-day events with their original task numbers while excluding date-free and nonmatching tasks.

Run command: `python test/run_panda_ui.py --fixture test/data/ui-valid-tasks.txt`

Input:

```text
list 2019-06-06
list 2019-08-07
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
Here are the tasks in your list:
2.[D][ ] return book (by: Jun 06 2019 18:00)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
3.[E][ ] project meeting (from: Aug 06 2019 14:00 to: Aug 08 2019 16:00)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Report and skip malformed stored task data

Aim: Verify that every malformed record is reported and skipped while valid records before and after them are loaded.

Run command: `python test/run_panda_ui.py --fixture test/data/ui-malformed-tasks.txt`

Input:

```text
list
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
Line 2 has an invalid completion status; expected 0 or 1.
Line 4 has an invalid task type; expected T, D, or E.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019 18:00)
3.[E][ ] project meeting (from: Aug 06 2019 14:00 to: Aug 06 2019 16:00)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Unmark a completed task

Aim: Verify that `unmark` reverses a completed task's status and that `list` displays it as unfinished.

Input:

```text
todo read book
todo return book
mark 2
unmark 2
list
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] return book
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [X] return book
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [ ] return book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[T][ ] return book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Add a date-free to-do

Aim: Verify that `todo` creates a Todo task, confirms its creation, and displays its type and unfinished status.

Input:

```text
todo borrow book
list
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Add a deadline with a valid date and time

Aim: Verify that `deadline` parses a valid date and time and displays it as `MMM dd yyyy HH:mm`.

Input:

```text
todo read book
deadline submit report /by 2019-10-11 17:00
list
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] submit report (by: Oct 11 2019 17:00)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] submit report (by: Oct 11 2019 17:00)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Add events with start and end values

Aim: Verify that `event` parses valid endpoints and displays each as `MMM dd yyyy HH:mm`.

Input:

```text
event team project meeting /from 2019-10-02 14:00 /to 2019-10-02 16:00
event orientation week /from 2019-10-04 09:00 /to 2019-10-11 17:00
list
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
Got it. I've added this task:
  [E][ ] team project meeting (from: Oct 02 2019 14:00 to: Oct 02 2019 16:00)
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] orientation week (from: Oct 04 2019 09:00 to: Oct 11 2019 17:00)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[E][ ] team project meeting (from: Oct 02 2019 14:00 to: Oct 02 2019 16:00)
2.[E][ ] orientation week (from: Oct 04 2019 09:00 to: Oct 11 2019 17:00)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Reject invalid list dates

Aim: Verify that `list` rejects malformed and impossible filter dates through InvalidDateException.

Input:

```text
list 2025/02/28
list 2025-02-29
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
OOPS!!! This panda needs a valid list date in yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda needs a valid list date in yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Reject invalid deadline and event timings

Aim: Verify that malformed dates and an Event starting after it ends report focused errors and do not add tasks.

Input:

```text
deadline leap-day report /by 2025-02-29 12:00
event bad start /from tomorrow /to 2026-08-26 16:00
event bad end /from 2026-08-26 14:00 /to 2025-02-29 16:00
event backwards /from 2026-08-26 17:00 /to 2026-08-26 16:00
list
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
OOPS!!! This panda needs a valid date and time in yyyy-MM-dd HH:mm format.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda needs a valid date and time in yyyy-MM-dd HH:mm format.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda needs a valid date and time in yyyy-MM-dd HH:mm format.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda needs an event to start no later than it ends.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Reject a to-do with no description

Aim: Verify that `todo` without a description reports a panda-themed error and does not add a task.

Input:

```text
todo
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
OOPS!!! This panda needs a todo description before it can get to work.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Reject unsupported command messages

Aim: Verify that the Command enum rejects unknown keywords and arguments supplied to the argument-free `bye` command.

Input:

```text
blah
unknown extra
bye extra
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
OOPS!!! I'm bamboo-zled; I don't know what that means :-(
____________________________________________________________
____________________________________________________________
OOPS!!! I'm bamboo-zled; I don't know what that means :-(
____________________________________________________________
____________________________________________________________
OOPS!!! I'm bamboo-zled; I don't know what that means :-(
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Reject missing deadline and event descriptions

Aim: Verify that task-creation commands without descriptions throw consistent EmptyDescriptionException messages.

Input:

```text
deadline
event
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
OOPS!!! This panda needs a deadline description before it can get to work.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda needs an event description before it can get to work.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Reject missing deadline and event timing details

Aim: Verify that incomplete deadline and event commands report their required structures through MissingDateTimeException.

Input:

```text
deadline submit report
event project meeting /from Monday
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
OOPS!!! This panda needs more timing details. Try: deadline <description> /by <date or time>.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda needs more timing details. Try: event <description> /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Reject invalid task numbers

Aim: Verify that mark, unmark, and delete reject missing, non-numeric, and out-of-range task numbers through InvalidTaskNumberException.

Input:

```text
mark
unmark bamboo
mark 1
delete bamboo
delete 1
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
OOPS!!! This panda needs a valid task number after mark.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda needs a valid task number after unmark.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda cannot find task 1 in the bamboo stack.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda needs a valid task number after delete.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda cannot find task 1 in the bamboo stack.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Delete a task from the ArrayList

Aim: Verify that `delete` reports the removed task, preserves the order of the remaining ArrayList entries, and updates the task count.

Input:

```text
todo read book
event project meeting /from 2019-08-06 14:00 /to 2019-08-06 16:00
todo borrow book
delete 2
list
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 06 2019 14:00 to: Aug 06 2019 16:00)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [E][ ] project meeting (from: Aug 06 2019 14:00 to: Aug 06 2019 16:00)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Save all task-list changes

Aim: Verify that added task subtypes and a changed completion status are written to disk with escaped task text.

Run command: `python test/run_panda_ui.py --expected-data test/data/ui-expected-saved-tasks.txt`

Input:

```text
todo buy bread | milk
deadline return book /by 2019-06-06 18:00
event project meeting /from 2019-08-06 14:00 /to 2019-08-06 16:00
mark 1
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
Got it. I've added this task:
  [T][ ] buy bread | milk
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Jun 06 2019 18:00)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 06 2019 14:00 to: Aug 06 2019 16:00)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [X] buy bread | milk
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Report a task-saving failure

Aim: Verify that a task remains available in memory and DataSavingException reports an unwritable storage destination.

Run command: `python test/run_panda_ui.py --unwritable`

Input:

```text
todo borrow book
list
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
OOPS!!! This panda could not save its bamboo archive.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Create a missing data file and parent folders

Aim: Verify that a first run starts without errors and creates the relative data file and missing folders when a task is added.

Run command: `python test/run_panda_ui.py --missing-parent --expected-data test/data/ui-expected-first-run.txt`

Input:

```text
todo first task
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
Got it. I've added this task:
  [T][ ] first task
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Update task names and save them

Aim: Verify that `update <number> /name <name>` renames every task subtype while preserving status, timing, order, and stored data.

Run command: `python test/run_panda_ui.py --fixture test/data/ui-valid-tasks.txt --expected-data test/data/ui-expected-renamed-tasks.txt`

Input:

```text
update 1 /name read Java book
update 2 /name return library book
update 3 /name project consultation
list
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
Got it. I've updated this task:
  [T][X] read Java book
____________________________________________________________
____________________________________________________________
Got it. I've updated this task:
  [D][ ] return library book (by: Jun 06 2019 18:00)
____________________________________________________________
____________________________________________________________
Got it. I've updated this task:
  [E][ ] project consultation (from: Aug 06 2019 14:00 to: Aug 08 2019 16:00)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read Java book
2.[D][ ] return library book (by: Jun 06 2019 18:00)
3.[E][ ] project consultation (from: Aug 06 2019 14:00 to: Aug 08 2019 16:00)
4.[T][X] join sports club
5.[T][ ] buy bread | milk
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Reject invalid name updates

Aim: Verify that update rejects missing numbers, malformed name changes, and unavailable task numbers without changing stored tasks.

Input:

```text
update
update bamboo /name read book
update 1
update 1 /name
update 1 /title tomorrow
update 99 /name read book
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
OOPS!!! This panda needs a valid task number after update.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda needs a valid task number after update.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda needs a valid update. Try /name <new name>, /by <date and time>, or /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda needs a valid update. Try /name <new name>, /by <date and time>, or /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda needs a valid update. Try /name <new name>, /by <date and time>, or /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda cannot find task 99 in the bamboo stack.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Update task timings and save them

Aim: Verify that update reschedules Deadlines and Events while preserving names, statuses, positions, and stored data.

Run command: `python test/run_panda_ui.py --fixture test/data/ui-valid-tasks.txt --expected-data test/data/ui-expected-rescheduled-tasks.txt`

Input:

```text
update 2 /by 2026-09-15 18:00
update 3 /from 2026-09-16 14:00 /to 2026-09-16 17:00
list
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
Got it. I've updated this task:
  [D][ ] return book (by: Sep 15 2026 18:00)
____________________________________________________________
____________________________________________________________
Got it. I've updated this task:
  [E][ ] project meeting (from: Sep 16 2026 14:00 to: Sep 16 2026 17:00)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Sep 15 2026 18:00)
3.[E][ ] project meeting (from: Sep 16 2026 14:00 to: Sep 16 2026 17:00)
4.[T][X] join sports club
5.[T][ ] buy bread | milk
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Reject invalid timing updates

Aim: Verify that timing updates reject incompatible task types, malformed intervals, and invalid dates without changing tasks.

Run command: `python test/run_panda_ui.py --fixture test/data/ui-valid-tasks.txt`

Input:

```text
update 1 /by 2026-09-15 18:00
update 2 /from 2026-09-16 14:00 /to 2026-09-16 17:00
update 3 /by 2026-09-15 18:00
update 2 /by 2025-02-29 12:00
update 3 /from tomorrow /to 2026-09-16 17:00
update 3 /from 2026-09-16 14:00 /to later
update 3 /from 2026-09-17 18:00 /to 2026-09-17 17:00
update 3 /from 2026-09-16 14:00
list
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
OOPS!!! This panda cannot apply that timing update to this task type.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda cannot apply that timing update to this task type.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda cannot apply that timing update to this task type.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda needs a valid date and time in yyyy-MM-dd HH:mm format.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda needs a valid date and time in yyyy-MM-dd HH:mm format.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda needs a valid date and time in yyyy-MM-dd HH:mm format.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda needs an event to start no later than it ends.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda needs a valid update. Try /name <new name>, /by <date and time>, or /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019 18:00)
3.[E][ ] project meeting (from: Aug 06 2019 14:00 to: Aug 08 2019 16:00)
4.[T][X] join sports club
5.[T][ ] buy bread | milk
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Update task names and timings together

Aim: Verify that combined updates change and save both properties while preserving task status, type, and position.

Run command: `python test/run_panda_ui.py --fixture test/data/ui-valid-tasks.txt --expected-data test/data/ui-expected-combined-updated-tasks.txt`

Input:

```text
update 2 /by 2026-09-15 18:00 /name submit final report
update 3 /from 2026-09-16 14:00 /to 2026-09-16 17:00 /name project consultation
list
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
Got it. I've updated this task:
  [D][ ] submit final report (by: Sep 15 2026 18:00)
____________________________________________________________
____________________________________________________________
Got it. I've updated this task:
  [E][ ] project consultation (from: Sep 16 2026 14:00 to: Sep 16 2026 17:00)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] submit final report (by: Sep 15 2026 18:00)
3.[E][ ] project consultation (from: Sep 16 2026 14:00 to: Sep 16 2026 17:00)
4.[T][X] join sports club
5.[T][ ] buy bread | milk
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Reject invalid combined updates atomically

Aim: Verify that malformed, invalid-date, and incompatible combined updates do not change either property.

Run command: `python test/run_panda_ui.py --fixture test/data/ui-valid-tasks.txt`

Input:

```text
update 1 /by 2026-09-15 18:00 /name renamed todo
update 2 /by 2025-02-29 12:00 /name renamed deadline
update 3 /from tomorrow /to 2026-09-16 17:00 /name renamed event
update 2 /by 2026-09-15 18:00 /name
update 3 /from 2026-09-16 14:00 /to 2026-09-16 17:00 /name
list
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
OOPS!!! This panda cannot apply that timing update to this task type.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda needs a valid date and time in yyyy-MM-dd HH:mm format.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda needs a valid date and time in yyyy-MM-dd HH:mm format.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda needs a valid update. Try /name <new name>, /by <date and time>, or /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
OOPS!!! This panda needs a valid update. Try /name <new name>, /by <date and time>, or /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019 18:00)
3.[E][ ] project meeting (from: Aug 06 2019 14:00 to: Aug 08 2019 16:00)
4.[T][X] join sports club
5.[T][ ] buy bread | milk
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
