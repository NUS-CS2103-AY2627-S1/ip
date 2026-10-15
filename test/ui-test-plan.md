# UI Test Plan

This file defines command-line UI test sessions for Bingus Dingus. Each session is run independently and in document order. A failure stops the test run.

The `bye` command must display its farewell response before terminating the application.

## Test case 1: Add a deadline task

Aim: Verify that a deadline command creates and displays a deadline task with its due date.

Inputs:

```text
deadline return book /by 2/12/2019 1800
bye
```

Expected output:

```text
        .-""""-.
       /  o  o  \
      |    ∆     |     BINGUS
      |  \___/   |     DINGUS
       \        /
        '-.__.-'
Hey there, I'm Bingus Dingusss.
How can I help ya?
Got it. I've added this task:
  [D][ ] return book (by: Dec 02 2019 6:00 PM)
Now you have 1 tasks in the list.
Goodbye!
```

## Test case 2: Add an event task

Aim: Verify that an event command parses the description, start time, and end time.

Inputs:

```text
event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600
bye
```

Expected output:

```text
        .-""""-.
       /  o  o  \
      |    ∆     |     BINGUS
      |  \___/   |     DINGUS
       \        /
        '-.__.-'
Hey there, I'm Bingus Dingusss.
How can I help ya?
Got it. I've added this task:
  [E][ ] project meeting (from: Oct 15 2019 2:00 PM to: Oct 15 2019 4:00 PM)
Now you have 1 tasks in the list.
Goodbye!
```

## Test case 3: Reject malformed task commands

Aim: Verify that malformed todo, deadline, event, and unknown commands are handled without terminating the application or adding tasks.

Inputs:

```text
todo
deadline return book
deadline return book /by not-a-date
event project meeting /from Mon 2pm
event project meeting /from 2026-09-02 1000 /to 2026-09-02 0900
unknown command
list
bye
```

Expected output:

```text
        .-""""-.
       /  o  o  \
      |    ∆     |     BINGUS
      |  \___/   |     DINGUS
       \        /
        '-.__.-'
Hey there, I'm Bingus Dingusss.
How can I help ya?
I've got no idea watchu talkin' about
Use:
todo <description>
deadline <description> /by <date>, or
event <description> /from <start> /to <end>.
deadline requires a description and a date
Use:
todo <description>
deadline <description> /by <date>, or
event <description> /from <start> /to <end>.
deadline date/time must use yyyy-mm-dd or d/M/yyyy HHmm
Use:
todo <description>
deadline <description> /by <date>, or
event <description> /from <start> /to <end>.
event requires a description, start, and end
Use:
todo <description>
deadline <description> /by <date>, or
event <description> /from <start> /to <end>.
event end date/time must be later than the start date/time
Use:
todo <description>
deadline <description> /by <date>, or
event <description> /from <start> /to <end>.
I've got no idea watchu talkin' about
Use:
todo <description>
deadline <description> /by <date>, or
event <description> /from <start> /to <end>.
Here are the tasks in your list:
Goodbye!
```

## Test case 4: Handle invalid task numbers

Aim: Verify that non-numeric and out-of-range task numbers are rejected without crashing.

Inputs:

```text
todo buy milk
mark abc
mark 2
unmark 0
unmark 1
bye
```

Expected output:

```text
        .-""""-.
       /  o  o  \
      |    ∆     |     BINGUS
      |  \___/   |     DINGUS
       \        /
        '-.__.-'
Hey there, I'm Bingus Dingusss.
How can I help ya?
Got it. I've added this task:
  [T][ ] buy milk
Now you have 1 tasks in the list.
Sorry, please specify a valid task number.
Sorry, that task number is invalid.
Sorry, that task number is invalid.
That task has not been marked done yet
Goodbye!
```

## Test case 5: Delete a task

Aim: Verify that deleting a task removes it and shifts the remaining task numbers.

Inputs:

```text
todo buy milk
todo return book
delete 1
list
bye
```

Expected output:

```text
        .-""""-.
       /  o  o  \
      |    ∆     |     BINGUS
      |  \___/   |     DINGUS
       \        /
        '-.__.-'
Hey there, I'm Bingus Dingusss.
How can I help ya?
Got it. I've added this task:
  [T][ ] buy milk
Now you have 1 tasks in the list.
Got it. I've added this task:
  [T][ ] return book
Now you have 2 tasks in the list.
I've removed this task:
  buy milk
Now you have 1 tasks in the list.
Here are the tasks in your list:
1. [T][ ] return book
Goodbye!
```

## Test case 6: Find tasks by description keyword

Aim: Verify that `find` displays tasks whose descriptions contain the keyword, ignoring letter case.

Inputs:

```text
todo buy milk
todo read book
deadline return book /by 2019-06-06
find BOOK
bye
```

Expected output:

