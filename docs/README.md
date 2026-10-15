# Zabud User Guide

Zabud is a royal task keeper for managing to-dos, deadlines, and events. It saves your tasks and command history automatically.

![Zabud's JavaFX interface](Ui.png)

## Quick start

1. Download `zabud.jar` from the [latest GitHub release](https://github.com/KohKoh-Nut/ip/releases/latest).
2. Install Java 25.
3. Open a terminal in the JAR's folder and run `java -jar zabud.jar`.
4. Type `help` to see the command summary.

Leading and trailing spaces are ignored. Dates use `DD/MM/YYYY`; times use the 24-hour `HHMM` format.

## Add tasks

Add a task without a date:

`todo DESCRIPTION`

Example: `todo read chapter 6`

Add a task with a deadline:

`deadline DESCRIPTION /by DATE_OR_TIME`

Example: `deadline submit report /by 18/09/2026 2359`

Add an event:

`event DESCRIPTION /from DATE_OR_TIME /to DATE_OR_TIME`

Example: `event tutorial /from 18/09/2026 1400 /to 18/09/2026 1500`

The event end must be later than its start. Both endpoints must either include dates or both be time-only.

## View and find tasks

- `list` shows every task and its number.
- `find KEYWORD` finds descriptions containing the keyword, ignoring case.

Example: `find report`

## Update tasks

Use the number shown by `list`:

- `mark TASK_NUMBER` marks a task as done.
- `unmark TASK_NUMBER` marks a task as not done.
- `delete TASK_NUMBER` permanently removes a task.

Examples: `mark 2`, `unmark 2`, `delete 2`

## Other commands

- `help` displays command formats and input requirements.
- `bye` saves the session and closes Zabud.

Zabud stores its data in `data/zabud.txt`, relative to the folder from which it is run. A missing data file is created automatically.
