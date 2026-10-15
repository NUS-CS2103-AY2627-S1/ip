## UI test plan

Build command: `./gradlew clean classes`

GUI program command: `./gradlew run`

Program command: `java -cp build/classes/java/main dude.Dude`

Each expected output is the complete stdout session. The runner uses Java 25
and the non-interactive default terminal width of 60 characters.

JUnit test command: `./gradlew test`

CheckStyle validation command: `./gradlew check`

GUI smoke test: launch the GUI, verify the task list loads, execute `todo read book`,
`mark 1`, `find book`, `unmark 1`, `delete 1`, and exit with `bye` or the native window
control. Also verify that
typed commands and application responses appear as distinct messages, the Send button
works alongside Enter, the command list filters by the typed command prefix and clicking
a command fills the input field, and Up/Down plus Enter/Tab can navigate and select
commands, with the selected row showing a `Tab to complete` hint on the right, without
showing parameter hints in the input, and conversational errors show `Try: command ...`
without a duplicated `Usage:` prefix, invalid commands are
visibly styled as conversational errors, DUDE messages are left-aligned with a sunglasses
profile icon and chat pointer, user messages are right-aligned with a chat pointer, new
messages auto-scroll into view, task
rows use structured UI elements in a left sidebar rather than a raw text list, selected
task labels remain readable and retain their list numbers, CLI borders are absent from GUI bubbles, and the layout
remains usable when the window is resized. Each task card places its type above its
description, and the command palette keeps a constant height,
`bye` closes the GUI after its farewell response, the matching command usage remains
visible after a command and its trailing space are typed, and cancelling a duplicate task does
not create an empty response bubble.

JUnit coverage includes date parsing and validation in `TaskDateTest`, case-insensitive
ordered task search and original-index preservation in `TaskListTest`, GUI command
execution and error handling in `GuiControllerTest`, storage persistence in
`StorageTest`, duplicate resolution in `DuplicateResolutionTest`, plus polymorphic
command invocation and session-based undo behavior in `CommandQueueTest`.

## Test case: find keeps original task numbers

Aim: Verify that a filtered search displays the task's original list number so that
the number can safely be used by `mark`, `unmark`, and `delete`.

### Inputs

```text
todo buy milk
todo read book
find book
mark 2
list
bye
```

### Expected output

```text
────────────────────────────────────────────────────────────
██████╗  ██╗   ██╗ ██████╗  ███████╗
██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝
██║  ██║ ██║   ██║ ██║  ██║ █████╗
██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝
██████╔╝ ╚██████╔╝╚██████╔╝ ███████╗
╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝
Hey! I'm DUDE, your dependable task buddy.
Dates can be represented in this format: yyyy-MM-dd.
To include a time, use this format: yyyy-MM-dd HHmm.
What can I help you with, dude?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nice, dude — I've added this task:
  [T][ ] buy milk
Now you have 1 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nice, dude — I've added this task:
  [T][ ] read book
Now you have 2 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here are the tasks I found, dude:
2.[T][ ] read book
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Solid work, dude — this task is done:
  [T][X] read book
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here's your task list, dude:
1.[T][ ] buy milk
2.[T][X] read book
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Catch you later, dude!
────────────────────────────────────────────────────────────
```

## Test case: undo task changes

Aim: Verify that undo reverses side-effect commands in last-in-first-out order
and reports an empty undo history without changing the task list.

### Inputs

```text
todo read book
todo buy milk
undo
undo
undo
list
bye
```

### Expected output

```text
────────────────────────────────────────────────────────────
██████╗  ██╗   ██╗ ██████╗  ███████╗
██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝
██║  ██║ ██║   ██║ ██║  ██║ █████╗
██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝
██████╔╝ ╚██████╔╝╚██████╔╝ ███████╗
╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝
Hey! I'm DUDE, your dependable task buddy.
Dates can be represented in this format: yyyy-MM-dd.
To include a time, use this format: yyyy-MM-dd HHmm.
What can I help you with, dude?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nice, dude — I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nice, dude — I've added this task:
  [T][ ] buy milk
Now you have 2 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Done, dude — I rolled back:
  add task.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Done, dude — I rolled back:
  add task.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nothing to undo yet, dude.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here's your task list, dude:
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Catch you later, dude!
────────────────────────────────────────────────────────────
```

