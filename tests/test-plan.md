# Manual test plan

## Add a task to complete within a period

1. Start Floppy with `./gradlew run`.
2. Enter `period collect certificate /from 2026-09-15 /to 2026-09-25`.
3. Verify that Floppy adds a `[P]` task and displays both dates.
4. Restart Floppy and enter `list`.
5. Verify that the period task and both dates were restored.

## Reject a reversed completion period

1. Enter `period collect certificate /from 2026-09-25 /to 2026-09-15`.
2. Verify that Floppy reports that the end date cannot precede the start date.
3. Enter `list` and verify that the invalid task was not added.

## Reject incomplete period commands

1. Enter each of the following commands separately:
   - `period`
   - `period collect certificate`
   - `period collect certificate /from 2026-09-15`
2. Verify that each command displays the required command format and does not
   add a task.

## Responsive and differentiated conversations

1. Resize the Floppy window both wider and taller.
2. Verify that the conversation area and text bubbles grow with the window.
3. Enter `list` and verify that Floppy's response appears on the left with its
   icon, while the user's command appears as a compact bubble on the right.
4. Enter `dance` and verify that the error appears in a distinct red style.

## Reject malformed command words and reserved characters

1. Enter `mark1`, `unmark2`, and `delete3` separately.
2. Verify that each input is rejected instead of being treated as an indexed
   command.
3. Enter `todo read | write`.
4. Verify that Floppy explains that `|` is reserved by the storage format and
   does not add the task.
