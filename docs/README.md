# Jaku User Guide

Jaku is a calm desktop task companion for keeping everyday tasks, dates, events, and recurring commitments in one local plan. It has a graphical chat window for everyday use and a terminal interface for keyboard-first use. Jaku keeps your data on your computer; no account or network connection is required.

![Jaku's task chat window](Ui.png)

## Contents

- [Quick start](#quick-start)
- [How Jaku works](#how-jaku-works)
- [Features](#features)
  - [Adding tasks](#adding-tasks)
  - [Viewing and finding tasks](#viewing-and-finding-tasks)
  - [Completing, restoring, and deleting tasks](#completing-restoring-and-deleting-tasks)
  - [Recurring tasks](#recurring-tasks)
  - [Leaving Jaku](#leaving-jaku)
- [Input help and common mistakes](#input-help-and-common-mistakes)
- [Saved tasks and recovery mode](#saved-tasks-and-recovery-mode)
- [Building the standalone JAR](#building-the-standalone-jar)

## Quick start

### Requirements

Jaku requires **Java 25**. On macOS with SDKMAN, select the course-supported JavaFX distribution before running Jaku:

```sh
sdk use java 25.0.3.fx-zulu
```

### Start the graphical app

From the project folder, run:

```sh
./gradlew run
```

The Jaku window opens with a welcome message and a command field. Type a command, then press **Enter** or click **Send**. These actions always submit the same command. Jaku places your command on the right and its answer on the left; validation and recovery problems appear in a distinct error card.

Start with one of these commands:

```text
todo read chapter 4
deadline submit tutorial /by 2026-10-02
list
```

### Use the terminal interface

The terminal interface supports the same commands and responses. It is useful when you prefer a plain command prompt or are testing Jaku:

```sh
./gradlew classes
java -cp build/classes/java/main jaku.Jaku
```

## How Jaku works

Jaku remembers a numbered list of tasks. Add a task first, then use the number shown by `list` with commands such as `mark`, `unmark`, and `delete`.

Commands are **case-insensitive**, so `TODO buy milk` and `todo buy milk` work the same way. Leading and trailing spaces are ignored. Parameter markers, such as `/by` and `/from`, must be separate words exactly as shown in the examples.

Jaku supports four task categories:

| Category | What it represents | Example display |
| --- | --- | --- |
| Todo | A task without a date | `[T][ ] read chapter 4` |
| Deadline | A task due on a date | `[D][ ] submit tutorial (by: Oct 2 2026)` |
| Event | An appointment with free-form timing text | `[E][ ] team sync (from: Fri 14:00 to: Fri 15:00)` |
| Recurring task | A daily or weekly todo/event whose next occurrence moves when completed | `[R][ ] review notes (every: daily, next: Oct 1 2026)` |

`[ ]` means the task is incomplete and `[X]` means it is complete. Jaku saves every successful change automatically.

## Features

### Adding tasks

#### Add a todo: `todo DESCRIPTION`

Use a todo for something that needs doing but has no date attached.

```text
todo buy printer paper
```

Jaku confirms the new task and tells you how many tasks are in the list. A description is required.

#### Add a deadline: `deadline DESCRIPTION /by yyyy-MM-dd`

Use a deadline for a task due on a particular calendar date. The date must use the ISO format `yyyy-MM-dd`.

```text
deadline submit reflection /by 2026-10-02
```

The task appears as a deadline and Jaku formats the date for readable display. Dates such as `02/10/2026` or `2 Oct` are not accepted here because Jaku needs an unambiguous date.

#### Add an event: `event DESCRIPTION /from START /to END`

Use an event for an appointment or period of time. Ordinary events preserve your start and end text, so you can write whatever is useful to you.

```text
event design review /from Friday 2pm /to Friday 3pm
event orientation week /from 4/10/2026 /to 11/10/2026
```

Both `/from` and `/to` are required, `/from` must come before `/to`, and each marker may appear only once. For a repeating event that Jaku can automatically move, use [recurring events](#add-a-recurring-event-repeat-event-description-from-yyyy-mm-dd-hhmm-to-yyyy-mm-dd-hhmm-every-dailyweekly) instead.

### Viewing and finding tasks

#### List every task: `list`

Use `list` whenever you need the current numbered plan.

```text
list
```

Jaku shows every task in the order you added it. If there are no tasks yet, it says that the list is empty.

#### Find tasks: `find KEYWORD`

Use `find` to narrow the list to descriptions containing a word or phrase. Matching ignores letter case.

```text
find meeting
find REVIEW
```

`find meeting` can match both `team meeting` and `Meeting preparation`. Jaku keeps matching tasks in their original order and numbers the matching results from one for easy reading. Run `list` before using `mark`, `unmark`, or `delete`: those commands always use the current task number in the full list.

### Completing, restoring, and deleting tasks

#### Complete a task: `mark NUMBER`

Use the number displayed by `list`. `find` is for locating descriptions; run `list` when you need a task number for a change.

```text
mark 2
```

For ordinary todos, deadlines, and events, Jaku changes `[ ]` to `[X]`. If you mark an already completed ordinary task, it remains completed.

For recurring tasks, `mark` completes the current occurrence and advances its next date/time by exactly one daily or weekly interval. The recurring task remains incomplete because the next occurrence is ready to do.

#### Restore a task: `unmark NUMBER`

```text
unmark 2
```

For ordinary tasks, this changes `[X]` back to `[ ]`. For recurring tasks, it moves the occurrence back by exactly one interval. This is helpful if you marked a repeating task by mistake.

#### Delete a task: `delete NUMBER`

```text
delete 3
```

Jaku removes the selected task permanently from its local task list and saves the new list. Use `list` afterwards if you need the updated numbering.

### Recurring tasks

Recurring tasks are for commitments that return every day or every week. Jaku supports the cadence words `daily` and `weekly` only. Its task list marks recurring items with `[R]` and shows their next date or time.

#### Add a recurring todo: `repeat todo DESCRIPTION /from yyyy-MM-dd /every daily|weekly`

```text
repeat todo review notes /from 2026-10-01 /every daily
repeat todo water plants /from 2026-10-03 /every weekly
```

`/from` is the date of the first or current occurrence. Completing a daily task advances its date by one day; completing a weekly task advances it by seven days.

#### Add a recurring event: `repeat event DESCRIPTION /from yyyy-MM-dd HH:mm /to yyyy-MM-dd HH:mm /every daily|weekly`

```text
repeat event project meeting /from 2026-10-01 14:00 /to 2026-10-01 15:00 /every weekly
repeat event stand-up /from 2026-10-01 09:30 /to 2026-10-01 09:45 /every daily
```

Recurring events use local date-times in the precise format `yyyy-MM-dd HH:mm`. Their `/to` time must be after their `/from` time, and the markers must appear once each in the order `/from`, `/to`, `/every`.

#### Complete or restore a recurring occurrence

```text
mark 4
unmark 4
```

`mark 4` moves recurring task 4 forward one interval. `unmark 4` moves it backward one interval. Jaku does not skip missed occurrences automatically; each `mark` moves forward once, giving you complete control over the schedule.

### Leaving Jaku

#### Exit: `bye`

```text
bye
```

Jaku shows a farewell. In the graphical app, the window then closes.

## Input help and common mistakes

Jaku protects the current task list by validating commands before it changes anything. Error cards are factual and include the expected command format. Correct the command and send it again; previously saved tasks remain unchanged.

| Situation | What to do |
| --- | --- |
| Empty todo description | Provide text after `todo`, for example `todo buy milk`. |
| Invalid deadline date | Use `yyyy-MM-dd`, for example `deadline pay bill /by 2026-10-02`. |
| Missing, repeated, or misplaced markers | Include each required marker once and in the shown order. |
| Invalid recurring cadence | Use exactly `daily` or `weekly`. |
| Recurring event ends first | Make `/to` later than `/from`. |
| Invalid task number | Run `list`, then use a whole number that appears in the list. |
| Unknown command | Use one of `todo`, `deadline`, `event`, `repeat`, `list`, `mark`, `unmark`, `delete`, `find`, or `bye`. |

For example, this is invalid because `monthly` is not a supported cadence:

```text
repeat todo review notes /from 2026-10-01 /every monthly
```

Use this instead:

```text
repeat todo review notes /from 2026-10-01 /every weekly
```

## Saved tasks and recovery mode

Jaku stores tasks in `data/jaku.txt` by default, relative to the directory from which you launch it. Each successful add, mark, unmark, or delete is saved automatically, so a later restart restores the list.

To use a different storage file when launching the JAR, set the `jaku.dataFile` system property:

```sh
java -Djaku.dataFile=/path/to/tasks.txt -jar build/libs/jaku.jar
```

### Missing data file

If the file does not exist, Jaku starts with an empty list. The file is created after your first successful task change.

### Unreadable or malformed data file

If Jaku cannot safely read an existing saved file, it enters **recovery mode**. Jaku displays a startup warning, leaves the original file untouched, and blocks every task-changing command. This prevents a damaged file from being silently overwritten by an empty list.

To recover:

1. Close Jaku.
2. Repair or replace the affected saved-task file using a backup or a text editor.
3. Restart Jaku.

Once Jaku can read the file, it loads the saved tasks normally and allows changes again.

## Building the standalone JAR

Create the runnable, cross-platform JAR with:

```sh
./gradlew clean shadowJar
```

The output is `build/libs/jaku.jar`. Copy that file to a new directory and run:

```sh
java -jar jaku.jar
```

Jaku creates its default `data/jaku.txt` folder beside the directory from which you run it. This makes it easy to keep a separate task list for a test or a different project.
