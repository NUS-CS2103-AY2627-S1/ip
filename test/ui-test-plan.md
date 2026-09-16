# Command-line UI test plan

## Test environment

- Working directory: repository root
- Build command: `./gradlew classes`
- Launch command: `java -cp build/classes/java/main kibo.Kibo`
- Timeout: 5 seconds
- Setup/reset: start a fresh process for each test case. Before the test session, back up any
  existing `data/duke.txt` file and restore it when testing finishes. Before UI-001 through
  UI-004 and UI-006, replace `data/duke.txt` with an empty file so the program starts with no
  tasks. Before UI-005, UI-007, UI-010, and UI-011, replace it with the fixture shown in that
  case. The program writes test data to `data/duke.txt` while each case runs.
- Output comparison: exact equality after converting CRLF to LF and ignoring one final newline
- Expected exit behavior: exit normally after receiving `bye`

JUnit tests are a separate suite: run `./gradlew check` from the repository root. Gradle runs
them in `build/test-work`, with disposable storage fixtures and sequential execution. This
does not change the repository-root working directory used by the console cases below.
See [automated-testing.md](automated-testing.md) for coverage reports and remaining gaps.

## UI-001: Add and display all task types

Aim: Verify creation, display, marking, and unmarking of all task types, including formatted
deadline dates.

Input, in order:

```text
todo borrow book
deadline do homework /by 2019-12-02
event project meeting /from Mon 2pm /to 4pm
mark 1
list
unmark 1
list
bye
```

Expected program output:

```text
 _  __ _ _           
| |/ /(_) |__   ___  
| ' / | | '_ \ / _ \
| . \ | | |_) | (_) |
|_|\_\|_|_.__/ \___/
Hey! I'm Kibo, your pocket cheerleader.
Small steps, brighter days!
Ready? Try 'list' or 'todo read book'.
____________________________________________________________
____________________________________________________________
 On the list! Let's make it happen:
   [T][ ] borrow book
 You've got 1 task on your list.
____________________________________________________________
____________________________________________________________
 On the list! Let's make it happen:
   [D][ ] do homework (by: Dec 02 2019)
 You've got 2 tasks on your list.
____________________________________________________________
____________________________________________________________
 On the list! Let's make it happen:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 You've got 3 tasks on your list.
____________________________________________________________
____________________________________________________________
 Woohoo! One more task done:
   [T][X] borrow book
____________________________________________________________
____________________________________________________________
 Here's your lineup. One step at a time!
 1.[T][X] borrow book
 2.[D][ ] do homework (by: Dec 02 2019)
 3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Back on the list. You've got this!
   [T][ ] borrow book
____________________________________________________________
____________________________________________________________
 Here's your lineup. One step at a time!
 1.[T][ ] borrow book
 2.[D][ ] do homework (by: Dec 02 2019)
 3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Catch you soon! Keep taking those little steps.
____________________________________________________________
```

## UI-002: Reject invalid commands without exiting

Aim: Verify malformed task commands, unknown commands, and invalid task numbers produce helpful errors while the chatbot continues accepting input.

Input, in order:

```text
todo
blah
deadline by 5
deadline return book /by
deadline return book /by 2019-02-29
event project meeting from 4 to 6
event project meeting /from /to 6
mark one
mark 1
unmark 1
bye
```

Expected program output:

```text
 _  __ _ _           
| |/ /(_) |__   ___  
| ' / | | '_ \ / _ \
| . \ | | |_) | (_) |
|_|\_\|_|_.__/ \___/
Hey! I'm Kibo, your pocket cheerleader.
Small steps, brighter days!
Ready? Try 'list' or 'todo read book'.
____________________________________________________________
____________________________________________________________
 The description of a todo cannot be empty.
 Usage: todo [description]
____________________________________________________________
____________________________________________________________
 Sorry, that is not a valid command.
 Available commands: todo, deadline, event, list, find, schedule, mark, unmark, delete, bye
____________________________________________________________
____________________________________________________________
 A deadline needs a description and /by date.
 Usage: deadline [description] /by yyyy-MM-dd
____________________________________________________________
____________________________________________________________
 A deadline needs a description and /by date.
 Usage: deadline [description] /by yyyy-MM-dd
____________________________________________________________
____________________________________________________________
 The deadline date must use yyyy-MM-dd format.
 Usage: deadline [description] /by yyyy-MM-dd
____________________________________________________________
____________________________________________________________
 An event needs a description, /from start, and /to end.
 Usage: event [description] /from [start] /to [end]
____________________________________________________________
____________________________________________________________
 An event needs a description, /from start, and /to end.
 Usage: event [description] /from [start] /to [end]
____________________________________________________________
____________________________________________________________
 Please provide a valid task number.
 Usage: mark [task number]
____________________________________________________________
____________________________________________________________
 Task 1 does not exist in your list.
____________________________________________________________
____________________________________________________________
 Task 1 does not exist in your list.
____________________________________________________________
____________________________________________________________
 Catch you soon! Keep taking those little steps.
____________________________________________________________
```

## UI-003: Delete tasks and renumber the list

Aim: Verify deletion removes the selected task, retains its displayed state in the confirmation, renumbers remaining tasks, and rejects invalid delete indexes.

Input, in order:

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from Aug 6th 2pm /to 4pm
mark 1
mark 2
list
delete 2
list
delete 9
delete
bye
```

Expected program output:

```text
 _  __ _ _           
| |/ /(_) |__   ___  
| ' / | | '_ \ / _ \
| . \ | | |_) | (_) |
|_|\_\|_|_.__/ \___/
Hey! I'm Kibo, your pocket cheerleader.
Small steps, brighter days!
Ready? Try 'list' or 'todo read book'.
____________________________________________________________
____________________________________________________________
 On the list! Let's make it happen:
   [T][ ] read book
 You've got 1 task on your list.
____________________________________________________________
____________________________________________________________
 On the list! Let's make it happen:
   [D][ ] return book (by: Jun 06 2019)
 You've got 2 tasks on your list.
____________________________________________________________
____________________________________________________________
 On the list! Let's make it happen:
   [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 You've got 3 tasks on your list.
____________________________________________________________
____________________________________________________________
 Woohoo! One more task done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Woohoo! One more task done:
   [D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
 Here's your lineup. One step at a time!
 1.[T][X] read book
 2.[D][X] return book (by: Jun 06 2019)
 3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 All cleared! I've removed this task:
   [D][X] return book (by: Jun 06 2019)
 You've got 2 tasks on your list.
____________________________________________________________
____________________________________________________________
 Here's your lineup. One step at a time!
 1.[T][X] read book
 2.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Task 9 does not exist in your list.
____________________________________________________________
____________________________________________________________
 Please provide a valid task number.
 Usage: delete [task number]
____________________________________________________________
____________________________________________________________
 Catch you soon! Keep taking those little steps.
____________________________________________________________
```

## UI-004: Save changed task lists to disk

Aim: Verify that adding, marking, and deleting tasks automatically updates `data/duke.txt`.

Input, in order:

```text
todo read book
deadline return book /by 2019-12-01
mark 1
delete 2
bye
```

Expected program output:

```text
 _  __ _ _           
| |/ /(_) |__   ___  
| ' / | | '_ \ / _ \
| . \ | | |_) | (_) |
|_|\_\|_|_.__/ \___/
Hey! I'm Kibo, your pocket cheerleader.
Small steps, brighter days!
Ready? Try 'list' or 'todo read book'.
____________________________________________________________
____________________________________________________________
 On the list! Let's make it happen:
   [T][ ] read book
 You've got 1 task on your list.
____________________________________________________________
____________________________________________________________
 On the list! Let's make it happen:
   [D][ ] return book (by: Dec 01 2019)
 You've got 2 tasks on your list.
____________________________________________________________
____________________________________________________________
 Woohoo! One more task done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 All cleared! I've removed this task:
   [D][ ] return book (by: Dec 01 2019)
 You've got 1 task on your list.
____________________________________________________________
____________________________________________________________
 Catch you soon! Keep taking those little steps.
____________________________________________________________
```

Expected contents of `data/duke.txt` after the program exits:

```text
T | 1 | read book
```

## UI-005: Load saved tasks when Kibo starts

Aim: Verify that Kibo reconstructs all task types and their done statuses from `data/duke.txt`
before it accepts commands.

Setup fixture for `data/duke.txt`:

```text
T | 1 | read book
D | 0 | return book | 2019-12-01
E | 0 | project meeting | Mon 2pm | 4pm
```

Input, in order:

```text
list
bye
```

Expected program output:

```text
 _  __ _ _           
| |/ /(_) |__   ___  
| ' / | | '_ \ / _ \
| . \ | | |_) | (_) |
|_|\_\|_|_.__/ \___/
Hey! I'm Kibo, your pocket cheerleader.
Small steps, brighter days!
Ready? Try 'list' or 'todo read book'.
____________________________________________________________
____________________________________________________________
 Here's your lineup. One step at a time!
 1.[T][X] read book
 2.[D][ ] return book (by: Dec 01 2019)
 3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Catch you soon! Keep taking those little steps.
