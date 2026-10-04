# Free-time Search Test Plan

## Automated coverage

Run `gradlew.bat test` on Windows. The JUnit suite verifies:

* recognition and dispatch of the `free` command;
* searches before, during, and after the daily 8:00 AM-6:00 PM window;
* selection of the earliest exact-duration slot;
* optional present or future `/from` dates and weekend searches;
* overlapping, adjacent, completed, zero-duration, and multi-day events;
* exclusion of todos and deadlines from busy time;
* invalid durations, dates, duplicate parameters, and unexpected text; and
* deterministic current-time behavior through an injected clock.

## Manual smoke test

1. Start Kelore with `gradlew.bat run`.
2. Choose a date that is today or later and use it as `DATE` below.
3. Enter `event class /from DATE 0800 /to DATE 1200`, replacing `DATE` with
   the chosen date in `d/M/yyyy` format.
4. Enter `free 4 /from DATE`, using the same date.
5. Verify that Kelore reports `DATE` from `12:00 PM` to `4:00 PM`.
6. Mark the event as completed using its task number.
7. Repeat `free 4 /from DATE`.
8. Verify that Kelore now reports `DATE` from `8:00 AM` to `12:00 PM`.

## Regression checks

Verify that `todo`, `deadline`, `event`, `list`, `mark`, `unmark`, `delete`,
`on`, `find`, and `bye` retain their existing behavior and that restarting
Kelore preserves the existing task data format.
