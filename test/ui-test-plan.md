# UI Test Plan

## Test settings

- Run commands from the repository root.
- Use Java 25.
- Natural-date acceptance tests use `cheecken.FixedClockCli` with a fixed
  clock of Monday, 7 September 2026 at 15:30 UTC. It runs the same console
  loop as the application. Parser unit tests also cover local timezone,
  leap-year, midnight, month/year boundaries, and unsupported expressions.
- In expected-output blocks, `\033` represents the actual ANSI escape
  character (U+001B), not four literal characters. Decode only this notation
  before comparing; preserve the ASCII-art backslashes literally.
- Ignore trailing ASCII spaces on the first six stdout lines (the decorative
  welcome banner), on both actual and expected output. Leading spaces and
  spaces within those lines remain significant.
- Compare all remaining stdout exactly, including ANSI formatting sequences,
  whitespace, blank lines, and the final newline. Require empty stderr and
  exit code 0 unless a case explicitly specifies otherwise.
- Compile with `./gradlew testClasses` so the fixed-clock console fixture is available.
- Run each console case in a fresh temporary working directory, using an
  absolute classpath to `build/classes/java/main` and `build/classes/java/test`. Never use real task data.
- Production uses `cheecken.Cheecken`; acceptance uses `cheecken.FixedClockCli`; the GUI uses
  `cheecken.Launcher`.

## JavaFX acceptance test

- **Aim:** Exercise the real chat window and preserve the existing task commands.
- **Command:** `./gradlew guiTest` (Java 25 and a graphical desktop required).
- **Setup:** The test supplies its own temporary persistence file. It does not
  read or modify `data/cheecken.txt` in the project.
