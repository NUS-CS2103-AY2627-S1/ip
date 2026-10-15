# Console UI Test Plan

## How to run the application

Use Java 25. Compile into an ignored temporary directory and start a fresh process for each test case:

```powershell
.\gradlew.bat classes
$uiTestClasses = Join-Path (Get-Location) 'build/classes/java/main'
java -ea -cp $uiTestClasses jassabot.JassaBot
```

The `-ea` option enables Java assertions so these cases also check internal assumptions.

Start each case in a fresh temporary working directory so tests never overwrite your own saved tasks.
Use the absolute class path from above when starting the process there. All data paths below are
relative to that case's working directory.

The interactive console is also available with `.\gradlew.bat --console=plain runConsole`.
The default `.\gradlew.bat run` starts the GUI; it uses the same commands and storage format.

Storage-file blocks have the following meanings:

- **Initial saved file:** create these exact lines after cleanup and before starting the process.
- **Initial storage path:** create the stated file-system object after cleanup and before starting the process.
- **Expected saved file:** compare these lines with `Get-Content .\data\jassabot.txt` after the preceding input. This deliberately ignores the operating system's line-ending convention.
- **Expected startup continuation:** append these exact lines to the shared startup block for that case.
- **Working directory:** start that case in the stated directory and resolve `data/jassabot.txt` relative to it.

In an input block, `<blank line>` means send an empty line and `<close input>` means close the process's input stream without sending another line.

The expected startup output for every test case is:

```text
____________________________________________________________
   _                         ____        _
  | | __ _ ___ ___  __ _    | __ )  ___ | |_
  | |/ _` / __/ __|/ _` |   |  _ \ / _ \| __|
  | | (_| \__ \__ \ (_| |   | |_) | (_) | |_
 _|_|\__,_|___/___/\__,_|   |____/ \___/ \__|
Hello, I'm JassaBot.
Let's make room for a little progress today.
Type help to see available commands.
____________________________________________________________
```

Each case below is one fresh console session. After sending an input, compare the next output block exactly, including whitespace and divider lines.

## Test cases

### TC-01 — Exit politely

**Aim:** Verify that the application acknowledges the exit command and terminates.

**Inputs and expected output:**

1. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye for now. Take your time, and keep growing.
   ____________________________________________________________
   ```

### TC-02 — Add and list the three task types

**Aim:** Verify that Todo, Deadline, and Event commands retain their type and date or time details in `list`.

**Inputs and expected output:**

1. Input:

   ```text
   todo read book
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [T][ ] read book
   Your garden now holds 1 task.
   ____________________________________________________________
   ```

2. Input:

   ```text
   deadline return book /by 2/12/2019 1800
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [D][ ] return book (by: Dec 2 2019, 6:00 PM)
   Your garden now holds 2 tasks.
   ____________________________________________________________
   ```

3. Input:

   ```text
   event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [E][ ] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
   Your garden now holds 3 tasks.
   ____________________________________________________________
   ```

4. Input:

   ```text
   list
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Here's what's growing in your task list:
   1.[T][ ] read book
   2.[D][ ] return book (by: Dec 2 2019, 6:00 PM)
   3.[E][ ] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
   ____________________________________________________________
   ```

5. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye for now. Take your time, and keep growing.
   ____________________________________________________________
   ```

### TC-03 — Mark and unmark a task

**Aim:** Verify that task completion state changes are reflected in the confirmation and list output.

**Inputs and expected output:**

1. Input:

   ```text
   todo read book
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [T][ ] read book
   Your garden now holds 1 task.
   ____________________________________________________________
   ```

2. Input:

   ```text
   mark 1
   ```

   Expected output:

   ```text
   ____________________________________________________________
   A little progress, a little growth. Task completed:
     [T][X] read book
   ____________________________________________________________
   ```

3. Input:

   ```text
   unmark 1
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Room to grow. This task is marked as not done:
     [T][ ] read book
   ____________________________________________________________
   ```

4. Input:

   ```text
   list
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Here's what's growing in your task list:
   1.[T][ ] read book
   ____________________________________________________________
   ```

5. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye for now. Take your time, and keep growing.
   ____________________________________________________________
   ```

### TC-04 — Reject an invalid task number

**Aim:** Verify that a task number outside the list is rejected without changing the task.

**Inputs and expected output:**

1. Input:

   ```text
   mark 1
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Please enter a valid task number.
   ____________________________________________________________
   ```

2. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye for now. Take your time, and keep growing.
   ____________________________________________________________
   ```

### TC-05 — Reject incomplete date commands

**Aim:** Verify that missing date markers do not add an incomplete Deadline or Event task.

**Inputs and expected output:**

1. Input:

   ```text
   deadline return book
   ```

   Expected output:

   ```text
   ____________________________________________________________
   A deadline needs '/by' followed by its due time.
   ____________________________________________________________
   ```

2. Input:

   ```text
   event project meeting /from Mon 2pm
   ```

   Expected output:

   ```text
   ____________________________________________________________
   An event needs both '/from' and '/to' time markers.
   ____________________________________________________________
   ```

3. Input:

   ```text
   list
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Your garden is clear. Enjoy the breathing room.
   ____________________________________________________________
   ```

4. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye for now. Take your time, and keep growing.
   ____________________________________________________________
   ```

### TC-06 — Interleave valid and invalid commands

**Aim:** Verify that empty descriptions and unknown commands are handled without adding tasks. The list after each group confirms that the invalid input did not affect the task count.

**Inputs and expected output:**

1. Input:

   ```text
   todo read book
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [T][ ] read book
   Your garden now holds 1 task.
   ____________________________________________________________
   ```

2. Input:

   ```text
   todo
   ```

   Expected output:

   ```text
   ____________________________________________________________
   The description of a todo cannot be empty.
   ____________________________________________________________
   ```

3. Input:

   ```text
   list
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Here's what's growing in your task list:
   1.[T][ ] read book
   ____________________________________________________________
   ```

4. Input:

   ```text
   deadline return book /by 2/12/2019 1800
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [D][ ] return book (by: Dec 2 2019, 6:00 PM)
   Your garden now holds 2 tasks.
   ____________________________________________________________
   ```

5. Input:

   ```text
   deadline
   ```

   Expected output:

   ```text
   ____________________________________________________________
   The description of a deadline cannot be empty.
   ____________________________________________________________
   ```

6. Input:

   ```text
   event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [E][ ] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
   Your garden now holds 3 tasks.
   ____________________________________________________________
   ```

7. Input:

   ```text
   event
   ```

   Expected output:

   ```text
   ____________________________________________________________
   The description of an event cannot be empty.
   ____________________________________________________________
   ```

8. Input:

   ```text
   blah
   ```

   Expected output:

   ```text
   ____________________________________________________________
   I couldn't understand that command. Try help to see what you can do.
   ____________________________________________________________
   ```

9. Input:

   ```text
   list
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Here's what's growing in your task list:
   1.[T][ ] read book
   2.[D][ ] return book (by: Dec 2 2019, 6:00 PM)
   3.[E][ ] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
   ____________________________________________________________
   ```

10. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye for now. Take your time, and keep growing.
   ____________________________________________________________
   ```

### TC-07 - Delete a task

**Aim:** Verify that deleting a task removes it from the `ArrayList`, reports the removed task and new count, renumbers later tasks, and rejects an invalid task number without changing the list.

**Inputs and expected output:**

1. Input:

   ```text
   todo read book
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [T][ ] read book
   Your garden now holds 1 task.
   ____________________________________________________________
   ```

2. Input:

   ```text
   deadline return book /by 2/12/2019 1800
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [D][ ] return book (by: Dec 2 2019, 6:00 PM)
   Your garden now holds 2 tasks.
   ____________________________________________________________
   ```

3. Input:

   ```text
   event project meeting /from 6/8/2019 1400 /to 6/8/2019 1600
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [E][ ] project meeting (from: Aug 6 2019, 2:00 PM to: Aug 6 2019, 4:00 PM)
   Your garden now holds 3 tasks.
   ____________________________________________________________
   ```

