# Wangsa architecture

The desktop and terminal interfaces use the same task logic. A command is parsed
once, passed to the task service, saved if it changes data, and then shown to the user.

## OOP in this project

| Principle | How Wangsa uses it |
| --- | --- |
| Inheritance | `Todo`, `Deadline`, and `Event` extend `Task`, sharing its description and completion state. |
| Polymorphism | `Task.toString()` calls `getDetails()`. Deadlines and events override it to supply their own details. |
| Encapsulation | Task fields and the task collection are private. `TaskList` controls capacity and indexing and returns an unmodifiable copy of its order. The task objects themselves remain mutable. |
| Abstraction | `TaskService` depends on `TaskRepository`, not file operations. `Storage` implements it; tests use fake repositories to simulate saving failures. |

The GUI and CLI compose the same service rather than duplicating saving rules.
Formatting belongs to the interfaces, while validation and task changes stay in
the shared classes. This is enough separation for a small task manager; no extra
framework or database is needed.

## Main classes

| Classes | Responsibility |
| --- | --- |
| `gui.Main`, `Wangsa` | Coordinate desktop and terminal command execution |
| `gui.MainView`, `Ui` | Desktop layout and controls, and terminal input/output |
| `Parser`, `Command` | Check command syntax and carry the parsed arguments |
| `TaskService` | Carry out commands and coordinate saving |
| `TaskList`, `TaskMatch` | Manage order, capacity, search, and task numbers |
| `Task`, `Todo`, `Deadline`, `Event`, `TaskType` | Represent tasks and their details |
| `TaskRepository` | Define the storage operations |
| `Storage` | Read, validate, and save tasks in a plain text file |
| `SaveLocation` | Keep task saves inside the current working folder |
| `gui.ChatMessage`, `gui.MessageIcon`, `gui.QuestIcon` | Render message bubbles and simple speaker symbols |
| `gui.TaskView`, `gui.TaskFormatter` | Lay out task rows and format their literal text |
| `gui.Typography` | Choose an installed font for readable desktop text |
| `CommandHelp`, `gui.HelpView`, `AiHelper` | Share command definitions, show grouped examples, and provide optional AI explanations |
| `gui.Launcher`, `gui.NativeLibraries` | Select the JavaFX native libraries before opening the window |

The interface draws its arrows and compass using JavaFX shapes. There are no
image or audio resources to load.
`gui.Typography` selects an installed font; it does not download or bundle fonts.
Message rows span the window, with Wangsa on the left and the user on the right.
Individual bubbles keep a maximum width so long lines stay readable.
`TaskView` uses a marker column for bullets or task numbers and a content column
for the status, description, and detail lines. Due dates and event times share
the title's left edge, without leading-space padding. Each reply is a snapshot,
so completing a task later does not change an earlier message.

`MainView` owns the header, conversation panel, command composer, and scrolling.
It passes submission, history, and shortcut actions to `Main` through callbacks.
`Main` handles those actions using the parser and task service, then asks the view
to display messages or update the header. This keeps layout code separate from
command execution without introducing an additional framework.

## Input validation

`Parser` checks command names and required markers before creating a task.
For a deadline, `/by` is required; plain `by` is not a marker. `LocalDate` also
checks that the date exists, so `2026-09-31` is rejected even though its format
looks right. Validation happens before task changes or saving.

An invalid command produces an error reply, leaving existing tasks unchanged.
The user can type the corrected command into the same input box. Both Enter and
SEND use the same submission handler.

## Task changes and saving

`TaskList` holds up to 100 valid tasks. Search results keep the numbers used by
the full list. Sorting puts deadlines first and keeps equal-date or undated
tasks in their previous relative order.

`TaskService` reports success only after saving. If an add, mark, unmark, or sort
cannot be saved, it restores the previous in-memory state. A delete is saved
before it is applied to memory.

`Storage` writes the complete list to a temporary UTF-8 file in the save folder,
then replaces `wangsa.txt`. It uses an atomic move where supported, with a regular
replacement as a fallback. Each line records the type, completion flag, description,
and any date or event times. Separators, backslashes, and line breaks are escaped.

Loading validates every line and the 100-task limit. Invalid data is reported
with its line number and file path; it is not silently discarded. The desktop
keeps `help` and `bye` available if loading fails. The terminal exits.

Both interfaces save under the terminal's current folder. When users follow the
quick start, this is the JAR's folder; source-code runs use the project folder.
Unlike v0.7, version 0.8 does not write beside a
JAR launched from a different folder. Existing save files keep the same format.
Old database files are not read, changed, or deleted.

The release launcher extracts JavaFX libraries to `.wangsa/javafx-natives/<platform>`
inside the launch folder. It reuses identical copies and repairs damaged ones,
avoiding repeated writes to DLLs that Windows may lock. Source runs use Gradle's
JavaFX dependencies and do not create this cache.

## Optional AI help

`CommandHelp` holds immutable command groups used by the desktop, terminal, and
AI prompts. `HelpView` lays out those groups as responsive cards, without copying
command definitions into the GUI. `TaskFormatter` keeps readable desktop output
separate from the compact terminal and storage formats.
`AiHelper` sends only that reference and the current question to Groq. It cannot
access saved tasks or execute commands, and it keeps no conversation history.

The desktop runs one request at a time in a background thread; task commands
remain usable. The terminal waits for the response. Requests have a 20-second
timeout and no retries. Missing configuration, empty replies, or provider errors
return offline help. Raw provider errors are not displayed because they may
contain request details.

Tests supply a fake model, so they need no API key or network connection.

## Adding a feature

1. Update the task model if new data is needed.
2. Add syntax and validation in `Parser`, with a matching `Command` type.
3. Put shared behaviour in `TaskService`; use `TaskRepository` for storage.
4. Add tests for valid input, invalid input, and failed saving.
5. Add desktop and terminal output, then update the user guide.

A different storage backend can implement `TaskRepository` without changing the
task service. A new task type should extend `Task` and be supported by the parser
and storage adapter.
