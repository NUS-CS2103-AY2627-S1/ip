# UI Test Plan

Use this file with the `$test-ui` skill. Each command starts a fresh instance
of the program.

These cases cover the console interface only. Run each from the project root:
the sources are compiled with Gradle rather than a bare `javac`, because the
graphical classes need JavaFX on the compile classpath and only Gradle knows
where those jars are. The console entry point itself needs no JavaFX, so it
still runs from a plain classpath.

Every case runs `java` with `-ea`, which turns on the `assert` statements that
document the code's internal assumptions. The JVM ignores them otherwise, so
without the flag these cases could not tell a held assumption from a broken
one. No case expects an assertion to fail: an `AssertionError` anywhere in the
output is a bug in the program, not a rejected input.

The graphical interface is covered by the manual checklist at the end of this
file. It is deliberately short: the two front ends share all of their logic, so
the console cases above already exercise the behaviour, and only what is
genuinely specific to the window needs checking by hand.

## Test case: reject an empty todo description

**Aim:** Verify that an empty `todo` command displays a helpful error and the
program continues to accept the next command.

**Command:**

```text
.\gradlew.bat compileJava
$uiTestBuildDirectory = (Resolve-Path 'build\classes\java\main').Path
$uiTestRunDirectory = Join-Path $env:TEMP ([guid]::NewGuid().ToString())
New-Item -ItemType Directory -Force -Path $uiTestRunDirectory | Out-Null
Set-Location $uiTestRunDirectory
java -ea -cp $uiTestBuildDirectory peter.Peter
```

**Inputs:**

```text
todo
bye
```

**Expected output:**

```text
____________________________________________________________
 ____      _
|  _ \ ___| |_ ___ _ __
| |_) / _ \ __/ _ \ '__|
|  __/  __/ ||  __/ |
|_|   \___|\__\___|_|
Hey! I'm Peter, your friendly neighborhood task buddy.
What's on the web today?
____________________________________________________________
____________________________________________________________
Please include a description after 'todo'.
____________________________________________________________
____________________________________________________________
Swing by again soon. Your task web will be right here!
____________________________________________________________
```

## Test case: list an empty task web

**Aim:** Verify that `list` gives a helpful response when there are no tasks
instead of displaying an empty list heading.

**Command:**

```text
.\gradlew.bat compileJava
$uiTestBuildDirectory = (Resolve-Path 'build\classes\java\main').Path
$uiTestRunDirectory = Join-Path $env:TEMP ([guid]::NewGuid().ToString())
New-Item -ItemType Directory -Force -Path $uiTestRunDirectory | Out-Null
Set-Location $uiTestRunDirectory
java -ea -cp $uiTestBuildDirectory peter.Peter
```

**Inputs:**

```text
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____      _
|  _ \ ___| |_ ___ _ __
| |_) / _ \ __/ _ \ '__|
|  __/  __/ ||  __/ |
|_|   \___|\__\___|_|
Hey! I'm Peter, your friendly neighborhood task buddy.
What's on the web today?
____________________________________________________________
____________________________________________________________
Your task web is empty. Add a task to get started!
____________________________________________________________
____________________________________________________________
Swing by again soon. Your task web will be right here!
____________________________________________________________
```

## Test case: load saved tasks at startup

**Aim:** Verify that todo, deadline, and event tasks are loaded from a UTF-8
data file with a byte-order mark and retain their saved completion statuses.

**Command:**

```text
.\gradlew.bat compileJava
$uiTestBuildDirectory = (Resolve-Path 'build\classes\java\main').Path
$uiTestRunDirectory = Join-Path $env:TEMP ([guid]::NewGuid().ToString())
$uiTestDataDirectory = Join-Path $uiTestRunDirectory 'data'
New-Item -ItemType Directory -Force -Path $uiTestDataDirectory | Out-Null
$uiTestDataFile = Join-Path $uiTestDataDirectory 'peter.txt'
$uiTestData = @(
    'T | 1 | read book',
    'D | 0 | return book | 2019-06-06T18:00',
    'E | 1 | project meeting | 2019-08-06 | 2019-08-07'
)
$utf8WithBom = New-Object System.Text.UTF8Encoding($true)
[System.IO.File]::WriteAllLines($uiTestDataFile, $uiTestData, $utf8WithBom)
Set-Location $uiTestRunDirectory
java -ea -cp $uiTestBuildDirectory peter.Peter
```

**Inputs:**