```text
        .-""""-.
       /  o  o  \
      |    ∆     |     BINGUS
      |  \___/   |     DINGUS
       \        /
        '-.__.-'
Hey there, I'm Bingus Dingusss.
How can I help ya?
Got it. I've added this task:
  [T][ ] buy milk
Now you have 1 tasks in the list.
Got it. I've added this task:
  [T][ ] read book
Now you have 2 tasks in the list.
Got it. I've added this task:
  [D][ ] return book (by: Jun 06 2019)
Now you have 3 tasks in the list.
Here are the matching tasks in your list:
2. [T][ ] read book
3. [D][ ] return book (by: Jun 06 2019)
Goodbye!
```

## Test case 7: Save task-list changes to disk

Aim: Verify that adding, completing, and deleting tasks writes the current task list to `./data/bingusdingus.txt`.

Inputs:

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
mark 1
delete 3
bye
```

Expected output:

```text
        .-""""-.
       /  o  o  \
      |    ∆     |     BINGUS
      |  \___/   |     DINGUS
       \        /
        '-.__.-'
Hey there, I'm Bingus Dingusss.
How can I help ya?
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
Got it. I've added this task:
  [D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 06 2019 2:00 PM to: Aug 06 2019 4:00 PM)
Now you have 3 tasks in the list.
Nice! I've marked this task as done:
[T][X] read book
I've removed this task:
  project meeting
Now you have 2 tasks in the list.
Goodbye!
```

After the session, `data/bingusdingus.txt` should contain:

```text
T | 1 | read book
D | 0 | return book | 2019-06-06T00:00
```

## Test case 8: Load saved tasks at startup

Aim: Verify that tasks saved in `./data/bingusdingus.txt` are loaded when the application starts.

Setup: Run test case 7 first, leaving its expected storage file in place.

Inputs:

```text
list
bye
```

Expected output:

```text
        .-""""-.
       /  o  o  \
      |    ∆     |     BINGUS
      |  \___/   |     DINGUS
       \        /
        '-.__.-'
Hey there, I'm Bingus Dingusss.
How can I help ya?
Here are the tasks in your list:
1. [T][X] read book
2. [D][ ] return book (by: Jun 06 2019)
Goodbye!
```

## Test case 9: Ignore malformed stored tasks

Aim: Verify that invalid storage records do not crash startup or prevent valid tasks from loading.

Setup: Replace `data/bingusdingus.txt` with:

```text
T | 1 | valid todo
malformed record
D | 0 | valid deadline | 2019-10-15T00:00
E | 1 | valid event | 2019-10-15T09:00 | 2019-10-15T10:00
X | 0 | unknown type
T | 2 | invalid status
```

Inputs:

```text
list
bye
```

Expected output:

```text
        .-""""-.
       /  o  o  \
      |    ∆     |     BINGUS
      |  \___/   |     DINGUS
       \        /
        '-.__.-'
Hey there, I'm Bingus Dingusss.
How can I help ya?
Here are the tasks in your list:
1. [T][X] valid todo
2. [D][ ] valid deadline (by: Oct 15 2019)
3. [E][X] valid event (from: Oct 15 2019 9:00 AM to: Oct 15 2019 10:00 AM)
Goodbye!
```

## Test case 10: Undo the most recent state-changing command

Aim: Verify that undo restores a deleted task at its original position, restores completion state,
and cannot be used twice for the same command.

Setup: Start with no existing `data/bingusdingus.txt` file.

Inputs:

```text
todo first task
todo second task
delete 1
undo
mark 1
undo
undo
list
bye
```

Expected output:

```text
        .-""""-.
       /  o  o  \
      |    ∆     |     BINGUS
      |  \___/   |     DINGUS
       \        /
        '-.__.-'
Hey there, I'm Bingus Dingusss.
How can I help ya?
Got it. I've added this task:
  [T][ ] first task
Now you have 1 tasks in the list.
Got it. I've added this task:
  [T][ ] second task
Now you have 2 tasks in the list.
I've removed this task:
  first task
Now you have 1 tasks in the list.
I've undone your last command.
Nice! I've marked this task as done:
[T][X] first task
I've undone your last command.
There is nothing to undo.
Here are the tasks in your list:
1. [T][ ] first task
2. [T][ ] second task
Goodbye!
```

## Test case 11: Display the help guide

Aim: Verify that the help command displays the available commands and date/time formats.

Inputs:

```text
help
bye
```

Expected output:

```text
        .-""""-.
       /  o  o  \
      |    ∆     |     BINGUS
      |  \___/   |     DINGUS
       \        /
        '-.__.-'
Hey there, I'm Bingus Dingusss.
How can I help ya?
Commands:
todo <description>
deadline <description> /by <date or date-time>
event <description> /from <start> /to <end>
list | find | mark | unmark | delete | undo | bye

Date formats:
2026-09-15
15/9/2026
2026-09-15 1800
15/9/2026 18:00

Times use 24-hour format. Event end times must be later than start times.
Goodbye!
```
