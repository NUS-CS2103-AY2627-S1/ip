# Console UI test plan

This file is the source of truth for the `test-ui` skill. Keep test cases deterministic and update expected output only when the intended UI behavior changes.

The JavaFX-specific manual checks are documented separately in [gui-test-plan.md](gui-test-plan.md).

## Execution information

- Working directory: repository root
- Test runner command: `java test/UiTestRunner.java`
- Java version: 25
- Setup/compile command: `javac -d out src/main/java/sumo/Sumo.java src/main/java/sumo/command/Command.java src/main/java/sumo/command/ExitCommand.java src/main/java/sumo/command/FindCommand.java src/main/java/sumo/command/ListCommand.java src/main/java/sumo/command/OnCommand.java src/main/java/sumo/command/SortCommand.java src/main/java/sumo/exception/SumoException.java src/main/java/sumo/parser/Parser.java src/main/java/sumo/storage/Storage.java src/main/java/sumo/task/DateTimeDisplay.java src/main/java/sumo/task/Deadline.java src/main/java/sumo/task/Event.java src/main/java/sumo/task/Task.java src/main/java/sumo/task/TaskList.java src/main/java/sumo/task/TaskType.java src/main/java/sumo/task/Todo.java src/main/java/sumo/ui/Ui.java`
- Program launch command: `java -cp out sumo.Sumo`
- Output comparison: exact, after normalizing Windows `CRLF` line endings to `LF`; each expected block contains only the response produced after its listed input
- Test isolation: before each test case, delete `data/sumo.txt` if it exists, then launch a fresh program process unless the case explicitly requires multiple continuous sessions

The runner parses the numbered `Command/input` entries and their following fenced `Expected output` blocks. Keep that structure when adding a feature. An optional fenced `Expected `<path>` content immediately after the command` block adds a file assertion to that step. Use `First session` and `Second session` headings when a case must restart without clearing persisted state.

## Test cases

### UI-001 — Add and list a todo

- Aim: Verify that a todo command creates a typed task and that `list` displays it.
- Inputs, commands, and expected output:

  1. Command/input: `todo read book`

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [T][ ] read book
      Now you have 1 tasks in the list.
     ____________________________________________________________
     ```

  2. Command/input: `list`

     Expected output:

     ```text
     ____________________________________________________________
      Here are the tasks in your list:
      1.[T][ ] read book
     ____________________________________________________________
     ```

  3. Command/input: `bye`

     Expected output:

     ```text
     ____________________________________________________________
     Until next time. I'll be here when you need me.
     ____________________________________________________________
     ```

### UI-002 — Mark and unmark a task

- Aim: Verify that a task changes status when marked done and returns to incomplete when unmarked.
- Inputs, commands, and expected output:

  1. Command/input: `todo return book`

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [T][ ] return book
      Now you have 1 tasks in the list.
     ____________________________________________________________
     ```

  2. Command/input: `mark 1`

     Expected output:

     ```text
     ____________________________________________________________
      Confirmed. Task completed:
        [T][X] return book
     ____________________________________________________________
     ```

  3. Command/input: `unmark 1`

     Expected output:

     ```text
     ____________________________________________________________
      Plans change. I've marked this task as incomplete:
        [T][ ] return book
     ____________________________________________________________
     ```

  4. Command/input: `bye`

     Expected output:

     ```text
     ____________________________________________________________
     Until next time. I'll be here when you need me.
     ____________________________________________________________
     ```

- Notes: Run all four inputs in one continuous process so the task state is preserved.

### UI-003 — Explain invalid commands