```text
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____      _
|  _ \ ___| |_ ___ _ __
| |_) / _ \ __/ _ \ '__|
|  __/  __/ ||  __/ |
|_|   \___|\__\___|_|
Hey! I'm Peter, your friendly neighborhood task buddy.
What's on the web today?
____________________________________________________________
____________________________________________________________
Here's what's caught in your task web:
1.[T][X] read book
2.[D][ ] return book (by: Jun 6 2019, 6:00 PM)
3.[E][X] project meeting (from: Aug 6 2019 to: Aug 7 2019)
____________________________________________________________
____________________________________________________________
Swing by again soon. Your task web will be right here!
____________________________________________________________
```

## Test case: reject invalid mark and unmark task numbers

**Aim:** Verify that `mark` and `unmark` reject missing, non-numeric, and
out-of-range task numbers without ending the program.

**Command:**

```text
.\gradlew.bat compileJava
$uiTestBuildDirectory = (Resolve-Path 'build\classes\java\main').Path
$uiTestRunDirectory = Join-Path $env:TEMP ([guid]::NewGuid().ToString())
New-Item -ItemType Directory -Force -Path $uiTestRunDirectory | Out-Null
Set-Location $uiTestRunDirectory
java -ea -cp $uiTestBuildDirectory peter.Peter
```

**Inputs:**

```text
mark
unmark 1
todo read
mark first
unmark nope
mark 2
unmark 0
bye
```

**Expected output:**

```text
____________________________________________________________
 ____      _
|  _ \ ___| |_ ___ _ __
| |_) / _ \ __/ _ \ '__|
|  __/  __/ ||  __/ |
|_|   \___|\__\___|_|
Hey! I'm Peter, your friendly neighborhood task buddy.
What's on the web today?
____________________________________________________________
____________________________________________________________
Oh dear! Please provide a task number to mark.
____________________________________________________________
____________________________________________________________
Oh dear! There are no tasks to unmark.
____________________________________________________________
____________________________________________________________
Thwip! This task is on the web:
  [T][ ] read
You've got 1 task on the web.
____________________________________________________________
____________________________________________________________
Oh dear! Please enter an integer task number to mark.
____________________________________________________________
____________________________________________________________
Oh dear! Please enter an integer task number to unmark.
____________________________________________________________
____________________________________________________________
Oh dear! Task number must be between 1 and 1.
____________________________________________________________
____________________________________________________________
Oh dear! Task number must be between 1 and 1.
____________________________________________________________
____________________________________________________________
Swing by again soon. Your task web will be right here!
____________________________________________________________
```

## Test case: reject malformed event commands

**Aim:** Verify that an event command requires `/from` and `/to` in order,
along with a description, start date, and end date, and that the end date is
not allowed to fall before the start date.

**Command:**

```text
.\gradlew.bat compileJava
$uiTestBuildDirectory = (Resolve-Path 'build\classes\java\main').Path
$uiTestRunDirectory = Join-Path $env:TEMP ([guid]::NewGuid().ToString())
New-Item -ItemType Directory -Force -Path $uiTestRunDirectory | Out-Null
Set-Location $uiTestRunDirectory
java -ea -cp $uiTestBuildDirectory peter.Peter
```

**Inputs:**

```text
event meeting
event /from noon /to 1pm
event meeting /from /to 1pm
event meeting /from noon /to
event meeting /to 1pm /from noon
event camp /from 2019-12-05 /to 2019-12-02
event camp /from 2019-12-02 /to 2019-12-02
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____      _
|  _ \ ___| |_ ___ _ __
| |_) / _ \ __/ _ \ '__|
|  __/  __/ ||  __/ |
|_|   \___|\__\___|_|
Hey! I'm Peter, your friendly neighborhood task buddy.
What's on the web today?
____________________________________________________________
____________________________________________________________
Use 'event <description> /from <start-date> /to <end-date>'.
____________________________________________________________
____________________________________________________________
Please include a description before '/from'.
____________________________________________________________
____________________________________________________________
Please include a start date after '/from'.
____________________________________________________________
____________________________________________________________
Please include an end date after '/to'.
____________________________________________________________
____________________________________________________________
Use 'event <description> /from <start-date> /to <end-date>'.
____________________________________________________________
____________________________________________________________
Please make sure the end date is not before the start date.
____________________________________________________________
____________________________________________________________
Thwip! This task is on the web:
  [E][ ] camp (from: Dec 2 2019 to: Dec 2 2019)
You've got 1 task on the web.
____________________________________________________________
____________________________________________________________
Here's what's caught in your task web:
1.[E][ ] camp (from: Dec 2 2019 to: Dec 2 2019)
____________________________________________________________
____________________________________________________________
Swing by again soon. Your task web will be right here!
____________________________________________________________
```

