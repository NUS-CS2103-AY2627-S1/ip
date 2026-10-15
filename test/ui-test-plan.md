# TryBot UI test plan

This file defines the scripted console UI tests used by the `test-ui` project skill.

## Test-session information

- Working directory: repository root
- Java requirement: Java 25
- Setup command: `javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src/main/java -Filter *.java | Where-Object { $_.Directory.Name -ne 'gui' } | Select-Object -ExpandProperty FullName)`
- Program command: `java -cp out trybot.TryBot`
- Output comparison: exact, with CRLF normalized to LF and the final newline treated as optional
- Execution order: top to bottom; positive and negative cases are intentionally interleaved; stop immediately after the first failure
- Per-case setup: preserve any existing `data/trybot.txt` outside the test run and restore it afterwards. Remove the test data file before each case so cases do not share persisted state. UI-012 and UI-013 create their fixtures after this reset.

## Test cases

### UI-001 — Start TryBot and exit (positive)

- Aim: Verify that TryBot starts with the expected welcome screen and exits when the user enters `bye`.
- Command: `java -cp out trybot.TryBot`
- Inputs:

  ```text
  bye
  ```

- Expected output:

  ```text
  ____________________________________________________________
   _____             ____        _
  |_   _| _ __ _   _ | __ )  ___ | |_
    | |  | '__| | | ||  _ \ / _ \| __|
    | |  | |  | |_| || |_) | (_) | |_
    |_|  |_|   \__, ||____/ \___/ \__|
                |___/
  Hello! I'm TryBot.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

### UI-002 — Reject invalid commands without adding tasks (negative)

- Aim: Verify that a missing todo description and an unknown command show errors, while `list` confirms that no task was added.
- Command: `java -cp out trybot.TryBot`
- Inputs:

  ```text
  todo
  blah
  list
  bye
  ```

- Expected output:

  ```text
  ____________________________________________________________
   _____             ____        _
  |_   _| _ __ _   _ | __ )  ___ | |_
    | |  | '__| | | ||  _ \ / _ \| __|
    | |  | |  | |_| || |_) | (_) | |_
    |_|  |_|   \__, ||____/ \___/ \__|
                |___/
  Hello! I'm TryBot.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
  A todo needs a description. Try: todo read book.
  ____________________________________________________________
  ____________________________________________________________
  I do not recognise that command. Try help to show the list of commands.
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

### UI-003 — Complete a todo lifecycle (positive)

- Aim: Verify that a valid todo can be added, marked done, listed as done, unmarked, and listed as not done again.
- Command: `java -cp out trybot.TryBot`
- Inputs:

  ```text
  todo write report
  mark 1
  list
  unmark 1
  list
  bye
  ```

- Expected output:

  ```text
  ____________________________________________________________
   _____             ____        _
  |_   _| _ __ _   _ | __ )  ___ | |_
    | |  | '__| | | ||  _ \ / _ \| __|
    | |  | |  | |_| || |_) | (_) | |_
    |_|  |_|   \__, ||____/ \___/ \__|
                |___/
  Hello! I'm TryBot.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
  Got it. I've added this task:
  [T][ ] write report
  Now you have 1 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  Good work!! I've marked this task as done:
  [T][X] write report
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  1.[T][X] write report
  ____________________________________________________________
  ____________________________________________________________
  OK, I've marked this task as not done yet:
  [T][ ] write report
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  1.[T][ ] write report
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

### UI-004 — Reject malformed structured commands (negative)

- Aim: Verify that malformed deadline, event, and mark commands produce actionable messages and leave the task list empty.
- Command: `java -cp out trybot.TryBot`
- Inputs:

  ```text
  deadline report
  event planning
  mark
  list
  bye
  ```

- Expected output:

  ```text
  ____________________________________________________________
   _____             ____        _
  |_   _| _ __ _   _ | __ )  ___ | |_
    | |  | '__| | | ||  _ \ / _ \| __|
    | |  | |  | |_| || |_) | (_) | |_
    |_|  |_|   \__, ||____/ \___/ \__|
                |___/
  Hello! I'm TryBot.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
  A deadline needs /by followed by a date or time. Example: deadline report /by Friday.
  ____________________________________________________________
  ____________________________________________________________
  An event needs /from and /to time details. Example: event meeting /from Monday /to Tuesday.
  ____________________________________________________________
  ____________________________________________________________
  Mark needs one task number. Example: mark 1.
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

