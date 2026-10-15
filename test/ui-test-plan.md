# UI Test Plan

This file is the source of truth for console UI tests run with the `$test-ui` skill. Each test case is one program command with its input and expected output. Add concrete test cases before running the skill. Expected output is matched exactly, except that Windows and Unix line endings are treated as equivalent.

## Test cases

### Test case: Add and list typed tasks

**Aim:** Verify that ToDos, Deadlines, and Events are created with the correct type marker and details, and that all three appear in the task list.

**Command:**

```text
sh -c 'rm -f _temp/ui-test-data.txt && exec ./gradlew --quiet --no-daemon --console=plain runConsole -Djaylen.storage=_temp/ui-test-data.txt'
```

**Input:**

```text
todo borrow book
deadline submit report /by 2019-12-08
event project meeting /from 2019-12-09 1400 /to 2019-12-09 1600
deadline do homework /by 2/12/2019 1800
list
bye
```

**Expected output:**

```text
____________________________________________________________
╔══════════════════════╗
║        JAYLEN        ║
╚══════════════════════╝
Hello! I'm Jaylen.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] submit report (by: Dec 08 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Dec 09 2019 14:00 to: Dec 09 2019 16:00)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] do homework (by: Dec 02 2019 18:00)
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] borrow book
 2.[D][ ] submit report (by: Dec 08 2019)
 3.[E][ ] project meeting (from: Dec 09 2019 14:00 to: Dec 09 2019 16:00)
 4.[D][ ] do homework (by: Dec 02 2019 18:00)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Test case: Parse and display dates and times

**Aim:** Verify that ISO dates, day/month/year dates, and date-time inputs are stored as typed values and displayed in a readable format.

**Command:**

```text
sh -c 'rm -f _temp/level8-test-data.txt && exec ./gradlew --quiet --no-daemon --console=plain runConsole -Djaylen.storage=_temp/level8-test-data.txt'
```

**Input:**

```text
deadline return book /by 2/12/2019 1800
event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600
list
bye
```

**Expected output:**

```text
____________________________________________________________
╔══════════════════════╗
║        JAYLEN        ║
╚══════════════════════╝
Hello! I'm Jaylen.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 02 2019 18:00)
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Oct 15 2019 14:00 to: Oct 15 2019 16:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Dec 02 2019 18:00)
 2.[E][ ] project meeting (from: Oct 15 2019 14:00 to: Oct 15 2019 16:00)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Test case: Save tasks and load them after restarting

**Aim:** Verify that tasks and completion status are written to disk and restored when Jaylen starts again. The command creates a clean temporary save file, runs a first session, then starts a second session using that file.

**Command:**

```text
sh -c 'rm -f _temp/level7-test-data.txt; printf "todo persisted task\\ndeadline submit report /by 2019-12-06\\nevent planning /from 2019-12-07 0900 /to 2019-12-07 1000\\nmark 2\\nbye\\n" | ./gradlew --quiet --no-daemon --console=plain runConsole -Djaylen.storage=_temp/level7-test-data.txt >/dev/null; exec ./gradlew --quiet --no-daemon --console=plain runConsole -Djaylen.storage=_temp/level7-test-data.txt'
```

**Input:**

```text
list
bye
```

**Expected output:**

```text
____________________________________________________________
╔══════════════════════╗
║        JAYLEN        ║
╚══════════════════════╝
Hello! I'm Jaylen.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] persisted task
 2.[D][X] submit report (by: Dec 06 2019)
 3.[E][ ] planning (from: Dec 07 2019 09:00 to: Dec 07 2019 10:00)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Test case: Handle invalid input without crashing

**Aim:** Verify that empty, unknown, malformed, and out-of-range commands produce user-friendly errors and that Jaylen continues accepting commands.

**Command:**

```text
sh -c 'rm -f _temp/ui-test-data.txt && exec ./gradlew --quiet --no-daemon --console=plain runConsole -Djaylen.storage=_temp/ui-test-data.txt'
```

**Input:**

```text
todo
blah
deadline return book
event meeting /from Monday
deadline invalid date /by 2019-02-30
mark 1
todo buy milk
mark 2
bye
```

**Expected output:**

```text
____________________________________________________________
╔══════════════════════╗
║        JAYLEN        ║
╚══════════════════════╝
Hello! I'm Jaylen.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
 OOPS!!! I'm sorry, but I don't know what that means.