## Test case: reject malformed deadline commands

**Aim:** Verify that a deadline command requires the `/by` marker, a
description, and a due date, while allowing the program to continue afterward.

**Command:**

```text
.\gradlew.bat compileJava
$uiTestBuildDirectory = (Resolve-Path 'build\classes\java\main').Path
$uiTestRunDirectory = Join-Path $env:TEMP ([guid]::NewGuid().ToString())
New-Item -ItemType Directory -Force -Path $uiTestRunDirectory | Out-Null
Set-Location $uiTestRunDirectory
java -ea -cp $uiTestBuildDirectory peter.Peter
```

**Inputs:**

```text
deadline task
deadline /by tomorrow
deadline task /by
bye
```

**Expected output:**

```text
____________________________________________________________
 ____      _
|  _ \ ___| |_ ___ _ __
| |_) / _ \ __/ _ \ '__|
|  __/  __/ ||  __/ |
|_|   \___|\__\___|_|
Hey! I'm Peter, your friendly neighborhood task buddy.
What's on the web today?
____________________________________________________________
____________________________________________________________
Use 'deadline <description> /by <date>'.
____________________________________________________________
____________________________________________________________
Please include a description before '/by'.
____________________________________________________________
____________________________________________________________
Please include a due date after '/by'.
____________________________________________________________
____________________________________________________________
Swing by again soon. Your task web will be right here!
____________________________________________________________
```

## Test case: parse and format schedule dates and times

**Aim:** Verify that schedules accept `yyyy-MM-dd` dates and `d/M/yyyy HHmm`
date-times, display them clearly, and reject malformed or impossible values.

**Command:**

```text
.\gradlew.bat compileJava
$uiTestBuildDirectory = (Resolve-Path 'build\classes\java\main').Path
$uiTestRunDirectory = Join-Path $env:TEMP ([guid]::NewGuid().ToString())
New-Item -ItemType Directory -Force -Path $uiTestRunDirectory | Out-Null
Set-Location $uiTestRunDirectory
java -ea -cp $uiTestBuildDirectory peter.Peter
```

**Inputs:**

```text
deadline return book /by 2019-02-29
event holiday /from 2019-12-01 /to tomorrow
deadline return book /by 2/12/2019 1800
event holiday /from 1/12/2019 0900 /to 2/12/2019 1730
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____      _
|  _ \ ___| |_ ___ _ __
| |_) / _ \ __/ _ \ '__|
|  __/  __/ ||  __/ |
|_|   \___|\__\___|_|
Hey! I'm Peter, your friendly neighborhood task buddy.
What's on the web today?
____________________________________________________________
____________________________________________________________
Please enter the due date as yyyy-MM-dd or d/M/yyyy HHmm (e.g., 2019-10-15 or 2/12/2019 1800).
____________________________________________________________
____________________________________________________________
Please enter the end date as yyyy-MM-dd or d/M/yyyy HHmm (e.g., 2019-10-15 or 2/12/2019 1800).
____________________________________________________________
____________________________________________________________
Thwip! This task is on the web:
  [D][ ] return book (by: Dec 2 2019, 6:00 PM)
You've got 1 task on the web.
____________________________________________________________
____________________________________________________________
Thwip! This task is on the web:
  [E][ ] holiday (from: Dec 1 2019, 9:00 AM to: Dec 2 2019, 5:30 PM)
You've got 2 tasks on the web.
____________________________________________________________
____________________________________________________________
Here's what's caught in your task web:
1.[D][ ] return book (by: Dec 2 2019, 6:00 PM)
2.[E][ ] holiday (from: Dec 1 2019, 9:00 AM to: Dec 2 2019, 5:30 PM)
____________________________________________________________
____________________________________________________________
Swing by again soon. Your task web will be right here!
____________________________________________________________
```

## Test case: find scheduled tasks on a date

**Aim:** Verify that `on` finds deadlines and events occurring on an ISO date,
uses original task numbers, includes event date ranges, and handles invalid or
unmatched dates.

**Command:**

