# UI test plan

## Program

- Compile with Java 25: `./gradlew classes`
- Run command: `java -cp out aster.Aster`
- The expected-output blocks below contain a command's response body. The runner
  also records the surrounding divider lines in its console transcript.

## Test case: Report input errors

### Aim

Verify that the chatbot gives an error and a correction for every invalid command
form supported by the current version.

#### Manual command

```text
todo
```

#### Expected result

```text
Error: A to-do needs a description.
Try: Enter: todo read a book
```

#### Manual command

```text
blah
```

#### Expected output

```text
Error: I don't recognise that command.
Try: Use todo, deadline, event, list, ondate, mark, unmark, delete, find, tag, or bye.
```

#### Manual command

```text

```

#### Expected result

```text
Error: No command was entered.
Try: Enter a command such as: todo read a book
```

#### Command

```text
deadline
```

#### Expected output

```text
Error: A deadline needs a /by date or time.
Try: Enter: deadline return book /by 2019-12-02
```

#### Command

```text
deadline /by 2019-12-02
```

#### Expected output

```text
Error: The deadline description is missing.
Try: Enter: deadline return book /by 2019-12-02
```

#### Command

```text
deadline return book /by
```

#### Expected output

```text
Error: The deadline date or time is missing.
Try: Add a value after /by, for example: deadline return book /by 2019-12-02
```

#### Command

```text
deadline return book /by 2019-12-02 /by 2019-12-03
```

#### Expected output

```text
Error: The /by marker should only appear once.
Try: Use exactly one /by in: deadline ... /by ...
```

#### Command

```text
deadline return book /by Sunday
```

#### Expected output

```text
Error: The deadline date or time is not recognised.
Try: Enter a date such as: deadline return book /by 2019-12-02
```

#### Command

```text
deadline return book /by 2019-10-15
```

#### Expected output

```text
Got it. I've added this task:
  [D][ ] return book (by: Oct 15 2019)
Now you have 1 tasks in the list.
```

#### Command

```text
deadline submission /by 2019-12-02
```

#### Expected output

```text
Got it. I've added this task:
  [D][ ] submission (by: Dec 02 2019)
Now you have 2 tasks in the list.
```

#### Command

```text
event project meeting /to 4pm
```

#### Expected output

```text
Error: An event needs a /from start time.
Try: Enter: event project meeting /from Mon 2pm /to 4pm
```

#### Command

```text
event project meeting /from Mon 2pm
```

#### Expected output

```text
Error: An event needs a /to end time.
Try: Enter: event project meeting /from Mon 2pm /to 4pm
```

#### Command

```text
event project meeting /to 4pm /from Mon 2pm
```

#### Expected output

```text
Error: The /from time must come before the /to time.
Try: Enter: event project meeting /from Mon 2pm /to 4pm
```

#### Command

```text
event /from Mon 2pm /to 4pm
```

#### Expected output

```text
Error: The event description is missing.
Try: Enter: event project meeting /from Mon 2pm /to 4pm
```

#### Command

```text
event project meeting /from /to 4pm
```

#### Expected output

```text
Error: The event start time is missing.
Try: Add a value after /from.
```

#### Command

```text
event project meeting /from Mon 2pm /to
```

#### Expected output

```text
Error: The event end time is missing.
Try: Add a value after /to.
```

#### Command

```text
event project meeting /from 2019-12-02 14:00 /to 2019-12-02 14:00
```

#### Expected output

```text
Error: The event start date/time must be earlier than the end date/time.
Try: Use: event project meeting /from 2019-12-02 14:00 /to 2019-12-02 16:00
```

#### Command

```text
event project meeting /from Mon 2pm /from Tue 3pm /to 4pm
```

#### Expected output

```text
Error: The /from marker should only appear once.
Try: Use exactly one /from in: event ... /from ...
```

#### Command

```text
event project meeting /from Mon 2pm /to 4pm /to 5pm
```

#### Expected output

```text
Error: The /to marker should only appear once.
Try: Use exactly one /to in: event ... /to ...
```

#### Command

```text
mark
```

#### Expected output

```text
Error: A task number is required.
Try: Enter: mark 1
```

#### Command

```text
delete
```

#### Expected output

```text
Error: A task number is required.
Try: Enter: delete 1
```

#### Command

```text
todo read book
```

#### Expected output

```text
Got it. I've added this task:
  [T][ ] read book
Now you have 3 tasks in the list.
```

#### Command

```text
todo read book
```

#### Expected output

```text
Error: That task already exists in your list.
Try: Use list to review existing tasks before adding another.
```

#### Command

```text
unmark
```

#### Expected output

```text
Error: A task number is required.
Try: Enter: unmark 1
```

#### Command

```text
mark one
```

#### Expected output

