# UI Test Plan

This file is the source of truth for fail-fast console UI regression tests.
Inputs and expected outputs are compared exactly, including spaces and newlines.
Java assertions are enabled so the cases also exercise internal assumptions.

## Test environment

- Setup command: `javac -d out/production/ip src/main/java/goop/*.java src/main/java/goop/command/*.java src/main/java/goop/exception/*.java src/main/java/goop/gui/*.java src/main/java/goop/parser/*.java src/main/java/goop/storage/*.java src/main/java/goop/task/*.java src/main/java/goop/ui/*.java && javac -cp out/production/ip -d out/test/ip test/goop/StorageTest.java && java -ea -cp out/production/ip:out/test/ip goop.StorageTest`
- Run command: `mkdir -p _temp/ui-test-data && rm -f _temp/ui-test-data/data/goop.txt && cd _temp/ui-test-data && java -ea -cp ../../out/production/ip goop.Goop`
- Timeout seconds: `10`

## TC-001: Exit the application

- Aim: Verify that the application greets the user and exits cleanly when given the `bye` command.

### Inputs

```text
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| ___   ___  _ __
| |  _ / _ \ / _ \| '_ \
| |_| | (_) | (_) | |_) |
 \____|\___/ \___/| .__/
                  |_|
 Hello! I'm Goop.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-002: Add and list a to-do task

- Aim: Verify that `todo` adds an incomplete to-do task and `list` displays its type, status, and index.

### Inputs

```text
todo read book
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| ___   ___  _ __
| |  _ / _ \ / _ \| '_ \
| |_| | (_) | (_) | |_) |
 \____|\___/ \___/| .__/
                  |_|
 Hello! I'm Goop.
 What can I do for you?
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

## TC-003: Mark a task as done

- Aim: Verify that `mark` updates a task and that `list` displays its completed status.

### Inputs

```text
todo read book
mark 1
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| ___   ___  _ __
| |  _ / _ \ / _ \| '_ \
| |_| | (_) | (_) | |_) |
 \____|\___/ \___/| .__/
                  |_|
 Hello! I'm Goop.
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
 Here are the tasks in your list:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-004: Unmark a completed task

- Aim: Verify that `unmark` restores a completed task to the incomplete state.

### Inputs

```text
todo read book
mark 1
unmark 1
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| ___   ___  _ __
| |  _ / _ \ / _ \| '_ \
| |_| | (_) | (_) | |_) |
 \____|\___/ \___/| .__/
                  |_|
 Hello! I'm Goop.
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
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-005: Parse a deadline date-time and add an event

- Aim: Verify that a day-first deadline date-time is parsed and reformatted while event details and task types remain visible.

### Inputs

```text
deadline do homework /by 2/12/2019 1800
event project meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| ___   ___  _ __
| |  _ / _ \ / _ \| '_ \
| |_| | (_) | (_) | |_) |
 \____|\___/ \___/| .__/
                  |_|
 Hello! I'm Goop.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] do homework (by: Dec 2 2019, 6:00 PM)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] do homework (by: Dec 2 2019, 6:00 PM)
 2.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-006: Reject incomplete and unknown commands

- Aim: Verify that empty task descriptions, unknown commands, and blank input produce actionable errors without ending the session.

### Inputs

```text
todo
blah

bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| ___   ___  _ __
| |  _ / _ \ / _ \| '_ \
| |_| | (_) | (_) | |_) |
 \____|\___/ \___/| .__/
                  |_|
 Hello! I'm Goop.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 ERROR: A todo needs a description. Use: todo <description>.
____________________________________________________________
____________________________________________________________
 ERROR: I don't recognise that command. Use todo, deadline, event, list, find, mark, unmark, delete, priority, or bye.
____________________________________________________________
____________________________________________________________
 ERROR: Please enter a command. For example: todo read book.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-007: Delete a task and renumber the list

- Aim: Verify that delete removes the selected task and closes the numbering gap for the remaining tasks.

