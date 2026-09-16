# Sumo User Guide

Sumo is a professional JavaFX chatbot that helps you keep assignments, errands, and appointments in one place. <br>
As its name implies, Sumo does the heavy lifting of keeping track of all your tasks and saves them automatically.

<img src="Ui.png" alt="Sumo chat window showing a task list and a chronological view" width="500">

[Getting started](#getting-started) · [Features](#features) ·
[Saving your tasks](#saving-your-tasks) · [Troubleshooting](#troubleshooting)

## Getting started

1. Install **Java 25**. Check your version by running `java -version` in a terminal
   (PowerShell on Windows, or Terminal on macOS/Linux).
2. Download **Sumo.jar** from the **Assets** section of a
   [Sumo release](https://github.com/Kenneth-Chia/ip/releases).
3. Put it in a folder where you want to keep your tasks. Open a terminal in that folder and run:

   ```shell
   java -jar Sumo.jar
   ```

   Use your downloaded filename if it differs. You don't need IntelliJ or Gradle.
4. In the **Sumo window**, type `todo read a book` and press **Enter** or click **Send**.
   Try `list` to see your tasks, then `mark 1` to complete the first one.

Launch from the same folder each time to reload your saved tasks.

## Features

### Command basics

- Use lowercase command names and enter one command at a time. `list`, `sort`, and `bye`
  take no extra arguments.
- Replace uppercase words below with your own values. `[HHmm]` means an optional time;
  don't type the brackets.
- Use real dates in `yyyy-MM-dd` or `d/M/yyyy` format, such as `2026-09-20` or `20/9/2026`.
  Times use four-digit, 24-hour format: `0915` is 9:15 am; no time means midnight.
- Descriptions must not be blank or contain `|`, line breaks, or control characters.
  Extra spaces are reduced to single spaces.

### Add a todo: `todo`

For a task without a date. Sumo confirms the addition and shows your new task count.

**Format:** `todo DESCRIPTION`

**Example:** `todo read a book`

### Add a deadline: `deadline`

For a task due on a date, optionally at a specific time.

**Format:** `deadline DESCRIPTION /by DATE [HHmm]`

**Example:** `deadline submit report /by 2026-09-20 1700` — due on 20 September at 5 pm.

### Add an event: `event`

For an activity with a start and end.

**Format:** `event DESCRIPTION /from DATE [HHmm] /to DATE [HHmm]`

**Example:** `event meeting /from 2026-09-20 1400 /to 2026-09-20 1600` — 2 pm to 4 pm.

The end must be after the start. For an event spanning days, you can omit times:
`event camp /from 2026-09-22 /to 2026-09-24`.

For deadlines and events, use `/by`, or `/from` and `/to`, exactly once in the order shown.
Avoid other words starting with `/` in these commands.

**Task already exists?** Tasks with the same type, description, and dates/times count as duplicates,
even with different capitalization, spacing, or completion status. An omitted time equals `0000`.
Use different dates or descriptions for recurring tasks.

### View all tasks: `list`

**Format:** `list`

Shows all tasks in the order you added them, including completed tasks:

```text
1.[T][ ] read a book
2.[D][X] submit report (by: Sep 20 2026 5:00 PM)
```

`[T]` = todo, `[D]` = deadline, `[E]` = event; `[ ]` = incomplete, `[X]` = complete.

### Complete or reopen a task: `mark` / `unmark`

**Formats:** `mark NUMBER` / `unmark NUMBER`

**Examples:** `mark 1` completes the first task; `unmark 1` makes it incomplete again.

> **Use the number from `list`** for `mark`, `unmark`, and `delete`.
> Search and sorted results use separate numbering. Run `list` before changing a task.
> `NUMBER` must be a positive whole number belonging to a task in that list.

### Delete a task: `delete`

**Format:** `delete NUMBER`

**Example:** `delete 1` removes the first task and shows the remaining count.

There is no undo command. Later task numbers shift up, so run `list` before your next change.

### Find tasks: `find`

**Format:** `find TEXT`

**Example:** `find book` matches both `read a book` and `buy books`.

Searches descriptions, including completed tasks. Matching is case-sensitive (`Book` differs from
`book`); multiple words match as one phrase. No matches? You'll see just the results heading.

### Check a date: `on`

**Format:** `on DATE`

**Example:** `on 2026-09-20`

Shows deadlines due that day and events covering it, including their start and end dates.
Completed tasks are included; todos are excluded. No matches? You'll see just the results heading.

### View tasks chronologically: `sort`

**Format:** `sort`

Shows incomplete tasks first, then completed tasks. Within each group, dated tasks appear earliest
first (deadline due date or event end date), followed by todos. Events with the same end are ordered
by start; other ties keep their original order.

This is a temporary view: saved order and task numbers stay the same. Use `list` for task numbers.

### End your session: `bye`

**Format:** `bye`

Ends the chat and disables input. Close the window; reopen Sumo when you're ready to continue.

The window closes automatically five seconds after `bye`.

## Saving your tasks

Every successful addition, deletion, or status change is saved to `data/sumo.txt` in the folder
where you launched Sumo. The file is created on your first change. No save command is needed.

To back up your tasks, close Sumo and copy `data/sumo.txt` somewhere safe. When moving Sumo,
bring the `data` folder too.

## Troubleshooting

| Problem | What to do |
| --- | --- |
| `java` isn't recognised, or Java is too old | Install Java 25, reopen your terminal, and check `java -version`. |
| `Unable to access jarfile` | Open the terminal in the download folder and use the exact JAR filename. |
| Tasks seem to be missing | Launch from your usual folder and check that its `data/sumo.txt` exists. |
| Command rejected | Follow Sumo's error message; check the format above and use task numbers from `list`. |
| Saving failed | The change is rolled back. Check that the data folder is writable. If the file changed outside Sumo, restart to reload it. |
| Saved-data warning at startup | Saving is disabled to protect your file; valid tasks remain viewable when possible. Back up `data/sumo.txt`, repair the reported lines or file permissions, then restart. |
