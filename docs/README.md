# Alfred User Guide

Alfred is a polite, cave-themed butler for keeping a simple task ledger.
Type a command in the box at the bottom of the window, then press Enter or
click **Send**. Alfred saves your tasks automatically in `data/alfred.txt`.

![Alfred application window](Ui.png)

## Quick start

Add a task, then use `list` to see your ledger:

```text
todo Pack climbing gear
deadline Send expedition brief /by 18/9/2026 1800
event Crystal cavern survey /from 17/9/2026 0900 /to 17/9/2026 1130
list
```

## Commands

### Add tasks

```text
todo DESCRIPTION
deadline DESCRIPTION /by DATE
event DESCRIPTION /from START /to END
```

Use `yyyy-MM-dd` for a date (for example, `2026-09-18`) or
`d/M/yyyy HHmm` for a date and time (for example, `18/9/2026 1800`). An
event's end time must be later than its start time.

### View tasks

```text
list
find KEYWORD
sort
sort asc
sort desc
```

`find` matches descriptions without regard to letter case. `sort` and
`sort asc` show tasks in chronological order; `sort desc` reverses that
view. Sorting does not change your saved task order or task numbers.

### Update tasks

```text
mark NUMBER
unmark NUMBER
delete NUMBER
```

`NUMBER` is the one-based number shown by `list`. Marking completes a task;
unmarking reopens it.

### Exit

```text
bye
```

## Tips

- Enter command names in lowercase. Leading and trailing spaces are fine.
- Alfred rejects duplicate tasks, repeated `/by`, `/from`, or `/to` markers,
  and incomplete commands. Rejected commands leave your ledger unchanged.
- If the saved task file is missing, Alfred starts with an empty ledger. If it
  cannot read the file, it shows an error instead of replacing your data.
