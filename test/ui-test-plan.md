# UI Test Plan

- Program command: `powershell -NoProfile -ExecutionPolicy Bypass -File test/reset-data-and-run.ps1`
- Working directory: `.`
- Java requirement: Java 25
- Setup command: `$javaSources = Get-ChildItem -Path src/main/java -Recurse -Filter *.java | Where-Object { $_.BaseName -notin @('DialogBox', 'Launcher', 'Main', 'MainWindow') } | ForEach-Object { $_.FullName }; javac -d out $javaSources`
- Session log: `test/ui-test-session.log`

Run this plan through the project-specific `test-ui` workflow. That workflow
checks the Java version and runs the setup command before invoking the runner;
invoking the runner directly assumes those prerequisites are already complete.

The setup command excludes JavaFX-specific classes because these tests launch
the console entry point directly. The runner starts a fresh process for each
test case. The reset helper seeds a two-record archive fixture only for the
dedicated `restore 1` fixture case below. The launcher runs each process in a unique temporary working
directory with its own `data/herta.txt`, so persisted tasks from one case do
not affect another case and the project's data file remains untouched. The
temporary directory is removed after the process exits. It sends the input
commands in order and compares the complete console output
with the expected output. Add one command per line in each `Inputs` block. The
output below is intentionally kept exact, including prompts, indentation, and
separators.

Saved-task loading is covered separately in `test/load-ui-test-plan.md` because
that plan supplies a persisted data fixture before startup.

## Coverage summary

| Behavior | Valid input | Missing or malformed input | Boundary or state check |
| --- | --- | --- | --- |
| Start and exit | `bye` | End of input | Fresh process per case |
| List tasks | Populated list | Empty list | State checked after errors |
| Find tasks | Matching keyword in descriptions | Empty keyword or no matches | Original task numbering and order are preserved |
| Add tasks | Todo, deadline, and event with valid date/time or date-only input in `d/M/yyyy` or `yyyy-MM-dd` | Empty fields, invalid delimiters, and malformed or impossible date/time | Whitespace normalization, date-only midnight, date formatting, and reuse of archived task descriptions |
| Update tasks | Mark, unmark, and repeated status commands | Missing, nonnumeric, and out-of-range numbers | State preserved after errors and repeats |
| Delete tasks | Valid one-based number | Missing, nonnumeric, and out-of-range numbers | Remaining tasks renumbered |
| Save tasks | Add, mark, unmark, and delete | File-write errors are outside this happy-path test | Complete list is rewritten to the test's temporary `data/herta.txt` |
| Archive tasks | Numbers, ranges, mixed and repeated selectors, `archive all` | Invalid, descending, out-of-bounds, and incomplete selections | Archived output follows active-list order and active commands remain isolated |
| View archive | Populated and empty archive | Arguments supplied to `archived` | Archive numbering is independent from active numbering |
| Restore tasks | Completed archive task and leading-zero number | Malformed, multiple, ranged, and out-of-bounds numbers | Restored task is appended to active tasks |
| Archive persistence | Archive then load in a fresh process | File failures are covered by JUnit tests | Cross-process persistence is verified in `test/archive-persistence-ui-test-plan.md` |
| Command matching | All supported commands | Command-name lookalikes | Lookalikes do not change state |

Date-only input is supported consistently by `deadline`, `event`, and `filter` in
both `d/M/yyyy` and `yyyy-MM-dd` forms. Impossible and malformed dates are
rejected strictly.

## Test case: Exit immediately

- Aim: Verify that the application displays its welcome screen and exits cleanly when the user enters `bye`.

### Inputs

```text
bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```

## Test case: Reject overlong command

- Aim: Verify that a command one character over the 4096-character limit is rejected without changing the task list.

### Inputs

```text
xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
list
bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     That is excessive. Keep the command under 4096 characters.
     ____________________________________________________________
Your command?      ____________________________________________________________
     No tasks to show. Give me something to organize.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```

## Test case: Restore entries in archive order

- Aim: Verify that restoring middle, first, and last archive entries appends each task to the active list in restore order.