### UI-005 — Add deadline and event tasks (positive)

- Aim: Verify that valid deadline and event inputs create the correct task types and preserve their details in the list.
- Command: `java -cp out trybot.TryBot`
- Inputs:

  ```text
  deadline submit report /by Friday
  event team meeting /from Monday /to Tuesday
  list
  bye
  ```

- Expected output:

  ```text
  ____________________________________________________________
   _____             ____        _
  |_   _| _ __ _   _ | __ )  ___ | |_
    | |  | '__| | | ||  _ \ / _ \| __|
    | |  | |  | |_| || |_) | (_) | |_
    |_|  |_|   \__, ||____/ \___/ \__|
                |___/
  Hello! I'm TryBot.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
  Got it. I've added this task:
  [D][ ] submit report (by: Friday)
  Now you have 1 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  Got it. I've added this task:
  [E][ ] team meeting (from: Monday to: Tuesday)
  Now you have 2 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  1.[D][ ] submit report (by: Friday)
  2.[E][ ] team meeting (from: Monday to: Tuesday)
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

### UI-005A — Parse numeric dates and times (positive)

- Aim: Verify that numeric dates and times are parsed into typed date-times and displayed in a different human-readable format for deadlines and events.
- Command: `java -cp out trybot.TryBot`
- Inputs:

  ```text
  deadline return book /by 2/12/2019 1800
  event study session /from 2019-12-03 0900 /to 3/12/2019 1030
  list
  bye
  ```

- Expected output:

  ```text
  ____________________________________________________________
   _____             ____        _
  |_   _| _ __ _   _ | __ )  ___ | |_
    | |  | '__| | | ||  _ \ / _ \| __|
    | |  | |  | |_| || |_) | (_) | |_
    |_|  |_|   \__, ||____/ \___/ \__|
                |___/
  Hello! I'm TryBot.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
  Got it. I've added this task:
  [D][ ] return book (by: Dec 02 2019 18:00)
  Now you have 1 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  Got it. I've added this task:
  [E][ ] study session (from: Dec 03 2019 09:00 to: Dec 03 2019 10:30)
  Now you have 2 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  1.[D][ ] return book (by: Dec 02 2019 18:00)
  2.[E][ ] study session (from: Dec 03 2019 09:00 to: Dec 03 2019 10:30)
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

### UI-005B — Reject invalid numeric dates and event ranges (negative)

- Aim: Verify that impossible dates, invalid times, and events whose end is before their start are rejected without adding tasks.
- Command: `java -cp out trybot.TryBot`
- Inputs:

  ```text
  deadline report /by 31/02/2019
  deadline deploy /by 2019-12-01 2500
  event invalid range /from 2020-01-02 1000 /to 2020-01-01 1000
  list
  bye
  ```

- Expected output:

  ```text
  ____________________________________________________________
   _____             ____        _
  |_   _| _ __ _   _ | __ )  ___ | |_
    | |  | '__| | | ||  _ \ / _ \| __|
    | |  | |  | |_| || |_) | (_) | |_
    |_|  |_|   \__, ||____/ \___/ \__|
                |___/
  Hello! I'm TryBot.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
  Invalid date or time: 31/02/2019. Use yyyy-mm-dd or d/M/yyyy HHmm.
  ____________________________________________________________
  ____________________________________________________________
  Invalid date or time: 2019-12-01 2500. Use yyyy-mm-dd or d/M/yyyy HHmm.
  ____________________________________________________________
  ____________________________________________________________
  An event cannot end before it starts.
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

### UI-006 — Preserve valid state after invalid inputs (negative)

- Aim: Verify that malformed deadline/event commands and invalid mark/unmark arguments do not alter an already valid todo.
- Command: `java -cp out trybot.TryBot`
- Inputs:

  ```text
  todo valid task
  deadline incomplete
  event incomplete
  mark abc
  unmark 2
  list
  bye
  ```

- Expected output:

  ```text
  ____________________________________________________________
   _____             ____        _
  |_   _| _ __ _   _ | __ )  ___ | |_
    | |  | '__| | | ||  _ \ / _ \| __|
    | |  | |  | |_| || |_) | (_) | |_
    |_|  |_|   \__, ||____/ \___/ \__|
                |___/
  Hello! I'm TryBot.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
  Got it. I've added this task:
  [T][ ] valid task
  Now you have 1 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  A deadline needs /by followed by a date or time. Example: deadline report /by Friday.
  ____________________________________________________________
  ____________________________________________________________
  An event needs /from and /to time details. Example: event meeting /from Monday /to Tuesday.
  ____________________________________________________________
  ____________________________________________________________
  The task number must be a whole number. Example: mark 1.
  ____________________________________________________________
  ____________________________________________________________
  That task number does not exist. Use list to see your task numbers.
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  1.[T][ ] valid task
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