- Aim: Verify that invalid input is handled through a user-friendly error message and does not end the program.
- Inputs, commands, and expected output:

  1. Command/input: `todo`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: Please add a description after 'todo'.
     ____________________________________________________________
     ```

  2. Command/input: `blah`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: I do not recognise that command. Try todo, deadline, event, list, sort, find, on, mark, unmark, or delete.
     ____________________________________________________________
     ```

  3. Command/input: `deadline submit report`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: Use: deadline <description> /by <date>.
     ____________________________________________________________
     ```

  4. Command/input: `event meeting /from Monday`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: Use: event <description> /from <start> /to <end>.
     ____________________________________________________________
     ```

  5. Command/input: `mark one`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: Task numbers must be whole numbers.
     ____________________________________________________________
     ```

  6. Command/input: `bye`

     Expected output:

     ```text
     ____________________________________________________________
     Until next time. I'll be here when you need me.
     ____________________________________________________________
     ```

### UI-004 — Delete a task

- Aim: Verify that deleting a task removes it from the collection and renumbers the remaining tasks.
- Inputs, commands, and expected output:

  1. Command/input: `todo read book`

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [T][ ] read book
      Now you have 1 tasks in the list.
     ____________________________________________________________
     ```

  2. Command/input: `deadline return book /by 2019-06-06`

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [D][ ] return book (by: Jun 06 2019)
      Now you have 2 tasks in the list.
     ____________________________________________________________
     ```

  3. Command/input: `delete 1`

     Expected output:

     ```text
     ____________________________________________________________
      Noted. I've removed this task:
        [T][ ] read book
      Now you have 1 tasks in the list.
     ____________________________________________________________
     ```

  4. Command/input: `list`

     Expected output:

     ```text
     ____________________________________________________________
      Here are the tasks in your list:
      1.[D][ ] return book (by: Jun 06 2019)
     ____________________________________________________________
     ```

  5. Command/input: `bye`

     Expected output:

     ```text
     ____________________________________________________________
     Until next time. I'll be here when you need me.
     ____________________________________________________________
     ```

- Notes: Run all five inputs in one continuous process so the task collection is preserved.

### UI-005 — Save every task-list change

- Aim: Verify that adding, marking, unmarking, and deleting tasks immediately rewrites `data/sumo.txt`.
- Inputs, commands, and expected output:

  1. Command/input: `todo read book`

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [T][ ] read book
      Now you have 1 tasks in the list.
     ____________________________________________________________
     ```

     Expected `data/sumo.txt` content immediately after the command:

     ```text
     T | 0 | read book
     ```

  2. Command/input: `deadline return book /by 2019-06-06`

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [D][ ] return book (by: Jun 06 2019)
      Now you have 2 tasks in the list.
     ____________________________________________________________
     ```

     Expected `data/sumo.txt` content immediately after the command:

     ```text
     T | 0 | read book
     D | 0 | return book | 2019-06-06
     ```

  3. Command/input: `event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600`

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [E][ ] project meeting (from: Aug 06 2019 2:00 PM to: Aug 06 2019 4:00 PM)
      Now you have 3 tasks in the list.
     ____________________________________________________________
     ```

     Expected `data/sumo.txt` content immediately after the command:

     ```text
     T | 0 | read book
     D | 0 | return book | 2019-06-06
     E | 0 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
     ```

  4. Command/input: `mark 1`

     Expected output:

     ```text
     ____________________________________________________________
      Confirmed. Task completed:
        [T][X] read book
     ____________________________________________________________
     ```

     Expected `data/sumo.txt` content immediately after the command:

     ```text
     T | 1 | read book
     D | 0 | return book | 2019-06-06
     E | 0 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
     ```

  5. Command/input: `unmark 1`

     Expected output:

     ```text
     ____________________________________________________________
      Plans change. I've marked this task as incomplete:
        [T][ ] read book
     ____________________________________________________________
     ```

     Expected `data/sumo.txt` content immediately after the command:

     ```text
     T | 0 | read book
     D | 0 | return book | 2019-06-06
     E | 0 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
     ```

  6. Command/input: `delete 2`

     Expected output:

     ```text
     ____________________________________________________________
      Noted. I've removed this task:
        [D][ ] return book (by: Jun 06 2019)
      Now you have 2 tasks in the list.
     ____________________________________________________________
     ```

     Expected `data/sumo.txt` content immediately after the command:

     ```text
     T | 0 | read book
     E | 0 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
     ```

  7. Command/input: `bye`

     Expected output:

     ```text
     ____________________________________________________________
     Until next time. I'll be here when you need me.
     ____________________________________________________________
     ```