### Inputs

```text
todo first
todo middle
todo last
mark 1
mark 2
mark 3
archive all
restore 2
restore 01
restore 01
list
bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] first
     That makes 1 active task. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] middle
     That makes 2 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] last
     That makes 3 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Done. It's marked complete:
       [T][X] first
     ____________________________________________________________
Your command?      ____________________________________________________________
     Done. It's marked complete:
       [T][X] middle
     ____________________________________________________________
Your command?      ____________________________________________________________
     Done. It's marked complete:
       [T][X] last
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've archived 3 completed tasks:
       [T][X] first
       [T][X] middle
       [T][X] last
     The active list is down to 0 tasks. Much tidier.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've restored it:
       [T][X] middle
     That makes 1 active task. Back where it belongs.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've restored it:
       [T][X] first
     That makes 2 active tasks. Back where it belongs.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've restored it:
       [T][X] last
     That makes 3 active tasks. Back where it belongs.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][X] middle
     2. [T][X] first
     3. [T][X] last
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```

## Manual case: Unicode descriptions

- Aim: Run this session with an explicitly UTF-8 terminal and verify that a Unicode description is displayed and remains persisted after restart. This case is manual because terminal encoding depends on the host.

### Inputs

```text
todo café
list
bye
```

### Expected output

The normal add, list, and goodbye responses, with `café` preserved exactly in both task displays.


## Test case: Restore completed and incomplete archived tasks

- Aim: Verify that completed and incomplete archive records loaded from storage can both be restored, that restoration appends to the active list, and that the archive becomes empty afterward.

### Inputs

```text
archived
restore 1
restore 1
list
archived
bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     The archive, as requested:
     1. [T][X] completed archive task
     2. [T][ ] incomplete archive task
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've restored it:
       [T][X] completed archive task
     That makes 1 active task. Back where it belongs.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've restored it:
       [T][ ] incomplete archive task
     That makes 2 active tasks. Back where it belongs.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][X] completed archive task
     2. [T][ ] incomplete archive task
     ____________________________________________________________
Your command?      ____________________________________________________________
     The archive has nothing to show. Complete a task before archiving it.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```

## Test case: Archive selectors and ordering

- Aim: Verify one-based numbers, leading-zero numbers, inclusive ranges, mixed selectors, duplicate and overlapping selector de-duplication, active-list ordering, and incomplete-task rejection.

### Inputs

```text
todo first
todo second
todo third
archive 1
mark 1
archive 01
mark 1
mark 2
archive 2 1-2 2-2 1
archived
bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] first
     That makes 1 active task. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] second
     That makes 2 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] third
     That makes 3 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That task is still unfinished. Complete it before archiving.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Done. It's marked complete:
       [T][X] first
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've archived 1 completed task:
       [T][X] first
     The active list is down to 2 tasks. Much tidier.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Done. It's marked complete:
       [T][X] second
     ____________________________________________________________
Your command?      ____________________________________________________________
     Done. It's marked complete:
       [T][X] third
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've archived 2 completed tasks:
       [T][X] second
       [T][X] third
     The active list is down to 0 tasks. Much tidier.
     ____________________________________________________________
Your command?      ____________________________________________________________
     The archive, as requested:
     1. [T][X] first
     2. [T][X] second
     3. [T][X] third
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```

## Test case: Archive all state handling

- Aim: Verify empty-active-list handling, no-completed-task handling, archiving all completed active tasks, and populated archive viewing.

### Inputs

```text
archive all
todo pending
archive all
mark 1
archive all
archived
bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     No active tasks. There is nothing here to archive.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] pending
     That makes 1 active task. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Nothing is ready for archiving. Complete a task first.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Done. It's marked complete:
       [T][X] pending
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've archived 1 completed task:
       [T][X] pending
     The active list is down to 0 tasks. Much tidier.
     ____________________________________________________________
Your command?      ____________________________________________________________
     The archive, as requested:
     1. [T][X] pending
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```

## Test case: Archive and restore an active task

