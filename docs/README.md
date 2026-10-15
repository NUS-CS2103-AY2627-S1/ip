# ChudGPT User Guide

ChudGPT manages ToDos, deadlines, and events with completion status and optional priorities.

## Quick start

Install Java 25 and run commands from the repository root. Running from the repository root also ensures that the
task data is stored in this repository's `data/tasks.txt` file.

Start the JavaFX interface on Windows:

```powershell
.\gradlew.bat run
```

On macOS or Linux, use:

```bash
./gradlew run
```

To use the console interface instead, compile the classes first and then start `chudgpt.ChudGpt`:

```powershell
# Windows PowerShell
.\gradlew.bat classes
java -cp "build\classes\java\main;build\resources\main" chudgpt.ChudGpt
```

```bash
# macOS or Linux
./gradlew classes
java -cp "build/classes/java/main:build/resources/main" chudgpt.ChudGpt
```

To build a self-contained GUI JAR, run `./gradlew shadowJar` (`.\gradlew.bat shadowJar` on Windows), then launch
`build/libs/chudGPT.jar` with `java -jar build/libs/chudGPT.jar`.

All relative paths are resolved from the directory in which ChudGPT is started. If it is started elsewhere, it
will use a different `<working directory>/data/tasks.txt` file.

## Interfaces and task notation

Both interfaces accept the same commands. The responsive GUI uses compact, right-aligned command bubbles and wider
left-aligned response cards so long replies have more room. Error responses use a high-contrast red card, while task
creation confirmations use a green success card. Large circular profile pictures clearly identify each participant.
Priority labels are color coded in GUI responses: purple for EXTREME, red for HIGH, amber for
MEDIUM, green for LOW, and muted gray for NONE. The GUI welcome card omits the console's text-art logo and divider
lines. Press Enter or click **Send** to submit a GUI command; the input is then cleared and focused for the next
command. The window can be resized down to its minimum dimensions.

Tasks use the following compact notation:

| Token | Meaning |
| --- | --- |
| `[T]` | ToDo |
| `[D]` | Deadline |
| `[E]` | Event |
| `[X]` | Completed |
| `[ ]` | Incomplete |
| `[P:HIGH   ]` | Priority; the name is padded to seven characters |

In the console, `bye` saves and ends the process. In the GUI, it saves and disables the input field and **Send**
button, but leaves the window open for you to close manually. If saving fails, neither interface exits or disables
its controls.

The app loads `data/tasks.txt` relative to its working directory. A missing file means an empty task list.
The folder and file are created when saving. There is no fixed 100-task limit.

## Commands and input

Commands and priority keywords are case-insensitive. Surrounding command whitespace is ignored. ChudGPT lowercases
the entire command before parsing it, so capitalization in a newly entered description is not preserved: `Buy Milk`
is stored and displayed as `buy milk`. Internal description whitespace is preserved for display, although repeated
whitespace is ignored when checking for duplicates. Dates use `yyyy-mm-dd`, such as `2026-10-01`;
natural-language dates and times such as `Sunday` and `Mon 2pm` are not accepted.
Descriptions must contain 1-200 characters and cannot contain `|`, line breaks, or control characters because
those values would corrupt the save-file format.

Enter `/by`, `/from`, and `/to` exactly where shown in the command table. The parser treats those strings as
delimiters even when they occur inside a longer word, so avoid using those slash-prefixed strings in descriptions.
`/priority` is different: it is treated as an option only when it appears as a standalone token.

| Command | Purpose |
| --- | --- |
| `hi` | Display a greeting. |
| `todo <description> [/priority <type>]` | Add a ToDo. |
| `deadline <description> /by <date> [/priority <type>]` | Add a deadline. |
| `event <description> /from <date> /to <date> [/priority <type>]` | Add an event. |
| `priority <task number> <type>` | Set or clear a task's priority. |
| `list` | Display tasks in their underlying order. |
| `list /sort priority` | Display a priority-sorted view. |
| `find <keyword>` | Search descriptions for a case-insensitive substring. |
| `mark <task number>` / `unmark <task number>` | Change completion status. |
| `delete <task number>` | Remove a task. |
| `save` | Save the current list. |
| `bye` | Save and exit. |

