# Bo

Bo is a Java task-management application for creating, organizing, searching,
and completing to-dos, deadlines, and events.

## Quick start

1. Install JDK 25 or later.
2. Open the project in IntelliJ.
3. Run `bo.gui.Launcher` for the graphical interface or `bo.Bo` for the
   command-line interface.

Bo saves changes automatically in `data/bo.txt`.

## Commands

| Purpose | Command |
| --- | --- |
| Add a to-do | `todo DESCRIPTION` |
| Add a deadline | `deadline DESCRIPTION /by DATE` |
| Add an event | `event DESCRIPTION /from DATE_TIME /to DATE_TIME` |
| List tasks | `list` |
| Find tasks | `find KEYWORD` |
| Mark complete | `mark TASK_NUMBER` |
| Mark incomplete | `unmark TASK_NUMBER` |
| Delete a task | `delete TASK_NUMBER` |
| Show upcoming deadlines | `upcoming` |
| Show deadlines on a date | `due DATE` |
| Postpone a deadline | `snooze TASK_NUMBER DAYSd` |
| Change a deadline date | `reschedule TASK_NUMBER DATE` |
| Exit Bo | `bye` |

Dates use the `yyyy-MM-dd` format. Event date-times use `yyyy-MM-dd HH:mm`.
Only deadline tasks can be snoozed or rescheduled, and new deadline dates
cannot be in the past.

## Documentation

See the complete [Bo User Guide](docs/README.md) or visit the project's
GitHub Pages site for the published documentation.
