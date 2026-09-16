# Bambolino UI Test Plan

Expected output in each test starts after Bambolino's startup greeting. The generated session record contains the commands sent to the program and the output it produced.

## Test: command aliases behave like full command names

**Aim:** Verify that short, case-insensitive aliases execute the same actions as the corresponding full commands.

**Inputs:**
```text
T borrow book
L
Q
```

**Expected output:**
```text
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
____________________________________________________________
Bye. Hope to see you again soon!
```

## Test: todo tasks display in the list

**Aim:** Verify that a to-do task is added and listed with its task type and incomplete status icon.

**Inputs:**
```text
todo borrow book
list
bye
```

**Expected output:**
```text
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
____________________________________________________________
Bye. Hope to see you again soon!
```

## Test: find searches task descriptions

**Aim:** Verify that find is case-insensitive, searches descriptions, and keeps the original task numbers.

**Inputs:**
```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from Aug 6th 2pm /to 4pm
find BOOK
bye
```

**Expected output:**
```text
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Jun 06 2019)
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
2.[D][ ] return book (by: Jun 06 2019)
____________________________________________________________
Bye. Hope to see you again soon!
```

## Test: deadline parses and formats an ISO date

**Aim:** Verify that a deadline stores an ISO date and displays it in the user-friendly format.

**Inputs:**
```text
deadline return book /by 2019-10-15
list
bye
```

**Expected output:**
```text
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Oct 15 2019)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] return book (by: Oct 15 2019)
____________________________________________________________
Bye. Hope to see you again soon!
```

## Test: deadline rejects an invalid date

**Aim:** Verify that a deadline date must use the yyyy-mm-dd format and be a real calendar date.

**Inputs:**
```text
deadline return book /by 2019-02-30
list
bye
```

**Expected output:**
```text
____________________________________________________________
Sorry, deadline dates must use yyyy-mm-dd. Try: deadline return book /by 2019-10-15
____________________________________________________________
No tasks added yet.
Bye. Hope to see you again soon!
```

## Test: delete removes a task and renumbers the list

**Aim:** Verify that deleting a task confirms the removed item and shifts later tasks to fill its number.

**Inputs:**
```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from Aug 6th 2pm /to 4pm
delete 2
list
bye
```

**Expected output:**
```text
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
____________________________________________________________
Bye. Hope to see you again soon!
```

## Test: mark and unmark retain the task type

**Aim:** Verify that marking and unmarking a to-do task changes only its completion status.

**Inputs:**
```text
todo read book
mark 1
unmark 1
bye
```

**Expected output:**
```text
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
Bye. Hope to see you again soon!
```

## Test: find matches task descriptions without case sensitivity

**Aim:** Verify that find displays only tasks whose descriptions contain the keyword, regardless of letter case.

**Inputs:**
```text
todo borrow book
deadline return book /by 2019-10-15
event team meeting /from Mon 2pm /to 4pm
find BOOK
bye
```

**Expected output:**
```text
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Oct 15 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] team meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Oct 15 2019)
____________________________________________________________
Bye. Hope to see you again soon!
```

## Test: find reports an empty result

**Aim:** Verify that find reports when no task description contains the keyword.

**Inputs:**
```text
todo borrow book
find notebook
bye
```

**Expected output:**
```text
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
No matching tasks found.
____________________________________________________________
Bye. Hope to see you again soon!
```

## Test: find requires a keyword

**Aim:** Verify that find explains how to provide a missing keyword.

**Inputs:**
```text
find
bye
```

**Expected output:**
```text
____________________________________________________________
Sorry, the find command needs a keyword. Try: find book
____________________________________________________________
Bye. Hope to see you again soon!
```

## Manual GUI checks for A-BetterGui

These visual checks supplement the console regression cases above.

- Submit `todo read book` with Enter and `list` with Send: commands appear on the right;
  responses appear in wider white cards without console separator lines or repeated avatars.
- Submit `todo`: the reply has an ERROR / WARNING label and contrasting red styling. The input
  remains selected for correction. Replace it with `todo read notes`: the reply uses normal styling.
- Resize from 680 × 520 to the minimum 380 × 360 and then enlarge: long commands and replies
  wrap within the conversation; the input and Send button remain usable without horizontal scrolling.
- Add enough tasks to require scrolling, then use `list`: the full response can be read by scrolling.
- Empty or whitespace-only input disables Send. Sending a valid command clears and refocuses the input.

## Test: repeated and missing parameters do not add tasks

**Aim:** Verify invalid commands report errors without changing tasks or ending the session.

**Inputs:**
```text
deadline book /by 2026-09-13 /by 2026-09-14
deadline book /by
event meeting /from 2pm /from 3pm /to 4pm
event meeting /from 2pm /to 3pm /to 4pm
event /from 2pm /to 4pm
event meeting /to 4pm /from 2pm
list
bye
```

