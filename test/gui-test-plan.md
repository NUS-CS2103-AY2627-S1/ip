# Zikiai GUI Test Plan

Run `./gradlew guiTest` using Java 25 on a graphical desktop. These JUnit tests
load the real FXML controllers on the JavaFX thread and use temporary task files.
They are separate from the desktop-independent `./gradlew check` suite.
The console plan still verifies all command types, errors, and persistence.

## GUI-01 Send, Enter, invalid commands, and farewell

**Aim:** Check the actual controls, shared command handling, and session state.

**Inputs:** Open a new session; click Send on an empty field; send `todo read book`;
submit `mark 99` through the field's Enter action; send `list`; send `bye`.

**Expected output:** One initial greeting bubble; no additional empty bubble;
paired user/reply bubbles for every command. Adding the todo confirms one task;
marking 99 reports that the task number does not exist; list still shows
`1.[T][ ] read book`. The input clears after each command and remains enabled
after the error. User messages have no avatar while Zikiai replies retain a
circular bot avatar, and a short command-format hint remains above the input.
After `bye`, the reply is `Okay, bye bye! See you again lah.` and both controls are disabled. Closing
the window exits the application.

Automated by `MainWindowTest.handleUserInput_sendAndEnter_updateDialogsAndPreserveState`.
A snapshot before bye is written to `build/reports/gui/window.png`.

## GUI-02 Corrupted storage at startup

**Aim:** Protect saved data when startup fails.

**Inputs:** Start a session with `corrupt` as its saved task file.

**Expected output:** The initial bubble reports that line 1 is invalid; input
and Send are disabled. No task can be added over the corrupted file.

Automated by `MainWindowTest.setZikiai_loadFailure_disablesControlsAndShowsError`
and the backend's corrupted-file test.

## GUI-03 Resize and long messages (manual)

**Aim:** Ensure messages remain readable and the latest reply is reachable.

**Inputs:** Run `./gradlew run`; add a todo with a long description; list it;
resize the window narrower and taller; enter enough commands to require scrolling.

**Expected output:** Message text wraps without truncation, controls remain
visible, and the view scrolls to the latest reply. Earlier replies are accessible
by scrolling upwards.

## GUI-04 Packaged application (manual)

**Aim:** Verify the GUI can launch without IntelliJ or the project resources.

**Inputs:** Build with `./gradlew shadowJar`, copy `build/libs/zikiai.jar` into an
empty folder, and run `java -jar zikiai.jar` there on the matching OS/architecture
using Java 25. Add a task, close the window, restart, and enter `list`.

**Expected output:** The GUI, styles, and avatars load from the JAR; the task is
stored in that folder's `data/zikiai.txt` and appears after restarting.
