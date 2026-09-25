# UI Test Plan

This file is the source of truth for Shan's console UI tests. Test cases run in document order. Each test case starts Shan in a fresh process, while the inputs within one test case share the same application state.

## Test configuration

- Required Java version: 25
- Compile command: `javac -d <temporary-class-directory> -sourcepath src/main/java src/main/java/shan/Shan.java`
- Run command: `java -cp <temporary-class-directory> shan.Shan`
- Data file: `data/shan.txt`
- Data setup: remove the data file before each test case unless the test case
  specifies initial file contents
- Comparison: exact text after converting CRLF line endings to LF and excluding the terminal's echo of submitted input
- Failure policy: terminate the current process and stop the complete test run on the first mismatch

## Test cases

### UI-001: Manage different task types

**Aim:** Verify that Shan can add, mark, unmark, and list ToDo, Deadline, and Event tasks in one session.

**Inputs:**

1. `todo read book`
2. `mark 1`
3. `deadline return book /by 2019-12-02 18:00`
4. `event project meeting /from 2019-12-03 14:00 /to 2019-12-03 16:00`
5. `unmark 1`
6. `list`
7. `bye`

**Expected outputs:**

1. Output caused by `todo read book`:

```text
____________________________________________________________
Shan: I Gotchu. I've added this:
  [T][ ] read book
Now you have 1 tasks.
____________________________________________________________
```

2. Output caused by `mark 1`:

```text
____________________________________________________________
Shan: Well done! I have marked this task as done!
  [T][X] read book
____________________________________________________________
```

3. Output caused by `deadline return book /by 2019-12-02 18:00`:

```text
____________________________________________________________
Shan: I Gotchu. I've added this:
  [D][ ] return book (by: Dec 02 2019, 6:00 PM)
Now you have 2 tasks.
____________________________________________________________
```

4. Output caused by `event project meeting /from 2019-12-03 14:00 /to 2019-12-03 16:00`:

```text
____________________________________________________________
Shan: I Gotchu. I've added this:
  [E][ ] project meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
Now you have 3 tasks.
____________________________________________________________
```

5. Output caused by `unmark 1`:

```text
____________________________________________________________
Shan: What happened? I have unmarked this task as completed...
  [T][ ] read book
____________________________________________________________
```

6. Output caused by `list`:

```text
____________________________________________________________
Shan: Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
3.[E][ ] project meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
____________________________________________________________
```

7. Output caused by `bye`:

```text
____________________________________________________________
Shan: Bye! See you soon.
____________________________________________________________
```

### UI-006: Load saved tasks on startup

**Aim:** Verify that Shan loads saved ToDo, Deadline, and Event tasks and restores their completion states.

**Initial data file:**

```text
T | 1 | read book
D | 0 | return book | 2019-12-02 18:00
E | 0 | project meeting | 2019-12-03 14:00 | 2019-12-03 16:00
```

**Inputs:**

1. `list`
2. `bye`

**Expected outputs:**

1. Output caused by `list`:

```text
____________________________________________________________
Shan: Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
3.[E][ ] project meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
____________________________________________________________
```

2. Output caused by `bye`:

```text
____________________________________________________________
Shan: Bye! See you soon.
____________________________________________________________
```

### UI-004: Delete tasks

**Aim:** Verify that deleting removes the selected task, renumbers the remaining list, updates the task count, and handles invalid arguments.

**Inputs:**

1. `todo read book`
2. `deadline return book /by 2019-12-02 18:00`
3. `delete 1`
4. `list`
5. `delete 1`
6. `list`
7. `delete 1`
8. `delete`
9. `delete one`
10. `bye`

**Expected outputs:**

1. Output caused by `todo read book`:

```text
____________________________________________________________
Shan: I Gotchu. I've added this:
  [T][ ] read book
Now you have 1 tasks.
____________________________________________________________
```

2. Output caused by `deadline return book /by 2019-12-02 18:00`:

```text
____________________________________________________________
Shan: I Gotchu. I've added this:
  [D][ ] return book (by: Dec 02 2019, 6:00 PM)
Now you have 2 tasks.
____________________________________________________________
```

3. Output caused by `delete 1`:

```text
____________________________________________________________
Shan: Noted. I've removed this task:
  [T][ ] read book
Now you have 1 tasks.
____________________________________________________________
```

4. Output caused by `list`:

```text
____________________________________________________________
Shan: Here are the tasks in your list:
1.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
____________________________________________________________
```

5. Output caused by `delete 1`:

```text
____________________________________________________________
Shan: Noted. I've removed this task:
  [D][ ] return book (by: Dec 02 2019, 6:00 PM)
Now you have 0 tasks.
____________________________________________________________
```