____________________________________________________________
____________________________________________________________
 OOPS!!! A deadline must include /by followed by a date or time.
____________________________________________________________
____________________________________________________________
 OOPS!!! An event must include /from and /to times.
____________________________________________________________
____________________________________________________________
 OOPS!!! Use a date such as 2019-10-15 or a date and time such as 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 OOPS!!! That task number does not exist.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] buy milk
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! That task number does not exist.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Test case: Reject malformed commands and normalize whitespace

**Aim:** Verify that commands missing required values, reversed event times, and
storage-delimiter characters produce specific errors without crashing. Also verify
that repeated whitespace is normalized and `mark`/`unmark` still work afterward.

**Command:**

```text
sh -c 'rm -f _temp/error-handling-test-data.txt && exec ./gradlew --quiet --no-daemon --console=plain runConsole -Djaylen.storage=_temp/error-handling-test-data.txt'
```

**Input:**

```text
delete
deadline /by 2026-09-25
event reversed /from 2026-09-21 1600 /to 2026-09-21 1400
todo compare A | B
todo    spaced    task
mark 1
unmark 1
list
bye
```

**Expected output:**

```text
____________________________________________________________
╔══════════════════════╗
║        JAYLEN        ║
╚══════════════════════╝
Hello! I'm Jaylen.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! A delete command must include a task number.
____________________________________________________________
____________________________________________________________
 OOPS!!! The description of a deadline cannot be empty.
____________________________________________________________
____________________________________________________________
 OOPS!!! The event end time must be after the start time.
____________________________________________________________
____________________________________________________________
 OOPS!!! Task descriptions cannot contain the | character.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] spaced task
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] spaced task
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] spaced task
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] spaced task
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Test case: Delete a task and keep list numbering contiguous

**Aim:** Verify that `delete` removes the selected task, reports the removed task and new count, and shifts later tasks into contiguous list positions. This also checks that tasks stored in the `ArrayList` retain their types and done status.

**Command:**

```text
sh -c 'rm -f _temp/ui-test-data.txt && exec ./gradlew --quiet --no-daemon --console=plain runConsole -Djaylen.storage=_temp/ui-test-data.txt'
```

**Input:**

```text
todo first task
deadline second task /by 2019-12-13
event third task /from 2019-12-14 1400 /to 2019-12-14 1600
mark 2
delete 2
list
bye
```

**Expected output:**

```text
____________________________________________________________
╔══════════════════════╗
║        JAYLEN        ║
╚══════════════════════╝
Hello! I'm Jaylen.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] first task
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] second task (by: Dec 13 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] third task (from: Dec 14 2019 14:00 to: Dec 14 2019 16:00)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] second task (by: Dec 13 2019)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][X] second task (by: Dec 13 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] first task
 2.[E][ ] third task (from: Dec 14 2019 14:00 to: Dec 14 2019 16:00)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Test case: Find tasks with flexible search terms

**Aim:** Verify that `find` matches multiple partial terms without regard to case or term order, preserves the original task numbers, and reports when there are no matches.

**Command:**

```text
sh -c 'rm -f _temp/level9-test-data.txt && exec ./gradlew --quiet --no-daemon --console=plain runConsole -Djaylen.storage=_temp/level9-test-data.txt'
```

**Input:**

```text
todo read book
deadline return book /by 2019-12-06
event planning /from 2019-12-07 0900 /to 2019-12-07 1000
find BOO urn
find xyz
bye
```

**Expected output:**

```text
____________________________________________________________
╔══════════════════════╗
║        JAYLEN        ║
╚══════════════════════╝
Hello! I'm Jaylen.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 06 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] planning (from: Dec 07 2019 09:00 to: Dec 07 2019 10:00)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 2.[D][ ] return book (by: Dec 06 2019)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 No matching tasks found.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