- Aim: Verify populated and empty archive views, restoring a completed task, appending it to the active list, and active-list isolation.

### Inputs

```text
todo active
mark 1
archive 1
archived
list
restore 01
archived
list
bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] active
     That makes 1 active task. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Done. It's marked complete:
       [T][X] active
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've archived 1 completed task:
       [T][X] active
     The active list is down to 0 tasks. Much tidier.
     ____________________________________________________________
Your command?      ____________________________________________________________
     The archive, as requested:
     1. [T][X] active
     ____________________________________________________________
Your command?      ____________________________________________________________
     No tasks to show. Give me something to organize.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've restored it:
       [T][X] active
     That makes 1 active task. Back where it belongs.
     ____________________________________________________________
Your command?      ____________________________________________________________
     The archive has nothing to show. Complete a task before archiving it.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][X] active
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```

## Test case: Re-add a task after archiving

- Aim: Verify that an archived task does not prevent a new active task with the same identity from being added, while both records remain in their respective lists.

### Inputs

```text
todo read book
mark 1
archive 1
todo read book
list
archived
bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] read book
     That makes 1 active task. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Done. It's marked complete:
       [T][X] read book
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've archived 1 completed task:
       [T][X] read book
     The active list is down to 0 tasks. Much tidier.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] read book
     That makes 1 active task. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][ ] read book
     ____________________________________________________________
Your command?      ____________________________________________________________
     The archive, as requested:
     1. [T][X] read book
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```

## Test case: Reject invalid archive and restore selections

- Aim: Verify dedicated syntax, invalid-selection, descending-range, all-combination, archive-number, and out-of-bounds errors, including `archived extra` recognition.

### Inputs

```text
todo pending
archive
archive 0
archive -1
archive +1
archive 1 - 2
archive 2-1
archive 999999999999999999
archive all 1
archived extra
restore
restore 1 2
restore 1-2
restore -1
restore +1
restore 0
restore 99999
bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] pending
     That makes 1 active task. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     You gave me nothing to archive. Try: archive 1 or archive all.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That selection won't do. Use task numbers or ranges, such as archive 1 3-5.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That selection won't do. Use task numbers or ranges, such as archive 1 3-5.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That selection won't do. Use task numbers or ranges, such as archive 1 3-5.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That selection won't do. Use task numbers or ranges, such as archive 1 3-5.
     ____________________________________________________________
Your command?      ____________________________________________________________
     The range runs the wrong way. Try: archive 2-5.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That selection won't do. Use task numbers or ranges, such as archive 1 3-5.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Pick one: archive all, or archive 1 3-5. Mixing them is unnecessary.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Just use: archived. Nothing else is required.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Tell me which archived task to restore. Try: restore 1.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That archived task number won't do. Try: restore 1.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That archived task number won't do. Try: restore 1.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That archived task number won't do. Try: restore 1.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That archived task number won't do. Try: restore 1.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That archived task number won't do. Try: restore 1.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That number points to nothing in the archive. Check again.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```

## Test case: Find tasks by description keyword

- Aim: Verify that `find` performs a case-insensitive substring search, preserves the original task numbers, and reports empty searches and searches without matches.

### Inputs

```text
todo read book
todo call June
todo return book
find BOOK
find missing
find
bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] read book
     That makes 1 active task. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] call June
     That makes 2 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] return book
     That makes 3 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Found them. Here are the matches:
     1. [T][ ] read book
     3. [T][ ] return book
     ____________________________________________________________
Your command?      ____________________________________________________________
     Nothing matched. Try a more useful keyword.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Find something specific. Use: find <keyword>.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```

## Test case: List an empty task list

- Aim: Verify that `list` explains when a fresh task list is empty and suggests adding a task.

### Inputs

```text
list
bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     No tasks to show. Give me something to organize.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```

## Test case: Delete tasks

- Aim: Verify that a valid one-based task number removes the task, shifts the remaining list, and reports the new count, while invalid numbers leave the list unchanged.

### Inputs

