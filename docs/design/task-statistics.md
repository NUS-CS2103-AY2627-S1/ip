# Task statistics command design

## Command

The command has no arguments:

```text
stats
```

It reports statistics derived from the current task list. It does not modify
tasks or write a separate statistics file.

## Output

```text
Task statistics:
Total tasks: 8 / 10
Completed: 3
Incomplete: 5
Completion rate: 37.5%

By type:
Todos: 3
Deadlines: 2
Events: 3

Scheduled today: 2
Remaining task slots: 2
```

The values in this example are illustrative. The actual output uses the
current task list and its configured task limit.

## Calculation rules

- Completed and incomplete counts use each task's existing completion status.
- Type counts distinguish todos, deadlines, and events.
- Scheduled-today count uses the existing `occursOn(LocalDate.now())` behavior.
- Remaining slots is the task limit minus the current task count.
- An empty list has a completion rate of `0.0%` to avoid division by zero.
- Any command text after `stats` is invalid.