```text
.\gradlew.bat compileJava
$uiTestBuildDirectory = (Resolve-Path 'build\classes\java\main').Path
$uiTestRunDirectory = Join-Path $env:TEMP ([guid]::NewGuid().ToString())
$uiTestDataDirectory = Join-Path $uiTestRunDirectory 'data'
New-Item -ItemType Directory -Force -Path $uiTestDataDirectory | Out-Null
$uiTestDataFile = Join-Path $uiTestDataDirectory 'peter.txt'
$uiTestData = @(
    'T | 0 | read book',
    'D | 0 | return book | 2019-12-02T18:00',
    'E | 1 | holiday | 2019-12-01T09:00 | 2019-12-03T17:30',
    'E | 0 | meeting | 2019-12-03T09:00 | 2019-12-03T10:00'
)
$utf8WithoutBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllLines($uiTestDataFile, $uiTestData, $utf8WithoutBom)
Set-Location $uiTestRunDirectory
java -ea -cp $uiTestBuildDirectory peter.Peter
```

**Inputs:**

```text
on
on tomorrow
on 2019-12-02
on 2019-12-03
on 2019-12-04
bye
```

**Expected output:**

```text
____________________________________________________________
 ____      _
|  _ \ ___| |_ ___ _ __
| |_) / _ \ __/ _ \ '__|
|  __/  __/ ||  __/ |
|_|   \___|\__\___|_|
Hey! I'm Peter, your friendly neighborhood task buddy.
What's on the web today?
____________________________________________________________
____________________________________________________________
Use 'on <date>' (e.g., on 2019-12-02).
____________________________________________________________
____________________________________________________________
Please enter the date in yyyy-MM-dd format (e.g., 2019-12-02).
____________________________________________________________
____________________________________________________________
Tasks swinging in on Dec 2 2019:
2.[D][ ] return book (by: Dec 2 2019, 6:00 PM)
3.[E][X] holiday (from: Dec 1 2019, 9:00 AM to: Dec 3 2019, 5:30 PM)
____________________________________________________________
____________________________________________________________
Tasks swinging in on Dec 3 2019:
3.[E][X] holiday (from: Dec 1 2019, 9:00 AM to: Dec 3 2019, 5:30 PM)
4.[E][ ] meeting (from: Dec 3 2019, 9:00 AM to: Dec 3 2019, 10:00 AM)
____________________________________________________________
____________________________________________________________
No tasks are swinging in on Dec 4 2019.
____________________________________________________________
____________________________________________________________
Swing by again soon. Your task web will be right here!
____________________________________________________________
```

## Test case: find tasks by keyword

**Aim:** Verify that `find` matches every one of several keywords anywhere in a
task description, in any order and without needing them adjacent, keeps
original task numbers, reports when nothing matches, and rejects a missing
keyword. Case folding and per-keyword partial matching are covered by the unit
tests rather than repeated here.

**Command:**

```text
.\gradlew.bat compileJava
$uiTestBuildDirectory = (Resolve-Path 'build\classes\java\main').Path
$uiTestRunDirectory = Join-Path $env:TEMP ([guid]::NewGuid().ToString())
$uiTestDataDirectory = Join-Path $uiTestRunDirectory 'data'
New-Item -ItemType Directory -Force -Path $uiTestDataDirectory | Out-Null
$uiTestDataFile = Join-Path $uiTestDataDirectory 'peter.txt'
$uiTestData = @(
    'T | 1 | read book',
    'D | 1 | return book | 2019-06-06T00:00',
    'E | 0 | book fair | 2019-08-06T00:00 | 2019-08-07T00:00',
    'T | 0 | buy milk',
    'T | 0 | read a book'
)
$utf8WithoutBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllLines($uiTestDataFile, $uiTestData, $utf8WithoutBom)
Set-Location $uiTestRunDirectory
java -ea -cp $uiTestBuildDirectory peter.Peter
```

**Inputs:**

```text
find
find book
find read book
find book read
find book milk
bye
```

**Expected output:**

```text
____________________________________________________________
 ____      _
|  _ \ ___| |_ ___ _ __
| |_) / _ \ __/ _ \ '__|
|  __/  __/ ||  __/ |
|_|   \___|\__\___|_|
Hey! I'm Peter, your friendly neighborhood task buddy.
What's on the web today?
____________________________________________________________
____________________________________________________________
Use 'find <keywords>' (e.g., find read book).
____________________________________________________________
____________________________________________________________
My spider-sense found these matches:
1.[T][X] read book
2.[D][X] return book (by: Jun 6 2019)
3.[E][ ] book fair (from: Aug 6 2019 to: Aug 7 2019)
5.[T][ ] read a book
____________________________________________________________
____________________________________________________________
My spider-sense found these matches:
1.[T][X] read book
5.[T][ ] read a book
____________________________________________________________
____________________________________________________________
My spider-sense found these matches:
1.[T][X] read book
5.[T][ ] read a book
____________________________________________________________
____________________________________________________________
My spider-sense couldn't find a match.
____________________________________________________________
____________________________________________________________
Swing by again soon. Your task web will be right here!
____________________________________________________________
```

