# UI test plan

This file is the source of truth for console UI tests. Each test case starts with
`## Test Case:` and contains **Aim**, **Command**, **Input**, and **Expected output**.
Commands are run from the project root. The test runner compares complete output.

## Test Case: Exits politely

- **Aim:** Verify that the chatbot starts and responds to the `bye` command with its farewell message.
- **Command**

```sh
rm -rf data && javac -d out $(find src/main/java -name '*.java' ! -name 'GuiNob.java' ! -name 'Launcher.java' ! -name 'DialogBox.java' ! -name 'MainWindow.java') && java -cp out nob.Nob
```

- **Input**

```text
bye
```

- **Expected output**

```text
____________________________________________________________
 _   _       _
| \ | | ___ | |__
|  \| |/ _ \| '_ \
| |\  | (_) | |_) |
|_| \_|\___/|_.__/

  (•_•)
  ( •_•)>⌐■-■
  (⌐■_■)

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________

____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

## Test Case: Starts without a data file

- **Aim:** Verify that Nob treats a missing data file as an empty task list instead of failing at startup.
- **Command**

```sh
rm -rf data && javac -d out $(find src/main/java -name '*.java' ! -name 'GuiNob.java' ! -name 'Launcher.java' ! -name 'DialogBox.java' ! -name 'MainWindow.java') && printf 'list\nbye\n' | java -cp out nob.Nob
```

- **Input**

```text
```

- **Expected output**

```text
____________________________________________________________
 _   _       _
| \ | | ___ | |__
|  \| |/ _ \| '_ \
| |\  | (_) | |_) |
|_| \_|\___/|_.__/

  (•_•)
  ( •_•)>⌐■-■
  (⌐■_■)

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________

____________________________________________________________
Your task list is empty right now. Add a task using the 'todo', 'deadline' or 'event' commands.
____________________________________________________________


____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

## Test Case: Saves tasks after each change

- **Aim:** Verify that adding, marking, and deleting tasks automatically writes the current task list to disk.
- **Command**

```sh
rm -rf data && javac -d out $(find src/main/java -name '*.java' ! -name 'GuiNob.java' ! -name 'Launcher.java' ! -name 'DialogBox.java' ! -name 'MainWindow.java') && java -cp out nob.Nob && cat data/nob.txt
```

- **Input**

```text
todo read book
deadline return book /by Friday
mark 1
delete 2
bye
```

- **Expected output**

```text
____________________________________________________________
 _   _       _
| \ | | ___ | |__
|  \| |/ _ \| '_ \
| |\  | (_) | |_) |
|_| \_|\___/|_.__/

  (•_•)
  ( •_•)>⌐■-■
  (⌐■_■)

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________

____________________________________________________________
Alrighty. I've added this task:
  [T] read book [ ]
Now you have 1 tasks in the list.
____________________________________________________________


____________________________________________________________
Alrighty. I've added this task:
  [D: Friday] return book [ ]
Now you have 2 tasks in the list.
____________________________________________________________


____________________________________________________________
LET'S GOOO! I've marked this task as done:
  [T] read book [✓]
____________________________________________________________


____________________________________________________________
Noted. I've removed the task:
  [D: Friday] return book [ ]
Now you have 1 tasks left in the list.
____________________________________________________________


____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
[T] read book [✓]
```

## Test Case: Loads tasks after restarting

- **Aim:** Verify that tasks saved by one chatbot run are loaded by the next run, including completion status.
- **Command**

```sh
rm -rf data && javac -d out $(find src/main/java -name '*.java' ! -name 'GuiNob.java' ! -name 'Launcher.java' ! -name 'DialogBox.java' ! -name 'MainWindow.java') && printf 'todo read book\nmark 1\nbye\n' | java -cp out nob.Nob >/dev/null && printf 'list\nbye\n' | java -cp out nob.Nob
```

- **Input**

```text
```

- **Expected output**

```text
____________________________________________________________
 _   _       _
| \ | | ___ | |__
|  \| |/ _ \| '_ \
| |\  | (_) | |_) |
|_| \_|\___/|_.__/

  (•_•)
  ( •_•)>⌐■-■
  (⌐■_■)

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________

____________________________________________________________
Here are the tasks in your list:
1.[T] read book [✓]
____________________________________________________________


____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

## Test Case: Ignores malformed saved tasks

- **Aim:** Verify that malformed records in the task file are ignored and do not crash startup or create invalid tasks.
- **Command**

```sh
rm -rf data && mkdir data && printf '%s\n' '[T] [ ]' '[D: ] task [ ]' 'not a task' > data/nob.txt && javac -d out $(find src/main/java -name '*.java' ! -name 'GuiNob.java' ! -name 'Launcher.java' ! -name 'DialogBox.java' ! -name 'MainWindow.java') && printf 'list\nbye\n' | java -cp out nob.Nob
```

- **Input**

```text
```

- **Expected output**

```text
____________________________________________________________
 _   _       _