```text
todo read book
todo return book
delete 1
list
delete 0
delete 2
delete nope
list
bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] read book
     That makes 1 active task. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] return book
     That makes 2 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Gone. I've removed it:
       [T][ ] read book
     That makes 1 active task. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][ ] return book
     ____________________________________________________________
Your command?      ____________________________________________________________
     That's not a task number. Try: delete 1.
     ____________________________________________________________
Your command?      ____________________________________________________________
     No active task has that number. Check the list and try again.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That's not a task number. Try: delete 1.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][ ] return book
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```

## Test case: Preserve state after empty and boundary commands

- Aim: Verify that an empty command and invalid boundary task numbers do not change a task, while valid mark and unmark commands do.

### Inputs

```text
todo core

list
mark 0
list
mark 1
list
unmark 0
list
unmark 1
list
bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] core
     That makes 1 active task. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Nothing? Use a command. Try: list, find <keyword>, or todo <description>.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][ ] core
     ____________________________________________________________
Your command?      ____________________________________________________________
     That's not a task number. Try: mark 1.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][ ] core
     ____________________________________________________________
Your command?      ____________________________________________________________
     Done. It's marked complete:
       [T][X] core
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][X] core
     ____________________________________________________________
Your command?      ____________________________________________________________
     That's not a task number. Try: unmark 1.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][X] core
     ____________________________________________________________
Your command?      ____________________________________________________________
     Fine. It's incomplete again:
       [T][ ] core
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][ ] core
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```

## Test case: Handle repeated mark and unmark commands

- Aim: Verify that repeated status commands report their no-op result and preserve task status while normal status changes keep their existing responses.

### Inputs

```text
todo repeat status
mark 1
mark 1
unmark 1
unmark 1
list
bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] repeat status
     That makes 1 active task. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Done. It's marked complete:
       [T][X] repeat status
     ____________________________________________________________
Your command?      ____________________________________________________________
     Already complete. There is nothing more to do.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Fine. It's incomplete again:
       [T][ ] repeat status
     ____________________________________________________________
Your command?      ____________________________________________________________
     Already incomplete. There is nothing to undo.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][ ] repeat status
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```

## Test case: Reject command lookalikes

- Aim: Verify that command names with extra characters are rejected and do not add unintended tasks.

### Inputs

```text
todo stable
todotask
deadline report /by 2/12/2019 1800
deadlines report /by Friday
event meeting /from 2019-10-15 /to 2019-10-16
events meeting /from Mon /to Tue
listall
marking 1
unmarking 1
deleting 1
byebye
list
bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] stable
     That makes 1 active task. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That command isn't in my vocabulary.
     Use: todo, deadline, event, list, find, filter, upcoming, sort, mark, unmark, delete, archive, archived, restore, and bye.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [D][ ] report (by: Dec 02 2019, 6:00 PM)
     That makes 2 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That command isn't in my vocabulary.
     Use: todo, deadline, event, list, find, filter, upcoming, sort, mark, unmark, delete, archive, archived, restore, and bye.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)
     That makes 3 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That command isn't in my vocabulary.
     Use: todo, deadline, event, list, find, filter, upcoming, sort, mark, unmark, delete, archive, archived, restore, and bye.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That command isn't in my vocabulary.
     Use: todo, deadline, event, list, find, filter, upcoming, sort, mark, unmark, delete, archive, archived, restore, and bye.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That command isn't in my vocabulary.
     Use: todo, deadline, event, list, find, filter, upcoming, sort, mark, unmark, delete, archive, archived, restore, and bye.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That command isn't in my vocabulary.
     Use: todo, deadline, event, list, find, filter, upcoming, sort, mark, unmark, delete, archive, archived, restore, and bye.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That command isn't in my vocabulary.
     Use: todo, deadline, event, list, find, filter, upcoming, sort, mark, unmark, delete, archive, archived, restore, and bye.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That command isn't in my vocabulary.
     Use: todo, deadline, event, list, find, filter, upcoming, sort, mark, unmark, delete, archive, archived, restore, and bye.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][ ] stable
     2. [D][ ] report (by: Dec 02 2019, 6:00 PM)
     3. [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```

