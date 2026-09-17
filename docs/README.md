# Sace User Guide

> **Sace** is a desktop task manager with the personality of a moonlit strategist.
> It keeps todos, deadlines, and events in one quest log, saves them automatically,
> and accepts fast keyboard commands.

![Sace showing its battle manual and quest list](Ui.png)

## Quick start

1. Install **Java 25**.
2. Download `sace.jar` from the
   [latest GitHub release](https://github.com/ChesterLZW/ip/releases).
3. Put the JAR in a folder where Sace is allowed to create files.
4. Open a terminal in that folder and run:

   ```text
   java -jar sace.jar
   ```

5. Enter `help` at any time to open the in-app battle manual.

Sace saves your quest log automatically. You do not need a separate save command.

## Command overview

| What you want to do | Command format | Example |
| --- | --- | --- |
| Open the battle manual | `help` | `help` |
| Show every quest | `list` | `list` |
| Add a todo | `todo DESCRIPTION` | `todo read chapter 4` |
| Add a deadline | `deadline DESCRIPTION /by yyyy-MM-dd` | `deadline submit tutorial /by 2026-09-30` |
| Add an event | `event DESCRIPTION /from START /to END` | `event tutorial /from Monday 2pm /to Monday 4pm` |
| Find matching quests | `find KEYWORD` | `find tutorial` |
| Mark a quest complete | `mark NUMBER` | `mark 2` |
| Mark a quest incomplete | `unmark NUMBER` | `unmark 2` |
| Delete a quest | `delete NUMBER` | `delete 1` |
| End the session | `bye` | `bye` |

Command words and parameter markers are not case-sensitive. Sace also accepts
leading, trailing, and repeated spaces, so `DEADLINE   report /BY 2026-09-30`
works as expected.

## Managing quests

### Add a todo

Use a todo for a task without a fixed date or time.

```text
todo prepare presentation slides
```

Sace adds the todo to the end of the quest log and shows the updated number of
quests.

### Add a deadline

Use a deadline for a task that must be completed by a particular date.

```text
deadline submit project report /by 2026-09-30
```

The date must be a real calendar date in `yyyy-MM-dd` format. For example,
`2026-02-30` is rejected because that date does not exist.

### Add an event

Use an event for an activity with a start and an end.

```text
event CS2103 tutorial /from Monday 2pm /to Monday 4pm
```

The start and end may be written in friendly text, but both are required and
must be different. The `/from` marker must come before `/to`.

### Review the quest log

```text
list
```

Each quest has a type and completion marker:

| Symbol | Meaning |
| --- | --- |
| `[T]` | Todo |
| `[D]` | Deadline |
| `[E]` | Event |
| `[ ]` | Incomplete |
| `[X]` | Complete |

The number shown before each quest is used by `mark`, `unmark`, and `delete`.
Numbers can change after a quest is deleted, so use `list` again if unsure.

### Mark or unmark a quest

```text
mark 2
unmark 2
```

`mark` records a quest as complete. `unmark` returns it to the active plan.

### Delete a quest

```text
delete 1
```

Sace shows the removed quest and the number of quests that remain.

### Find quests

```text
find tutorial
```

Search is case-insensitive and checks task descriptions. Matching quests are
shown in their original order. If there are no matches, Sace tells you that no
quests were found.

### End the session

```text
bye
```

Sace displays a farewell and closes the command session. Your quests have
already been saved.

## Saving and recovery

Sace stores its data in `data/sace.txt`, relative to the folder from which the
JAR is launched.

- If the `data` folder or file is missing, Sace starts with an empty quest log
  and creates them when the first quest is saved.
- If the file cannot be read or contains damaged data, Sace displays a warning
  and starts with a fresh in-memory quest log instead of crashing.
- If a change cannot be saved, Sace explains the problem and rolls back that
  change so the displayed quest log remains consistent with the saved data.

Do not edit `data/sace.txt` manually. If you want a backup, close Sace and copy
the whole `data` folder.

## When a command needs correction

Command errors appear in a highlighted **COMMAND ALERT** bubble. The message
explains what needs to change; correct the command and send it again.

| Situation | What to check |
| --- | --- |
| Sace does not recognize a command | Enter `help` and check the command word. |
| A todo is rejected | Add a description after `todo`. |
| A deadline is rejected | Include one `/by` marker and a valid `yyyy-MM-dd` date. |
| An event is rejected | Include one `/from` and one `/to`, in that order, with different values. |
| A task number is rejected | Run `list`, then use a whole number currently shown in the list. |
| Quests cannot be saved | Move the JAR to a writable folder and try again. |

## Tips

- Press **Enter** to send a command; clicking **SEND** works too.
- The window is resizable, and the conversation automatically follows the
  newest response.
- Use `help` whenever you need a quick command reminder without leaving Sace.

---

Sace is an individual project created for the NUS CS2103/T software engineering
course.