## Test case: typed dates and date query

Aim: Verify that date-only and date-time values are parsed, formatted, persisted, and found by `on`.

### Inputs

```text
deadline return book /by 2019-12-02 1800
event project /from 2019-12-01 /to 2019-12-03
on 2019-12-02
list
bye
```

### Expected output

```text
────────────────────────────────────────────────────────────
██████╗  ██╗   ██╗ ██████╗  ███████╗
██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝
██║  ██║ ██║   ██║ ██║  ██║ █████╗
██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝
██████╔╝ ╚██████╔╝╚██████╔╝ ███████╗
╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝
Hey! I'm DUDE, your dependable task buddy.
Dates can be represented in this format: yyyy-MM-dd.
To include a time, use this format: yyyy-MM-dd HHmm.
What can I help you with, dude?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nice, dude — I've added this task:
  [D][ ] return book (by: Dec 02 2019 18:00)
Now you have 1 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nice, dude — I've added this task:
  [E][ ] project (from: Dec 01 2019 to: Dec 03 2019)
Now you have 2 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here's what you have on 2019-12-02:
1.[D][ ] return book (by: Dec 02 2019 18:00)
2.[E][ ] project (from: Dec 01 2019 to: Dec 03 2019)
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here's your task list, dude:
1.[D][ ] return book (by: Dec 02 2019 18:00)
2.[E][ ] project (from: Dec 01 2019 to: Dec 03 2019)
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Catch you later, dude!
────────────────────────────────────────────────────────────
```

## Test case: invalid temporal input

Aim: Verify that invalid calendar dates and reversed event ranges are rejected without adding tasks.

### Inputs

```text
deadline invalid /by 2019-02-29
event reversed /from 2019-12-03 /to 2019-12-02
on 2019-02-30
list
bye
```

### Expected output

```text
────────────────────────────────────────────────────────────
██████╗  ██╗   ██╗ ██████╗  ███████╗
██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝
██║  ██║ ██║   ██║ ██║  ██║ █████╗
██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝
██████╔╝ ╚██████╔╝╚██████╔╝ ███████╗
╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝
Hey! I'm DUDE, your dependable task buddy.
Dates can be represented in this format: yyyy-MM-dd.
To include a time, use this format: yyyy-MM-dd HHmm.
What can I help you with, dude?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Error: invalid by "2019-02-29" for deadline.
Expected: yyyy-MM-dd or yyyy-MM-dd HHmm.
Usage: deadline <description> /by <yyyy-MM-dd [HHmm]>
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Error: invalid to "2019-12-02" for event.
Expected: a date after the start date.
Usage: event <description> /from <yyyy-MM-dd [HHmm]> /to <yyyy-MM-dd [HHmm]>
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Error: invalid date "2019-02-30" for on.
Expected: yyyy-MM-dd.
Usage: on <yyyy-MM-dd>
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here's your task list, dude:
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Catch you later, dude!
────────────────────────────────────────────────────────────
```

## Test case: corrupted temporal records

Aim: Verify that malformed JSONL records and legacy free-form temporal values remain visible with their raw content.

### Data file before startup

```text
not json
{"type":"D","done":false,"description":"old","by":"Sunday"}
```

### Inputs

```text
list
bye
```

### Expected output

