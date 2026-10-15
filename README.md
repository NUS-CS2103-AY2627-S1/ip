# LuckyNoSlacky

LuckyNoSlacky is a task manager for keeping track of ToDos, deadlines, and
events through the command-line or graphical interface. Tasks are kept in
memory while the application is running and persisted automatically to disk.

## Features

- Add ToDo tasks without a date or time.
- Add Deadline tasks with flexible date/time input.
- Add Event tasks with flexible start and end date/time input.
- List tasks in the order they were added.
- Find tasks by description, date, or both.
- Mark tasks as done or explicitly mark them as not done.
- Delete tasks by their task number.
- Snooze deadlines and events by a duration or explicit end time.
- Reschedule deadlines and events, including partial event rescheduling.
- Accept natural-language and abbreviated snooze durations.
- Validate unsupported slash markers consistently.
- Store up to 100 tasks.
- Save tasks automatically to disk whenever the task list changes.
- Load previously saved tasks automatically when the chatbot starts.

## Getting started

### Prerequisites

- Java Development Kit (JDK) 25.
- IntelliJ IDEA, if you want to run the application using the IDE.
- Gradle is not required separately because the project includes the Gradle
  wrapper.

### Run using IntelliJ IDEA

1. Open IntelliJ IDEA. 
2. Select **Open** and choose the project directory.
3. Configure the project SDK and language level to use JDK 25. See the
   [IntelliJ IDEA JDK instructions](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).
4. Open `src/main/java/luckynoslacky/LuckyNoSlacky.java`.
5. Right-click the file and select **Run `LuckyNoSlacky.main()`**.

The application starts with the following banner:

```text
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
```

After displaying the banner, LuckyNoSlacky greets the user:

```text
Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
```

### Run using the command line

From the project root, run the chatbot with:

```bash
./gradlew run
```

On Windows, use `gradlew.bat run` instead of `./gradlew run`.

### Run the JavaFX GUI

From the project root, launch the graphical interface with:

```bash
./gradlew runGui
```

On Windows, use `gradlew.bat runGui` instead. Ensure that Java 25 is
configured before launching the application.

The GUI uses the same commands as the command-line interface. Enter a command
in the input field and press Enter or click **Send**. Task results are shown as
task cards, while warnings and system errors are displayed separately. After
`bye`, the goodbye message is shown before the GUI closes.

### Run using the released JAR

LuckyNoSlacky is distributed as one cross-platform fat JAR containing the
application and JavaFX runtime dependencies for Windows, macOS, and Linux.
Build it with Java 25:

```bash
./gradlew clean shadowJar
```

Create the release JAR on macOS. The build automatically combines the Intel
and Apple Silicon JavaFX native libraries there, while retaining the Windows
and Linux JavaFX libraries in the same JAR.

The generated file is:

```text
build/libs/luckyNoSlacky.jar
```

Run it with Java 25 from the directory where you want its relative `data/`
folder to be created:

```bash
java -jar luckyNoSlacky.jar
```

This opens the JavaFX GUI without additional dependency or classpath setup.
The relative `data/` folder and `data/luckyNoSlacky.csv` file are created in
the current working directory.

### Task data persistence

LuckyNoSlacky saves the task list automatically after a task is added, marked,
unmarked, deleted, snoozed, or rescheduled. The data is stored in the relative
path `data/luckyNoSlacky.csv` using the following columns:

```text
Task type, isCompleted, Description, startTime, endTime
```

The data directory and CSV file are created automatically when the first task
change is saved. If the data file does not exist or is empty when the chatbot
starts, the chatbot starts with an empty task list. `Task type` is stored as
`T`, `D`, or `E`, and `isCompleted` is stored as `0` or `1`.

The time fields are used as follows:

- ToDos leave both time fields empty.
- Deadlines store the deadline in `endTime`.
- Events store their start time in `startTime` and end time in `endTime`.

Date/time values are saved using `yyyy-MM-dd HH:mm` (for example,
`2030-10-15 14:15`).

Do not edit the data file while LuckyNoSlacky is running.

If the file is malformed or cannot be read,
LuckyNoSlacky displays the following while starting with an empty task list.

```text
Eh you so free ah, no tasks were loaded! If you think this is salah, check your task data file.
```

If a task change cannot be saved, LuckyNoSlacky displays:

```text
Honggan la your system abit rabs ah, I cannot save your task
```

## User guide

Enter one command per line. Commands are case-insensitive, and leading or
trailing spaces are ignored.

