# HappyBot UI Test Plan

## Runner configuration

**Working directory:** `..`

### Build command

```sh
source /Users/tianle/.sdkman/bin/sdkman-init.sh && sdk use java 25.0.3.fx-zulu >/dev/null && rm -rf /private/tmp/happybot-ui-test-classes && mkdir -p /private/tmp/happybot-ui-test-classes && javac -d /private/tmp/happybot-ui-test-classes $(find src/main/java -name "*.java")
```

### Run command

```sh
source /Users/tianle/.sdkman/bin/sdkman-init.sh && sdk use java 25.0.3.fx-zulu >/dev/null && rm -f data/HappyBot.txt && java -cp /private/tmp/happybot-ui-test-classes happybot.HappyBot
```

### Test isolation

HappyBot loads `../../data/HappyBot.txt` on startup, so tasks saved by one test case
would otherwise be visible to the next one. The run command deletes that file
before each case so that every case starts from an empty task list.

### Checks that stay manual

Deleting the data file also puts the two startup notices out of reach, because
both need saved data that is already there and already broken. The runner has
one run command for every case and no per-case setup, so these two are checked
by hand from a scratch directory holding a `../../data/HappyBot.txt`:

- a file with unreadable lines, including an event whose end is not after its start, prints
  `Heads up! I skipped <n> unreadable line(s) in your saved data.`
- a file that cannot be read at all prints
  `Heads up! I could not read data/HappyBot.txt, so I am starting with an empty
  task list.`

Both notices appear between the welcome message and the first command, inside
their own pair of divider lines.

The data-file lock also needs a manual two-process check:

- start one HappyBot console session and leave it running;
- start a second session from the same working directory; and
- confirm that the second session reports that the task list is open in another HappyBot session,
  tells the user to close the other session and restart this session, and rejects task commands
  without crashing.

### Statistics checks that stay manual

The runner starts each case with an empty data file and cannot freeze the system date, so the
following current-week boundary and saved-data cases are checked manually:

- mark one task on Monday and another on Sunday, then confirm that both appear in the current
  week's completed counts;
- confirm that a task marked done before Monday is not included;
- restart HappyBot after marking a task done during the week and confirm that `stats` still
  counts it; and
- load a legacy completed line such as `T | 1 | old task` and confirm it remains completed but
  is not counted as completed this week.

The time-sensitive expiry boundaries also need a manual check because their expected result
depends on the clock when HappyBot runs:

- a date-only deadline and date-only event ending today are accepted;
- an explicitly timed deadline or event end earlier today is rejected; and
- an explicitly timed deadline or event end later today is accepted.

### Graphical farewell checks that stay manual

The console runner cannot check JavaFX rendering or window timing. Launch the graphical app
and check these cases, restarting it after each valid exit:

- enter `bye` using Enter, then repeat using Send: the chat shows
  `Bye. Hope to see you again soon!` for approximately three seconds before the window closes;
- confirm that the input field and Send button are disabled during the farewell pause;
- enter `  bye  ` and confirm that it follows the same farewell sequence;
- enter `bye now` and confirm that `Oops! Use: bye.` appears, the window remains open, and
  commands can still be entered; and
- after enough commands to fill the chat, enter `bye` and confirm that the farewell scrolls
  into view before the window closes.

## Test cases

### TC-01: Exit the program

**Aim:** Confirm that HappyBot displays its welcome and farewell messages when the user exits.

#### Inputs

```text
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-02: Display an empty task list

**Aim:** Confirm that listing an empty task list displays no numbered task entries.

#### Inputs

```text
list
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-03: Reject a todo without a description

**Aim:** Confirm that HappyBot rejects a todo command with no task description.

#### Inputs

```text
todo
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Oops! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-04: Add a todo with a description

**Aim:** Confirm that HappyBot adds and displays a todo with its task-type marker.

#### Inputs

```text
todo read book
list
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-05: Mark and unmark a task

**Aim:** Confirm that marking and unmarking a task changes the status shown in the task list.

#### Inputs

```text
todo read book
mark 1
unmark 1
list
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
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
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-06: Add deadline and event tasks

**Aim:** Confirm that HappyBot accepts yyyy-MM-dd dates and displays them as MMM dd yyyy with the time.

#### Inputs

```text
deadline return book /by 2096-10-15
event project meeting /from 2096-08-06 /to 2096-08-07
list
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Oct 15 2096 12:00AM)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Aug 06 2096 12:00AM to: Aug 07 2096 12:00AM)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Oct 15 2096 12:00AM)
 2.[E][ ] project meeting (from: Aug 06 2096 12:00AM to: Aug 07 2096 12:00AM)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-07: Reject deadlines with missing fields

