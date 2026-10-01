# Console UI test plan

Run from the repository root with `python test-ui/scripts/run_ui_tests.py test/ui-test-plan.md`.
Commands are run by the system shell. Console output includes both stdout and stderr.

The UI runner clears `data/victoria.txt` before each case so cases remain independent.
The save case below verifies that adding a task creates the data file.

## ToDo and list

### Aim

Confirm that a ToDo is stored and displayed with the ToDo marker.

### Inputs

```text
todo borrow book
list
bye
```

### Command

```text
java -cp build/classes/java/main victoria.Victoria --test
```

### Expected output

```text
 Nice! I've added this to your list:
   [T][ ] borrow book
 Your list now has 1 task. You're on a roll!
____________________________________________________________
 Here is your game plan:
 1.[T][ ] borrow book
____________________________________________________________
That's all for now—great work today! See you soon!
____________________________________________________________
```

## Find tasks by description keyword

### Aim

Confirm that `find` displays tasks whose descriptions contain a case-insensitive keyword and keeps their original task numbers.

### Inputs

```text
todo read book
deadline return book /by 2019-06-06
todo buy milk
find BOOK
bye
```

### Command

```text
java -cp build/classes/java/main victoria.Victoria --test
```

### Expected output

```text
 Nice! I've added this to your list:
   [T][ ] read book
 Your list now has 1 task. You're on a roll!
____________________________________________________________
 Nice! I've added this to your list:
   [D][ ] return book (by: Jun 06 2019)
 Your list now has 2 tasks. You're on a roll!
____________________________________________________________
 Nice! I've added this to your list:
   [T][ ] buy milk
 Your list now has 3 tasks. You're on a roll!
____________________________________________________________
 I found these matching tasks:
 1.[T][ ] read book
 2.[D][ ] return book (by: Jun 06 2019)
____________________________________________________________
That's all for now—great work today! See you soon!
____________________________________________________________
```

## Specific deadline and event errors

### Aim

Confirm that malformed deadline and event commands identify whether the description or timing part is missing.

### Inputs

```text
deadline /by Sunday
deadline return book
event /from 2pm /to 4pm
event meeting /from 2pm
event meeting /from /to 4pm
bye
```

### Command

```text
java -cp build/classes/java/main victoria.Victoria --test
```

### Expected output

```text
 Oops! I need a task description to add it to your list.
____________________________________________________________
 Oops! Add /by <date> so I know when this deadline is due.
____________________________________________________________
 Oops! I need a task description to add it to your list.
____________________________________________________________
 Oops! Add /to <end date> so I know when the event finishes.
____________________________________________________________
 Oops! Add a start date after /from to save the event.
____________________________________________________________
That's all for now—great work today! See you soon!
____________________________________________________________
```

## Load saved tasks

### Aim

Confirm that a task record already present on disk is loaded when Victoria starts.

### Inputs

```text
list
bye
```

### Command

```text
java -cp build/classes/java/main victoria.Victoria --test
```

### Expected output

```text
 Here is your game plan:
 1.[T][X] persistent task
____________________________________________________________
That's all for now—great work today! See you soon!
____________________________________________________________
```

## Delete a task

### Aim

Confirm that deleting a one-based task number removes the task and updates the task count.

### Inputs

```text
todo buy milk
event project meeting /from 2019-08-06 /to 2019-08-06
todo submit report
delete 2
list
bye
```

### Command

```text
java -cp build/classes/java/main victoria.Victoria --test
```

### Expected output

```text
 Nice! I've added this to your list:
   [T][ ] buy milk
 Your list now has 1 task. You're on a roll!
____________________________________________________________
 Nice! I've added this to your list:
   [E][ ] project meeting (from: Aug 06 2019 to: Aug 06 2019)
 Your list now has 2 tasks. You're on a roll!
____________________________________________________________
 Nice! I've added this to your list:
   [T][ ] submit report
 Your list now has 3 tasks. You're on a roll!
____________________________________________________________
 Done! I've removed this task:
   [E][ ] project meeting (from: Aug 06 2019 to: Aug 06 2019)
 You now have 2 tasks left. Keep it up!
____________________________________________________________
 Here is your game plan:
 1.[T][ ] buy milk
 2.[T][ ] submit report
____________________________________________________________
That's all for now—great work today! See you soon!
____________________________________________________________
```

## Ignore corrupted records

### Aim