| Command               | Format                                                            | Description |
|-----------------------|-------------------------------------------------------------------| --- |
| Create Todo Task      | `todo <description>`                                              | Adds a task without date or time information. |
| Create Deadline Task  | `deadline <description> /by <date/time>`                          | Adds a task with a deadline. |
| Create Event Task     | `event <description> /from <start date/time> /to <end date/time>` | Adds a task with a start and end time. |
| List Tasks            | `list`                                                            | Displays all tasks and their numbers. |
| Mark Task as Done     | `mark <number>`                                                   | Marks the specified task as done. |
| Unmark Task as Undone | `unmark <number>`                                                 | Marks the specified task as not done. |
| Delete Task           | `delete <number>`                                                 | Removes the specified task from the list. |
| Find Tasks             | `find [<description>] [/on <date/time>]`                           | Finds tasks by description, date, or both. |
| Snooze Task            | `snooze <number> [/by <duration>]`                                | Extends a timed task by a duration. |
| Snooze Task            | `snooze <number> [/to <date/time>]`                               | Replaces a timed task's ending time. |
| Reschedule Deadline    | `resched <number> /to <date/time>`                                | Replaces a deadline's date/time. |
| Reschedule Event       | `resched <number> [/from <date/time>] [/to <date/time>]`           | Changes an event's start and/or end time. |
| Exit                  | `bye`                                                             | Exits the chatbot. |

### Adding tasks

To add a ToDo task:

```text
todo borrow book
```

To add a Deadline task:

```text
deadline return book /by 2030-10-15 14:15
```

To add an Event task:

```text
event project meeting /from 2030/10/16 2pm /to 2030-10-16 16:00
```

### Listing tasks

Use `list` to display all tasks:

```text
list
```

Tasks are displayed using a type marker and a completion marker:

```text
1.[📌][❗] borrow book
2.[⏳][✅] return book (by: Tue Oct 15 2030, 2.15pm)
3.[📆][❗] project meeting (from: Wed Oct 16 2030, 2.00pm to: Wed Oct 16 2030, 4.00pm)
```

#### Type markers

- `[📌]` represents a ToDo.
- `[⏳]` represents a Deadline.
- `[📆]` represents an Event.

#### Completion markers

- `[❗]` means the task is not done.
- `[✅]` means the task is done.

### Marking and Unmarking Tasks

Mark and unmark tasks by writing the corresponding command,
followed by the task number based on the output of the `list` command:

```text
mark 1
unmark 1
```

Both commands require exactly one valid task number.

Marking an already completed task keeps it completed. Unmarking an incomplete
task keeps it incomplete. These commands explicitly set the desired status
instead of toggling the current status.

### Deleting tasks

Delete a task by using its number from the `list` output:

```text
delete 2
```

The deleted task is removed from memory, and later tasks are renumbered. The
command requires a valid task number. Failed deletion commands do not
change the task list.

### Finding tasks

Use `find` with a description, a date, or both:

```text
find book
find /on 26 Aug 2026
find book /on 26 Aug 2026
```

Description queries use case-insensitive substring matching. For example,
`find book` matches descriptions such as `read book` and `return book`.

The `/on` tag limits the search to a specified date. It can be used without a
description:

```text
find /on 26 Aug 2026
```

When both filters are provided, a task must satisfy both the description and
date conditions:

```text
find book /on tmr
```

Date-only searches include:

- Deadlines whose due date is the queried date.
- Events whose start and end dates include the queried date.

Description-only searches can match ToDos, Deadlines, and Events,
using the regular task-list header.

If `list` or a `find` query has no matching tasks, LuckyNoSlacky replies:

```text
Chill lah bro got nothing yet lah!
```

### Snoozing tasks

ToDos cannot be snoozed because they do not have time fields.

```text
snooze 2
snooze 2 /by 1.5 hours
snooze 2 /by 1 hour 30 minutes
snooze 2 /by 1 month 2 days
snooze 2 /to tomorrow 5pm
```

The default snooze adds one hour. For deadlines, snoozing changes the
deadline. For events, only the end time changes; the start time is preserved.
Supported duration units include minutes, hours, days, weeks, months, and
years. Abbreviations such as `1h`, `1hr`, `1mo`, and `1yr` are accepted.
Natural-language forms such as `one more week` and `half an hour` are also
supported.

### Accepted duration formats

