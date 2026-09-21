# Manual Test Plan

## C-NaturalDates

Before testing, note the computer's current local date. Start Melodie with an
empty task list.

| ID | Command | Expected result |
| --- | --- | --- |
| ND-1 | `deadline numeric /by 2/12/2026 1800` | Adds a deadline for 2 December 2026 at 6:00 PM. |
| ND-2 | `deadline same day /by today 1800` | Adds a deadline for today at 6:00 PM. |
| ND-3 | `deadline next day /by tomorrow 0900` | Adds a deadline for tomorrow at 9:00 AM. |
| ND-4 | `deadline weekday /by Mon 1400` | Adds a deadline for the first Monday strictly after today at 2:00 PM. |
| ND-5 | `deadline full weekday /by mOnDaY 1400` | Produces the same date as ND-4, showing that names are case-insensitive. |
| ND-6 | `event overnight /from today 2300 /to tomorrow 0100` | Adds an event from today at 11:00 PM to tomorrow at 1:00 AM. |
| ND-7 | `deadline invalid /by someday 1800` | Rejects the command and displays the supported date/time formats. |
| ND-8 | `deadline invalid time /by tomorrow 2500` | Rejects the command and displays the supported date/time formats. |

After restarting Melodie, run `list` and confirm that the dates resolved in
ND-1 to ND-6 remain the same calendar dates rather than being recalculated.

## C-Update

Start Melodie with one todo, one deadline, and one event. Mark at least one of
them as complete, then run `list` to obtain their task numbers.

| ID | Command | Expected result |
| --- | --- | --- |
| U-1 | `update 1 /description revised description` | Changes only task 1's description and preserves its type and completion status. |
| U-2 | `update 2 /by tomorrow 1800` | Changes only the deadline's due date and time. |
| U-3 | `update 3 /from Mon 1400` | Changes only the event's start date and time, provided it remains before the end. |
| U-4 | `update 3 /to Mon 1700` | Changes only the event's end date and time, provided it remains after the start. |
| U-5 | `update 1 /by tomorrow 1800` | Rejects `/by` if task 1 is a todo and leaves the task unchanged. |
| U-6 | `update 3 /to today 0000` | Rejects the update if the resulting event would end before it starts. |
| U-7 | `update 99 /description missing task` | Rejects an out-of-range task number. |
| U-8 | `update 1 /description` | Rejects a missing replacement value and displays the update format. |

Restart Melodie and run `list`. Confirm that every successful update remains
saved and unsuccessful updates did not alter any task.
