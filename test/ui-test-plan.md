# UI Test Plan

These tests cover the console interaction layer. Check the graphical
interface manually by launching `./gradlew run` and resizing the window.

## Test command

Run from the project root:

```sh
./gradlew classes
java -cp build/classes/java/main kdb.Kdb
```

Use a temporary copy of `data/tasks.txt` if the test should not change saved
tasks.

## 1. Exits on `bye`

**Aim:** Confirm that KDB displays the ASCII banner and exits after the
farewell command.

**Input:** `bye`

**Expected output:** The banner and command guide are displayed, followed by
`Full time. See you next match!`, and the program exits.

## 2. Saves and deletes a task

**Aim:** Confirm that adding and deleting a task updates and persists the
task list.

**Input:**

```text
todo buy milk
high
delete 1
bye
```

**Expected output:** KDB displays `Perfect pass! I've added this task:`, shows
the task, then displays `Cleared from the pitch! I've removed this task:` and
finishes normally.

## 3. Finds matching tasks

**Aim:** Confirm that `find` searches descriptions case-insensitively.

**Input:**

```text
todo read book
medium
deadline return book /by 2/12/2019 1800
low
find BOOK
bye
```

**Expected output:** Both tasks are listed after `Here are the matching
plays:`.

## 4. Rejects invalid commands

**Aim:** Confirm that invalid commands do not terminate KDB.

**Input:**

```text
unknown command
find
bye
```

**Expected output:** KDB displays command help, reports the missing keyword,
and exits normally. In the GUI, errors use the red error style.

## 5. Handles invalid task data

**Aim:** Confirm that malformed task-file entries are handled safely.

**Preparation:** Place an invalid status or malformed deadline in a temporary
task file and start KDB with that file.

**Expected output:** KDB reports the loading problem and starts with an empty
task list instead of crashing.
