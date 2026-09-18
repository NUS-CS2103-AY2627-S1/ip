# Manual testing

JUnit tests (`./gradlew test`) and the text-UI plan (`test/ui-test-plan.md`) cover the
chatbot's logic. This page covers what they cannot: the JavaFX window, the packaged
JAR, and the machine the app runs on.

Coverage report: `./gradlew test jacocoTestReport`, then open
`build/reports/jacoco/test/html/index.html`. The GUI classes are left out of that report
on purpose, because they are checked by hand here.

## How the GUI was checked

A throwaway JavaFX harness opened the real `MainWindow.fxml` with the real stylesheet,
typed commands into the text field, pressed the button (or fired Enter), resized the
window, and saved snapshots of the scene. Each result below was confirmed from those
snapshots or from the window state the harness printed.

Last run: 2026-09-15, macOS 15 (Apple silicon), Java 25.0.3 (Zulu FX).

## GUI checklist

| # | Check | Steps | Expected | Result |
| --- | --- | --- | --- | --- |
| G1 | Asymmetric messages | Type `todo run 10 miles` | Command in a small blue chip on the right, no avatar; reply in a wide cream card with a small round avatar | Pass |
| G2 | Error highlight | Type `mark 9` on a one-task list | Red card with a `NO EXCUSES!` tag, shakes briefly, no prefix repeated in the text | Pass |
| G3 | Task lists line up | Add three tasks, type `list` | Task lines in monospace so the `[T][X]` boxes form a column; sentences stay proportional | Pass |
| G4 | Resize narrow | Shrink the window to 340 px wide | Bubbles re-wrap, nothing cut off, no horizontal scroll bar | Pass |
| G5 | Resize wide | Stretch the window to 760 px wide | Reply cards grow to use the width; user chips stay compact | Pass |
| G6 | Long replies | Add a deadline with a long description, then `list` | Text wraps inside the card; card height grows to fit | Pass |
| G7 | Load warning | Start with `data/tasks.txt` holding one bad line | Yellow `HEADS UP` card after the greeting, naming the `.bak` backup | Pass |
| G8 | Enter key | Type `help` and press Enter | Command sent and text field cleared | Pass |
| G9 | Help window | Type `help` twice | One help window opens; the second `help` reuses it instead of opening another | Pass |
| G10 | `bye` | Type `bye` | Farewell shown, input and button disabled, app exits about 1.5 s later | Pass |
| G11 | Packaged JAR | `./gradlew shadowJar`, then `java -jar build/libs/davidgoggins.jar` in an empty folder | Window opens with no error output | Pass |

## Environment checklist

| # | Check | Steps | Expected | Result |
| --- | --- | --- | --- | --- |
| E1 | Chinese locale | Run the same dated commands with `-Duser.language=zh -Duser.country=CN`, and run `./gradlew test` under that locale | Output identical to English; all tests pass | Pass |
| E2 | Turkish locale | Type `LIST` and `find lift` with `-Duser.language=tr -Duser.country=TR` | `LIST` works and `find lift` matches `LIFT weights` | **Failed, then fixed**: `LIST` became `lıst` (dotless i). Now lower-cased with `Locale.ROOT`, with regression tests |
| E3 | Unreadable save file | `chmod 000 data/tasks.txt`, start, add a task | Warning; file is not overwritten | Pass |
| E4 | Folder in place of the save file | `mkdir data/tasks.txt`, start, add a task | Read and save warnings; the app keeps working | Pass |
| E5 | Binary save file | Copy a binary file to `data/tasks.txt` | "not a plain text file" warning; original backed up | Pass |
| E6 | File in place of the data folder | Create a file named `data`, start, add a task | Save warning; the app keeps working | Pass |

## Not yet checked

These need a machine or a person the checks above did not have:

- Windows and Linux: fonts (Menlo falls back to Consolas or DejaVu Sans Mono), and the
  file-permission test, which JUnit skips where permissions are not enforced.
- High-DPI and non-Retina screens, and dragging the window edge by hand rather than
  resizing it from code.
- Scrolling a long conversation with a mouse wheel or trackpad.