### UI-007 — Accept whitespace and case variations (positive)

- Aim: Verify that command matching is case-insensitive, surrounding whitespace is ignored, and the todo description is trimmed.
- Command: `java -cp out trybot.TryBot`
- Inputs:

  ```text
    ToDo:   buy milk  
  LiSt
  BYE!
  ```

- Expected output:

  ```text
  ____________________________________________________________
   _____             ____        _
  |_   _| _ __ _   _ | __ )  ___ | |_
    | |  | '__| | | ||  _ \ / _ \| __|
    | |  | |  | |_| || |_) | (_) | |_
    |_|  |_|   \__, ||____/ \___/ \__|
                |___/
  Hello! I'm TryBot.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
  Got it. I've added this task:
  [T][ ] buy milk
  Now you have 1 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  1.[T][ ] buy milk
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

### UI-008 — Reject empty and boundary inputs (negative)

- Aim: Verify that blank input, zero-based task numbers, out-of-range task numbers, and an empty `todo:` do not create a task.
- Command: `java -cp out trybot.TryBot`
- Inputs:

  ```text

  mark 0
  unmark 999
  todo:
  list
  bye
  ```

- Expected output:

  ```text
  ____________________________________________________________
   _____             ____        _
  |_   _| _ __ _   _ | __ )  ___ | |_
    | |  | '__| | | ||  _ \ / _ \| __|
    | |  | |  | |_| || |_) | (_) | |_
    |_|  |_|   \__, ||____/ \___/ \__|
                |___/
  Hello! I'm TryBot.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
  I need a command. Try todo, list, or bye.
  ____________________________________________________________
  ____________________________________________________________
  That task number does not exist. Use list to see your task numbers.
  ____________________________________________________________
  ____________________________________________________________
  That task number does not exist. Use list to see your task numbers.
  ____________________________________________________________
  ____________________________________________________________
  A todo needs a description. Try: todo read book.
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

### UI-009 — Delete a middle task and renumber the list (positive)

- Aim: Verify that a completed todo and deadline remain correct when a middle event is deleted, and that later tasks are renumbered.
- Command: `java -cp out trybot.TryBot`
- Inputs:

  ```text
  todo read book
  deadline return book /by June 6th
  event project meeting /from Aug 6th 2pm /to 4pm
  todo join sports club
  todo borrow book
  mark 1
  mark 2
  delete 3
  list
  bye
  ```

- Expected output:

  ```text
  ____________________________________________________________
   _____             ____        _
  |_   _| _ __ _   _ | __ )  ___ | |_
    | |  | '__| | | ||  _ \ / _ \| __|
    | |  | |  | |_| || |_) | (_) | |_
    |_|  |_|   \__, ||____/ \___/ \__|
                |___/
  Hello! I'm TryBot.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
  Got it. I've added this task:
  [T][ ] read book
  Now you have 1 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  Got it. I've added this task:
  [D][ ] return book (by: June 6th)
  Now you have 2 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  Got it. I've added this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
  Now you have 3 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  Got it. I've added this task:
  [T][ ] join sports club
  Now you have 4 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  Got it. I've added this task:
  [T][ ] borrow book
  Now you have 5 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  Good work!! I've marked this task as done:
  [T][X] read book
  ____________________________________________________________
  ____________________________________________________________
  Good work!! I've marked this task as done:
  [D][X] return book (by: June 6th)
  ____________________________________________________________
  ____________________________________________________________
  Noted. I've removed this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
  Now you have 4 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  1.[T][X] read book
  2.[D][X] return book (by: June 6th)
  3.[T][ ] join sports club
  4.[T][ ] borrow book
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

### UI-010 — Reject invalid delete commands without corrupting state (negative)

- Aim: Verify that missing, non-numeric, zero, and out-of-range delete arguments leave a valid task intact, while a later valid delete removes it.
- Command: `java -cp out trybot.TryBot`
- Inputs:

  ```text
  todo keep task
  delete
  delete abc
  delete 0
  delete 2
  list
  delete 1
  list
  bye
  ```

- Expected output:

  ```text
  ____________________________________________________________
   _____             ____        _
  |_   _| _ __ _   _ | __ )  ___ | |_
    | |  | '__| | | ||  _ \ / _ \| __|
    | |  | |  | |_| || |_) | (_) | |_
    |_|  |_|   \__, ||____/ \___/ \__|
                |___/
  Hello! I'm TryBot.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
  Got it. I've added this task:
  [T][ ] keep task
  Now you have 1 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  Delete needs one task number. Example: delete 1.
  ____________________________________________________________
  ____________________________________________________________
  The task number must be a whole number. Example: delete 1.
  ____________________________________________________________
  ____________________________________________________________
  That task number does not exist. Use list to see your task numbers.
  ____________________________________________________________
  ____________________________________________________________
  That task number does not exist. Use list to see your task numbers.
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  1.[T][ ] keep task
  ____________________________________________________________
  ____________________________________________________________
  Noted. I've removed this task:
  [T][ ] keep task
  Now you have 0 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