Square brackets in syntax tables indicate optional arguments; do not type those brackets.
There is no in-app `help` command; use the table above as the command reference.

Task numbers start at 1 and are positions rather than permanent IDs, so they can change after deletion. Plain `list`
and `list /sort priority` both show the real current task numbers. `find` instead numbers its matches from 1 for
display only; those result numbers are not safe to use with `mark`, `unmark`, `priority`, or `delete`. Run `list` to
obtain the task's current number before editing it.

## Priorities

The priorities, highest first, are `EXTREME`, `HIGH`, `MEDIUM`, `LOW`, and `NONE`.
Omitting `/priority` assigns `NONE`. Only these full keywords are accepted: `HIGH` and `high` work;
`1`, `h`, `med`, and `urgent` do not. Priorities apply equally to completed and incomplete tasks of all types.

Append `/priority <type>` once, at the end of a creation command. For example:

```text
todo read book /priority high
deadline return book /by 2026-10-01 /priority medium
event project meeting /from 2026-10-01 /to 2026-10-02 /priority low
```

The first command on an empty list produces:

```text
Got it. I've added this task:
  [T][ ][P:HIGH   ] read book
Now you have 1 tasks in your list.
```

Every rendered task includes a seven-character, right-padded priority name:

```text
[P:EXTREME]
[P:HIGH   ]
[P:MEDIUM ]
[P:LOW    ]
[P:NONE   ]
```

This fixes the priority field's character width. Different task-number widths and proportional GUI fonts can
still prevent descriptions from lining up visually. Priority commands use the existing GUI text field.

Use `priority 1 extreme` to update task 1:

```text
Got it. I've set this task's priority to EXTREME:
  [T][ ][P:EXTREME] read book
```

Use `priority 1 none` to clear it:

```text
Got it. I've removed the priority from this task:
  [T][ ][P:NONE   ] read book
```

Assigning the existing value again succeeds with the same confirmation and invokes saving again.
These changes preserve the description, dates, and completion status.

## Listing and sorting

`list` preserves underlying task order. `list /sort priority` displays priorities highest first, with equal-priority
tasks in their underlying relative order. Completion status does not affect sorting. For example:

```text
Here are the tasks in your list:
2. [T][X][P:EXTREME] submit report
3. [T][ ][P:HIGH   ] review code
1. [T][ ][P:NONE   ] buy milk
```

The displayed numbers are the current underlying task numbers: `priority 2 low` would change `submit report`.
Sorting does not change the backing list, save-file order, or later plain `list` output, and does not save.
`LIST /SORT PRIORITY` and extra whitespace between the two arguments are accepted.
All other list arguments return `Usage: list or list /sort priority.`

Both list forms on an empty list return:

```text
You have no tasks in your list! Try adding some
```

`find` continues searching descriptions only, not priority metadata. Its task results include priority labels.
A search with no matches returns `I couldn't find any tasks that contains that keyword.` Searching does not change
or save the task list.

## Validation

Missing, duplicated, or misplaced `/priority` clauses reject the entire creation command. Examples:

```text
todo read book /priority
todo read /priority high book
todo read book /priority high /priority low
deadline return book /priority high /by 2026-10-01
```

The missing value returns:

```text
OOPS!!! I've run into an error :( I'm such a chud...
Details:
  The /priority argument requires a priority value.
```

A duplicate clause returns `The /priority argument can only be specified once.` A non-final clause returns
`The /priority argument must be the final argument.`

Only a standalone `/priority` token is reserved. `document/priority` and `/priority-high` remain description text.
A structurally correct clause with an unsupported value, such as `todo book /priority 1` or `priority 1 urgent`, returns:

```text
OOPS!!! I've run into an error :( I'm such a chud...
Details:
  Priority must be one of: EXTREME, HIGH, MEDIUM, LOW, NONE.
```

`priority`, `priority 1`, and `priority 1 high extra` return the detail
`Usage: priority <task number> <priority>.` A non-integer task number produces
`Task number must be a positive whole number.` An unavailable number identifies the task number that does not exist.
Priority-command shape is checked first, then integer syntax, then the keyword, then the task's existence.
Rejected priority commands do not modify or save the list.

