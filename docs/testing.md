# Testing Penny

Run the automated suite with Java 25:

```powershell
.\gradlew.bat test checkstyleTest
```

The JUnit suite covers command recognition, parser success and failure paths,
loading mode, duplicate prevention, filtering with original task numbers, integer
boundaries, task creation and completion, date parsing and display, task list
operations, file creation and replacement, I/O failures, and CLI persistence
across sessions. CLI tests use separate Java processes in temporary directories;
they do not read or modify the project's `commands.txt`.

Error handling tests also cover whitespace, missing and repeated parameters,
control characters, impossible dates, invalid event ranges, unreadable saved
content, read-only files, failed-save rollback and retry, and end-of-input.
The read-only attribute test is skipped on filesystems that do not support or
enforce DOS read-only attributes.

This is a behavior-based test inventory, not a measured coverage percentage.
JavaFX launch and interaction are excluded from the automated suite, as are
unreachable fallback branches and JVM-generated record/enum methods.

## Manual GUI checks

These checks still need to be performed on a desktop with JavaFX. Use unique
task descriptions and delete the test tasks afterwards, as the GUI uses the
normal save file.

1. Run `./gradlew runGui`. Confirm that the window opens and both speaker images
   appear when exchanging messages.
2. Enter `todo GUI test task` using Enter. Confirm that the input clears and the
   user message and Penny reply appear on the correct sides.
3. Submit `list` using the Send button. Confirm that the new task appears.
4. Mark and unmark the task using its displayed number. Run `list` after each
   operation and confirm that its completion indicator changes.
5. Submit an unknown command. Confirm that its error reply is visible and that
   a subsequent valid command still works.
6. Send enough messages to fill the window. Confirm automatic scrolling, readable
   wrapping, and usable layout when resizing down to the minimum window size.
7. Restart the GUI and run `list`. Confirm that the task was restored, then
   delete it using its current number.
8. Enter `bye`. Confirm that the goodbye reply appears, the input and Send button
   become disabled, and the window closes after about one second.

The task lifecycle test checks that `unmark` replies “Marked as undone” and
changes the task's completion state.

## Input and storage error behavior

- Leading/trailing whitespace and extra spaces between command parameters are
  accepted. Task descriptions retain their internal spacing and punctuation.
- Dated tasks require standalone `/by`, or `/from` followed by `/to`, exactly
  once each. Slash-prefixed tokens are reserved for parameters in dated tasks;
  slashes within words and URLs remain valid description text.
- Dates must exist, and an event's end must be strictly later than its start.
  A date without a time still means midnight. Descriptions remain unique across
  all task types using the existing case-sensitive comparison.
- New save files and parent directories are created automatically. Task changes
  are applied only after saving succeeds. If saving fails, fix permissions,
  free disk space, or correct the path, then retry the command.
- Saves are written to a temporary file before replacing the previous save.
  Atomic replacement is used where supported by the filesystem.
- Queries and `bye` do not write to storage, so they remain available during
  storage problems.
- An unreadable save disables task changes for that session. Invalid command
  history loads only up to the first bad line and also disables task changes.
  Later numbered commands are not replayed because their target tasks could
  have changed. The original file is preserved. Back it up, repair the reported
  problem, and restart Penny.

To check warning visibility manually, use a disposable copy of `commands.txt`
with an invalid command on its second line. Restart the GUI, confirm that the
warning identifies the line, and confirm that task changes are blocked while
`list` and `bye` still work. Restore the original file before continuing.