### Inputs

```text
todo read book
deadline return book /by 2019-06-06 1800
event project meeting /from Aug 6th 2pm /to 4pm
delete 2
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| ___   ___  _ __
| |  _ / _ \ / _ \| '_ \
| |_| | (_) | (_) | |_) |
 \____|\___/ \___/| .__/
                  |_|
 Hello! I'm Goop.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Jun 6 2019, 6:00 PM)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: Jun 6 2019, 6:00 PM)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-008: Reject invalid delete commands

- Aim: Verify that delete rejects missing, malformed, empty-list, and out-of-range task numbers while allowing recovery.

### Inputs

```text
delete
delete two
delete 1
todo read book
delete 2
delete 1
delete 1
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| ___   ___  _ __
| |  _ / _ \ / _ \| '_ \
| |_| | (_) | (_) | |_) |
 \____|\___/ \___/| .__/
                  |_|
 Hello! I'm Goop.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 ERROR: The delete command needs one task number. Use: delete <number>.
____________________________________________________________
____________________________________________________________
 ERROR: The delete command accepts one positive whole number. Use: delete 1.
____________________________________________________________
____________________________________________________________
 ERROR: There are no tasks to delete. Add a task first.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 ERROR: Task 2 is outside the list. Run list and choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] read book
 Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
 ERROR: There are no tasks to delete. Add a task first.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-009: Reject invalid deadline and event details

- Aim: Verify that missing deadline/event components, unsupported date formats, and impossible calendar dates produce specific correction guidance.

### Inputs

```text
deadline submit report
deadline /by Sunday
deadline submit report /by
deadline submit report /byte Sunday
deadline submit report /by tomorrow
deadline submit report /by 31/2/2019 1800
event meeting
event /from Mon /to Tue
event meeting /from /to Tue
event meeting /from Mon
event meeting /from Mon /to
event meeting /fromage Mon /to Tue
event meeting /from Mon /today Tue
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| ___   ___  _ __
| |  _ / _ \ / _ \| '_ \
| |_| | (_) | (_) | |_) |
 \____|\___/ \___/| .__/
                  |_|
 Hello! I'm Goop.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 ERROR: A deadline needs '/by' between its description and date. Use: deadline <description> /by <date or time>.
____________________________________________________________
____________________________________________________________
 ERROR: A deadline needs a description before '/by'. Use: deadline <description> /by <date or time>.
____________________________________________________________
____________________________________________________________
 ERROR: A deadline needs a date or time after '/by'. Use: deadline <description> /by <date or time>.
____________________________________________________________
____________________________________________________________
 ERROR: A deadline needs '/by' between its description and date. Use: deadline <description> /by <date or time>.
____________________________________________________________
____________________________________________________________
 ERROR: The deadline date and time must use d/M/yyyy HHmm or yyyy-MM-dd HHmm. For example: deadline return book /by 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 ERROR: The deadline date and time must use d/M/yyyy HHmm or yyyy-MM-dd HHmm. For example: deadline return book /by 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 ERROR: An event needs '/from' before its start time. Use: event <description> /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
 ERROR: An event needs a description before '/from'. Use: event <description> /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
 ERROR: An event needs a start time after '/from'. Use: event <description> /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
 ERROR: An event needs '/to' before its end time. Use: event <description> /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
 ERROR: An event needs an end time after '/to'. Use: event <description> /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
 ERROR: An event needs '/from' before its start time. Use: event <description> /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
 ERROR: An event needs '/to' before its end time. Use: event <description> /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-010: Reject invalid task numbers

- Aim: Verify that mark and unmark reject missing, malformed, oversized, and out-of-range task numbers without ending the session.

### Inputs

```text
mark
mark two
mark 999999999999999999999
mark 1
todo read book
mark 2
unmark 0
unmark 1 extra
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| ___   ___  _ __
| |  _ / _ \ / _ \| '_ \
| |_| | (_) | (_) | |_) |
 \____|\___/ \___/| .__/
                  |_|
 Hello! I'm Goop.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 ERROR: The mark command needs one task number. Use: mark <number>.