____________________________________________________________
```

## UI-006: Reject empty, extra, and unsafe command input

Aim: Verify that empty input, extra arguments to `list` and `bye`, and text that would make the
save file ambiguous are rejected without changing the task list.

Input, in order:

```text

list extra
bye later
todo read | book
list
bye
```

Expected program output:

```text
 _  __ _ _           
| |/ /(_) |__   ___  
| ' / | | '_ \ / _ \
| . \ | | |_) | (_) |
|_|\_\|_|_.__/ \___/
Hey! I'm Kibo, your pocket cheerleader.
Small steps, brighter days!
Ready? Try 'list' or 'todo read book'.
____________________________________________________________
____________________________________________________________
 Please enter a command.
 Available commands: todo, deadline, event, list, find, schedule, mark, unmark, delete, bye
____________________________________________________________
____________________________________________________________
 This command does not take any additional text.
 Usage: list
____________________________________________________________
____________________________________________________________
 This command does not take any additional text.
 Usage: bye
____________________________________________________________
____________________________________________________________
 Task text cannot contain " | " because it is used to save tasks.
____________________________________________________________
____________________________________________________________
 Here's your lineup. One step at a time!
____________________________________________________________
____________________________________________________________
 Catch you soon! Keep taking those little steps.
____________________________________________________________
```

## UI-007: Report malformed saved task data

Aim: Verify that Kibo exits safely with a clear message when its save file has an invalid line.

Setup fixture for `data/duke.txt`:

```text
T | 2 | read book
```

Input: no input; the program exits after reporting the startup error.

Expected program output:

```text
 _  __ _ _           
| |/ /(_) |__   ___  
| ' / | | '_ \ / _ \
| . \ | | |_) | (_) |
|_|\_\|_|_.__/ \___/
Hey! I'm Kibo, your pocket cheerleader.
Small steps, brighter days!
Ready? Try 'list' or 'todo read book'.
____________________________________________________________
 The saved task on line 1 has an invalid format.
____________________________________________________________
```

## UI-008: Start and save without a data folder

Aim: Verify that a first-time user can start Kibo without `data/duke.txt` or its parent
`data/` folder, then create and save a task successfully.

Setup: use a fresh empty temporary working directory with no `data/` folder. Launch Kibo with
the compiled classes from the repository, for example:

```text
java -cp /absolute/path/to/ip/build/classes/java/main kibo.Kibo
```

Input, in order:

```text
todo first task
bye
```

Expected program output:

```text
 _  __ _ _           
| |/ /(_) |__   ___  
| ' / | | '_ \ / _ \
| . \ | | |_) | (_) |
|_|\_\|_|_.__/ \___/
Hey! I'm Kibo, your pocket cheerleader.
Small steps, brighter days!
Ready? Try 'list' or 'todo read book'.
____________________________________________________________
____________________________________________________________
 On the list! Let's make it happen:
   [T][ ] first task
 You've got 1 task on your list.
____________________________________________________________
____________________________________________________________
 Catch you soon! Keep taking those little steps.
