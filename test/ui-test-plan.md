# CharlieK UI test plan

This plan contains end-to-end console tests for `CharlieK`.

## Execution information

- Run from the repository root.
- Use Java 25.
- Compile before testing:

  ```
    javac -d _temp/ui-test-classes (Get-ChildItem -Path src/main/java -Recurse -Filter *.java |
      Where-Object { $_.Name -notin @('Launcher.java', 'DialogBox.java', 'Main.java', 'MainWindow.java') } |
      ForEach-Object { $_.FullName })
  ```

- Each test case starts a fresh process with:

  ```
  java -cp _temp/ui-test-classes charliek.CharlieK
  ```

- Compare output exactly after normalizing only platform line endings. The skill must stop at the first failure and show the complete console transcript.
- Run each test case from a fresh temporary working directory under `_temp/` so
  the repository's real `data/charliek.csv` is never modified or removed. For
  setup-based cases, create the CSV file inside that temporary directory with
  the contents specified in that test's setup first.
- The CSV file has no header row. Columns are `type,status,description`, followed by `deadline` for `D` tasks or `from,to` for `E` tasks; status `0` means incomplete and `1` means complete. Dates are stored as `yyyy-MM-dd`, and date-times as ISO local date-times such as `yyyy-MM-ddTHH:mm:ss`.

## JavaFX GUI smoke test

The console cases above verify the preserved command-line entry point. Use
the following manual smoke test for the JavaFX entry point:

1. Run `.\gradlew.bat run` from the repository root using Java 25.
2. Confirm that the greeting appears in a left-aligned chatbot bubble with
   `chatbot.png` and that the input field and Send button appear at the bottom.
3. Enter `todo buy milk` and press Enter. Confirm that the command appears in
   a right-aligned user bubble with `user.png`, followed by a left-aligned
   chatbot response.
4. Enter enough long commands to exceed the visible area. Confirm that the
   conversation scrolls automatically to the newest response.
5. Enter `bye`. Confirm that the chatbot shows the goodbye response and the
   input controls become disabled.

## Shared startup output

Every test case begins with:

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you? Let's make this task list great again!
____________________________________________________________
```

## Test cases

### UI-01 — Exit command

**Aim:** Verify that the application starts and exits cleanly when the user enters `bye`.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you? Let's make this task list great again!
____________________________________________________________
____________________________________________________________
     Bye. Keep carrying the flame!
____________________________________________________________
```

### UI-21 — Reject malformed and conflicting task commands

**Aim:** Verify that duplicate tasks, repeated parameters, invalid event ranges, malformed task numbers, and ambiguous spacing are reported without corrupting the task list.

**Setup:** Before starting the application, create an empty `data/charliek.csv` so the test starts without starter tasks.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
todo buy milk
todo buy milk
deadline report /by 2026-06-01 /by 2026-06-02
event meeting /from 2026-06-02 /to 2026-06-01
mark +1
todo  double
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you? Let's make this task list great again!
____________________________________________________________
____________________________________________________________
     Big win! I've added this task:
       [T][ ] buy milk
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     That task is already on the list. Bring a new winner to the board.
____________________________________________________________
____________________________________________________________
     The parameter /by was specified more than once. Use: deadline <description> /by <date/time>
____________________________________________________________
____________________________________________________________
     An event must end after it starts. Keep the timeline winning.
____________________________________________________________
____________________________________________________________
     That command format is not winning. Use: <number>, for example: mark 1
____________________________________________________________
____________________________________________________________
     That command format is not winning. Use 'help' to check the playbook.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your winning list:
     1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
     Bye. Keep carrying the flame!
____________________________________________________________
```

### UI-02 — Add, list, mark, and unmark a task

**Aim:** Verify task creation, listing, completion marking, and completion reversal in one session.

**Setup:** Before starting the application, create an empty `data/charliek.csv` so the test starts without starter tasks.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
todo buy milk
list
mark 1
mark 1
list
unmark 1
unmark 1
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you? Let's make this task list great again!
____________________________________________________________
____________________________________________________________
     Big win! I've added this task:
       [T][ ] buy milk
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your winning list:
     1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
     Another win! I've marked this task as done:
       [T][X] buy milk
____________________________________________________________
____________________________________________________________
     This task is already winning:
       [T][X] buy milk
____________________________________________________________
____________________________________________________________
     Here are the tasks in your winning list:
     1.[T][X] buy milk
____________________________________________________________
____________________________________________________________
     No surrender! I've marked this task as not done yet:
       [T][ ] buy milk
____________________________________________________________
____________________________________________________________
     This task is already not done - stay focused:
       [T][ ] buy milk
____________________________________________________________
____________________________________________________________
     Here are the tasks in your winning list:
     1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
     Bye. Keep carrying the flame!
____________________________________________________________
```