**Aim:** Confirm that HappyBot rejects deadlines with a missing description or due date.

#### Inputs

```text
deadline /by Sunday
deadline  /by Sunday
deadline return book /by
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Oops! Use: deadline <description> /by <yyyy-MM-dd>.
____________________________________________________________
____________________________________________________________
 Oops! Use: deadline <description> /by <yyyy-MM-dd>.
____________________________________________________________
____________________________________________________________
 Oops! Use: deadline <description> /by <yyyy-MM-dd>.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-08: Reject events with missing fields

**Aim:** Confirm that HappyBot rejects events with a missing description, start, or end.

#### Inputs

```text
event /from Mon /to Tue
event meeting /from /to Tue
event meeting /from Mon
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Oops! Use: event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>.
____________________________________________________________
____________________________________________________________
 Oops! Use: event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>.
____________________________________________________________
____________________________________________________________
 Oops! Use: event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-09: Handle task-number and unknown-command errors

**Aim:** Confirm that HappyBot reports task-number and unknown-command errors and continues accepting commands.

#### Inputs

```text
mark 1
unmark 1
todo read book
mark abc
unmark 2
blah
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Oops! There are no tasks to mark.
____________________________________________________________
____________________________________________________________
 Oops! There are no tasks to unmark.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Oops! Please provide one positive whole task number.
____________________________________________________________
____________________________________________________________
 Oops! Please choose a valid task number.
____________________________________________________________
____________________________________________________________
 Oops! I don't know what that means :-(
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-10: Delete a task and renumber the list

**Aim:** Confirm that HappyBot removes the selected task, reports the new task count, and renumbers the remaining tasks.

#### Inputs

```text
todo read book
deadline return book /by 2096-10-15
event project meeting /from 2096-08-06 /to 2096-08-07
delete 2
list
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Oct 15 2096 12:00AM)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Aug 06 2096 12:00AM to: Aug 07 2096 12:00AM)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Alrighties I've removed this task:
   [D][ ] return book (by: Oct 15 2096 12:00AM)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[E][ ] project meeting (from: Aug 06 2096 12:00AM to: Aug 07 2096 12:00AM)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-11: Reject invalid delete commands

**Aim:** Confirm that HappyBot rejects deletion from an empty list and non-positive, non-numeric,
or out-of-range task numbers.

#### Inputs

```text
delete 1
todo read book
delete abc
delete 0
delete 2
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Oops! There are no tasks to delete.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Oops! Please provide one positive whole task number.
____________________________________________________________
____________________________________________________________
 Oops! Please provide one positive whole task number.
____________________________________________________________
____________________________________________________________
 Oops! Please choose a valid task number.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-12: Reject task text containing the data-file separator

**Aim:** Confirm that HappyBot rejects task text containing '|', which the data file uses to separate fields.

#### Inputs

```text
todo read | book
deadline return | book /by Sunday
event project | meeting /from Mon /to Tue
todo read book
list
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Oops! A task cannot contain the '|' character.
____________________________________________________________
____________________________________________________________
 Oops! A task cannot contain the '|' character.
____________________________________________________________
____________________________________________________________
 Oops! A task cannot contain the '|' character.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-13: End the session when the input ends

**Aim:** Confirm that HappyBot exits with its farewell message when the input ends without a bye command.

#### Inputs

```text
todo read book
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-14: Reject dates that are not in the accepted pattern

**Aim:** Confirm that HappyBot rejects dates that match none of the accepted date patterns.

#### Inputs

```text
deadline return book /by Sunday
event project meeting /from Mon /to Tue
deadline return book /by 2019-13-45
list
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Oops! Please write dates as yyyy-MM-dd or d/M/yyyy, with an optional 24-hour time that defaults to 0000, such as 2019-12-02 1800.
____________________________________________________________
____________________________________________________________
 Oops! Please write dates as yyyy-MM-dd or d/M/yyyy, with an optional 24-hour time that defaults to 0000, such as 2019-12-02 1800.
____________________________________________________________
____________________________________________________________
 Oops! Please write dates as yyyy-MM-dd or d/M/yyyy, with an optional 24-hour time that defaults to 0000, such as 2019-12-02 1800.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-15: Accept an optional time and both date patterns

**Aim:** Confirm that HappyBot reads a 24-hour time after a date, accepts the d/M/yyyy pattern, and defaults a missing time to 0000.

#### Inputs