- **Input/actions, in order:**
  1. Open the window: greeting visible; empty input disables Send.
  2. Submit spaces: no message is added.
  3. Enter `todo GUI test task`: one user message and an added-task reply;
     input clears.
  4. Click Send for `mark 1`: reply includes `[T][X] GUI test task`.
  5. Add a deadline and event using `15/10/2026 1800` and
     `/from 15/10/2026 0900 /to 15/10/2026 1000`: dates display in 12-hour format.
     With the fixed Monday clock, also enter `deadline natural /by tomorrow`
     and `event weekday /from Mon 0900 /to MONDAY 1000`. Expect Sep 08 2026
     without a time and Sep 14 2026 at 9:00 AM–10:00 AM respectively.
     Reject `deadline invalid /by next week` without adding a task.
  6. Enter `wat`, then `find GUI`: errors (including the invalid deadline above)
     appear in red (#b42318), bold italic text. The next successful reply uses
     the normal text style, and user messages retain their normal style.
     Storage load/save warnings also use the error style.
  7. Expand Command guide: command examples and date format are visible.
     Natural-date help includes today, tomorrow, now, and the strictly-next weekday rule.
  8. Submit a long task and repeated `list` commands; resize to 440 × 560:
     messages wrap, the composer remains usable, and the conversation scrolls
     to the latest response.
  9. Enter `bye`: the window closes immediately.
  10. Open a new window using the same temporary file and enter `list`:
      the first task is still marked complete. Submit `bye` using Send and
      verify that this also closes the window.
      Use a clock one day later and verify the natural deadline still displays
      Sep 08 2026 without midnight.
- **Expected result:** All assertions pass. A rendered scene snapshot is saved
  to the Java temporary directory as `cheecken-gui.png` for visual review.
  JavaFX may issue a Java 25 native-access warning on stderr; this is not a
  command response or a failed assertion.

## Console acceptance cases

The obsolete error expectations in cases 3 and 4 are corrected with user approval.
Each complete expected stdout below is authored from the agreed behavior. Do not
replace it with captured output when a test fails. Stop on the first failure.

All console cases use the fixed Monday clock above. Before running a case, compile
with `./gradlew testClasses`, set `CLASS_DIR` to the absolute main and test class
folders joined with `:`, and enter a fresh temporary directory. Feed the exact
Input block to the Command, then close stdin. Expected stderr is empty and the
exit code is 0. An Initial storage block is written to `data/cheecken.txt` in that
temporary directory before launch; otherwise begin without a task file. Expected
storage blocks are exact file contents, including the final newline. Case 19
runs a second process in the same temporary directory to verify reloading.

### 1. Exit with bye

- **Aim:** Display the welcome and farewell, then exit.
- **Command:** `java -ea -cp "$CLASS_DIR" cheecken.FixedClockCli`
- **Input:**

```text
bye
```

- **Expected output:**

```text
 _____ _                    _
/  __ \ |                  | |
| /  \/ |__   ___  ___  ___| | _____ _ __
| |   | '_ \ / _ \/ _ \/ __| |/ / _ \ '_ \
| \__/\ | | |  __/  __/ (__|   <  __/ | | |
 \____/_| |_|\___|\___|\___|_|\_\___|_| |_|
____________________________________________________________
Hello! I'm \033[3mCHEECKEN\033[0m.
What can I do for you?
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### 2. Reject an invalid index

- **Aim:** Leave the existing task unmarked.
- **Command:** `java -ea -cp "$CLASS_DIR" cheecken.FixedClockCli`
- **Input:**

```text
todo Read book
mark 2
list
bye
```

- **Expected output:**

```text
 _____ _                    _
/  __ \ |                  | |
| /  \/ |__   ___  ___  ___| | _____ _ __
| |   | '_ \ / _ \/ _ \/ __| |/ / _ \ '_ \
| \__/\ | | |  __/  __/ (__|   <  __/ | | |
 \____/_| |_|\___|\___|\___|_|\_\___|_| |_|
____________________________________________________________
Hello! I'm \033[3mCHEECKEN\033[0m.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] Read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Task index out of range
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] Read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### 3. Reject an event missing /to

- **Aim:** Reject incomplete event input without adding a task.
- **Command:** `java -ea -cp "$CLASS_DIR" cheecken.FixedClockCli`
- **Input:**

```text
todo Keep baseline
event Broken /from 15/10/2026 0900
list
bye
```

- **Expected output:**

```text
 _____ _                    _
/  __ \ |                  | |
| /  \/ |__   ___  ___  ___| | _____ _ __
| |   | '_ \ / _ \/ _ \/ __| |/ / _ \ '_ \
| \__/\ | | |  __/  __/ (__|   <  __/ | | |
 \____/_| |_|\___|\___|\___|_|\_\___|_| |_|
____________________________________________________________
Hello! I'm \033[3mCHEECKEN\033[0m.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] Keep baseline
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
No time how I set the task...
Try: event meeting /from today 0900 /to tomorrow 1000.
Dates: d/M/yyyy, today, tomorrow, Mon–Sun (or full names); optional HHmm. Or now.
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] Keep baseline
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### 4. Reject a deadline missing /by

- **Aim:** Reject incomplete deadline input without adding a task.
- **Command:** `java -ea -cp "$CLASS_DIR" cheecken.FixedClockCli`
- **Input:**

```text
todo Keep baseline
deadline Broken
list
bye
```

- **Expected output:**

```text
 _____ _                    _
/  __ \ |                  | |
| /  \/ |__   ___  ___  ___| | _____ _ __
| |   | '_ \ / _ \/ _ \/ __| |/ / _ \ '_ \
| \__/\ | | |  __/  __/ (__|   <  __/ | | |
 \____/_| |_|\___|\___|\___|_|\_\___|_| |_|
____________________________________________________________
Hello! I'm \033[3mCHEECKEN\033[0m.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] Keep baseline
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
No time how I set the task...
Try: deadline report /by tomorrow 1800.
Dates: d/M/yyyy, today, tomorrow, Mon–Sun (or full names); optional HHmm. Or now.
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] Keep baseline
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### 5. Reject unknown input

- **Aim:** Preserve the completion state after an unknown command.
- **Command:** `java -ea -cp "$CLASS_DIR" cheecken.FixedClockCli`
- **Input:**

```text
todo Keep state
mark 1
wat
list
bye
```

- **Expected output:**

```text
 _____ _                    _
/  __ \ |                  | |
| /  \/ |__   ___  ___  ___| | _____ _ __
| |   | '_ \ / _ \/ _ \/ __| |/ / _ \ '_ \
| \__/\ | | |  __/  __/ (__|   <  __/ | | |
 \____/_| |_|\___|\___|\___|_|\_\___|_| |_|
____________________________________________________________
Hello! I'm \033[3mCHEECKEN\033[0m.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] Keep state
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] Keep state
____________________________________________________________
____________________________________________________________
What do you want? I only understand sentences starting with `todo`, `event` and `deadline.`
(e.g. `event her wedding /from later /to forever`)
____________________________________________________________
Here are the tasks in your list:
1.[T][X] Keep state
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### 6. Reject command-name prefixes

- **Aim:** Do not interpret marker as mark.
- **Command:** `java -ea -cp "$CLASS_DIR" cheecken.FixedClockCli`
- **Input:**

```text
todo Keep boundary
marker 1
list
bye
```

- **Expected output:**

```text
 _____ _                    _
/  __ \ |                  | |
| /  \/ |__   ___  ___  ___| | _____ _ __
| |   | '_ \ / _ \/ _ \/ __| |/ / _ \ '_ \
| \__/\ | | |  __/  __/ (__|   <  __/ | | |
 \____/_| |_|\___|\___|\___|_|\_\___|_| |_|
____________________________________________________________
Hello! I'm \033[3mCHEECKEN\033[0m.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] Keep boundary
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
What do you want? I only understand sentences starting with `todo`, `event` and `deadline.`
(e.g. `event her wedding /from later /to forever`)
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] Keep boundary
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### 7. Persist state changes

- **Aim:** Persist adding, marking, and unmarking.
- **Command:** `java -ea -cp "$CLASS_DIR" cheecken.FixedClockCli`
- **Input:**

```text
todo read book
mark 1
unmark 1
bye
```

- **Expected output:**

```text
 _____ _                    _
/  __ \ |                  | |
| /  \/ |__   ___  ___  ___| | _____ _ __
| |   | '_ \ / _ \/ _ \/ __| |/ / _ \ '_ \
| \__/\ | | |  __/  __/ (__|   <  __/ | | |
 \____/_| |_|\___|\___|\___|_|\_\___|_| |_|
____________________________________________________________
Hello! I'm \033[3mCHEECKEN\033[0m.
What can I do for you?
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
Bye. Hope to see you again soon!
____________________________________________________________
```

- **Expected storage:**

```text
T | 0 | read book
```

### 8. Reject an empty event

- **Aim:** Report an empty description before checking dates.
- **Command:** `java -ea -cp "$CLASS_DIR" cheecken.FixedClockCli`
- **Input:**

```text
event /from /to
```

- **Expected output:**

```text
 _____ _                    _
/  __ \ |                  | |
| /  \/ |__   ___  ___  ___| | _____ _ __
| |   | '_ \ / _ \/ _ \/ __| |/ / _ \ '_ \
| \__/\ | | |  __/  __/ (__|   <  __/ | | |
 \____/_| |_|\___|\___|\___|_|\_\___|_| |_|
____________________________________________________________
Hello! I'm \033[3mCHEECKEN\033[0m.
What can I do for you?
____________________________________________________________
____________________________________________________________
There's no task that is empty. LOCK INNN!
(e.g. event love me /from 15/10/2025 0900 /to 15/10/3000 1100)
```

### 9. Find tasks by keyword

- **Aim:** Find matching descriptions in insertion order.
- **Command:** `java -ea -cp "$CLASS_DIR" cheecken.FixedClockCli`
- **Input:**

```text
todo read book
deadline return book /by 15/10/2025
find book
bye
```

- **Expected output:**

```text
 _____ _                    _
/  __ \ |                  | |
| /  \/ |__   ___  ___  ___| | _____ _ __
| |   | '_ \ / _ \/ _ \/ __| |/ / _ \ '_ \
| \__/\ | | |  __/  __/ (__|   <  __/ | | |
 \____/_| |_|\___|\___|\___|_|\_\___|_| |_|
____________________________________________________________
Hello! I'm \033[3mCHEECKEN\033[0m.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Oct 15 2025)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Oct 15 2025)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### 10. Case-insensitive keyword search

- **Aim:** Keep existing case-insensitive search behavior.
- **Command:** `java -ea -cp "$CLASS_DIR" cheecken.FixedClockCli`
- **Input:**

```text
todo Read Book
find book
bye
```

- **Expected output:**

```text
 _____ _                    _
/  __ \ |                  | |
| /  \/ |__   ___  ___  ___| | _____ _ __
| |   | '_ \ / _ \/ _ \/ __| |/ / _ \ '_ \
| \__/\ | | |  __/  __/ (__|   <  __/ | | |
 \____/_| |_|\___|\___|\___|_|\_\___|_| |_|
____________________________________________________________
Hello! I'm \033[3mCHEECKEN\033[0m.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] Read Book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] Read Book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### 11. Find without matches

- **Aim:** Report that no tasks match.
- **Command:** `java -ea -cp "$CLASS_DIR" cheecken.FixedClockCli`
- **Input:**

```text
todo buy flowers
find book
bye
```

- **Expected output:**

```text
 _____ _                    _
/  __ \ |                  | |
| /  \/ |__   ___  ___  ___| | _____ _ __
| |   | '_ \ / _ \/ _ \/ __| |/ / _ \ '_ \
| \__/\ | | |  __/  __/ (__|   <  __/ | | |
 \____/_| |_|\___|\___|\___|_|\_\___|_| |_|
____________________________________________________________
Hello! I'm \033[3mCHEECKEN\033[0m.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] buy flowers
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
There are no matching tasks in your list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### 12. Find without a keyword

- **Aim:** Report a missing search keyword.
- **Command:** `java -ea -cp "$CLASS_DIR" cheecken.FixedClockCli`
- **Input:**

```text
todo read book
find
bye
```

- **Expected output:**

```text
 _____ _                    _
/  __ \ |                  | |
| /  \/ |__   ___  ___  ___| | _____ _ __
| |   | '_ \ / _ \/ _ \/ __| |/ / _ \ '_ \
| \__/\ | | |  __/  __/ (__|   <  __/ | | |
 \____/_| |_|\___|\___|\___|_|\_\___|_| |_|
____________________________________________________________
Hello! I'm \033[3mCHEECKEN\033[0m.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Please provide a keyword to search for.
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### 13. Event without dates

- **Aim:** Give a concise supported-date example.
- **Command:** `java -ea -cp "$CLASS_DIR" cheecken.FixedClockCli`
- **Input:**

```text
event love me
```

- **Expected output:**

```text
 _____ _                    _
/  __ \ |                  | |
| /  \/ |__   ___  ___  ___| | _____ _ __
| |   | '_ \ / _ \/ _ \/ __| |/ / _ \ '_ \
| \__/\ | | |  __/  __/ (__|   <  __/ | | |
 \____/_| |_|\___|\___|\___|_|\_\___|_| |_|
____________________________________________________________
Hello! I'm \033[3mCHEECKEN\033[0m.
What can I do for you?
____________________________________________________________
____________________________________________________________
No time how I set the task...
Try: event meeting /from today 0900 /to tomorrow 1000.
Dates: d/M/yyyy, today, tomorrow, Mon–Sun (or full names); optional HHmm. Or now.
```

### 14. Existing event datetime format

- **Aim:** Preserve numeric dates and 12-hour display.
- **Command:** `java -ea -cp "$CLASS_DIR" cheecken.FixedClockCli`
- **Input:**

```text
event love me /from 15/10/2025 0900 /to 15/10/3000 1100
```

- **Expected output:**

```text
 _____ _                    _
/  __ \ |                  | |
| /  \/ |__   ___  ___  ___| | _____ _ __
| |   | '_ \ / _ \/ _ \/ __| |/ / _ \ '_ \
| \__/\ | | |  __/  __/ (__|   <  __/ | | |
 \____/_| |_|\___|\___|\___|_|\_\___|_| |_|
____________________________________________________________
Hello! I'm \033[3mCHEECKEN\033[0m.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] love me (from: Oct 15 2025 9:00 AM to: Oct 15 3000 11:00 AM)
Now you have 1 tasks in the list.
____________________________________________________________
```

### 15. Natural deadlines

- **Aim:** Resolve tomorrow, a past time today, and now with the fixed Monday clock.
- **Command:** `java -ea -cp "$CLASS_DIR" cheecken.FixedClockCli`
- **Input:**

```text
deadline report /by ToMoRrOw
deadline past /by today 0900
deadline instant /by NOW
list
bye
```

- **Expected output:**

```text
 _____ _                    _
/  __ \ |                  | |
| /  \/ |__   ___  ___  ___| | _____ _ __
| |   | '_ \ / _ \/ _ \/ __| |/ / _ \ '_ \
| \__/\ | | |  __/  __/ (__|   <  __/ | | |
 \____/_| |_|\___|\___|\___|_|\_\___|_| |_|
____________________________________________________________
Hello! I'm \033[3mCHEECKEN\033[0m.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] report (by: Sep 08 2026)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] past (by: Sep 07 2026 9:00 AM)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] instant (by: Sep 07 2026 3:30 PM)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] report (by: Sep 08 2026)
2.[D][ ] past (by: Sep 07 2026 9:00 AM)
3.[D][ ] instant (by: Sep 07 2026 3:30 PM)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

- **Expected storage:**

```text
D | 0 | report | 2026-09-08
D | 0 | past | 2026-09-07T09:00
D | 0 | instant | 2026-09-07T15:30
```

### 16. Weekdays and independent event dates

- **Aim:** Move Monday seven days ahead; retain independent endpoints and reversed events.
- **Command:** `java -ea -cp "$CLASS_DIR" cheecken.FixedClockCli`
- **Input:**

```text
event meeting /from Mon 0900 /to MONDAY 1000
event reverse /from tomorrow /to today
bye
```

- **Expected output:**

```text
 _____ _                    _
/  __ \ |                  | |
| /  \/ |__   ___  ___  ___| | _____ _ __
| |   | '_ \ / _ \/ _ \/ __| |/ / _ \ '_ \
| \__/\ | | |  __/  __/ (__|   <  __/ | | |
 \____/_| |_|\___|\___|\___|_|\_\___|_| |_|
____________________________________________________________
Hello! I'm \033[3mCHEECKEN\033[0m.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] meeting (from: Sep 14 2026 9:00 AM to: Sep 14 2026 10:00 AM)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] reverse (from: Sep 08 2026 to: Sep 07 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### 17. Reject unsupported natural expressions

- **Aim:** Leave saved state unchanged for invalid phrases, times, and now suffixes.
- **Command:** `java -ea -cp "$CLASS_DIR" cheecken.FixedClockCli`
- **Input:**

```text
todo keep
deadline bad /by next Monday
deadline bad /by today 2400
deadline bad /by now 0900
event bad /from today /to yesterday
list
bye
```

- **Expected output:**

```text
 _____ _                    _
/  __ \ |                  | |
| /  \/ |__   ___  ___  ___| | _____ _ __
| |   | '_ \ / _ \/ _ \/ __| |/ / _ \ '_ \
| \__/\ | | |  __/  __/ (__|   <  __/ | | |
 \____/_| |_|\___|\___|\___|_|\_\___|_| |_|
____________________________________________________________
Hello! I'm \033[3mCHEECKEN\033[0m.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] keep
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
No time how I set the task...
Try: deadline report /by tomorrow 1800.
Dates: d/M/yyyy, today, tomorrow, Mon–Sun (or full names); optional HHmm. Or now.
____________________________________________________________
No time how I set the task...
Try: deadline report /by tomorrow 1800.
Dates: d/M/yyyy, today, tomorrow, Mon–Sun (or full names); optional HHmm. Or now.
____________________________________________________________
No time how I set the task...
Try: deadline report /by tomorrow 1800.
Dates: d/M/yyyy, today, tomorrow, Mon–Sun (or full names); optional HHmm. Or now.
____________________________________________________________
No time how I set the task...
Try: event meeting /from today 0900 /to tomorrow 1000.
Dates: d/M/yyyy, today, tomorrow, Mon–Sun (or full names); optional HHmm. Or now.
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] keep
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

- **Expected storage:**

```text
T | 0 | keep
```

### 18. Retain lowercase exact flags

- **Aim:** Reject uppercase flags and longer flag-like words.
- **Command:** `java -ea -cp "$CLASS_DIR" cheecken.FixedClockCli`
- **Input:**

```text
deadline bad /BY tomorrow
deadline bad /byx today
event bad /FROM today /to tomorrow
event bad /from today /TO tomorrow
list
bye
```

- **Expected output:**

```text
 _____ _                    _
/  __ \ |                  | |
| /  \/ |__   ___  ___  ___| | _____ _ __
| |   | '_ \ / _ \/ _ \/ __| |/ / _ \ '_ \
| \__/\ | | |  __/  __/ (__|   <  __/ | | |
 \____/_| |_|\___|\___|\___|_|\_\___|_| |_|
____________________________________________________________
Hello! I'm \033[3mCHEECKEN\033[0m.
What can I do for you?
____________________________________________________________
____________________________________________________________
No time how I set the task...
Try: deadline report /by tomorrow 1800.
Dates: d/M/yyyy, today, tomorrow, Mon–Sun (or full names); optional HHmm. Or now.
____________________________________________________________
No time how I set the task...
Try: deadline report /by tomorrow 1800.
Dates: d/M/yyyy, today, tomorrow, Mon–Sun (or full names); optional HHmm. Or now.
____________________________________________________________
No time how I set the task...
Try: event meeting /from today 0900 /to tomorrow 1000.
Dates: d/M/yyyy, today, tomorrow, Mon–Sun (or full names); optional HHmm. Or now.
____________________________________________________________
No time how I set the task...
Try: event meeting /from today 0900 /to tomorrow 1000.
Dates: d/M/yyyy, today, tomorrow, Mon–Sun (or full names); optional HHmm. Or now.
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### 19. Save and reopen date-only values

- **Aim:** Keep a date-only deadline distinct from explicit midnight in a second process.
- **Command:** `java -ea -cp "$CLASS_DIR" cheecken.FixedClockCli`
- **Input:**

```text
deadline date /by tomorrow
deadline midnight /by tomorrow 0000
bye
```

- **Expected output:**

```text
 _____ _                    _
/  __ \ |                  | |
| /  \/ |__   ___  ___  ___| | _____ _ __
| |   | '_ \ / _ \/ _ \/ __| |/ / _ \ '_ \
| \__/\ | | |  __/  __/ (__|   <  __/ | | |
 \____/_| |_|\___|\___|\___|_|\_\___|_| |_|
____________________________________________________________
Hello! I'm \033[3mCHEECKEN\033[0m.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] date (by: Sep 08 2026)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] midnight (by: Sep 08 2026 12:00 AM)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

- **Expected storage:**

```text
D | 0 | date | 2026-09-08
D | 0 | midnight | 2026-09-08T00:00
```

- **Restart input:**

```text
list
bye
```

- **Restart expected output:**

```text
 _____ _                    _
/  __ \ |                  | |
| /  \/ |__   ___  ___  ___| | _____ _ __
| |   | '_ \ / _ \/ _ \/ __| |/ / _ \ '_ \
| \__/\ | | |  __/  __/ (__|   <  __/ | | |
 \____/_| |_|\___|\___|\___|_|\_\___|_| |_|
____________________________________________________________
Hello! I'm \033[3mCHEECKEN\033[0m.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] date (by: Sep 08 2026)
2.[D][ ] midnight (by: Sep 08 2026 12:00 AM)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### 20. Load old records and skip stored natural words

- **Aim:** Retain old absolute records without reinterpreting malformed relative records.
- **Command:** `java -ea -cp "$CLASS_DIR" cheecken.FixedClockCli`
- **Initial storage:**

```text
D | 0 | skip relative | tomorrow
E | 0 | skip relative | Mon | 2026-09-09
D | 1 | old midnight | 2026-09-07T00:00
D | 0 | old date | 2026-09-08
```

- **Input:**

```text
list
bye
```

- **Expected output:**

```text
 _____ _                    _
/  __ \ |                  | |
| /  \/ |__   ___  ___  ___| | _____ _ __
| |   | '_ \ / _ \/ _ \/ __| |/ / _ \ '_ \
| \__/\ | | |  __/  __/ (__|   <  __/ | | |
 \____/_| |_|\___|\___|\___|_|\_\___|_| |_|
____________________________________________________________
Hello! I'm \033[3mCHEECKEN\033[0m.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][X] old midnight (by: Sep 07 2026 12:00 AM)
2.[D][ ] old date (by: Sep 08 2026)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
