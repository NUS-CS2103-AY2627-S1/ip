# Manual GUI Testing

Use this checklist for behavior that is visual, animated, or dependent on native pointer and window
handling. Run the checks on a disposable copy of the application so task data can be changed safely.

## Launch and layout

1. Start Yachiyo with Java 25.
2. Verify that the greeting matches the time of day and appears only once.
3. Verify that both profile pictures are circular and retain their proportions.
4. Resize the window to its minimum size and then enlarge it.
5. Verify that the task summary remains centered, fits its text, and does not stretch across the
   window.
6. Verify that dialogs remain readable and are not covered by the input controls.

## Error presentation

1. Enter an unknown command and verify that `Check command` uses the gentle warning style.
2. Enter a valid command with an unavailable task number and verify that
   `Cannot complete command` uses the invalid-operation style.
3. In a disposable copy, replace the data file with malformed task data and restart Yachiyo.
4. Verify that only the red `Storage error` dialog is shown and no task-list response follows it.
5. Enter `bye` and verify that the farewell appears without another storage error, then that the
   window closes normally.
6. Restore or remove the disposable data file after the check.

## Task summary and celebration

1. Add one task and verify that the summary reads `1 task · 1 remaining`.
2. Add another task and verify that the summary uses the plural `tasks`.
3. Mark one task and verify that the remaining count decreases.
4. Mark the final task and verify that the summary briefly glows and pulses.
5. Verify that the completion response ends with `Good job!` on a new line.
6. Run `mark` on an already completed task and verify that the animation does not repeat.
7. Unmark a task and verify that its remaining count increases.

## Scrolling and input

1. Add enough tasks for the conversation to exceed the visible area.
2. Without dragging the vertical scroll bar first, use the mouse wheel or trackpad to scroll upward.
3. Verify that manual scrolling works immediately after launch and after sending commands.
4. Send another command and verify that the conversation moves to the newest response.
5. Submit an empty or whitespace-only input and verify that no dialog is added.
6. Verify that pressing Enter and clicking `Send` each submit exactly one command.

## Exit behavior

1. Enter `bye` and verify that the farewell is displayed.
2. Verify that the input field and send button become disabled immediately.
3. Verify that the window closes after the farewell remains visible briefly.