```text
deadline return book /by 2/12/2096 1800
deadline pay fees /by 2096-10-15
event orientation /from 2096-12-01 0900 /to 2096-12-03 1700
list
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 02 2096 6:00PM)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] pay fees (by: Oct 15 2096 12:00AM)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] orientation (from: Dec 01 2096 9:00AM to: Dec 03 2096 5:00PM)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Dec 02 2096 6:00PM)
 2.[D][ ] pay fees (by: Oct 15 2096 12:00AM)
 3.[E][ ] orientation (from: Dec 01 2096 9:00AM to: Dec 03 2096 5:00PM)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-16: Reject an event that does not start before it ends

**Aim:** Confirm that HappyBot rejects an event whose start is after or equal to its end, and accepts one that starts earlier on the same day.

#### Inputs

```text
event trip /from 2019-08-08 /to 2019-08-06
event trip /from 2019-08-06 /to 2019-08-06
event trip /from 2096-08-06 0900 /to 2096-08-06 1700
list
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Oops! An event must start before it ends.
____________________________________________________________
____________________________________________________________
 Oops! An event must start before it ends.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] trip (from: Aug 06 2096 9:00AM to: Aug 06 2096 5:00PM)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[E][ ] trip (from: Aug 06 2096 9:00AM to: Aug 06 2096 5:00PM)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-17: List the deadlines due on one date

**Aim:** Confirm that the due command shows only deadlines due on the given date, leaves out todos and events, keeps each task's number from the full list, and accepts either date pattern.

#### Inputs

```text
todo read book
deadline return book /by 2096-12-02 1800
event orientation /from 2096-12-01 /to 2096-12-03
deadline pay fees /by 2096-12-02
due 2096-12-02
due 2/12/2096
due 2096-12-05
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 02 2096 6:00PM)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] orientation (from: Dec 01 2096 12:00AM to: Dec 03 2096 12:00AM)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] pay fees (by: Dec 02 2096 12:00AM)
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the deadlines due on Dec 02 2096:
 2.[D][ ] return book (by: Dec 02 2096 6:00PM)
 4.[D][ ] pay fees (by: Dec 02 2096 12:00AM)
____________________________________________________________
____________________________________________________________
 Here are the deadlines due on Dec 02 2096:
 2.[D][ ] return book (by: Dec 02 2096 6:00PM)
 4.[D][ ] pay fees (by: Dec 02 2096 12:00AM)
____________________________________________________________
____________________________________________________________
 There are no deadlines due on Dec 05 2096.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-18: Reject invalid due commands

**Aim:** Confirm that HappyBot rejects a due command with no date, an unreadable date, or an
unnecessary time argument.

#### Inputs

```text
due
due someday
due 2019-12-02 1800
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Oops! Use: due <date>, such as due 2019-12-02.
____________________________________________________________
____________________________________________________________
 Oops! Please write dates as yyyy-MM-dd or d/M/yyyy, with an optional 24-hour time that defaults to 0000, such as 2019-12-02 1800.
____________________________________________________________
____________________________________________________________
 Oops! Use: due <date>, such as due 2019-12-02.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-19: Accept extra spaces around a date and time

**Aim:** Confirm that HappyBot accepts more than one space between a date and its time, and spaces around the date itself.

#### Inputs

```text
deadline return book /by 2096-12-02    1800
deadline pay fees /by   2/12/2096 1800
event orientation /from  2096-12-01   0900 /to 2096-12-03    1700
list
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 02 2096 6:00PM)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] pay fees (by: Dec 02 2096 6:00PM)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] orientation (from: Dec 01 2096 9:00AM to: Dec 03 2096 5:00PM)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Dec 02 2096 6:00PM)
 2.[D][ ] pay fees (by: Dec 02 2096 6:00PM)
 3.[E][ ] orientation (from: Dec 01 2096 9:00AM to: Dec 03 2096 5:00PM)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-20: Trim spaces around a task description

**Aim:** Confirm that HappyBot removes the spaces before and after a task description, so that
the stored and displayed description holds neither.

#### Inputs

```text
todo    read book
deadline return book    /by 2096-12-02
event   orientation    /from 2096-12-01 /to 2096-12-03
list
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 02 2096 12:00AM)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] orientation (from: Dec 01 2096 12:00AM to: Dec 03 2096 12:00AM)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Dec 02 2096 12:00AM)
 3.[E][ ] orientation (from: Dec 01 2096 12:00AM to: Dec 03 2096 12:00AM)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-21: Reject a date that names no day on the calendar

