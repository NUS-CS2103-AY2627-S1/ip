# Mochi acceptance checks

## Core workflow

1. Start with an empty temporary data file and add a todo, deadline, and event.
2. Verify `list`, `mark`, `unmark`, `delete`, `find`, and `bye` responses.
3. Restart Mochi and verify all task details and statuses are retained.

## Error handling

1. Try blank input, an unknown command, missing task details, invalid dates,
   missing event markers, invalid task numbers, and extra arguments.
2. Verify each error is reported without terminating Mochi or changing tasks.
3. Place malformed data in a temporary file and verify startup reports the
   problem. Try adding or sorting: changes must be blocked and the original
   file must remain unchanged. Back up and repair the file, then restart.
4. Make the save destination unwritable after startup. Try adding, marking,
   unmarking, deleting, and sorting. Each failed save must leave the visible
   list unchanged. Restore the destination and retry successfully.

## Sorting

Use a temporary data directory to avoid changing personal tasks.

1. Add `todo Zebra`, `deadline apple /by 2026-09-12`, and
   `event Apple /from noon /to evening`; mark task 1.
2. Enter `sort`. Expect apple, Apple, Zebra in that order, with Zebra done.
3. Restart and enter `list`. Expect the same order and task details.
4. Enter `delete 3`. Expect Zebra to be removed.
5. Enter `sort date`. Expect an error with no list changes.
6. Sort an empty list. Expect an empty task listing without an error.
7. Sort twice. Expect the second result to match the first.

The JUnit MochiTest suite covers mixed task sorting, case ties, persistence,
renumbering, empty lists, and rejected arguments.

## Automated checks

Select Java 25, then run:

```text
./gradlew clean test checkstyleMain checkstyleTest shadowJar
```

JUnit covers command recognition, parsing and invalid input, real calendar
dates and event ordering, task display/storage formats, independent copies,
search under a Turkish locale, stable sorting, UTF-8 persistence, corrupt
records, missing files, failed saves, GUI response error flags, and the
console command loop. See `build/reports/tests/test/index.html` for results.

## Desktop acceptance checks

1. Copy `build/libs/mochi.jar` into an empty temporary folder. With Java 25,
   run `java -jar mochi.jar` from that folder.
2. Verify the title bar and header say **Mochi**, the greeting uses its
   personality, and the command field starts focused.
3. Add all three task types, using both Enter and Send. Verify user commands
   are right aligned and bot replies use the available width.
4. Enter `deadline report /by 2026-02-30`. Expect a labelled orange error
   panel. Correct it to `2026-09-18` and verify a normal reply follows.
5. Resize between 420 × 480 and 900 × 800. Long replies must wrap without
   overlapping the input bar or requiring horizontal scrolling.
6. Click Clear. Verify the greeting returns and `list` still shows the tasks.
7. Enter `bye now`: an error appears and the window stays open. Enter `bye`:
   the window closes. Relaunch the JAR and verify tasks persist.
8. Capture one full window, including its title bar, as `docs/Ui.png`.
9. Repeat the JAR workflow on Windows and Linux when those systems are available.
   Record actual results; a macOS test alone does not establish cross-OS success.

## Verification on 2026-09-18

- Java 25.0.3 on macOS x86_64: 126 JUnit test cases passed; both Checkstyle
  tasks and the shadow JAR build passed.
- The JAR launched with `java -jar mochi.jar` from an empty temporary folder.
- A temporary GUI helper exercised the packaged classes through the actual
  JavaFX controls: Enter, Send, Clear, task commands, highlighted errors,
  keyboard focus, resizing from 420 × 480 to 900 × 800, and `bye` passed.
- The GUI checks also passed with the JDK's JavaFX modules excluded using
  `--limit-modules`, verifying that the JAR's bundled JavaFX libraries work.
- `docs/Ui.png` is a screen capture of one complete running Mochi window,
  including the native title bar, task list, input controls, and error panel.
- The existing public User Guide and `Ui.png` URLs returned HTTP 200.
- Windows/Linux GUI smoke tests and checks on other CPU architectures have
  not been performed here.