## Test case: reject an unknown command

**Aim:** Verify that an unknown command displays a helpful error and is not
added to the task list.

**Command:**

```text
.\gradlew.bat compileJava
$uiTestBuildDirectory = (Resolve-Path 'build\classes\java\main').Path
$uiTestRunDirectory = Join-Path $env:TEMP ([guid]::NewGuid().ToString())
New-Item -ItemType Directory -Force -Path $uiTestRunDirectory | Out-Null
Set-Location $uiTestRunDirectory
java -ea -cp $uiTestBuildDirectory peter.Peter
```

**Inputs:**

```text
blah
bye
```

**Expected output:**

```text
____________________________________________________________
 ____      _
|  _ \ ___| |_ ___ _ __
| |_) / _ \ __/ _ \ '__|
|  __/  __/ ||  __/ |
|_|   \___|\__\___|_|
Hey! I'm Peter, your friendly neighborhood task buddy.
What's on the web today?
____________________________________________________________
____________________________________________________________
My spider-sense doesn't recognize that command. Please try again.
____________________________________________________________
____________________________________________________________
Swing by again soon. Your task web will be right here!
____________________________________________________________
```

## Test case: delete a task from the list

**Aim:** Verify that `delete` removes the selected task, displays its full
details, updates the task count, and renumbers the remaining tasks.

**Command:**

```text
.\gradlew.bat compileJava
$uiTestBuildDirectory = (Resolve-Path 'build\classes\java\main').Path
$uiTestRunDirectory = Join-Path $env:TEMP ([guid]::NewGuid().ToString())
New-Item -ItemType Directory -Force -Path $uiTestRunDirectory | Out-Null
Set-Location $uiTestRunDirectory
java -ea -cp $uiTestBuildDirectory peter.Peter
```

**Inputs:**

```text
todo read book
event project meeting /from 2019-08-06 /to 2019-08-07
deadline return book /by 2019-06-06
delete 2
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____      _
|  _ \ ___| |_ ___ _ __
| |_) / _ \ __/ _ \ '__|
|  __/  __/ ||  __/ |
|_|   \___|\__\___|_|
Hey! I'm Peter, your friendly neighborhood task buddy.
What's on the web today?
____________________________________________________________
____________________________________________________________
Thwip! This task is on the web:
  [T][ ] read book
You've got 1 task on the web.
____________________________________________________________
____________________________________________________________
Thwip! This task is on the web:
  [E][ ] project meeting (from: Aug 6 2019 to: Aug 7 2019)
You've got 2 tasks on the web.
____________________________________________________________
____________________________________________________________
Thwip! This task is on the web:
  [D][ ] return book (by: Jun 6 2019)
You've got 3 tasks on the web.
____________________________________________________________
____________________________________________________________
Snip! This task is off the web:
  [E][ ] project meeting (from: Aug 6 2019 to: Aug 7 2019)
You've got 2 tasks on the web.
____________________________________________________________
____________________________________________________________
Here's what's caught in your task web:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 6 2019)
____________________________________________________________
____________________________________________________________
Swing by again soon. Your task web will be right here!
____________________________________________________________
```

## Test case: reject invalid delete task numbers

**Aim:** Verify that `delete` rejects missing, non-integer, and out-of-range
task numbers, handles an empty list, and continues accepting commands.

**Command:**

```text
.\gradlew.bat compileJava
$uiTestBuildDirectory = (Resolve-Path 'build\classes\java\main').Path
$uiTestRunDirectory = Join-Path $env:TEMP ([guid]::NewGuid().ToString())
New-Item -ItemType Directory -Force -Path $uiTestRunDirectory | Out-Null
Set-Location $uiTestRunDirectory
java -ea -cp $uiTestBuildDirectory peter.Peter
```

**Inputs:**

