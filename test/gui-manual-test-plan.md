# GUI Manual Test Plan

Use this plan for behavior that is impractical to verify reliably with headless JUnit tests.

## Supported commands

Run `todo`, `deadline`, `event`, `list`, `find`, `date`, `mark`, `unmark`, `update`, `delete`, and `bye`
through both the Send button and the Enter key. Confirm that each user command appears on the right, Pixel's response
appears on the left, errors use the alert style, and `bye` disables further input.

## Window sizes and display scaling

Test the minimum 420 by 480 window, the default 560 by 680 window, and a maximized window. Repeat at 100%, 125%,
150%, and 200% display scaling where available. Confirm that text remains readable, dialogs wrap without horizontal
scrolling, controls remain accessible, and the conversation automatically scrolls to the latest response.

## Operating systems

Launch the application on Windows, macOS, and Linux using Java 25. Confirm that the FXML views and stylesheet load,
the window can be resized, commands behave consistently, and tasks persist in `data/pixel.txt` after restarting.

## Language and regional settings

Repeat startup and representative deadline commands under English and Chinese OS language settings. Confirm that Pixel
still accepts ISO dates such as `2026-09-12`, displays dates in the documented English format, and reads and writes
non-ASCII task descriptions without corruption.

## Storage failures

Run Pixel from a location where `data` is read-only, then add or update a task. Confirm that Pixel reports the save
failure without closing and that the task remains available for the current session. Restore write access and verify
normal persistence afterward.
