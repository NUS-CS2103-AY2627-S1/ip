# Week 2 iP Increments Design

## Goal

Complete the remaining Week 2 iP increments for HABI in the prescribed order:
`Level-4`, `Level-5`, `Level-6`, and `A-Enums`. Also implement and tag the
closely related `A-Inheritance`, `A-Exceptions`, and `A-Collections`
extensions shown inside the current Week 2 requirements.

The implementation must preserve the completed Level-0 through Level-3
behavior, keep source code under `src`, avoid committing generated `.class`
files, and publish each completed increment to the GitHub fork.

## Sources

- [Current Week 2 project requirements](https://nus-cs2103-ay2627-s1.github.io/website/schedule/week2/project.html)
- [Git tagging lesson](https://git-mastery.org/lessons/tag/)

## Scope

### Included

- Three task types: todo, deadline, and event.
- Inheritance and polymorphic storage through `ArrayList<Task>`.
- Validation of all commands available by the end of Level-6.
- A custom HABI-specific exception for invalid user input.
- Task deletion.
- A Java enum where it naturally represents task type.
- Exact console regression scenarios maintained in `test/ui-test-plan.md`.
- A separate commit and lightweight tag for each increment, followed by a
  push to the fork.

### Excluded

- File persistence, date parsing, searching, Gradle, packages, JAR creation,
  and GUI work. These belong to later weeks.
- A full `Parser`, `Ui`, `Storage`, or `TaskList` architecture. Those changes
  belong to later OOP increments and are unnecessary for Week 2.

## Command Syntax

HABI will accept these commands:

```text
todo DESCRIPTION
deadline DESCRIPTION /by DATE_OR_TIME
event DESCRIPTION /from START /to END
list
mark INDEX
unmark INDEX
delete INDEX
bye
```

Dates and times remain strings, as required by Level-4. Leading and trailing
whitespace around the entire input and extracted fields will be ignored.

## Increment Strategy

The code will evolve in small, separately testable steps so that the Git
history demonstrates the requested learning increments.

### Level-4: typed tasks

The existing `Task` representation will first be extended without subclasses.
It will store a one-letter type icon and optional timing text in addition to
the existing description and completion status. `Habi` will recognize valid
`todo`, `deadline`, and `event` commands and format them as:

```text
[T][ ] borrow book
[D][ ] return book (by: Sunday)
[E][ ] project meeting (from: Mon 2pm to: 4pm)
```

This state will be committed and tagged `Level-4`.

### A-Inheritance: polymorphic task classes

The typed-task implementation will then be refactored into:

- `Task`: shared description, completion state, type, and display behavior.
- `Todo`: a task with no timing details.
- `Deadline`: a task with a `by` value.
- `Event`: a task with `from` and `to` values.

All objects will remain in one `ArrayList<Task>`, and task-specific display
details will be supplied polymorphically. This refactor must preserve the
Level-4 console output and will be tagged `A-Inheritance`.

### Level-5: input validation

HABI will validate input before changing the task list. It will report clear,
HABI-specific errors for:

- empty commands and unknown commands;
- empty todo descriptions;
- missing or empty deadline descriptions and `/by` values;
- missing or empty event descriptions, `/from` values, and `/to` values;
- non-integer, zero, negative, and out-of-range task indexes for `mark` and
  `unmark`.

Invalid input must not change the list or terminate the program. The first
working validation implementation will use direct control flow and will be
tagged `Level-5`.

### A-Exceptions: custom exception handling

The same Level-5 behavior will be refactored to throw and catch a checked
`HabiException` for invalid commands. The exception message will contain the
user-facing explanation. This must not change valid-command output or internal
state and will be tagged `A-Exceptions`.

### Level-6 and A-Collections: deletion

`delete INDEX` will remove the selected task, display the removed task, and
report the new task count. Invalid indexes will use the existing exception
path and leave the list unchanged.

The project already uses `ArrayList<Task>`, which satisfies A-Collections and
is also the natural structure for deletion. The deletion commit will receive
both `Level-6` and `A-Collections` lightweight tags.

### A-Enums: task type enum

A `TaskType` enum with `TODO`, `DEADLINE`, and `EVENT` values will replace raw
type strings. Each enum value will own its one-letter display icon. Task
subclasses will pass the appropriate enum value to `Task`. This behavior-
preserving refactor will be tagged `A-Enums`.

## Console Responses

Responses will retain HABI's existing banner, divider, greeting, and farewell.
New successful operations will follow the course examples while retaining the
HABI name. For example:

```text
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 1 task in the list.
```

Deletion will use:

```text
Noted. I've removed this task:
  [D][ ] return book (by: Sunday)
Now you have 0 tasks in the list.
```

Singular and plural task counts will be grammatically correct. Error messages
will identify the invalid field or index and tell the user the expected
command form where that helps correction.

## Data Flow

1. `Habi` reads one input line.
2. It trims the line and identifies the command word.
3. It parses and validates the command's arguments.
4. It creates, reads, updates, or removes a `Task` in `ArrayList<Task>`.
5. It prints one response enclosed by the existing divider.
6. If parsing or validation fails after A-Exceptions, `HabiException` is caught
   at the command-loop boundary and its message is printed without terminating
   the loop.

No command may partly mutate the task list before all its arguments are known
to be valid.

## Testing Strategy

Development will follow red-green-refactor for each behavior:

1. Add or update an exact-output scenario in `test/ui-test-plan.md` and, where
   useful, a focused assertion in `src/test/java/HabiTest.java`.
2. Run the test and confirm it fails for the missing behavior.
3. Add the minimum implementation needed for the increment.
4. Run the full UI suite and the Java test harness.
5. Refactor only while all tests remain green.

The UI plan will cover:

- adding and listing all three task types;
- marking and unmarking typed tasks;
- valid deletion and list re-numbering;
- empty descriptions and missing separators;
- unknown commands;
- malformed and out-of-range indexes;
- invalid commands interleaved with valid commands to prove that errors do not
  corrupt task-list state;
- normal exit.

The project requires Java 25. The current machine exposes only Java 21, so the
implementation phase will first try to use a temporary, non-system-wide Java
25 runtime. Java 21 fallback runs may provide interim feedback but will not be
reported as Java 25 verification.

## Documentation and Code Style

Every new class and each nontrivial public or package-visible method will have
brief explanatory Javadoc. Names will follow the project's existing simple,
introductory Java style. Generated `.class` files will remain ignored.

## Commit, Tag, and Push Sequence

After its tests pass, each row will be committed, tagged with a lightweight
tag, and pushed before the next increment begins:

| Order | Change | Tag(s) |
|---|---|---|
| 1 | Add todo, deadline, and event behavior | `Level-4` |
| 2 | Refactor task types to inheritance | `A-Inheritance` |
| 3 | Handle all current command errors | `Level-5` |
| 4 | Refactor errors to `HabiException` | `A-Exceptions` |
| 5 | Add deletion using `ArrayList<Task>` | `Level-6`, `A-Collections` |
| 6 | Replace raw task types with `TaskType` | `A-Enums` |

The final verification will confirm that `master` is synchronized with
`origin/master`, the working tree is clean, and every listed tag resolves on
the GitHub remote.
