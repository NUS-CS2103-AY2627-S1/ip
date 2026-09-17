# Panda Assistant

Panda is a desktop task manager with the personality of a sarcastic kungfu master. It guides a panda trainee through
their tasks as a path toward kungfu mastery, offering questionable but effective advice along the way.

## Prerequisites

- JDK 25
- IntelliJ IDEA (optional)

## Running Panda

Clone the repository:

```sh
git clone https://github.com/kungfuxiongmao/ip.git
cd ip
```

Start the JavaFX graphical interface from the project root:

```sh
./gradlew run
```

On Windows, use `gradlew.bat run` instead.

## Features

Panda can:

- Add to-dos, deadlines, and events.
- Display all tasks while preserving their task numbers.
- Mark, unmark, and delete tasks.
- Find tasks by a case-insensitive whole word or phrase in their descriptions.
- Display deadlines and events that occur on a specified date or on the current date.
- Reject events that overlap existing events, including events marked as done.
- Load tasks when the application starts and save them whenever the task list changes.
- Validate commands and display errors without ending the session.

## Command Reference

Leading and trailing whitespace around a command is ignored. `TASK_NUMBER` is the one-based number shown by `list`.

| Command | Purpose                                                                                        |
| --- |------------------------------------------------------------------------------------------------|
| `todo DESCRIPTION` | Adds a task without a date or time.                                                            |
| `deadline DESCRIPTION /by DATE` | Adds a task due on the given date or date-time.                                                |
| `event DESCRIPTION /from START /to END` | Adds an event with a start and end.<br/> New time period should not clash with stored events.  |
| `list` | Displays every task.                                                                           |
| `mark TASK_NUMBER` | Marks a task as done.                                                                          |
| `unmark TASK_NUMBER` | Marks a task as not done.                                                                      |
| `delete TASK_NUMBER` | Deletes a task and renumbers the remaining tasks.                                              |
| `find KEYWORD` | Displays tasks whose descriptions contain the whole keyword or phrase.                         |
| `today` | Displays deadlines and events that occur today.                                                |
| `display /date DATE` | Displays deadlines and events that occur on the specified date.                                |
| `bye` | Saves the task list and opens the exit-confirmation dialog.                                    |

Dates use `d/M/yyyy`, and date-times use `d/M/yyyy H:mm`. Panda displays them as `d MMM yyyy` and
`d MMM yyyy H:mm`, respectively.

## Event Scheduling

An event's duration must not clash with the duration of any stored event. Its start is inclusive and its end is
exclusive, so adjacent events do not clash.

When a date is entered without a time, Panda treats a `/from` date as starting at `00:00` and a `/to` date as covering
through `23:59`. Internally, the `/to` boundary is `00:00` on the following day so that it remains exclusive while
still reserving the whole date. This is a conservative blocking policy: when the exact times are unknown, Panda blocks
the complete date to prevent another event from being scheduled in a period that may already be occupied.

Panda rejects an event that overlaps any existing event. Its response identifies all conflicting events in
chronological order using the task numbers from the full list. Deleting an event frees its scheduled time. An event is
also rejected if its end is not after its start.

## Storage and Exit Behavior

Panda stores tasks in `data/tasks.txt`, relative to the directory from which the application is run. It loads that file
automatically at startup; if the file does not exist, Panda starts with an empty task list.

Panda saves the task list immediately after a task is added, marked, unmarked, or deleted. Entering `bye` or closing
the window performs a final save before displaying an exit-confirmation dialog. Cancelling the dialog returns to the
application. If the final save fails, Panda reports the error and still lets the user choose whether to exit.

If the save file contains a malformed task, an invalid event range, or overlapping events, Panda reports the problem
and starts with an empty task list.

## Input Validation and Error Handling

Panda validates the command keyword and its arguments before executing the command. It reports unknown commands,
malformed arguments, invalid task numbers, repeated mark or unmark operations, invalid dates, event clashes, and
storage failures in the graphical interface while keeping the application running whenever recovery is possible.

## AI Declaration

AI (Codex) has been used in the development of this project up to level AI-4:

- **Think and compare:** First consider how to complete a task manually, ask AI to complete it, and compare the two
  approaches. AI-generated code is reviewed and modified when necessary to match the intended design.
- Some portions of the code use level AI-3 (**hand-code to start, get AI to finish**): begin with a minimal
  proof-of-concept, then use AI to strengthen it into a fuller implementation, including edge-case handling and tests.
  This approach is generally used when introducing new repository structure so that the developer retains control of
  the core design before AI expands it.

Additionally, AI has been used to generate content, such as the avatars, background pictures, and assist in refining 
documentation.