- Notes: Run all seven inputs in one continuous process. After each of the first six inputs, inspect the file before sending the next input.

### UI-006 — Load tasks on restart

- Aim: Verify that restarting Sumo reconstructs every task type, its details, and its completion status from `data/sumo.txt`.
- First session inputs, commands, and expected output:

  1. Command/input: `todo read book`

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [T][ ] read book
      Now you have 1 tasks in the list.
     ____________________________________________________________
     ```

  2. Command/input: `deadline return book /by 2019-06-06`

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [D][ ] return book (by: Jun 06 2019)
      Now you have 2 tasks in the list.
     ____________________________________________________________
     ```

  3. Command/input: `event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600`

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [E][ ] project meeting (from: Aug 06 2019 2:00 PM to: Aug 06 2019 4:00 PM)
      Now you have 3 tasks in the list.
     ____________________________________________________________
     ```

  4. Command/input: `mark 2`

     Expected output:

     ```text
     ____________________________________________________________
      Confirmed. Task completed:
        [D][X] return book (by: Jun 06 2019)
     ____________________________________________________________
     ```

  5. Command/input: `bye`

     Expected output:

     ```text
     ____________________________________________________________
     Until next time. I'll be here when you need me.
     ____________________________________________________________
     ```

- Second session inputs, commands, and expected output:

  1. Command/input: `list`

     Expected output:

     ```text
     ____________________________________________________________
      Here are the tasks in your list:
      1.[T][ ] read book
      2.[D][X] return book (by: Jun 06 2019)
      3.[E][ ] project meeting (from: Aug 06 2019 2:00 PM to: Aug 06 2019 4:00 PM)
     ____________________________________________________________
     ```

  2. Command/input: `bye`

     Expected output:

     ```text
     ____________________________________________________________
     Until next time. I'll be here when you need me.
     ____________________________________________________________
     ```

- Notes: End the first process after `bye`, then launch a second process without deleting or changing `data/sumo.txt` between the two sessions.

### UI-007 — Start without an existing data folder or file

- Aim: Verify that Sumo starts with an empty task list and creates the relative `data` folder when neither the folder nor `data/sumo.txt` exists.
- Setup: Before launching Sumo, delete the `data` directory recursively if it exists.
- Inputs, commands, and expected output:

  1. Command/input: `list`

     Expected output:

     ```text
     ____________________________________________________________
      No tasks in your list yet.
     ____________________________________________________________
     ```

  2. Command/input: `bye`

     Expected output:

     ```text
     ____________________________________________________________
     Until next time. I'll be here when you need me.
     ____________________________________________________________
     ```

- Notes: Confirm that `data` exists after startup. The `sumo.txt` file is not required until a task-list change is saved.

### UI-008 — Parse and format dates and times

- Aim: Verify that day/month/year and ISO date inputs become typed values and are displayed in the requested output formats.
- Inputs, commands, and expected output:

  1. Command/input: `deadline return book /by 2/12/2019 1800`

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [D][ ] return book (by: Dec 02 2019 6:00 PM)
      Now you have 1 tasks in the list.
     ____________________________________________________________
     ```

  2. Command/input: `deadline submit report /by 2019-10-15`

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [D][ ] submit report (by: Oct 15 2019)
      Now you have 2 tasks in the list.
     ____________________________________________________________
     ```

  3. Command/input: `list`

     Expected output:

     ```text
     ____________________________________________________________
      Here are the tasks in your list:
      1.[D][ ] return book (by: Dec 02 2019 6:00 PM)
      2.[D][ ] submit report (by: Oct 15 2019)
     ____________________________________________________________
     ```

  4. Command/input: `bye`

     Expected output:

     ```text
     ____________________________________________________________
     Until next time. I'll be here when you need me.
     ____________________________________________________________
     ```

- Notes: Run all four inputs in one continuous process so the task state is preserved.

### UI-009 — List tasks on a date

- Aim: Verify that `on <date>` lists matching deadlines and events, supports both date formats, includes multi-day events, and excludes todos.
- Inputs, commands, and expected output:

  1. Command/input: `todo read book`

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [T][ ] read book
      Now you have 1 tasks in the list.
     ____________________________________________________________
     ```

  2. Command/input: `deadline return book /by 2019-10-15`

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [D][ ] return book (by: Oct 15 2019)
      Now you have 2 tasks in the list.
     ____________________________________________________________
     ```

  3. Command/input: `event project meeting /from 2019-10-14 0900 /to 2019-10-16 1700`

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [E][ ] project meeting (from: Oct 14 2019 9:00 AM to: Oct 16 2019 5:00 PM)
      Now you have 3 tasks in the list.
     ____________________________________________________________
     ```

  4. Command/input: `on 2019-10-15`

     Expected output:

     ```text
     ____________________________________________________________
      Here are the tasks on Oct 15 2019:
      1.[D][ ] return book (by: Oct 15 2019)
      2.[E][ ] project meeting (from: Oct 14 2019 9:00 AM to: Oct 16 2019 5:00 PM)
     ____________________________________________________________
     ```

  5. Command/input: `on 15/10/2019`

     Expected output:

     ```text
     ____________________________________________________________
      Here are the tasks on Oct 15 2019:
      1.[D][ ] return book (by: Oct 15 2019)
      2.[E][ ] project meeting (from: Oct 14 2019 9:00 AM to: Oct 16 2019 5:00 PM)
     ____________________________________________________________
     ```

  6. Command/input: `on 2019-10-17`

     Expected output:

     ```text
     ____________________________________________________________
      No tasks on Oct 17 2019.
     ____________________________________________________________
     ```

  7. Command/input: `bye`

     Expected output:

     ```text
     ____________________________________________________________
     Until next time. I'll be here when you need me.
     ____________________________________________________________
     ```