____________________________________________________________
____________________________________________________________
 ERROR: The mark command accepts one positive whole number. Use: mark 1.
____________________________________________________________
____________________________________________________________
 ERROR: That task number is too large. Run list and choose a displayed number.
____________________________________________________________
____________________________________________________________
 ERROR: There are no tasks to mark. Add a task first.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 ERROR: Task 2 is outside the list. Run list and choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
 ERROR: The unmark command accepts one positive whole number. Use: unmark 1.
____________________________________________________________
____________________________________________________________
 ERROR: The unmark command accepts one positive whole number. Use: unmark 1.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-011: Find tasks by description keyword

- Aim: Verify that `find` displays only tasks whose descriptions contain the keyword, preserves their order, renumbers the matches, and rejects a missing keyword.

### Inputs

```text
todo read book
deadline return book /by 2019-06-06 1800
todo write essay
mark 1
mark 2
find book
find
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| ___   ___  _ __
| |  _ / _ \ / _ \| '_ \
| |_| | (_) | (_) | |_) |
 \____|\___/ \___/| .__/
                  |_|
 Hello! I'm Goop.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Jun 6 2019, 6:00 PM)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] write essay
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Jun 6 2019, 6:00 PM)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return book (by: Jun 6 2019, 6:00 PM)
____________________________________________________________
____________________________________________________________
 ERROR: The find command needs a keyword. Use: find <keyword>.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-012: Find with no matching tasks

- Aim: Verify that an empty search result displays only its heading and does not alter the full task list.

### Inputs

```text
todo read book
find missing
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| ___   ___  _ __
| |  _ / _ \ / _ \| '_ \
| |_| | (_) | (_) | |_) |
 \____|\___/ \___/| .__/
                  |_|
 Hello! I'm Goop.
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
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-013: Assign, change, and clear task priorities

- Aim: Verify that priorities appear in list and find, survive completion changes, accept numeric levels, and can be cleared.

### Inputs

```text
todo read book
priority 1 high
mark 1
find book
priority 1 3
list
priority 1 none
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| ___   ___  _ __
| |  _ / _ \ / _ \| '_ \
| |_| | (_) | (_) | |_) |
 \____|\___/ \___/| .__/
                  |_|
 Hello! I'm Goop.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 OK, I've set this task's priority to high:
   [T][ ] [high] read book
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] [high] read book
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] [high] read book
____________________________________________________________
____________________________________________________________
 OK, I've set this task's priority to low:
   [T][X] [low] read book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] [low] read book
____________________________________________________________
____________________________________________________________
 OK, I've cleared this task's priority:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-014: Reject invalid priority commands

- Aim: Verify exact guidance for missing arguments, invalid levels and task numbers, empty lists, and out-of-range indices.

### Inputs

```text
priority
priority 1 urgent
priority 0 high
priority 999999999999999999 high
priority 1 high
todo read book
priority 2 high
priority 1 high extra
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| ___   ___  _ __
| |  _ / _ \ / _ \| '_ \
| |_| | (_) | (_) | |_) |
 \____|\___/ \___/| .__/
                  |_|
 Hello! I'm Goop.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 ERROR: Use: priority <number> <high|medium|low|none> (1=high, 2=medium, 3=low).
____________________________________________________________
____________________________________________________________
 ERROR: Priority must be high, medium, low, or none (1=high, 2=medium, 3=low).
____________________________________________________________
____________________________________________________________
 ERROR: The priority command accepts one positive whole number. Use: priority 1 high.
____________________________________________________________
____________________________________________________________
 ERROR: That task number is too large. Run list and choose a displayed number.