6. Output caused by `list`:

```text
____________________________________________________________
Shan: Here are the tasks in your list:
____________________________________________________________
```

7. Output caused by `delete 1`:

```text
____________________________________________________________
Shan: Woopsies, this task does not exist!!
____________________________________________________________
```

8. Output caused by `delete`:

```text
____________________________________________________________
Shan: Specify a task number.
____________________________________________________________
```

9. Output caused by `delete one`:

```text
____________________________________________________________
Shan: The task number must be an int.
____________________________________________________________
```

10. Output caused by `bye`:

```text
____________________________________________________________
Shan: Bye! See you soon.
____________________________________________________________
```

### UI-005: Save tasks after list changes

**Aim:** Verify that ToDo, Deadline, and Event tasks are serialized and that marking and deleting update the data file.

**Inputs:**

1. `todo read book`
2. `deadline return book /by 2019-12-02 18:00`
3. `event project meeting /from 2019-12-03 14:00 /to 2019-12-03 16:00`
4. `mark 1`
5. `delete 2`
6. `bye`

**Expected outputs:**

1. Output caused by `todo read book`:

```text
____________________________________________________________
Shan: I Gotchu. I've added this:
  [T][ ] read book
Now you have 1 tasks.
____________________________________________________________
```

2. Output caused by `deadline return book /by 2019-12-02 18:00`:

```text
____________________________________________________________
Shan: I Gotchu. I've added this:
  [D][ ] return book (by: Dec 02 2019, 6:00 PM)
Now you have 2 tasks.
____________________________________________________________
```

3. Output caused by `event project meeting /from 2019-12-03 14:00 /to 2019-12-03 16:00`:

```text
____________________________________________________________
Shan: I Gotchu. I've added this:
  [E][ ] project meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
Now you have 3 tasks.
____________________________________________________________
```

4. Output caused by `mark 1`:

```text
____________________________________________________________
Shan: Well done! I have marked this task as done!
  [T][X] read book
____________________________________________________________
```

5. Output caused by `delete 2`:

```text
____________________________________________________________
Shan: Noted. I've removed this task:
  [D][ ] return book (by: Dec 02 2019, 6:00 PM)
Now you have 2 tasks.
____________________________________________________________
```

6. Output caused by `bye`:

```text
____________________________________________________________
Shan: Bye! See you soon.
____________________________________________________________
```

**Expected data file after each list change:**

1. After `todo read book`:

```text
T | 0 | read book
```

2. After `deadline return book /by 2019-12-02 18:00`:

```text
T | 0 | read book
D | 0 | return book | 2019-12-02 18:00
```

3. After `event project meeting /from 2019-12-03 14:00 /to 2019-12-03 16:00`:

```text
T | 0 | read book
D | 0 | return book | 2019-12-02 18:00
E | 0 | project meeting | 2019-12-03 14:00 | 2019-12-03 16:00
```

4. After `mark 1`:

```text
T | 1 | read book
D | 0 | return book | 2019-12-02 18:00
E | 0 | project meeting | 2019-12-03 14:00 | 2019-12-03 16:00
```

5. After `delete 2`:

```text
T | 1 | read book
E | 0 | project meeting | 2019-12-03 14:00 | 2019-12-03 16:00
```

### UI-002: Reject malformed task commands

**Aim:** Verify that missing descriptions and task delimiters produce helpful messages without adding tasks or terminating Shan.

**Inputs:**

1. `todo`
2. `deadline return book`
3. `deadline /by 2019-12-02 18:00`
4. `event project meeting`
5. `event project meeting /from 2019-12-03 14:00`
6. `event /from 2019-12-03 14:00 /to 2019-12-03 16:00`
7. `list`
8. `bye`

**Expected outputs:**

1. Output caused by `todo`:

```text
____________________________________________________________
Shan: The task description cannot be empty my guy.
____________________________________________________________
```

2. Output caused by `deadline return book`:

```text
____________________________________________________________
Shan: Please specify a deadline using /by.
____________________________________________________________
```

3. Output caused by `deadline /by 2019-12-02 18:00`:

```text
____________________________________________________________
Shan: The deadline description and date cannot be empty bruh.
____________________________________________________________
```

4. Output caused by `event project meeting`:

```text
____________________________________________________________
Shan: Specify the event start using /from.
____________________________________________________________
```

5. Output caused by `event project meeting /from 2019-12-03 14:00`:

```text
____________________________________________________________
Shan: Specify the event end using /to.
____________________________________________________________
```