- Notes: Run all seven inputs in one continuous process so the task state is preserved.

### UI-010 — Find tasks by description keyword

- Aim: Verify that `find <keyword>` ignores capitalization, displays matching tasks in their original order, excludes non-matches, and validates a missing keyword.
- Inputs, commands, and expected output:

  1. Command/input: `todo read book`

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [T][ ] read book
      Now you have 1 tasks in the list.
     ____________________________________________________________
     ```

  2. Command/input: `deadline return book /by 2019-06-06`

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [D][ ] return book (by: Jun 06 2019)
      Now you have 2 tasks in the list.
     ____________________________________________________________
     ```

  3. Command/input: `todo write notes`

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [T][ ] write notes
      Now you have 3 tasks in the list.
     ____________________________________________________________
     ```

  4. Command/input: `mark 2`

     Expected output:

     ```text
     ____________________________________________________________
      Confirmed. Task completed:
        [D][X] return book (by: Jun 06 2019)
     ____________________________________________________________
     ```

  5. Command/input: `find bOoK`

     Expected output:

     ```text
     ____________________________________________________________
      Here are the matching tasks in your list:
      1.[T][ ] read book
      2.[D][X] return book (by: Jun 06 2019)
     ____________________________________________________________
     ```

  6. Command/input: `find missing`

     Expected output:

     ```text
     ____________________________________________________________
      No matching tasks found.
     ____________________________________________________________
     ```

  7. Command/input: `find`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: Please add a keyword after 'find'.
     ____________________________________________________________
     ```

  8. Command/input: `bye`

     Expected output:

     ```text
     ____________________________________________________________
     Until next time. I'll be here when you need me.
     ____________________________________________________________
     ```

- Notes: Run all eight inputs in one continuous process so the task state is preserved.

### UI-011 — Display a temporary chronological sort

- Aim: Verify that `sort` displays the full task list in ascending chronological order, groups
  incomplete and completed tasks, leaves undated todos last, preserves the normal order, and
  rejects arguments.