____________________________________________________________
____________________________________________________________
 ERROR: There are no tasks to prioritize. Add a task first.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 ERROR: Task 2 is outside the list. Run list and choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
 ERROR: Use: priority <number> <high|medium|low|none> (1=high, 2=medium, 3=low).
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-015: Normalize spacing and reject extra arguments

- Aim: Verify that leading/trailing spaces, repeated spaces, and tabs are accepted, while list and bye reject extra arguments without exiting.

### Inputs

```text
  todo	read   book  
list extra
bye now
  list  
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| ___   ___  _ __
| |  _ / _ \ / _ \| '_ \
| |_| | (_) | (_) | |_) |
 \____|\___/ \___/| .__/
                  |_|
 Hello! I'm Goop.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 ERROR: The list command takes no arguments. Use: list.
____________________________________________________________
____________________________________________________________
 ERROR: The bye command takes no arguments. Use: bye.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-016: Reject repeated and reordered parameters

- Aim: Verify exact guidance for repeated delimiters and reversed event parameters, with no tasks added.

### Inputs

```text
deadline task /by 1/1/2026 1200 /by 2/1/2026 1200
event task /from Mon /from Tue /to Wed
event task /from Mon /to Tue /to Wed
event task /to Tue /from Mon
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| ___   ___  _ __
| |  _ / _ \ / _ \| '_ \
| |_| | (_) | (_) | |_) |
 \____|\___/ \___/| .__/
                  |_|
 Hello! I'm Goop.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 ERROR: Use '/by' only once per command.
____________________________________________________________
____________________________________________________________
 ERROR: Use '/from' only once per command.
____________________________________________________________
____________________________________________________________
 ERROR: Use '/to' only once per command.
____________________________________________________________
____________________________________________________________
 ERROR: An event needs '/from' before '/to'. Use: event <description> /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-017: Validate event dates and ordering

- Aim: Verify impossible dates, invalid times, equal/reversed endpoints, and incomplete dated ranges; accept a valid overnight event.

### Inputs

```text
event task /from 30/2/2026 1200 /to 1/3/2026 1300
event task /from 25:00 /to 26:00
event task /from 4pm /to 2pm
event task /from 1400 /to 14:00
event task /from 2026-09-15 /to 4pm
event task /from 2026-09-15 2300 /to 2026-09-16 0100
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| ___   ___  _ __
| |  _ / _ \ / _ \| '_ \
| |_| | (_) | (_) | |_) |
 \____|\___/ \___/| .__/
                  |_|
 Hello! I'm Goop.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 ERROR: Invalid event date or time. Use d/M/yyyy or yyyy-MM-dd with optional HHmm, or a time such as 14:00 or 2pm.
____________________________________________________________
____________________________________________________________
 ERROR: Invalid event date or time. Use d/M/yyyy or yyyy-MM-dd with optional HHmm, or a time such as 14:00 or 2pm.
____________________________________________________________
____________________________________________________________
 ERROR: An event must end after it starts. Use full dates and times for an overnight event.
____________________________________________________________
____________________________________________________________
 ERROR: An event must end after it starts. Use full dates and times for an overnight event.
____________________________________________________________
____________________________________________________________
 ERROR: Use full dates for both event endpoints.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] task (from: 2026-09-15 2300 to: 2026-09-16 0100)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-018: Reject duplicate tasks after status changes

- Aim: Verify duplicate detection ignores completion and priority, keeps the existing task intact, and allows adding it again after deletion.

### Inputs

```text
todo read book
mark 1
priority 1 high
todo read   book
list
delete 1
todo read book
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| ___   ___  _ __
| |  _ / _ \ / _ \| '_ \
| |_| | (_) | (_) | |_) |
 \____|\___/ \___/| .__/
                  |_|
 Hello! I'm Goop.
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
 OK, I've set this task's priority to high:
   [T][X] [high] read book
____________________________________________________________
____________________________________________________________
 ERROR: This task already exists. Use list to find it.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] [high] read book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][X] [high] read book
 Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```