### UI-011 — Save task changes to disk (positive)

- Aim: Verify that adding, completing, uncompleting, and deleting tasks still produce the expected confirmations while each successful task-list change is saved.
- Command: `java -cp out trybot.TryBot`
- Inputs:

  ```text
  todo write | file
  mark 1
  unmark 1
  delete 1
  bye
  ```

- Expected output:

  ```text
  ____________________________________________________________
   _____             ____        _
  |_   _| _ __ _   _ | __ )  ___ | |_
    | |  | '__| | | ||  _ \ / _ \| __|
    | |  | |  | |_| || |_) | (_) | |_
    |_|  |_|   \__, ||____/ \___/ \__|
                |___/
  Hello! I'm TryBot.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
  Got it. I've added this task:
  [T][ ] write | file
  Now you have 1 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  Good work!! I've marked this task as done:
  [T][X] write | file
  ____________________________________________________________
  ____________________________________________________________
  OK, I've marked this task as not done yet:
  [T][ ] write | file
  ____________________________________________________________
  ____________________________________________________________
  Noted. I've removed this task:
  [T][ ] write | file
  Now you have 0 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

  - Post-test check: `data/trybot.txt` should exist and be empty after the final delete.

### UI-013 — Ignore malformed saved records (positive)

- Aim: Verify that blank and malformed records are ignored while valid escaped todo, deadline, and event records are loaded with their statuses and details intact.
- Setup command: `Set-Content -Path data/trybot.txt -Value @('', 'not a task record', 'T | 2 | invalid status', 'D | 0 | missing date', 'E | 1 | missing end | Monday', 'T | 1 | loaded \| pipe', 'D | 0 | loaded deadline | Friday', 'E | 1 | loaded event | Monday | Tuesday')`
- Command: `java -cp out trybot.TryBot`
- Inputs:

  ```text
  list
  bye
  ```

- Expected output:

  ```text
  ____________________________________________________________
   _____             ____        _
  |_   _| _ __ _   _ | __ )  ___ | |_
    | |  | '__| | | ||  _ \ / _ \| __|
    | |  | |  | |_| || |_) | (_) | |_
    |_|  |_|   \__, ||____/ \___/ \__|
                |___/
  Hello! I'm TryBot.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  1.[T][X] loaded | pipe
  2.[D][ ] loaded deadline (by: Friday)
  3.[E][X] loaded event (from: Monday to: Tuesday)
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

### UI-012 — Load saved tasks at startup (positive)

- Aim: Verify that TryBot restores todo, deadline, and event tasks with their completion states from the task data file before processing the first command.
- Setup command: `Set-Content -Path data/trybot.txt -Value @('T | 1 | loaded todo', 'D | 0 | loaded deadline | Friday', 'E | 1 | loaded event | Monday | Tuesday')`
- Command: `java -cp out trybot.TryBot`
- Inputs:

  ```text
  list
  bye
  ```