____________________________________________________________
```

Expected file created after the program exits:

```text
data/duke.txt
```

Expected contents of that file:

```text
T | 0 | first task
```

## UI-009: Launch the packaged JavaFX application

Aim: Verify that the Shadow plugin produces an executable fat JAR that opens Kibo's JavaFX
chat window, highlights command errors, and keeps normal replies visually distinct.

Setup: run `./gradlew shadowJar` from the repository root. Then use a fresh empty temporary
working directory with no `data/` folder and launch the JAR using its absolute path, for example:

```text
java -jar /absolute/path/to/ip/build/libs/kibo.jar
```

Manual interaction, in order:

1. Confirm the title reads `Kibo - Small steps, brighter days`, the welcome calls Kibo your
   pocket cheerleader, and the window uses a cream background with golden avatars and user bubbles.
2. Enter `todo first task` and confirm the user and Kibo dialog boxes appear.
3. Enter `list` and confirm the task is shown.
4. Enter `todo` and confirm the reply has an `Error` label, pale red background, dark red
   text, and red border. The explanation and `Usage: todo [description]` must remain visible.
5. Enter `list` again and confirm its reply uses the normal white bubble with no `Error` label.
6. Enter `bye` and confirm the input field and Send button become disabled.

## UI-010: Find tasks by description

Aim: Verify that `find` returns matching descriptions in their original order, ignores letter
case, does not search task metadata, and rejects an empty keyword.

Setup fixture for `data/duke.txt`:

```text
T | 1 | read book
D | 1 | return book | 2019-06-06
E | 0 | project meeting | 2pm | 4pm
```

Input, in order:

```text
find BOOK
find meeting
find 2019
find
bye
```

Expected program output:

```text
 _  __ _ _           
| |/ /(_) |__   ___  
| ' / | | '_ \ / _ \
| . \ | | |_) | (_) |
|_|\_\|_|_.__/ \___/
Hey! I'm Kibo, your pocket cheerleader.
Small steps, brighter days!
Ready? Try 'list' or 'todo read book'.
____________________________________________________________
____________________________________________________________
 Let's see what matches your search:
 1.[T][X] read book
 2.[D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
 Let's see what matches your search:
 1.[E][ ] project meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Let's see what matches your search:
____________________________________________________________
____________________________________________________________
 The search keyword cannot be empty.
 Usage: find [keyword]
____________________________________________________________
____________________________________________________________
 Catch you soon! Keep taking those little steps.
____________________________________________________________
```

## UI-011: View deadlines and dated events by date

Aim: Verify that `schedule` returns matching deadlines and dated events in their original order,
excludes todos and legacy free-form events, and rejects missing or invalid dates.

Setup fixture for `data/duke.txt`:

```text
T | 0 | undated todo
E | 0 | legacy meeting | Mon 2pm | 4pm
E | 0 | dated meeting | 2019-12-02 2pm | 4pm
D | 0 | submit report | 2019-12-02
D | 0 | later deadline | 2019-12-03
```

Input, in order:

```text
schedule 2019-12-02
schedule 2019-12-04
schedule
schedule 2019-02-29
event invalid date /from 2019-02-29 2pm /to 4pm
bye
```

Expected program output:

```text
 _  __ _ _           
| |/ /(_) |__   ___  
| ' / | | '_ \ / _ \
| . \ | | |_) | (_) |
|_|\_\|_|_.__/ \___/
Hey! I'm Kibo, your pocket cheerleader.
Small steps, brighter days!
Ready? Try 'list' or 'todo read book'.
____________________________________________________________
____________________________________________________________
 Let's plan your day! Schedule for Dec 02 2019:
 1.[E][ ] dated meeting (from: 2019-12-02 2pm to: 4pm)
 2.[D][ ] submit report (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
 Let's plan your day! Schedule for Dec 04 2019:
____________________________________________________________
____________________________________________________________
 The schedule date must use yyyy-MM-dd format.
 Usage: schedule yyyy-MM-dd
____________________________________________________________
____________________________________________________________
 The schedule date must use yyyy-MM-dd format.
 Usage: schedule yyyy-MM-dd
____________________________________________________________
____________________________________________________________
 The event start date must use yyyy-MM-dd format.
 Date-aware event usage: event [description] /from yyyy-MM-dd [time] /to [end]
____________________________________________________________
____________________________________________________________
 Catch you soon! Keep taking those little steps.
____________________________________________________________
```
