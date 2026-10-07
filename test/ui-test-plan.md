# UI Test Plan

## Working-folder write policy (2026-10-04)

Console command/output expectations are unchanged. Run the active R1-R2, P1-P4,
F1-F2, S1-S3 and H1 suite in isolated working folders using Java 25.
Additional `LocalPathsTest` subprocess scenarios run the production path checks:

| Aim / input | Expected result |
| --- | --- |
| Save `data/tasks.txt` and an absolute path inside the child working folder | `SAVED`; task records exist only inside that folder |
| Save `../outside/tasks.txt`, a nested traversal, or an outside absolute path | `REJECTED:` explanation; no outside file or folder created |
| Save or prepare caches through a directory symlink/Windows junction pointing outside | `REJECTED:` explanation; linked target stays empty |
| Prepare caches with JVM home/cache/temp settings pointing outside | Cache and temp properties resolve to local `.mary` subfolders; outside paths stay absent |
| Start with a regular file at `.mary` | `REJECTED:` explanation; file unchanged, no home or external temporary fallback |

`jarGuiSmoke` now invokes the same cache preparation as the real launcher.

## Cross-platform JAR launch plan (2026-10-03)

Run with Java 25 in isolated working directories; stop at the first failed case.
Existing console expectations are unchanged by the packaging fix.

| Case | Aim / input | Expected output or result |
| --- | --- | --- |
| J1 | `gradlew verifyJar` | Build succeeds; Windows, Linux and Mac Glass/software-renderer libraries and native-access manifest attribute exist |
| J2 | `gradlew jarGuiSmoke` on Windows | `GUI_SMOKE_PASS: MARY opened and rendered successfully.`; exit 0; no renderer/toolkit exception |
| J3 | Run `mary.PackagedGuiSmoke` with only the built JAR and test classes on Linux/WSLg, Java 25 | Same success line as J2; exit 0; no renderer/toolkit exception |
| J4 | Launch `java -jar mary.jar` on a graphical desktop | MARY window opens; no restricted-native-access warning or renderer exception; close window afterward |

The smoke helper opens the real application window, takes a scene snapshot and
closes it. Its classpath intentionally excludes Gradle runtime dependencies so
missing native libraries cannot be supplied by the development environment.
The known JavaFX unnamed-module configuration warning is not a test failure.
macOS execution requires a Mac and must not be inferred from archive checks.

## JavaFX interaction plan (2026-10-02)

The default launch now opens JavaFX; the recorded console cases below still run
unchanged via `java -jar build/libs/mary.jar --cli`. Use Java 25 and isolated
working directories, compare exact output and saved records, and stop on the
first failure. GUI automation is separate: `gradlew.bat guiTest` requires a
graphical desktop and uses temporary task files. It exercises actual JavaFX
controls on the application thread; text-field action events represent Enter.

| Case | Aim and input | Expected result |
| --- | --- | --- |
| G1 | Send `todo read book`; Enter `mark 1`, `list`; reopen and `list` | Visible add/mark replies; `1.[T][X] read book`; input clears; stored `T \| 1 \| read book` reloads |
| G2 | Submit blank, `blah`, then `todo read` | Specific errors appear in bubbles; subsequent valid command succeeds |
| G3 | Click Add a to-do; expand Command guide | Input becomes `todo ` without saving; suggestions disable while draft exists; guide explains types, dates, task numbers |
| G4 | Enter `bye`, then click Close | Farewell remains readable; input/suggestions disabled; Close hides the window |
| G5 | Start with file containing `broken` | Loading-error bubble names line 1; file remains unchanged |
| G6 | Resize to 480x600; add a long task and run `list` twelve times | Wrapped messages fit the viewport, transcript scrolls to latest reply, composer stays usable |
| G7 | `todo read book`, `mark 1` in a 760x780 scene | Render preview at `build/reports/gui/mary.png`; inspect contrast, spacing and clipping |
| G8 | Open a fresh window | MARY heading and sender, cat-themed welcome, accessible smiling-cat emblem, Cat's guide title |

The shared-session JUnit tests additionally cover deadlines, events, date search,
keyword search, unmark, delete, Unicode, persistence errors and commands after bye.
The GUI delegates to this same session; it does not maintain a second task list.

## Bug-fix UI regression run (2026-09-27)

Run R1–R2 and P1–P4 from the shared JSON specification against the newly built
`mary.jar`, with Java 25. R1 adds exact expectations for missing mark/unmark
arguments and impossible dates; R2 adds a malformed todo record with an extra
field. Use isolated directories. Compare exact output and saved data, stopping
at the first mismatch. Do not compare these corrected cases to the old buggy
baseline program. All prior console records below are historical.

<!-- bugfix-ui-session -->
Result: **PASS** — six cases (R1, R2, P1–P4), seven JAR sessions, Java 25.0.4.1.
Exact stdout, empty stderr, zero exit codes and saved-file contents all matched
the specification. Gradle `test shadowJar` succeeded and all 52 JUnit tests passed.
The tests used temporary data directories; the project save file was not used.

Complete console records follow, with redirected stdin shown separately.

### Bug-fix UI R1, session 1: PASS

Input (JSON strings preserve the trailing spaces in two commands):

```json
[
  "todo read",
  "mark",
  "unmark",
  "mark ",
  "unmark ",
  "on 29/2/2023",
  "deadline invalid /by 31/4/2024 1800",
  "event invalid /from 29/2/2023 1400 /to 1/3/2023 1600",
  "list",
  "bye"
]
```

Output:

```text
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Bug-fix UI R2, session 1: PASS

Input:

```text
list
bye
```

Output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Bug-fix UI P1, session 1: PASS

Input:

```text
list
todo read book
deadline return book /by 2/12/2019 1800
event project meeting /from 2/12/2019 1400 /to 4/12/2019 1600
list
mark 2
unmark 2
mark 1
delete 2
list
on 3/12/2019
on 1/12/2019
bye
```

Output:

```text
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: 2 Dec 2019 18:00)
 3.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 No deadlines or events occur on 2019-12-01.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Bug-fix UI P1, session 2: PASS

Input:

```text
list
on 3/12/2019
bye
```

Output:

```text
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Bug-fix UI P2, session 1: PASS

Input:

```text

blah
todo
deadline homework
event meeting /from 2pm
mark abc
delete 0
deadline return book /by tomorrow
event meeting /from 2/12/2019 /to 2/12/2019 1600
on tomorrow
list
bye
```

Output:

```text
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 Error: please enter a command or task.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Error: use 'todo description' to add a task without a date.
____________________________________________________________
____________________________________________________________
 Error: use 'deadline description /by date or time'.
____________________________________________________________
____________________________________________________________
 Error: use 'event description /from start /to end'.
____________________________________________________________
____________________________________________________________
 Error: 'abc' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Error: task 0 does not exist; use 'list' to see valid task numbers.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Bug-fix UI P3, session 1: PASS

Input:

```text
list
bye
```

Output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Bug-fix UI P4, session 1: PASS

Input (EOF):

```text
```

Output:

```text
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
```
<!-- /bugfix-ui-session -->

## JUnit-addition UI regression run (2026-09-27)

The JUnit addition does not change production behavior. Run the existing P1–P4
suite and exact expectations below against `mary.jar` with Java 25. This UI run
is separate from the new boundary-focused JUnit tests, whose defects are recorded
in `junit-test-plan.md`. Stop this UI run at its first mismatch.

<!-- junit-ui-session -->
Result: **PASS** — P1–P4, five JAR sessions using Java 25. Every stdout
comparison and saved-file check matched the recorded expectations. Each process
exited with 0 and empty stderr. These results do not supersede the four failing
JUnit boundary tests described in `junit-test-plan.md`.

Complete console records follow (stdin is shown separately from stdout).

### JUnit-addition UI P1, session 1: PASS

Input:

```text
list
todo read book
deadline return book /by 2/12/2019 1800
event project meeting /from 2/12/2019 1400 /to 4/12/2019 1600
list
mark 2
unmark 2
mark 1
delete 2
list
on 3/12/2019
on 1/12/2019
bye
```

Output:

```text
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: 2 Dec 2019 18:00)
 3.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 No deadlines or events occur on 2019-12-01.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### JUnit-addition UI P1, session 2: PASS

Input:

```text
list
on 3/12/2019
bye
```

Output:

```text
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### JUnit-addition UI P2, session 1: PASS

Input:

```text

blah
todo
deadline homework
event meeting /from 2pm
mark abc
delete 0
deadline return book /by tomorrow
event meeting /from 2/12/2019 /to 2/12/2019 1600
on tomorrow
list
bye
```

Output:

```text
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 Error: please enter a command or task.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Error: use 'todo description' to add a task without a date.
____________________________________________________________
____________________________________________________________
 Error: use 'deadline description /by date or time'.
____________________________________________________________
____________________________________________________________
 Error: use 'event description /from start /to end'.
____________________________________________________________
____________________________________________________________
 Error: 'abc' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Error: task 0 does not exist; use 'list' to see valid task numbers.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### JUnit-addition UI P3, session 1: PASS

Input:

```text
list
bye
```

Output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### JUnit-addition UI P4, session 1: PASS

Input (EOF):

```text
```

Output:

```text
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
```
<!-- /junit-ui-session -->

## Gradle JAR verification (2026-09-27)

Run the P1–P4 suite below against the Gradle-built `build/libs/mary.jar` using
Java 25 and `java -jar`, so the tests also verify the manifest's main class.
The aims, commands, exact output expectations and save-file expectations are
unchanged. Use isolated working directories and stop at the first mismatch.

<!-- gradle-session -->
Result: **PASS**. Gradle 9.6.1 `build` succeeded using Temurin Java 25.0.4.1.
The generated `mary.jar` launched successfully via `java -jar`. All five JAR
sessions (P1–P4) matched the exact expected console output and saved data;
stderr was empty and each process exited with code 0. Gradle's `test` task
reported NO-SOURCE; the UI suite below was run separately.

Actual console input and complete output follow.

### Gradle P1, session 1: PASS

Input:

```text
list
todo read book
deadline return book /by 2/12/2019 1800
event project meeting /from 2/12/2019 1400 /to 4/12/2019 1600
list
mark 2
unmark 2
mark 1
delete 2
list
on 3/12/2019
on 1/12/2019
bye
```

Output:

```text
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: 2 Dec 2019 18:00)
 3.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 No deadlines or events occur on 2019-12-01.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Gradle P1, session 2: PASS

Input:

```text
list
on 3/12/2019
bye
```

Output:

```text
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Gradle P2, session 1: PASS

Input:

```text

blah
todo
deadline homework
event meeting /from 2pm
mark abc
delete 0
deadline return book /by tomorrow
event meeting /from 2/12/2019 /to 2/12/2019 1600
on tomorrow
list
bye
```

Output:

```text
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 Error: please enter a command or task.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Error: use 'todo description' to add a task without a date.
____________________________________________________________
____________________________________________________________
 Error: use 'deadline description /by date or time'.
____________________________________________________________
____________________________________________________________
 Error: use 'event description /from start /to end'.
____________________________________________________________
____________________________________________________________
 Error: 'abc' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Error: task 0 does not exist; use 'list' to see valid task numbers.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Gradle P3, session 1: PASS

Input:

```text
list
bye
```

Output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Gradle P4, session 1: PASS

Input (EOF):

```text
```

Output:

```text
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
```
<!-- /gradle-session -->

## Current package-migration regression suite

This suite invokes `mary.MARY` with Java 25 and compares complete stdout exactly
(normalizing CRLF/LF only). Expected output is defined below before testing.
The historical cases farther down are retained as history: some predate typed
dates, command extraction, and the current farewell and are not current oracles.

Each case starts in a new isolated working directory with no save file, unless
`seed` specifies its contents. Sessions within a case share that directory to
test persistence. The repository's real `mary-data.txt` must never be used or
modified by these tests. Compile to a separate output directory with `javac -d`;
do not overwrite or remove the existing tracked `.class` files.

For this structural refactor, also compile a snapshot of the original sources
and compare its stdout and saved data with the packaged version using identical
fixtures. Stop immediately if either an exact expectation or comparison fails.

The executable specification below records each case's aim, input commands, and
expected output. `welcome` is the exact startup output (including the existing
two initial dividers). For a `startupError`, insert ` Error: MESSAGE` and one
divider after the first startup divider. Each step prints one divider, its
`output` lines, and one divider. Input is supplied through stdin and is not
included in stdout. Every line, including the last, ends with a newline.
An empty `steps` array means EOF immediately after startup.

`saved` is the exact expected final data file content, or null if no file should
exist. Case P1 covers adding all task types, marking/unmarking, deletion, date
search and persistence; P2 covers invalid inputs; P3 covers corrupted data;
P4 covers missing data and EOF. These cover the behaviours described by the
older cases without relying on obsolete date strings such as "Sunday".