```text
delete
delete 1
todo read book
delete first
delete 0
delete 2
delete 1
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____      _
|  _ \ ___| |_ ___ _ __
| |_) / _ \ __/ _ \ '__|
|  __/  __/ ||  __/ |
|_|   \___|\__\___|_|
Hey! I'm Peter, your friendly neighborhood task buddy.
What's on the web today?
____________________________________________________________
____________________________________________________________
Oh dear! Please provide a task number to delete.
____________________________________________________________
____________________________________________________________
Oh dear! There are no tasks to delete.
____________________________________________________________
____________________________________________________________
Thwip! This task is on the web:
  [T][ ] read book
You've got 1 task on the web.
____________________________________________________________
____________________________________________________________
Oh dear! Please enter an integer task number to delete.
____________________________________________________________
____________________________________________________________
Oh dear! Task number must be between 1 and 1.
____________________________________________________________
____________________________________________________________
Oh dear! Task number must be between 1 and 1.
____________________________________________________________
____________________________________________________________
Snip! This task is off the web:
  [T][ ] read book
You've got 0 tasks on the web.
____________________________________________________________
____________________________________________________________
Your task web is empty. Add a task to get started!
____________________________________________________________
____________________________________________________________
Swing by again soon. Your task web will be right here!
____________________________________________________________
```

## Test case: mark, unmark, and delete a task

**Aim:** Verify that a task can be marked and unmarked, that `list` displays
the updated status, and that deleting a completed task preserves its status.

**Command:**

```text
.\gradlew.bat compileJava
$uiTestBuildDirectory = (Resolve-Path 'build\classes\java\main').Path
$uiTestRunDirectory = Join-Path $env:TEMP ([guid]::NewGuid().ToString())
New-Item -ItemType Directory -Force -Path $uiTestRunDirectory | Out-Null
Set-Location $uiTestRunDirectory
java -ea -cp $uiTestBuildDirectory peter.Peter
```

**Inputs:**

```text
todo read book
mark 1
list
unmark 1
list
mark 1
delete 1
bye
```

**Expected output:**

```text
____________________________________________________________
 ____      _
|  _ \ ___| |_ ___ _ __
| |_) / _ \ __/ _ \ '__|
|  __/  __/ ||  __/ |
|_|   \___|\__\___|_|
Hey! I'm Peter, your friendly neighborhood task buddy.
What's on the web today?
____________________________________________________________
____________________________________________________________
Thwip! This task is on the web:
  [T][ ] read book
You've got 1 task on the web.
____________________________________________________________
____________________________________________________________
Amazing! This task is wrapped up:
  [X] read book
____________________________________________________________
____________________________________________________________
Here's what's caught in your task web:
1.[T][X] read book
____________________________________________________________
____________________________________________________________
Back on the web! This task is active again:
  [ ] read book
____________________________________________________________
____________________________________________________________
Here's what's caught in your task web:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Amazing! This task is wrapped up:
  [X] read book
____________________________________________________________
____________________________________________________________
Snip! This task is off the web:
  [T][X] read book
You've got 0 tasks on the web.
____________________________________________________________
____________________________________________________________
Swing by again soon. Your task web will be right here!
____________________________________________________________
```

## Test case: reject whitespace-only structured fields

**Aim:** Verify that deadline and event fields containing only whitespace are
rejected instead of being accepted as meaningful task details.

**Command:**

```text
.\gradlew.bat compileJava
$uiTestBuildDirectory = (Resolve-Path 'build\classes\java\main').Path
$uiTestRunDirectory = Join-Path $env:TEMP ([guid]::NewGuid().ToString())
New-Item -ItemType Directory -Force -Path $uiTestRunDirectory | Out-Null
Set-Location $uiTestRunDirectory
java -ea -cp $uiTestBuildDirectory peter.Peter
```

**Inputs:**

```text
deadline   /by tomorrow
event   /from noon /to 1pm
event meeting /from   /to 1pm
bye
```

**Expected output:**

```text
____________________________________________________________
 ____      _
|  _ \ ___| |_ ___ _ __
| |_) / _ \ __/ _ \ '__|
|  __/  __/ ||  __/ |
|_|   \___|\__\___|_|
Hey! I'm Peter, your friendly neighborhood task buddy.
What's on the web today?
____________________________________________________________
____________________________________________________________
Please include a description before '/by'.
____________________________________________________________
____________________________________________________________
Please include a description before '/from'.
____________________________________________________________
____________________________________________________________
Please include a start date after '/from'.
____________________________________________________________
____________________________________________________________
Swing by again soon. Your task web will be right here!
____________________________________________________________
```

## Test case: save changes without altering console output

**Aim:** Verify that adding, marking, and deleting tasks still produces the
expected console interaction while each successful change is saved silently.

**Command:**