### UI-03 — Invalid task references

**Aim:** Verify that invalid and out-of-range task references produce helpful errors and do not terminate the application.

**Setup:** Before starting the application, create an empty `data/charliek.csv` so the test starts without starter tasks.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
mark 1
mark x
unmark 0
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you? Let's make this task list great again!
____________________________________________________________
____________________________________________________________
     That task does not exist. Next time, choose a winner.
____________________________________________________________
____________________________________________________________
     That command format is not winning. Use: <number>, for example: mark 1
____________________________________________________________
____________________________________________________________
     That task does not exist. Next time, choose a winner.
____________________________________________________________
____________________________________________________________
     Bye. Keep carrying the flame!
____________________________________________________________
```

### UI-04 — Add to-do, deadline, and event tasks

**Aim:** Verify that the three supported task types are parsed, stored polymorphically in the task list, and displayed with their type-specific details.

**Setup:** Before starting the application, create an empty `data/charliek.csv` so the test starts without starter tasks.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
todo borrow book
deadline return book /by 2026-06-07
event project meeting /from 2026-08-06 14:00 /to 2026-08-06 16:00
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you? Let's make this task list great again!
____________________________________________________________
____________________________________________________________
     Big win! I've added this task:
       [T][ ] borrow book
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Big win! I've added this task:
       [D][ ] return book (by: 7 Jun 2026)
     Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
     Big win! I've added this task:
       [E][ ] project meeting (from: 6 Aug 2026, 14:00 to: 6 Aug 2026, 16:00)
     Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your winning list:
     1.[T][ ] borrow book
     2.[D][ ] return book (by: 7 Jun 2026)
     3.[E][ ] project meeting (from: 6 Aug 2026, 14:00 to: 6 Aug 2026, 16:00)
____________________________________________________________
____________________________________________________________
     Bye. Keep carrying the flame!
____________________________________________________________
```

### UI-05 — Parse and normalize date/time variations

**Aim:** Verify that common numeric, ISO, compact-time, and 12-hour date/time inputs are parsed and printed consistently.

**Setup:** Before starting the application, create an empty `data/charliek.csv` so the test starts without starter tasks.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
deadline review report /by 2/12/2019 1800
event orientation week /from 2/12/2019 6pm /to 2019-12-2 23:00
deadline holiday /by 2-12-2019
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you? Let's make this task list great again!
____________________________________________________________
____________________________________________________________
     Big win! I've added this task:
       [D][ ] review report (by: 2 Dec 2019, 18:00)
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Big win! I've added this task:
       [E][ ] orientation week (from: 2 Dec 2019, 18:00 to: 2 Dec 2019, 23:00)
     Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
     Big win! I've added this task:
       [D][ ] holiday (by: 2 Dec 2019)
     Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your winning list:
     1.[D][ ] review report (by: 2 Dec 2019, 18:00)
     2.[E][ ] orientation week (from: 2 Dec 2019, 18:00 to: 2 Dec 2019, 23:00)
     3.[D][ ] holiday (by: 2 Dec 2019)
____________________________________________________________
____________________________________________________________
     Bye. Keep carrying the flame!
____________________________________________________________
```
### UI-06 — Reject non-keyword task additions

**Aim:** Verify that entering a command without a task keyword does not add a task.

**Setup:** Before starting the application, create an empty `data/charliek.csv` so the test starts without starter tasks.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
buy milk
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you? Let's make this task list great again!
____________________________________________________________
____________________________________________________________
     That command is not winning, but I know how to carry the flame!
____________________________________________________________
____________________________________________________________
     Here are the tasks in your winning list:
____________________________________________________________
____________________________________________________________
     Bye. Keep carrying the flame!
____________________________________________________________
```

### UI-07 — Reject empty descriptions and parameters

**Aim:** Verify that empty task descriptions, missing deadline parameters, and missing event parameters are caught and reported without adding malformed tasks.

**Setup:** Before starting the application, create an empty `data/charliek.csv` so the test starts without starter tasks.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
todo
deadline
event
deadline return book
deadline return book /by
event project meeting
event project meeting /from /to 2026-06-06
event project meeting /from Mon 2pm /to
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you? Let's make this task list great again!
____________________________________________________________
____________________________________________________________
     The description is empty! Bring the energy and enter it, or I will carry the flame!