The duration supplied after `/by` consists of one or more duration components.
Each component has a non-negative number followed by a supported unit. Numbers
may be whole numbers or decimals, and whitespace between the number and unit is
optional:

```text
<duration> ::= <component> [ <component> ... ]
<component> ::= <amount> [whitespace] <unit>
<amount> ::= <digits> | <digits>.<digits>
```

The unit names and abbreviations are case-insensitive:

| Unit | Accepted forms | Decimal amounts |
|------|-----------------|------------------|
| Year | `year`, `years`, `yr`, `yrs` | No |
| Month | `month`, `months`, `mo`, `mos` | No |
| Week | `week`, `weeks` | No |
| Day | `day`, `days`, `d`, `ds` | Yes |
| Hour | `hour`, `hours`, `h`, `hs`, `hr`, `hrs` | Yes |
| Minute | `minute`, `minutes`, `min`, `mins` | Yes |

Multiple components must be separated by whitespace and written in this order:
years, months, weeks, days, hours, then minutes. Each unit may appear at most
once. For example, `1 month 2 days 30 minutes` and `1hr 30mins` are accepted,
but `1 hour 2 hours`, `2 days 1 month`, and `1h30min` are rejected.

Natural-language forms are also accepted:

- The number words `a`, `an`, and `one` through `ten` may be used with full unit
  names, optionally followed by `more`: `a week`, `two days`, and `one more
  week`.
- `half a` or `half an` may be used with minutes, hours, days, or weeks,
  including accepted abbreviations: `half an hour`, `half an hr`, and `half a
  week`. Half-month and half-year values are not supported.
- Decimal amounts are supported for days, hours, and minutes only. Decimal
  weeks, months, and years are rejected. Therefore `1.5 hours` is accepted,
  while `1.5 weeks` and `1.5 months` are rejected.
- Negative amounts, unsupported units, missing amounts or units, and malformed
  combinations are rejected. A duration must contain at least one valid
  component.

After a valid duration, ordinary trailing commentary is allowed, but a
trailing slash marker is rejected. For example, `snooze 1 /by 2 hours please`
is accepted, while `snooze 1 /by 2 hours /please` is rejected.

### Rescheduling tasks

```text
resched 2 /to Friday 6pm
resched 3 /from next Monday 2pm
resched 3 /to Friday 6pm
resched 3 /to Friday 6pm /from next Monday 2pm
```

Event markers may appear in either order. Event start and end times are
validated before the task is changed, and omitted event times remain unchanged.
Past deadlines are rejected. Past event starts and ends are allowed as long as
the event end is not before its start.

### Slash handling

Ordinary slashes are accepted:

```text
todo read/book
todo read / book
deadline report /by 2026/08/26
```

A slash at the beginning of an argument, or after whitespace, followed by a
letter is treated as a command marker. Unsupported markers are rejected:

```text
todo read /book
snooze 1 /by 2 hours /please
```

### Invalid commands

Invalid input produces an explanatory message and does not terminate the
chatbot. Examples include:

- Empty input.
- Unknown commands.
- Missing task descriptions.
- Missing or invalid task numbers.
- Extra arguments after `list` or `bye`.
- Incorrect `/by`, `/from`, or `/to` formats.
- Missing or invalid `/on` formats for `find`.
- Unsupported slash markers in command arguments.
- Invalid date/time values.
- Deadlines in the past or events whose end is before their start.

### Accepted date/time formats

The chatbot accepts the following date forms:

- ISO-style dates: `2030-10-15`, `2030/10/15`.
- Day-first numeric dates: `15/10/2030`, `15-10-2030`; day and month may be
  one or two digits.
- Text dates: `15 Oct 2030`, `15 October 2030`, `Oct 15 2030`,
  `October 15 2030`.
- Dates with weekdays: `Tue Oct 15 2030`, `Tuesday, October 15 2030`; both
  full and three-letter weekday names are accepted.
- A year by itself: `2030`; this resolves to 1 January of that year.
- Dates without a year: `June 6th`; the current year is used unless that date
  has passed, in which case the next year is used.
- A month by itself: `June` or `Jun`; this resolves to the first day of the
  current year, or the next year if that month has passed.
- Named relative dates: `today`, `tomorrow`/`tmr`, and `yesterday`/`ytd`; these
  resolve relative to the current date.
- Days of a month without a month: `the 15th`; the current month is used unless
  that date has passed, in which case the next month is used.
- A weekday alone: `Monday`; this resolves to the next occurrence of Monday.
- Current-week weekdays: `this Monday` through `this Sunday` refer to the
  Monday-to-Sunday week containing today.
