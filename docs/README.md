# Cheecken User Guide

Your memory has left the chat. Cheecken has not.
Type a command, then press **Enter** or click **Send**.

## Commands

Replace `<task>`, `<date>`, `<number>`, and `<keyword>` with your own values—skip the angle brackets.

| Command | What it does | Example |
|---|---|---|
| `todo <task>` | Add a task without a deadline. Optimism is free. | `todo touch grass` |
| `deadline <task> /by <date>` | Add a task with a due date. | `deadline submit report /by tomorrow 1800` |
| `event <task> /from <date> /to <date>` | Add an event with a start and end. | `event study /from Mon 0900 /to Mon 1000` |
| `list` | Show all tasks and their numbers. | `list` |
| `mark <number>` | Mark a task as done. Take the tiny victory. | `mark 1` |
| `unmark <number>` | Mark it as unfinished. We saw nothing. | `unmark 1` |
| `find <keyword>` | Find descriptions containing your keyword, ignoring case. | `find report` |
| `delete <number>` | Remove a task permanently. No undo button, no take-backs. | `delete 1` |
| `bye` | Close the app. Go do the tasks. | `bye` |

Use numbers from **`list`**, starting at **1**; deleting a task shifts later numbers.
`find` numbers its matches separately, so run `list` before marking or deleting a search result.
`[T]`, `[D]`, and `[E]` mean todo, deadline, and event; `[X]` means done, `[ ]` means unfinished.
Command names ignore case. Keep `/by`, `/from`, and `/to` **lowercase**, with spaces around them.

## Dates Cheecken understands

Use any of these after `/by`, `/from`, or `/to`:

| Input | Meaning |
|---|---|
| `today` | Today's local date |
| `tomorrow` | The next local calendar date |
| `now` | The current local date **and time** |
| `Mon` or `Monday` | The next Monday |
| `Tue` or `Tuesday` | The next Tuesday |
| `Wed` or `Wednesday` | The next Wednesday |
| `Thu` or `Thursday` | The next Thursday |
| `Fri` or `Friday` | The next Friday |
| `Sat` or `Saturday` | The next Saturday |
| `Sun` or `Sunday` | The next Sunday |
| `<date word> HHmm` | Any word above **except `now`**, plus a 24-hour time: `today 0900`, `tomorrow 1800`, `Friday 2359` |
| `d/M/yyyy` | Numeric date: `15/10/2026` |
| `d/M/yyyy HHmm` | Numeric date and time: `15/10/2026 1800` |
| `yyyy-MM-dd` | ISO date: `2026-10-15` |
| `yyyy-MM-ddTHH:mm` | ISO date-time: `2026-10-15T18:00`; seconds and fractional seconds are also accepted |

- Date words ignore case: `tOmOrRoW` works. Your keyboard can relax.
- Weekdays mean **strictly next**: entering `Mon` on a Monday means seven days later.
- `HHmm` needs four digits: hours `00–23`, minutes `00–59`. Use `0900`, not `9am` or `09:00`.
- `now` stands alone: `now 0900` is not a negotiation.
- Date-only inputs have no time. Explicit times display in 12-hour form, such as `6:00 PM`.
- Dates use your computer's local timezone. Past dates are allowed; time travel is your problem.
- Event endpoints resolve independently. An end before the start is accepted, so double-check both dates.

**Not supported:** `yesterday`, `next Monday`, `next week`, `in 3 days`, `Tues`, `Thurs`, `Mon.`,
`today 09:00`, or `tomorrow 6pm`. Invalid commands show an error; fix the input and try again.

## Saving your precious plans

Tasks save automatically to `data/cheecken.txt` relative to the app's **working directory**.
Launch from the same directory to see the same tasks. If the file is missing, Cheecken starts empty
and creates it when saving. A save error means the change may not survive a restart.

Natural dates are resolved when entered: today's `tomorrow` stays that date forever.
Create them through commands; natural words typed directly into the storage file are skipped.
Only tasks are saved, not chat history. Your embarrassing commands can rest in peace.

For setup and launch instructions, see the [project README](../README.md).