____________________________________________________________
____________________________________________________________
     The description is empty! Bring the energy and enter it, or I will carry the flame!
____________________________________________________________
____________________________________________________________
     The description is empty! Bring the energy and enter it, or I will carry the flame!
____________________________________________________________
____________________________________________________________
     The parameter is empty! Enter the required parameters and let's get this done!
____________________________________________________________
____________________________________________________________
     The parameter is empty! Enter the required parameters and let's get this done!
____________________________________________________________
____________________________________________________________
     The parameter is empty! Enter the required parameters and let's get this done!
____________________________________________________________
____________________________________________________________
     The parameter is empty! Enter the required parameters and let's get this done!
____________________________________________________________
____________________________________________________________
     The parameter is empty! Enter the required parameters and let's get this done!
____________________________________________________________
____________________________________________________________
     Here are the tasks in your winning list:
____________________________________________________________
____________________________________________________________
     Bye. Keep carrying the flame!
____________________________________________________________
```

### UI-08 — Delete a task and compact the list

**Aim:** Verify that a selected task is removed, the remaining tasks are renumbered, and the updated task count is displayed.

**Setup:** Before starting the application, create an empty `data/charliek.csv` so the test starts without starter tasks.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
todo read book
mark 1
deadline return book /by 2026-06-06
mark 2
event project meeting /from 2026-08-06 14:00 /to 2026-08-06 16:00
todo join sports club
mark 4
todo borrow book
list
delete 3
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you? Let's make this task list great again!
____________________________________________________________
____________________________________________________________
     Big win! I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Another win! I've marked this task as done:
       [T][X] read book
____________________________________________________________
____________________________________________________________
     Big win! I've added this task:
       [D][ ] return book (by: 6 Jun 2026)
     Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
     Another win! I've marked this task as done:
       [D][X] return book (by: 6 Jun 2026)
____________________________________________________________
____________________________________________________________
     Big win! I've added this task:
       [E][ ] project meeting (from: 6 Aug 2026, 14:00 to: 6 Aug 2026, 16:00)
     Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
     Big win! I've added this task:
       [T][ ] join sports club
     Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
     Another win! I've marked this task as done:
       [T][X] join sports club
____________________________________________________________
____________________________________________________________
     Big win! I've added this task:
       [T][ ] borrow book
     Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your winning list:
     1.[T][X] read book
     2.[D][X] return book (by: 6 Jun 2026)
     3.[E][ ] project meeting (from: 6 Aug 2026, 14:00 to: 6 Aug 2026, 16:00)
     4.[T][X] join sports club
     5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
     Strong move. I've removed this task:
       [E][ ] project meeting (from: 6 Aug 2026, 14:00 to: 6 Aug 2026, 16:00)
     Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your winning list:
     1.[T][X] read book
     2.[D][X] return book (by: 6 Jun 2026)
     3.[T][X] join sports club
     4.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
     Bye. Keep carrying the flame!
____________________________________________________________
```

### UI-09 — Save the task list after changes

**Aim:** Exercise the task-list mutation commands that trigger automatic saving to `data/charliek.csv`.

**Setup:** Before starting the application, create an empty `data/charliek.csv` so the test starts without starter tasks.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
todo save me
deadline keep me /by 2026-06-06
mark 1
unmark 1
delete 2
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you? Let's make this task list great again!
____________________________________________________________
____________________________________________________________
     Big win! I've added this task:
       [T][ ] save me
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Big win! I've added this task:
       [D][ ] keep me (by: 6 Jun 2026)
     Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
     Another win! I've marked this task as done:
       [T][X] save me
____________________________________________________________
____________________________________________________________
     No surrender! I've marked this task as not done yet:
       [T][ ] save me
____________________________________________________________
____________________________________________________________
     Strong move. I've removed this task:
       [D][ ] keep me (by: 6 Jun 2026)
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your winning list:
     1.[T][ ] save me
____________________________________________________________
____________________________________________________________
     Bye. Keep carrying the flame!