- Following-week weekdays: `next Wednesday` refers to the Wednesday in the
  week beginning with the following Sunday. `next next Wednesday` and `the
  following Wednesday` refer to the week after that.
- Upcoming weekdays: `coming Wednesday`, `this coming Wednesday`, and `the
  coming Tuesday` refer to the next occurrence strictly after today.
- Relative months and years use the same offsets: `this month`/`this year`
  means the current period, `next` means the following period, and `next next`
  or `the following` means the period after that.
- A bare day of the month such as `the 15th` resolves to the next available
  15th; `next next 15th` and `the following 15th` skip one additional month.

The chatbot accepts these time forms:

- 24-hour time: `14:15`, `14:15:30`.
- 12-hour time: `2pm`, `2 pm`, `2:15pm`, `2:15 pm`, `2.15pm`, `2.15 pm`, with
  optional seconds such as `2:15:30pm`. Dotted meridiems such as `2 a.m.`
  are also accepted.
- Standalone compact `HHMM` time: `2359`.
- Compact `HHMM` time when it appears where an invalid year would otherwise be
  expected: `25 Aug 0000` means 25 August of the current year at `00:00`.

Four-digit values are interpreted as years first when both interpretations are
valid. Therefore `25 Aug 2030` means the year 2030; use `25 Aug 20:30` when
you mean 8:30pm.

When a time is given without a date, it uses today if that time has not passed,
or tomorrow if it has passed. A date without a time uses `00:00` for an event
start and `23:59` for an event end or deadline. Listed task times use the
format `Tue Oct 15 2030, 2.15pm`.

An ISO date and time may use `T` as the separator, such as
`2030-10-15T2.15pm`. Plain trailing commentary after a valid date/time is
accepted when it does not begin with a slash marker.

For events, a time-only end uses the event start as its reference. If the end
time is later than the start time, it uses the start date; otherwise, it uses
the next date. For example, an event from `25 Aug 2026 11pm` to `1am` ends on
`26 Aug 2026` at `1.00am`. An explicitly supplied end date always takes priority.

### Example session

```text
todo borrow book
  ____________________________________________________________
  Got one more thing to remember ah:
    [📌][❗] borrow book
  Now you got 1 tasks to settle.
  ____________________________________________________________
deadline return book /by 2030-10-15 14:15
  ____________________________________________________________
  Got one more thing to remember ah:
    [⏳][❗] return book (by: Tue Oct 15 2030, 2.15pm)
  Now you got 2 tasks to settle.
  ____________________________________________________________
list
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📌][❗] borrow book
  2.[⏳][❗] return book (by: Tue Oct 15 2030, 2.15pm)
  ____________________________________________________________
mark 1
  ____________________________________________________________
  Swee lah you're done with this task:
    [📌][✅] borrow book
  ____________________________________________________________
unmark 1
  ____________________________________________________________
  Eh salah you're not done with this task ah, must remember to do ah!
    [📌][❗] borrow book
  ____________________________________________________________

bye
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Running the tests

Run the automated tests from the project root with:

```bash
./gradlew test
```

This runs the JUnit tests. The documented command-line integration tests are
excluded from this task and can be run with:

```bash
./gradlew clitest
```

The JavaFX GUI regression tests can be run with:

```bash
./gradlew guiTest
```

To run all three test suites, use:

```bash
./gradlew test clitest guiTest
```

The tests cover task storage, completion state, task subclasses, task parsing,
invalid input, and documented command-line and GUI behavior.

Generate the aggregate JaCoCo coverage report with:

```bash
./gradlew coverageReport
```

The HTML report is generated at
`build/reports/jacoco/coverageReport/html/index.html`. Coverage is currently
reported for review and is not used as a `check` threshold.

Check the aggregate report against the current optional thresholds with:

```bash
./gradlew coverageVerification
```

The thresholds are 90% for lines, 80% for branches, and 95% for methods. This
task is separate from `check` and does not change the CI workflow.

## Development notes

### AI declaration
This project was developed with AI assistance at AI-4 level. 
General ways the AI has been used include:
- I provide the AI with the level requirements and my general action plan, 
  review a more detailed action plan and code snippets, and approve the changes before implementation.
- I detail my intended end result for the AI, and ask it to suggest multiple ways for me to achieve this task.
  I then give further details and ask for a more detailed action plan and code snippets to review,
  for me to approve of the changes before implementation.
