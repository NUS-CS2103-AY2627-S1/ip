# Console UI Test Plan

## Test environment

- Java version: Java 25
- Main class: `dennis.Dennis`
- Source files: `src/main/java/dennis/*.java` (source root: `src/main/java`)
- Date format (Level 8): deadline/event dates are entered as `yyyy-MM-dd`
  (e.g. `2019-12-01`) and shown as `MMM dd yyyy` (e.g. `Dec 01 2019`).
- Isolation: Compile into a temporary directory and start a fresh program for each case.
- Comparison: Compare the complete standard output exactly, normalizing only CRLF/LF line endings.
- Failure behavior: Stop at the first compilation or output mismatch; do not run later cases.

## Test cases

### TC-01: Add and list all task types

**Aim:** Verify that ToDo, Deadline, and Event commands create correctly formatted tasks, that `yyyy-MM-dd` dates are parsed and shown as `MMM dd yyyy`, and that `list` displays all of them.

**Input:**

```text
todo borrow book
deadline return book /by 2019-12-01
event project meeting /from 2019-12-02 /to 2019-12-05
list
bye
```

**Expected output:**

```text
 ____                   _     
|  _ \  ___ _ __  _ __ (_)___ 
| | | |/ _ \ '_ \| '_ \| / __|
| |_| |  __/ | | | | | | \__ \
|____/ \___|_| |_|_| |_|_|___/

Hi, my name is Dennis. It is lovely to meet you!
How may I help you today?
_____________________________________________________
Understood. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
Understood. I've added this task:
  [D][ ] return book (by: Dec 01 2019)
Now you have 2 tasks in the list.
Understood. I've added this task:
  [E][ ] project meeting (from: Dec 02 2019 to: Dec 05 2019)
Now you have 3 tasks in the list.
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Dec 01 2019)
3.[E][ ] project meeting (from: Dec 02 2019 to: Dec 05 2019)
Bye. Looking forward to seeing you again!
_____________________________________________________
```

### TC-02: Handle invalid commands and task numbers

**Aim:** Verify that invalid commands, missing command details, and invalid task numbers produce error messages without stopping the chatbot.

**Input:**

```text
todo
deadline return book
event meeting /from 2pm
mark
mark abc
mark 1
unmark abc
unmark 1
delete abc
delete 1
blah
bye
```

**Expected output:**

```text
 ____                   _     
|  _ \  ___ _ __  _ __ (_)___ 
| | | |/ _ \ '_ \| '_ \| / __|
| |_| |  __/ | | | | | | \__ \
|____/ \___|_| |_|_| |_|_|___/

Hi, my name is Dennis. It is lovely to meet you!
How may I help you today?
_____________________________________________________
ERROR!! I'm sorry, todo must contain a task.
_____________________________________________________
ERROR!! Use /by to specify the deadline.
_____________________________________________________
ERROR!! Use /from and /to to specify the duration of the event.
_____________________________________________________
ERROR!! Please enter a task number.
_____________________________________________________
ERROR!! The task number must be an integer.
_____________________________________________________
ERROR!! That task number exceeds the tasks.
_____________________________________________________
ERROR!! The task number must be an integer.
_____________________________________________________
ERROR!! That task number exceeds the tasks.
_____________________________________________________
ERROR!! The task number must be an integer.
_____________________________________________________
ERROR!! That task number exceeds the tasks.
_____________________________________________________
ERROR!! I'm sorry, I don't understand what you are trying to say :(
_____________________________________________________
Bye. Looking forward to seeing you again!
_____________________________________________________
```

### TC-03: Reject a deadline with no description

**Aim:** Verify that a deadline with a date but no task description produces its specific error message (the missing-description check runs before the date is parsed).

**Input:**

```text
deadline /by 2019-12-01
bye
```

**Expected output:**

```text
 ____                   _     
|  _ \  ___ _ __  _ __ (_)___ 
| | | |/ _ \ '_ \| '_ \| / __|
| |_| |  __/ | | | | | | \__ \
|____/ \___|_| |_|_| |_|_|___/

Hi, my name is Dennis. It is lovely to meet you!
How may I help you today?
_____________________________________________________
ERROR!! I'm sorry, deadline must contain a task.
_____________________________________________________
Bye. Looking forward to seeing you again!
_____________________________________________________
```

### TC-04: Delete a task

**Aim:** Verify that `delete` removes the selected task, reports the removed task, updates the task count, and shifts the remaining task numbers forward.