____________________________________________________________
```

### UI-10 — Load the saved task list at startup

**Aim:** Verify that to-do, deadline, and event tasks, including their completion status, are reconstructed from the saved file when the chatbot starts.

**Setup:** Before starting the application, create `data/charliek.csv` with exactly:

```
T,1,persisted to-do
D,0,persisted deadline,2019-12-02
E,0,persisted event,2019-12-02T14:00,2019-12-02T15:00
```

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you? Let's make this task list great again!
____________________________________________________________
____________________________________________________________
     Here are the tasks in your winning list:
     1.[T][X] persisted to-do
     2.[D][ ] persisted deadline (by: 2 Dec 2019)
     3.[E][ ] persisted event (from: 2 Dec 2019, 14:00 to: 2 Dec 2019, 15:00)
____________________________________________________________
____________________________________________________________
     Bye. Keep carrying the flame!
____________________________________________________________
```

### UI-11 — Seed sample tasks for a new user

**Aim:** Verify that a first-time user receives a varied starter task list and that it is persisted.

**Setup:** Before starting the application, ensure that `data/charliek.csv` does not exist. The application must create the missing parent directory and CSV file with starter tasks.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you? Let's make this task list great again!
____________________________________________________________
____________________________________________________________
     Here are the tasks in your winning list:
     1.[T][X] Read the quick-start help
     2.[T][ ] Add your first personal task
     3.[D][ ] Plan the week's priorities (by: 15 Jan 2099)
     4.[D][ ] Submit an objective report (by: 20 Jan 2099, 17:00)
     5.[E][ ] Morning briefing (no podium required) (from: 10 Jan 2099 to: 11 Jan 2099)
     6.[E][ ] Deep work (hot takes muted) (from: 12 Jan 2099, 09:00 to: 12 Jan 2099, 10:30)
     7.[T][ ] Drink water before another hot take
____________________________________________________________
____________________________________________________________
     Bye. Keep carrying the flame!
____________________________________________________________
```

### UI-12 — Ignore malformed saved records

**Aim:** Verify that malformed lines in the task file do not crash startup or prevent valid records from loading.

**Setup:** Before starting the application, create `data/charliek.csv` with exactly:

```
not a CSV record
T,1,valid saved task
D,0,valid saved deadline,2019-12-02
```

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you? Let's make this task list great again!
____________________________________________________________
____________________________________________________________
     Here are the tasks in your winning list:
     1.[T][X] valid saved task
     2.[D][ ] valid saved deadline (by: 2 Dec 2019)
____________________________________________________________
____________________________________________________________
     Bye. Keep carrying the flame!
____________________________________________________________
```

### UI-13 — Report an unusable task path and roll back changes

**Aim:** Verify that an unusable task path produces a helpful error and that a failed save does not leave an unsaved task in memory.

**Setup:** Before starting the application, create a directory named `data/charliek.csv` instead of a regular file.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
todo should not save
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you? Let's make this task list great again!
____________________________________________________________
     I couldn't load saved tasks because the task file path is not a regular file.
____________________________________________________________
____________________________________________________________
     I couldn't save tasks. Please check that the data folder is writable.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your winning list:
____________________________________________________________
____________________________________________________________
     Bye. Keep carrying the flame!
____________________________________________________________
```

### UI-14 — Preserve commas and quotes in CSV fields

**Aim:** Verify that commas and quotes inside descriptions and timing values are escaped when saved and reconstructed correctly when loaded.

**Setup:** Before starting the application, create `data/charliek.csv` with exactly:

```
T,0,"buy, milk"
D,1,"return, book","2019-06-06"
E,0,"project ""sync""","2019-08-06T14:00","2019-08-06T16:00"
```

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you? Let's make this task list great again!
____________________________________________________________
____________________________________________________________
     Here are the tasks in your winning list:
     1.[T][ ] buy, milk
     2.[D][X] return, book (by: 6 Jun 2019)
     3.[E][ ] project "sync" (from: 6 Aug 2019, 14:00 to: 6 Aug 2019, 16:00)
____________________________________________________________
____________________________________________________________
     Bye. Keep carrying the flame!
____________________________________________________________
```

### UI-15 — Reject unsupported date/time formats

**Aim:** Verify that malformed deadline and event date/time values are reported without terminating the application or adding tasks.

**Setup:** Before starting the application, create an empty `data/charliek.csv` so the test starts without starter tasks.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
deadline submit report /by not a date
event planning /from 2/12/2019 /to invalid
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you? Let's make this task list great again!
____________________________________________________________
____________________________________________________________
     I couldn't understand that date/time! Try 2/12/2019 or 2/12/2019 6pm. Keep it winning.
____________________________________________________________
____________________________________________________________
     I couldn't understand that date/time! Try 2/12/2019 or 2/12/2019 6pm. Keep it winning.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your winning list:
____________________________________________________________
____________________________________________________________
     Bye. Keep carrying the flame!
