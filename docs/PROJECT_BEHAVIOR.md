# Miku project behavior

Miku is a Java 25 JavaFX task tracker with a cheerful, idol-like chat interface.

## Launching and distribution

Run `./gradlew run` on macOS/Linux or `gradlew.bat run` on Windows with Java 25
to open the GUI. Run the corresponding `shadowJar` task to create
`build/libs/miku.jar`, then launch it with `java -jar build/libs/miku.jar`. The
Shadow plugin includes Miku's runtime dependencies in this file. ★

Gradle's development run and test tasks enable Java assertions. To diagnose an
internal invariant in a JAR build, launch it with
`java -ea -jar build/libs/miku.jar`; ordinary `java -jar` launches remain
available for end users. ♪

## GUI behavior

The window opens at 465 × 660 px, matching the portrait background's aspect
ratio, and can be resized. Its chat area shows
`src/main/resources/images/bg.jpg` beneath a light tint so messages stay easy
to read. ☆

Users submit commands with Enter or `Send ♪`. Submitted commands appear in
compact, right-aligned rosy bubbles. Recognized command words and their
applicable syntax markers (`/by`, `/from`, `/to`) appear in teal even when their
arguments are invalid; unknown command words remain plain. Miku's blue replies
appear on the left and expand to fit long text. Errors use the same blue bubble,
with only the leading `OOPS!!!` in bold red. The chat scrolls to the newest
message, and the command field keeps focus after sending. Empty commands display
only Miku's validation error. `bye` shows Miku's farewell, disables input, and
closes the window after one second.

Both speakers have 48 px avatars beside their bubbles, loaded from
`src/main/resources/images/DaUser.jpg` and
`src/main/resources/images/DaMiku.jpg`. If an image is missing, its avatar space
is hidden so the chat remains usable. Chat text uses Trebuchet MS where available
and the platform font otherwise. The input field offers a short example command. ☆

## Commands

- `todo <description>` creates a task without a date or time.
- `deadline <description> /by <date or time>` creates a task with a due date or time.
- `event <description> /from <start> /to <end>` creates a task with a start and end date or time.
- `reschedule <number> /by <new date or time>` changes a deadline's due date or time.
- `reschedule <number> /from <new start> /to <new end>` changes an event's start and end dates or times.
- `list` displays all tasks.
- `find <keyword>` displays tasks whose descriptions contain the keyword, ignoring letter case.
- `mark <number>` and `unmark <number>` update completion status.
- `delete <number>` removes the selected task from the list.
- `bye` closes the application after its farewell is displayed.

Dates use `yyyy-MM-dd` (for example, `2019-10-15`) or `d/M/yyyy` (for example,
`2/12/2019`) and may include a 24-hour time as `yyyy-MM-dd HHmm` or
`d/M/yyyy HHmm`. Date-only values display as `MMM dd yyyy`; date-times display
as `MMM dd yyyy h:mm a`.

An event's end cannot be earlier than its start; events with equal start and end
date-times are allowed. ☆

Only deadlines and events can be rescheduled. A reschedule keeps the task's
description and completion status unchanged. ♪

## Persistence and messages

Miku automatically saves task-list changes to `data/miku.json` as UTF-8 JSON and
loads it at startup. An unreadable or invalid save file produces a friendly chat
warning in a separate Miku bubble and starts with an empty task list. Invalid
commands and task fields show a reply with the highlighted error prefix while
leaving the app ready for the next command.

Tasks display type and completion markers: `[T]`, `[D]`, `[E]`, `[★]`, and `[ ]`.
User-facing messages remain warm and energetic, using symbols such as ★, ☆, ♪,
♫, and ✨ where they improve clarity.

## Verification

Run `./gradlew test checkstyleMain checkstyleTest` on macOS/Linux or
`gradlew.bat test checkstyleMain checkstyleTest` on Windows with Java 25. The
JUnit suite covers core task, parsing, date-time, and GUI-facing response logic.

## Important files

- `src/main/java/miku/Miku.java` — command coordination and response API.
- `src/main/java/miku/ui/MainWindow.java` — GUI controller.
- `src/main/resources/view/` — FXML main window layout.
- `src/main/resources/css/` — responsive Miku styling.