- Inputs, commands, and expected output:

  1. Command/input: `todo buy groceries`

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [T][ ] buy groceries
      Now you have 1 tasks in the list.
     ____________________________________________________________
     ```

  2. Command/input: `deadline submit report /by 2026-02-09`

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [D][ ] submit report (by: Feb 09 2026)
      Now you have 2 tasks in the list.
     ____________________________________________________________
     ```

  3. Command/input: `event meeting /from 2026-02-03 /to 2026-02-10`

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [E][ ] meeting (from: Feb 03 2026 to: Feb 10 2026)
      Now you have 3 tasks in the list.
     ____________________________________________________________
     ```

  4. Command/input: `mark 2`

     Expected output:

     ```text
     ____________________________________________________________
      Confirmed. Task completed:
        [D][X] submit report (by: Feb 09 2026)
     ____________________________________________________________
     ```

  5. Command/input: `sort`

     Expected output:

     ```text
     ____________________________________________________________
      Here are your tasks sorted chronologically (ascending):
      Incomplete tasks:
      1.[E][ ] meeting (from: Feb 03 2026 to: Feb 10 2026)
      2.[T][ ] buy groceries
      Completed tasks:
      3.[D][X] submit report (by: Feb 09 2026)
      Sorted view only; the normal task order is unchanged.
     ____________________________________________________________
     ```

  6. Command/input: `list`

     Expected output:

     ```text
     ____________________________________________________________
      Here are the tasks in your list:
      1.[T][ ] buy groceries
      2.[D][X] submit report (by: Feb 09 2026)
      3.[E][ ] meeting (from: Feb 03 2026 to: Feb 10 2026)
     ____________________________________________________________
     ```

  7. Command/input: `sort descending`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: Use: sort. This command takes no arguments.
     ____________________________________________________________
     ```

  8. Command/input: `bye`

     Expected output:

     ```text
     ____________________________________________________________
     Until next time. I'll be here when you need me.
     ____________________________________________________________
     ```

- Notes: Run all eight inputs in one continuous process so the task state is preserved.

### UI-012 — Accept flexible whitespace and explain missing inputs