4. Input:

   ```text
   todo exercise
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [T][ ] exercise
   Your garden now holds 4 tasks.
   ____________________________________________________________
   ```

5. Input:

   ```text
   todo sleep
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [T][ ] sleep
   Your garden now holds 5 tasks.
   ____________________________________________________________
   ```

6. Input:

   ```text
   delete 3
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Removed this task. More room for what matters:
     [E][ ] project meeting (from: Aug 6 2019, 2:00 PM to: Aug 6 2019, 4:00 PM)
   Your garden now holds 4 tasks.
   ____________________________________________________________
   ```

7. Input:

   ```text
   delete 9
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Please enter a valid task number.
   ____________________________________________________________
   ```

8. Input:

   ```text
   list
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Here's what's growing in your task list:
   1.[T][ ] read book
   2.[D][ ] return book (by: Dec 2 2019, 6:00 PM)
   3.[T][ ] exercise
   4.[T][ ] sleep
   ____________________________________________________________
   ```

9. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye for now. Take your time, and keep growing.
   ____________________________________________________________
   ```

### TC-17 — Find tasks by description keyword

**Aim:** Verify that `find` performs a case-insensitive substring search, keeps matching tasks in list order, handles no matches, and rejects a missing keyword.

**Inputs and expected output:**

1. Input:

   ```text
   todo Read Book
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [T][ ] Read Book
   Your garden now holds 1 task.
   ____________________________________________________________
   ```

2. Input:

   ```text
   deadline return book /by 2/12/2019 1800
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [D][ ] return book (by: Dec 2 2019, 6:00 PM)
   Your garden now holds 2 tasks.
   ____________________________________________________________
   ```

3. Input:

   ```text
   todo exercise
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [T][ ] exercise
   Your garden now holds 3 tasks.
   ____________________________________________________________
   ```

4. Input:

   ```text
   mark 1
   ```

   Expected output:

   ```text
   ____________________________________________________________
   A little progress, a little growth. Task completed:
     [T][X] Read Book
   ____________________________________________________________
   ```

5. Input:

   ```text
   find BOOK
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Here's what I found in your task garden:
   1.[T][X] Read Book
   2.[D][ ] return book (by: Dec 2 2019, 6:00 PM)
   ____________________________________________________________
   ```

6. Input:

   ```text
   find groceries
   ```

   Expected output:

   ```text
   ____________________________________________________________
   No matching tasks this time. Try another keyword.
   ____________________________________________________________
   ```

7. Input:

   ```text
   find
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Please enter a keyword to find.
   ____________________________________________________________
   ```

8. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye for now. Take your time, and keep growing.
   ____________________________________________________________
   ```

### TC-08 — Save every task-list change

**Aim:** Verify that adding, marking, unmarking, and deleting tasks immediately rewrites `./data/jassabot.txt` with the complete current task list.

**Inputs and expected output:**

1. Input:

   ```text
   todo read book
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [T][ ] read book
   Your garden now holds 1 task.
   ____________________________________________________________
   ```

   Expected saved file:

   ```text
   T | 0 | read book
   ```

2. Input:

   ```text
   deadline return book /by 2/12/2019 1800
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [D][ ] return book (by: Dec 2 2019, 6:00 PM)
   Your garden now holds 2 tasks.
   ____________________________________________________________
   ```

   Expected saved file:

   ```text
   T | 0 | read book
   D | 0 | return book | 2019-12-02T18:00
   ```

3. Input:

   ```text
   event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [E][ ] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
   Your garden now holds 3 tasks.
   ____________________________________________________________
   ```

   Expected saved file:

   ```text
   T | 0 | read book
   D | 0 | return book | 2019-12-02T18:00
   E | 0 | project meeting | 2019-12-02T14:00 | 2019-12-02T16:00
   ```

4. Input:

   ```text
   mark 1
   ```

   Expected output:

   ```text
   ____________________________________________________________
   A little progress, a little growth. Task completed:
     [T][X] read book
   ____________________________________________________________
   ```

   Expected saved file:

   ```text
   T | 1 | read book
   D | 0 | return book | 2019-12-02T18:00
   E | 0 | project meeting | 2019-12-02T14:00 | 2019-12-02T16:00
   ```

