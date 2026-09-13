# Nelson User Guide

Nelson is a chess-themed task assistant that stores ToDos, deadlines, and
events. It remembers your tasks between runs and responds with the personality
of a rather confident chess opponent.

![Nelson user interface](Ui.png)

## Quick start

1. Install Java 25.
2. Download `nelson-all.jar` from the latest GitHub release.
3. Put the JAR in an empty folder so Nelson's data stays with the application.
4. Open a terminal in that folder and run `java -jar nelson-all.jar`.
5. Enter one command at a time in the command box and press **Enter** or
   **Send**.

Nelson saves tasks automatically in `data/nelson.txt`, relative to the folder
where it is launched. Dates must use `yyyy-MM-dd`, such as `2026-09-10`.

## Command summary

| Action | Command |
| --- | --- |
| Add a ToDo | `todo DESCRIPTION` |
| Add a deadline | `deadline DESCRIPTION /by DATE` |
| Add an event | `event DESCRIPTION /from START_DATE /to END_DATE` |
| View all tasks | `list` |
| Find tasks | `find KEYWORD` |
| Sort tasks | `sort` |
| Mark a task complete | `mark NUMBER` |
| Mark a task incomplete | `unmark NUMBER` |
| Delete a task | `delete NUMBER` |
| Exit Nelson | `bye` |

Commands are lowercase and may have extra spaces before or after them.

## Adding tasks

### ToDos

Use `todo DESCRIPTION` for a task without a date.

```text
todo study chess openings
```

### Deadlines

Use `deadline DESCRIPTION /by DATE` for a task due on a specific date.

```text
deadline submit report /by 2026-09-10
```

Use `/by` exactly once. Nelson rejects missing descriptions, missing dates,
duplicate `/by` parameters, and dates that do not exist.

### Events

Use `event DESCRIPTION /from START_DATE /to END_DATE` for an event with a
start and end date.

```text
event project meeting /from 2026-09-08 /to 2026-09-09
```

Use `/from` and `/to` exactly once and in that order. The start date must be
earlier than the end date. Nelson rejects missing or duplicate parameters,
invalid dates, and equal or reversed date ranges.

## Viewing and finding tasks

Use `list` to display every task and its number. Use `find KEYWORD` to display
tasks whose descriptions contain the keyword. Matching ignores letter case
and preserves list order.

```text
find book
```

Task numbers shown by `find` refer to positions in the full task list.

## Sorting tasks

Use `sort` to arrange all tasks alphabetically by description. Sorting ignores
letter case, preserves the previous order of equal descriptions, displays the
new order, and saves it for the next run.

## Updating task status

Use `mark NUMBER` to mark a task complete and `unmark NUMBER` to mark it
incomplete. Obtain the current task numbers with `list`.

```text
mark 1
unmark 1
```

Nelson reports an error for a missing, non-numeric, or out-of-range number.

## Deleting tasks

Use `delete NUMBER` to permanently remove the numbered task.

```text
delete 2
```

## Exiting

Use `bye` to close Nelson. Changes have already been saved after each command.

## Troubleshooting

- If Java reports an unsupported class version, confirm that `java -version`
  shows Java 25.
- If Nelson starts with an empty list after a storage warning, inspect
  `data/nelson.txt` for manual edits. Nelson skips malformed records and keeps
  valid ones whenever possible.
- Keep the JAR in a folder where Nelson can create and update the `data`
  subfolder.

## Acknowledgements

This project was developed with assistance from OpenAI Codex for code,
testing, documentation, and workflow review. All generated changes were
reviewed and verified against the project requirements.
