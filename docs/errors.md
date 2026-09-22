# Error Catalogue

This file lists all anticipated user-input errors in the current command-line interface. Deadline and event date-time inputs and stored values use the strict `yyyy-MM-dd HH:mm` format; confirmations and lists display them as `MMM dd yyyy HH:mm`.

| Command or condition | Error condition | Exception | User-facing response |
| --- | --- | --- | --- |
| `todo` | Description is missing or blank | `EmptyDescriptionException` | `OOPS!!! This panda needs a todo description before it can get to work.` |
| `deadline` | Description is missing or blank | `EmptyDescriptionException` | `OOPS!!! This panda needs a deadline description before it can get to work.` |
| `deadline` | `/by` information is missing or empty | `MissingDateTimeException` | `OOPS!!! This panda needs more timing details. Try: deadline <description> /by <date or time>.` |
| `deadline` | `/by` is malformed or is not a real date and time | `InvalidDateException` | `OOPS!!! This panda needs a valid date and time in yyyy-MM-dd HH:mm format.` |
| `event` | Description is missing or blank | `EmptyDescriptionException` | `OOPS!!! This panda needs an event description before it can get to work.` |
| `event` | `/from` or `/to` information is missing or empty | `MissingDateTimeException` | `OOPS!!! This panda needs more timing details. Try: event <description> /from <start> /to <end>.` |
| `event` | `/from` or `/to` is malformed or is not a real date and time | `InvalidDateException` | `OOPS!!! This panda needs a valid date and time in yyyy-MM-dd HH:mm format.` |
| `event` | The `/from` date and time is after the `/to` date and time | `InvalidEventPeriodException` | `OOPS!!! This panda needs an event to start no later than it ends.` |
| `list` | Optional date is malformed, impossible, or contains extra arguments | `InvalidDateException` | `OOPS!!! This panda needs a valid list date in yyyy-MM-dd format.` |
| `find` | Search keyword is missing or blank | `EmptySearchTermException` | `OOPS!!! This panda needs a search keyword after find.` |
| `mark`, `unmark`, `update`, or `delete` | Task number is missing or is not an integer | `InvalidTaskNumberException` | `OOPS!!! This panda needs a valid task number after <command>.` |
| `mark`, `unmark`, `update`, or `delete` | Task number is below 1 or greater than the number of stored tasks | `InvalidTaskNumberException` | `OOPS!!! This panda cannot find task <number> in the bamboo stack.` |
| `update` | The update field is missing or malformed, `/name` is blank, an Event timing update omits an endpoint, or a combined update does not put `/name` last | `InvalidUpdateException` | `OOPS!!! This panda needs a valid update. Try /name <new name>, /by <date and time>, or /from <start> /to <end>.` |
| `update` | `/by` targets a non-Deadline, or `/from` and `/to` target a non-Event | `InvalidUpdateException` | `OOPS!!! This panda cannot apply that timing update to this task type.` |
| `update` | A supplied timing is malformed or is not a real date and time | `InvalidDateException` | `OOPS!!! This panda needs a valid date and time in yyyy-MM-dd HH:mm format.` |
| `update` | An Event's new start date and time is after its new end date and time | `InvalidEventPeriodException` | `OOPS!!! This panda needs an event to start no later than it ends.` |
| `bye` | Any arguments follow `bye` | `InvalidCommandException` | `OOPS!!! I'm bamboo-zled; I don't know what that means :-(` |
| Any unsupported command | Command name is not recognized | `InvalidCommandException` | `OOPS!!! I'm bamboo-zled; I don't know what that means :-(` |
| Startup data loading | A stored record has an unknown task type, invalid completion status, missing field, incorrect number of fields, invalid date, or reversed Event period | `DataLoadingException`, `InvalidDateException`, or `InvalidEventPeriodException` handled at the individual-record boundary | Report the applicable error and skip that record while continuing to load valid records. |
| Startup data loading | The configured data file exists but cannot be opened or read | `DataLoadingException` | `OOPS!!! This panda cannot read its bamboo archive at <path>.` |
| Startup data loading | The configured relative data file does not exist | No exception; treated as the first run | Start with an empty task list; create the file and any missing parent folders on the first task-list change. |
| Saving after a task-list change | The data file or its parent directory cannot be created or written | `DataSavingException` | `OOPS!!! This panda could not save its bamboo archive.` The in-memory change remains available for the current session. |
| Input stream closes | No more console input is available | No exception; handled with `Scanner.hasNextLine()` | End the session cleanly and print the farewell message. |

`PandaException` is the checked base class for expected input problems. The UI catches only this base type, allowing unexpected Java runtime and programming errors to remain visible during development.

Task fields use ` | ` as the storage delimiter. A pipe or backslash entered by
the user is prefixed with a backslash in the file and decoded again when loaded.
The default location is the relative, OS-independent path constructed as
`Path.of("data", "info.txt")`.

In the GUI, failed commands remain in the input field for correction. A red input hint accompanies
the original error card; the hint clears after a successful command.
