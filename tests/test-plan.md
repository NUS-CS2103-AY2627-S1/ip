# Test Plan

## Recurring Tasks

1. Add a weekly recurring deadline.
   - Command: `deadline submit report /by 10/9/2026 1800 /repeat weekly`
   - Expected: the added task is shown as `[D][ ] submit report (by: 10 Sep 2026, Thursday 18:00) (repeats weekly)`.

2. Add a weekly recurring event.
   - Command: `event project meeting /from 10/9/2026 1400 /to 10/9/2026 1500 /repeat weekly`
   - Expected: the added task is shown with the event range and `(repeats weekly)`.

3. Turn on recurrence for an existing dated task.
   - Setup: add a normal deadline or event.
   - Command: `repeat 1 /weekly`
   - Expected: the selected task now shows `(repeats weekly)`.

4. Turn off recurrence for an existing recurring task.
   - Setup: add a recurring deadline or event.
   - Command: `unrepeat 1`
   - Expected: the selected task no longer shows `(repeats weekly)`.

5. Reject recurrence for todos.
   - Setup: add `todo read book`.
   - Command: `repeat 1 /weekly`
   - Expected: Downtown Gurl reports that only deadlines and events can repeat.

6. Reject unsupported recurrence frequency.
   - Command: `deadline submit report /by 10/9/2026 1800 /repeat daily`
   - Expected: Downtown Gurl reports that recurring tasks can only repeat weekly for now.

7. Preserve existing task storage compatibility.
   - Setup: start the app with existing saved `T`, `D`, and `E` task lines without recurrence fields.
   - Expected: all existing tasks load normally and appear without recurrence text.

8. Persist recurring tasks.
   - Setup: add a recurring deadline or event, then restart the app.
   - Expected: the task still shows `(repeats weekly)` after loading from disk.
