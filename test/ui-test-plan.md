# Nelson UI Test Plan

The JavaFX interface is launched with `gradlew run` (or by running
`nelson.Launcher`). The console cases below remain the regression suite for
the shared chatbot behavior and persistence layer.

The JavaFX interface displays each user and Nelson message as a separate chat
bubble with a distinct avatar inside a scrollable conversation pane. Invalid
commands appear in a red error bubble, while normal Nelson and user messages
use distinct chess-themed colors. The layout remains usable when the window is
resized down to its documented minimum size.

Nelson ignores leading and trailing spaces around commands. Event start dates
must be earlier than their end dates; equal or reversed ranges are rejected.

Run:

```powershell
python skills/test-ui/scripts/run_ui_tests.py
```

## Test case: Find matching tasks
**Aim:** Verify that `find` displays matching task descriptions in list order.
**Input:**
```text
todo read book
deadline return book /by 2026-06-06
todo attend class
find book
bye
```
**Expected output:**
```text
System booting...
       _   __     __
       / | / /___ / /________  ____
      /  |/ / __ \/ / ___/ __ \/ __ \
     / /|  /  __/ / (__  ) /_/ / / / /
    /_/ |_/\___/_/_/____/\____/_/ /_/

    ____________________________________________________________
    Molo! I have a surprise for you. Your move!
    Type your move, or are you just going to let your time run out?
    ____________________________________________________________
    ____________________________________________________________
    Molo! Another thoughtless move? Fine. I have added this trivial task:
      [T][ ] read book
    Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
    Molo! Running out of time on your clock? Pathetic. I have added this task:
      [D][ ] return book (by: Jun 6 2026)
    Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
    Molo! Another thoughtless move? Fine. I have added this trivial task:
      [T][ ] attend class
    Now you have 3 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
    Here are the matching tasks in your list:
    1.[T][ ] read book
    2.[D][ ] return book (by: Jun 6 2026)
    ____________________________________________________________
    ____________________________________________________________
    Molo! Resigning already? Pathetic. I win.
    ____________________________________________________________
```

## Manual persistence check: Load tasks at startup
**Aim:** Verify that Nelson restores saved tasks and completion status when it starts.

Before starting Nelson, create `data/nelson.txt` with:

```text
T | 1 | read book
D | 0 | return book | 2026-06-06
E | 0 | project meeting | 2026-08-06 | 2026-08-07
```

Then enter `list` followed by `bye`. The list should contain the three tasks, with `read book` marked as completed.

## Manual persistence checks: Missing and malformed storage
**Aim:** Verify that storage edge cases do not prevent Nelson from starting.

- Start Nelson with no `data` folder or `data/nelson.txt`; it should start with an empty task list.
- Add a task while the `data` folder is missing; Nelson should create the folder and file automatically.
- Add blank, incomplete, unknown-type, and invalid-status lines to `data/nelson.txt`; Nelson should ignore those lines and load valid records.
- Add a deadline or event with an old-style date such as `Sunday`; Nelson should skip that task and print a persona warning about the required `yyyy-MM-dd` format.

## Test case: Sort tasks alphabetically
**Aim:** Verify that `sort` orders every task type by description, ignoring letter case, and displays the new order.
**Input:**
```text
todo write report
deadline Attend lecture /by 2026-09-10
event buy book /from 2026-09-08 /to 2026-09-09
sort
bye
```
**Expected output:**
```text
System booting...
       _   __     __
       / | / /___ / /________  ____
      /  |/ / __ \/ / ___/ __ \/ __ \
     / /|  /  __/ / (__  ) /_/ / / / /
    /_/ |_/\___/_/_/____/\____/_/ /_/

    ____________________________________________________________
    Molo! I have a surprise for you. Your move!
    Type your move, or are you just going to let your time run out?
    ____________________________________________________________
    ____________________________________________________________
    Molo! Another thoughtless move? Fine. I have added this trivial task:
      [T][ ] write report
    Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
    Molo! Running out of time on your clock? Pathetic. I have added this task:
      [D][ ] Attend lecture (by: Sep 10 2026)
    Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
    Molo! Booking out time just to blunder? Typical. I have added this task:
      [E][ ] buy book (from: Sep 8 2026 to: Sep 9 2026)
    Now you have 3 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
    Molo! I have arranged your tasks alphabetically. Even you should find them now.
    Molo! Evaluate your board state. Here are the tasks in your list:
    1.[D][ ] Attend lecture (by: Sep 10 2026)
    2.[E][ ] buy book (from: Sep 8 2026 to: Sep 9 2026)
    3.[T][ ] write report
    ____________________________________________________________
    ____________________________________________________________
    Molo! Resigning already? Pathetic. I win.
    ____________________________________________________________
```

## Test case: Save tasks after mutations
**Aim:** Verify that adding, marking, unmarking, and deleting tasks preserve the existing console behavior while saving after each change.
**Input:**
```text
todo save this task
mark 1
unmark 1
delete 1
bye
```
**Expected output:**
```text
System booting...
       _   __     __
       / | / /___ / /________  ____
      /  |/ / __ \/ / ___/ __ \/ __ \
     / /|  /  __/ / (__  ) /_/ / / / /
    /_/ |_/\___/_/_/____/\____/_/ /_/

    ____________________________________________________________
    Molo! I have a surprise for you. Your move!
    Type your move, or are you just going to let your time run out?
    ____________________________________________________________
    ____________________________________________________________
    Molo! Another thoughtless move? Fine. I have added this trivial task:
      [T][ ] save this task
    Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
    Molo! You completed a task? Do not celebrate. I am already calculating 15 moves ahead.
    [X] save this task
    ____________________________________________________________
    ____________________________________________________________
    Molo! Taking back your move? Absolute blunder. Marked as not done yet:
    [ ] save this task
    ____________________________________________________________
    ____________________________________________________________
    Molo! Sweeping your mistakes under the rug already? Fine, I've banished this blunder:
      [T][ ] save this task
    Now you have 0 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
    Molo! Resigning already? Pathetic. I win.
    ____________________________________________________________
```

