# Nimbus User Guide

Nimbus is a lightweight student task assistant that keeps todos, deadlines, and events in one local list—a clear forecast of what's next.

![Nimbus desktop interface](Ui.png)

## Quick start

1. Install Java 25.
1. Download `nimbus.jar` from the [latest Nimbus release](https://github.com/shiverin/ip/releases/latest).
1. Move `nimbus.jar` into a folder where Nimbus can keep its data.
1. Open a terminal in that folder and run:

   ```shell
   java -jar nimbus.jar
   ```

Nimbus opens a desktop window and stores tasks in `data/nimbus.txt` beside the JAR's working folder.

## Commands

| Action | Command | Example |
|---|---|---|
| Show all tasks | `list` | `list` |
| Add a todo | `todo DESCRIPTION` | `todo read chapter 6` |
| Add a deadline | `deadline DESCRIPTION /by YYYY-MM-DD` | `deadline submit report /by 2026-09-18` |
| Add an event | `event DESCRIPTION /from START /to END` | `event project meeting /from 2pm /to 3pm` |
| Mark a task as done | `mark TASK_NUMBER` | `mark 2` |
| Mark a task as not done | `unmark TASK_NUMBER` | `unmark 2` |
| Change a description | `update TASK_NUMBER NEW_DESCRIPTION` | `update 2 submit final report` |
| Remove a task | `delete TASK_NUMBER` | `delete 2` |
| Find tasks | `find KEYWORD` | `find report` |
| Exit Nimbus | `bye` | `bye` |

Task numbers come from the numbered output of `list`. Nimbus ignores letter case when finding tasks.

## Common workflows

### Plan and complete a deadline

1. Enter `deadline submit report /by 2026-09-18`.
1. Enter `list` to check its task number.
1. Enter `mark 1` when the report is submitted.

Nimbus keeps the deadline date and displays the completed task with an `X`.

### Correct a task

Enter `update 1 submit final report` to replace only the description. Nimbus preserves the task type, completion state, date, and time information.

## Data and recovery

Nimbus creates `data/nimbus.txt` automatically. Keep this file if you move to a newer Nimbus release and want to retain your tasks.

If an individual saved record is damaged, Nimbus loads the remaining valid tasks and reports how many records it skipped. If the data file cannot be opened, Nimbus explains the problem and starts with an empty in-memory list.

## Troubleshooting

### Nimbus does not start

Run `java -version` and confirm the major version is 25. Then start Nimbus from a terminal with `java -jar nimbus.jar` so any launch error remains visible.

### A command is rejected

Compare your input with the command table. Include a task number for task-changing commands and use `YYYY-MM-DD` for deadline dates.

### My tasks are missing

Start Nimbus from the same folder you used previously. Nimbus reads tasks relative to the folder from which the JAR is run.

## Credits

Nimbus began from the SE-EDU individual-project starter template. The Week 6 optional enhancements were developed with OpenAI Codex as an AI coding collaborator.