```text
────────────────────────────────────────────────────────────
██████╗  ██╗   ██╗ ██████╗  ███████╗
██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝
██║  ██║ ██║   ██║ ██║  ██║ █████╗
██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝
██████╔╝ ╚██████╔╝╚██████╔╝ ███████╗
╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝
Hey! I'm DUDE, your dependable task buddy.
Dates can be represented in this format: yyyy-MM-dd.
To include a time, use this format: yyyy-MM-dd HHmm.
What can I help you with, dude?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here's your task list, dude:
1.[C][ ] [Corrupted: not json]
2.[C][ ] [Corrupted: {"type":"D","done":false,"description":"old","by":"Sunday"}]
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Catch you later, dude!
────────────────────────────────────────────────────────────
```
## Test case: find tasks by keyword

Aim: Verify that find performs case-insensitive description matching, handles no matches, and rejects a missing keyword.

### Inputs

```text
todo read book
deadline return book /by 2019-12-02
todo buy milk
find BOOK
find train
find
bye
```

### Expected output

```text
────────────────────────────────────────────────────────────
██████╗  ██╗   ██╗ ██████╗  ███████╗
██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝
██║  ██║ ██║   ██║ ██║  ██║ █████╗
██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝
██████╔╝ ╚██████╔╝╚██████╔╝ ███████╗
╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝
Hey! I'm DUDE, your dependable task buddy.
Dates can be represented in this format: yyyy-MM-dd.
To include a time, use this format: yyyy-MM-dd HHmm.
What can I help you with, dude?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nice, dude — I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nice, dude — I've added this task:
  [D][ ] return book (by: Dec 02 2019)
Now you have 2 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nice, dude — I've added this task:
  [T][ ] buy milk
Now you have 3 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here are the tasks I found, dude:
1.[T][ ] read book
2.[D][ ] return book (by: Dec 02 2019)
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here are the tasks I found, dude:
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Error: invalid keyword <missing> for find.
Expected: a non-blank keyword.
Usage: find <keyword>
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Catch you later, dude!
────────────────────────────────────────────────────────────
```
## Test case: duplicate task resolution

Aim: Verify that normalized duplicate descriptions can be added, edited, or cancelled through the CLI.

### Inputs

```text
todo read book
todo READ   BOOK
ad
todo Read Book
e
1
todo read book
c
list extra
bye
```

### Expected output

```text
────────────────────────────────────────────────────────────
██████╗  ██╗   ██╗ ██████╗  ███████╗
██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝
██║  ██║ ██║   ██║ ██║  ██║ █████╗
██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝
██████╔╝ ╚██████╔╝╚██████╔╝ ███████╗
╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝
Hey! I'm DUDE, your dependable task buddy.
Dates can be represented in this format: yyyy-MM-dd.
To include a time, use this format: yyyy-MM-dd HHmm.
What can I help you with, dude?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nice, dude — I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
I found a task with the same description, dude:
1.[T][ ] read book
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Edit [E], add [A], or cancel [C]?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nice, dude — I've added this task:
  [T][ ] READ   BOOK
Now you have 2 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
I found a task with the same description, dude:
1.[T][ ] read book
2.[T][ ] READ   BOOK
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Edit [E], add [A], or cancel [C]?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Which matching task number should I edit?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Updated, dude — I've replaced the matching task:
  [T][ ] Read Book
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
I found a task with the same description, dude:
1.[T][ ] Read Book
2.[T][ ] READ   BOOK
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Edit [E], add [A], or cancel [C]?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Error: invalid argument "extra" for list.
Expected: no arguments.
Usage: [31mlist[0m
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Catch you later, dude!
────────────────────────────────────────────────────────────
```
## Latest test session