- Expected output:

  ```text
  ____________________________________________________________
   _____             ____        _
  |_   _| _ __ _   _ | __ )  ___ | |_
    | |  | '__| | | ||  _ \ / _ \| __|
    | |  | |  | |_| || |_) | (_) | |_
    |_|  |_|   \__, ||____/ \___/ \__|
                |___/
  Hello! I'm TryBot.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  1.[T][X] loaded todo
  2.[D][ ] loaded deadline (by: Friday)
  3.[E][X] loaded event (from: Monday to: Tuesday)
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

### UI-014 — Find tasks by description keyword (positive and negative)

- Aim: Verify that find matches a case-insensitive keyword within todo and deadline descriptions, preserves matching task order and status, reports no matches clearly, and rejects a missing keyword.
- Command: `java -cp out trybot.TryBot`
- Inputs:

  ```text
  todo read book
  deadline return book /by Friday
  mark 1
  find BOOK
  find laptop
  find
  bye
  ```

- Expected output:

  ```text
  ____________________________________________________________
   _____             ____        _
  |_   _| _ __ _   _ | __ )  ___ | |_
    | |  | '__| | | ||  _ \ / _ \| __|
    | |  | |  | |_| || |_) | (_) | |_
    |_|  |_|   \__, ||____/ \___/ \__|
                |___/
  Hello! I'm TryBot.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
  Got it. I've added this task:
  [T][ ] read book
  Now you have 1 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  Got it. I've added this task:
  [D][ ] return book (by: Friday)
  Now you have 2 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  Good work!! I've marked this task as done:
  [T][X] read book
  ____________________________________________________________
  ____________________________________________________________
  Here are the matching tasks in your list:
  1.[T][X] read book
  2.[D][ ] return book (by: Friday)
  ____________________________________________________________
  ____________________________________________________________
  No tasks match that keyword.
  ____________________________________________________________
  ____________________________________________________________
  Find needs a keyword. Example: find book.
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

### UI-015 — Display command help (positive)

- Aim: Verify that `help` is case-insensitive and displays usage information for every supported command.
- Command: `java -cp out trybot.TryBot`
- Inputs:

  ```text
  HeLp
  bye
  ```

- Expected output:

  ```text
  ____________________________________________________________
   _____             ____        _
  |_   _| _ __ _   _ | __ )  ___ | |_
    | |  | '__| | | ||  _ \ / _ \| __|
    | |  | |  | |_| || |_) | (_) | |_
    |_|  |_|   \__, ||____/ \___/ \__|
                |___/
  Hello! I'm TryBot.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
  Here are the commands you can use:
  help: help (shows this list of commands)
  todo: todo <task> (adds a todo task to the list)
  deadline: deadline <task> /by <date or time> (adds a deadline task)
  event: event <task> /from <start> /to <end> (adds an event task)
  list: list (shows all tasks)
  find: find <keyword> (finds tasks containing the keyword)
  tag: tag <number> <tag name> (attaches a tag to a task)
  tagdel: tagdel <number> (removes all tags from a task)
  mark: mark <number> (marks a task as done)
  unmark: unmark <number> (marks a task as not done)
  delete: delete <number> (deletes a task)
  bye: bye (exits TryBot)
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

### UI-016 — Tag tasks and preserve tags across listing (positive)

- Aim: Verify that a tag can be attached to a task and appears beside the task in the list.
- Command: `java -cp out trybot.TryBot`
- Inputs:

  ```text
  todo submit report
  deadline revise report /by Friday
  event review /from Monday /to Tuesday
  tag 1 urgent
  tag 2 school
  tag 3 work
  tagdel 1
  list
  bye
  ```

- Expected output:

  ```text
  ____________________________________________________________
   _____             ____        _
  |_   _| _ __ _   _ | __ )  ___ | |_
    | |  | '__| | | ||  _ \ / _ \| __|
    | |  | |  | |_| || |_) | (_) | |_
    |_|  |_|   \__, ||____/ \___/ \__|
                |___/
  Hello! I'm TryBot.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
  Got it. I've added this task:
  [T][ ] submit report
  Now you have 1 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  Got it. I've added this task:
  [D][ ] revise report (by: Friday)
  Now you have 2 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  Got it. I've added this task:
  [E][ ] review (from: Monday to: Tuesday)
  Now you have 3 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  Tag Created for Task 1: urgent
  ____________________________________________________________
  ____________________________________________________________
  Tag Created for Task 2: school
  ____________________________________________________________
  ____________________________________________________________
  Tag Created for Task 3: work
  ____________________________________________________________
  ____________________________________________________________
  Tags Deleted for Task 1
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  1.[T][ ] submit report
  2.[D][ ] revise report #school (by: Friday)
  3.[E][ ] review #work (from: Monday to: Tuesday)
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```
