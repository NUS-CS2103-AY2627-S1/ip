# Alice

Alice is a task manager for keeping track of todos, deadlines, events, and recurring scheduled tasks.
Tasks are saved automatically in `data/Alice.txt` and restored when the application starts.

## AI usage

The project author used **OpenAI Codex (GPT-5)** as an AI coding assistant.
Codex was used in the making of the GUI, personality of the chatbot, Javadoc comments and in some parts of the chatbot features.
This includes the command class, recurrence class and the parser class.
The author directed the requested changes, reviewed the generated work, and tested the application.

## Setting up in IntelliJ

Prerequisite: JDK 25.

1. Open IntelliJ IDEA and select **Open**.
2. Select this project folder and accept the default import settings.
3. Set the project SDK to **JDK 25** and the language level to **SDK default**.
4. Run `alice.Alice.main()` from `src/main/java/alice/Alice.java`.

## Commands

### Add tasks

```text
todo DESCRIPTION
deadline DESCRIPTION /by yyyy-MM-dd
event DESCRIPTION /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm
```

Examples:

```text
todo borrow book
deadline return book /by 2026-10-01
event project meeting /from 2026-10-08 1400 /to 2026-10-08 1600
```

### Manage tasks

```text
list
mark TASK_NUMBER
unmark TASK_NUMBER
delete TASK_NUMBER
find KEYWORD
date yyyy-MM-dd
bye
```

- `list` shows every task.
- `mark` and `unmark` change a task's completion status.
- `delete` removes one task, including an individual recurring occurrence.
- `find` searches task descriptions without considering letter case.
- `date` shows deadlines due on the supplied date and events that occur on that date.

### Recurring deadlines and events

Deadlines and events can repeat `daily`, `weekly`, `monthly`, or `yearly`. Interval names are case-insensitive.
Todos cannot recur because they do not have a scheduled date.

Add recurrence while creating a task:

```text
deadline Pay rent /by 2026-10-01 /r MONTHLY
event Team meeting /from 2026-10-08 1400 /to 2026-10-08 1500 /r weekly
```

Alternatively, configure an incomplete deadline or event already in the list:

```text
repeat TASK_NUMBER INTERVAL
repeat TASK_NUMBER none
```

`repeat TASK_NUMBER none` stops that task from generating future occurrences. Todos and completed tasks cannot be
configured for recurrence.

When a recurring task is marked complete, Alice keeps the completed occurrence and adds the first future occurrence
based on the current date and time. Missed intervals are skipped. For events, both the start and end date-times are
shifted by the same interval. Recurring tasks have a suffix such as `(repeats: monthly)`.

Completed recurring tasks cannot be unmarked or reconfigured, but they can be deleted individually.

## Storage compatibility

Recurrence is saved only for recurring deadlines and events. Existing saved tasks without recurrence information remain
valid and are loaded as non-recurring tasks.