**Input:**

```text
todo read book
deadline return book /by 2019-12-01
event project meeting /from 2019-12-02 /to 2019-12-05
delete 2
list
bye
```

**Expected output:**

```text
 ____                   _     
|  _ \  ___ _ __  _ __ (_)___ 
| | | |/ _ \ '_ \| '_ \| / __|
| |_| |  __/ | | | | | | \__ \
|____/ \___|_| |_|_| |_|_|___/

Hi, my name is Dennis. It is lovely to meet you!
How may I help you today?
_____________________________________________________
Understood. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
Understood. I've added this task:
  [D][ ] return book (by: Dec 01 2019)
Now you have 2 tasks in the list.
Understood. I've added this task:
  [E][ ] project meeting (from: Dec 02 2019 to: Dec 05 2019)
Now you have 3 tasks in the list.
Understood. I've removed this task:
  [D][ ] return book (by: Dec 01 2019)
Now there are 2 tasks in the list.
Here are the tasks in your list:
1.[T][ ] read book
2.[E][ ] project meeting (from: Dec 02 2019 to: Dec 05 2019)
Bye. Looking forward to seeing you again!
_____________________________________________________
```

### TC-05: Saving to disk does not disturb the console output

**Aim:** Verify that the Level 7 auto-save (writing `./data/dennis.txt` after every
change to the task list) runs silently: adding all three task types, marking,
unmarking, and deleting must still produce exactly the normal console output,
with no extra lines, warnings, or stack traces on standard output. The contents
of the save file are checked separately by inspecting `./data/dennis.txt` after the
run; with this input it should contain:

```text
T | 0 | read book
E | 1 | project meeting | 2019-12-02 | 2019-12-05
```

**Input:**

```text
todo read book
deadline return book /by 2019-12-01
event project meeting /from 2019-12-02 /to 2019-12-05
mark 1
unmark 1
delete 2
mark 2
list
bye
```

**Expected output:**

```text
 ____                   _     
|  _ \  ___ _ __  _ __ (_)___ 
| | | |/ _ \ '_ \| '_ \| / __|
| |_| |  __/ | | | | | | \__ \
|____/ \___|_| |_|_| |_|_|___/

Hi, my name is Dennis. It is lovely to meet you!
How may I help you today?
_____________________________________________________
Understood. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
Understood. I've added this task:
  [D][ ] return book (by: Dec 01 2019)
Now you have 2 tasks in the list.
Understood. I've added this task:
  [E][ ] project meeting (from: Dec 02 2019 to: Dec 05 2019)
Now you have 3 tasks in the list.
Excellent! I've marked this task as done:
  [T][X] read book
Alright, I've marked this task as not done yet:
  [T][ ] read book
Understood. I've removed this task:
  [D][ ] return book (by: Dec 01 2019)
Now there are 2 tasks in the list.
Excellent! I've marked this task as done:
  [E][X] project meeting (from: Dec 02 2019 to: Dec 05 2019)
Here are the tasks in your list:
1.[T][ ] read book
2.[E][X] project meeting (from: Dec 02 2019 to: Dec 05 2019)
Bye. Looking forward to seeing you again!
_____________________________________________________
```

### TC-06: Load saved tasks on start-up

**Aim:** Verify that when `./data/dennis.txt` already exists, the chatbot reads it
on start-up so the previous tasks (including their done/not-done state) are
available immediately, before the user enters any command.

**Precondition — create `./data/dennis.txt` with exactly this content before running:**

```text
T | 0 | read book
D | 1 | return book | 2019-12-01
E | 0 | project meeting | 2019-12-02 | 2019-12-05
```

**Input:**

```text
list
bye
```

**Expected output:**

```text
 ____                   _     
|  _ \  ___ _ __  _ __ (_)___ 
| | | |/ _ \ '_ \| '_ \| / __|
| |_| |  __/ | | | | | | \__ \
|____/ \___|_| |_|_| |_|_|___/

Hi, my name is Dennis. It is lovely to meet you!
How may I help you today?
_____________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][X] return book (by: Dec 01 2019)
3.[E][ ] project meeting (from: Dec 02 2019 to: Dec 05 2019)
Bye. Looking forward to seeing you again!
_____________________________________________________
```

### TC-07: Start cleanly when no save file exists

**Aim:** Verify that on a fresh machine, where `./data/dennis.txt` does not exist
yet, the chatbot starts normally with an empty list and no error on standard
output.

