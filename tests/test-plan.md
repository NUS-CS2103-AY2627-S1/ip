# Test Plan

## Automated Tests

The JUnit suite covers command parsing, date parsing, task completion state, date matching,
task-list mutations and searches, chronological sorting, and storage loading and saving.

Run the suite and code-style checks with `./gradlew clean check`.

## GUI Presentation

1. Start the graphical application and enter `list`.
2. Verify that user messages are right-aligned and HSE messages are left-aligned with distinct colors.
3. Enter an invalid command and verify that HSE's error message is displayed with an error color.
4. Resize the window horizontally and vertically.
5. Verify that the transcript grows or shrinks with the window and the input field remains beside the Send button.

## Sorting Tasks

1. Add a deadline dated `2026-09-20`, an event starting `2026-09-15 1400`, and a todo.
2. Enter `sort`.
3. Enter `list`.
4. Verify that the event appears before the deadline and the todo appears last.
5. Restart HSE and enter `list` again to verify that the sorted order was saved.
6. Enter `sort extra` and verify that HSE reports an invalid command.
