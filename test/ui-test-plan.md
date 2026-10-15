# Command-Line UI Test Plan

This plan checks the text interface that shares Gnaix's parser, task logic,
and storage implementation with the JavaFX interface.

## Setup

- Working directory: project root
- Java version: Java 25
- Build command: `.\gradlew.bat clean classes`
- Test data: one isolated directory under `build/ui-test/` per test case

After building, create the isolated working directories:

```powershell
1..5 | ForEach-Object {
    New-Item -ItemType Directory -Force "build/ui-test/case-$_" | Out-Null
}
```

Run the test cases in order. Each `java` command is started from its case
directory, so Gnaix writes `data/gnaix.txt` below `build/` instead of changing
the user's normal task data. Enter the listed inputs after starting Gnaix,
then return to the project root with `Pop-Location` after Gnaix exits.

## Test Case 1: Add and list a todo

**Aim:** Verify that a todo can be added and displayed in the task list.

**Command:**

```powershell
Push-Location build/ui-test/case-1
java -cp ../../classes/java/main gnaix.Gnaix
Pop-Location
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
  ____ _   _    _    _____  __
 / ___| \ | |  / \  |_ _\ \/ /
| |  _|  \| | / _ \  | | \  /
| |_| | |\  |/ ___ \ | | /  \
 \____|_| \_/_/   \_\___/_/\_\
Hello. I'm Gnaix.
What do you need?
____________________________________________________________
Fine. I've added this:
  [T][ ] read book
You now have 1 tasks.
____________________________________________________________
Here. Your current list:
1. [T][ ] read book
____________________________________________________________
____________________________________________________________
All right. Goodbye.
Try not to make more work for me.
____________________________________________________________
```

## Test Case 2: Add and search dated tasks

**Aim:** Verify deadline and event parsing and date-based search.

**Command:**

```powershell
Push-Location build/ui-test/case-2
java -cp ../../classes/java/main gnaix.Gnaix
Pop-Location
```

**Inputs:**

```text
deadline submit report /by 2026-09-20
event project meeting /from 2026-09-20 1400 /to 2026-09-20 1530
date 2026-09-20
bye
```

**Expected output:**

```text
____________________________________________________________
  ____ _   _    _    _____  __
 / ___| \ | |  / \  |_ _\ \/ /
| |  _|  \| | / _ \  | | \  /
| |_| | |\  |/ ___ \ | | /  \
 \____|_| \_/_/   \_\___/_/\_\
Hello. I'm Gnaix.
What do you need?
____________________________________________________________
Fine. I've added this:
  [D][ ] submit report (by: Sep 20 2026)
You now have 1 tasks.
____________________________________________________________
Fine. I've added this:
  [E][ ] project meeting (from: Sep 20 2026 14:00 to: Sep 20 2026 15:30)
You now have 2 tasks.
____________________________________________________________
These are the tasks scheduled for that date:
[D][ ] submit report (by: Sep 20 2026)
[E][ ] project meeting (from: Sep 20 2026 14:00 to: Sep 20 2026 15:30)
____________________________________________________________
____________________________________________________________
All right. Goodbye.
Try not to make more work for me.
____________________________________________________________
```

## Test Case 3: Change and delete a task

**Aim:** Verify that completion changes and deletion update the task list.

**Command:**

```powershell
Push-Location build/ui-test/case-3
java -cp ../../classes/java/main gnaix.Gnaix
Pop-Location
```

**Inputs:**

```text
todo first task
todo second task
mark 1
unmark 1
delete 2
list
bye
```

**Expected output:**

```text
____________________________________________________________
  ____ _   _    _    _____  __
 / ___| \ | |  / \  |_ _\ \/ /
| |  _|  \| | / _ \  | | \  /
| |_| | |\  |/ ___ \ | | /  \
 \____|_| \_/_/   \_\___/_/\_\
Hello. I'm Gnaix.
What do you need?
____________________________________________________________
Fine. I've added this:
  [T][ ] first task
You now have 1 tasks.
____________________________________________________________
Fine. I've added this:
  [T][ ] second task
You now have 2 tasks.
____________________________________________________________
There. It's done:
  [T][X] first task
____________________________________________________________
Apparently we're undoing that.
  [T][ ] first task
____________________________________________________________
Gone. I've removed this:
  [T][ ] second task
You now have 1 tasks left.
____________________________________________________________
Here. Your current list:
1. [T][ ] first task
____________________________________________________________
____________________________________________________________
All right. Goodbye.
Try not to make more work for me.
____________________________________________________________
```

## Test Case 4: Search descriptions and tags

**Aim:** Verify case-insensitive description search and multi-tag matching.

**Command:**

```powershell
Push-Location build/ui-test/case-4
java -cp ../../classes/java/main gnaix.Gnaix
Pop-Location
```

**Inputs:**

```text
todo Prepare Slides #school #urgent
todo buy groceries #errands
find slides
tag #school #urgent
bye
```

**Expected output:**

```text
____________________________________________________________
  ____ _   _    _    _____  __
 / ___| \ | |  / \  |_ _\ \/ /
| |  _|  \| | / _ \  | | \  /
| |_| | |\  |/ ___ \ | | /  \
 \____|_| \_/_/   \_\___/_/\_\
Hello. I'm Gnaix.
What do you need?
____________________________________________________________
Fine. I've added this:
  [T][ ] Prepare Slides #school #urgent
You now have 1 tasks.
____________________________________________________________
Fine. I've added this:
  [T][ ] buy groceries #errands
You now have 2 tasks.
____________________________________________________________
These are the tasks that match your search:
1. [T][ ] Prepare Slides #school #urgent
____________________________________________________________
These are the tasks carrying those tags:
1. [T][ ] Prepare Slides #school #urgent
____________________________________________________________
____________________________________________________________
All right. Goodbye.
Try not to make more work for me.
____________________________________________________________
```

## Test Case 5: Reject invalid input

**Aim:** Verify useful errors for malformed dates and task numbers.

**Command:**

```powershell
Push-Location build/ui-test/case-5
java -cp ../../classes/java/main gnaix.Gnaix
Pop-Location
```

**Inputs:**

```text
deadline submit report /by tomorrow
mark abc
delete 1
bye
```

**Expected output:**

```text
____________________________________________________________
  ____ _   _    _    _____  __
 / ___| \ | |  / \  |_ _\ \/ /
| |  _|  \| | / _ \  | | \  /
| |_| | |\  |/ ___ \ | | /  \
 \____|_| \_/_/   \_\___/_/\_\
Hello. I'm Gnaix.
What do you need?
____________________________________________________________
That date doesn't work.
Use yyyy-MM-dd.
____________________________________________________________
Which task?
You'll need to give me a task number.
____________________________________________________________
Which task?
You'll need to give me a task number.
____________________________________________________________
____________________________________________________________
All right. Goodbye.
Try not to make more work for me.
____________________________________________________________
```

## Passing criteria

A test case passes when its output matches the expected output exactly,
excluding the commands typed by the tester and incidental shell prompts.