6. Output caused by `event /from 2019-12-03 14:00 /to 2019-12-03 16:00`:

```text
____________________________________________________________
Shan: The event description, start, and end cannot be empty, lock in bro.
____________________________________________________________
```

7. Output caused by `list`:

```text
____________________________________________________________
Shan: Here are the tasks in your list:
____________________________________________________________
```

8. Output caused by `bye`:

```text
____________________________________________________________
Shan: Bye! See you soon.
____________________________________________________________
```

### UI-003: Reject empty command arguments

**Aim:** Verify that every command requiring an argument reports an error through Shan's exception handling and leaves the application running.

**Inputs:**

1. `todo`
2. `deadline`
3. `event`
4. `mark`
5. `unmark`
6. `mark one`
7. `bye`

**Expected outputs:**

1. Output caused by `todo`:

```text
____________________________________________________________
Shan: The task description cannot be empty my guy.
____________________________________________________________
```

2. Output caused by `deadline`:

```text
____________________________________________________________
Shan: The deadline description cannot be empty, else its not a deadline
____________________________________________________________
```

3. Output caused by `event`:

```text
____________________________________________________________
Shan: The event description cannot be empty...
____________________________________________________________
```

4. Output caused by `mark`:

```text
____________________________________________________________
Shan: Specify a task number.
____________________________________________________________
```

5. Output caused by `unmark`:

```text
____________________________________________________________
Shan: Specify a task number.
____________________________________________________________
```

6. Output caused by `mark one`:

```text
____________________________________________________________
Shan: The task number must be an int.
____________________________________________________________
```

7. Output caused by `bye`:

```text
____________________________________________________________
Shan: Bye! See you soon.
____________________________________________________________
```

### UI-007: Skip malformed saved tasks

**Aim:** Verify that Shan loads valid entries, skips malformed entries, and reports how many were skipped.

**Initial data file:**

```text
T | 1 | read book
D | 0 | missing deadline
D | 0 | return book |
E | 0 | meeting | 2019-12-03 14:00 | 2019-12-03 16:00 | extra
X | 0 | unknown type
T | 2 | invalid status
E | 0 | valid meeting | 2019-12-03 14:00 | 2019-12-03 16:00
```

**Expected startup warning:**

```text
____________________________________________________________
Shan: Warning: I skipped 5 invalid task entries in data/shan.txt.
____________________________________________________________
```

**Inputs:**

1. `list`
2. `bye`

**Expected outputs:**

1. Output caused by `list`:

```text
____________________________________________________________
Shan: Here are the tasks in your list:
1.[T][X] read book
2.[E][ ] valid meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
____________________________________________________________
```

2. Output caused by `bye`:

```text
____________________________________________________________
Shan: Bye! See you soon.
____________________________________________________________
```

### UI-008: Handle read and write failures

**Aim:** Verify that an unusable data path does not crash Shan and that a failed addition is rolled back.

**Initial filesystem:** `data/shan.txt` is a directory rather than a file.

**Expected startup warning:**

```text
____________________________________________________________
Shan: I couldn't read data/shan.txt. Starting with an empty task list.
____________________________________________________________
```

**Inputs:**

1. `todo read book`
2. `list`
3. `bye`

**Expected outputs:**

1. Output caused by `todo read book`:

```text
____________________________________________________________
Shan: I couldn't save your tasks to data/shan.txt.
____________________________________________________________
```

2. Output caused by `list`:

```text
____________________________________________________________
Shan: Here are the tasks in your list:
____________________________________________________________
```

3. Output caused by `bye`:

```text
____________________________________________________________
Shan: Bye! See you soon.
____________________________________________________________
```

### UI-009: Reject the save-file delimiter in task fields

**Aim:** Verify that task fields containing `|` are rejected before they can corrupt the data file.

**Inputs:**

1. `todo read | book`
2. `deadline return book /by 2019-12-02 | 18:00`
3. `event project meeting /from 2019-12-03 | 14:00 /to 2019-12-03 16:00`
4. `list`
5. `bye`

**Expected outputs:**

1. Output caused by `todo read | book`:

```text
____________________________________________________________
Shan: Task details cannot contain |.
____________________________________________________________
```

2. Output caused by `deadline return book /by 2019-12-02 | 18:00`:

```text
____________________________________________________________
Shan: Task details cannot contain |.
____________________________________________________________
```

3. Output caused by `event project meeting /from 2019-12-03 | 14:00 /to 2019-12-03 16:00`:

```text
____________________________________________________________
Shan: Task details cannot contain |.
____________________________________________________________
```

4. Output caused by `list`:

