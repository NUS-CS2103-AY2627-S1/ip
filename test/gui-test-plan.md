# Manual GUI test plan

Use disposable task data for every test session. Build with `./gradlew shadowJar`
(Windows: `gradlew.bat shadowJar`), then launch with Java 25:

```text
java -Dsevensix.data.file=<absolute-path-to-test-folder>/tasks.txt -jar build/libs/SevenSix.jar
```

Replace the path placeholder and quote the entire `-D...` argument if it contains
spaces. Do not test destructive commands or file corruption against personal data.
Record the OS/version, JDK, JVM language, OS language, display resolution, scaling,
window dimensions, date, and result for each session.

## Cases

| ID | Action | Expected result |
| --- | --- | --- |
| G1 | Launch with an empty test folder. | Greeting and input are visible; input receives focus; no startup error. |
| G2 | Enter `todo read book`, press Enter, then enter `list` and click Send command. | Each command is submitted once; replies are correct; the input clears. |
| G3 | Submit blank input and spaces. | No empty message is added and the task list is unchanged. |
| G4 | Submit `deadline report /by`, `mark 999`, and a duplicate task. | Each error is legible in a red card; a subsequent valid command still works. |
| G5 | Resize from the normal window to 560x520, then 1440x900. | Header, field, and button remain usable; long messages wrap without ellipses or horizontal scrolling. |
| G6 | Add 20 tasks and list them; scroll up and down. | Older replies remain reachable; new replies are brought into view; scrolling does not move the input bar. |
| G7 | Paste a long mixed description containing `阅读`, emoji, pipes, and backslashes. | Glyphs remain legible and replies wrap; restarting and listing preserves the text. |
| G8 | Use the Chinese input method to compose a description, confirm it, then submit. | Composition text is not submitted prematurely; the completed command is submitted once. |
| G9 | Navigate with Tab/Shift-Tab, use Enter, and click controls after resizing. | Focus is visible and every control remains reachable and responsive. |
| G10 | Use a directory as the task-file path, or a disposable corrupt task file. | Startup warning is visible; failed writes show errors; existing valid tasks remain unchanged. |
| G11 | Enter `bye`, then separately reopen and close using the window close control. | The application exits without an uncaught exception; saved tasks survive restart. |
| G12 | Repeat under English and Chinese OS languages, and at different display scaling levels. | Dates retain the documented English format; task text is preserved; controls do not overlap. |
| G13 | Submit `help` using Enter and Send command, resize to 560x520, then submit `help todo`. | The full guide appears as a normal reply and remains readable by scrolling; extra arguments produce an error; the composer hint points to help. |
| G14 | Add a task, mark it, run `list`, then run `undo` twice. | List has a heading; each undo names the action and task; the second undo removes the addition. |

## Recorded local rendering checks

2026-09-14: macOS 26.5.1, Zulu JavaFX JDK 25.0.3. The running application's JavaFX
scene was captured and visually inspected with disposable task data. A temporary
driver submitted commands through the field action and button action and resized
the real window. This checks rendering and event handlers, not physical keyboard,
mouse, input-method composition, or a change to the actual OS display settings.

| JVM language | Captured scene sizes | Checks |
| --- | --- | --- |
| English | 900x720, 560x492, 1440x872 | Greeting, input, errors, scrolling layout, and label truncation checks passed. |
| Chinese | 900x720, 560x492, 1440x872 | Same checks, with mixed Chinese/English task descriptions, passed. |

The smaller scene heights reflect the native title bar in windows sized 560x520
and 1440x900. These are window/scene sizes, not simulated monitor resolutions.
The OS language and display scaling were not changed during these checks.

## Review-feedback smoke check

2026-10-13: macOS, Java 25.0.3. A temporary driver invoked the packaged
`sevensix.Launcher`, submitted commands through the field and button handlers,
and checked the list heading and two successive undo replies. The captured
900x720 scene was visually inspected; the new replies were readable.
This verifies application handlers and rendering, not physical input or the
cross-platform matrix below.

## Remaining manual matrix

All rows below are pending; do not infer results from JVM locale tests.

| Platform | OS language | Display/scaling examples | Required cases |
| --- | --- | --- | --- |
| macOS | English and Chinese | Native Retina and a scaled display mode | G1-G13 with physical input |
| Windows | English and Chinese | 1366x768 at 100%; 1920x1080 at 150% | G1-G13 |
| Linux | English and Chinese | 1366x768 and 1920x1080; available HiDPI mode | G1-G13 |

On each available machine, also run `gradlew check testChineseLocale`, using
`./gradlew` or `gradlew.bat` as appropriate. Record failures and skipped tests,
especially filesystem permission tests, rather than marking unsupported checks
as passed. The recorded console plan uses POSIX shell fixture commands; on Windows
use a compatible shell for that runner, or use the platform-independent JUnit suite.