| \ | | ___ | |__
|  \| |/ _ \| '_ \
| |\  | (_) | |_) |
|_| \_|\___/|_.__/

  (•_•)
  ( •_•)>⌐■-■
  (⌐■_■)

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________

____________________________________________________________
Your task list is empty right now. Add a task using the 'todo', 'deadline' or 'event' commands.
____________________________________________________________


____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

## Test Case: Unknown commands should not show raw terminal input artifacts

- **Aim:** Ensure that typing an unrecognised command produces the normal Nob error output without leaking the raw terminal prompt or cursor-control artifacts.
- **Command**

```sh
rm -rf data && javac -d out $(find src/main/java -name '*.java' ! -name 'GuiNob.java' ! -name 'Launcher.java' ! -name 'DialogBox.java' ! -name 'MainWindow.java') && java -cp out nob.Nob
```

- **Input**

```text
hello
bye
```

- **Expected output**

```text
____________________________________________________________
 _   _       _
| \ | | ___ | |__
|  \| |/ _ \| '_ \
| |\  | (_) | |_) |
|_| \_|\___/|_.__/

  (•_•)
  ( •_•)>⌐■-■
  (⌐■_■)

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________

____________________________________________________________
That isn't any of the commands I know. Did you mean one of these?
  todo DESCRIPTION
  deadline DESCRIPTION /by DATE_OR_TIME
  event DESCRIPTION /from START /to END
____________________________________________________________


____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

## Test Case: Handles invalid task numbers

- **Aim:** Verify that invalid task index input raises a clear error message instead of crashing or silently failing.
- **Command**

```sh
rm -rf data && javac -d out $(find src/main/java -name '*.java' ! -name 'GuiNob.java' ! -name 'Launcher.java' ! -name 'DialogBox.java' ! -name 'MainWindow.java') && java -cp out nob.Nob
```

- **Input**

```text
todo borrow book
mark zebra
mark 1
list
bye
```

- **Expected output**

```text
____________________________________________________________
 _   _       _
| \ | | ___ | |__
|  \| |/ _ \| '_ \
| |\  | (_) | |_) |
|_| \_|\___/|_.__/

  (•_•)
  ( •_•)>⌐■-■
  (⌐■_■)

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________

____________________________________________________________
Alrighty. I've added this task:
  [T] borrow book [ ]
Now you have 1 tasks in the list.
____________________________________________________________


____________________________________________________________
Please enter a valid task number.
____________________________________________________________


____________________________________________________________
LET'S GOOO! I've marked this task as done:
  [T] borrow book [✓]
____________________________________________________________


____________________________________________________________
Here are the tasks in your list:
1.[T] borrow book [✓]
____________________________________________________________


____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

## Test Case: Parses date and time inputs for deadlines and events

- **Aim:** Verify that dates and times are understood as date values rather than plain text, while preserving the task list format.
- **Command**

```sh
rm -rf data && javac -d out $(find src/main/java -name '*.java' ! -name 'GuiNob.java' ! -name 'Launcher.java' ! -name 'DialogBox.java' ! -name 'MainWindow.java') && java -cp out nob.Nob
```

- **Input**

```text
deadline return book /by 2/12/2019 1800
event team sync /from 2019-12-02 1800 /to 2019-12-02 1900
list
bye
```

- **Expected output**

```text
____________________________________________________________
 _   _       _
| \ | | ___ | |__
|  \| |/ _ \| '_ \
| |\  | (_) | |_) |
|_| \_|\___/|_.__/

  (•_•)
  ( •_•)>⌐■-■
  (⌐■_■)

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________

____________________________________________________________
Alrighty. I've added this task:
  [D: Dec 02 2019, 6:00PM] return book [ ]
Now you have 1 tasks in the list.
____________________________________________________________


____________________________________________________________
Alrighty. I've added this task:
  [E: Dec 02 2019, 6:00PM to Dec 02 2019, 7:00PM] team sync [ ]
Now you have 2 tasks in the list.
____________________________________________________________


____________________________________________________________
Here are the tasks in your list:
1.[D: Dec 02 2019, 6:00PM] return book [ ]
2.[E: Dec 02 2019, 6:00PM to Dec 02 2019, 7:00PM] team sync [ ]
____________________________________________________________


____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

## Test Case: Rejects malformed deadline syntax without losing valid state

- **Aim:** Ensure a malformed deadline command is rejected while a later valid deadline remains in the task list.
- **Command**

```sh
rm -rf data && javac -d out $(find src/main/java -name '*.java' ! -name 'GuiNob.java' ! -name 'Launcher.java' ! -name 'DialogBox.java' ! -name 'MainWindow.java') && java -cp out nob.Nob
```

- **Input**

```text
deadline return book /bySunday
deadline finish draft /by Friday
list
bye
```

- **Expected output**

```text
____________________________________________________________
 _   _       _