<!-- package-suite -->
```json
{
  "separator": "____________________________________________________________",
  "welcome": [
    "____________________________________________________________",
    "____________________________________________________________",
    " /\\_/\\",
    "( o.o )   M A R Y",
    " > ^ <    Your purr-sonal task assistant.",
    "",
    "What's on your list today?",
    "____________________________________________________________"
  ],
  "cases": [
    {
      "id": "R1",
      "aim": "Verify missing mark/unmark arguments and impossible dates give errors without crashing or adding invalid tasks.",
      "sessions": [
        {
          "steps": [
            {
              "input": "todo read",
              "output": [
                " Purr-fect! I've added this task:",
                "   [T][ ] read",
                " Tasks on your list: 1."
              ]
            },
            {
              "input": "mark",
              "output": [
                " Error: use 'mark N' or 'unmark N', where N is a task number."
              ]
            },
            {
              "input": "unmark",
              "output": [
                " Error: use 'mark N' or 'unmark N', where N is a task number."
              ]
            },
            {
              "input": "mark ",
              "output": [
                " Error: use 'mark N' or 'unmark N', where N is a task number."
              ]
            },
            {
              "input": "unmark ",
              "output": [
                " Error: use 'mark N' or 'unmark N', where N is a task number."
              ]
            },
            {
              "input": "on 29/2/2023",
              "output": [
                " Error: use date format d/M/yyyy, for example 2/12/2019."
              ]
            },
            {
              "input": "deadline invalid /by 31/4/2024 1800",
              "output": [
                " Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800."
              ]
            },
            {
              "input": "event invalid /from 29/2/2023 1400 /to 1/3/2023 1600",
              "output": [
                " Error: use event date/time format d/M/yyyy HHmm for both /from and /to."
              ]
            },
            {
              "input": "list",
              "output": [
                " Here's your task lineup:",
                " 1.[T][ ] read"
              ]
            },
            {
              "input": "bye",
              "output": [
                "Time for a catnap. See you soon!"
              ]
            }
          ]
        }
      ],
      "saved": "T | 0 | read\n"
    },
    {
      "id": "R2",
      "aim": "Reject a saved todo containing an extra field, report its line, and preserve the corrupted file.",
      "seed": "T | 0 | read | unexpected\n",
      "sessions": [
        {
          "startupError": "could not load saved tasks: invalid record on line 1. Repair or move the data file and restart MARY; saving is disabled to protect it.",
          "steps": [
            {
              "input": "list",
              "output": [
                " Nothing to chase yet! Add a task with 'todo description'."
              ]
            },
            {
              "input": "bye",
              "output": [
                "Time for a catnap. See you soon!"
              ]
            }
          ]
        }
      ],
      "saved": "T | 0 | read | unexpected\n"
    },
    {
      "id": "P1",
      "aim": "Exercise all packaged command types and task subtypes, list renumbering, date filtering, and save/reload across two processes.",
      "sessions": [
        {
          "steps": [
            {
              "input": "list",
              "output": [
                " Nothing to chase yet! Add a task with 'todo description'."
              ]
            },
            {
              "input": "todo read book",
              "output": [
                " Purr-fect! I've added this task:",
                "   [T][ ] read book",
                " Tasks on your list: 1."
              ]
            },
            {
              "input": "deadline return book /by 2/12/2019 1800",
              "output": [
                " Purr-fect! I've added this task:",
                "   [D][ ] return book (by: 2 Dec 2019 18:00)",
                " Tasks on your list: 2."
              ]
            },
            {
              "input": "event project meeting /from 2/12/2019 1400 /to 4/12/2019 1600",
              "output": [
                " Purr-fect! I've added this task:",
                "   [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)",
                " Tasks on your list: 3."
              ]
            },
            {
              "input": "list",
              "output": [
                " Here's your task lineup:",
                " 1.[T][ ] read book",
                " 2.[D][ ] return book (by: 2 Dec 2019 18:00)",
                " 3.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)"
              ]
            },
            {
              "input": "mark 2",
              "output": [
                " Pawsome! Task completed:",
                "   [D][X] return book (by: 2 Dec 2019 18:00)"
              ]
            },
            {
              "input": "unmark 2",
              "output": [
                " Back on your list, ready for another pounce:",
                "   [D][ ] return book (by: 2 Dec 2019 18:00)"
              ]
            },
            {
              "input": "mark 1",
              "output": [
                " Pawsome! Task completed:",
                "   [T][X] read book"
              ]
            },
            {
              "input": "delete 2",
              "output": [
                " Whisked away! I've removed this task:",
                "   [D][ ] return book (by: 2 Dec 2019 18:00)",
                " Tasks on your list: 2."
              ]
            },
            {
              "input": "list",
              "output": [
                " Here's your task lineup:",
                " 1.[T][X] read book",
                " 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)"
              ]
            },
            {
              "input": "on 3/12/2019",
              "output": [
                " Tasks occurring on 2019-12-03:",
                " [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)"
              ]
            },
            {
              "input": "on 1/12/2019",
              "output": [
                " No deadlines or events occur on 2019-12-01."
              ]
            },
            {
              "input": "bye",
              "output": [
                "Time for a catnap. See you soon!"
              ]
            }
          ]
        },
        {
          "steps": [
            {
              "input": "list",
              "output": [
                " Here's your task lineup:",
                " 1.[T][X] read book",
                " 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)"
              ]
            },
            {
              "input": "on 3/12/2019",
              "output": [
                " Tasks occurring on 2019-12-03:",
                " [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)"
              ]
            },
            {
              "input": "bye",
              "output": [
                "Time for a catnap. See you soon!"
              ]
            }
          ]
        }
      ],
      "saved": "T | 1 | read book\nE | 0 | project meeting | 2019-12-02T14:00 | 2019-12-04T16:00\n"
    },
    {
      "id": "P2",
      "aim": "Verify exceptions and invalid command/date messages still cross package boundaries, with no saved data created.",
      "sessions": [
        {
          "steps": [
            {
              "input": "",
              "output": [
                " Error: please enter a command or task."
              ]
            },
            {
              "input": "blah",
              "output": [
                " Error: I can't get my paws around that command; use todo, deadline, event, on, list, find, sort, mark, unmark, delete, or bye."
              ]
            },
            {
              "input": "todo",
              "output": [
                " Error: please add a task description after 'todo'."
              ]
            },
            {
              "input": "deadline homework",
              "output": [
                " Error: use 'deadline description /by date or time'."
              ]
            },
            {
              "input": "event meeting /from 2pm",
              "output": [
                " Error: use 'event description /from start /to end'."
              ]
            },
            {
              "input": "mark abc",
              "output": [
                " Error: 'abc' is not a valid task number; use a positive whole number."
              ]
            },
            {
              "input": "delete 0",
              "output": [
                " Error: task 0 does not exist; use 'list' to see valid task numbers."
              ]
            },
            {
              "input": "deadline return book /by tomorrow",
              "output": [
                " Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800."
              ]
            },
            {
              "input": "event meeting /from 2/12/2019 /to 2/12/2019 1600",
              "output": [
                " Error: use event date/time format d/M/yyyy HHmm for both /from and /to."
              ]
            },
            {
              "input": "on tomorrow",
              "output": [
                " Error: use date format d/M/yyyy, for example 2/12/2019."
              ]
            },
            {
              "input": "list",
              "output": [
                " Nothing to chase yet! Add a task with 'todo description'."
              ]
            },
            {
              "input": "bye",
              "output": [
                "Time for a catnap. See you soon!"
              ]
            }
          ]
        }
      ],
      "saved": null
    },
    {
      "id": "P3",
      "aim": "Load a malformed record in an isolated data file; report the loading error and continue accepting commands without changing that file.",
      "seed": "not a valid task record\n",
      "sessions": [
        {
          "startupError": "could not load saved tasks: invalid record on line 1. Repair or move the data file and restart MARY; saving is disabled to protect it.",
          "steps": [
            {
              "input": "list",
              "output": [
                " Nothing to chase yet! Add a task with 'todo description'."
              ]
            },
            {
              "input": "todo do not overwrite",
              "output": [
                " Error: saving is disabled because loading failed; repair or move the data file and restart MARY. Your original file has not been changed."
              ]
            },
            {
              "input": "bye",
              "output": [
                "Time for a catnap. See you soon!"
              ]
            }
          ]
        }
      ],
      "saved": "not a valid task record\n"
    },
    {
      "id": "P4",
      "aim": "Verify the packaged entry point handles end-of-input and a missing data file.",
      "sessions": [
        {
          "steps": []
        }
      ],
      "saved": null
    },
    {
      "id": "F1",
      "aim": "Search persisted task descriptions across all types and states; verify case folding, substrings, phrases, description-only matching, invalid syntax, and unchanged storage/list order.",
      "seed": "T | 0 | buy bread\nT | 1 | read book\nD | 1 | return Book | 2019-12-02T18:00\nE | 0 | BOOK club | 2019-12-02T14:00 | 2019-12-02T16:00\n",
      "sessions": [
        {
          "steps": [
            {
              "input": "find book",
              "output": [
                " Look what I sniffed out:",
                " 1.[T][X] read book",
                " 2.[D][X] return Book (by: 2 Dec 2019 18:00)",
                " 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)"
              ]
            },
            {
              "input": "find   BOOK  ",
              "output": [
                " Look what I sniffed out:",
                " 1.[T][X] read book",
                " 2.[D][X] return Book (by: 2 Dec 2019 18:00)",
                " 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)"
              ]
            },
            {
              "input": "find ook",
              "output": [
                " Look what I sniffed out:",
                " 1.[T][X] read book",
                " 2.[D][X] return Book (by: 2 Dec 2019 18:00)",
                " 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)"
              ]
            },
            {
              "input": "find read book",
              "output": [
                " Look what I sniffed out:",
                " 1.[T][X] read book"
              ]
            },
            {
              "input": "find Dec",
              "output": [
                " No matching tasks in sight. Try another keyword."
              ]
            },
            {
              "input": "find [X]",
              "output": [
                " No matching tasks in sight. Try another keyword."
              ]
            },
            {
              "input": "find missing",
              "output": [
                " No matching tasks in sight. Try another keyword."
              ]
            },
            {
              "input": "find",
              "output": [
                " Error: use 'find keyword', for example 'find book'."
              ]
            },
            {
              "input": "find   ",
              "output": [
                " Error: use 'find keyword', for example 'find book'."
              ]
            },
            {
              "input": "finder book",
              "output": [
                " Error: I can't get my paws around that command; use todo, deadline, event, on, list, find, sort, mark, unmark, delete, or bye."
              ]
            },
            {
              "input": "list",
              "output": [
                " Here's your task lineup:",
                " 1.[T][ ] buy bread",
                " 2.[T][X] read book",
                " 3.[D][X] return Book (by: 2 Dec 2019 18:00)",
                " 4.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)"
              ]
            },
            {
              "input": "bye",
              "output": [
                "Time for a catnap. See you soon!"
              ]
            }
          ]
        }
      ],
      "saved": "T | 0 | buy bread\nT | 1 | read book\nD | 1 | return Book | 2019-12-02T18:00\nE | 0 | BOOK club | 2019-12-02T14:00 | 2019-12-02T16:00\n"
    },
    {
      "id": "F2",
      "aim": "Search an empty list without creating a data file.",
      "sessions": [
        {
          "steps": [
            {
              "input": "find book",
              "output": [
                " No matching tasks in sight. Try another keyword."
              ]
            },
            {
              "input": "bye",
              "output": [
                "Time for a catnap. See you soon!"
              ]
            }
          ]
        }
      ],
      "saved": null
    },
    {
      "id": "S1",
      "aim": "Sort mixed tasks chronologically with stable ties and todos last; repeat, restart, mark and delete using the new numbers.",
      "seed": "T | 0 | read\nD | 0 | late | 2027-01-01T00:00\nE | 0 | meeting | 2026-12-02T14:00 | 2026-12-02T16:00\nD | 0 | early | 2026-12-02T13:00\nT | 0 | buy\nD | 1 | tie | 2026-12-02T14:00\n",
      "sessions": [
        {
          "steps": [
            {
              "input": "sort",
              "output": [
                " Tasks lined up chronologically (deadlines by due time, events by start time; todos last).",
                " Here's your task lineup:",
                " 1.[D][ ] early (by: 2 Dec 2026 13:00)",
                " 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)",
                " 3.[D][X] tie (by: 2 Dec 2026 14:00)",
                " 4.[D][ ] late (by: 1 Jan 2027 00:00)",
                " 5.[T][ ] read",
                " 6.[T][ ] buy"
              ]
            },
            {
              "input": "sort",
              "output": [
                " Tasks lined up chronologically (deadlines by due time, events by start time; todos last).",
                " Here's your task lineup:",
                " 1.[D][ ] early (by: 2 Dec 2026 13:00)",
                " 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)",
                " 3.[D][X] tie (by: 2 Dec 2026 14:00)",
                " 4.[D][ ] late (by: 1 Jan 2027 00:00)",
                " 5.[T][ ] read",
                " 6.[T][ ] buy"
              ]
            },
            {
              "input": "bye",
              "output": [
                "Time for a catnap. See you soon!"
              ]
            }
          ]
        },
        {
          "steps": [
            {
              "input": "list",
              "output": [
                " Here's your task lineup:",
                " 1.[D][ ] early (by: 2 Dec 2026 13:00)",
                " 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)",
                " 3.[D][X] tie (by: 2 Dec 2026 14:00)",
                " 4.[D][ ] late (by: 1 Jan 2027 00:00)",
                " 5.[T][ ] read",
                " 6.[T][ ] buy"
              ]
            },
            {
              "input": "mark 1",
              "output": [
                " Pawsome! Task completed:",
                "   [D][X] early (by: 2 Dec 2026 13:00)"
              ]
            },
            {
              "input": "delete 4",
              "output": [
                " Whisked away! I've removed this task:",
                "   [D][ ] late (by: 1 Jan 2027 00:00)",
                " Tasks on your list: 5."
              ]
            },
            {
              "input": "list",
              "output": [
                " Here's your task lineup:",
                " 1.[D][X] early (by: 2 Dec 2026 13:00)",
                " 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)",
                " 3.[D][X] tie (by: 2 Dec 2026 14:00)",
                " 4.[T][ ] read",
                " 5.[T][ ] buy"
              ]
            },
            {
              "input": "bye",
              "output": [
                "Time for a catnap. See you soon!"
              ]
            }
          ]
        }
      ],
      "saved": "D | 1 | early | 2026-12-02T13:00\nE | 0 | meeting | 2026-12-02T14:00 | 2026-12-02T16:00\nD | 1 | tie | 2026-12-02T14:00\nT | 0 | read\nT | 0 | buy\n"
    },
    {
      "id": "S2",
      "aim": "Empty sort creates no file; invalid sort options give usage and longer words stay unknown.",
      "sessions": [
        {
          "steps": [
            {
              "input": "sort",
              "output": [
                " Nothing to chase yet! Add a task with 'todo description'."
              ]
            },
            {
              "input": "sort descending",
              "output": [
                " Error: use 'sort' without arguments to order tasks chronologically."
              ]
            },
            {
              "input": "sorter",
              "output": [
                " Error: I can't get my paws around that command; use todo, deadline, event, on, list, find, sort, mark, unmark, delete, or bye."
              ]
            },
            {
              "input": "bye",
              "output": [
                "Time for a catnap. See you soon!"
              ]
            }
          ]
        }
      ],
      "saved": null
    },
    {
      "id": "S3",
      "aim": "Todo-only sorting is stable and accepts trailing whitespace without changing completion states.",
      "seed": "T | 1 | z task\nT | 0 | a task\n",
      "sessions": [
        {
          "steps": [
            {
              "input": "sort \t",
              "output": [
                " Tasks lined up chronologically (deadlines by due time, events by start time; todos last).",
                " Here's your task lineup:",
                " 1.[T][X] z task",
                " 2.[T][ ] a task"
              ]
            },
            {
              "input": "bye",
              "output": [
                "Time for a catnap. See you soon!"
              ]
            }
          ]
        }
      ],
      "saved": "T | 1 | z task\nT | 0 | a task\n"
    },
    {
      "id": "H1",
      "aim": "Accept command whitespace, reject duplicate details and malformed parameters/ranges without mutation, and recover with valid input.",
      "sessions": [
        {
          "steps": [
            {
              "input": "  todo\tread book  ",
              "output": [
                " Purr-fect! I've added this task:",
                "   [T][ ] read book",
                " Tasks on your list: 1."
              ]
            },
            {
              "input": "todo read book",
              "output": [
                " Error: that task already exists; use 'list' to find it or change its details."
              ]
            },
            {
              "input": "todo a | b",
              "output": [
                " Error: task descriptions cannot contain '|'; replace it with another character."
              ]
            },
            {
              "input": "list extra",
              "output": [
                " Error: use 'list' without arguments."
              ]
            },
            {
              "input": "bye now",
              "output": [
                " Error: use 'bye' without arguments."
              ]
            },
            {
              "input": "deadline read /by 1/1/2026 1200 /by 2/1/2026 1200",
              "output": [
                " Error: parameter /by is repeated; specify it only once."
              ]
            },
            {
              "input": "event camp /from 1/1/2026 1200 /to 1/1/2026 1200",
              "output": [
                " Error: event end must be after its start; correct /from or /to."
              ]
            },
            {
              "input": "event camp /from 2/1/2026 1200 /to 1/1/2026 1200",
              "output": [
                " Error: event end must be after its start; correct /from or /to."
              ]
            },
            {
              "input": "mark +1",
              "output": [
                " Error: '+1' is not a valid task number; use a positive whole number."
              ]
            },
            {
              "input": " mark\t1 ",
              "output": [
                " Pawsome! Task completed:",
                "   [T][X] read book"
              ]
            },
            {
              "input": " list ",
              "output": [
                " Here's your task lineup:",
                " 1.[T][X] read book"
              ]
            },
            {
              "input": " bye ",
              "output": [
                "Time for a catnap. See you soon!"
              ]
            }
          ]
        }
      ],
      "saved": "T | 1 | read book\n"
    }
  ]
}
```
<!-- /package-suite -->

