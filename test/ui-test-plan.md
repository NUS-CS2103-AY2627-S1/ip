# UI Test Plan

This file drives the `test-ui` skill. It records how to build/run the
program, and the list of test cases the skill executes.

Each test case is run as its **own fresh program session** (state does not
carry over between test cases). Every test case ends with `bye` so the
program exits cleanly.

## How to run the program

Classes now live under packages (`will`, `will.task`, `will.command`),
so the build command needs to pick up every `.java` file recursively
instead of a flat `src/main/java/*.java` wildcard.

PowerShell (Windows):
```
javac -d out (Get-ChildItem -Path src\main\java -Filter *.java -Recurse).FullName
java -cp out will.Will
```

macOS/Linux (bash):
```
javac -d out $(find src/main/java -name "*.java")
java -cp out will.Will
```

Feed the "Inputs" of a test case to the program's stdin, one command per
line, and compare stdout against "Expected output" (both trimmed of
trailing whitespace per line). Ignore anything the JVM/launcher prints to
stderr (e.g. `JAVA_TOOL_OPTIONS` notices) — only stdout is checked.

The program saves tasks to `./data/will.txt` (created automatically if
the `data/` folder doesn't exist yet) after every command that changes
the task list, and loads it back at startup. Delete the `data/` folder
before **every** test case, not just the ones that check file content
— since startup now depends on it, a stale file left over from the
previous test case would silently change a later test case's starting
task count.

**Verified requirements (course file-path guidance):**
* The data file path is relative and OS-independent — `DATA_FILE` is
  built with `java.nio.file.Paths.get("data", "will.txt")` rather than
  a hardcoded absolute path or a manually concatenated `/`/`\`, so it
  resolves correctly on Windows, macOS, and Linux.
* A missing `data/` folder or `data/will.txt` file at startup is
  handled, not just deferred: `loadTasks()` checks `Files.exists()`
  and returns an empty list rather than erroring, and `saveTasks()`
  calls `Files.createDirectories()` before writing so the very first
  save on a fresh checkout succeeds. Re-confirmed by manually deleting
  `data/` entirely and running both a `list` (no crash on missing
  file) and a `todo` (folder + file created on demand); TC28 covers
  the fresh-checkout save case as an automated test case.

## Test cases

### TC1 — Greet and exit

**Aim:** The chatbot greets the user on startup and exits cleanly on `bye`.

**Inputs:**
```
bye
```

**Expected output:**
```
    ____________________________________________________________
 __        _____ _     _
 \ \      / /_ _| |   | |
  \ \ /\ / / | || |   | |
   \ V  V /  | || |___| |___
    \_/\_/  |___|_____|_____|

     What's up!!! I'm Will.
     How may I assist you?
    ____________________________________________________________
     Seee yaaaa! Meet again soon!
    ____________________________________________________________
```

### TC2 — List when empty

**Aim:** `list` on a fresh session shows the header with no items.

**Inputs:**
```
list
bye
```

**Expected output (list section only):**
```
     Here are the tasks in your list:
```

### TC3 — Add a Todo

**Aim:** `todo <description>` adds a `[T]` task and reports the new count.

**Inputs:**
```
todo borrow book
bye
```

**Expected output (add confirmation):**
```
     Got it. I've added this task:
       [T][ ] borrow book
     Now you have 1 tasks in the list.
```

### TC4 — Add a Deadline

**Aim:** `deadline <description> /by <when>` adds a `[D]` task with the
`(by: ...)` suffix.

**Inputs:**
```
deadline return book /by Sunday
bye
```

**Expected output (add confirmation):**
```
     Got it. I've added this task:
       [D][ ] return book (by: Sunday)
     Now you have 1 tasks in the list.
```

### TC5 — Add an Event

**Aim:** `event <description> /from <start> /to <end>` adds an `[E]` task
with the `(from: ... to: ...)` suffix.

**Inputs:**
```
event project meeting /from Mon 2pm /to 4pm
bye
```

**Expected output (add confirmation):**
```
     Got it. I've added this task:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     Now you have 1 tasks in the list.
```

### TC6 — List shows all three task types

**Aim:** After adding one of each task type, `list` shows them numbered,
in order, each with its type tag and status icon.

**Inputs:**
```
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

**Expected output (list section only):**
```
     Here are the tasks in your list:
     1.[T][ ] borrow book
     2.[D][ ] return book (by: Sunday)
     3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
```

### TC7 — Mark a task as done

**Aim:** `mark <index>` flips the task's status to done and echoes it back.

**Inputs:**
```
todo borrow book
mark 1
bye
```

**Expected output (mark confirmation):**
```
     Amazing Gangie! I've marked this task as done:
       [T][X] borrow book
```

### TC8 — Unmark a task

**Aim:** `unmark <index>` flips a done task back to not-done and echoes it
back.

**Inputs:**
```
todo borrow book
mark 1
unmark 1
bye
```

**Expected output (unmark confirmation):**
```
     OK, I've marked this task as not done yet:
       [T][ ] borrow book
```

### TC9 — Mark/unmark status persists in list

**Aim:** A task marked done stays done (and shows `[X]`) in a later `list`.

**Inputs:**
```
todo borrow book
todo join sports club
mark 2
list
bye
```

**Expected output (list section only):**
```
     Here are the tasks in your list:
     1.[T][ ] borrow book
     2.[T][X] join sports club
```

### TC10 — Empty todo description

**Aim:** `todo` with no description is rejected with a `WillException`
error message instead of adding a blank task.

**Inputs:**
```
todo
bye
```

**Expected output (error section only):**
```
     OOPS!!! A todo needs a description! Try: todo <what you need to do>
```

### TC11 — Unknown command

**Aim:** An unrecognized command (e.g. `blah`) is rejected with an error
message instead of being silently ignored or crashing.

**Inputs:**
```
blah
bye
```

**Expected output (error section only):**
```
     OOPS!!! I don't recognize that command. Try: todo, deadline, event, list, on, find, mark, unmark, delete, or bye.
```

### TC12 — mark with no task number

**Aim:** `mark` with no argument reports a specific error rather than
throwing an unhandled exception.

**Inputs:**
```
mark
bye
```

**Expected output (error section only):**
```
     OOPS!!! Tell me which task number! Try: mark <task number>
```

### TC13 — mark with an out-of-range task number

**Aim:** `mark <n>` where `n` doesn't correspond to an existing task
reports how many tasks actually exist.

**Inputs:**
```
mark 99
bye
```

**Expected output (error section only):**
```
     OOPS!!! Task number 99 doesn't exist. You have 0 task(s) in your list.
```

### TC14 — mark with a non-numeric task number

**Aim:** `mark <not a number>` is rejected with a specific message.

**Inputs:**
```
mark abc
bye
```

**Expected output (error section only):**
```
     OOPS!!! "abc" isn't a valid task number.
```

### TC15 — Deadline missing /by

**Aim:** `deadline <description>` with no `/by` clause is rejected.

**Inputs:**
```
deadline return book
bye
```

**Expected output (error section only):**
```
     OOPS!!! A deadline needs a description and a /by time! Try: deadline <what you need to do> /by <when it's due>
```

### TC16 — Event missing /to

**Aim:** `event <description> /from <start>` with no `/to` clause is
rejected.

**Inputs:**
```
event meeting /from Mon
bye
```

**Expected output (error section only):**
```
     OOPS!!! An event needs a description, a /from time and a /to time! Try: event <what's happening> /from <start> /to <end>
```

## Edge-case / internal-state tests

The tests below interleave valid commands with invalid ones and re-check
`list` afterwards. Their purpose isn't just "does this command error" —
it's "does a rejected command leave the task list/state exactly as it
was", which a plain per-command test can miss (e.g. a bug that increments
the task counter even when validation fails, or that half-applies a
change before throwing).

