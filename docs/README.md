# Es User Guide

Es is a quiet, observant task assistant for the grand library of dreams, Somniareves.

## Starting Es

Run `es.gui.Launcher` from IntelliJ, or use:

```powershell
$env:GRADLE_USER_HOME = "$pwd\.gradle-home"
.\gradlew.bat run
```

The console launcher is `es.Es`.

## Commands

| Command | Example | Purpose |
| --- | --- | --- |
| `todo` | `todo read book` | Add a task without a date |
| `deadline` | `deadline submit report /by 2026-10-15` | Add a task due by a date/time |
| `event` | `event meeting /from 2026-10-15 14:00 /to 2026-10-15 16:00` | Add an event |
| `list` | `list` | Display all tasks |
| `mark` / `unmark` | `mark 2` | Change completion status |
| `delete` | `delete 2` | Remove a task |
| `find` | `find book` | Find whole-word description matches |
| `tag` / `untag` | `tag 1 errand urgent` | Add or remove tags |
| `findtag` | `findtag errand` | Find tasks with a tag |
| `bye` | `bye` | Close the conversation |

### Informational responses

Es gives specific, quiet responses for common edge cases:

- An empty `list` reports that the shelves are empty.
- A `find` or `findtag` search with no matches says that nothing matched.
- Marking an already-complete task reports that it is already marked.
- Unmarking an already-incomplete task reports that it is already unmarked.

## Tags

Tags added during creation require `#`, for example `todo read book #study #quiet`. For `tag`, `untag`, and `findtag`, the `#` is optional. Tags are case-sensitive and duplicates are rejected.

## Dates and conversation

Es accepts dates such as `2026-10-15`, `2026-10-15 18:00`, and `15/10/2026 1800`. Events must end after they begin. Es also understands `hi`, `hello`, `who are you?`, `what are you?`, and `where is this place?`, regardless of letter casing.

## Edge cases

- Commands must use single spaces with no leading or trailing whitespace.
- `todo`, `deadline`, and `event` require a description.
- `deadline` requires `/by`; `event` requires both `/from` and `/to`.
- Dates must exist, and an event cannot end at or before its start.
- Tags must be non-empty, must not contain spaces or `|`, and cannot be duplicated on one task.
- Task numbers must refer to an existing task.
- Duplicate task details are not added again.
- `find` matches whole words in descriptions; `findtag` matches tags exactly, including case.
- Invalid input is reported without terminating the application.

When `bye` is entered in the GUI, Es displays the farewell briefly before closing the window.

Invalid commands, missing arguments, malformed dates, duplicate tags, invalid event periods, and non-existent task numbers are reported without terminating Es. Use single spaces and avoid leading or trailing spaces. The GUI and console use the same command responses.

Tasks are saved automatically in `data/es.txt`.