## Package-migration test session

<!-- package-session -->
Date: 2026-09-25. Runtime/compiler: Temurin Java 25.0.4.1.

Result: **PASS** — P1–P4 (five packaged sessions, plus five matching baseline
sessions). All 22 sources compiled into a clean output folder. The complete
stdout, zero exit codes, empty stderr, and final saved-file contents matched the
predeclared expectations and the original default-package implementation.
Only line endings were normalized; spaces and the Unicode banner were preserved.

Each case used an isolated working directory. The project save file was not
used. Below are the actual inputs and complete captured outputs; input is shown
separately because redirected stdin is not echoed by the application.

### P1, session 1: PASS

Console input:

```text
list
todo read book
deadline return book /by 2/12/2019 1800
event project meeting /from 2/12/2019 1400 /to 4/12/2019 1600
list
mark 2
unmark 2
mark 1
delete 2
list
on 3/12/2019
on 1/12/2019
bye
```

Console output (stderr empty; exit code 0):

```text
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: 2 Dec 2019 18:00)
 3.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 No deadlines or events occur on 2019-12-01.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P1, session 2: PASS

Console input:

```text
list
on 3/12/2019
bye
```

Console output (stderr empty; exit code 0):

```text
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P2, session 1: PASS

Console input:

```text

blah
todo
deadline homework
event meeting /from 2pm
mark abc
delete 0
deadline return book /by tomorrow
event meeting /from 2/12/2019 /to 2/12/2019 1600
on tomorrow
list
bye
```

Console output (stderr empty; exit code 0):

