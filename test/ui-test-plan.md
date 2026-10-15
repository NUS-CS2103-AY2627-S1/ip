# UI Test Plan

## Test Case 1: Manage missions with Orbit

Aim: Verify that Orbit uses its mission-control voice while supporting every task command, priority display,
task updates, search, deletion, and a clean exit.

### Inputs

```text
todo borrow book
deadline return book /by 2026-01-18 /priority high
event project meeting /from 2026-01-18 14:00 /to 2026-01-18 16:00 /priority med
mark 1
unmark 1
find high
list
delete 2
bye
```

### Expected Output

```text
____________________________________________________________
 ____  _
| __ )| | ___   ___  _ __
|  _ \| |/ _ \ / _ \| '_ \
| |_) | | (_) | (_) | |_) |
|____/|_|\___/ \___/| .__/
                    |_|
Orbit online. Your task mission is ready.
What shall we launch?
____________________________________________________________
____________________________________________________________
 Mission logged:
  [T][ ] borrow book (priority: low)
 You now have 1 missions on your radar.
____________________________________________________________
____________________________________________________________
 Mission logged:
  [D][ ] return book (by: Jan 18 2026) (priority: high)
 You now have 2 missions on your radar.
____________________________________________________________
____________________________________________________________
 Mission logged:
  [E][ ] project meeting (from: Jan 18 2026 14:00 to: Jan 18 2026 16:00) (priority: med)
 You now have 3 missions on your radar.
____________________________________________________________
____________________________________________________________
 Mission complete:
  [T][X] borrow book (priority: low)
____________________________________________________________
____________________________________________________________
 Mission returned to active orbit:
  [T][ ] borrow book (priority: low)
____________________________________________________________
____________________________________________________________
 Signals matching "high":
2.[D][ ] return book (by: Jan 18 2026) (priority: high)
____________________________________________________________
____________________________________________________________
 Mission control overview:
1.[T][ ] borrow book (priority: low)
2.[D][ ] return book (by: Jan 18 2026) (priority: high)
3.[E][ ] project meeting (from: Jan 18 2026 14:00 to: Jan 18 2026 16:00) (priority: med)
____________________________________________________________
____________________________________________________________
 Mission removed from your radar:
  [D][ ] return book (by: Jan 18 2026) (priority: high)
 You now have 2 missions on your radar.
____________________________________________________________
____________________________________________________________
 Orbit signing off. Clear skies ahead!
____________________________________________________________
```