| \ | | ___ | |__
|  \| |/ _ \| '_ \
| |\  | (_) | |_) |
|_| \_|\___/|_.__/

  (•_•)
  ( •_•)>⌐■-■
  (⌐■_■)

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________

____________________________________________________________
Check that there is a space before and after '/by'.
Use: deadline DESCRIPTION /by DATE_OR_TIME
(eg., deadline return book /by Sunday)
____________________________________________________________


____________________________________________________________
Alrighty. I've added this task:
  [D: Friday] finish draft [ ]
Now you have 1 tasks in the list.
____________________________________________________________


____________________________________________________________
Here are the tasks in your list:
1.[D: Friday] finish draft [ ]
____________________________________________________________


____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

## Test Case: Rejects malformed event syntax without losing valid state

- **Aim:** Ensure a malformed event command is rejected while a later valid event remains in the task list.
- **Command**

```sh
rm -rf data && javac -d out $(find src/main/java -name '*.java' ! -name 'GuiNob.java' ! -name 'Launcher.java' ! -name 'DialogBox.java' ! -name 'MainWindow.java') && java -cp out nob.Nob
```

- **Input**

```text
event project meeting /from Mon 2pm /to 4pm
event bad /from Mon 2pm
list
bye
```

- **Expected output**

```text
____________________________________________________________
 _   _       _
| \ | | ___ | |__
|  \| |/ _ \| '_ \
| |\  | (_) | |_) |
|_| \_|\___/|_.__/

  (•_•)
  ( •_•)>⌐■-■
  (⌐■_■)

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________

____________________________________________________________
Alrighty. I've added this task:
  [E: Mon 2pm to 4pm] project meeting [ ]
Now you have 1 tasks in the list.
____________________________________________________________


____________________________________________________________
Check that there is a space before and after '/from' and '/to'.
Use: event DESCRIPTION /from START /to END
(eg., event project meeting /from Mon 2pm /to 4pm)
____________________________________________________________


____________________________________________________________
Here are the tasks in your list:
1.[E: Mon 2pm to 4pm] project meeting [ ]
____________________________________________________________


____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

## Test Case: Handles empty todo and later valid todo without corrupting state

- **Aim:** Confirm an empty todo is rejected and a following valid todo still creates a task normally.
- **Command**

```sh
rm -rf data && javac -d out $(find src/main/java -name '*.java' ! -name 'GuiNob.java' ! -name 'Launcher.java' ! -name 'DialogBox.java' ! -name 'MainWindow.java') && java -cp out nob.Nob
```

- **Input**

```text
todo
todo read book
list
bye
```

- **Expected output**

```text
____________________________________________________________
 _   _       _
| \ | | ___ | |__
|  \| |/ _ \| '_ \
| |\  | (_) | |_) |
|_| \_|\___/|_.__/

  (•_•)
  ( •_•)>⌐■-■
  (⌐■_■)

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________

____________________________________________________________
Use: todo DESCRIPTION
(eg., todo borrow book)
____________________________________________________________


____________________________________________________________
Alrighty. I've added this task:
  [T] read book [ ]
Now you have 1 tasks in the list.
____________________________________________________________


____________________________________________________________
Here are the tasks in your list:
1.[T] read book [ ]
____________________________________________________________


____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

## Test Case: Rejects empty deadline description

- **Aim:** Ensure a deadline without a description is rejected with a clear message telling the user the description must not be empty.
- **Command**

```sh
rm -rf data && javac -d out $(find src/main/java -name '*.java' ! -name 'GuiNob.java' ! -name 'Launcher.java' ! -name 'DialogBox.java' ! -name 'MainWindow.java') && java -cp out nob.Nob
```

- **Input**

```text
deadline /by 2
bye
```

- **Expected output**

