# Xian User Guide

Xian is a friendly task-management chatbot. Use the text field and press
Enter or click **Send** to submit a command.

<img src="Ui.png" alt="Xian user interface" width="400">

## Commands

### Add tasks

Create a simple task:

```text
todo read Java book
```

Create a deadline:

```text
deadline submit report /by 18/9/2026 1800
```

Create an event:

```text
event project meeting /from 19/9/2026 1400 /to 19/9/2026 1600
```

Dates use the format `d/M/yyyy HHmm`.

### View and search tasks

```text
list
find book
```

### Mark and delete tasks

Use the task number shown by `list`:

```text
mark 1
unmark 1
delete 1
```

### Update tasks

Update a task description:

```text
update 1 desc read Java textbook
```

Update a deadline or event time:

```text
update 2 by 20/9/2026 1800
update 3 from 21/9/2026 1400
update 3 to 21/9/2026 1600
```

Xian validates task numbers, dates, event time ranges, command formats, and
saved task data. Invalid commands are shown as error messages without closing
the application.

## Exit

Type `bye` to close the application, or close the window normally.