### TC17 — A rejected command does not change the task count or list

**Aim:** After a valid `todo`, an unrecognized command must not add a
phantom task or otherwise change what `list` shows.

**Inputs:**
```
todo take out trash
blah
list
bye
```

**Expected output (list section only):**
```
     Here are the tasks in your list:
     1.[T][ ] take out trash
```

### TC18 — mark with task number 0 (just below the valid range)

**Aim:** Task numbers are 1-indexed; `mark 0` must be rejected as
out-of-range, not treated as a valid (off-by-one) index.

**Inputs:**
```
todo take out trash
todo buy milk
mark 0
list
bye
```

**Expected output:**
```
     OOPS!!! Task number 0 doesn't exist. You have 2 task(s) in your list.
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] take out trash
     2.[T][ ] buy milk
```
(Note: both tasks must still show `[ ]` — the rejected `mark 0` must not
have flipped either task's status.)

### TC19 — mark with a negative task number

**Aim:** A negative task number is rejected as out-of-range rather than
wrapping around or crashing.

**Inputs:**
```
todo take out trash
mark -1
list
bye
```

**Expected output:**
```
     OOPS!!! Task number -1 doesn't exist. You have 1 task(s) in your list.
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] take out trash
```

### TC20 — Internal whitespace in a description is preserved

**Aim:** Leading/trailing whitespace around the command is trimmed, but
whitespace the user typed inside the description itself is left alone.

**Inputs:**
```
todo    read   book
list
bye
```

**Expected output (list section only):**
```
     Here are the tasks in your list:
     1.[T][ ] read   book
```

### TC21 — Deadline with /by but an empty description

**Aim:** `deadline /by <when>` (nothing before `/by`) is rejected with the
"needs a description" message, not treated as a deadline with a blank
description.

**Inputs:**
```
deadline /by Monday
list
bye
```

**Expected output:**
```
     OOPS!!! A deadline needs a description before /by! Try: deadline <what you need to do> /by <when it's due>
    ____________________________________________________________
     Here are the tasks in your list:
```
(Note: `list` afterwards must show no tasks — the rejected deadline must
not have been added.)

### TC22 — Event with /from but an empty description

**Aim:** `event /from <start> /to <end>` (nothing before `/from`) is
rejected with the "needs a description" message.

**Inputs:**
```
event /from Mon /to 5pm
list
bye
```

**Expected output:**
```
     OOPS!!! An event needs a description before /from! Try: event <what's happening> /from <start> /to <end>
    ____________________________________________________________
     Here are the tasks in your list:
```

### TC23 — Unmarking an already-not-done task is idempotent

**Aim:** `unmark` on a task that's already not done should not error —
it's a valid (if redundant) request, and the task must remain `[ ]`.

**Inputs:**
```
todo take out trash
unmark 1
unmark 1
list
bye
```

**Expected output (list section only):**
```
     Here are the tasks in your list:
     1.[T][ ] take out trash
```

### TC24 — Delete a task

**Aim:** `delete <index>` removes the task at that (1-indexed) position,
echoes it back, and reports the new count.

**Inputs:**
```
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
delete 3
list
bye
```

**Expected output:**
```
     Noted. I've removed this task:
       [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
     Now you have 2 tasks in the list.
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: June 6th)
```
(Note: the task numbers renumber after the delete — the old "task 2"
`return book` is now "1.", confirming this is a real removal from an
`ArrayList<Task>`, not just blanking a slot.)

### TC25 — delete with no task number

**Aim:** `delete` with no argument reports a specific error.

**Inputs:**
```
delete
bye
```

**Expected output (error section only):**
```
     OOPS!!! Tell me which task number! Try: delete <task number>
```

### TC26 — delete with an out-of-range task number

**Aim:** `delete <n>` where `n` doesn't correspond to an existing task is
rejected and does not remove anything.

**Inputs:**
```
todo read book
delete 99
list
bye
```

**Expected output:**
```
     OOPS!!! Task number 99 doesn't exist. You have 1 task(s) in your list.
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
```

## Persistence tests

### TC27 — Tasks are saved to disk after every change

**Aim:** `./data/will.txt` is created automatically (even if `data/`
doesn't exist yet) and reflects the task list's current state — added
tasks, a marked-done status, and a deletion — after each change.

**Setup:** Delete the `data/` folder (if present) before running this
test case, so it starts from "the folder doesn't exist yet".

**Inputs:**
```
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
mark 1
delete 2
bye
```

**Expected file content of `data/will.txt` after the session ends:**
```
T | 1 | read book
E | 0 | project meeting | Aug 6th 2pm | 4pm
```
(Note: task 1 is `1` for done since it was marked; task 2, "return
book", is gone since it was deleted; the surviving event's line has 5
`|`-delimited fields: type, done flag, description, from, to.)

### TC28 — A fresh checkout with no data folder still saves correctly

**Aim:** The very first save on a machine that has never run the
program before (no `data/` folder exists at all) must succeed rather
than throwing because the parent folder is missing.

**Setup:** Delete the `data/` folder (if present) before running.

**Inputs:**
```
todo first run test
bye
```

**Expected file content of `data/will.txt` after the session ends:**
```
T | 0 | first run test
```

### TC29 — Tasks are loaded from disk at startup

**Aim:** On startup, the program reads `data/will.txt` and populates the
task list from it (preserving the done/not-done status), before any
command is typed.

**Setup:** Delete the `data/` folder, then create `data/will.txt` with
this exact content before starting the program:
```
T | 1 | read book
D | 0 | return book | June 6th
E | 0 | project meeting | Aug 6th 2pm | 4pm
```

**Inputs:**
```
list
bye
```

**Expected output (list section only):**
```
     Here are the tasks in your list:
     1.[T][X] read book
     2.[D][ ] return book (by: June 6th)
     3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
```

### TC30 — Corrupted lines are skipped with a warning, valid ones still load

**Aim:** A data file with some unreadable lines (unknown type tag, too
few fields) doesn't stop the whole load — it reports each bad line and
loads everything else.

**Setup:** Delete the `data/` folder, then create `data/will.txt` with
this exact content before starting the program:
```
T | 0 | valid todo
X | 0 | bad type
D | 1 | valid deadline | June 6th
not even close to valid
```

**Inputs:**
```
list
bye
```

**Expected output:**
```
     OOPS!!! Skipping a corrupted line in the data file: "X" isn't a recognized task type.
     OOPS!!! Skipping a corrupted line in the data file: "not even close to valid" doesn't have enough fields.
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] valid todo
     2.[D][X] valid deadline (by: June 6th)
```

## Robustness / edge-case tests (parsing and data integrity)

### TC31 — Leading whitespace on the command line doesn't break parsing

**Aim:** A line like `"   todo read book"` (leading spaces before the
command) must still be recognized as `todo`, not as an empty/unknown
command. Before this fix, `input.split(" ", 2)[0]` on a string with
leading spaces produced an empty first token.

**Inputs:**
```
   todo read book
list
bye
```

**Expected output (list section only):**
```
     Here are the tasks in your list:
     1.[T][ ] read book
```

### TC32 — Blank lines are silently ignored, not treated as errors

**Aim:** Pressing Enter with nothing typed (or only whitespace) should
not produce an "OOPS!!!" — the program should just wait for the next
line.

**Inputs:**
```

   
todo x
bye
```

**Expected output:**
```
    ____________________________________________________________
 __        _____ _     _
 \ \      / /_ _| |   | |
  \ \ /\ / / | || |   | |
   \ V  V /  | || |___| |___
    \_/\_/  |___|_____|_____|

     What's up!!! I'm Will.
     How may I assist you?
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] x
     Now you have 1 tasks in the list.
    ____________________________________________________________
     Seee yaaaa! Meet again soon!
    ____________________________________________________________
```
(Note: no `OOPS!!!` line appears for the two blank inputs before `todo x`.)

### TC33 — Commands are case-insensitive

**Aim:** `TODO`, `LiSt`, `BYE`, etc. should all work the same as their
lowercase form — only the command word's case is normalized, not the
description text itself.

**Inputs:**
```
TODO read book
LiSt
BYE
```

**Expected output (list section only):**
```
     Here are the tasks in your list:
     1.[T][ ] read book
```

### TC34 — Event with /to before /from is rejected, not a crash

**Aim:** Before this fix, `/to` appearing before `/from` in the input
caused a `StringIndexOutOfBoundsException` (an unhandled crash) because
the code assumed `/from` always comes first when slicing substrings.
Now it must be caught and reported as a normal `WillException`.

**Inputs:**
```
event meeting /to 5pm /from 3pm
bye
```

**Expected output (error section only):**
```
     OOPS!!! Your /from time needs to come before /to! Try: event <what's happening> /from <start> /to <end>
```

### TC35 — A "|" character in a description is rejected, not silently corrupted

**Aim:** The save file format is pipe-delimited (`T | 0 | description`).
Before this fix, a description containing `|` would be written to disk
in a way that misparses into extra fields on the next load. It must
now be rejected up front with a clear error instead.

**Inputs:**
```
todo buy milk | bread
bye
```

**Expected output (error section only):**
```
     OOPS!!! Sorry, the description can't contain a "|" character — try rephrasing without it.
```

## Dates and times (Level 8)

Deadline/event dates are stored as a `java.time.LocalDate` when the
`/by`, `/from`, or `/to` value matches `yyyy-MM-dd` (e.g. `2019-10-15`),
and displayed as `MMM dd yyyy` (e.g. `Oct 15 2019`). A value that
doesn't match that format (e.g. `Sunday`, `no idea :-p`) is kept as
plain text and displayed unchanged — this is a deliberate fallback, not
a bug, so free-text dates from earlier test cases keep working.

### TC36 — A yyyy-MM-dd deadline date is parsed and reformatted

**Aim:** `deadline <description> /by 2019-10-15` is understood as an
actual date and displayed as `Oct 15 2019`, not echoed back verbatim.

**Inputs:**
```
deadline return book /by 2019-10-15
list
bye
```

**Expected output (list section only):**
```
     Here are the tasks in your list:
     1.[D][ ] return book (by: Oct 15 2019)
```

### TC37 — yyyy-MM-dd event dates are parsed and reformatted (both /from and /to)

**Aim:** Both the `/from` and `/to` values of an event are independently
checked against `yyyy-MM-dd` and reformatted if they match.

**Inputs:**
```
event project meeting /from 2019-08-06 /to 2019-08-07
list
bye
```

**Expected output (list section only):**
```
     Here are the tasks in your list:
     1.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
```

### TC38 — Free-text dates still work (no regression from the date feature)

**Aim:** A `/by`/`/from`/`/to` value that isn't in `yyyy-MM-dd` format
is kept and displayed as plain text, exactly like before dates were
added — this must NOT become an error.

**Inputs:**
```
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
list
bye
```

**Expected output (list section only):**
```
     Here are the tasks in your list:
     1.[D][ ] return book (by: June 6th)
     2.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
```

### TC39 — A saved LocalDate round-trips correctly through save/load

**Aim:** A deadline saved with a recognized date is written to disk in
`yyyy-MM-dd` form (not the display form) and, when reloaded, is still
recognized as a date and displayed the same way — not permanently
downgraded to plain text after a restart.

**Setup:** Delete the `data/` folder before running.

**Inputs (first session):**
```
deadline return book /by 2019-10-15
bye
```

**Expected file content of `data/will.txt` after the first session:**
```
D | 0 | return book | 2019-10-15
```

**Inputs (second session, same data file):**
```
list
bye
```

**Expected output (list section only) of the second session:**
```
     Here are the tasks in your list:
     1.[D][ ] return book (by: Oct 15 2019)
```

## Querying by date (Level 8 stretch goal)

The `on <yyyy-MM-dd>` command prints every deadline/event occurring on
that date: a deadline matches if its `/by` equals the queried date; an
event matches if the queried date falls within `[from, to]` (or equals
whichever single end is a recognized date, if only one is). A `todo`
never matches, since it has no date. Matches are renumbered 1, 2, ...
within the filtered results, not by their original position in `list`.

### TC40 — `on` finds a matching deadline and an overlapping event

**Aim:** A deadline due exactly on the queried date, and an event whose
`[from, to]` range spans it, both show up; an unrelated todo and a
deadline on a different date do not.

**Inputs:**
```
deadline return book /by 2019-10-15
event project meeting /from 2019-10-14 /to 2019-10-16
todo unrelated todo
deadline other thing /by 2019-11-01
on 2019-10-15
bye
```

**Expected output (on-command section only):**
```
     Here are the tasks occurring on Oct 15 2019:
     1.[D][ ] return book (by: Oct 15 2019)
     2.[E][ ] project meeting (from: Oct 14 2019 to: Oct 16 2019)
```

### TC41 — `on` with no matches prints an empty (but not erroring) list

**Aim:** Querying a date nothing occurs on shows the header with zero
results, not an error.

**Inputs:**
```
deadline return book /by 2019-10-15
on 2019-01-01
bye
```

**Expected output (on-command section only):**
```
     Here are the tasks occurring on Jan 01 2019:
    ____________________________________________________________
     Seee yaaaa! Meet again soon!
```

### TC42 — `on` with an invalid date format is rejected

**Aim:** A value that isn't `yyyy-MM-dd` is reported as a clear error,
not a crash.

**Inputs:**
```
on not-a-date
bye
```

**Expected output (error section only):**
```
     OOPS!!! "not-a-date" isn't a date in yyyy-MM-dd format. Try: on <yyyy-MM-dd>, e.g. on 2019-10-15
```

### TC43 — `on` with no argument is rejected

**Aim:** `on` alone (no date) reports a specific error.

**Inputs:**
```
on
bye
```

**Expected output (error section only):**
```
     OOPS!!! Tell me which date! Try: on <yyyy-MM-dd>, e.g. on 2019-10-15
```

## Finding tasks by keyword (Level 9)

The `find <keyword>` command lists every task whose description
contains the given keyword, case-insensitively. Matches are
renumbered 1, 2, ... within the filtered results, not by their
original position in `list`. The task type and done status have no
bearing on matching — only the description text is searched.

### TC44 — `find` matches a keyword across multiple task types

**Aim:** A keyword appearing in a todo's, deadline's, and event's
description all show up; an unrelated task does not.

**Inputs:**
```
todo read book
deadline return book /by 2019-10-15
event book club meeting /from 2019-10-20 /to 2019-10-20
todo buy groceries
find book
bye
```

**Expected output (find-command section only):**
```
     Here are the matching tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: Oct 15 2019)
     3.[E][ ] book club meeting (from: Oct 20 2019 to: Oct 20 2019)
```

### TC45 — `find` is case-insensitive

**Aim:** A keyword typed in a different case from the stored
description still matches.

**Inputs:**
```
todo read Book
find BOOK
bye
```

**Expected output (find-command section only):**
```
     Here are the matching tasks in your list:
     1.[T][ ] read Book
```

### TC46 — `find` with no matches prints an empty (but not erroring) list

**Aim:** Searching for a keyword nothing matches shows the header with
zero results, not an error.

**Inputs:**
```
todo read book
find xyz
bye
```

**Expected output (find-command section only):**
```
     Here are the matching tasks in your list:
    ____________________________________________________________
     Seee yaaaa! Meet again soon!
```

### TC47 — `find` with no keyword is rejected

**Aim:** `find` alone (no keyword) reports a specific error.

**Inputs:**
```
find
bye
```

**Expected output (error section only):**
```
     OOPS!!! Tell me what to search for! Try: find <keyword>
```

## A-MoreOOP refactor verification

Extracting `Ui`, `Storage`, `Parser`, and `TaskList` out of `Will` is a pure
refactor: it must not change anything a user can observe on stdin/stdout. No
new test cases are added for a refactor increment — instead, the full
existing suite above (TC1–TC43) is re-run after each increment and must
still pass unchanged. If an increment changes what any of these test cases
expects, that is a regression, not an intentional update.

- **Increment 1 (extract `Ui`)** — moved the startup banner/greeting, the
  goodbye message, and the `showLine`/`showMessage` framing helpers out of
  `Will` into a new `Ui` class. Re-ran TC1–TC43: 42/42 automated cases
  passed (TC39 remains the one manually-verified case, unaffected by this
  change since it doesn't touch save/load or dates).
- **Increment 2 (extract `Storage`)** — moved `loadTasks`/`saveTasks`/
  `parseSavedTask` and the save-file path out of `Will` into a new
  `Storage` class (`load(ui)` / `save(tasks)`). Re-ran TC1–TC43: 42/42
  automated cases passed, including the save/load cases (TC27–TC30) and
  the manually-verified round-trip case (TC39).
- **Increment 3 (extract `TaskList`)** — wrapped the `ArrayList<Task>`
  in a new `TaskList` class (`add`/`remove`/`get`/`size`, plus
  `Iterable<Task>` so the existing `list`/`on <date>` for-each loops
  keep working unchanged). `Will` now holds a `TaskList` instead of a
  raw `ArrayList<Task>`. Re-ran TC1–TC43: 42/42 automated cases passed.
- **Increment 4 (Command hierarchy + `Parser`, stretch goal)** —
  introduced an abstract `Command` class and one concrete subclass per
  command (`ExitCommand`, `ListCommand`, `OnCommand`, `MarkCommand`,
  `UnmarkCommand`, `DeleteCommand`, `AddCommand`), rewrote `Parser` to
  turn raw input into a `Command` instead of executing it inline, and
  rewrote `Will` around a `Ui`/`Storage`/`TaskList` constructor plus a
  `run()` loop that just does `Parser.parse(...)` →
  `command.execute(...)` → `isExit()`. First pass (loading in the
  constructor before printing the welcome banner) failed TC30 by
  printing a corrupted-line warning before the banner instead of after
  it — caught immediately by this suite and fixed by greeting first,
  then loading. Re-ran TC1–TC43 after the fix: 42/42 automated cases
  passed, with zero output changes from before this increment.

## A-Packages verification

Organizing classes into packages is a pure move: no class's logic
changes, only its `package` declaration and the imports needed to
reach classes now in a different package. As with A-MoreOOP, no new
test cases are added — the existing suite is re-run and must still
pass unchanged.

- **`will`** (root) — `Will`, `Ui`, `Storage`, `Parser`, `TaskList`,
  `WillException`: the "wiring" classes, each talking to several of
  the others.
- **`will.task`** — `Task`, `TaskType`, `Todo`, `Deadline`, `Event`,
  `FlexibleDate`: what a task is.
- **`will.command`** — `Command`, `ExitCommand`, `ListCommand`,
  `OnCommand`, `MarkCommand`, `UnmarkCommand`, `DeleteCommand`,
  `AddCommand`: what a command does.

Re-ran TC1–TC43 after the move (and after updating the build/run
commands above for the new package layout): 42/42 automated cases
passed, with zero output changes.

## A-JUnit verification

Added JUnit 5 tests under `src/test/java`, mirroring the package path
of the class under test (e.g. `will.task.FlexibleDate` is tested by
`will.task.FlexibleDateTest` at
`src/test/java/will/task/FlexibleDateTest.java`), following the
`featureUnderTest_testScenario_expectedBehavior()` naming convention.

Minimal target was two non-trivial methods from two different
classes. Chosen instead to cover two classes thoroughly, since both
are pure logic with no side effects and are easy to test well:

- **`FlexibleDateTest`** (9 tests) — `toDisplayString()`,
  `toSaveString()`, `getDate()`, `parseExact(String)`, and
  `formatForDisplay(LocalDate)`, each covering both the
  recognized-`yyyy-MM-dd` path and the free-text fallback path.
- **`TaskTest`** (8 tests, via `Todo`) — `toString()`,
  `toSaveFormat()`, `occursOn(LocalDate)`, `getDescription()`, and
  `getType()`, including the `markAsDone()`/`markAsNotDone()`
  transitions.
- **`DeadlineTest`** (6 tests) — the `Deadline` overrides of
  `toString()`, `toSaveFormat()`, and `occursOn(LocalDate)`, covering
  both the recognized-date and free-text `by` field, and both the
  matching and non-matching `occursOn` cases.

23 tests total, run via `.\gradlew.bat test` (Windows) or
`./gradlew test` (macOS/Linux). The manual black-box suite above
(TC1–TC43) is unaffected and continues to be run separately, since it
exercises the CLI end-to-end rather than individual methods.

## A-Jar verification

Packaging as a fat JAR needed no build.gradle changes: the shadowJar
plugin, application plugin, and shadowJar { archiveFileName = 'will.jar' }
were already wired up back in A-Gradle. /build/ is already in
.gitignore, so the generated JAR is never committed (per the course's
"do not commit the JAR" rule) — it is meant to be distributed via a
GitHub release instead.

- **Create the JAR**: `.\gradlew.bat shadowJar` (Windows) or
  `./gradlew shadowJar` (macOS/Linux).
- **Locate it**: `build\libs\will.jar`.
- **Run it** (per the course's assumed usage — copy into an empty
  folder, open a command window there, run from that folder):
  `java -jar "will.jar"`.

## A-JavaDoc verification

Added header (Javadoc) comments to all non-private classes and
methods, plus the non-trivial private helper methods in Parser
(stretch goal). Straightforward `@Override` implementations whose
behavior is already described by the class-level Javadoc or by the
abstract method they implement (e.g. each Command subclass's
`execute()`) were left to inherit that documentation rather than
repeating it, per the SE-EDU convention's own exception for overrides.

No behavior changes — comments only, plus one import-order fix in
OnCommand (found during the same pass) — so the manual UI suite
(TC1–TC43) and the JUnit suite are both unaffected. Compiled cleanly
in a sandbox JDK to confirm no syntax errors from the additions.