```text
____________________________________________________________
Shan: Here are the tasks in your list:
____________________________________________________________
```

5. Output caused by `bye`:

```text
____________________________________________________________
Shan: Bye! See you soon.
____________________________________________________________
```

### UI-010: Roll back unsaved task changes

**Aim:** Verify that failed mark, unmark, and delete operations leave the in-memory task list unchanged.

**Initial data file:**

```text
T | 0 | read book
T | 1 | write book
```

**After startup:** Move the data file aside and create a directory at
`data/shan.txt` so every subsequent save attempt fails.

**Inputs:**

1. `mark 1`
2. `unmark 2`
3. `delete 1`
4. `list`
5. `bye`

**Expected outputs:**

1. Output caused by `mark 1`:

```text
____________________________________________________________
Shan: I couldn't save your tasks to data/shan.txt.
____________________________________________________________
```

2. Output caused by `unmark 2`:

```text
____________________________________________________________
Shan: I couldn't save your tasks to data/shan.txt.
____________________________________________________________
```

3. Output caused by `delete 1`:

```text
____________________________________________________________
Shan: I couldn't save your tasks to data/shan.txt.
____________________________________________________________
```

4. Output caused by `list`:

```text
____________________________________________________________
Shan: Here are the tasks in your list:
1.[T][ ] read book
2.[T][X] write book
____________________________________________________________
```

5. Output caused by `bye`:

```text
____________________________________________________________
Shan: Bye! See you soon.
____________________________________________________________
```

### UI-011: Parse and validate date-times

**Aim:** Verify that Shan parses both supported date-time formats, displays them clearly, and rejects invalid ranges and calendar dates.

**Inputs:**

1. `deadline return book /by 2/12/2019 1800`
2. `event project meeting /from 2019-12-03 14:00 /to 2019-12-03 16:00`
3. `deadline impossible date /by 2019-02-30 18:00`
4. `event backwards /from 2019-12-03 16:00 /to 2019-12-03 14:00`
5. `list`
6. `bye`

**Expected outputs:**

1. Output caused by `deadline return book /by 2/12/2019 1800`:

```text
____________________________________________________________
Shan: I Gotchu. I've added this:
  [D][ ] return book (by: Dec 02 2019, 6:00 PM)
Now you have 1 tasks.
____________________________________________________________
```

2. Output caused by `event project meeting /from 2019-12-03 14:00 /to 2019-12-03 16:00`:

```text
____________________________________________________________
Shan: I Gotchu. I've added this:
  [E][ ] project meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
Now you have 2 tasks.
____________________________________________________________
```

3. Output caused by `deadline impossible date /by 2019-02-30 18:00`:

```text
____________________________________________________________
Shan: Use a valid date and time in yyyy-MM-dd HH:mm or d/M/yyyy HHmm format.
____________________________________________________________
```

4. Output caused by `event backwards /from 2019-12-03 16:00 /to 2019-12-03 14:00`:

```text
____________________________________________________________
Shan: The event end must be after its start.
____________________________________________________________
```

5. Output caused by `list`:

```text
____________________________________________________________
Shan: Here are the tasks in your list:
1.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
2.[E][ ] project meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
____________________________________________________________
```

6. Output caused by `bye`:

```text
____________________________________________________________
Shan: Bye! See you soon.
____________________________________________________________
```

**Expected final data file:**

```text
D | 0 | return book | 2019-12-02 18:00
E | 0 | project meeting | 2019-12-03 14:00 | 2019-12-03 16:00
```

### UI-012: Show dated tasks occurring on a date or within a range

**Aim:** Verify that Shan finds deadlines and overlapping events on one date or
within an inclusive date range, preserves original task numbers, and rejects
missing dates, invalid dates, and reversed ranges.

**Initial data file:**

```text
T | 0 | read book
D | 0 | return book | 2019-12-02 18:00
E | 0 | project meeting | 2019-12-02 14:00 | 2019-12-02 16:00
E | 0 | overnight event | 2019-12-02 20:00 | 2019-12-03 02:00
D | 0 | submit report | 2019-12-04 09:00
```

**Inputs:**

1. `on 2019-12-02`
2. `on 2019-12-03`
3. `on 2019-12-03 /to 2019-12-04`
4. `on 2019-12-05`
5. `on`
6. `on 2019-02-30`
7. `on 2019-12-04 /to 2019-12-03`
8. `on 2019-12-03 /to`
9. `bye`

**Expected outputs:**

1. Output caused by `on 2019-12-02`:

```text
____________________________________________________________
Shan: Here are the deadlines and events on Dec 02 2019:
2.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
3.[E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
4.[E][ ] overnight event (from: Dec 02 2019, 8:00 PM to: Dec 03 2019, 2:00 AM)
____________________________________________________________
```

