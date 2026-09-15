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

Known observation: `unmark` currently says “Marked as done” in its reply, although
the task correctly becomes undone. The tests verify the state change without
requiring that misleading wording.