## Test case: Handle EOF input

- Aim: Verify that the chatbot exits cleanly when standard input is closed before `bye`.

### Inputs

```text
todo before eof
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] before eof
     That makes 1 active task. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```

## Test case: Preserve state across errors

- Aim: Verify that invalid commands interleaved with valid additions and updates do not change the task list.

### Inputs

```text
todo alpha
todo
list
deadline report /by 2019-10-15
deadline report
list
event meeting /from 2019-10-15 /to 2019-10-16
event meeting /from Monday
list
mark 1
mark 9
list
unmark 1
unmark nope
list
bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] alpha
     That makes 1 active task. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     You forgot the todo description. Try: todo <description>.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][ ] alpha
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [D][ ] report (by: Oct 15 2019)
     That makes 2 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That deadline format won't work. Use: deadline <description> /by <date/time>.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][ ] alpha
     2. [D][ ] report (by: Oct 15 2019)
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)
     That makes 3 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     An event needs an end marker: /to <end>.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][ ] alpha
     2. [D][ ] report (by: Oct 15 2019)
     3. [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)
     ____________________________________________________________
Your command?      ____________________________________________________________
     Done. It's marked complete:
       [T][X] alpha
     ____________________________________________________________
Your command?      ____________________________________________________________
     No active task has that number. Check the list and try again.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][X] alpha
     2. [D][ ] report (by: Oct 15 2019)
     3. [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)
     ____________________________________________________________
Your command?      ____________________________________________________________
     Fine. It's incomplete again:
       [T][ ] alpha
     ____________________________________________________________
Your command?      ____________________________________________________________
     That's not a task number. Try: unmark 1.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][ ] alpha
     2. [D][ ] report (by: Oct 15 2019)
     3. [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```

## Test case: Handle command formatting edge cases

- Aim: Verify that extra spaces are trimmed correctly and malformed commands do not add tasks.

### Inputs

```text
todo   spaced description
todo    
list
deadline   submit report   /by   2019-10-15
deadline submit report /by
event   meeting /from   2019-10-15 /to   2019-10-16
event meeting /from Mon /to
list
todo
blah
list
bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] spaced description
     That makes 1 active task. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     You forgot the todo description. Try: todo <description>.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][ ] spaced description
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [D][ ] submit report (by: Oct 15 2019)
     That makes 2 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     A deadline needs a time. Use: deadline <description> /by <date/time>.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)
     That makes 3 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     An event cannot end nowhere. Add: /to <end>.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][ ] spaced description
     2. [D][ ] submit report (by: Oct 15 2019)
     3. [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)
     ____________________________________________________________
Your command?      ____________________________________________________________
     You forgot the todo description. Try: todo <description>.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That command isn't in my vocabulary.
     Use: todo, deadline, event, list, find, filter, upcoming, sort, mark, unmark, delete, archive, archived, restore, and bye.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][ ] spaced description
     2. [D][ ] submit report (by: Oct 15 2019)
     3. [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```

## Test case: Handle invalid commands

- Aim: Verify that empty descriptions, unknown commands, malformed or lookalike delimiters, invalid dates, invalid event ranges, and missing or nonnumeric task numbers produce helpful errors without adding tasks.

### Inputs