2. Output caused by `on 2019-12-03`:

```text
____________________________________________________________
Shan: Here are the deadlines and events on Dec 03 2019:
4.[E][ ] overnight event (from: Dec 02 2019, 8:00 PM to: Dec 03 2019, 2:00 AM)
____________________________________________________________
```

3. Output caused by `on 2019-12-03 /to 2019-12-04`:

```text
____________________________________________________________
Shan: Here are the deadlines and events from Dec 03 2019 to Dec 04 2019:
4.[E][ ] overnight event (from: Dec 02 2019, 8:00 PM to: Dec 03 2019, 2:00 AM)
5.[D][ ] submit report (by: Dec 04 2019, 9:00 AM)
____________________________________________________________
```

4. Output caused by `on 2019-12-05`:

```text
____________________________________________________________
Shan: There are no deadlines or events on Dec 05 2019.
____________________________________________________________
```

5. Output caused by `on`:

```text
____________________________________________________________
Shan: Specify a date using yyyy-MM-dd.
____________________________________________________________
```

6. Output caused by `on 2019-02-30`:

```text
____________________________________________________________
Shan: Use a valid date in yyyy-MM-dd format.
____________________________________________________________
```

7. Output caused by `on 2019-12-04 /to 2019-12-03`:

```text
____________________________________________________________
Shan: The range end date cannot be before its start date.
____________________________________________________________
```

8. Output caused by `on 2019-12-03 /to`:

```text
____________________________________________________________
Shan: Specify both range dates using /to.
____________________________________________________________
```

9. Output caused by `bye`:

```text
____________________________________________________________
Shan: Bye! See you soon.
____________________________________________________________
```

### UI-013: Find tasks by description keyword

**Aim:** Verify that Shan finds task descriptions case-insensitively, displays
matches in their original order, renumbers the results, and rejects a missing
keyword without changing the task list.

**Inputs:**

1. `todo read book`
2. `mark 1`
3. `deadline return book /by 2019-12-02 18:00`
4. `mark 2`
5. `event project meeting /from 2019-12-03 14:00 /to 2019-12-03 16:00`
6. `find book`
7. `find BOOK`
8. `find meeting`
9. `find Dec`
10. `find`
11. `bye`

**Expected outputs:**

1. Output caused by `todo read book`:

```text
____________________________________________________________
Shan: I Gotchu. I've added this:
  [T][ ] read book
Now you have 1 tasks.
____________________________________________________________
```

2. Output caused by `mark 1`:

```text
____________________________________________________________
Shan: Well done! I have marked this task as done!
  [T][X] read book
____________________________________________________________
```

3. Output caused by `deadline return book /by 2019-12-02 18:00`:

```text
____________________________________________________________
Shan: I Gotchu. I've added this:
  [D][ ] return book (by: Dec 02 2019, 6:00 PM)
Now you have 2 tasks.
____________________________________________________________
```

4. Output caused by `mark 2`:

```text
____________________________________________________________
Shan: Well done! I have marked this task as done!
  [D][X] return book (by: Dec 02 2019, 6:00 PM)
____________________________________________________________
```

5. Output caused by
`event project meeting /from 2019-12-03 14:00 /to 2019-12-03 16:00`:

```text
____________________________________________________________
Shan: I Gotchu. I've added this:
  [E][ ] project meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
Now you have 3 tasks.
____________________________________________________________
```

6. Output caused by `find book`:

```text
____________________________________________________________
Shan: Here are the matching tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Dec 02 2019, 6:00 PM)
____________________________________________________________
```

7. Output caused by `find BOOK`:

```text
____________________________________________________________
Shan: Here are the matching tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Dec 02 2019, 6:00 PM)
____________________________________________________________
```

8. Output caused by `find meeting`:

```text
____________________________________________________________
Shan: Here are the matching tasks in your list:
1.[E][ ] project meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
____________________________________________________________
```

9. Output caused by `find Dec`:

```text
____________________________________________________________
Shan: Here are the matching tasks in your list:
____________________________________________________________
```

10. Output caused by `find`:

```text
____________________________________________________________
Shan: Specify a keyword to find.
____________________________________________________________
```

11. Output caused by `bye`:

```text
____________________________________________________________
Shan: Bye! See you soon.
____________________________________________________________
```

**Expected final data file:**

```text
T | 1 | read book
D | 1 | return book | 2019-12-02 18:00
E | 0 | project meeting | 2019-12-03 14:00 | 2019-12-03 16:00
```
