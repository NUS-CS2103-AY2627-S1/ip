# Downtown Gurl User Guide

Downtown Gurl is a chatbot that tracks todos, deadlines, and events.

## Adding Todos

Use `todo` followed by the task description.

Example:

```text
todo read book
```

Expected outcome:

```text
Gotcha. Noted it downz:
  [T][ ] read book
```

## Adding Deadlines

Use `deadline`, a description, and `/by` followed by a date. Dates use `d/M/yyyy` and may include a
24-hour time in `HHmm` form.

Example:

```text
deadline submit report /by 10/9/2026 1800
```

Expected outcome:

```text
Gotcha. Noted it downz:
  [D][ ] submit report (by: 10 Sep 2026, Thursday 18:00)
```

## Adding Events

Use `event`, a description, `/from`, and `/to`.

Example:

```text
event project meeting /from 10/9/2026 1400 /to 10/9/2026 1500
```

Expected outcome:

```text
Gotcha. Noted it downz:
  [E][ ] project meeting (from: 10 Sep 2026, Thursday 14:00 to: 10 Sep 2026, Thursday 15:00)
```

## Recurring Tasks

Deadlines and events can be labelled as recurring weekly. Recurrence is a label only; Downtown Gurl
does not generate future task copies.

Add a recurring deadline:

```text
deadline submit report /by 10/9/2026 1800 /repeat weekly
```

Add a recurring event:

```text
event project meeting /from 10/9/2026 1400 /to 10/9/2026 1500 /repeat weekly
```

Turn on weekly recurrence for an existing deadline or event:

```text
repeat 2 /weekly
```

Turn off recurrence:

```text
unrepeat 2
```

Recurring tasks appear with `(repeats weekly)`:

```text
[E][ ] project meeting (from: 10 Sep 2026, Thursday 14:00 to: 10 Sep 2026, Thursday 15:00) (repeats weekly)
```

## Listing Tasks

Use `list` to show all tasks. Dated tasks are sorted by date before todos.

Example:

```text
list
```

## Finding Tasks

Use `find` followed by a keyword to search task descriptions.

Example:

```text
find meeting
```

## Marking And Deleting Tasks

Use the task number shown by `list`.

```text
mark 1
unmark 1
delete 1
```
