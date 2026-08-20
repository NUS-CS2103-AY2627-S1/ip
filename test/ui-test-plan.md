# Mochi UI Test Plan

This plan records the test cases used to verify Mochi's text output.
Run them with the `test-ui` skill (`.codex/skills/test-ui/test-ui.ps1`).
Machine-readable input/expected file pairs for each case are in `test/cases/`.

## Test 1: Greeting and exit

- **Aim**: Verify Mochi greets the user and says goodbye on `bye`.
- **Inputs**:
  ```
  bye
  ```
- **Expected output**:
  ```
  ____________________________________________________________
      __  ___           __    _
     /  |/  /___  _____/ /_  (_)
    / /|_/ / __ \/ ___/ __ \/ /
   / /  / / /_/ / /__/ / / / /
  /_/  /_/\____/\___/_/ /_/_/
  Hello! I'm Mochi.
  What can I do for you?
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test 2: Add all task types and list

- **Aim**: Verify `todo`, `deadline`, and `event` tasks are added and shown correctly in `list`.
  Dates are stored as `LocalDate` and displayed in `MMM d yyyy` format.
- **Inputs**:
  ```
  todo borrow book
  deadline return book /by 2019-12-02
  event project meeting /from 2019-10-15 /to 2019-10-16
  list
  bye
  ```
- **Expected output**:
  ```
  ____________________________________________________________
      __  ___           __    _
     /  |/  /___  _____/ /_  (_)
    / /|_/ / __ \/ ___/ __ \/ /
   / /  / / /_/ / /__/ / / / /
  /_/  /_/\____/\___/_/ /_/_/
  Hello! I'm Mochi.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
   Got it. I've added this task:
     [T][ ] borrow book
   Now you have 1 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
   Got it. I've added this task:
     [D][ ] return book (by: Dec 2 2019)
   Now you have 2 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
   Got it. I've added this task:
     [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
   Now you have 3 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
   Here are the tasks in your list:
   1.[T][ ] borrow book
   2.[D][ ] return book (by: Dec 2 2019)
   3.[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test 3: Mark and unmark

- **Aim**: Verify `mark` and `unmark` update a task's done status.
- **Inputs**:
  ```
  todo read book
  todo return book
  mark 2
  list
  unmark 2
  list
  bye
  ```
- **Expected output**:
  ```
  ____________________________________________________________
      __  ___           __    _
     /  |/  /___  _____/ /_  (_)
    / /|_/ / __ \/ ___/ __ \/ /
   / /  / / /_/ / /__/ / / / /
  /_/  /_/\____/\___/_/ /_/_/
  Hello! I'm Mochi.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
   Got it. I've added this task:
     [T][ ] read book
   Now you have 1 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
   Got it. I've added this task:
     [T][ ] return book
   Now you have 2 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
   Nice! I've marked this task as done:
     [T][X] return book
  ____________________________________________________________
  ____________________________________________________________
   Here are the tasks in your list:
   1.[T][ ] read book
   2.[T][X] return book
  ____________________________________________________________
  ____________________________________________________________
   OK, I've marked this task as not done yet:
     [T][ ] return book
  ____________________________________________________________
  ____________________________________________________________
   Here are the tasks in your list:
   1.[T][ ] read book
   2.[T][ ] return book
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test 4: Deadline with ISO date

- **Aim**: Verify deadline dates are parsed from `yyyy-mm-dd` and displayed as `MMM d yyyy`.
- **Inputs**:
  ```
  deadline do homework /by 2019-10-15
  list
  bye
  ```
- **Expected output**:
  ```
  ____________________________________________________________
      __  ___           __    _
     /  |/  /___  _____/ /_  (_)
    / /|_/ / __ \/ ___/ __ \/ /
   / /  / / /_/ / /__/ / / / /
  /_/  /_/\____/\___/_/ /_/_/
  Hello! I'm Mochi.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
   Got it. I've added this task:
     [D][ ] do homework (by: Oct 15 2019)
   Now you have 1 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
   Here are the tasks in your list:
   1.[D][ ] do homework (by: Oct 15 2019)
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test 5: Empty list

- **Aim**: Verify `list` on an empty task list produces no task lines.
- **Inputs**:
  ```
  list
  bye
  ```
- **Expected output**:
  ```
  ____________________________________________________________
      __  ___           __    _
     /  |/  /___  _____/ /_  (_)
    / /|_/ / __ \/ ___/ __ \/ /
   / /  / / /_/ / /__/ / / / /
  /_/  /_/\____/\___/_/ /_/_/
  Hello! I'm Mochi.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
   Here are the tasks in your list:
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test 6: Invalid commands (interleaved with valid ones)

- **Aim**: Verify Mochi rejects unknown commands and malformed mark/deadline/event
  commands with specific error messages, and that invalid commands do not corrupt
  the stored tasks. Positive and negative commands are interleaved.
- **Inputs**:
  ```
  todo read book
  blah
  todo
  mark abc
  mark 5
  deadline return book
  event project meeting
  event project meeting /from Mon 2pm
  list
  bye
  ```
- **Expected output**:
  ```
  ____________________________________________________________
      __  ___           __    _
     /  |/  /___  _____/ /_  (_)
    / /|_/ / __ \/ ___/ __ \/ /
   / /  / / /_/ / /__/ / / / /
  /_/  /_/\____/\___/_/ /_/_/
  Hello! I'm Mochi.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
   Got it. I've added this task:
     [T][ ] read book
   Now you have 1 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
   OOPS!!! I'm sorry, but I don't know what that means :-(
  ____________________________________________________________
  ____________________________________________________________
   OOPS!!! The description of a todo cannot be empty.
  ____________________________________________________________
  ____________________________________________________________
   OOPS!!! Please give a task number, e.g., mark 2
  ____________________________________________________________
  ____________________________________________________________
   OOPS!!! There is no task number 5 in the list.
  ____________________________________________________________
  ____________________________________________________________
   OOPS!!! Please add the deadline with /by, e.g., deadline return book /by Sunday
  ____________________________________________________________
  ____________________________________________________________
   OOPS!!! Please add the start time with /from, e.g., event project meeting /from Mon 2pm /to 4pm
  ____________________________________________________________
  ____________________________________________________________
   OOPS!!! Please add the end time with /to, e.g., event project meeting /from Mon 2pm /to 4pm
  ____________________________________________________________
  ____________________________________________________________
   Here are the tasks in your list:
   1.[T][ ] read book
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test 7: Empty task descriptions

- **Aim**: Verify each task type rejects a missing description.
- **Inputs**:
  ```
  todo
  deadline
  event
  list
  bye
  ```
- **Expected output**:
  ```
  ____________________________________________________________
      __  ___           __    _
     /  |/  /___  _____/ /_  (_)
    / /|_/ / __ \/ ___/ __ \/ /
   / /  / / /_/ / /__/ / / / /
  /_/  /_/\____/\___/_/ /_/_/
  Hello! I'm Mochi.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
   OOPS!!! The description of a todo cannot be empty.
  ____________________________________________________________
  ____________________________________________________________
   OOPS!!! The description of a deadline cannot be empty.
  ____________________________________________________________
  ____________________________________________________________
   OOPS!!! The description of an event cannot be empty.
  ____________________________________________________________
  ____________________________________________________________
   Here are the tasks in your list:
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test 8: Delete a task

- **Aim**: Verify `delete N` removes the task, renumbers the rest, and reports the new count.
  Includes deadlines and events with ISO dates.
- **Inputs**:
  ```
  todo read book
  deadline return book /by 2019-06-06
  event project meeting /from 2019-08-06 /to 2019-08-07
  todo join sports club
  todo borrow book
  list
  delete 3
  list
  bye
  ```
- **Expected output**:
  ```
  ____________________________________________________________
      __  ___           __    _
     /  |/  /___  _____/ /_  (_)
    / /|_/ / __ \/ ___/ __ \/ /
   / /  / / /_/ / /__/ / / / /
  /_/  /_/\____/\___/_/ /_/_/
  Hello! I'm Mochi.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
   Got it. I've added this task:
     [T][ ] read book
   Now you have 1 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
   Got it. I've added this task:
     [D][ ] return book (by: Jun 6 2019)
   Now you have 2 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
   Got it. I've added this task:
     [E][ ] project meeting (from: Aug 6 2019 to: Aug 7 2019)
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
   Here are the tasks in your list:
   1.[T][ ] read book
   2.[D][ ] return book (by: Jun 6 2019)
   3.[E][ ] project meeting (from: Aug 6 2019 to: Aug 7 2019)
   4.[T][ ] join sports club
   5.[T][ ] borrow book
  ____________________________________________________________
  ____________________________________________________________
   Noted. I've removed this task:
     [E][ ] project meeting (from: Aug 6 2019 to: Aug 7 2019)
   Now you have 4 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
   Here are the tasks in your list:
   1.[T][ ] read book
   2.[D][ ] return book (by: Jun 6 2019)
   3.[T][ ] join sports club
   4.[T][ ] borrow book
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test 9: Invalid delete commands

- **Aim**: Verify `delete` rejects an out-of-range, zero, non-numeric, or missing
  task number, and that the task list is left unchanged.
- **Inputs**:
  ```
  todo read book
  delete 2
  delete 0
  delete abc
  delete
  list
  bye
  ```
- **Expected output**:
  ```
  ____________________________________________________________
      __  ___           __    _
     /  |/  /___  _____/ /_  (_)
    / /|_/ / __ \/ ___/ __ \/ /
   / /  / / /_/ / /__/ / / / /
  /_/  /_/\____/\___/_/ /_/_/
  Hello! I'm Mochi.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
   Got it. I've added this task:
     [T][ ] read book
   Now you have 1 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
   OOPS!!! There is no task number 2 in the list.
  ____________________________________________________________
  ____________________________________________________________
   OOPS!!! There is no task number 0 in the list.
  ____________________________________________________________
  ____________________________________________________________
   OOPS!!! Please give a task number, e.g., mark 2
  ____________________________________________________________
  ____________________________________________________________
   OOPS!!! Please give a task number, e.g., mark 2
  ____________________________________________________________
  ____________________________________________________________
   Here are the tasks in your list:
   1.[T][ ] read book
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test 10: Deadline date formatting

- **Aim**: Verify a deadline date given as `yyyy-mm-dd` is displayed as `MMM d yyyy`.
- **Inputs**:
  ```
  deadline return book /by 2019-12-02
  list
  bye
  ```
- **Expected output**:
  ```
  ____________________________________________________________
      __  ___           __    _
     /  |/  /___  _____/ /_  (_)
    / /|_/ / __ \/ ___/ __ \/ /
   / /  / / /_/ / /__/ / / / /
  /_/  /_/\____/\___/_/ /_/_/
  Hello! I'm Mochi.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
   Got it. I've added this task:
     [D][ ] return book (by: Dec 2 2019)
   Now you have 1 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
   Here are the tasks in your list:
   1.[D][ ] return book (by: Dec 2 2019)
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test 11: Invalid date format

- **Aim**: Verify that non-ISO dates (e.g., `Sunday`, `Mon`) are rejected with a
  helpful error message.
- **Inputs**:
  ```
  deadline return book /by Sunday
  event meeting /from Mon /to Tue
  bye
  ```
- **Expected output**:
  ```
  ____________________________________________________________
      __  ___           __    _
     /  |/  /___  _____/ /_  (_)
    / /|_/ / __ \/ ___/ __ \/ /
   / /  / / /_/ / /__/ / / / /
  /_/  /_/\____/\___/_/ /_/_/
  Hello! I'm Mochi.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
   OOPS!!! The date must be in yyyy-mm-dd format, e.g., 2019-10-15
  ____________________________________________________________
  ____________________________________________________________
   OOPS!!! The dates must be in yyyy-mm-dd format, e.g., 2019-10-15
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test 12: Find tasks by keyword

- **Aim**: Verify `find` returns tasks whose description contains the keyword.
- **Inputs**:
  ```
  todo read book
  deadline return book /by 2019-12-02
  event project meeting /from 2019-10-15 /to 2019-10-16
  find book
  bye
  ```
- **Expected output**:
  ```
  ____________________________________________________________
      __  ___           __    _
     /  |/  /___  _____/ /_  (_)
    / /|_/ / __ \/ ___/ __ \/ /
   / /  / / /_/ / /__/ / / / /
  /_/  /_/\____/\___/_/ /_/_/
  Hello! I'm Mochi.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
   Got it. I've added this task:
     [T][ ] read book
   Now you have 1 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
   Got it. I've added this task:
     [D][ ] return book (by: Dec 2 2019)
   Now you have 2 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
   Got it. I've added this task:
     [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
   Now you have 3 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
   Here are the matching tasks in your list:
   1.[T][ ] read book
   2.[D][ ] return book (by: Dec 2 2019)
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test 13: Find with no results

- **Aim**: Verify `find` with a keyword that matches no tasks shows an empty list.
- **Inputs**:
  ```
  todo read book
  find swim
  bye
  ```
- **Expected output**:
  ```
  ____________________________________________________________
      __  ___           __    _
     /  |/  /___  _____/ /_  (_)
    / /|_/ / __ \/ ___/ __ \/ /
   / /  / / /_/ / /__/ / / / /
  /_/  /_/\____/\___/_/ /_/_/
  Hello! I'm Mochi.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
   Got it. I've added this task:
     [T][ ] read book
   Now you have 1 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
   Here are the matching tasks in your list:
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```