```text
____________________________________________________________
 _   _       _
| \ | | ___ | |__
|  \| |/ _ \| '_ \
| |\  | (_) | |_) |
|_| \_|\___/|_.__/

  (•_•)
  ( •_•)>⌐■-■
  (⌐■_■)

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________

____________________________________________________________
Description should not be empty.
Use: deadline DESCRIPTION /by DATE_OR_TIME
(eg., deadline return book /by Sunday)
____________________________________________________________


____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

## Test Case: Rejects empty event description

- **Aim:** Ensure an event without a description is rejected with a clear message telling the user the description must not be empty.
- **Command**

```sh
rm -rf data && javac -d out $(find src/main/java -name '*.java' ! -name 'GuiNob.java' ! -name 'Launcher.java' ! -name 'DialogBox.java' ! -name 'MainWindow.java') && java -cp out nob.Nob
```

- **Input**

```text
event /from Mon 2pm /to 4pm
bye
```

- **Expected output**

```text
____________________________________________________________
 _   _       _
| \ | | ___ | |__
|  \| |/ _ \| '_ \
| |\  | (_) | |_) |
|_| \_|\___/|_.__/

  (•_•)
  ( •_•)>⌐■-■
  (⌐■_■)

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________

____________________________________________________________
Description should not be empty.
Use: event DESCRIPTION /from START /to END
(eg., event project meeting /from Mon 2pm /to 4pm)
____________________________________________________________


____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

## Test Case: Shows available commands with help

- **Aim:** Verify that the `help` command prints all available Nob commands, including task management actions and syntax.
- **Command**

```sh
rm -rf data && javac -d out $(find src/main/java -name '*.java' ! -name 'GuiNob.java' ! -name 'Launcher.java' ! -name 'DialogBox.java' ! -name 'MainWindow.java') && java -cp out nob.Nob
```

- **Input**

```text
help
bye
```

- **Expected output**

```text
____________________________________________________________
 _   _       _
| \ | | ___ | |__
|  \| |/ _ \| '_ \
| |\  | (_) | |_) |
|_| \_|\___/|_.__/

  (•_•)
  ( •_•)>⌐■-■
  (⌐■_■)

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________

____________________________________________________________
Here are the commands you can use:
  list
  todo DESCRIPTION
  deadline DESCRIPTION /by DATE_OR_TIME
  event DESCRIPTION /from START /to END
  find KEYWORD
  mark TASK_NUMBER
  unmark TASK_NUMBER
  delete TASK_NUMBER
  clear
  bye
____________________________________________________________


____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

## Test Case: Finds tasks by a keyword

- **Aim:** Verify that `find` shows every task whose description contains the keyword, regardless of case.
- **Command**

```sh
rm -rf data && javac -d out $(find src/main/java -name '*.java' ! -name 'GuiNob.java' ! -name 'Launcher.java' ! -name 'DialogBox.java' ! -name 'MainWindow.java') && java -cp out nob.Nob
```

- **Input**

```text
todo read book
deadline return book /by Friday
event team meeting /from Mon /to Tue
find BOOK
bye
```

- **Expected output**

```text
____________________________________________________________
 _   _       _
| \ | | ___ | |__
|  \| |/ _ \| '_ \
| |\  | (_) | |_) |
|_| \_|\___/|_.__/

  (•_•)
  ( •_•)>⌐■-■
  (⌐■_■)

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________

____________________________________________________________
Alrighty. I've added this task:
  [T] read book [ ]
Now you have 1 tasks in the list.
____________________________________________________________


____________________________________________________________
Alrighty. I've added this task:
  [D: Friday] return book [ ]
Now you have 2 tasks in the list.
____________________________________________________________


____________________________________________________________
Alrighty. I've added this task:
  [E: Mon to Tue] team meeting [ ]
Now you have 3 tasks in the list.
____________________________________________________________


____________________________________________________________
Here are the matching tasks in your list:
1.[T] read book [ ]
2.[D: Friday] return book [ ]
____________________________________________________________


____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

## Test Case: Deletes a task from the list

- **Aim:** Verify that `delete 2` removes the second task and shifts the remaining tasks left without affecting the rest of the task list.
- **Command**

```sh
rm -rf data && javac -d out $(find src/main/java -name '*.java' ! -name 'GuiNob.java' ! -name 'Launcher.java' ! -name 'DialogBox.java' ! -name 'MainWindow.java') && java -cp out nob.Nob
```

- **Input**

```text
todo first task
todo second task
todo third task
delete 2
list
bye
```

- **Expected output**