Unknown commands produce `Invalid command.`, while blank commands produce `Please enter a command.` All task types
require a nonblank description. Commands validate their complete shape: for example, `hi extra`, `save now`,
`delete`, and `mark 1 2` are rejected with command-specific usage messages instead of silently ignoring arguments.

Event end dates must be later than their start dates; equal or reversed ranges are rejected. Calendar validation
also rejects non-existent dates such as February 30. A newly added task is rejected as a duplicate when an existing
task has the same type, normalized description, and dates. Case and repeated whitespace do not make a duplicate
description unique. Completion status and priority are ignored for duplicate detection.

Task numbers must be positive whole numbers starting from 1. Missing numbers produce the relevant command usage,
values such as `one`, `1.5`, `-1`, and `+1` produce `Task number must be a positive whole number.`, zero produces
`Task numbers start from 1.`, and excessively large numbers produce `Task number is too large.` A valid number that
is not present in the current list produces `Task number <number> does not exist.`

## Completion, deletion, and other responses

`mark 1` and `unmark 1` both render the updated task, including its priority:

```text
Nice! I've marked this task as completed!
  [T][X][P:HIGH   ] read book

OK, I've marked this task as incomplete.
  [T][ ][P:HIGH   ] read book
```

Deleting that task from a one-item list returns:

```text
Got it. Noted. I've removed this task:
  [T][ ][P:HIGH   ] read book
Now you have 0 tasks in your list.
```

`hi` returns `Hi! I'm ChudGPT. How can I help you?`; `save` returns
`I've saved your current list of tasks.`; `bye` returns `Bye. Hope to see you again soon!`.
Response examples omit the welcome banner and the console's divider lines around each response.

## Saving and compatibility

Adding, deleting, marking, unmarking, and changing a priority invoke automatic saving. `save` and `bye` also save.
Saving first writes a temporary file and then replaces the task file atomically when the file system supports it.
If saving fails, the app reports the failure instead of displaying a success confirmation. Changes already made
remain available in the current session so the user can retry. A failed `bye` does not exit the app.

The save file uses these formats; completion is `0` for incomplete and `1` for complete:

```text
T | 0 | HIGH | read book
D | 0 | NONE | return book | 2026-10-01
E | 1 | EXTREME | project meeting | 2026-10-01 | 2026-10-02
```

Writers always include an uppercase priority in the third field. Readers accept only uppercase priority keywords;
new-format records containing lowercase or mixed-case priorities are skipped as malformed.
Legacy records without that field still load, with priority `NONE`:

```text
T | 1 | read book
D | 0 | return book | 2026-10-01
E | 0 | project meeting | 2026-10-01 | 2026-10-02
```

Legacy and new records can coexist. Field count determines the format: `T | 0 | HIGH` is a legacy ToDo whose
description is `HIGH`, not a malformed priority record. Loading alone does not rewrite the file; the next save
(including `bye`) writes every loaded task in the new format. Older app versions need not read the new format.

Records with invalid priorities, completion statuses, task types, field counts, descriptions, dates, event ranges,
or duplicate task details are skipped independently. Valid records before and after a malformed line still load.
The startup warning identifies every skipped line and its reason. Before the recovered list is next saved, the
original file is copied to `tasks.txt.bak`; if that backup cannot be created, saving is aborted. A missing task file
still represents an empty list, while unreadable paths and directories are reported as loading errors.

## Development checks

Use Java 25 and run `./gradlew test checkstyleMain checkstyleTest` (`.\gradlew.bat` on Windows).
Relevant JUnit tests cover parsing, task state, sorting, command side effects, new-format round trips, and migration.

The executable console scenarios are in [`test/ui-test-plan.md`](../test/ui-test-plan.md). Run:

```text
python .codex/skills/test-ui/scripts/run_ui_tests.py
```

The runner follows the project `test-ui` skill's workflow: Java 25, isolated temporary working directories,
full input/output transcripts, exact stdout comparison after line-ending normalization, and stopping at the first
failure. It compiles the console entry point and its dependencies, so JavaFX libraries are not needed for this check.
