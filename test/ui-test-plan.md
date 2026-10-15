# Console UI Test Plan

This plan contains exact-output regression tests for Clearblue's command-line interface. Each test case runs in a fresh program process with an empty task list.

**Persistence note:** Clearblue saves tasks to `data/clearblue.txt` on every change and loads them on startup. Because every test case here starts a fresh process in the same working directory, the runner (`run_ui_tests.py`) deletes the `data/` folder before each case so the "empty task list" guarantee above still holds — none of the cases below exercise save/load behavior directly. Cross-restart persistence (a second process picking up a first process's saved tasks) and corrupted/missing-file handling were verified manually, since this runner executes only one process per case and cannot restart with retained state mid-case. This includes verifying that a `yyyy-MM-dd` deadline/event value survives a restart as a real date (not degraded into free text) — save writes the original raw text, not the `MMM dd yyyy` display text. It also includes verifying that a save file with some corrupted lines mixed in with valid ones loads the valid tasks and prints `OOPS!!! <N> line(s) in your saved data could not be read and was/were skipped.` before the banner, rather than silently losing the corrupted entries.

## Configuration

- **Working directory:** `.`
- **Required Java major version:** `25`
- **Compile command:** `javac -d {classes_dir} src/main/java/clearblue/*.java src/main/java/clearblue/*/*.java`
- **Run command:** `java -cp {classes_dir} clearblue.Clearblue`
- **Timeout seconds:** `10`
- **Comparison:** Standard output is compared line-for-line. CRLF and LF are treated equally, and one final newline is ignored. Any standard-error output fails the case.

## Test cases

### TC-01: Add and list a todo

**Aim:** Verify that a todo is stored without date information and displayed with the `[T]` type marker.

**Inputs:**
```text
todo borrow book
list
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] borrow book
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] borrow book
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```

### TC-02: Preserve a deadline value as text

**Aim:** Verify that an arbitrary `/by` value is retained as a string and displayed with the `[D]` type marker.

**Inputs:**
```text
deadline do homework /by no idea :-p
list
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] do homework (by: no idea :-p)
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[D][ ] do homework (by: no idea :-p)
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```

### TC-03: Add, mark, and list an event

**Aim:** Verify that an event retains both time strings and its `[E]` type marker after being marked as done.

**Inputs:**
```text
event project meeting /from Mon 2pm /to 4pm
mark 1
list
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Nice! I've marked this task as done:
       [E][X] project meeting (from: Mon 2pm to: 4pm)
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[E][X] project meeting (from: Mon 2pm to: 4pm)
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```

### TC-04: Reject empty and unknown commands

**Aim:** Verify that blank input, an empty todo, unknown commands, and extra arguments on argument-free commands produce helpful errors without ending the session.

**Inputs:**
```text

todo
blah
list extra
bye now
list
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! Please enter a command.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! A todo needs a description after "todo".
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! I don't recognize that command. Try todo, deadline, event, list, mark, unmark, delete, on, find, undo, or bye.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! I don't recognize that command. Try todo, deadline, event, list, mark, unmark, delete, on, find, undo, or bye.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! I don't recognize that command. Try todo, deadline, event, list, mark, unmark, delete, on, find, undo, or bye.
    ____________________________________________________________
    ____________________________________________________________
     Your task list is empty.
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```

### TC-05: Reject malformed deadlines and events

**Aim:** Verify that every missing deadline or event component produces an error that identifies the missing value or separator.

**Inputs:**
```text
deadline
deadline /by Sunday
deadline return book /by
event
event project meeting /from Mon 2pm
event /from Mon 2pm /to 4pm
event meeting /from /to 4pm
event meeting /from 2pm /to
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! A deadline needs a /by separator. Example: deadline return book /by Sunday
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! A deadline needs a description before /by.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! A deadline needs a date or time after /by.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! An event needs a /from separator. Example: event meeting /from 2pm /to 4pm
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! An event needs a /to separator. Example: event meeting /from 2pm /to 4pm
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! An event needs a description before /from.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! An event needs a start date or time after /from.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! An event needs an end date or time after /to.
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```

### TC-06: Reject invalid mark and unmark commands

**Aim:** Verify that task status commands handle an empty list, missing numbers, non-numeric values, and out-of-range numbers without crashing or changing the wrong task.

**Inputs:**
```text
mark 1
todo read book
mark
mark one
mark 2
mark 1
unmark
unmark zero
unmark 0
unmark 1
list
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! There are no tasks to mark.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! Tell me which task to mark. Example: mark 1
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! The task number must be a whole number. Example: mark 1
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! Task 2 does not exist. Choose 1.
    ____________________________________________________________
    ____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! Tell me which task to unmark. Example: unmark 1
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! The task number must be a whole number. Example: unmark 1
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! Task 0 does not exist. Choose 1.
    ____________________________________________________________
    ____________________________________________________________
     OK, I've marked this task as not done yet:
       [T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```

### TC-07: Delete a task and renumber the list

**Aim:** Verify that deleting a task removes the selected item and shifts later tasks forward without changing their details.

**Inputs:**
```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
delete 2
list
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: June 6th)
     Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
     Now you have 3 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Noted. I've removed this task:
       [D][ ] return book (by: June 6th)
     Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```

### TC-08: Reject invalid delete commands

**Aim:** Verify that deleting from an empty list or using a missing, non-numeric, or out-of-range number leaves the task list unchanged.

**Inputs:**
```text
delete 1
todo keep task
delete
delete one
delete 0
delete 2
list
delete 1
list
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! There are no tasks to delete.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] keep task
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! Tell me which task to delete. Example: delete 1
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! The task number must be a whole number. Example: delete 1
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! Task 0 does not exist. Choose 1.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! Task 2 does not exist. Choose 1.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] keep task
    ____________________________________________________________
    ____________________________________________________________
     Noted. I've removed this task:
       [T][ ] keep task
     Now you have 0 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Your task list is empty.
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```

### TC-09: Understand yyyy-MM-dd dates in deadlines and events

**Aim:** Verify that a `yyyy-MM-dd` value given for `/by`, `/from`, or `/to` is parsed as a real date and displayed as `MMM dd yyyy`.

**Inputs:**
```text
deadline return book /by 2019-06-06
event trip /from 2019-12-01 /to 2019-12-05
list
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: Jun 06 2019)
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] trip (from: Dec 01 2019 to: Dec 05 2019)
     Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[D][ ] return book (by: Jun 06 2019)
     2.[E][ ] trip (from: Dec 01 2019 to: Dec 05 2019)
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```

### TC-10: Keep non-date text unchanged in deadlines and events

**Aim:** Verify that a `/by`, `/from`, or `/to` value that is not a `yyyy-MM-dd` date is still displayed exactly as entered.

**Inputs:**
```text
deadline homework /by no idea :-p
event meeting /from Mon 2pm /to 4pm
list
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] homework (by: no idea :-p)
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] meeting (from: Mon 2pm to: 4pm)
     Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[D][ ] homework (by: no idea :-p)
     2.[E][ ] meeting (from: Mon 2pm to: 4pm)
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```

### TC-11: List deadlines and events on a specific date

**Aim:** Verify that `on` lists deadlines/events whose `by`, `from`, or `to` date matches the given date, and skips a todo and a non-matching date.

**Inputs:**
```text
deadline return book /by 2019-06-06
event trip /from 2019-06-06 /to 2019-06-08
todo unrelated
on 2019-06-06
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: Jun 06 2019)
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] trip (from: Jun 06 2019 to: Jun 08 2019)
     Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] unrelated
     Now you have 3 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the deadlines and events on Jun 06 2019:
     1.[D][ ] return book (by: Jun 06 2019)
     2.[E][ ] trip (from: Jun 06 2019 to: Jun 08 2019)
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```

### TC-12: Reject invalid or missing `on` command input

**Aim:** Verify that `on` with no matching tasks, a non-`yyyy-MM-dd` date, or no date argument all produce the correct message without crashing.

**Inputs:**
```text
deadline return book /by 2019-06-06
on 2020-01-01
on not-a-date
on
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: Jun 06 2019)
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     There are no deadlines or events on Jan 01 2020.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! The date must be in yyyy-MM-dd format. Example: on 2019-06-06
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! Tell me which date to check. Example: on 2019-06-06
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```

### TC-13: Find tasks by a keyword in their description

**Aim:** Verify that `find` matches task descriptions case-insensitively across todos, deadlines, and events, and skips tasks that don't match.

**Inputs:**
```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
find book
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: June 6th)
     Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
     Now you have 3 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the matching tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: June 6th)
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```

### TC-14: Reject a missing find keyword and report no matches

**Aim:** Verify that `find` with no keyword produces a helpful error, and that a keyword matching nothing reports there are no matches rather than crashing.

**Inputs:**
```text
todo read book
find xyz
find
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     No tasks in your list match "xyz".
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! Tell me what to search for. Example: find book
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```

### TC-15: Undo reverses the most recent add, delete, and mark command

**Aim:** Verify that `undo` reverses an add (by removing the task), a delete (by restoring the task, reported as an add), and a mark (by reporting the task unmarked again).

**Inputs:**
```text
todo read book
undo
list
todo return laptop
delete 1
undo
list
mark 1
undo
list
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Noted. I've removed this task:
       [T][ ] read book
     Now you have 0 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Your task list is empty.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] return laptop
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Noted. I've removed this task:
       [T][ ] return laptop
     Now you have 0 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] return laptop
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] return laptop
    ____________________________________________________________
    ____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] return laptop
    ____________________________________________________________
    ____________________________________________________________
     OK, I've marked this task as not done yet:
       [T][ ] return laptop
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] return laptop
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```

### TC-16: Undo skips read-only commands and does not chain

**Aim:** Verify that a read-only `list` between an add and `undo` does not clear the undo history (the earlier add is still undone), and that a second consecutive `undo` reports nothing left to undo rather than redoing.

**Inputs:**
```text
todo x
list
undo
undo
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] x
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] x
    ____________________________________________________________
    ____________________________________________________________
     Noted. I've removed this task:
       [T][ ] x
     Now you have 0 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! There is nothing to undo.
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```

### TC-17: Reject a command parameter given more than once

**Aim:** Verify that repeating `/by`, `/from`, or `/to` in one command produces a clear error, instead of silently keeping only the first or last value.

**Inputs:**
```text
deadline return book /by Sunday /by Monday
event trip /from 2pm /from 3pm /to 4pm
event trip /from 2pm /to 4pm /to 5pm
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! A deadline can only have one /by.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! An event can only have one /from.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! An event can only have one /to.
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```

### TC-18: Reject task text containing the storage field separator

**Aim:** Verify that a description or date/time value containing `" | "` (the exact save-file field separator) is rejected, instead of silently corrupting the saved data on the next load.

**Inputs:**
```text
todo read book | urgent
deadline return book | urgent /by Sunday
event trip | overseas /from 2pm /to 4pm
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! A task's description can't contain " | ", since that's used internally to save your data.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! A task's description can't contain " | ", since that's used internally to save your data.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! A task's description can't contain " | ", since that's used internally to save your data.
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```

### TC-19: Reject an event whose start is not before its end

**Aim:** Verify that an event is rejected when both `/from` and `/to` are real dates and the start is not strictly before the end, but that free-text times (which can't be compared this way) are still accepted regardless of order.

**Inputs:**
```text
event trip /from 2019-06-08 /to 2019-06-06
event trip /from 2019-06-06 /to 2019-06-06
event trip /from 4pm /to 2pm
list
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! An event's start date must be before its end date.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! An event's start date must be before its end date.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] trip (from: 4pm to: 2pm)
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[E][ ] trip (from: 4pm to: 2pm)
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```