### find keeps original task numbers
Console input:
```text
todo buy milk
todo read book
find book
mark 2
list
bye
```
Console output:
```text
────────────────────────────────────────────────────────────
██████╗  ██╗   ██╗ ██████╗  ███████╗
██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝
██║  ██║ ██║   ██║ ██║  ██║ █████╗
██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝
██████╔╝ ╚██████╔╝╚██████╔╝ ███████╗
╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝
Hey! I'm DUDE, your dependable task buddy.
Dates can be represented in this format: yyyy-MM-dd.
To include a time, use this format: yyyy-MM-dd HHmm.
What can I help you with, dude?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nice, dude — I've added this task:
  [T][ ] buy milk
Now you have 1 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nice, dude — I've added this task:
  [T][ ] read book
Now you have 2 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here are the tasks I found, dude:
2.[T][ ] read book
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Solid work, dude — this task is done:
  [T][X] read book
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here's your task list, dude:
1.[T][ ] buy milk
2.[T][X] read book
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Catch you later, dude!
────────────────────────────────────────────────────────────
```

### undo task changes
Console input:
```text
todo read book
todo buy milk
undo
undo
undo
list
bye
```
Console output:
```text
────────────────────────────────────────────────────────────
██████╗  ██╗   ██╗ ██████╗  ███████╗
██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝
██║  ██║ ██║   ██║ ██║  ██║ █████╗
██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝
██████╔╝ ╚██████╔╝╚██████╔╝ ███████╗
╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝
Hey! I'm DUDE, your dependable task buddy.
Dates can be represented in this format: yyyy-MM-dd.
To include a time, use this format: yyyy-MM-dd HHmm.
What can I help you with, dude?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nice, dude — I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nice, dude — I've added this task:
  [T][ ] buy milk
Now you have 2 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Done, dude — I rolled back:
  add task.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Done, dude — I rolled back:
  add task.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nothing to undo yet, dude.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here's your task list, dude:
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Catch you later, dude!
────────────────────────────────────────────────────────────
```

### typed dates and date query
Console input:
```text
deadline return book /by 2019-12-02 1800
event project /from 2019-12-01 /to 2019-12-03
on 2019-12-02
list
bye
```
Console output:
```text
────────────────────────────────────────────────────────────
██████╗  ██╗   ██╗ ██████╗  ███████╗
██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝
██║  ██║ ██║   ██║ ██║  ██║ █████╗
██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝
██████╔╝ ╚██████╔╝╚██████╔╝ ███████╗
╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝
Hey! I'm DUDE, your dependable task buddy.
Dates can be represented in this format: yyyy-MM-dd.
To include a time, use this format: yyyy-MM-dd HHmm.
What can I help you with, dude?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nice, dude — I've added this task:
  [D][ ] return book (by: Dec 02 2019 18:00)
Now you have 1 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nice, dude — I've added this task:
  [E][ ] project (from: Dec 01 2019 to: Dec 03 2019)
Now you have 2 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here's what you have on 2019-12-02:
1.[D][ ] return book (by: Dec 02 2019 18:00)
2.[E][ ] project (from: Dec 01 2019 to: Dec 03 2019)
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here's your task list, dude:
1.[D][ ] return book (by: Dec 02 2019 18:00)
2.[E][ ] project (from: Dec 01 2019 to: Dec 03 2019)
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Catch you later, dude!
────────────────────────────────────────────────────────────
```

### invalid temporal input
Console input:
```text
deadline invalid /by 2019-02-29
event reversed /from 2019-12-03 /to 2019-12-02
on 2019-02-30
list
bye
```
Console output:
```text
────────────────────────────────────────────────────────────
██████╗  ██╗   ██╗ ██████╗  ███████╗
██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝
██║  ██║ ██║   ██║ ██║  ██║ █████╗
██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝
██████╔╝ ╚██████╔╝╚██████╔╝ ███████╗
╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝
Hey! I'm DUDE, your dependable task buddy.
Dates can be represented in this format: yyyy-MM-dd.
To include a time, use this format: yyyy-MM-dd HHmm.
What can I help you with, dude?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Error: invalid by "2019-02-29" for deadline.
Expected: yyyy-MM-dd or yyyy-MM-dd HHmm.
Usage: deadline <description> /by [31m<yyyy-MM-dd [HHmm]>[0m
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Error: invalid to "2019-12-02" for event.
Expected: a date after the start date.
Usage: event <description> /from [31m<yyyy-MM-dd [HHmm]>[0m /to [31m<yyyy-MM-dd [HHmm]>[0m
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Error: invalid date "2019-02-30" for on.
Expected: yyyy-MM-dd.
Usage: on [31m<yyyy-MM-dd>[0m
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here's your task list, dude:
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Catch you later, dude!
────────────────────────────────────────────────────────────
```

