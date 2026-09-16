# More error handling: acceptance checks

## Commands to try

Start with at least two tasks. Each rejected command must leave the list and saved data unchanged.

| Input | Expected result |
| --- | --- |
| `deadline /by 2026-09-30` | Missing description, rather than missing `/by`. |
| `deadline report /by` | Missing date after `/by`. |
| `deadline report /by 2026-09-30 /by 2026-10-01` | Duplicate `/by`. |
| `event camp /from /to 2026-10-01` | Missing start date. |
| `event camp /from 2026-09-30 /to` | Missing end date. |
| `event camp /from 2026-10-05 /to 2026-10-01` | End date cannot precede start date. |
| `event camp /from 2026-02-30 /to 2026-10-01` | Invalid start date. |
| `mark 1 2` | Neither task is marked; only one number is accepted. |
| `bye extra` | Error; the app stays open. |
| `todo read \| notes` (without the backslash) | Description separator rejected before saving. |

Same-day events, past dates, and duplicate tasks remain allowed. Tabs between command words and arguments work.
Repeated mark/unmark commands report the existing state without saving or celebrating another completion.

## GUI checks

- Submit an invalid date through **Add task**. The form stays open with its fields and a visible error.
- Correct the date and submit again. Exactly one task is added and the form closes.
- Submit an invalid inline tag. The board and editor remain available for correction.
- Submit an invalid typed command. Its text remains in the command field.
- The task board hides after a failed typed command, including immediately after startup. **View tasks** restores it.
- An inline tag error keeps its task board visible so the editor can be corrected.
- Check that storage failures give file recovery advice, rather than asking users to correct their command.

## Automated failure checks

`MoreErrorHandlingTest` uses temporary data and injected save failures. It checks rollback for additions,
mark/unmark, deletion, and tags; invalid saved dates/descriptions/ranges; duplicate markers; and argument errors.
`MainWindowTest` checks correcting an invalid Add-task form without losing its contents.

Never introduce malformed records into your only copy of personal data. For a disposable save file, an invalid
date should produce a load error with its value and line number. Task commands and saving remain blocked until
the file is repaired and the app restarted. The file itself must remain unchanged.