**Precondition:** `./data/dennis.txt` must not exist.

**Input:**

```text
list
todo first task
bye
```

**Expected output:**

```text
 ____                   _     
|  _ \  ___ _ __  _ __ (_)___ 
| | | |/ _ \ '_ \| '_ \| / __|
| |_| |  __/ | | | | | | \__ \
|____/ \___|_| |_|_| |_|_|___/

Hi, my name is Dennis. It is lovely to meet you!
How may I help you today?
_____________________________________________________
Here are the tasks in your list:
Understood. I've added this task:
  [T][ ] first task
Now you have 1 tasks in the list.
Bye. Looking forward to seeing you again!
_____________________________________________________
```

### TC-08: Reject a task field containing the save-file separator

**Aim:** The save file separates fields with `" | "`, so a `|` inside a
description or date would corrupt the file. Verify such input is rejected with
a clear error and does not stop the chatbot; well-formed input after it still
works.

**Input:**

```text
todo grocery | list
todo buy milk
list
bye
```

**Expected output:**

```text
 ____                   _     
|  _ \  ___ _ __  _ __ (_)___ 
| | | |/ _ \ '_ \| '_ \| / __|
| |_| |  __/ | | | | | | \__ \
|____/ \___|_| |_|_| |_|_|___/

Hi, my name is Dennis. It is lovely to meet you!
How may I help you today?
_____________________________________________________
ERROR!! A task description cannot contain the '|' character.
_____________________________________________________
Understood. I've added this task:
  [T][ ] buy milk
Now you have 1 tasks in the list.
Here are the tasks in your list:
1.[T][ ] buy milk
Bye. Looking forward to seeing you again!
_____________________________________________________
```

### TC-09: Recover from a corrupted save file

**Aim:** Verify that a save file containing several unreadable lines does not
stop start-up: the readable tasks are still loaded and listed, and the bad
lines are skipped. This includes a `D` line whose date is not `yyyy-MM-dd`,
which Level 8 now rejects at load time. (The per-line "Skipping unreadable
line ..." notes are written to the error stream, so they do not appear in the
standard output compared here; check them separately with `2>` redirection if
desired.)

**Precondition — create `./data/dennis.txt` with exactly this content before running:**

```text
T | 1 | valid todo
GARBAGE
D | 0 | no date
X | 0 | wrong type | z
D | 0 | pay rent | next Friday
E | 0 | ok event | 2019-12-02 | 2019-12-05
```

**Expected error-stream output (not part of the comparison above):**

```text
Skipping unreadable line 2 in data\dennis.txt: expected at least 3 fields but found 1 in "GARBAGE"
Skipping unreadable line 3 in data\dennis.txt: expected 4 fields but found 3 in "D | 0 | no date"
Skipping unreadable line 4 in data\dennis.txt: unknown task type "X" (expected T, D or E) in "X | 0 | wrong type | z"
Skipping unreadable line 5 in data\dennis.txt: A deadline date must be a date in yyyy-MM-dd form, e.g. 2019-12-01.
Loaded 2 task(s) from data\dennis.txt; 4 line(s) were ignored.
```

**Input:**

```text
list
bye
```

**Expected output:**

```text
 ____                   _     
|  _ \  ___ _ __  _ __ (_)___ 
| | | |/ _ \ '_ \| '_ \| / __|
| |_| |  __/ | | | | | | \__ \
|____/ \___|_| |_|_| |_|_|___/

Hi, my name is Dennis. It is lovely to meet you!
How may I help you today?
_____________________________________________________
Here are the tasks in your list:
1.[T][X] valid todo
2.[E][ ] ok event (from: Dec 02 2019 to: Dec 05 2019)
Bye. Looking forward to seeing you again!
_____________________________________________________
```

### TC-10: Reject a date that is not yyyy-MM-dd

**Aim:** Verify that an out-of-range date, a non-date word, and a
non-zero-padded date are each rejected with a clear error without stopping the
chatbot, and that a following well-formed command still works.

**Input:**

```text
deadline submit report /by 2019-13-40
deadline submit report /by tomorrow
event trip /from 2019-1-5 /to 2019-01-06
deadline submit report /by 2019-10-15
list
bye
```

**Expected output:**