```text
Error: The task number must contain digits only.
Try: Enter: mark 1
```

#### Command

```text
mark 1 2
```

#### Expected output

```text
Error: Only one task number is allowed.
Try: Enter: mark 1
```

#### Command

```text
mark 4
```

#### Expected output

```text
Error: That task number is not in the list.
Try: Enter a number from 1 to 3.
```

#### Command

```text
delete
```

#### Expected output

```text
Error: A task number is required.
Try: Enter: delete 1
```

#### Command

```text
delete one
```

#### Expected output

```text
Error: The task number must contain digits only.
Try: Enter: delete 1
```

#### Command

```text
delete 4
```

#### Expected output

```text
Error: That task number is not in the list.
Try: Enter a number from 1 to 3.
```

#### Command

```text
bye
```

#### Expected output

```text
Until next time - keep your goals in orbit!
```

## Test case: Delete a task

### Aim

Verify that the `delete` command removes the specified task and renumbers the
remaining tasks consecutively.

#### Command

```text
todo read book
```

#### Expected output

```text
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
```

#### Command

```text
deadline return book /by 2019-12-02
```

#### Expected output

```text
Got it. I've added this task:
  [D][ ] return book (by: Dec 02 2019)
Now you have 2 tasks in the list.
```

#### Command

```text
event project meeting /from Mon 2pm /to 4pm
```

#### Expected output

```text
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
```

#### Command

```text
delete 2
```

#### Expected output

```text
Noted. I've removed this task:
  [D][ ] return book (by: Dec 02 2019)
Now you have 2 tasks in the list.
```

#### Command

```text
list
```

#### Expected output

```text
Here are the tasks in your list:
1.[T][ ] read book
2.[E][ ] project meeting (from: Mon 2pm to: 4pm)
```

#### Command

```text
bye
```

#### Expected output

```text
Until next time - keep your goals in orbit!
```

## Test case: Add, mark, and unmark to-do tasks

### Aim

Verify that the `todo` command adds an incomplete to-do task and that to-do tasks
can be marked done, returned to not done, and displayed with the correct type and
status markers.

#### Command

```text
todo read book
```

#### Expected output

```text
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
```

#### Command

```text
todo return book
```

#### Expected output

```text
Got it. I've added this task:
  [T][ ] return book
Now you have 2 tasks in the list.
```

#### Command

```text
todo buy bread
```

#### Expected output

```text
Got it. I've added this task:
  [T][ ] buy bread
Now you have 3 tasks in the list.
```

#### Command

```text
mark 1
```

#### Expected output

```text
Nice! I've marked this task as done:
  [T][X] read book
```

#### Command

```text
mark 2
```

#### Expected output

```text
Nice! I've marked this task as done:
  [T][X] return book
```

#### Command

```text
unmark 2
```

#### Expected output

```text
OK, I've marked this task as not done yet:
  [T][ ] return book
```

#### Command

```text
list
```

#### Expected output

```text
Here are the tasks in your list:
1.[T][X] read book
2.[T][ ] return book
3.[T][ ] buy bread
```

#### Command

```text
bye
```

#### Expected output

```text
Until next time - keep your goals in orbit!
```

## Test case: Add and list events

### Aim

Verify that the `event` command stores its description, `/from` text, and `/to` text
without interpreting the dates or times.

#### Command

```text
event project meeting /from Mon 2pm /to 4pm
```

#### Expected output

```text
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 1 tasks in the list.
```

#### Command

```text
event orientation week /from 4/10/2019 /to 11/10/2019
```

#### Expected output

```text
Got it. I've added this task:
  [E][ ] orientation week (from: 4/10/2019 to: 11/10/2019)
Now you have 2 tasks in the list.
```

#### Command

```text
list
```

#### Expected output

```text
Here are the tasks in your list:
1.[E][ ] project meeting (from: Mon 2pm to: 4pm)
2.[E][ ] orientation week (from: 4/10/2019 to: 11/10/2019)
```

#### Command

```text
bye
```

#### Expected output

```text
Until next time - keep your goals in orbit!
```

## Test case: Add and list deadlines

### Aim

Verify that the `deadline` command accepts a recognised date or date-time for
`/by` and rejects free-form text that cannot be parsed as one.

#### Command

```text
deadline return book /by 2019-10-15
```

#### Expected output

```text
Got it. I've added this task:
  [D][ ] return book (by: Oct 15 2019)
Now you have 1 tasks in the list.
```

#### Command

```text
deadline do homework /by 2019-10-20 1800
```

#### Expected output

```text
Got it. I've added this task:
  [D][ ] do homework (by: Oct 20 2019, 6:00 PM)
Now you have 2 tasks in the list.
```

#### Command

```text
list
```

#### Expected output