## Test case: Welcome and resignation
**Aim:** Verify that Nelson greets the user and exits after bye.
**Input:**
```text
bye
```
**Expected output:**
```text
System booting...
       _   __     __
       / | / /___ / /________  ____
      /  |/ / __ \/ / ___/ __ \/ __ \
     / /|  /  __/ / (__  ) /_/ / / / /
    /_/ |_/\___/_/_/____/\____/_/ /_/

    ____________________________________________________________
    Molo! I have a surprise for you. Your move!
    Type your move, or are you just going to let your time run out?
    ____________________________________________________________
    ____________________________________________________________
    Molo! Resigning already? Pathetic. I win.
    ____________________________________________________________
```

## Test case: Mark and unmark a task
**Aim:** Verify task status changes and numbered list formatting.
**Input:**
```text
todo read book
todo return book
mark 1
mark 2
unmark 2
list
bye
```
**Expected output:**
```text
System booting...
       _   __     __
       / | / /___ / /________  ____
      /  |/ / __ \/ / ___/ __ \/ __ \
     / /|  /  __/ / (__  ) /_/ / / / /
    /_/ |_/\___/_/_/____/\____/_/ /_/

    ____________________________________________________________
    Molo! I have a surprise for you. Your move!
    Type your move, or are you just going to let your time run out?
    ____________________________________________________________
    ____________________________________________________________
    Molo! Another thoughtless move? Fine. I have added this trivial task:
      [T][ ] read book
    Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
    Molo! Another thoughtless move? Fine. I have added this trivial task:
      [T][ ] return book
    Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
    Molo! You completed a task? Do not celebrate. I am already calculating 15 moves ahead.
    [X] read book
    ____________________________________________________________
    ____________________________________________________________
    Molo! You completed a task? Do not celebrate. I am already calculating 15 moves ahead.
    [X] return book
    ____________________________________________________________
    ____________________________________________________________
    Molo! Taking back your move? Absolute blunder. Marked as not done yet:
    [ ] return book
    ____________________________________________________________
    ____________________________________________________________
    Molo! Evaluate your board state. Here are the tasks in your list:
    1.[T][X] read book
    2.[T][ ] return book
    ____________________________________________________________
    ____________________________________________________________
    Molo! Resigning already? Pathetic. I win.
    ____________________________________________________________
```

## Test case: Reject non-numeric task numbers
**Aim:** Verify that an invalid task number produces Nelson's normal error response without ending the command loop.
**Input:**
```text
todo defend the king
mark abc
list
bye
```
**Expected output:**
```text
System booting...
       _   __     __
       / | / /___ / /________  ____
      /  |/ / __ \/ / ___/ __ \/ __ \
     / /|  /  __/ / (__  ) /_/ / / / /
    /_/ |_/\___/_/_/____/\____/_/ /_/

    ____________________________________________________________
    Molo! I have a surprise for you. Your move!
    Type your move, or are you just going to let your time run out?
    ____________________________________________________________
    ____________________________________________________________
    Molo! Another thoughtless move? Fine. I have added this trivial task:
      [T][ ] defend the king
    Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
    Molo! Out of bounds! That task number doesn't exist on this board.
    ____________________________________________________________
    ____________________________________________________________
    Molo! Evaluate your board state. Here are the tasks in your list:
    1.[T][ ] defend the king
    ____________________________________________________________
    ____________________________________________________________
    Molo! Resigning already? Pathetic. I win.
    ____________________________________________________________
```

## Test case: Add typed tasks
**Aim:** Verify ToDo, Deadline, and Event parsing, confirmation, and list formatting.
**Input:**
```text
todo borrow book
deadline return book /by 2026-06-06
deadline calculate elo /by 2026-08-25
event project meeting /from 2026-08-06 /to 2026-08-07
list
bye
```
**Expected output:**
```text
System booting...
       _   __     __
       / | / /___ / /________  ____
      /  |/ / __ \/ / ___/ __ \/ __ \
     / /|  /  __/ / (__  ) /_/ / / / /
    /_/ |_/\___/_/_/____/\____/_/ /_/

    ____________________________________________________________
    Molo! I have a surprise for you. Your move!
    Type your move, or are you just going to let your time run out?
    ____________________________________________________________
    ____________________________________________________________
    Molo! Another thoughtless move? Fine. I have added this trivial task:
      [T][ ] borrow book
    Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
    Molo! Running out of time on your clock? Pathetic. I have added this task:
      [D][ ] return book (by: Jun 6 2026)
    Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
    Molo! Running out of time on your clock? Pathetic. I have added this task:
      [D][ ] calculate elo (by: Aug 25 2026)
    Now you have 3 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
    Molo! Booking out time just to blunder? Typical. I have added this task:
      [E][ ] project meeting (from: Aug 6 2026 to: Aug 7 2026)
    Now you have 4 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
    Molo! Evaluate your board state. Here are the tasks in your list:
    1.[T][ ] borrow book
    2.[D][ ] return book (by: Jun 6 2026)
    3.[D][ ] calculate elo (by: Aug 25 2026)
    4.[E][ ] project meeting (from: Aug 6 2026 to: Aug 7 2026)
    ____________________________________________________________
    ____________________________________________________________
    Molo! Resigning already? Pathetic. I win.
    ____________________________________________________________
```