Confirm that malformed records on disk are ignored while valid records still load.

### Inputs

```text
list
bye
```

### Command

```text
java -cp build/classes/java/main victoria.Victoria --test
```

### Expected output

```text
 Here is your game plan:
 1.[T][ ] valid tasks
____________________________________________________________
That's all for now—great work today! See you soon!
____________________________________________________________
```

## Empty list

### Aim

Confirm that listing an empty task list uses a dedicated message.

### Inputs

```text
list
bye
```

### Command

```text
java -cp build/classes/java/main victoria.Victoria --test
```

### Expected output

```text
 Your list is clear—enjoy the breathing room!
____________________________________________________________
That's all for now—great work today! See you soon!
____________________________________________________________
```

## Reject non-lowercase commands

### Aim

Confirm that standard commands must be entered in lowercase.

### Inputs

```text
LIST
Bye
bye
```

### Command

```text
java -cp build/classes/java/main victoria.Victoria --test
```

### Expected output

```text
 Oops! I didn't catch that command. Try one from the command guide above.
____________________________________________________________
 Oops! I didn't catch that command. Try one from the command guide above.
____________________________________________________________
That's all for now—great work today! See you soon!
____________________________________________________________
```

## Deadline and event

### Aim

Confirm that deadline and event commands create the correct subclasses and format deadline date values.

### Inputs

```text
deadline return book /by 2019-12-02
event project meeting /from 2019-08-06 /to 2019-08-06
bye
```

### Command

```text
java -cp build/classes/java/main victoria.Victoria --test
```

### Expected output

```text
 Nice! I've added this to your list:
   [D][ ] return book (by: Dec 02 2019)
 Your list now has 1 task. You're on a roll!
____________________________________________________________
 Nice! I've added this to your list:
   [E][ ] project meeting (from: Aug 06 2019 to: Aug 06 2019)
 Your list now has 2 tasks. You're on a roll!
____________________________________________________________
That's all for now—great work today! See you soon!
____________________________________________________________
```

## List deadlines and events on a date

### Aim

Confirm that a date query finds deadlines on that date and events spanning that date.

### Inputs

```text
deadline submit report /by 2019-10-15
event project meeting /from 2019-10-14 /to 2019-10-16
list on 2019-10-15
bye
```

### Command

```text
java -cp build/classes/java/main victoria.Victoria --test
```

### Expected output

```text
 Nice! I've added this to your list:
   [D][ ] submit report (by: Oct 15 2019)
 Your list now has 1 task. You're on a roll!
____________________________________________________________
 Nice! I've added this to your list:
   [E][ ] project meeting (from: Oct 14 2019 to: Oct 16 2019)
 Your list now has 2 tasks. You're on a roll!
____________________________________________________________
 Here is what's happening on 2019-10-15:
 1.[D][ ] submit report (by: Oct 15 2019)
 2.[E][ ] project meeting (from: Oct 14 2019 to: Oct 16 2019)
____________________________________________________________
That's all for now—great work today! See you soon!
____________________________________________________________
```

## Reject non-standard input

### Aim

Confirm that text without a recognized command prefix is rejected instead of becoming a ToDo.

### Inputs

```text
read book
bye
```

### Command

```text
java -cp build/classes/java/main victoria.Victoria --test
```

### Expected output

```text
 Oops! I didn't catch that command. Try one from the command guide above.
____________________________________________________________
That's all for now—great work today! See you soon!
____________________________________________________________
```

## Handle input errors with exceptions

### Aim

Confirm that empty task descriptions and unknown commands produce specific error messages and do not stop the session.

### Inputs

```text
todo
blah
bye
```

### Command

```text
java -cp build/classes/java/main victoria.Victoria --test
```

### Expected output

```text
 Oops! I need a task description to add it to your list.
____________________________________________________________
 Oops! I didn't catch that command. Try one from the command guide above.
____________________________________________________________
That's all for now—great work today! See you soon!
____________________________________________________________
```

### Manual GUI verification

Launch the JavaFX application with `gradlew.bat run`, enter `blah`, and press Send. The right-aligned user command
card should show `blah` with the user avatar. Before commands are entered, Victoria should display the locally restored
task list. Its left-aligned reply should be a red, bold error card showing `Oops! I don't recognize that command. Try
a standard command format.` If the restored task list is long, every task should wrap within the reply card and remain
reachable by scrolling.