```text
todo
blah
read book
deadline return book
event project meeting /from Mon 2pm
deadline report /bye Friday
deadline report /by 31/02/2019 1800
deadline report /by 31/02/2019
event meeting /from 2026-02-30 /to 2026-03-01
event meeting /from nope /to 2019-10-16
event meeting /from 2019-10-16 /to 2019-10-15
event meeting /fromage Mon /today Tue
mark abc
mark
unmark
delete
list
bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     You forgot the todo description. Try: todo <description>.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That command isn't in my vocabulary.
     Use: todo, deadline, event, list, find, filter, upcoming, sort, mark, unmark, delete, archive, archived, restore, and bye.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That command isn't in my vocabulary.
     Use: todo, deadline, event, list, find, filter, upcoming, sort, mark, unmark, delete, archive, archived, restore, and bye.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That deadline format won't work. Use: deadline <description> /by <date/time>.
     ____________________________________________________________
Your command?      ____________________________________________________________
     An event needs an end marker: /to <end>.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That deadline format won't work. Use: deadline <description> /by <date/time>.
     ____________________________________________________________
Your command?      ____________________________________________________________
     I can't schedule that value. Use a valid date/time, such as 2019-10-15 1800.
     ____________________________________________________________
Your command?      ____________________________________________________________
     I can't schedule that value. Use a valid date/time, such as 2019-10-15 1800.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Those dates won't do. Use valid dates, such as 2019-10-15 or 2/12/2019 1800.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Those dates won't do. Use valid dates, such as 2019-10-15 or 2/12/2019 1800.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Time moves forward. Make the event end after it starts.
     ____________________________________________________________
Your command?      ____________________________________________________________
     An event needs a start marker: /from <start>.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That's not a task number. Try: mark 1.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That's not a task number. Try: mark 1.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That's not a task number. Try: unmark 1.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That's not a task number. Try: delete 1.
     ____________________________________________________________
Your command?      ____________________________________________________________
     No tasks to show. Give me something to organize.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```

## Test case: Add and list a task

- Aim: Verify that leading command whitespace is ignored and a todo is displayed by `list`.

The three input lines intentionally have two leading spaces.

### Inputs

```text
  todo read book
  list
  bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] read book
     That makes 1 active task. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][ ] read book
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```

## Test case: Add deadlines and events

- Aim: Verify that deadlines and events parse date/time and date-only input into typed values, represent date-only values at midnight, display them in a readable format, and retain the correct task type.

### Inputs

```text
todo borrow book
deadline return book /by 2/12/2019  1800
deadline date-only slash /by 2/12/2019
event project meeting /from 2/12/2019 1800 /to 3/12/2019 1800
event date-only mixed /from 2019-12-03 /to 4/12/2019
list
bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] borrow book
     That makes 1 active task. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [D][ ] return book (by: Dec 02 2019, 6:00 PM)
     That makes 2 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [D][ ] date-only slash (by: Dec 02 2019)
     That makes 3 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [E][ ] project meeting (from: Dec 02 2019, 6:00 PM to: Dec 03 2019, 6:00 PM)
     That makes 4 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [E][ ] date-only mixed (from: Dec 03 2019 to: Dec 04 2019)
     That makes 5 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][ ] borrow book
     2. [D][ ] return book (by: Dec 02 2019, 6:00 PM)
     3. [D][ ] date-only slash (by: Dec 02 2019)
     4. [E][ ] project meeting (from: Dec 02 2019, 6:00 PM to: Dec 03 2019, 6:00 PM)
     5. [E][ ] date-only mixed (from: Dec 03 2019 to: Dec 04 2019)
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```

## Test case: Save task changes to disk

- Aim: Verify that successful additions, status changes, and deletion are persisted by rewriting the complete task list in the test's temporary `data/herta.txt`.

### Inputs

```text
todo save todo
deadline save deadline /by 2019-10-15
event save event /from 2019-10-15 /to 2019-10-16
mark 1
unmark 1
mark 1
delete 2
list
bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] save todo
     That makes 1 active task. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [D][ ] save deadline (by: Oct 15 2019)
     That makes 2 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [E][ ] save event (from: Oct 15 2019 to: Oct 16 2019)
     That makes 3 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Done. It's marked complete:
       [T][X] save todo
     ____________________________________________________________
Your command?      ____________________________________________________________
     Fine. It's incomplete again:
       [T][ ] save todo
     ____________________________________________________________
Your command?      ____________________________________________________________
     Done. It's marked complete:
       [T][X] save todo
     ____________________________________________________________
Your command?      ____________________________________________________________
     Gone. I've removed it:
       [D][ ] save deadline (by: Oct 15 2019)
     That makes 2 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][X] save todo
     2. [E][ ] save event (from: Oct 15 2019 to: Oct 16 2019)
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```

