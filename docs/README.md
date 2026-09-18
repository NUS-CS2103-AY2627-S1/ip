# Kopi

Kopi is a desktop task manager for todos, deadlines, and events. It saves your tasks locally, so they are still there the next time you open it.

![Kopi's graphical interface](Ui.png)

## Getting started

1. Install Java 25.
2. Download `kopi.jar` from the [latest GitHub release](https://github.com/pranavp311/ip/releases/latest).
3. Open a terminal in the download folder.
4. Run `java -jar kopi.jar`.

Type a command in the box and press <kbd>Enter</kbd> or click **Send**. Dates use `YYYY-MM-DD`.

## Commands

| Action | Command | Example |
| --- | --- | --- |
| Add a todo | `todo DESCRIPTION` | `todo borrow book` |
| Add a deadline | `deadline DESCRIPTION /by DATE` | `deadline submit iP /by 2026-09-18` |
| Add an event | `event DESCRIPTION /from START /to END` | `event camp /from 2026-09-19 /to 2026-09-21` |
| List tasks | `list` | `list` |
| Find tasks | `find KEYWORD` | `find book` |
| Mark a task | `mark NUMBER` | `mark 1` |
| Unmark a task | `unmark NUMBER` | `unmark 1` |
| Delete a task | `delete NUMBER` | `delete 1` |
| Exit Kopi | `bye` | `bye` |

Task numbers come from `list`. Searches ignore letter case. You can add extra spaces around `/by`, `/from`, and `/to`.

Rules worth knowing:

- Dates must use `YYYY-MM-DD`, and an event must end after it starts.
- Descriptions cannot contain `|`, because Kopi uses it in the data file.
- Two tasks are duplicates when their type and details match. Description matching ignores letter case.
- Commands that do not need arguments, such as `list` and `bye`, reject extra text.

Kopi shows invalid commands in a red reply and leaves the task list unchanged.

## Storage

Kopi stores tasks in `data/kopi.txt`, relative to the folder where you launch the JAR. Launch it from the same folder each time to keep using the same task list.

If Kopi cannot read the data file, it reports the problem and blocks task-changing commands so it does not overwrite the file. Back up or fix `data/kopi.txt`, then restart Kopi. Removing the file starts a new empty task list.