5. Input:

   ```text
   unmark 1
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Room to grow. This task is marked as not done:
     [T][ ] read book
   ____________________________________________________________
   ```

   Expected saved file:

   ```text
   T | 0 | read book
   D | 0 | return book | 2019-12-02T18:00
   E | 0 | project meeting | 2019-12-02T14:00 | 2019-12-02T16:00
   ```

6. Input:

   ```text
   delete 2
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Removed this task. More room for what matters:
     [D][ ] return book (by: Dec 2 2019, 6:00 PM)
   Your garden now holds 2 tasks.
   ____________________________________________________________
   ```

   Expected saved file:

   ```text
   T | 0 | read book
   E | 0 | project meeting | 2019-12-02T14:00 | 2019-12-02T16:00
   ```

7. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye for now. Take your time, and keep growing.
   ____________________________________________________________
   ```

### TC-09 — Load saved tasks on startup

**Aim:** Verify that a fresh application process reconstructs every task type and its completion status from `./data/jassabot.txt`.

**Initial saved file:** After the common cleanup, create the file with these exact lines before starting the process.

```text
T | 1 | read book
D | 0 | return book | 2019-12-02T18:00
E | 1 | project meeting | 2019-12-02T14:00 | 2019-12-02T16:00
```

**Inputs and expected output:**

1. Input:

   ```text
   list
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Here's what's growing in your task list:
   1.[T][X] read book
   2.[D][ ] return book (by: Dec 2 2019, 6:00 PM)
   3.[E][X] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
   ____________________________________________________________
   ```

2. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye for now. Take your time, and keep growing.
   ____________________________________________________________
   ```

### TC-10 — Round-trip delimiter and backslash characters

**Aim:** Verify that reserved storage characters in user text are escaped when saved and decoded without changing the task when loaded.

**Initial saved file:**

```text
T | 0 | compare A \| B \\ C
```

**Inputs and expected output:**

1. Input:

   ```text
   list
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Here's what's growing in your task list:
   1.[T][ ] compare A | B \ C
   ____________________________________________________________
   ```

2. Input:

   ```text
   todo save X | Y \ Z
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [T][ ] save X | Y \ Z
   Your garden now holds 2 tasks.
   ____________________________________________________________
   ```

   Expected saved file:

   ```text
   T | 0 | compare A \| B \\ C
   T | 0 | save X \| Y \\ Z
   ```

3. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye for now. Take your time, and keep growing.
   ____________________________________________________________
   ```


### TC-11 — Recover valid tasks from malformed data

**Aim:** Verify that malformed task types, field counts, statuses, and dates produce precise warnings while valid lines still load.

**Initial saved file:**

```text
T | 1 | valid todo
X | 0 | mystery task
T | 0 | too | many
T | 2 | invalid status
D | 0 | missing time
E | 0 | missing end | 2pm
D | 0 | invalid stored deadline | Sunday
E | 0 | invalid stored event | 2019-12-02T14:00 | 4pm
E | 1 | valid meeting | 2019-12-02T14:00 | 2019-12-02T16:00
```

**Expected startup continuation:**

```text
WARNING: Skipped data line 2: unknown task type 'X'.
WARNING: Skipped data line 3: task type 'T' expects 3 fields but found 4.
WARNING: Skipped data line 4: status must be 0 or 1.
WARNING: Skipped data line 5: task type 'D' expects 4 fields but found 3.
WARNING: Skipped data line 6: task type 'E' expects 5 fields but found 4.
WARNING: Skipped data line 7: deadline date and time must use yyyy-MM-dd'T'HH:mm format.
WARNING: Skipped data line 8: event end date and time must use yyyy-MM-dd'T'HH:mm format.
____________________________________________________________
```

**Inputs and expected output:**

1. Input:

   ```text
   list
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Here's what's growing in your task list:
   1.[T][X] valid todo
   2.[E][X] valid meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
   ____________________________________________________________
   ```

2. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye for now. Take your time, and keep growing.
   ____________________________________________________________
   ```