```text
 ____                   _     
|  _ \  ___ _ __  _ __ (_)___ 
| | | |/ _ \ '_ \| '_ \| / __|
| |_| |  __/ | | | | | | \__ \
|____/ \___|_| |_|_| |_|_|___/

Hi, my name is Dennis. It is lovely to meet you!
How may I help you today?
_____________________________________________________
ERROR!! A deadline date must be a date in yyyy-MM-dd form, e.g. 2019-12-01.
_____________________________________________________
ERROR!! A deadline date must be a date in yyyy-MM-dd form, e.g. 2019-12-01.
_____________________________________________________
ERROR!! An event start must be a date in yyyy-MM-dd form, e.g. 2019-12-01.
_____________________________________________________
Understood. I've added this task:
  [D][ ] submit report (by: Oct 15 2019)
Now you have 1 tasks in the list.
Here are the tasks in your list:
1.[D][ ] submit report (by: Oct 15 2019)
Bye. Looking forward to seeing you again!
_____________________________________________________
```

### TC-11: Load a pre-Level-8 save file with free-text dates

**Aim:** Verify the upgrade path: a save file written before Level 8 (dates as
free text such as `Sunday`) does not crash start-up. The lines with
unparseable dates are skipped, the rest load, and the chatbot is usable.

**Precondition — create `./data/dennis.txt` with exactly this content before running:**

```text
T | 0 | read book
D | 1 | return book | Sunday
E | 0 | project meeting | Mon 2pm | 4pm
```

**Input:**

```text
list
todo still works
bye
```

**Expected output:**

```text
 ____                   _     
|  _ \  ___ _ __  _ __ (_)___ 
| | | |/ _ \ '_ \| '_ \| / __|
| |_| |  __/ | | | | | | \__ \
|____/ \___|_| |_|_| |_|_|___/

Hi, my name is Dennis. It is lovely to meet you!
How may I help you today?
_____________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
Understood. I've added this task:
  [T][ ] still works
Now you have 2 tasks in the list.
Bye. Looking forward to seeing you again!
_____________________________________________________
```

**Expected error-stream output:**

```text
Skipping unreadable line 2 in data\dennis.txt: A deadline date must be a date in yyyy-MM-dd form, e.g. 2019-12-01.
Skipping unreadable line 3 in data\dennis.txt: An event start must be a date in yyyy-MM-dd form, e.g. 2019-12-01.
Loaded 1 task(s) from data\dennis.txt; 2 line(s) were ignored.
```

### TC-12: `on <date>` lists tasks occurring on a date (stretch goal)

**Aim:** Verify the `on` command. It should list every deadline whose date
equals the given date and every event whose span includes it (both ends
inclusive), shown at their real list positions. It should also handle a date
with no matches, a missing date argument, and an unparseable date.

**Precondition:** `./data/dennis.txt` must not exist (so the list starts empty).

**Input:**

```text
todo read book
deadline return book /by 2019-12-01
event project meeting /from 2019-12-02 /to 2019-12-05
deadline pay rent /by 2019-12-05
on 2019-12-01
on 2019-12-05
on 2019-11-11
on
on notadate
bye
```

**Expected output:**

```text
 ____                   _     
|  _ \  ___ _ __  _ __ (_)___ 
| | | |/ _ \ '_ \| '_ \| / __|
| |_| |  __/ | | | | | | \__ \
|____/ \___|_| |_|_| |_|_|___/

Hi, my name is Dennis. It is lovely to meet you!
How may I help you today?
_____________________________________________________
Understood. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
Understood. I've added this task:
  [D][ ] return book (by: Dec 01 2019)
Now you have 2 tasks in the list.
Understood. I've added this task:
  [E][ ] project meeting (from: Dec 02 2019 to: Dec 05 2019)
Now you have 3 tasks in the list.
Understood. I've added this task:
  [D][ ] pay rent (by: Dec 05 2019)
Now you have 4 tasks in the list.
Here are the tasks on Dec 01 2019:
2.[D][ ] return book (by: Dec 01 2019)
Here are the tasks on Dec 05 2019:
3.[E][ ] project meeting (from: Dec 02 2019 to: Dec 05 2019)
4.[D][ ] pay rent (by: Dec 05 2019)
Here are the tasks on Nov 11 2019:
You have no deadlines or events on that date.
ERROR!! Please enter a date, e.g. on 2019-12-01.
_____________________________________________________
ERROR!! The date must be a date in yyyy-MM-dd form, e.g. 2019-12-01.
_____________________________________________________
Bye. Looking forward to seeing you again!
_____________________________________________________
```

## Session record

After a run, report the input and actual output in the conversation. Do not store transient session results in this plan unless the user asks for them to be saved.