____________________________________________________________
```

### UI-16 — Sort tasks chronologically with `list time`

**Aim:** Verify that `list time` sorts dated tasks by their deadline or event start, places undated to-dos last, and leaves ordinary `list` order unchanged.

**Setup:** Before starting the application, create an empty `data/charliek.csv` so the test starts without starter tasks.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
todo no date
deadline later /by 2026-12-31
event early /from 2026-01-01 /to 2026-01-02
deadline middle /by 2026-06-01 1200
list time
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you? Let's make this task list great again!
____________________________________________________________
____________________________________________________________
     Big win! I've added this task:
       [T][ ] no date
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Big win! I've added this task:
       [D][ ] later (by: 31 Dec 2026)
     Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
     Big win! I've added this task:
       [E][ ] early (from: 1 Jan 2026 to: 2 Jan 2026)
     Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
     Big win! I've added this task:
       [D][ ] middle (by: 1 Jun 2026, 12:00)
     Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your winning list:
     1.[E][ ] early (from: 1 Jan 2026 to: 2 Jan 2026)
     2.[D][ ] middle (by: 1 Jun 2026, 12:00)
     3.[D][ ] later (by: 31 Dec 2026)
     4.[T][ ] no date
____________________________________________________________
____________________________________________________________
     Here are the tasks in your winning list:
     1.[T][ ] no date
     2.[D][ ] later (by: 31 Dec 2026)
     3.[E][ ] early (from: 1 Jan 2026 to: 2 Jan 2026)
     4.[D][ ] middle (by: 1 Jun 2026, 12:00)
____________________________________________________________
____________________________________________________________
     Bye. Keep carrying the flame!
____________________________________________________________
```

### UI-17 — Handle an unsupported `list` option

**Aim:** Verify that an unsupported argument to `list` is reported without terminating the application.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
list unsupported
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you? Let's make this task list great again!
____________________________________________________________
____________________________________________________________
     That command format is not winning. Use: list [time]
____________________________________________________________
____________________________________________________________
     Bye. Keep carrying the flame!
____________________________________________________________
```

### UI-18 — Find tasks by keyword

**Aim:** Verify that `find` displays only tasks whose descriptions contain the supplied keyword, without regard to letter case.

**Setup:** Before starting the application, create an empty `data/charliek.csv` so the test starts without starter tasks.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
todo Read book
todo Buy milk
find BOOK
find meeting
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you? Let's make this task list great again!
____________________________________________________________
____________________________________________________________
     Big win! I've added this task:
       [T][ ] Read book
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Big win! I've added this task:
       [T][ ] Buy milk
     Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the matching tasks in your winning list:
     1.[T][ ] Read book
____________________________________________________________
____________________________________________________________
     Here are the matching tasks in your winning list:
____________________________________________________________
____________________________________________________________
     Bye. Keep carrying the flame!
____________________________________________________________
```

### UI-19 — Display the available commands

**Aim:** Verify that `help` lists every available command with its parameters, a brief description, and an example.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
help
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you? Let's make this task list great again!
____________________________________________________________
____________________________________________________________
     Available commands - the winning playbook

     todo <description>
       Add an undated to-do task.
       Example: todo buy milk

     deadline <description> /by <date/time>
       Add a task with a deadline.
       Example: deadline submit report /by 2/12/2019

     event <description> /from <date/time> /to <date/time>
       Add an event task.
       Example: event project meeting /from 2026-08-06 2pm /to 2026-08-06 4pm

     list [time]
       Display tasks, optionally in chronological order.
       Example: list time

     find <keyword>
       Find tasks containing a keyword.
       Example: find book

     mark <number>
       Mark a task as complete.
       Example: mark 1

     unmark <number>
       Mark a task as incomplete.
       Example: unmark 1

     delete <number>
       Delete a task.
       Example: delete 1

     bye
       Exit CharlieK.
       Example: bye
____________________________________________________________
____________________________________________________________
     Bye. Keep carrying the flame!
____________________________________________________________
```

### UI-20 — Display detailed command usage

**Aim:** Verify that `help <command>` displays the selected command's usage, description, and example.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
help deadline
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you? Let's make this task list great again!
____________________________________________________________
____________________________________________________________
     Help: deadline

     Usage
       deadline <description> /by <date/time>

     Description
       Add a task with a deadline.

     Example
       deadline submit report /by 2/12/2019
____________________________________________________________
____________________________________________________________
     Bye. Keep carrying the flame!
____________________________________________________________
```