## Test case: Reject task text that breaks storage format

- Aim: Verify that a task containing the storage delimiter is rejected without changing the local task list or saved data.

### Inputs

```text
todo contains | separator
list
bye
```
### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     Keep the description on one line and leave out the storage delimiter and control characters.
     ____________________________________________________________
Your command?      ____________________________________________________________
     No tasks to show. Give me something to organize.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```

## Test case: Normalize GUI-style whitespace and reject extra arguments

- Aim: Verify that leading/repeated command whitespace, including horizontal tabs,
  is normalized before parsing, while no-argument commands reject extra arguments
  with precise usage guidance.

### Inputs

```text
  todo leading input
  list	anything
  bye	anything
  list
  bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] leading input
     That makes 1 active task. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Just use: list. Nothing else is required.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Just use: bye. Nothing else is required.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][ ] leading input
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```
## Test case: Query and sort date-aware tasks

- Aim: Verify that ISO and slash-format dates can find tasks occurring on a date, show upcoming incomplete tasks, and display all tasks in chronological order without changing their stored order.

### Inputs

```text
todo buy groceries
deadline submit report /by 2019-10-15
event project meeting /from 2019-10-14 /to 2019-10-16
deadline future report /by 9999-12-31
event future meeting /from 9999-12-30 /to 9999-12-31
filter /on 2019-10-15
filter /on 2020-01-01
upcoming 4000000
sort date
list
filter /on 15/10/2019
filter /on 31/02/2019
filter 2019-10-15
upcoming 0
sort time
bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [T][ ] buy groceries
     That makes 1 active task. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [D][ ] submit report (by: Oct 15 2019)
     That makes 2 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [E][ ] project meeting (from: Oct 14 2019 to: Oct 16 2019)
     That makes 3 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [D][ ] future report (by: Dec 31 9999)
     That makes 4 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've added it:
       [E][ ] future meeting (from: Dec 30 9999 to: Dec 31 9999)
     That makes 5 active tasks. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Here's what's scheduled for Oct 15 2019. Try not to miss it:
     2. [D][ ] submit report (by: Oct 15 2019)
     3. [E][ ] project meeting (from: Oct 14 2019 to: Oct 16 2019)
     ____________________________________________________________
Your command?      ____________________________________________________________
     Here's what's scheduled for Jan 01 2020. Try not to miss it:
     No tasks on Jan 01 2020. A remarkably empty date.
     ____________________________________________________________
Your command?      ____________________________________________________________
     The next 4000000 days, arranged for you:
     4. [D][ ] future report (by: Dec 31 9999)
     5. [E][ ] future meeting (from: Dec 30 9999 to: Dec 31 9999)
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. Your tasks are in date order.
     3. [E][ ] project meeting (from: Oct 14 2019 to: Oct 16 2019)
     2. [D][ ] submit report (by: Oct 15 2019)
     5. [E][ ] future meeting (from: Dec 30 9999 to: Dec 31 9999)
     4. [D][ ] future report (by: Dec 31 9999)
     1. [T][ ] buy groceries
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][ ] buy groceries
     2. [D][ ] submit report (by: Oct 15 2019)
     3. [E][ ] project meeting (from: Oct 14 2019 to: Oct 16 2019)
     4. [D][ ] future report (by: Dec 31 9999)
     5. [E][ ] future meeting (from: Dec 30 9999 to: Dec 31 9999)
     ____________________________________________________________
Your command?      ____________________________________________________________
     Here's what's scheduled for Oct 15 2019. Try not to miss it:
     2. [D][ ] submit report (by: Oct 15 2019)
     3. [E][ ] project meeting (from: Oct 14 2019 to: Oct 16 2019)
     ____________________________________________________________
Your command?      ____________________________________________________________
     That date won't do. Try 2019-10-15 or 15/10/2019.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Your filter needs /on. Try: filter /on <date>.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That day count is not useful. Use: upcoming <days>, with a positive number in range.
     ____________________________________________________________
Your command?      ____________________________________________________________
     That sorting option is not supported. Try: sort date.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```