```text
____________________________________________________________
 _   _       _
| \ | | ___ | |__
|  \| |/ _ \| '_ \
| |\  | (_) | |_) |
|_| \_|\___/|_.__/

  (•_•)
  ( •_•)>⌐■-■
  (⌐■_■)

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________

____________________________________________________________
Alrighty. I've added this task:
  [T] first task [ ]
Now you have 1 tasks in the list.
____________________________________________________________


____________________________________________________________
Alrighty. I've added this task:
  [T] second task [ ]
Now you have 2 tasks in the list.
____________________________________________________________


____________________________________________________________
Alrighty. I've added this task:
  [T] third task [ ]
Now you have 3 tasks in the list.
____________________________________________________________


____________________________________________________________
Noted. I've removed the task:
  [T] second task [ ]
Now you have 2 tasks left in the list.
____________________________________________________________


____________________________________________________________
Here are the tasks in your list:
1.[T] first task [ ]
2.[T] third task [ ]
____________________________________________________________


____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

## Test Case: Rejects an event whose end is before its start

- **Aim:** Verify that a chronologically reversed event is rejected and is not added to the task list.
- **Command**

```sh
rm -rf data && javac -d out $(find src/main/java -name '*.java' ! -name 'GuiNob.java' ! -name 'Launcher.java' ! -name 'DialogBox.java' ! -name 'MainWindow.java') && java -cp out nob.Nob
```

- **Input**

```text
event backwards meeting /from 2019-12-02 1900 /to 2019-12-02 1800
list
bye
```

- **Expected output**

```text
____________________________________________________________
 _   _       _
| \ | | ___ | |__
|  \| |/ _ \| '_ \
| |\  | (_) | |_) |
|_| \_|\___/|_.__/

  (•_•)
  ( •_•)>⌐■-■
  (⌐■_■)

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________

____________________________________________________________
Event end time should not be before its start time.
Use: event DESCRIPTION /from START /to END
____________________________________________________________


____________________________________________________________
Your task list is empty right now. Add a task using the 'todo', 'deadline' or 'event' commands.
____________________________________________________________


____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

# Manual GUI test matrix

The JavaFX interface depends on a graphical desktop, window manager, fonts, and platform-specific
rendering. Run the checks below after the automated console and JUnit suites pass. Use a fresh copy
of `data/nob.txt` for each environment, and record the OS version, display scale, resolution, and
system language with the result.

## Environments

Test at least these representative configurations when they are available:

| Platform | Resolution and scale | System language |
| --- | --- | --- |
| macOS | 1440 × 900 at default scaling | English |
| Windows 11 | 1920 × 1080 at 100% scaling | English |
| Windows 11 | 1920 × 1080 at 150% scaling | Chinese |
| Linux | 1366 × 768 at 100% scaling | English |

If an environment is unavailable, record it as **Not tested** rather than treating it as passed.

## Manual checks

1. Start the GUI with `./gradlew run`. Confirm that the window opens at no less than 460 × 540,
   the title is `Nob — Your task companion`, Nob's avatar is visible, and the welcome response panel
   is fully readable.
2. Resize the window to its minimum size and then maximize it. Confirm that dialog text wraps,
   controls remain usable, no content overlaps, and the conversation can still be scrolled.
3. Enter `todo read book`, a deadline with a parsed date, and an event with start/end times. Confirm
   that user commands appear as compact dark bubbles on the right without avatars. Confirm that Nob
   replies appear as wider white panels on the left with Nob's avatar, the `NOB` label, and a blue
   accent edge. Dates should use the same format as the console UI.
4. Enter `list`, `find BOOK`, `mark 1`, `unmark 1`, `delete 1`, and `clear`. Confirm that each response
   is correct and that the latest response scrolls into view automatically.
5. Enter malformed commands such as `todo`, `deadline book /by`, `event meeting /from Monday`, and
   `mark zebra`. Confirm that the errors are readable and that the app remains responsive.
6. Enter a blank line and whitespace-only text. Confirm that no dialog bubble is added.
7. Close and reopen the app after adding and marking tasks. Confirm that saved tasks and completion
   states are restored.
8. Temporarily make the data location unavailable, then add or clear a task. Confirm that the app
   remains usable. Restore the location after the check.
9. Enter enough messages to exceed the visible height. Confirm that the view follows the newest
   message and older messages remain reachable by scrolling.
10. Enter `bye`. Confirm that the farewell bubble appears before the app closes after approximately
    one second.
11. On the Chinese-language environment, repeat checks 1–5. Confirm that English command parsing,
    date formatting, punctuation, the tick icon, and emoji do not change with the system locale.