```text
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 Error: please enter a command or task.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Error: use 'todo description' to add a task without a date.
____________________________________________________________
____________________________________________________________
 Error: use 'deadline description /by date or time'.
____________________________________________________________
____________________________________________________________
 Error: use 'event description /from start /to end'.
____________________________________________________________
____________________________________________________________
 Error: 'abc' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Error: task 0 does not exist; use 'list' to see valid task numbers.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P3, session 1: PASS

Console input:

```text
list
bye
```

Console output (stderr empty; exit code 0):

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P4, session 1: PASS

Console input (immediate EOF):

```text
```

Console output (stderr empty; exit code 0):

```text
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
```
<!-- /package-session -->

## Historical test plans and records (superseded)

This file contains command-line UI test cases for MARY. Each test starts a fresh program process, so task data is kept only for the duration of that test. The full startup banner is included in the captured session records.

## Test case 1: Add tasks and list them

Aim: Verify that entered task text is stored and displayed by the `list` command.

Inputs:

```text
todo read book
todo return book
list
bye
```

Expected output: The output includes the MARY greeting, typed task markers, and:

```text
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
[T][ ] return book
Now you have 2 tasks in the list.
Here are the tasks in your list:
1.[T][ ] read book
2.[T][ ] return book
Bye. Hope to see you again soon!
```

## Test case 2: Mark and unmark a task

Aim: Verify that `mark N` changes a task to done and `unmark N` changes it back to not done.

Inputs:

```text
todo read book
mark 1
list
unmark 1
list
bye
```

Expected output: The output includes:

```text
Nice! I've marked this task as done:
[X] read book
1.[T][X] read book
OK, I've marked this task as not done yet:
[ ] read book
1.[T][ ] read book
Bye. Hope to see you again soon!
```

## Test case 3: Add todo, deadline, and event tasks

Aim: Verify that MARY stores todo, deadline, and event objects polymorphically in one task list and preserves their date/time details.

Inputs:

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

Expected output:

```text
[T][ ] borrow book
[D][ ] return book (by: Sunday)
[E][ ] project meeting (from: Mon 2pm to: 4pm)
1.[T][ ] borrow book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
Bye. Hope to see you again soon!
```

## Test session record

## Test case 6: Save and reload tasks

Aim: Verify that task changes are saved to the relative current-folder file and loaded by a new chatbot process.

Inputs for the first session:

```text
todo read book
deadline return book /by Sunday
mark 1
bye
```

Expected output: The tasks are added and marked successfully, and a later session started in the same folder displays `[T][X] read book` and `[D][ ] return book (by: Sunday)` after `list`.

Inputs for the second session:

```text
list
bye
```

## Test case 7: Handle corrupted saved data

Aim: Verify that malformed saved records produce a clear startup error and do not crash the chatbot.

Setup: Replace `mary-data.txt` in the current folder with:

```text
not a valid task record
```

Inputs:

```text
list
bye
```

Expected output includes:

```text
Error: the saved task data is corrupted: invalid record on line 1.
MARY has no saved tasks yet.
See you later. Complete your tasks on time!
```

## Test case 8: Parse and search dates and times

Aim: Verify that deadline and event date/time text is parsed into date/time values and that `on d/M/yyyy` finds tasks occurring on that date.

Inputs:

```text
deadline return book /by 2/12/2019 1800
event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600
on 2/12/2019
bye
```

Expected output includes:

```text
[D][ ] return book (by: 2 Dec 2019 18:00)
[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
Tasks occurring on 2019-12-02:
[D][ ] return book (by: 2 Dec 2019 18:00)
[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
```

## Test case 9: Reject invalid date/time input

Aim: Verify that malformed deadline/event date-time values and malformed date searches receive specific correction messages.

Inputs:

```text
deadline return book /by tomorrow
event meeting /from 2/12/2019 /to 2/12/2019 1600
on tomorrow
bye
```

Expected output includes:

```text
Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
Error: use date format d/M/yyyy, for example 2/12/2019.
```

### 2026-09-25 date/time session

Test cases 8 and 9: PASS

The date/time parsing, `on` search, invalid-input messages, and reloading of ISO date-time records matched the expected results.

The test must restore or remove the temporary data file after completion.

### 2026-09-21 persistence session

Test case 6: PASS

The first session saved:

```text
T | 1 | read book
D | 0 | return book | Sunday
```

A second process loaded both records and displayed:

```text
1.[T][X] read book
2.[D][ ] return book (by: Sunday)
```

Test case 7: PASS

Malformed records were reported as a corrupted-data error, and the chatbot continued to accept `list` and `bye`.

### 2026-09-21

Test case 1: PASS

Console input:

```text
todo read book
todo return book
list
bye
```

Console output:

```text
MARY startup banner and greeting displayed.
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
Got it. I've added this task:
  [T][ ] return book
Now you have 2 tasks in the list.
Here are the tasks in your list:
1.[T][ ] read book
2.[T][ ] return book
See you later. Complete your tasks on time!
```

## Test case 5: Delete a task

Aim: Verify that `delete N` removes the selected task, shifts later task numbers, and reports the updated task count.

Inputs:

```text
todo read book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
delete 2
list
bye
```

Expected output:

```text
1.[T][ ] read book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
Noted. I've removed this task:
[D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
1.[T][ ] read book
2.[E][ ] project meeting (from: Mon 2pm to: 4pm)
See you later. Complete your tasks on time!
```

### 2026-09-21 deletion session

Test case 5: PASS

Console input:

```text
todo read book
todo return book
event meeting /from 2pm /to 4pm
list
delete 3
list
bye
```

Console output:

```text
1.[T][ ] read book
2.[T][ ] return book
3.[E][ ] meeting (from: 2pm to: 4pm)
Noted. I've removed this task:
  [E][ ] meeting (from: 2pm to: 4pm)
Now you have 2 tasks in the list.
1.[T][ ] read book
2.[T][ ] return book
See you later. Complete your tasks on time!
```

Test case 2: PASS

Console input:

```text
read book
mark 1
list
unmark 1
list
bye
```

Console output:

```text
Got it. I've added this task:
  [T][ ] read book
Nice! I've marked this task as done:
  [T][X] read book
1.[T][X] read book
OK, I've marked this task as not done yet:
  [T][ ] read book
1.[T][ ] read book
See you later. Complete your tasks on time!
```

Test case 3: PASS

Console input:

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

Console output:

```text
Got it. I've added this task:
  [T][ ] borrow book
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
See you later. Complete your tasks on time!
```

Implementation note: The three task types are now represented by `Todo`, `Deadline`, and `Event` subclasses in separate files. They are stored together in `Task[]` and formatted through polymorphic `toString()` methods.

## Test case 4: Explain invalid input

Aim: Verify that invalid commands and malformed task commands produce specific correction guidance without terminating the session.

Inputs:

```text
todo
deadline homework
event meeting /from 2pm
mark abc
blah
bye
```

Expected output:

```text
Error: use 'todo description' to add a task without a date.
Error: use 'deadline description /by date or time'.
Error: use 'event description /from start /to end'.
Error: 'abc' is not a valid task number; use a positive whole number.
Error: I don't recognize that command; use todo, deadline, event, list, mark, unmark, or bye.
See you later. Complete your tasks on time!
```

### 2026-09-21 error-handling session

Test case 4: PASS

Console input:

```text
todo
deadline homework
event meeting /from 2pm
mark abc
blah
bye
```

Console output:

```text
Error: use 'todo description' to add a task without a date.
Error: use 'deadline description /by date or time'.
Error: use 'event description /from start /to end'.
Error: 'abc' is not a valid task number; use a positive whole number.
Error: I don't recognize that command; use todo, deadline, event, list, mark, unmark, or bye.
See you later. Complete your tasks on time!
```
## Fat JAR verification — 2026-09-27

Built with Java 25 using Gradle `test shadowJar --rerun-tasks`: 52 JUnit tests passed.
Verified `build/libs/mary.jar` has `Main-Class: mary.MARY`, includes the entry-point class,
and excludes JUnit. Existing R1, R2, P1–P4 aims, inputs and expected outputs remain unchanged.
All six UI cases (seven fresh-process sessions) passed using `java -jar` in isolated
working directories; saved data also matched. No project task data was changed.
Inputs below are JSON strings to make trailing spaces and empty input explicit.
Actual output matched the recorded expected output exactly, ignoring only line endings.

### R1, session 1: PASS

Console input (one array element per line; an empty array means EOF):

```json
[
  "todo read",
  "mark",
  "unmark",
  "mark ",
  "unmark ",
  "on 29/2/2023",
  "deadline invalid /by 31/4/2024 1800",
  "event invalid /from 29/2/2023 1400 /to 1/3/2023 1600",
  "list",
  "bye"
]
```

Complete console output:

```text
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### R2, session 1: PASS

Console input (one array element per line; an empty array means EOF):

```json
[
  "list",
  "bye"
]
```

Complete console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P1, session 1: PASS

Console input (one array element per line; an empty array means EOF):

```json
[
  "list",
  "todo read book",
  "deadline return book /by 2/12/2019 1800",
  "event project meeting /from 2/12/2019 1400 /to 4/12/2019 1600",
  "list",
  "mark 2",
  "unmark 2",
  "mark 1",
  "delete 2",
  "list",
  "on 3/12/2019",
  "on 1/12/2019",
  "bye"
]
```

Complete console output:

```text
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: 2 Dec 2019 18:00)
 3.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 No deadlines or events occur on 2019-12-01.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P1, session 2: PASS

Console input (one array element per line; an empty array means EOF):

```json
[
  "list",
  "on 3/12/2019",
  "bye"
]
```

Complete console output:

```text
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P2, session 1: PASS

Console input (one array element per line; an empty array means EOF):

```json
[
  "",
  "blah",
  "todo",
  "deadline homework",
  "event meeting /from 2pm",
  "mark abc",
  "delete 0",
  "deadline return book /by tomorrow",
  "event meeting /from 2/12/2019 /to 2/12/2019 1600",
  "on tomorrow",
  "list",
  "bye"
]
```

Complete console output:

```text
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 Error: please enter a command or task.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Error: use 'todo description' to add a task without a date.
____________________________________________________________
____________________________________________________________
 Error: use 'deadline description /by date or time'.
____________________________________________________________
____________________________________________________________
 Error: use 'event description /from start /to end'.
____________________________________________________________
____________________________________________________________
 Error: 'abc' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Error: task 0 does not exist; use 'list' to see valid task numbers.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P3, session 1: PASS

Console input (one array element per line; an empty array means EOF):

```json
[
  "list",
  "bye"
]
```

Complete console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P4, session 1: PASS

Console input (one array element per line; an empty array means EOF):

```json
[]
```

Complete console output:

```text
____________________________________________________________
____________________________________________________________
███╗   ███╗ █████╗ ██████╗ ██╗   ██╗
████╗ ████║██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
Hi! I'm MARY.
What have you got for me today?
____________________________________________________________
```

## ASCII cat banner verification — 2026-09-27

Aim: display the selected ASCII cat artwork and greeting without Unicode block
characters, preserving all task-command and persistence behavior. The current
suite's shared welcome expectation was updated before testing; historical logs
are unchanged. Added UiTest for exact welcome output and ASCII-only characters.
Java 25 Gradle `test shadowJar --rerun-tasks` passed all 53 JUnit tests.
The test-ui skill ran the rebuilt JAR in isolated working directories.
R1, R2, P1–P4 passed: six cases/seven sessions, including exact stdout,
exit status, and saved data. Inputs below use JSON strings to preserve spaces.

### R1, session 1: PASS

Console input:

```json
[
  "todo read",
  "mark",
  "unmark",
  "mark ",
  "unmark ",
  "on 29/2/2023",
  "deadline invalid /by 31/4/2024 1800",
  "event invalid /from 29/2/2023 1400 /to 1/3/2023 1600",
  "list",
  "bye"
]
```

Actual console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### R2, session 1: PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Actual console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P1, session 1: PASS

Console input:

```json
[
  "list",
  "todo read book",
  "deadline return book /by 2/12/2019 1800",
  "event project meeting /from 2/12/2019 1400 /to 4/12/2019 1600",
  "list",
  "mark 2",
  "unmark 2",
  "mark 1",
  "delete 2",
  "list",
  "on 3/12/2019",
  "on 1/12/2019",
  "bye"
]
```

Actual console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: 2 Dec 2019 18:00)
 3.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 No deadlines or events occur on 2019-12-01.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P1, session 2: PASS

Console input:

```json
[
  "list",
  "on 3/12/2019",
  "bye"
]
```

Actual console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P2, session 1: PASS

Console input:

```json
[
  "",
  "blah",
  "todo",
  "deadline homework",
  "event meeting /from 2pm",
  "mark abc",
  "delete 0",
  "deadline return book /by tomorrow",
  "event meeting /from 2/12/2019 /to 2/12/2019 1600",
  "on tomorrow",
  "list",
  "bye"
]
```

Actual console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Error: please enter a command or task.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Error: use 'todo description' to add a task without a date.
____________________________________________________________
____________________________________________________________
 Error: use 'deadline description /by date or time'.
____________________________________________________________
____________________________________________________________
 Error: use 'event description /from start /to end'.
____________________________________________________________
____________________________________________________________
 Error: 'abc' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Error: task 0 does not exist; use 'list' to see valid task numbers.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P3, session 1: PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Actual console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P4, session 1: PASS

Console input:

```json
[]
```

Actual console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
```

## Keyword search verification — 2026-09-27

The test-ui skill ran the recorded R1, R2, P1–P4, F1 and F2 cases against the
rebuilt JAR using Java 25. All eight cases/nine sessions passed exact stdout,
exit status, and saved-data checks. F1 verifies case-insensitive substring and
phrase searches, matching all task types/statuses, excluding metadata, invalid
input, and unchanged list order/storage. F2 verifies empty-list searching without
creating a data file. Expected results were recorded before running the suite.
All 62 JUnit tests passed with Gradle `test shadowJar --rerun-tasks`.
Inputs below use JSON strings so empty input and trailing spaces remain visible.

### R1, session 1: PASS

Console input:

```json
[
  "todo read",
  "mark",
  "unmark",
  "mark ",
  "unmark ",
  "on 29/2/2023",
  "deadline invalid /by 31/4/2024 1800",
  "event invalid /from 29/2/2023 1400 /to 1/3/2023 1600",
  "list",
  "bye"
]
```

Complete console output (matches expected):

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### R2, session 1: PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Complete console output (matches expected):

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P1, session 1: PASS

Console input:

```json
[
  "list",
  "todo read book",
  "deadline return book /by 2/12/2019 1800",
  "event project meeting /from 2/12/2019 1400 /to 4/12/2019 1600",
  "list",
  "mark 2",
  "unmark 2",
  "mark 1",
  "delete 2",
  "list",
  "on 3/12/2019",
  "on 1/12/2019",
  "bye"
]
```

Complete console output (matches expected):

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: 2 Dec 2019 18:00)
 3.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 No deadlines or events occur on 2019-12-01.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P1, session 2: PASS

Console input:

```json
[
  "list",
  "on 3/12/2019",
  "bye"
]
```

Complete console output (matches expected):

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P2, session 1: PASS

Console input:

```json
[
  "",
  "blah",
  "todo",
  "deadline homework",
  "event meeting /from 2pm",
  "mark abc",
  "delete 0",
  "deadline return book /by tomorrow",
  "event meeting /from 2/12/2019 /to 2/12/2019 1600",
  "on tomorrow",
  "list",
  "bye"
]
```

Complete console output (matches expected):

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Error: please enter a command or task.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, find, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Error: use 'todo description' to add a task without a date.
____________________________________________________________
____________________________________________________________
 Error: use 'deadline description /by date or time'.
____________________________________________________________
____________________________________________________________
 Error: use 'event description /from start /to end'.
____________________________________________________________
____________________________________________________________
 Error: 'abc' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Error: task 0 does not exist; use 'list' to see valid task numbers.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P3, session 1: PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Complete console output (matches expected):

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P4, session 1: PASS

Console input:

```json
[]
```

Complete console output (matches expected):

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
```

### F1, session 1: PASS

Console input:

```json
[
  "find book",
  "find   BOOK  ",
  "find ook",
  "find read book",
  "find Dec",
  "find [X]",
  "find missing",
  "find",
  "find   ",
  "finder book",
  "list",
  "bye"
]
```

Complete console output (matches expected):

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, find, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] buy bread
 2.[T][X] read book
 3.[D][X] return Book (by: 2 Dec 2019 18:00)
 4.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### F2, session 1: PASS

Console input:

```json
[
  "find book",
  "bye"
]
```

Complete console output (matches expected):

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

## Checkstyle regression session — 2026-10-02

Reviewed the existing aims, inputs, and expected outputs: no changes needed because
this update changes coding style and build checks, not chatbot behavior.
Java 25 `check shadowJar --rerun-tasks` passed, including 62 JUnit tests and
Checkstyle for production and tests (zero violations). The test-ui skill ran
R1, R2, P1–P4, F1 and F2: eight cases, nine isolated processes, all passed.
Each process matched exact stdout (apart from line endings), empty stderr, exit
code 0, and the case's expected saved data. The prioritized JUnit method selection
remains unchanged; no assertion expectations were weakened.

Inputs below are JSON string arrays to preserve blank commands and trailing spaces.
Each command was followed by a newline; stdin was then closed. Output is complete.

### R1, session 1 — PASS

Console input:

```json
[
  "todo read",
  "mark",
  "unmark",
  "mark ",
  "unmark ",
  "on 29/2/2023",
  "deadline invalid /by 31/4/2024 1800",
  "event invalid /from 29/2/2023 1400 /to 1/3/2023 1600",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### R2, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P1, session 1 — PASS

Console input:

```json
[
  "list",
  "todo read book",
  "deadline return book /by 2/12/2019 1800",
  "event project meeting /from 2/12/2019 1400 /to 4/12/2019 1600",
  "list",
  "mark 2",
  "unmark 2",
  "mark 1",
  "delete 2",
  "list",
  "on 3/12/2019",
  "on 1/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: 2 Dec 2019 18:00)
 3.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 No deadlines or events occur on 2019-12-01.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P1, session 2 — PASS

Console input:

```json
[
  "list",
  "on 3/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P2, session 1 — PASS

Console input:

```json
[
  "",
  "blah",
  "todo",
  "deadline homework",
  "event meeting /from 2pm",
  "mark abc",
  "delete 0",
  "deadline return book /by tomorrow",
  "event meeting /from 2/12/2019 /to 2/12/2019 1600",
  "on tomorrow",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Error: please enter a command or task.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, find, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Error: use 'todo description' to add a task without a date.
____________________________________________________________
____________________________________________________________
 Error: use 'deadline description /by date or time'.
____________________________________________________________
____________________________________________________________
 Error: use 'event description /from start /to end'.
____________________________________________________________
____________________________________________________________
 Error: 'abc' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Error: task 0 does not exist; use 'list' to see valid task numbers.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P3, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### P4, session 1 — PASS

Console input:

```json
[]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
```

### F1, session 1 — PASS

Console input:

```json
[
  "find book",
  "find   BOOK  ",
  "find ook",
  "find read book",
  "find Dec",
  "find [X]",
  "find missing",
  "find",
  "find   ",
  "finder book",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, find, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] buy bread
 2.[T][X] read book
 3.[D][X] return Book (by: 2 Dec 2019 18:00)
 4.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### F2, session 1 — PASS

Console input:

```json
[
  "find book",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

## JavaFX regression session — 2026-10-02

Result: PASS. Java 25 `check guiTest shadowJar` completed successfully:
67 non-graphical JUnit tests and seven JavaFX control tests passed with no failures
or skips. Checkstyle reported zero violations for production and test sources.
Reviewed the rendered scene at `build/reports/gui/mary.png`; corrected an oversized
command-guide title and reran the checks. The GUI run covers G1–G7 above.

The packaged JAR's default GUI launcher created its MARY window and closed
normally (exit 0) in an isolated working directory. No task file was created;
JavaFX extracted native libraries only into that directory's `.mary/javafx-cache`.
An initial smoke-harness check using Process.MainWindowHandle timed out because
the window was deliberately launched hidden. That session stopped immediately;
a separate corrected check enumerated only windows owned by its test process,
verified MARY's title, and closed the window normally. No app fix was needed.

The classpath-based JavaFX tests and fat JAR emitted this runtime warning:

```text
WARNING: Unsupported JavaFX configuration: classes were loaded from 'unnamed module ...'
```

No JavaFX exceptions or CSS errors occurred. This warning is distinct from
Checkstyle; the non-modular fat JAR was verified to launch and exit successfully.
Only Windows was executed here; other platforms require their own build/test run.

The test-ui skill ran R1, R2, P1–P4, F1 and F2 using `mary.jar --cli`:
eight cases, nine processes, all passed. Exact stdout (normalizing only CRLF),
empty stderr, exit status and saved data matched the unchanged JSON expectations.
Input below uses JSON arrays to preserve empty commands and trailing spaces.
Each command ends in a newline; stdin closes afterward. Output is complete.

### JavaFX CLI regression R1, session 1 — PASS

Console input:

```json
[
  "todo read",
  "mark",
  "unmark",
  "mark ",
  "unmark ",
  "on 29/2/2023",
  "deadline invalid /by 31/4/2024 1800",
  "event invalid /from 29/2/2023 1400 /to 1/3/2023 1600",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### JavaFX CLI regression R2, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### JavaFX CLI regression P1, session 1 — PASS

Console input:

```json
[
  "list",
  "todo read book",
  "deadline return book /by 2/12/2019 1800",
  "event project meeting /from 2/12/2019 1400 /to 4/12/2019 1600",
  "list",
  "mark 2",
  "unmark 2",
  "mark 1",
  "delete 2",
  "list",
  "on 3/12/2019",
  "on 1/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: 2 Dec 2019 18:00)
 3.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 No deadlines or events occur on 2019-12-01.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### JavaFX CLI regression P1, session 2 — PASS

Console input:

```json
[
  "list",
  "on 3/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### JavaFX CLI regression P2, session 1 — PASS

Console input:

```json
[
  "",
  "blah",
  "todo",
  "deadline homework",
  "event meeting /from 2pm",
  "mark abc",
  "delete 0",
  "deadline return book /by tomorrow",
  "event meeting /from 2/12/2019 /to 2/12/2019 1600",
  "on tomorrow",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Error: please enter a command or task.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, find, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Error: use 'todo description' to add a task without a date.
____________________________________________________________
____________________________________________________________
 Error: use 'deadline description /by date or time'.
____________________________________________________________
____________________________________________________________
 Error: use 'event description /from start /to end'.
____________________________________________________________
____________________________________________________________
 Error: 'abc' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Error: task 0 does not exist; use 'list' to see valid task numbers.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### JavaFX CLI regression P3, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### JavaFX CLI regression P4, session 1 — PASS

Console input:

```json
[]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
```

### JavaFX CLI regression F1, session 1 — PASS

Console input:

```json
[
  "find book",
  "find   BOOK  ",
  "find ook",
  "find read book",
  "find Dec",
  "find [X]",
  "find missing",
  "find",
  "find   ",
  "finder book",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, find, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] buy bread
 2.[T][X] read book
 3.[D][X] return Book (by: 2 Dec 2019 18:00)
 4.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### JavaFX CLI regression F2, session 1 — PASS

Console input:

```json
[
  "find book",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

## Varargs regression session — 2026-10-02

Reviewed the test plan: commands and expected output are unchanged. Ran the
`test-ui` workflow against the rebuilt fat JAR with Java 25 in isolated temporary
directories; personal saved tasks were not used or changed. All eight cases
passed across nine sessions. Exact stdout, exit status, stderr and persistence
checks passed. Each input array entry below is followed by a newline, then stdin
is closed. Empty arrays represent immediate EOF. The complete console records
follow.

### Varargs R1, session 1 — PASS

Console input:

```json
[
  "todo read",
  "mark",
  "unmark",
  "mark ",
  "unmark ",
  "on 29/2/2023",
  "deadline invalid /by 31/4/2024 1800",
  "event invalid /from 29/2/2023 1400 /to 1/3/2023 1600",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Varargs R2, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Varargs P1, session 1 — PASS

Console input:

```json
[
  "list",
  "todo read book",
  "deadline return book /by 2/12/2019 1800",
  "event project meeting /from 2/12/2019 1400 /to 4/12/2019 1600",
  "list",
  "mark 2",
  "unmark 2",
  "mark 1",
  "delete 2",
  "list",
  "on 3/12/2019",
  "on 1/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: 2 Dec 2019 18:00)
 3.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 No deadlines or events occur on 2019-12-01.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Varargs P1, session 2 — PASS

Console input:

```json
[
  "list",
  "on 3/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Varargs P2, session 1 — PASS

Console input:

```json
[
  "",
  "blah",
  "todo",
  "deadline homework",
  "event meeting /from 2pm",
  "mark abc",
  "delete 0",
  "deadline return book /by tomorrow",
  "event meeting /from 2/12/2019 /to 2/12/2019 1600",
  "on tomorrow",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Error: please enter a command or task.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, find, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Error: use 'todo description' to add a task without a date.
____________________________________________________________
____________________________________________________________
 Error: use 'deadline description /by date or time'.
____________________________________________________________
____________________________________________________________
 Error: use 'event description /from start /to end'.
____________________________________________________________
____________________________________________________________
 Error: 'abc' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Error: task 0 does not exist; use 'list' to see valid task numbers.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Varargs P3, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Varargs P4, session 1 — PASS

Console input:

```json
[]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
```

### Varargs F1, session 1 — PASS

Console input:

```json
[
  "find book",
  "find   BOOK  ",
  "find ook",
  "find read book",
  "find Dec",
  "find [X]",
  "find missing",
  "find",
  "find   ",
  "finder book",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, find, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] buy bread
 2.[T][X] read book
 3.[D][X] return Book (by: 2 Dec 2019 18:00)
 4.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Varargs F2, session 1 — PASS

Console input:

```json
[
  "find book",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

## Cross-platform packaging execution — 2026-10-03

Final build: Java 25, `check guiTest jarGuiSmoke --rerun-tasks`.
J1 and J2 passed. 71 unit tests, seven GUI tests and Checkstyle passed.
R1, R2, P1–P4, F1 and F2 passed in nine isolated console sessions below.

During development, the first packaging check failed and the run stopped:
expected `verifyJar` success; actual:
`Missing JavaFX native library: com/sun/glass/ui/gtk/GtkApplication.class`.
An already-dispatched Linux probe against that incomplete artifact also failed
(exit 1): expected the J3 success line; actual `ClassNotFoundException:
com.sun.glass.ui.gtk.GtkPlatformFactory`, followed by
`Failed to load Glass factory class` and a NullPointerException.
Including platform Java classes as well as native libraries and forcing a fresh
build resolved this. The final build and Linux rerun passed.

### J2 — Windows packaged rendering

Input: `gradlew.bat jarGuiSmoke`, Java 25.

```text
Oct 03, 2026 10:08:13 PM com.sun.javafx.application.PlatformImpl startup
WARNING: Unsupported JavaFX configuration: classes were loaded from 'unnamed module @20fa23c1'
GUI_SMOKE_PASS: MARY opened and rendered successfully.
```

Exit: 0.

### J3 — Linux packaged rendering

Ubuntu 24.04 / WSLg x86_64, Temurin Java 25.0.4.1.
Working directory: isolated `_temp/linux-smoke/run`.
Input (JAR and compiled helper paths supplied as absolute paths at execution):

```sh
java --enable-native-access=ALL-UNNAMED -cp mary.jar:test-classes mary.PackagedGuiSmoke
```

Complete combined console output:

```text
Oct 03, 2026 10:09:19 PM com.sun.javafx.application.PlatformImpl startup
WARNING: Unsupported JavaFX configuration: classes were loaded from 'unnamed module @593634ad'
MESA: error: ZINK: failed to choose pdev
glx: failed to create drisw screen
MESA: error: ZINK: failed to choose pdev
glx: failed to create drisw screen
GUI_SMOKE_PASS: MARY opened and rendered successfully.
```

Exit: 0. WSL emitted graphics-driver diagnostics, but JavaFX successfully opened
and rendered the application. No QuantumRenderer/no-toolkit crash occurred.
macOS runtime testing and the optional Apple Silicon build were not performed.

### J4 — Windows executable launch

Input: `java -jar C:/iP/ip/build/libs/mary.jar`, without native-access flags.
Working directory: a fresh ignored temporary directory. Wait for the MARY-titled
window, then request normal close. Expected: window opens, exit 0, no restricted
native-access warning. Actual harness record and full application stderr:

```text
WINDOW: MARY | Your purr-sonal task assistant
EXIT: 0

Oct 03, 2026 10:10:47 PM com.sun.javafx.application.PlatformImpl startup
WARNING: Unsupported JavaFX configuration: classes were loaded from 'unnamed module @20a4d60'
```

Application stdout was empty. A first harness attempt selected a temporary
untitled JavaFX window and timed out closing it; that process was terminated.
Waiting specifically for the MARY title corrected the harness and the rerun
above passed. This was a test-harness issue, not a renderer failure.

### Console regression records

Each JSON input entry was followed by a newline; stdin was then closed. Empty
input means immediate EOF. Output comparisons allow only line-ending differences.
All sessions exited 0 with empty stderr; saved-file checks also passed.

#### Packaging R1, session 1 — PASS

Console input:

```json
[
  "todo read",
  "mark",
  "unmark",
  "mark ",
  "unmark ",
  "on 29/2/2023",
  "deadline invalid /by 31/4/2024 1800",
  "event invalid /from 29/2/2023 1400 /to 1/3/2023 1600",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

#### Packaging R2, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

#### Packaging P1, session 1 — PASS

Console input:

```json
[
  "list",
  "todo read book",
  "deadline return book /by 2/12/2019 1800",
  "event project meeting /from 2/12/2019 1400 /to 4/12/2019 1600",
  "list",
  "mark 2",
  "unmark 2",
  "mark 1",
  "delete 2",
  "list",
  "on 3/12/2019",
  "on 1/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: 2 Dec 2019 18:00)
 3.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 No deadlines or events occur on 2019-12-01.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

#### Packaging P1, session 2 — PASS

Console input:

```json
[
  "list",
  "on 3/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

#### Packaging P2, session 1 — PASS

Console input:

```json
[
  "",
  "blah",
  "todo",
  "deadline homework",
  "event meeting /from 2pm",
  "mark abc",
  "delete 0",
  "deadline return book /by tomorrow",
  "event meeting /from 2/12/2019 /to 2/12/2019 1600",
  "on tomorrow",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Error: please enter a command or task.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, find, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Error: use 'todo description' to add a task without a date.
____________________________________________________________
____________________________________________________________
 Error: use 'deadline description /by date or time'.
____________________________________________________________
____________________________________________________________
 Error: use 'event description /from start /to end'.
____________________________________________________________
____________________________________________________________
 Error: 'abc' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Error: task 0 does not exist; use 'list' to see valid task numbers.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

#### Packaging P3, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

#### Packaging P4, session 1 — PASS

Console input:

```json
[]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
```

#### Packaging F1, session 1 — PASS

Console input:

```json
[
  "find book",
  "find   BOOK  ",
  "find ook",
  "find read book",
  "find Dec",
  "find [X]",
  "find missing",
  "find",
  "find   ",
  "finder book",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, find, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] buy bread
 2.[T][X] read book
 3.[D][X] return Book (by: 2 Dec 2019 18:00)
 4.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

#### Packaging F2, session 1 — PASS

Console input:

```json
[
  "find book",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

## Assertion regression execution — 2026-10-03

Aim: internal assertions must not change valid command behavior or replace normal
input/file error handling. Inputs and expected outputs remain the package-suite
cases R1, R2, P1–P4, F1 and F2 above. Run every case with Java 25 both with `-ea`
and with assertions disabled (the default), using isolated working directories.
Both runs passed all eight cases/nine sessions: 18 processes total. All exit codes
were zero, stderr was empty, and saved-data checks passed. Full console input and
output below is shared by both runs: the records were compared and are identical.
Each input entry is followed by a newline; stdin is then closed.

Gradle `check guiTest jarGuiSmoke` passed 72 unit and eight GUI tests with
assertions enabled, plus JAR verification and rendering smoke checks. The GUI
negative test intentionally invokes submission off the FX thread and expects
`AssertionError: Command submission must run on the JavaFX thread`; it passed.
An initial Checkstyle lambda-wrapping violation in that test was fixed before the
successful rerun; no test assertions were weakened. Assertions and their
justifications are documented in README.md.

### Assertions enabled AND disabled: R1, session 1 — PASS

Console input:

```json
[
  "todo read",
  "mark",
  "unmark",
  "mark ",
  "unmark ",
  "on 29/2/2023",
  "deadline invalid /by 31/4/2024 1800",
  "event invalid /from 29/2/2023 1400 /to 1/3/2023 1600",
  "list",
  "bye"
]
```

Console output (identical in both modes):

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Assertions enabled AND disabled: R2, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output (identical in both modes):

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Assertions enabled AND disabled: P1, session 1 — PASS

Console input:

```json
[
  "list",
  "todo read book",
  "deadline return book /by 2/12/2019 1800",
  "event project meeting /from 2/12/2019 1400 /to 4/12/2019 1600",
  "list",
  "mark 2",
  "unmark 2",
  "mark 1",
  "delete 2",
  "list",
  "on 3/12/2019",
  "on 1/12/2019",
  "bye"
]
```

Console output (identical in both modes):

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: 2 Dec 2019 18:00)
 3.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 No deadlines or events occur on 2019-12-01.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Assertions enabled AND disabled: P1, session 2 — PASS

Console input:

```json
[
  "list",
  "on 3/12/2019",
  "bye"
]
```

Console output (identical in both modes):

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Assertions enabled AND disabled: P2, session 1 — PASS

Console input:

```json
[
  "",
  "blah",
  "todo",
  "deadline homework",
  "event meeting /from 2pm",
  "mark abc",
  "delete 0",
  "deadline return book /by tomorrow",
  "event meeting /from 2/12/2019 /to 2/12/2019 1600",
  "on tomorrow",
  "list",
  "bye"
]
```

Console output (identical in both modes):

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Error: please enter a command or task.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, find, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Error: use 'todo description' to add a task without a date.
____________________________________________________________
____________________________________________________________
 Error: use 'deadline description /by date or time'.
____________________________________________________________
____________________________________________________________
 Error: use 'event description /from start /to end'.
____________________________________________________________
____________________________________________________________
 Error: 'abc' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Error: task 0 does not exist; use 'list' to see valid task numbers.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Assertions enabled AND disabled: P3, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output (identical in both modes):

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Assertions enabled AND disabled: P4, session 1 — PASS

Console input:

```json
[]
```

Console output (identical in both modes):

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
```

### Assertions enabled AND disabled: F1, session 1 — PASS

Console input:

```json
[
  "find book",
  "find   BOOK  ",
  "find ook",
  "find read book",
  "find Dec",
  "find [X]",
  "find missing",
  "find",
  "find   ",
  "finder book",
  "list",
  "bye"
]
```

Console output (identical in both modes):

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, find, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] buy bread
 2.[T][X] read book
 3.[D][X] return Book (by: 2 Dec 2019 18:00)
 4.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Assertions enabled AND disabled: F2, session 1 — PASS

Console input:

```json
[
  "find book",
  "bye"
]
```

Console output (identical in both modes):

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

## Code quality: task parser extraction — 2026-10-03

Reviewed the package-suite plan: commands, outputs and saved formats are unchanged.
Java 25, assertions enabled, isolated working directories. All eight cases passed
in nine processes; exact stdout, empty stderr, exit 0 and saved-data checks passed.
Each input entry below is followed by a newline, then stdin is closed.

### Parser extraction R1, session 1 — PASS

Console input:

```json
[
  "todo read",
  "mark",
  "unmark",
  "mark ",
  "unmark ",
  "on 29/2/2023",
  "deadline invalid /by 31/4/2024 1800",
  "event invalid /from 29/2/2023 1400 /to 1/3/2023 1600",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Parser extraction R2, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Parser extraction P1, session 1 — PASS

Console input:

```json
[
  "list",
  "todo read book",
  "deadline return book /by 2/12/2019 1800",
  "event project meeting /from 2/12/2019 1400 /to 4/12/2019 1600",
  "list",
  "mark 2",
  "unmark 2",
  "mark 1",
  "delete 2",
  "list",
  "on 3/12/2019",
  "on 1/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: 2 Dec 2019 18:00)
 3.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 No deadlines or events occur on 2019-12-01.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Parser extraction P1, session 2 — PASS

Console input:

```json
[
  "list",
  "on 3/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Parser extraction P2, session 1 — PASS

Console input:

```json
[
  "",
  "blah",
  "todo",
  "deadline homework",
  "event meeting /from 2pm",
  "mark abc",
  "delete 0",
  "deadline return book /by tomorrow",
  "event meeting /from 2/12/2019 /to 2/12/2019 1600",
  "on tomorrow",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Error: please enter a command or task.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, find, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Error: use 'todo description' to add a task without a date.
____________________________________________________________
____________________________________________________________
 Error: use 'deadline description /by date or time'.
____________________________________________________________
____________________________________________________________
 Error: use 'event description /from start /to end'.
____________________________________________________________
____________________________________________________________
 Error: 'abc' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Error: task 0 does not exist; use 'list' to see valid task numbers.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Parser extraction P3, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Parser extraction P4, session 1 — PASS

Console input:

```json
[]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
```

### Parser extraction F1, session 1 — PASS

Console input:

```json
[
  "find book",
  "find   BOOK  ",
  "find ook",
  "find read book",
  "find Dec",
  "find [X]",
  "find missing",
  "find",
  "find   ",
  "finder book",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, find, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] buy bread
 2.[T][X] read book
 3.[D][X] return Book (by: 2 Dec 2019 18:00)
 4.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Parser extraction F2, session 1 — PASS

Console input:

```json
[
  "find book",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

## Code quality: storage validation — 2026-10-03

Reviewed the package-suite plan: no user-visible behavior or file format changed.
Java 25, assertions enabled, isolated working directories. All eight cases passed
in nine processes; exact stdout, empty stderr, exit 0 and saved-data checks passed.
Each input entry below is followed by a newline, then stdin is closed.

### Storage validation R1, session 1 — PASS

Console input:

```json
[
  "todo read",
  "mark",
  "unmark",
  "mark ",
  "unmark ",
  "on 29/2/2023",
  "deadline invalid /by 31/4/2024 1800",
  "event invalid /from 29/2/2023 1400 /to 1/3/2023 1600",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Storage validation R2, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Storage validation P1, session 1 — PASS

Console input:

```json
[
  "list",
  "todo read book",
  "deadline return book /by 2/12/2019 1800",
  "event project meeting /from 2/12/2019 1400 /to 4/12/2019 1600",
  "list",
  "mark 2",
  "unmark 2",
  "mark 1",
  "delete 2",
  "list",
  "on 3/12/2019",
  "on 1/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: 2 Dec 2019 18:00)
 3.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 No deadlines or events occur on 2019-12-01.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Storage validation P1, session 2 — PASS

Console input:

```json
[
  "list",
  "on 3/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Storage validation P2, session 1 — PASS

Console input:

```json
[
  "",
  "blah",
  "todo",
  "deadline homework",
  "event meeting /from 2pm",
  "mark abc",
  "delete 0",
  "deadline return book /by tomorrow",
  "event meeting /from 2/12/2019 /to 2/12/2019 1600",
  "on tomorrow",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Error: please enter a command or task.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, find, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Error: use 'todo description' to add a task without a date.
____________________________________________________________
____________________________________________________________
 Error: use 'deadline description /by date or time'.
____________________________________________________________
____________________________________________________________
 Error: use 'event description /from start /to end'.
____________________________________________________________
____________________________________________________________
 Error: 'abc' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Error: task 0 does not exist; use 'list' to see valid task numbers.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Storage validation P3, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Storage validation P4, session 1 — PASS

Console input:

```json
[]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
```

### Storage validation F1, session 1 — PASS

Console input:

```json
[
  "find book",
  "find   BOOK  ",
  "find ook",
  "find read book",
  "find Dec",
  "find [X]",
  "find missing",
  "find",
  "find   ",
  "finder book",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, find, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] buy bread
 2.[T][X] read book
 3.[D][X] return Book (by: 2 Dec 2019 18:00)
 4.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Storage validation F2, session 1 — PASS

Console input:

```json
[
  "find book",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

## Stream-based task search — 2026-10-03

Reviewed the package-suite plan; all commands, expected outputs and saved formats
are unchanged. Java 25, assertions enabled, isolated working directories.
All eight cases passed across nine sessions: exact stdout, empty stderr, exit 0,
and saved-data checks passed. Each JSON input entry is followed by a newline,
then stdin is closed. The complete console records follow.

### Stream search R1, session 1 — PASS

Console input:

```json
[
  "todo read",
  "mark",
  "unmark",
  "mark ",
  "unmark ",
  "on 29/2/2023",
  "deadline invalid /by 31/4/2024 1800",
  "event invalid /from 29/2/2023 1400 /to 1/3/2023 1600",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Stream search R2, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Stream search P1, session 1 — PASS

Console input:

```json
[
  "list",
  "todo read book",
  "deadline return book /by 2/12/2019 1800",
  "event project meeting /from 2/12/2019 1400 /to 4/12/2019 1600",
  "list",
  "mark 2",
  "unmark 2",
  "mark 1",
  "delete 2",
  "list",
  "on 3/12/2019",
  "on 1/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: 2 Dec 2019 18:00)
 3.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 No deadlines or events occur on 2019-12-01.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Stream search P1, session 2 — PASS

Console input:

```json
[
  "list",
  "on 3/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Stream search P2, session 1 — PASS

Console input:

```json
[
  "",
  "blah",
  "todo",
  "deadline homework",
  "event meeting /from 2pm",
  "mark abc",
  "delete 0",
  "deadline return book /by tomorrow",
  "event meeting /from 2/12/2019 /to 2/12/2019 1600",
  "on tomorrow",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Error: please enter a command or task.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, find, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Error: use 'todo description' to add a task without a date.
____________________________________________________________
____________________________________________________________
 Error: use 'deadline description /by date or time'.
____________________________________________________________
____________________________________________________________
 Error: use 'event description /from start /to end'.
____________________________________________________________
____________________________________________________________
 Error: 'abc' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Error: task 0 does not exist; use 'list' to see valid task numbers.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Stream search P3, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Stream search P4, session 1 — PASS

Console input:

```json
[]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
```

### Stream search F1, session 1 — PASS

Console input:

```json
[
  "find book",
  "find   BOOK  ",
  "find ook",
  "find read book",
  "find Dec",
  "find [X]",
  "find missing",
  "find",
  "find   ",
  "finder book",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, find, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] buy bread
 2.[T][X] read book
 3.[D][X] return Book (by: 2 Dec 2019 18:00)
 4.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Stream search F2, session 1 — PASS

Console input:

```json
[
  "find book",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

## Chronological sorting execution — 2026-10-04

Java 25, assertions enabled, rebuilt fat JAR, isolated working directories.
All 11 cases (R1, R2, P1–P4, F1, F2, S1–S3) passed across 13 sessions, including
restart persistence. Exact stdout, empty stderr, exit 0 and saved contents matched
the recorded plan. Each JSON input entry below is followed by a newline; stdin is
then closed. Unknown-command help now includes sort; historical records remain
unchanged. New sorting cases cover ties, todos, repeat sorting, renumbering,
restart, empty lists and invalid arguments.

### Chronological sorting R1, session 1 — PASS

Console input:

```json
[
  "todo read",
  "mark",
  "unmark",
  "mark ",
  "unmark ",
  "on 29/2/2023",
  "deadline invalid /by 31/4/2024 1800",
  "event invalid /from 29/2/2023 1400 /to 1/3/2023 1600",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Chronological sorting R2, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Chronological sorting P1, session 1 — PASS

Console input:

```json
[
  "list",
  "todo read book",
  "deadline return book /by 2/12/2019 1800",
  "event project meeting /from 2/12/2019 1400 /to 4/12/2019 1600",
  "list",
  "mark 2",
  "unmark 2",
  "mark 1",
  "delete 2",
  "list",
  "on 3/12/2019",
  "on 1/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: 2 Dec 2019 18:00)
 3.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 No deadlines or events occur on 2019-12-01.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Chronological sorting P1, session 2 — PASS

Console input:

```json
[
  "list",
  "on 3/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Chronological sorting P2, session 1 — PASS

Console input:

```json
[
  "",
  "blah",
  "todo",
  "deadline homework",
  "event meeting /from 2pm",
  "mark abc",
  "delete 0",
  "deadline return book /by tomorrow",
  "event meeting /from 2/12/2019 /to 2/12/2019 1600",
  "on tomorrow",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Error: please enter a command or task.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, find, sort, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Error: use 'todo description' to add a task without a date.
____________________________________________________________
____________________________________________________________
 Error: use 'deadline description /by date or time'.
____________________________________________________________
____________________________________________________________
 Error: use 'event description /from start /to end'.
____________________________________________________________
____________________________________________________________
 Error: 'abc' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Error: task 0 does not exist; use 'list' to see valid task numbers.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Chronological sorting P3, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Chronological sorting P4, session 1 — PASS

Console input:

```json
[]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
```

### Chronological sorting F1, session 1 — PASS

Console input:

```json
[
  "find book",
  "find   BOOK  ",
  "find ook",
  "find read book",
  "find Dec",
  "find [X]",
  "find missing",
  "find",
  "find   ",
  "finder book",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, find, sort, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] buy bread
 2.[T][X] read book
 3.[D][X] return Book (by: 2 Dec 2019 18:00)
 4.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Chronological sorting F2, session 1 — PASS

Console input:

```json
[
  "find book",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 No matching tasks found.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Chronological sorting S1, session 1 — PASS

Console input:

```json
[
  "sort",
  "sort",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Sorted chronologically (deadlines by due time, events by start time; todos last).
 Here are the tasks in your list:
 1.[D][ ] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[D][ ] late (by: 1 Jan 2027 00:00)
 5.[T][ ] read
 6.[T][ ] buy
____________________________________________________________
____________________________________________________________
 Sorted chronologically (deadlines by due time, events by start time; todos last).
 Here are the tasks in your list:
 1.[D][ ] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[D][ ] late (by: 1 Jan 2027 00:00)
 5.[T][ ] read
 6.[T][ ] buy
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Chronological sorting S1, session 2 — PASS

Console input:

```json
[
  "list",
  "mark 1",
  "delete 4",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[D][ ] late (by: 1 Jan 2027 00:00)
 5.[T][ ] read
 6.[T][ ] buy
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] early (by: 2 Dec 2026 13:00)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] late (by: 1 Jan 2027 00:00)
 Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][X] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[T][ ] read
 5.[T][ ] buy
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Chronological sorting S2, session 1 — PASS

Console input:

```json
[
  "sort",
  "sort descending",
  "sorter",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 MARY has no saved tasks yet.
____________________________________________________________
____________________________________________________________
 Error: use 'sort' without arguments to order tasks chronologically.
____________________________________________________________
____________________________________________________________
 Error: I don't recognize that command; use todo, deadline, event, on, list, find, sort, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

### Chronological sorting S3, session 1 — PASS

Console input:

```json
[
  "sort \t",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Sorted chronologically (deadlines by due time, events by start time; todos last).
 Here are the tasks in your list:
 1.[T][X] z task
 2.[T][ ] a task
____________________________________________________________
____________________________________________________________
See you later. Complete your tasks on time!
____________________________________________________________
```

## Mosswick personality execution — 2026-10-04

Aim: verify the new welcome and replies while preserving commands and saved data.
Java 25 with assertions enabled: R1-R2, P1-P4, F1-F2, S1-S3 passed in 13 isolated
sessions. Exact stdout, empty stderr, zero exit codes, and saved records matched
the active JSON specification above. Personal task data was not used.

### Mosswick R1, session 1 — PASS

Console input:

```json
[
  "todo read",
  "mark",
  "unmark",
  "mark ",
  "unmark ",
  "on 29/2/2023",
  "deadline invalid /by 31/4/2024 1800",
  "event invalid /from 29/2/2023 1400 /to 1/3/2023 1600",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
  .--. .--.
 /    V    \   M O S S W I C K
 '----^----'   Your woodland task librarian.

What shall we note in your field journal?
____________________________________________________________
____________________________________________________________
 Noted in your field journal:
   [T][ ] read
 Journal entries: 1.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Your field journal:
 1.[T][ ] read
____________________________________________________________
____________________________________________________________
Bookmark placed. Until our next little adventure!
____________________________________________________________
```

### Mosswick R2, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
  .--. .--.
 /    V    \   M O S S W I C K
 '----^----'   Your woodland task librarian.

What shall we note in your field journal?
____________________________________________________________
____________________________________________________________
 A fresh page! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
Bookmark placed. Until our next little adventure!
____________________________________________________________
```

### Mosswick P1, session 1 — PASS

Console input:

```json
[
  "list",
  "todo read book",
  "deadline return book /by 2/12/2019 1800",
  "event project meeting /from 2/12/2019 1400 /to 4/12/2019 1600",
  "list",
  "mark 2",
  "unmark 2",
  "mark 1",
  "delete 2",
  "list",
  "on 3/12/2019",
  "on 1/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
  .--. .--.
 /    V    \   M O S S W I C K
 '----^----'   Your woodland task librarian.

What shall we note in your field journal?
____________________________________________________________
____________________________________________________________
 A fresh page! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
 Noted in your field journal:
   [T][ ] read book
 Journal entries: 1.
____________________________________________________________
____________________________________________________________
 Noted in your field journal:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Journal entries: 2.
____________________________________________________________
____________________________________________________________
 Noted in your field journal:
   [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
 Journal entries: 3.
____________________________________________________________
____________________________________________________________
 Your field journal:
 1.[T][ ] read book
 2.[D][ ] return book (by: 2 Dec 2019 18:00)
 3.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 A leaf turned! Task completed:
   [D][X] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Back on the page, ready when you are:
   [D][ ] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 A leaf turned! Task completed:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 I've taken this task off the page:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Journal entries: 2.
____________________________________________________________
____________________________________________________________
 Your field journal:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 No deadlines or events occur on 2019-12-01.
____________________________________________________________
____________________________________________________________
Bookmark placed. Until our next little adventure!
____________________________________________________________
```

### Mosswick P1, session 2 — PASS

Console input:

```json
[
  "list",
  "on 3/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
  .--. .--.
 /    V    \   M O S S W I C K
 '----^----'   Your woodland task librarian.

What shall we note in your field journal?
____________________________________________________________
____________________________________________________________
 Your field journal:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
Bookmark placed. Until our next little adventure!
____________________________________________________________
```

### Mosswick P2, session 1 — PASS

Console input:

```json
[
  "",
  "blah",
  "todo",
  "deadline homework",
  "event meeting /from 2pm",
  "mark abc",
  "delete 0",
  "deadline return book /by tomorrow",
  "event meeting /from 2/12/2019 /to 2/12/2019 1600",
  "on tomorrow",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
  .--. .--.
 /    V    \   M O S S W I C K
 '----^----'   Your woodland task librarian.

What shall we note in your field journal?
____________________________________________________________
____________________________________________________________
 Error: please enter a command or task.
____________________________________________________________
____________________________________________________________
 Error: That command isn't in my field guide; use todo, deadline, event, on, list, find, sort, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Error: use 'todo description' to add a task without a date.
____________________________________________________________
____________________________________________________________
 Error: use 'deadline description /by date or time'.
____________________________________________________________
____________________________________________________________
 Error: use 'event description /from start /to end'.
____________________________________________________________
____________________________________________________________
 Error: 'abc' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Error: task 0 does not exist; use 'list' to see valid task numbers.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 A fresh page! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
Bookmark placed. Until our next little adventure!
____________________________________________________________
```

### Mosswick P3, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
  .--. .--.
 /    V    \   M O S S W I C K
 '----^----'   Your woodland task librarian.

What shall we note in your field journal?
____________________________________________________________
____________________________________________________________
 A fresh page! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
Bookmark placed. Until our next little adventure!
____________________________________________________________
```

### Mosswick P4, session 1 — PASS

Console input:

```json
[]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
  .--. .--.
 /    V    \   M O S S W I C K
 '----^----'   Your woodland task librarian.

What shall we note in your field journal?
____________________________________________________________
```

### Mosswick F1, session 1 — PASS

Console input:

```json
[
  "find book",
  "find   BOOK  ",
  "find ook",
  "find read book",
  "find Dec",
  "find [X]",
  "find missing",
  "find",
  "find   ",
  "finder book",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
  .--. .--.
 /    V    \   M O S S W I C K
 '----^----'   Your woodland task librarian.

What shall we note in your field journal?
____________________________________________________________
____________________________________________________________
 Found these entries in your journal:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Found these entries in your journal:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Found these entries in your journal:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Found these entries in your journal:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
 No entries match that trail. Try another keyword.
____________________________________________________________
____________________________________________________________
 No entries match that trail. Try another keyword.
____________________________________________________________
____________________________________________________________
 No entries match that trail. Try another keyword.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: That command isn't in my field guide; use todo, deadline, event, on, list, find, sort, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Your field journal:
 1.[T][ ] buy bread
 2.[T][X] read book
 3.[D][X] return Book (by: 2 Dec 2019 18:00)
 4.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
Bookmark placed. Until our next little adventure!
____________________________________________________________
```

### Mosswick F2, session 1 — PASS

Console input:

```json
[
  "find book",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
  .--. .--.
 /    V    \   M O S S W I C K
 '----^----'   Your woodland task librarian.

What shall we note in your field journal?
____________________________________________________________
____________________________________________________________
 No entries match that trail. Try another keyword.
____________________________________________________________
____________________________________________________________
Bookmark placed. Until our next little adventure!
____________________________________________________________
```

### Mosswick S1, session 1 — PASS

Console input:

```json
[
  "sort",
  "sort",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
  .--. .--.
 /    V    \   M O S S W I C K
 '----^----'   Your woodland task librarian.

What shall we note in your field journal?
____________________________________________________________
____________________________________________________________
 Journal ordered chronologically (deadlines by due time, events by start time; todos last).
 Your field journal:
 1.[D][ ] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[D][ ] late (by: 1 Jan 2027 00:00)
 5.[T][ ] read
 6.[T][ ] buy
____________________________________________________________
____________________________________________________________
 Journal ordered chronologically (deadlines by due time, events by start time; todos last).
 Your field journal:
 1.[D][ ] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[D][ ] late (by: 1 Jan 2027 00:00)
 5.[T][ ] read
 6.[T][ ] buy
____________________________________________________________
____________________________________________________________
Bookmark placed. Until our next little adventure!
____________________________________________________________
```

### Mosswick S1, session 2 — PASS

Console input:

```json
[
  "list",
  "mark 1",
  "delete 4",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
  .--. .--.
 /    V    \   M O S S W I C K
 '----^----'   Your woodland task librarian.

What shall we note in your field journal?
____________________________________________________________
____________________________________________________________
 Your field journal:
 1.[D][ ] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[D][ ] late (by: 1 Jan 2027 00:00)
 5.[T][ ] read
 6.[T][ ] buy
____________________________________________________________
____________________________________________________________
 A leaf turned! Task completed:
   [D][X] early (by: 2 Dec 2026 13:00)
____________________________________________________________
____________________________________________________________
 I've taken this task off the page:
   [D][ ] late (by: 1 Jan 2027 00:00)
 Journal entries: 5.
____________________________________________________________
____________________________________________________________
 Your field journal:
 1.[D][X] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[T][ ] read
 5.[T][ ] buy
____________________________________________________________
____________________________________________________________
Bookmark placed. Until our next little adventure!
____________________________________________________________
```

### Mosswick S2, session 1 — PASS

Console input:

```json
[
  "sort",
  "sort descending",
  "sorter",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
  .--. .--.
 /    V    \   M O S S W I C K
 '----^----'   Your woodland task librarian.

What shall we note in your field journal?
____________________________________________________________
____________________________________________________________
 A fresh page! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
 Error: use 'sort' without arguments to order tasks chronologically.
____________________________________________________________
____________________________________________________________
 Error: That command isn't in my field guide; use todo, deadline, event, on, list, find, sort, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
Bookmark placed. Until our next little adventure!
____________________________________________________________
```

### Mosswick S3, session 1 — PASS

Console input:

```json
[
  "sort \t",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
  .--. .--.
 /    V    \   M O S S W I C K
 '----^----'   Your woodland task librarian.

What shall we note in your field journal?
____________________________________________________________
____________________________________________________________
 Journal ordered chronologically (deadlines by due time, events by start time; todos last).
 Your field journal:
 1.[T][X] z task
 2.[T][ ] a task
____________________________________________________________
____________________________________________________________
Bookmark placed. Until our next little adventure!
____________________________________________________________
```

## Cat-themed MARY execution — 2026-10-04

Aim: verify restored MARY identity and cat-themed replies with unchanged commands
and saved records. Java 25 with assertions enabled: all 11 cases passed in 13
isolated sessions. Exact stdout, empty stderr, exit status and saved data matched.

### Cat MARY R1, session 1 — PASS

Console input:

```json
[
  "todo read",
  "mark",
  "unmark",
  "mark ",
  "unmark ",
  "on 29/2/2023",
  "deadline invalid /by 31/4/2024 1800",
  "event invalid /from 29/2/2023 1400 /to 1/3/2023 1600",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Purr-fect! I've added this task:
   [T][ ] read
 Tasks on your list: 1.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[T][ ] read
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Cat MARY R2, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Nothing to chase yet! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Cat MARY P1, session 1 — PASS

Console input:

```json
[
  "list",
  "todo read book",
  "deadline return book /by 2/12/2019 1800",
  "event project meeting /from 2/12/2019 1400 /to 4/12/2019 1600",
  "list",
  "mark 2",
  "unmark 2",
  "mark 1",
  "delete 2",
  "list",
  "on 3/12/2019",
  "on 1/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Nothing to chase yet! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
 Purr-fect! I've added this task:
   [T][ ] read book
 Tasks on your list: 1.
____________________________________________________________
____________________________________________________________
 Purr-fect! I've added this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Tasks on your list: 2.
____________________________________________________________
____________________________________________________________
 Purr-fect! I've added this task:
   [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
 Tasks on your list: 3.
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[T][ ] read book
 2.[D][ ] return book (by: 2 Dec 2019 18:00)
 3.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Pawsome! Task completed:
   [D][X] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Back on your list, ready for another pounce:
   [D][ ] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Pawsome! Task completed:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Whisked away! I've removed this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Tasks on your list: 2.
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 No deadlines or events occur on 2019-12-01.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Cat MARY P1, session 2 — PASS

Console input:

```json
[
  "list",
  "on 3/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Cat MARY P2, session 1 — PASS

Console input:

```json
[
  "",
  "blah",
  "todo",
  "deadline homework",
  "event meeting /from 2pm",
  "mark abc",
  "delete 0",
  "deadline return book /by tomorrow",
  "event meeting /from 2/12/2019 /to 2/12/2019 1600",
  "on tomorrow",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Error: please enter a command or task.
____________________________________________________________
____________________________________________________________
 Error: I can't get my paws around that command; use todo, deadline, event, on, list, find, sort, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Error: use 'todo description' to add a task without a date.
____________________________________________________________
____________________________________________________________
 Error: use 'deadline description /by date or time'.
____________________________________________________________
____________________________________________________________
 Error: use 'event description /from start /to end'.
____________________________________________________________
____________________________________________________________
 Error: 'abc' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Error: task 0 does not exist; use 'list' to see valid task numbers.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 Nothing to chase yet! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Cat MARY P3, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: the saved task data is corrupted: invalid record on line 1.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Nothing to chase yet! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Cat MARY P4, session 1 — PASS

Console input:

```json
[]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
```

### Cat MARY F1, session 1 — PASS

Console input:

```json
[
  "find book",
  "find   BOOK  ",
  "find ook",
  "find read book",
  "find Dec",
  "find [X]",
  "find missing",
  "find",
  "find   ",
  "finder book",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Look what I sniffed out:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Look what I sniffed out:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Look what I sniffed out:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Look what I sniffed out:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
 No matching tasks in sight. Try another keyword.
____________________________________________________________
____________________________________________________________
 No matching tasks in sight. Try another keyword.
____________________________________________________________
____________________________________________________________
 No matching tasks in sight. Try another keyword.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: I can't get my paws around that command; use todo, deadline, event, on, list, find, sort, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[T][ ] buy bread
 2.[T][X] read book
 3.[D][X] return Book (by: 2 Dec 2019 18:00)
 4.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Cat MARY F2, session 1 — PASS

Console input:

```json
[
  "find book",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 No matching tasks in sight. Try another keyword.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Cat MARY S1, session 1 — PASS

Console input:

```json
[
  "sort",
  "sort",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Tasks lined up chronologically (deadlines by due time, events by start time; todos last).
 Here's your task lineup:
 1.[D][ ] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[D][ ] late (by: 1 Jan 2027 00:00)
 5.[T][ ] read
 6.[T][ ] buy
____________________________________________________________
____________________________________________________________
 Tasks lined up chronologically (deadlines by due time, events by start time; todos last).
 Here's your task lineup:
 1.[D][ ] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[D][ ] late (by: 1 Jan 2027 00:00)
 5.[T][ ] read
 6.[T][ ] buy
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Cat MARY S1, session 2 — PASS

Console input:

```json
[
  "list",
  "mark 1",
  "delete 4",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[D][ ] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[D][ ] late (by: 1 Jan 2027 00:00)
 5.[T][ ] read
 6.[T][ ] buy
____________________________________________________________
____________________________________________________________
 Pawsome! Task completed:
   [D][X] early (by: 2 Dec 2026 13:00)
____________________________________________________________
____________________________________________________________
 Whisked away! I've removed this task:
   [D][ ] late (by: 1 Jan 2027 00:00)
 Tasks on your list: 5.
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[D][X] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[T][ ] read
 5.[T][ ] buy
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Cat MARY S2, session 1 — PASS

Console input:

```json
[
  "sort",
  "sort descending",
  "sorter",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Nothing to chase yet! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
 Error: use 'sort' without arguments to order tasks chronologically.
____________________________________________________________
____________________________________________________________
 Error: I can't get my paws around that command; use todo, deadline, event, on, list, find, sort, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Cat MARY S3, session 1 — PASS

Console input:

```json
[
  "sort \t",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Tasks lined up chronologically (deadlines by due time, events by start time; todos last).
 Here's your task lineup:
 1.[T][X] z task
 2.[T][ ] a task
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

## Error-handling execution — 2026-10-04

Aim: verify specific command errors, whitespace tolerance, duplicate/range validation,
and continued operation without corrupting saved data. Java 25 with assertions enabled:
R1-R2, P1-P4, F1-F2, S1-S3, and H1 passed in 14 isolated sessions. Exact stdout,
empty stderr, zero exit status, and saved records matched the active JSON specification.
P3 also verified that adding after a failed load cannot overwrite the corrupt file.

### Error handling R1, session 1 — PASS

Console input:

```json
[
  "todo read",
  "mark",
  "unmark",
  "mark ",
  "unmark ",
  "on 29/2/2023",
  "deadline invalid /by 31/4/2024 1800",
  "event invalid /from 29/2/2023 1400 /to 1/3/2023 1600",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Purr-fect! I've added this task:
   [T][ ] read
 Tasks on your list: 1.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[T][ ] read
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Error handling R2, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: could not load saved tasks: invalid record on line 1. Repair or move the data file and restart MARY; saving is disabled to protect it.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Nothing to chase yet! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Error handling P1, session 1 — PASS

Console input:

```json
[
  "list",
  "todo read book",
  "deadline return book /by 2/12/2019 1800",
  "event project meeting /from 2/12/2019 1400 /to 4/12/2019 1600",
  "list",
  "mark 2",
  "unmark 2",
  "mark 1",
  "delete 2",
  "list",
  "on 3/12/2019",
  "on 1/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Nothing to chase yet! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
 Purr-fect! I've added this task:
   [T][ ] read book
 Tasks on your list: 1.
____________________________________________________________
____________________________________________________________
 Purr-fect! I've added this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Tasks on your list: 2.
____________________________________________________________
____________________________________________________________
 Purr-fect! I've added this task:
   [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
 Tasks on your list: 3.
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[T][ ] read book
 2.[D][ ] return book (by: 2 Dec 2019 18:00)
 3.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Pawsome! Task completed:
   [D][X] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Back on your list, ready for another pounce:
   [D][ ] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Pawsome! Task completed:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Whisked away! I've removed this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Tasks on your list: 2.
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 No deadlines or events occur on 2019-12-01.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Error handling P1, session 2 — PASS

Console input:

```json
[
  "list",
  "on 3/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Error handling P2, session 1 — PASS

Console input:

```json
[
  "",
  "blah",
  "todo",
  "deadline homework",
  "event meeting /from 2pm",
  "mark abc",
  "delete 0",
  "deadline return book /by tomorrow",
  "event meeting /from 2/12/2019 /to 2/12/2019 1600",
  "on tomorrow",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Error: please enter a command or task.
____________________________________________________________
____________________________________________________________
 Error: I can't get my paws around that command; use todo, deadline, event, on, list, find, sort, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Error: please add a task description after 'todo'.
____________________________________________________________
____________________________________________________________
 Error: use 'deadline description /by date or time'.
____________________________________________________________
____________________________________________________________
 Error: use 'event description /from start /to end'.
____________________________________________________________
____________________________________________________________
 Error: 'abc' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Error: task 0 does not exist; use 'list' to see valid task numbers.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 Nothing to chase yet! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Error handling P3, session 1 — PASS

Console input:

```json
[
  "list",
  "todo do not overwrite",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: could not load saved tasks: invalid record on line 1. Repair or move the data file and restart MARY; saving is disabled to protect it.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Nothing to chase yet! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
 Error: saving is disabled because loading failed; repair or move the data file and restart MARY. Your original file has not been changed.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Error handling P4, session 1 — PASS

Console input:

```json
[]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
```

### Error handling F1, session 1 — PASS

Console input:

```json
[
  "find book",
  "find   BOOK  ",
  "find ook",
  "find read book",
  "find Dec",
  "find [X]",
  "find missing",
  "find",
  "find   ",
  "finder book",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Look what I sniffed out:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Look what I sniffed out:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Look what I sniffed out:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Look what I sniffed out:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
 No matching tasks in sight. Try another keyword.
____________________________________________________________
____________________________________________________________
 No matching tasks in sight. Try another keyword.
____________________________________________________________
____________________________________________________________
 No matching tasks in sight. Try another keyword.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: I can't get my paws around that command; use todo, deadline, event, on, list, find, sort, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[T][ ] buy bread
 2.[T][X] read book
 3.[D][X] return Book (by: 2 Dec 2019 18:00)
 4.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Error handling F2, session 1 — PASS

Console input:

```json
[
  "find book",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 No matching tasks in sight. Try another keyword.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Error handling S1, session 1 — PASS

Console input:

```json
[
  "sort",
  "sort",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Tasks lined up chronologically (deadlines by due time, events by start time; todos last).
 Here's your task lineup:
 1.[D][ ] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[D][ ] late (by: 1 Jan 2027 00:00)
 5.[T][ ] read
 6.[T][ ] buy
____________________________________________________________
____________________________________________________________
 Tasks lined up chronologically (deadlines by due time, events by start time; todos last).
 Here's your task lineup:
 1.[D][ ] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[D][ ] late (by: 1 Jan 2027 00:00)
 5.[T][ ] read
 6.[T][ ] buy
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Error handling S1, session 2 — PASS

Console input:

```json
[
  "list",
  "mark 1",
  "delete 4",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[D][ ] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[D][ ] late (by: 1 Jan 2027 00:00)
 5.[T][ ] read
 6.[T][ ] buy
____________________________________________________________
____________________________________________________________
 Pawsome! Task completed:
   [D][X] early (by: 2 Dec 2026 13:00)
____________________________________________________________
____________________________________________________________
 Whisked away! I've removed this task:
   [D][ ] late (by: 1 Jan 2027 00:00)
 Tasks on your list: 5.
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[D][X] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[T][ ] read
 5.[T][ ] buy
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Error handling S2, session 1 — PASS

Console input:

```json
[
  "sort",
  "sort descending",
  "sorter",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Nothing to chase yet! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
 Error: use 'sort' without arguments to order tasks chronologically.
____________________________________________________________
____________________________________________________________
 Error: I can't get my paws around that command; use todo, deadline, event, on, list, find, sort, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Error handling S3, session 1 — PASS

Console input:

```json
[
  "sort \t",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Tasks lined up chronologically (deadlines by due time, events by start time; todos last).
 Here's your task lineup:
 1.[T][X] z task
 2.[T][ ] a task
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Error handling H1, session 1 — PASS

Console input:

```json
[
  "  todo\tread book  ",
  "todo read book",
  "todo a | b",
  "list extra",
  "bye now",
  "deadline read /by 1/1/2026 1200 /by 2/1/2026 1200",
  "event camp /from 1/1/2026 1200 /to 1/1/2026 1200",
  "event camp /from 2/1/2026 1200 /to 1/1/2026 1200",
  "mark +1",
  " mark\t1 ",
  " list ",
  " bye "
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Purr-fect! I've added this task:
   [T][ ] read book
 Tasks on your list: 1.
____________________________________________________________
____________________________________________________________
 Error: that task already exists; use 'list' to find it or change its details.
____________________________________________________________
____________________________________________________________
 Error: task descriptions cannot contain '|'; replace it with another character.
____________________________________________________________
____________________________________________________________
 Error: use 'list' without arguments.
____________________________________________________________
____________________________________________________________
 Error: use 'bye' without arguments.
____________________________________________________________
____________________________________________________________
 Error: parameter /by is repeated; specify it only once.
____________________________________________________________
____________________________________________________________
 Error: event end must be after its start; correct /from or /to.
____________________________________________________________
____________________________________________________________
 Error: event end must be after its start; correct /from or /to.
____________________________________________________________
____________________________________________________________
 Error: '+1' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Pawsome! Task completed:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

## Working-folder confinement execution — 2026-10-04

Java 25 with assertions: all 12 console cases passed in 14 isolated sessions.
Exact stdout, empty stderr, zero exit codes, and saved data matched the plan.
Separately, all four LocalPathsTest subprocess scenarios passed, including a real
Windows junction pointing outside the child working folder. Outside fixture paths
were unchanged or remained absent. The packaged GUI used the production cache setup.

### Local writes R1, session 1 — PASS

Console input:

```json
[
  "todo read",
  "mark",
  "unmark",
  "mark ",
  "unmark ",
  "on 29/2/2023",
  "deadline invalid /by 31/4/2024 1800",
  "event invalid /from 29/2/2023 1400 /to 1/3/2023 1600",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Purr-fect! I've added this task:
   [T][ ] read
 Tasks on your list: 1.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[T][ ] read
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Local writes R2, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: could not load saved tasks: invalid record on line 1. Repair or move the data file and restart MARY; saving is disabled to protect it.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Nothing to chase yet! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Local writes P1, session 1 — PASS

Console input:

```json
[
  "list",
  "todo read book",
  "deadline return book /by 2/12/2019 1800",
  "event project meeting /from 2/12/2019 1400 /to 4/12/2019 1600",
  "list",
  "mark 2",
  "unmark 2",
  "mark 1",
  "delete 2",
  "list",
  "on 3/12/2019",
  "on 1/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Nothing to chase yet! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
 Purr-fect! I've added this task:
   [T][ ] read book
 Tasks on your list: 1.
____________________________________________________________
____________________________________________________________
 Purr-fect! I've added this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Tasks on your list: 2.
____________________________________________________________
____________________________________________________________
 Purr-fect! I've added this task:
   [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
 Tasks on your list: 3.
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[T][ ] read book
 2.[D][ ] return book (by: 2 Dec 2019 18:00)
 3.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Pawsome! Task completed:
   [D][X] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Back on your list, ready for another pounce:
   [D][ ] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Pawsome! Task completed:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Whisked away! I've removed this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Tasks on your list: 2.
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 No deadlines or events occur on 2019-12-01.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Local writes P1, session 2 — PASS

Console input:

```json
[
  "list",
  "on 3/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Local writes P2, session 1 — PASS

Console input:

```json
[
  "",
  "blah",
  "todo",
  "deadline homework",
  "event meeting /from 2pm",
  "mark abc",
  "delete 0",
  "deadline return book /by tomorrow",
  "event meeting /from 2/12/2019 /to 2/12/2019 1600",
  "on tomorrow",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Error: please enter a command or task.
____________________________________________________________
____________________________________________________________
 Error: I can't get my paws around that command; use todo, deadline, event, on, list, find, sort, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Error: please add a task description after 'todo'.
____________________________________________________________
____________________________________________________________
 Error: use 'deadline description /by date or time'.
____________________________________________________________
____________________________________________________________
 Error: use 'event description /from start /to end'.
____________________________________________________________
____________________________________________________________
 Error: 'abc' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Error: task 0 does not exist; use 'list' to see valid task numbers.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 Nothing to chase yet! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Local writes P3, session 1 — PASS

Console input:

```json
[
  "list",
  "todo do not overwrite",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: could not load saved tasks: invalid record on line 1. Repair or move the data file and restart MARY; saving is disabled to protect it.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Nothing to chase yet! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
 Error: saving is disabled because loading failed; repair or move the data file and restart MARY. Your original file has not been changed.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Local writes P4, session 1 — PASS

Console input:

```json
[]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
```

### Local writes F1, session 1 — PASS

Console input:

```json
[
  "find book",
  "find   BOOK  ",
  "find ook",
  "find read book",
  "find Dec",
  "find [X]",
  "find missing",
  "find",
  "find   ",
  "finder book",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Look what I sniffed out:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Look what I sniffed out:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Look what I sniffed out:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Look what I sniffed out:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
 No matching tasks in sight. Try another keyword.
____________________________________________________________
____________________________________________________________
 No matching tasks in sight. Try another keyword.
____________________________________________________________
____________________________________________________________
 No matching tasks in sight. Try another keyword.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: I can't get my paws around that command; use todo, deadline, event, on, list, find, sort, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[T][ ] buy bread
 2.[T][X] read book
 3.[D][X] return Book (by: 2 Dec 2019 18:00)
 4.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Local writes F2, session 1 — PASS

Console input:

```json
[
  "find book",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 No matching tasks in sight. Try another keyword.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Local writes S1, session 1 — PASS

Console input:

```json
[
  "sort",
  "sort",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Tasks lined up chronologically (deadlines by due time, events by start time; todos last).
 Here's your task lineup:
 1.[D][ ] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[D][ ] late (by: 1 Jan 2027 00:00)
 5.[T][ ] read
 6.[T][ ] buy
____________________________________________________________
____________________________________________________________
 Tasks lined up chronologically (deadlines by due time, events by start time; todos last).
 Here's your task lineup:
 1.[D][ ] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[D][ ] late (by: 1 Jan 2027 00:00)
 5.[T][ ] read
 6.[T][ ] buy
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Local writes S1, session 2 — PASS

Console input:

```json
[
  "list",
  "mark 1",
  "delete 4",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[D][ ] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[D][ ] late (by: 1 Jan 2027 00:00)
 5.[T][ ] read
 6.[T][ ] buy
____________________________________________________________
____________________________________________________________
 Pawsome! Task completed:
   [D][X] early (by: 2 Dec 2026 13:00)
____________________________________________________________
____________________________________________________________
 Whisked away! I've removed this task:
   [D][ ] late (by: 1 Jan 2027 00:00)
 Tasks on your list: 5.
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[D][X] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[T][ ] read
 5.[T][ ] buy
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Local writes S2, session 1 — PASS

Console input:

```json
[
  "sort",
  "sort descending",
  "sorter",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Nothing to chase yet! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
 Error: use 'sort' without arguments to order tasks chronologically.
____________________________________________________________
____________________________________________________________
 Error: I can't get my paws around that command; use todo, deadline, event, on, list, find, sort, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Local writes S3, session 1 — PASS

Console input:

```json
[
  "sort \t",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Tasks lined up chronologically (deadlines by due time, events by start time; todos last).
 Here's your task lineup:
 1.[T][X] z task
 2.[T][ ] a task
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Local writes H1, session 1 — PASS

Console input:

```json
[
  "  todo\tread book  ",
  "todo read book",
  "todo a | b",
  "list extra",
  "bye now",
  "deadline read /by 1/1/2026 1200 /by 2/1/2026 1200",
  "event camp /from 1/1/2026 1200 /to 1/1/2026 1200",
  "event camp /from 2/1/2026 1200 /to 1/1/2026 1200",
  "mark +1",
  " mark\t1 ",
  " list ",
  " bye "
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Purr-fect! I've added this task:
   [T][ ] read book
 Tasks on your list: 1.
____________________________________________________________
____________________________________________________________
 Error: that task already exists; use 'list' to find it or change its details.
____________________________________________________________
____________________________________________________________
 Error: task descriptions cannot contain '|'; replace it with another character.
____________________________________________________________
____________________________________________________________
 Error: use 'list' without arguments.
____________________________________________________________
____________________________________________________________
 Error: use 'bye' without arguments.
____________________________________________________________
____________________________________________________________
 Error: parameter /by is repeated; specify it only once.
____________________________________________________________
____________________________________________________________
 Error: event end must be after its start; correct /from or /to.
____________________________________________________________
____________________________________________________________
 Error: event end must be after its start; correct /from or /to.
____________________________________________________________
____________________________________________________________
 Error: '+1' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Pawsome! Task completed:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

## Linux JAR execution — 2026-10-04

Tested the existing `build/libs/mary.jar` on Ubuntu 24.04 x86_64 under WSL2/WSLg,
using Temurin Java 25.0.4.1. No source changes or JAR rebuild were needed.
All 12 console cases passed in 14 isolated sessions: exact stdout, empty stderr,
exit code 0, and saved task records matched the active specification.
The packaged GUI smoke test used only the JAR and the compiled test helper;
it opened, rendered, and closed successfully with exit code 0.

JAR SHA-256: `169d82cf2b86b881360f6ab7e817212391f51e78e48cb4f5930ad3d39463b1ab`.

GUI stdout:

```text
GUI_SMOKE_PASS: MARY opened and rendered successfully.
```

GUI stderr (non-fatal warnings; retained rather than suppressed):

```text
Oct 04, 2026 2:54:34 AM com.sun.javafx.application.PlatformImpl startup
WARNING: Unsupported JavaFX configuration: classes were loaded from 'unnamed module @68f7aae2'
MESA: error: ZINK: failed to choose pdev
glx: failed to create drisw screen
MESA: error: ZINK: failed to choose pdev
glx: failed to create drisw screen
```

This verifies this Ubuntu/WSLg environment, not every Linux distribution or CPU
architecture. A graphical session is required for the GUI; console mode uses `--cli`.

### Linux R1, session 1 — PASS

Console input:

```json
[
  "todo read",
  "mark",
  "unmark",
  "mark ",
  "unmark ",
  "on 29/2/2023",
  "deadline invalid /by 31/4/2024 1800",
  "event invalid /from 29/2/2023 1400 /to 1/3/2023 1600",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Purr-fect! I've added this task:
   [T][ ] read
 Tasks on your list: 1.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use 'mark N' or 'unmark N', where N is a task number.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[T][ ] read
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Linux R2, session 1 — PASS

Console input:

```json
[
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: could not load saved tasks: invalid record on line 1. Repair or move the data file and restart MARY; saving is disabled to protect it.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Nothing to chase yet! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Linux P1, session 1 — PASS

Console input:

```json
[
  "list",
  "todo read book",
  "deadline return book /by 2/12/2019 1800",
  "event project meeting /from 2/12/2019 1400 /to 4/12/2019 1600",
  "list",
  "mark 2",
  "unmark 2",
  "mark 1",
  "delete 2",
  "list",
  "on 3/12/2019",
  "on 1/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Nothing to chase yet! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
 Purr-fect! I've added this task:
   [T][ ] read book
 Tasks on your list: 1.
____________________________________________________________
____________________________________________________________
 Purr-fect! I've added this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Tasks on your list: 2.
____________________________________________________________
____________________________________________________________
 Purr-fect! I've added this task:
   [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
 Tasks on your list: 3.
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[T][ ] read book
 2.[D][ ] return book (by: 2 Dec 2019 18:00)
 3.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Pawsome! Task completed:
   [D][X] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Back on your list, ready for another pounce:
   [D][ ] return book (by: 2 Dec 2019 18:00)
____________________________________________________________
____________________________________________________________
 Pawsome! Task completed:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Whisked away! I've removed this task:
   [D][ ] return book (by: 2 Dec 2019 18:00)
 Tasks on your list: 2.
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 No deadlines or events occur on 2019-12-01.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Linux P1, session 2 — PASS

Console input:

```json
[
  "list",
  "on 3/12/2019",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[T][X] read book
 2.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Tasks occurring on 2019-12-03:
 [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Linux P2, session 1 — PASS

Console input:

```json
[
  "",
  "blah",
  "todo",
  "deadline homework",
  "event meeting /from 2pm",
  "mark abc",
  "delete 0",
  "deadline return book /by tomorrow",
  "event meeting /from 2/12/2019 /to 2/12/2019 1600",
  "on tomorrow",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Error: please enter a command or task.
____________________________________________________________
____________________________________________________________
 Error: I can't get my paws around that command; use todo, deadline, event, on, list, find, sort, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Error: please add a task description after 'todo'.
____________________________________________________________
____________________________________________________________
 Error: use 'deadline description /by date or time'.
____________________________________________________________
____________________________________________________________
 Error: use 'event description /from start /to end'.
____________________________________________________________
____________________________________________________________
 Error: 'abc' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Error: task 0 does not exist; use 'list' to see valid task numbers.
____________________________________________________________
____________________________________________________________
 Error: use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
 Error: use event date/time format d/M/yyyy HHmm for both /from and /to.
____________________________________________________________
____________________________________________________________
 Error: use date format d/M/yyyy, for example 2/12/2019.
____________________________________________________________
____________________________________________________________
 Nothing to chase yet! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Linux P3, session 1 — PASS

Console input:

```json
[
  "list",
  "todo do not overwrite",
  "bye"
]
```

Console output:

```text
____________________________________________________________
 Error: could not load saved tasks: invalid record on line 1. Repair or move the data file and restart MARY; saving is disabled to protect it.
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Nothing to chase yet! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
 Error: saving is disabled because loading failed; repair or move the data file and restart MARY. Your original file has not been changed.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Linux P4, session 1 — PASS

Console input:

```json
[]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
```

### Linux F1, session 1 — PASS

Console input:

```json
[
  "find book",
  "find   BOOK  ",
  "find ook",
  "find read book",
  "find Dec",
  "find [X]",
  "find missing",
  "find",
  "find   ",
  "finder book",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Look what I sniffed out:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Look what I sniffed out:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Look what I sniffed out:
 1.[T][X] read book
 2.[D][X] return Book (by: 2 Dec 2019 18:00)
 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
 Look what I sniffed out:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
 No matching tasks in sight. Try another keyword.
____________________________________________________________
____________________________________________________________
 No matching tasks in sight. Try another keyword.
____________________________________________________________
____________________________________________________________
 No matching tasks in sight. Try another keyword.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: use 'find keyword', for example 'find book'.
____________________________________________________________
____________________________________________________________
 Error: I can't get my paws around that command; use todo, deadline, event, on, list, find, sort, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[T][ ] buy bread
 2.[T][X] read book
 3.[D][X] return Book (by: 2 Dec 2019 18:00)
 4.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Linux F2, session 1 — PASS

Console input:

```json
[
  "find book",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 No matching tasks in sight. Try another keyword.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Linux S1, session 1 — PASS

Console input:

```json
[
  "sort",
  "sort",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Tasks lined up chronologically (deadlines by due time, events by start time; todos last).
 Here's your task lineup:
 1.[D][ ] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[D][ ] late (by: 1 Jan 2027 00:00)
 5.[T][ ] read
 6.[T][ ] buy
____________________________________________________________
____________________________________________________________
 Tasks lined up chronologically (deadlines by due time, events by start time; todos last).
 Here's your task lineup:
 1.[D][ ] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[D][ ] late (by: 1 Jan 2027 00:00)
 5.[T][ ] read
 6.[T][ ] buy
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Linux S1, session 2 — PASS

Console input:

```json
[
  "list",
  "mark 1",
  "delete 4",
  "list",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[D][ ] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[D][ ] late (by: 1 Jan 2027 00:00)
 5.[T][ ] read
 6.[T][ ] buy
____________________________________________________________
____________________________________________________________
 Pawsome! Task completed:
   [D][X] early (by: 2 Dec 2026 13:00)
____________________________________________________________
____________________________________________________________
 Whisked away! I've removed this task:
   [D][ ] late (by: 1 Jan 2027 00:00)
 Tasks on your list: 5.
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[D][X] early (by: 2 Dec 2026 13:00)
 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 2 Dec 2026 16:00)
 3.[D][X] tie (by: 2 Dec 2026 14:00)
 4.[T][ ] read
 5.[T][ ] buy
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Linux S2, session 1 — PASS

Console input:

```json
[
  "sort",
  "sort descending",
  "sorter",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Nothing to chase yet! Add a task with 'todo description'.
____________________________________________________________
____________________________________________________________
 Error: use 'sort' without arguments to order tasks chronologically.
____________________________________________________________
____________________________________________________________
 Error: I can't get my paws around that command; use todo, deadline, event, on, list, find, sort, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Linux S3, session 1 — PASS

Console input:

```json
[
  "sort \t",
  "bye"
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Tasks lined up chronologically (deadlines by due time, events by start time; todos last).
 Here's your task lineup:
 1.[T][X] z task
 2.[T][ ] a task
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```

### Linux H1, session 1 — PASS

Console input:

```json
[
  "  todo\tread book  ",
  "todo read book",
  "todo a | b",
  "list extra",
  "bye now",
  "deadline read /by 1/1/2026 1200 /by 2/1/2026 1200",
  "event camp /from 1/1/2026 1200 /to 1/1/2026 1200",
  "event camp /from 2/1/2026 1200 /to 1/1/2026 1200",
  "mark +1",
  " mark\t1 ",
  " list ",
  " bye "
]
```

Console output:

```text
____________________________________________________________
____________________________________________________________
 /\_/\
( o.o )   M A R Y
 > ^ <    Your purr-sonal task assistant.

What's on your list today?
____________________________________________________________
____________________________________________________________
 Purr-fect! I've added this task:
   [T][ ] read book
 Tasks on your list: 1.
____________________________________________________________
____________________________________________________________
 Error: that task already exists; use 'list' to find it or change its details.
____________________________________________________________
____________________________________________________________
 Error: task descriptions cannot contain '|'; replace it with another character.
____________________________________________________________
____________________________________________________________
 Error: use 'list' without arguments.
____________________________________________________________
____________________________________________________________
 Error: use 'bye' without arguments.
____________________________________________________________
____________________________________________________________
 Error: parameter /by is repeated; specify it only once.
____________________________________________________________
____________________________________________________________
 Error: event end must be after its start; correct /from or /to.
____________________________________________________________
____________________________________________________________
 Error: event end must be after its start; correct /from or /to.
____________________________________________________________
____________________________________________________________
 Error: '+1' is not a valid task number; use a positive whole number.
____________________________________________________________
____________________________________________________________
 Pawsome! Task completed:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Here's your task lineup:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
Time for a catnap. See you soon!
____________________________________________________________
```