**Aim:** Confirm that HappyBot refuses a correctly written date that no month holds, such as
31 November or 29 February in a year that is not a leap year, instead of moving it to the last
day of the month, and that it still accepts a real leap day.

#### Inputs

```text
deadline return book /by 2019-11-31
deadline pay fees /by 2019-02-30
deadline submit form /by 31/11/2019
due 2019-02-29
deadline leap task /by 2096-02-29
list
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Oops! There is no such date on the calendar. Please enter a valid date, checking the number of days the month has.
____________________________________________________________
____________________________________________________________
 Oops! There is no such date on the calendar. Please enter a valid date, checking the number of days the month has.
____________________________________________________________
____________________________________________________________
 Oops! There is no such date on the calendar. Please enter a valid date, checking the number of days the month has.
____________________________________________________________
____________________________________________________________
 Oops! There is no such date on the calendar. Please enter a valid date, checking the number of days the month has.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] leap task (by: Feb 29 2096 12:00AM)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] leap task (by: Feb 29 2096 12:00AM)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-22: Find tasks by a keyword in the description

**Aim:** Confirm that the find command shows every task whose description holds the keyword
whatever its capitalization, keeps each task's number from the full list, reports when nothing
matches, and rejects a find command with no keyword.

#### Inputs

```text
todo read book
deadline return book /by 2096-06-06
event orientation /from 2096-12-01 /to 2096-12-03
mark 1
mark 2
find book
find BOOK
find orientation
find pizza
find
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Jun 06 2096 12:00AM)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] orientation (from: Dec 01 2096 12:00AM to: Dec 03 2096 12:00AM)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Jun 06 2096 12:00AM)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return book (by: Jun 06 2096 12:00AM)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return book (by: Jun 06 2096 12:00AM)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 3.[E][ ] orientation (from: Dec 01 2096 12:00AM to: Dec 03 2096 12:00AM)
____________________________________________________________
____________________________________________________________
 There are no tasks matching "pizza".
____________________________________________________________
____________________________________________________________
 Oops! Use: find <keyword>, such as find book.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-23: Display current-week statistics

**Aim:** Confirm that stats counts current-session completions by task type and reports no
uncompleted deadline when every deadline in the list is completed.

#### Inputs

```text
todo read book
deadline return book /by 2096-12-02
event orientation /from 2096-12-01 /to 2096-12-03
mark 1
mark 2
mark 3
stats
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 02 2096 12:00AM)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] orientation (from: Dec 01 2096 12:00AM to: Dec 03 2096 12:00AM)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Dec 02 2096 12:00AM)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [E][X] orientation (from: Dec 01 2096 12:00AM to: Dec 03 2096 12:00AM)
____________________________________________________________
____________________________________________________________
 Here are your statistics for this week:
 Total tasks: 3
 Completed this week: 3
   To-dos: 1
   Deadlines: 1
   Events: 1
 Uncompleted deadlines due this week: 0
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-24: Reject invalid statistics commands

**Aim:** Confirm that stats accepts no arguments and reports a usage error when an argument is
provided.

#### Inputs

```text
stats
stats week
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Here are your statistics for this week:
 Total tasks: 0
 Completed this week: 0
   To-dos: 0
   Deadlines: 0
   Events: 0
 Uncompleted deadlines due this week: 0
____________________________________________________________
____________________________________________________________
 Oops! Use: stats.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-25: Reject duplicate tasks and unexpected command arguments

**Aim:** Confirm that HappyBot normalizes accidental whitespace, rejects duplicate task details,
and does not exit or change the list when commands have unexpected arguments.

#### Inputs

```text
  todo   read  book
todo READ book
list now
bye now
list
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Oops! That task is already in your list.
____________________________________________________________
____________________________________________________________
 Oops! Use: list.
____________________________________________________________
____________________________________________________________
 Oops! Use: bye.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-26: Reject expired deadline and event tasks

**Aim:** Confirm that HappyBot rejects a deadline that is already due and an event that has
already ended, without adding either task to the list.

#### Inputs

```text
deadline old task /by 2000-01-01
event old meeting /from 2000-01-01 /to 2000-01-02
list
bye
```

#### Expected output

```text
____________________________________________________________
H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
H   H  A   A  P      P         Y    B   B  O   O    T
H   H  A   A  P      P         Y    BBBB    OOO     T
Hello! I'm HappyBot.
How can I cheer you up today?
____________________________________________________________
____________________________________________________________
 Oops! A deadline cannot be due in the past.
____________________________________________________________
____________________________________________________________
 Oops! An event cannot end in the past.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