**Expected output:**
```text
____________________________________________________________
Sorry, use /by exactly once, separated by spaces.
____________________________________________________________
____________________________________________________________
Sorry, a deadline needs both a description and a date after /by.
____________________________________________________________
____________________________________________________________
Sorry, use /from exactly once, separated by spaces.
____________________________________________________________
____________________________________________________________
Sorry, use /to exactly once, separated by spaces.
____________________________________________________________
____________________________________________________________
Sorry, an event needs a description, a start after /from, and an end after /to.
____________________________________________________________
____________________________________________________________
Sorry, use /to exactly once, separated by spaces.
____________________________________________________________
No tasks added yet.
Bye. Hope to see you again soon!
```

## Test: invalid exit and task numbers allow recovery

**Aim:** Verify invalid commands report errors without changing tasks or ending the session.

**Inputs:**
```text
bye now
q now
mark 99999999999999999999
delete 0
unmark -1
list
bye
```

**Expected output:**
```text
____________________________________________________________
Sorry, the bye command does not take any extra words.
____________________________________________________________
____________________________________________________________
Sorry, the bye command does not take any extra words.
____________________________________________________________
____________________________________________________________
Sorry, the task number for mark must be a whole number.
____________________________________________________________
____________________________________________________________
Sorry, task 0 is not in your list yet.
____________________________________________________________
____________________________________________________________
Sorry, task -1 is not in your list yet.
____________________________________________________________
No tasks added yet.
Bye. Hope to see you again soon!
```

## Test: flexible parameter whitespace

**Aim:** Verify leading and trailing whitespace and tabs around parameters are accepted.

**Inputs:**
```text
  deadline   book	/by	2026-09-13  
  event meeting	/from	2pm	/to	4pm  
bye
```

**Expected output:**
```text
____________________________________________________________
Got it. I've added this task:
  [D][ ] book (by: Sep 13 2026)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] meeting (from: 2pm to: 4pm)
Now you have 2 tasks in the list.
____________________________________________________________
Bye. Hope to see you again soon!
```

## Test: end of input exits cleanly

**Aim:** Verify the console exits without an exception when its input ends without bye.

**Inputs:**
```text
list
```

**Expected output:**
```text
No tasks added yet.
Bye. Hope to see you again soon!
```

Additional manual GUI checks for A-MoreErrorHandling:

- Enter `q`: Bambolino shows the same goodbye response as `bye`.
- Enter `bye now`: an error card appears and the command remains available for correction.
- Enter an event with repeated `/from` or `/to`: an error card appears; `list` confirms no task was added.

The whitespace test intentionally includes trailing spaces and literal tabs in its input.

## Automated JUnit coverage for A-MoreTesting

Run `./gradlew test` with Java 25. The tests in `src/test/java/bambolino` cover:

- Parser aliases, whitespace, unknown commands, and command normalization under Turkish locale.
- Task display, completion, keyword matching, list ordering, and immutable list snapshots.
- Storage round trips for every task type, Unicode and delimiters, corrupt records, missing files,
  and file locations that cannot be read or written as task files. Storage tests use temporary folders.
- Command lifecycles, persistence after mutations, search numbering, malformed inputs that leave
  tasks unchanged, and loading/saving error messages.
- Console greetings, errors, empty lists, trimmed input, and end-of-input handling.

The existing console cases remain applicable because the default storage location and command
behavior have not changed. GUI appearance remains covered by the manual checks above.
Cross-OS and screen-resolution checks have not been performed by the JUnit suite.

## Manual startup and naming checks

- Check that the window title, header, greeting, and response captions say Bambolino.
- Launch from a temporary working directory without a data file: the app starts with an empty list;
  adding a task creates `data/bambolino.txt`.
- With the app closed, use a temporary data file containing `bad` and `T|0|Ym9vaw==` on separate lines.
  Launch the GUI: a visible warning follows the greeting, and `list` displays the retained book task.
- In a temporary working directory, create a directory at `data/bambolino.txt` and launch the GUI:
  a visible loading warning appears and the interface remains usable.

The corrupt-record warning route is also covered by `BambolinoTest`.

## Loading-failure data protection

JUnit regression tests verify unreadable UTF-8 data and partially corrupt files remain byte-for-byte
unchanged after attempted saves. All six mutating commands are rejected after an incomplete load;
list and find still work. Repairing the file does not unlock the existing session; a fresh Storage
instance loads the repaired file and saves normally. A missing file still permits the first save.

Manual GUI check using a temporary working directory: start with a corrupt record and a valid task,
confirm the protection warning appears, then try `delete 1` and `todo new`. Both must display an
error, and `list` must retain the original task. Close, repair the file, and restart to resume editing.

## Cross-platform release smoke tests

The `Release smoke tests` GitHub Actions workflow runs the published v0.2 JAR on Windows
and Linux with plain Temurin Java 25. The artifact checksum must match the macOS-tested JAR.
Each runner starts in an empty temporary directory, enters commands through the GUI, checks
persisted task changes and invalid-date preservation, and restarts to verify loading.
Screenshots and Java logs are retained as workflow artifacts. This is automated smoke coverage;
it does not replace a person's assessment of usability on those operating systems.