```text
.\gradlew.bat compileJava
$uiTestBuildDirectory = (Resolve-Path 'build\classes\java\main').Path
$uiTestRunDirectory = Join-Path $env:TEMP ([guid]::NewGuid().ToString())
New-Item -ItemType Directory -Force -Path $uiTestRunDirectory | Out-Null
Set-Location $uiTestRunDirectory
java -ea -cp $uiTestBuildDirectory peter.Peter
```

**Inputs:**

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-07
mark 1
delete 2
bye
```

**Expected output:**

```text
____________________________________________________________
 ____      _
|  _ \ ___| |_ ___ _ __
| |_) / _ \ __/ _ \ '__|
|  __/  __/ ||  __/ |
|_|   \___|\__\___|_|
Hey! I'm Peter, your friendly neighborhood task buddy.
What's on the web today?
____________________________________________________________
____________________________________________________________
Thwip! This task is on the web:
  [T][ ] read book
You've got 1 task on the web.
____________________________________________________________
____________________________________________________________
Thwip! This task is on the web:
  [D][ ] return book (by: Jun 6 2019)
You've got 2 tasks on the web.
____________________________________________________________
____________________________________________________________
Thwip! This task is on the web:
  [E][ ] project meeting (from: Aug 6 2019 to: Aug 7 2019)
You've got 3 tasks on the web.
____________________________________________________________
____________________________________________________________
Amazing! This task is wrapped up:
  [X] read book
____________________________________________________________
____________________________________________________________
Snip! This task is off the web:
  [D][ ] return book (by: Jun 6 2019)
You've got 2 tasks on the web.
____________________________________________________________
____________________________________________________________
Swing by again soon. Your task web will be right here!
____________________________________________________________
```

## Test case: reject malformed saved task data

**Aim:** Verify that invalid saved records identify the affected line and stop
startup gracefully without displaying a Java exception.

**Command:**

```text
.\gradlew.bat compileJava
$uiTestBuildDirectory = (Resolve-Path 'build\classes\java\main').Path
$uiTestRunDirectory = Join-Path $env:TEMP ([guid]::NewGuid().ToString())
$uiTestDataDirectory = Join-Path $uiTestRunDirectory 'data'
New-Item -ItemType Directory -Force -Path $uiTestDataDirectory | Out-Null
$uiTestDataFile = Join-Path $uiTestDataDirectory 'peter.txt'
$uiTestData = @('', 'T | 0 | read book', 'D | maybe | return book | June 6th')
$utf8WithoutBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllLines($uiTestDataFile, $uiTestData, $utf8WithoutBom)
Set-Location $uiTestRunDirectory
java -ea -cp $uiTestBuildDirectory peter.Peter
```

**Inputs:**

```text
```

**Expected output:**

```text
____________________________________________________________
 ____      _
|  _ \ ___| |_ ___ _ __
| |_) / _ \ __/ _ \ '__|
|  __/  __/ ||  __/ |
|_|   \___|\__\___|_|
Hey! I'm Peter, your friendly neighborhood task buddy.
What's on the web today?
____________________________________________________________
Oh dear! The task data file is invalid at line 3. Please fix or remove it before restarting me!
____________________________________________________________
```

## Test case: handle an unreadable task data path

**Aim:** Verify that a data path that cannot be read as a file produces a
helpful message and stops startup gracefully.

**Command:**

```text
.\gradlew.bat compileJava
$uiTestBuildDirectory = (Resolve-Path 'build\classes\java\main').Path
$uiTestRunDirectory = Join-Path $env:TEMP ([guid]::NewGuid().ToString())
$uiTestDataPath = Join-Path $uiTestRunDirectory 'data\peter.txt'
New-Item -ItemType Directory -Force -Path $uiTestDataPath | Out-Null
Set-Location $uiTestRunDirectory
java -ea -cp $uiTestBuildDirectory peter.Peter
```

**Inputs:**

```text
```

**Expected output:**

```text
____________________________________________________________
 ____      _