```text
Here are the tasks in your list:
1.[D][ ] return book (by: Oct 15 2019)
2.[D][ ] do homework (by: Oct 20 2019, 6:00 PM)
```

#### Command

```text
deadline finish essay /by no idea :-p
```

#### Expected output

```text
Error: The deadline date or time is not recognised.
Try: Enter a date such as: deadline return book /by 2019-12-02
```

#### Command

```text
bye
```

#### Expected output

```text
Until next time - keep your goals in orbit!
```

## Test case: Filter tasks by date

### Aim

Verify that the `ondate` command prints the deadlines and events that fall on the
selected date while ignoring task items unrelated to that date.

#### Command

```text
deadline return book /by 2019-10-15
```

#### Expected output

```text
Got it. I've added this task:
  [D][ ] return book (by: Oct 15 2019)
Now you have 1 tasks in the list.
```

#### Command

```text
event orientation week /from 2019-10-04 /to 2019-10-11
```

#### Expected output

```text
Got it. I've added this task:
  [E][ ] orientation week (from: 2019-10-04 to: 2019-10-11)
Now you have 2 tasks in the list.
```

#### Command

```text
ondate 2019-10-04
```

#### Expected output

```text
Here are the tasks on Oct 04 2019:
1.[E][ ] orientation week (from: 2019-10-04 to: 2019-10-11)
```

#### Command

```text
bye
```

#### Expected output

```text
Until next time - keep your goals in orbit!
```

## Test case: Save after task-list changes

### Aim

Verify that task-changing commands still return the expected responses while
triggering the save path for the happy path.

#### Command

```text
todo read book
```

#### Expected output

```text
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
```

#### Command

```text
mark 1
```

#### Expected output

```text
Nice! I've marked this task as done:
  [T][X] read book
```

#### Command

```text
delete 1
```

#### Expected output

```text
Noted. I've removed this task:
  [T][X] read book
Now you have 0 tasks in the list.
```

#### Command

```text
bye
```

#### Expected output

```text
Until next time - keep your goals in orbit!
```

## Test case: Find tasks by keyword

### Aim

Verify that the `find` command lists tasks whose description contains the given
keyword, ignoring case, that it reports an empty result when nothing matches, and
that it reports an error when no keyword is given.

#### Command

```text
todo read book
```

#### Expected output

```text
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
```

#### Command

```text
deadline return book /by 2026-06-06
```

#### Expected output

```text
Got it. I've added this task:
  [D][ ] return book (by: Jun 06 2026)
Now you have 2 tasks in the list.
```

#### Command

```text
mark 1
```

#### Expected output

```text
Nice! I've marked this task as done:
  [T][X] read book
```

#### Command

```text
find book
```

#### Expected output

```text
Here are the matching tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2026)
```

#### Command

```text
find laptop
```

#### Expected output

```text
Here are the matching tasks in your list:
```

#### Command

```text
find
```

#### Expected output

```text
Error: A find command needs a keyword.
Try: Enter: find book
```

#### Command

```text
bye
```

#### Expected output

```text
Until next time - keep your goals in orbit!
```

## Test case: Tag items

### Aim

Verify that the `tag` command adds hashtag labels to existing tasks, rejects missing
or malformed tags, and shows tags in list output.

#### Command

```text
todo read book
```

#### Expected output

```text
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
```

#### Command

```text
tag 1 #fun
```

#### Expected output

```text
Got it. I've tagged this task:
  [T][ ] read book #fun
```

#### Command

```text
tag 1 fun
```

#### Expected output

```text
Error: Tags must start with # and use letters, digits, - or _.
Try: Enter: tag 1 #fun
```

#### Command

```text
tag 1
```

#### Expected output

```text
Error: A tag value is required.
Try: Enter: tag 1 #fun
```

#### Command

```text
list
```

#### Expected output

```text
Here are the tasks in your list:
1.[T][ ] read book #fun
```

#### Command

```text
bye
```

#### Expected output

```text
Until next time - keep your goals in orbit!
```

## Manual storage robustness checks

### Aim

Validate the save/load edge cases that are not covered by the default automated
runner because the runner clears the `data` directory before each case. These
checks are intended for a local manual run when verifying file persistence.

#### Manual command

```text
list
```

#### Expected result

```text
Here are the tasks in your list:
```

#### Manual setup

Create a malformed file at `data/aster.txt` such as:

```text
T | 1 | read book
D | maybe | missing by field
E | 0 | event | 2pm
```

Then start the chatbot and run:

```text
list
```

#### Expected output

```text
Here are the tasks in your list:
```

The malformed file should be ignored and, if enough lines are unreadable, moved to
`data/aster.txt.corrupt.<timestamp>` before startup continues with an empty list.
The default UI runner intentionally removes `data` before each case, so this
validation is best performed as a separate manual smoke test.