- Aim: Verify surrounding and repeated spaces are accepted, while blank commands, missing fields, and extra arguments leave the session usable.
- Inputs, commands, and expected output:

  1. Command/input: `  todo   read    book  `

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [T][ ] read book
      Now you have 1 tasks in the list.
     ____________________________________________________________
     ```

     Expected `data/sumo.txt` content immediately after the command:

     ```text
     T | 0 | read book
     ```

  2. Command/input: `  mark   1  `

     Expected output:

     ```text
     ____________________________________________________________
      Confirmed. Task completed:
        [T][X] read book
     ____________________________________________________________
     ```

  3. Command/input: `   `

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: Please enter a command.
     ____________________________________________________________
     ```

  4. Command/input: `list extra`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: Use: list. This command takes no arguments.
     ____________________________________________________________
     ```

  5. Command/input: `bye now`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: Use: bye. This command takes no arguments.
     ____________________________________________________________
     ```

  6. Command/input: `delete`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: Please specify the number of the task to update.
     ____________________________________________________________
     ```

  7. Command/input: `delete 999999999999999999999999`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: That task number is not in your list.
     ____________________________________________________________
     ```

  8. Command/input: `mark +1`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: Task numbers must be whole numbers.
     ____________________________________________________________
     ```

  9. Command/input: `  list  `

     Expected output:

     ```text
     ____________________________________________________________
      Here are the tasks in your list:
      1.[T][X] read book
     ____________________________________________________________
     ```

     Expected `data/sumo.txt` content immediately after the command:

     ```text
     T | 1 | read book
     ```

  10. Command/input: `bye`

     Expected output:

     ```text
     ____________________________________________________________
     Until next time. I'll be here when you need me.
     ____________________________________________________________
     ```

### UI-013 — Reject malformed parameters and invalid dates

- Aim: Verify repeated, missing, or misplaced parameters, impossible dates, invalid times, and reserved pipe characters are rejected before creating a task.
- Inputs, commands, and expected output:

  1. Command/input: `deadline report /by`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: Use: deadline <description> /by <date>.
     ____________________________________________________________
     ```

  2. Command/input: `deadline report /by 2026-02-03 /by 2026-02-04`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: Use: deadline <description> /by <date>. Specify each parameter exactly once, in the shown order.
     ____________________________________________________________
     ```

  3. Command/input: `event camp /to 2026-02-04 /from 2026-02-03`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: Use: event <description> /from <start> /to <end>.
     ____________________________________________________________
     ```

  4. Command/input: `event camp /from 2026-02-03 /to 2026-02-04 /to 2026-02-05`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: Use: event <description> /from <start> /to <end>. Specify each parameter exactly once, in the shown order.
     ____________________________________________________________
     ```

  5. Command/input: `deadline report /by 2026-02-30`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: Use: deadline <description> /by <date> [HHmm]. Dates must use yyyy-MM-dd or d/M/yyyy, optionally followed by HHmm.
     ____________________________________________________________
     ```

  6. Command/input: `deadline report /by 2026-02-03 2400`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: Use: deadline <description> /by <date> [HHmm]. Dates must use yyyy-MM-dd or d/M/yyyy, optionally followed by HHmm.
     ____________________________________________________________
     ```

  7. Command/input: `on 29/2/2025`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: Use: on <date>. Dates must use yyyy-MM-dd or d/M/yyyy.
     ____________________________________________________________
     ```

  8. Command/input: `deadline report | /by 2026-02-03`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: Task text cannot contain '|'.
     ____________________________________________________________
     ```

  9. Command/input: `list`

     Expected output:

     ```text
     ____________________________________________________________
      No tasks in your list yet.
     ____________________________________________________________
     ```

  10. Command/input: `bye`

     Expected output:

     ```text
     ____________________________________________________________
     Until next time. I'll be here when you need me.
     ____________________________________________________________
     ```

### UI-014 — Reject invalid event ranges and duplicate tasks

- Aim: Verify equal and reversed event endpoints are rejected, equivalent date formats cannot bypass duplicate detection, and failed additions preserve saved data.
- Inputs, commands, and expected output:

  1. Command/input: `event camp /from 2026-02-03 /to 2026-02-03`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: An event's end must be after its start.
     ____________________________________________________________
     ```

  2. Command/input: `event camp /from 2026-02-04 /to 2026-02-03`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: An event's end must be after its start.
     ____________________________________________________________
     ```

  3. Command/input: `event camp /from 2026-02-03 1000 /to 2026-02-03 0900`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: An event's end must be after its start.
     ____________________________________________________________
     ```

  4. Command/input: `event camp /from 2026-02-03 0900 /to 2026-02-03 0900`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: An event's end must be after its start.
     ____________________________________________________________
     ```

  5. Command/input: `event   camp   /from   2026-02-03   0900   /to   2026-02-03   1000`

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [E][ ] camp (from: Feb 03 2026 9:00 AM to: Feb 03 2026 10:00 AM)
      Now you have 1 tasks in the list.
     ____________________________________________________________
     ```

  6. Command/input: `mark 1`

     Expected output:

     ```text
     ____________________________________________________________
      Confirmed. Task completed:
        [E][X] camp (from: Feb 03 2026 9:00 AM to: Feb 03 2026 10:00 AM)
     ____________________________________________________________
     ```

  7. Command/input: `event CAMP /from 3/2/2026 0900 /to 3/2/2026 1000`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: That task is already in your list.
     ____________________________________________________________
     ```

     Expected `data/sumo.txt` content immediately after the command:

     ```text
     E | 1 | camp | 2026-02-03T09:00 | 2026-02-03T10:00
     ```

  8. Command/input: `todo camp`

     Expected output:

     ```text
     ____________________________________________________________
      Understood. I've added this task:
        [T][ ] camp
      Now you have 2 tasks in the list.
     ____________________________________________________________
     ```

  9. Command/input: `todo   CAMP`

     Expected output:

     ```text
     ____________________________________________________________
      I could not complete that command: That task is already in your list.
     ____________________________________________________________
     ```

     Expected `data/sumo.txt` content immediately after the command:

     ```text
     E | 1 | camp | 2026-02-03T09:00 | 2026-02-03T10:00
     T | 0 | camp
     ```

  10. Command/input: `bye`

     Expected output:

     ```text
     ____________________________________________________________
     Until next time. I'll be here when you need me.
     ____________________________________________________________
     ```

## Test-session records

Concise results from completed runs are stored in [ui-test-history.md](ui-test-history.md). Keep execution history out of this plan so the current test definitions remain quick to read.