|  _ \ ___| |_ ___ _ __
| |_) / _ \ __/ _ \ '__|
|  __/  __/ ||  __/ |
|_|   \___|\__\___|_|
Hey! I'm Peter, your friendly neighborhood task buddy.
What's on the web today?
____________________________________________________________
Oh dear! I couldn't read the task data file. Please check that it is accessible.
____________________________________________________________
```

## Test case: roll back a task when saving fails

**Aim:** Verify that a failed save displays a helpful message, keeps the
chatbot running, and does not leave an unsaved task in memory.

**Command:**

```text
.\gradlew.bat compileJava
$uiTestBuildDirectory = (Resolve-Path 'build\classes\java\main').Path
$uiTestRunDirectory = Join-Path $env:TEMP ([guid]::NewGuid().ToString())
New-Item -ItemType Directory -Force -Path $uiTestRunDirectory | Out-Null
New-Item -ItemType File -Path (Join-Path $uiTestRunDirectory 'data') | Out-Null
Set-Location $uiTestRunDirectory
java -ea -cp $uiTestBuildDirectory peter.Peter
```

**Inputs:**

```text
todo read book
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____      _
|  _ \ ___| |_ ___ _ __
| |_) / _ \ __/ _ \ '__|
|  __/  __/ ||  __/ |
|_|   \___|\__\___|_|
Hey! I'm Peter, your friendly neighborhood task buddy.
What's on the web today?
____________________________________________________________
____________________________________________________________
Oh dear! I couldn't save your tasks. Please check that the data folder is writable.
____________________________________________________________
____________________________________________________________
Your task web is empty. Add a task to get started!
____________________________________________________________
____________________________________________________________
Swing by again soon. Your task web will be right here!
____________________________________________________________
```

## Test case: reject the storage delimiter in task details

**Aim:** Verify that task details cannot contain the data-file delimiter and
therefore cannot create records that fail during the next startup.

**Command:**

```text
.\gradlew.bat compileJava
$uiTestBuildDirectory = (Resolve-Path 'build\classes\java\main').Path
$uiTestRunDirectory = Join-Path $env:TEMP ([guid]::NewGuid().ToString())
New-Item -ItemType Directory -Force -Path $uiTestRunDirectory | Out-Null
Set-Location $uiTestRunDirectory
java -ea -cp $uiTestBuildDirectory peter.Peter
```

**Inputs:**

```text
todo read | book
deadline return book /by June | 6th
event project meeting /from 2 | pm /to 4pm
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____      _
|  _ \ ___| |_ ___ _ __
| |_) / _ \ __/ _ \ '__|
|  __/  __/ ||  __/ |
|_|   \___|\__\___|_|
Hey! I'm Peter, your friendly neighborhood task buddy.
What's on the web today?
____________________________________________________________
____________________________________________________________
Oh dear! Task details cannot contain ' | '.
____________________________________________________________
____________________________________________________________
Oh dear! Task details cannot contain ' | '.
____________________________________________________________
____________________________________________________________
Oh dear! Task details cannot contain ' | '.
____________________________________________________________
____________________________________________________________
Your task web is empty. Add a task to get started!
____________________________________________________________
____________________________________________________________
Swing by again soon. Your task web will be right here!
____________________________________________________________
```

# Manual GUI Checklist

The graphical interface cannot be driven by piping text at it, so these are
checked by hand. Start each one with `./gradlew run` from the project root, or
`java -ea -jar build/libs/peter.jar` when checking a release.

Run these after any change under `src/main/java/peter/gui` or
`src/main/resources`. Changes to the task, parser or storage code are covered
by the console cases above.

| # | Check | Expected |
|---|---|---|
| 1 | Open the window | A window titled "Peter - Friendly Neighborhood Task Buddy" appears with a red spider-mask application icon. The red-and-blue web background fills the entire conversation viewport immediately, including space below the greeting. No banner or divider lines: those belong to the console. |
| 2 | Type `list` with no saved tasks and press Enter | Your command appears on the right with your picture; Peter replies `Your task web is empty. Add a task to get started!` on the left. The input box clears. |
| 3 | Click **Send** with text in the box | Behaves exactly as pressing Enter did. |
| 4 | Press Enter on an empty box | Nothing is added to the transcript. |
| 5 | Add a task with a very long description, then `list` | The reply wraps onto several lines. No text is cut off at the right edge. |
| 6 | Keep sending commands until the transcript overflows | The view follows the newest message; you never have to scroll down by hand. |
| 7 | Resize the window narrower | Existing bubbles re-wrap. The input box and Send button stay at the bottom. |
| 8 | Type `bye` | The farewell appears, the input box and Send button grey out, and the window closes about a second later. |
| 9 | Corrupt `data/peter.txt` (e.g. change a status field to `maybe`) and restart | The greeting is followed by an error naming the bad line, and both controls are disabled so no command can overwrite the file. |
| 10 | Delete `data/peter.txt` and restart | Peter greets you with an empty list and no error. A missing file is not a failure. |
| 11 | Run the jar from an empty folder | The window opens; a `data` folder is created there on the first change, not at startup. |