### TC-12 — Report storage-path failures and roll back

**Aim:** Verify that a non-file storage path produces a startup warning and that a failed save leaves the in-memory task list unchanged.

**Initial storage path:** Create a directory at `./data/jassabot.txt`.

**Expected startup continuation:**

```text
WARNING: The task data path is not a regular file. Starting with an empty task list.
____________________________________________________________
```

**Inputs and expected output:**

1. Input:

   ```text
   todo should not remain
   ```

   Expected output:

   ```text
   ____________________________________________________________
   I couldn't save your tasks, so no changes were made.
   ____________________________________________________________
   ```

2. Input:

   ```text
   list
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Your garden is clear. Enjoy the breathing room.
   ____________________________________________________________
   ```

3. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye for now. Take your time, and keep growing.
   ____________________________________________________________
   ```


### TC-13 — Reject blank and incomplete commands

**Aim:** Verify that blank input, missing task numbers, overflowed numbers, empty times, and surrounding whitespace are handled without crashing or adding invalid tasks.

**Inputs and expected output:**

1. Input:

   ```text
   <blank line>
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Please enter a command.
   ____________________________________________________________
   ```

2. Input:

   ```text
   mark
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Please enter a valid task number.
   ____________________________________________________________
   ```

3. Input:

   ```text
   unmark
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Please enter a valid task number.
   ____________________________________________________________
   ```

4. Input:

   ```text
   delete
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Please enter a valid task number.
   ____________________________________________________________
   ```

5. Input:

   ```text
   mark 999999999999999999999999
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Please enter a valid task number.
   ____________________________________________________________
   ```

6. Input:

   ```text
   deadline return book /by
   ```

   Expected output:

   ```text
   ____________________________________________________________
   A deadline needs '/by' followed by its due time.
   ____________________________________________________________
   ```

7. Input:

   ```text
   event meeting /from /to 4pm
   ```

   Expected output:

   ```text
   ____________________________________________________________
   An event needs non-empty times after both '/from' and '/to'.
   ____________________________________________________________
   ```

8. Input:

   ```text
   event meeting /from 2pm /to
   ```

   Expected output:

   ```text
   ____________________________________________________________
   An event needs non-empty times after both '/from' and '/to'.
   ____________________________________________________________
   ```

9. Input:

   ```text
      todo spaced task
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [T][ ] spaced task
   Your garden now holds 1 task.
   ____________________________________________________________
   ```

10. Input:

   ```text
   list
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Here's what's growing in your task list:
   1.[T][ ] spaced task
   ____________________________________________________________
   ```

11. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye for now. Take your time, and keep growing.
   ____________________________________________________________
   ```


### TC-14 — Exit cleanly when input closes

**Aim:** Verify that end-of-input is handled as a normal shutdown instead of throwing an exception.

**Inputs and expected output:**

1. Input:

   ```text
   <close input>
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Input closed. Take care, and keep growing.
   ____________________________________________________________
   ```


### TC-15 — Create storage on first run

**Aim:** Verify that the OS-independent relative path creates the missing `data` folder and task file on a first run.

**Working directory:** Start in a fresh temporary directory that does not contain a `data` folder.

**Inputs and expected output:**

1. Input:

   ```text
   todo first-run task
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [T][ ] first-run task
   Your garden now holds 1 task.
   ____________________________________________________________
   ```

   Expected saved file:

   ```text
   T | 0 | first-run task
   ```

2. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye for now. Take your time, and keep growing.
   ____________________________________________________________
   ```

### TC-16 - Parse and validate dates and times

**Aim:** Verify both supported date styles, optional times, friendly display formatting, and rejection of impossible dates without adding a task.

**Inputs and expected output:**

1. Input:

   ```text
   deadline return book /by 2/12/2019 1800
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [D][ ] return book (by: Dec 2 2019, 6:00 PM)
   Your garden now holds 1 task.
   ____________________________________________________________
   ```

2. Input:

   ```text
   event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [E][ ] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
   Your garden now holds 2 tasks.
   ____________________________________________________________
   ```

3. Input:

   ```text
   deadline date only /by 2019-10-15
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [D][ ] date only (by: Oct 15 2019)
   Your garden now holds 3 tasks.
   ____________________________________________________________
   ```

4. Input:

   ```text
   deadline impossible /by 2019-02-29 1200
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Please enter a valid date as yyyy-MM-dd or d/M/yyyy, optionally followed by a time in HHmm format.
   ____________________________________________________________
   ```

5. Input:

   ```text
   list
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Here's what's growing in your task list:
   1.[D][ ] return book (by: Dec 2 2019, 6:00 PM)
   2.[E][ ] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
   3.[D][ ] date only (by: Oct 15 2019)
   ____________________________________________________________
   ```

6. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye for now. Take your time, and keep growing.
   ____________________________________________________________
   ```

### TC-18 - Show help and continue using tasks

**Aim:** Verify exact help output, repeated requests, leading spaces, unchanged tasks, and continued command handling.

**Inputs and expected output:**

1. Input:

   ```text
   help
   ```

   Expected output:

   ```text
   ____________________________________________________________
   JassaBot commands:
   todo DESCRIPTION - Add a task.
   deadline DESCRIPTION /by DATE [TIME] - Add a deadline.
   event DESCRIPTION /from DATE [TIME] /to DATE [TIME] - Add an event.
   list - Show all tasks and their numbers.
   find KEYWORD - Find descriptions containing KEYWORD, ignoring case.
   mark NUMBER - Mark a task as done.
   unmark NUMBER - Mark a task as not done.
   delete NUMBER - Delete a task.
   help - Show this help.
   bye - Exit JassaBot.

   Replace uppercase placeholders with your values. [TIME] is optional.
   Commands are lowercase. Dates: yyyy-MM-dd or d/M/yyyy. Time: 24-hour HHmm.
   Use numbers from list for mark, unmark, and delete; find renumbers its results.

   Examples:
   deadline return book /by 2019-12-02 1800
   event meeting /from 2019-12-02 1400 /to 2019-12-02 1600
   ____________________________________________________________
   ```

2. Input:

   ```text
   todo keep me
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [T][ ] keep me
   Your garden now holds 1 task.
   ____________________________________________________________
   ```

   Expected saved file:

   ```text
   T | 0 | keep me
   ```

3. Input:

   ```text
     help
   ```

   Expected output:

   ```text
   ____________________________________________________________
   JassaBot commands:
   todo DESCRIPTION - Add a task.
   deadline DESCRIPTION /by DATE [TIME] - Add a deadline.
   event DESCRIPTION /from DATE [TIME] /to DATE [TIME] - Add an event.
   list - Show all tasks and their numbers.
   find KEYWORD - Find descriptions containing KEYWORD, ignoring case.
   mark NUMBER - Mark a task as done.
   unmark NUMBER - Mark a task as not done.
   delete NUMBER - Delete a task.
   help - Show this help.
   bye - Exit JassaBot.

   Replace uppercase placeholders with your values. [TIME] is optional.
   Commands are lowercase. Dates: yyyy-MM-dd or d/M/yyyy. Time: 24-hour HHmm.
   Use numbers from list for mark, unmark, and delete; find renumbers its results.

   Examples:
   deadline return book /by 2019-12-02 1800
   event meeting /from 2019-12-02 1400 /to 2019-12-02 1600
   ____________________________________________________________
   ```

   Expected saved file:

   ```text
   T | 0 | keep me
   ```

4. Input:

   ```text
   list
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Here's what's growing in your task list:
   1.[T][ ] keep me
   ____________________________________________________________
   ```

5. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye for now. Take your time, and keep growing.
   ____________________________________________________________
   ```


### TC-19 - Reject help arguments, aliases, and wrong case

**Aim:** Verify that invalid help variants use the unknown-command response and allow recovery.

**Inputs and expected output:**

1. Input:

   ```text
   help todo
   ```

   Expected output:

   ```text
   ____________________________________________________________
   I couldn't understand that command. Try help to see what you can do.
   ____________________________________________________________
   ```

2. Input:

   ```text
   HELP
   ```

   Expected output:

   ```text
   ____________________________________________________________
   I couldn't understand that command. Try help to see what you can do.
   ____________________________________________________________
   ```

3. Input:

   ```text
   Help
   ```

   Expected output:

   ```text
   ____________________________________________________________
   I couldn't understand that command. Try help to see what you can do.
   ____________________________________________________________
   ```

4. Input:

   ```text
   ?
   ```

   Expected output:

   ```text
   ____________________________________________________________
   I couldn't understand that command. Try help to see what you can do.
   ____________________________________________________________
   ```

5. Input:

   ```text
   /help
   ```

   Expected output:

   ```text
   ____________________________________________________________
   I couldn't understand that command. Try help to see what you can do.
   ____________________________________________________________
   ```

6. Input:

   ```text
   --help
   ```

   Expected output:

   ```text
   ____________________________________________________________
   I couldn't understand that command. Try help to see what you can do.
   ____________________________________________________________
   ```

7. Input:

   ```text
   helper
   ```

   Expected output:

   ```text
   ____________________________________________________________
   I couldn't understand that command. Try help to see what you can do.
   ____________________________________________________________
   ```

8. Input:

   ```text
   help123
   ```

   Expected output:

   ```text
   ____________________________________________________________
   I couldn't understand that command. Try help to see what you can do.
   ____________________________________________________________
   ```

9. Input:

   ```text
   help	todo
   ```

   Expected output:

   ```text
   ____________________________________________________________
   I couldn't understand that command. Try help to see what you can do.
   ____________________________________________________________
   ```

10. Input:

   ```text
   help
   ```

   Expected output:

   ```text
   ____________________________________________________________
   JassaBot commands:
   todo DESCRIPTION - Add a task.
   deadline DESCRIPTION /by DATE [TIME] - Add a deadline.
   event DESCRIPTION /from DATE [TIME] /to DATE [TIME] - Add an event.
   list - Show all tasks and their numbers.
   find KEYWORD - Find descriptions containing KEYWORD, ignoring case.
   mark NUMBER - Mark a task as done.
   unmark NUMBER - Mark a task as not done.
   delete NUMBER - Delete a task.
   help - Show this help.
   bye - Exit JassaBot.

   Replace uppercase placeholders with your values. [TIME] is optional.
   Commands are lowercase. Dates: yyyy-MM-dd or d/M/yyyy. Time: 24-hour HHmm.
   Use numbers from list for mark, unmark, and delete; find renumbers its results.

   Examples:
   deadline return book /by 2019-12-02 1800
   event meeting /from 2019-12-02 1400 /to 2019-12-02 1600
   ____________________________________________________________
   ```

11. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye for now. Take your time, and keep growing.
   ____________________________________________________________
   ```


### TC-20 - Tend an empty garden and remove the last task

**Aim:** Verify the empty-list and empty-search responses, singular task count, and zero count after deleting the last task.

**Inputs and expected output:**

1. Input:

   ```text
   list
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Your garden is clear. Enjoy the breathing room.
   ____________________________________________________________
   ```

2. Input:

   ```text
   find seedlings
   ```

   Expected output:

   ```text
   ____________________________________________________________
   No matching tasks this time. Try another keyword.
   ____________________________________________________________
   ```

3. Input:

   ```text
   todo water seedlings
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Planted a new task:
     [T][ ] water seedlings
   Your garden now holds 1 task.
   ____________________________________________________________
   ```

4. Input:

   ```text
   find book
   ```

   Expected output:

   ```text
   ____________________________________________________________
   No matching tasks this time. Try another keyword.
   ____________________________________________________________
   ```

5. Input:

   ```text
   list
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Here's what's growing in your task list:
   1.[T][ ] water seedlings
   ____________________________________________________________
   ```

6. Input:

   ```text
   delete 1
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Removed this task. More room for what matters:
     [T][ ] water seedlings
   Your garden now holds 0 tasks.
   ____________________________________________________________
   ```

7. Input:

   ```text
   list
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Your garden is clear. Enjoy the breathing room.
   ____________________________________________________________
   ```

8. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye for now. Take your time, and keep growing.
   ____________________________________________________________
   ```