### corrupted temporal records
Console input:
```text
list
bye
```
Console output:
```text
────────────────────────────────────────────────────────────
██████╗  ██╗   ██╗ ██████╗  ███████╗
██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝
██║  ██║ ██║   ██║ ██║  ██║ █████╗
██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝
██████╔╝ ╚██████╔╝╚██████╔╝ ███████╗
╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝
Hey! I'm DUDE, your dependable task buddy.
Dates can be represented in this format: yyyy-MM-dd.
To include a time, use this format: yyyy-MM-dd HHmm.
What can I help you with, dude?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here's your task list, dude:
1.[C][ ] [Corrupted: not json]
2.[C][ ] [Corrupted: {"type":"D","done":false,"description":"old","by":"Sunday"}]
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Catch you later, dude!
────────────────────────────────────────────────────────────
```

### find tasks by keyword
Console input:
```text
todo read book
deadline return book /by 2019-12-02
todo buy milk
find BOOK
find train
find
bye
```
Console output:
```text
────────────────────────────────────────────────────────────
██████╗  ██╗   ██╗ ██████╗  ███████╗
██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝
██║  ██║ ██║   ██║ ██║  ██║ █████╗
██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝
██████╔╝ ╚██████╔╝╚██████╔╝ ███████╗
╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝
Hey! I'm DUDE, your dependable task buddy.
Dates can be represented in this format: yyyy-MM-dd.
To include a time, use this format: yyyy-MM-dd HHmm.
What can I help you with, dude?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nice, dude — I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nice, dude — I've added this task:
  [D][ ] return book (by: Dec 02 2019)
Now you have 2 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nice, dude — I've added this task:
  [T][ ] buy milk
Now you have 3 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here are the tasks I found, dude:
1.[T][ ] read book
2.[D][ ] return book (by: Dec 02 2019)
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here are the tasks I found, dude:
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Error: invalid keyword <missing> for find.
Expected: a non-blank keyword.
Usage: find [31m<keyword>[0m
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Catch you later, dude!
────────────────────────────────────────────────────────────
```

### duplicate task resolution
Console input:
```text
todo read book
todo READ   BOOK
ad
todo Read Book
e
1
todo read book
c
list extra
bye
```
Console output:
```text
────────────────────────────────────────────────────────────
██████╗  ██╗   ██╗ ██████╗  ███████╗
██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝
██║  ██║ ██║   ██║ ██║  ██║ █████╗
██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝
██████╔╝ ╚██████╔╝╚██████╔╝ ███████╗
╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝
Hey! I'm DUDE, your dependable task buddy.
Dates can be represented in this format: yyyy-MM-dd.
To include a time, use this format: yyyy-MM-dd HHmm.
What can I help you with, dude?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nice, dude — I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
I found a task with the same description, dude:
1.[T][ ] read book
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Edit [E], add [A], or cancel [C]?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Nice, dude — I've added this task:
  [T][ ] READ   BOOK
Now you have 2 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
I found a task with the same description, dude:
1.[T][ ] read book
2.[T][ ] READ   BOOK
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Edit [E], add [A], or cancel [C]?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Which matching task number should I edit?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Updated, dude — I've replaced the matching task:
  [T][ ] Read Book
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
I found a task with the same description, dude:
1.[T][ ] Read Book
2.[T][ ] READ   BOOK
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Edit [E], add [A], or cancel [C]?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Error: invalid argument "extra" for list.
Expected: no arguments.
Usage: [31mlist[0m
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Catch you later, dude!
────────────────────────────────────────────────────────────
```
