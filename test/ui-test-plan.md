# UI Test Plan

## Test configuration

- Application: Glennon text UI
- Entry point: `glennon.Glennon`
- Sources: `src/main/java/glennon/*.java`, `src/main/java/glennon/command/*.java`,
  `src/main/java/glennon/exception/*.java`,
  `src/main/java/glennon/task/*.java`, and
  `src/main/java/glennon/util/*.java`
- Required JDK: Zulu OpenJDK `25.0.3.fx-zulu`
- Session isolation: Start each ordinary UI case in a fresh temporary working
  directory so persisted data cannot leak between cases
- Persistence checks: Run both sessions of each persistence case from the same
  temporary working directory, and use a new directory for the next case
- Comparison: Compare each command's complete stdout exactly after normalizing CRLF/CR line endings to LF; treat stderr, timeouts, nonzero exits, and trailing output as failures
- Fixture runner: `python3 test/run_ui_plan.py --plan test/ui-test-plan.md -- <absolute-java> -cp <absolute-classes> glennon.Glennon`
- Storage fault fixtures: block the data path with a directory before the indicated command, then restore the untouched saved file before retrying
- Response boundary: Consider a response complete after 0.25 seconds without stdout or stderr
- Command timeout: 10 seconds

## Expected startup output

```text
____________________________________________________________
+==========================================================+
|                                                          |
|             ________                                     |
|            / ____/ /__  ____  ____  ____  ____           |
|           / / __/ / _ \/ __ \/ __ \/ __ \/ __ \          |
|          / /_/ / /  __/ / / / / / / /_/ / / / /          |
|          \____/_/\___/_/ /_/_/ /_/\____/_/ /_/           |
|                                                          |
+==========================================================+
Hey there! Glennon online.
What's the mission?
____________________________________________________________
```

## Test cases

### TC-001: Exit cleanly

#### Aim

Verify that `bye` prints the sign-off message and ends a newly started session normally.

#### Command 1

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-002: Manage a mission through its lifecycle

#### Aim

Verify that a to-do can be added, listed, marked complete, marked incomplete, and followed by a clean exit in one stateful session.

#### Command 1

##### Input

```text
todo write report
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] write report
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] write report
____________________________________________________________
```

#### Command 3

##### Input

```text
mark 1
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [T][X] write report
____________________________________________________________
```

#### Command 4

##### Input

```text
unmark 1
```

##### Expected output

```text
____________________________________________________________
Mission marked incomplete:
  [T][ ] write report
____________________________________________________________
```

#### Command 5

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-003: Display typed to-dos and deadlines

#### Aim

Verify that `todo` and `deadline` commands create the correct task subclasses, format parsed deadline values, display `[T]` and `[D]` markers, and retain those markers when a task is completed.

#### Command 1

##### Input

```text
todo visit new theme park
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] visit new theme park
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline do homework /by 2/12/2019 1800
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] do homework (by: Dec 2 2019, 6:00 PM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] visit new theme park
2. [D][ ] do homework (by: Dec 2 2019, 6:00 PM)
____________________________________________________________
```

#### Command 4

##### Input

```text
mark 2
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [D][X] do homework (by: Dec 2 2019, 6:00 PM)
____________________________________________________________
```

#### Command 5

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] visit new theme park
2. [D][X] do homework (by: Dec 2 2019, 6:00 PM)
____________________________________________________________
```

#### Command 6

##### Input

```text
mark 1
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [T][X] visit new theme park
____________________________________________________________
```

#### Command 7

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][X] visit new theme park
2. [D][X] do homework (by: Dec 2 2019, 6:00 PM)
____________________________________________________________
```

#### Command 8

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-004: Reject malformed typed tasks

#### Aim

Verify that incomplete `todo` and `deadline` commands show guidance and do not add missions.

#### Command 1

##### Input

```text
todo
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a mission after todo.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Use: deadline <mission> /by <d/M/yyyy [HHmm]>.
____________________________________________________________
```

#### Command 3

##### Input

```text
deadline return book /by
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Use: deadline <mission> /by <d/M/yyyy [HHmm]>.
____________________________________________________________
```

#### Command 4

##### Input

```text
deadline /by Sunday
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Use: deadline <mission> /by <d/M/yyyy [HHmm]>.
____________________________________________________________
```

#### Command 5

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
____________________________________________________________
```

#### Command 6

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-005: Display an event

#### Aim

Verify that `event` creates an `Event`, formats parsed start and end values,
displays its `[E]` marker, and retains the marker after completion.

#### Command 1

##### Input

```text
event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [E][ ] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
____________________________________________________________
```

#### Command 3

##### Input

```text
mark 1
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [E][X] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
____________________________________________________________
```

#### Command 4

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [E][X] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
____________________________________________________________
```

#### Command 5

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-007: Reject unknown commands and invalid mission numbers

#### Aim

Verify that unknown commands, malformed mission numbers, and out-of-range
mission numbers use the exception-backed error response without changing the
mission log.

#### Command 1

##### Input

```text
blah
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Glennon doesn't recognize that command.
Try: todo, deadline, event, list, sort, find, on, mark, unmark, delete, or bye.
____________________________________________________________
```

#### Command 2

##### Input

```text
mark nope
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid mission number.
____________________________________________________________
```

#### Command 3

##### Input

```text
mark 1
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid mission number.
____________________________________________________________
```

#### Command 4

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
____________________________________________________________
```

#### Command 5

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-008: Preserve the mission log across rejected additions

#### Aim

Verify that invalid creation commands interleaved with valid ones do not add,
remove, or reorder missions, and that valid commands still work after errors.

#### Command 1

##### Input

```text
todo calibrate sensors
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] calibrate sensors
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
launch engines
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Glennon doesn't recognize that command.
Try: todo, deadline, event, list, sort, find, on, mark, unmark, delete, or bye.
____________________________________________________________
```

#### Command 3

##### Input

```text
deadline submit report /by
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Use: deadline <mission> /by <d/M/yyyy [HHmm]>.
____________________________________________________________
```

#### Command 4

##### Input

```text
deadline submit report /by 2/12/2019 1800
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] submit report (by: Dec 2 2019, 6:00 PM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 5

##### Input

```text
event team sync /from Monday 2pm /to
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Use: event <mission> /from <d/M/yyyy [HHmm]> /to <d/M/yyyy [HHmm]>.
____________________________________________________________
```

#### Command 6

##### Input

```text
event team sync /from 2/12/2019 1400 /to 2/12/2019 1600
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] team sync (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
Mission log now has 3 missions.
____________________________________________________________
```

#### Command 7

##### Input

```text
todo
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a mission after todo.
____________________________________________________________
```

#### Command 8

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] calibrate sensors
2. [D][ ] submit report (by: Dec 2 2019, 6:00 PM)
3. [E][ ] team sync (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
____________________________________________________________
```

#### Command 9

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-009: Preserve completion state across rejected updates

#### Aim

Verify that invalid mission numbers interleaved with valid mark and unmark
commands do not change the completion state of any mission.

#### Command 1

##### Input

```text
todo first mission
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] first mission
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
todo second mission
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] second mission
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
mark 0
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid mission number.
____________________________________________________________
```

#### Command 4

##### Input

```text
mark 2
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [T][X] second mission
____________________________________________________________
```

#### Command 5

##### Input

```text
mark 3
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid mission number.
____________________________________________________________
```

#### Command 6

##### Input

```text
unmark nope
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid mission number.
____________________________________________________________
```

#### Command 7

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] first mission
2. [T][X] second mission
____________________________________________________________
```

#### Command 8

##### Input

```text
unmark 2
```

##### Expected output

```text
____________________________________________________________
Mission marked incomplete:
  [T][ ] second mission
____________________________________________________________
```

#### Command 9

##### Input

```text
mark 1
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [T][X] first mission
____________________________________________________________
```

#### Command 10

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][X] first mission
2. [T][ ] second mission
____________________________________________________________
```

#### Command 11

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-010: Delete a middle mission

#### Aim

Positively verify that deleting a middle event reports the correct mission,
decrements the count, and renumbers the remaining typed missions.

#### Command 1

##### Input

```text
todo inspect equipment
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] inspect equipment
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
deadline return book /by 2/12/2019 1800
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] return book (by: Dec 2 2019, 6:00 PM)
Mission log now has 3 missions.
____________________________________________________________
```

#### Command 4

##### Input

```text
delete 2
```

##### Expected output

```text
____________________________________________________________
Mission removed:
  [E][ ] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 5

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] inspect equipment
2. [D][ ] return book (by: Dec 2 2019, 6:00 PM)
____________________________________________________________
```

#### Command 6

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-011: Delete the sole completed mission

#### Aim

Positively verify that deleting the only mission preserves its completed
representation and changes the mission log to the empty state.

#### Command 1

##### Input

```text
deadline submit specifications /by 2/12/2019 1800
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] submit specifications (by: Dec 2 2019, 6:00 PM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
mark 1
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [D][X] submit specifications (by: Dec 2 2019, 6:00 PM)
____________________________________________________________
```

#### Command 3

##### Input

```text
delete 1
```

##### Expected output

```text
____________________________________________________________
Mission removed:
  [D][X] submit specifications (by: Dec 2 2019, 6:00 PM)
Mission log now has 0 missions.
____________________________________________________________
```

#### Command 4

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
____________________________________________________________
```

#### Command 5

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-014: Delete the last mission

#### Aim

Positively verify deletion at the highest valid index and confirm that all
earlier missions retain their order and representations.

#### Command 1

##### Input

```text
todo prepare launch
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] prepare launch
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline fuel rocket /by 2/12/2019 1800
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] fuel rocket (by: Dec 2 2019, 6:00 PM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
event launch window /from 2/12/2019 1400 /to 2/12/2019 1600
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] launch window (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
Mission log now has 3 missions.
____________________________________________________________
```

#### Command 4

##### Input

```text
delete 3
```

##### Expected output

```text
____________________________________________________________
Mission removed:
  [E][ ] launch window (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 5

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] prepare launch
2. [D][ ] fuel rocket (by: Dec 2 2019, 6:00 PM)
____________________________________________________________
```

#### Command 6

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-019: Reject deletion indexes outside the valid range

#### Aim

Negatively verify that zero and an index beyond the list size both show
guidance and preserve the existing event.

#### Command 1

##### Input

```text
event preserve window /from 2/12/2019 1400 /to 2/12/2019 1600
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] preserve window (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
delete 0
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid mission number.
____________________________________________________________
```

#### Command 3

##### Input

```text
delete 2
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid mission number.
____________________________________________________________
```

#### Command 4

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [E][ ] preserve window (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
____________________________________________________________
```

#### Command 5

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-021: Preserve symbols in a to-do

#### Aim

Positively verify that a to-do preserves spaces and symbols.

#### Command 1

##### Input

```text
todo buy milk & bread
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] buy milk & bread
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] buy milk & bread
____________________________________________________________
```

#### Command 3

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-022: Trim extra to-do spacing

#### Aim

Positively verify that extra spacing after the keyword is trimmed.

#### Command 1

##### Input

```text
todo    calibrate compass
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] calibrate compass
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] calibrate compass
____________________________________________________________
```

#### Command 3

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-028: Accept leading space before todo

#### Aim

Positively verify leading whitespace is accepted without changing the existing task.

#### Command 1

##### Input

```text
todo retain valid note
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] retain valid note
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
 todo leading space note
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] leading space note
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] retain valid note
2. [T][ ] leading space note
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-031: Format a parsed deadline

#### Aim

Positively verify a multiword parsed deadline value.

#### Command 1

##### Input

```text
deadline finish prototype /by 29/2/2024 0000
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] finish prototype (by: Feb 29 2024, 12:00 AM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [D][ ] finish prototype (by: Feb 29 2024, 12:00 AM)
____________________________________________________________
```

#### Command 3

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-032: Preserve symbols in a deadline

#### Aim

Positively verify symbols and numeric time text in a deadline.

#### Command 1

##### Input

```text
deadline deploy release /by 31/12/9999 2359
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] deploy release (by: Dec 31 9999, 11:59 PM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [D][ ] deploy release (by: Dec 31 9999, 11:59 PM)
____________________________________________________________
```

#### Command 3

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-036: Add a deadline among mixed tasks

#### Aim

Positively verify deadline ordering among to-do and event tasks.

#### Command 1

##### Input

```text
todo open venue
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] open venue
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline confirm caterer /by 2/12/2019 1800
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] confirm caterer (by: Dec 2 2019, 6:00 PM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
event host dinner /from 2/12/2019 1400 /to 2/12/2019 1600
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] host dinner (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
Mission log now has 3 missions.
____________________________________________________________
```

#### Command 4

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] open venue
2. [D][ ] confirm caterer (by: Dec 2 2019, 6:00 PM)
3. [E][ ] host dinner (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
____________________________________________________________
```

#### Command 5

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-042: Preserve symbols in event times

#### Aim

Positively verify ISO-like and numeric event time text.

#### Command 1

##### Input

```text
event system migration /from 28/2/2024 2330 /to 29/2/2024 0030
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] system migration (from: Feb 28 2024, 11:30 PM to: Feb 29 2024, 12:30 AM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [E][ ] system migration (from: Feb 28 2024, 11:30 PM to: Feb 29 2024, 12:30 AM)
____________________________________________________________
```

#### Command 3

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-047: Reject a missing event body

#### Aim

Negatively verify a missing event body while preserving a to-do.

#### Command 1

##### Input

```text
todo preserve route map
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] preserve route map
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
event
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Use: event <mission> /from <d/M/yyyy [HHmm]> /to <d/M/yyyy [HHmm]>.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] preserve route map
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-054: List mixed mission types

#### Aim

Positively verify ordering and representations for all task types.

#### Command 1

##### Input

```text
todo list first item
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] list first item
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline list second item /by 2/12/2019 1800
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] list second item (by: Dec 2 2019, 6:00 PM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
event list third item /from 2/12/2019 1400 /to 2/12/2019 1600
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] list third item (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
Mission log now has 3 missions.
____________________________________________________________
```

#### Command 4

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] list first item
2. [D][ ] list second item (by: Dec 2 2019, 6:00 PM)
3. [E][ ] list third item (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
____________________________________________________________
```

#### Command 5

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-057: Reject arguments after list

#### Aim

Negatively verify that list rejects arguments and preserves state.

#### Command 1

##### Input

```text
todo preserve list argument state
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] preserve list argument state
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
list all
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
The list command does not take arguments. Use: list.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] preserve list argument state
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-069: Reject mark indexes outside the range

#### Aim

Negatively verify zero and excessive indexes while preserving an event.

#### Command 1

##### Input

```text
event preserve ranged mark /from 2/12/2019 1400 /to 2/12/2019 1600
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] preserve ranged mark (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
mark 0
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid mission number.
____________________________________________________________
```

#### Command 3

##### Input

```text
mark 2
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid mission number.
____________________________________________________________
```

#### Command 4

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [E][ ] preserve ranged mark (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
____________________________________________________________
```

#### Command 5

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-079: Reject unmark indexes outside the range

#### Aim

Negatively verify zero and excessive indexes preserve an event.

#### Command 1

##### Input

```text
event preserve ranged unmark /from 2/12/2019 1400 /to 2/12/2019 1600
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] preserve ranged unmark (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
mark 1
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [E][X] preserve ranged unmark (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
____________________________________________________________
```

#### Command 3

##### Input

```text
unmark 0
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid mission number.
____________________________________________________________
```

#### Command 4

##### Input

```text
unmark 2
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid mission number.
____________________________________________________________
```

#### Command 5

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [E][X] preserve ranged unmark (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
____________________________________________________________
```

#### Command 6

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-090: Reject a malformed deadline date-time

#### Aim

Negatively verify that a deadline with the wrong date-time format is rejected
and the existing mission remains unchanged.

#### Command 1

##### Input

```text
todo preserve malformed deadline
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] preserve malformed deadline
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline return book /by 2019-12-02 1800
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid calendar date as d/M/yyyy with an optional HHmm time, for example 2/12/2019 or 2/12/2019 1800.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] preserve malformed deadline
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-095: Reject an impossible event date

#### Aim

Negatively verify strict calendar validation for event boundaries and
preserved state.

#### Command 1

##### Input

```text
todo preserve impossible event
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] preserve impossible event
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
event review /from 29/2/2025 1400 /to 29/2/2025 1600
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid calendar date as d/M/yyyy with an optional HHmm time, for example 2/12/2019 or 2/12/2019 1800.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] preserve impossible event
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-096: Reject equal event timestamps

#### Aim

Negatively verify a zero-duration timed event is rejected and existing tasks are unchanged.

#### Command 1

##### Input

```text
todo preserve equal event range
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] preserve equal event range
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
event instant checkpoint /from 3/12/2019 0900 /to 3/12/2019 0900
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
The event end must be after its start.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] preserve equal event range
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-097: Reject a reversed same-day event

#### Aim

Negatively verify that a same-day event ending before it starts is rejected
and the existing mission remains unchanged.

#### Command 1

##### Input

```text
todo preserve same-day range
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] preserve same-day range
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
event reversed meeting /from 3/12/2019 1600 /to 3/12/2019 1400
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
The event end must be after its start.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] preserve same-day range
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-105: Filter mixed mission types

#### Aim

Positively verify ordering, full-log mission numbering, and exclusion of to-dos and nonmatching dates.

#### Command 1

##### Input

```text
todo buy stationery
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] buy stationery
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline submit report /by 2/12/2019 1800
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] submit report (by: Dec 2 2019, 6:00 PM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
deadline renew pass /by 3/12/2019 0900
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] renew pass (by: Dec 3 2019, 9:00 AM)
Mission log now has 3 missions.
____________________________________________________________
```

#### Command 4

##### Input

```text
event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
Mission log now has 4 missions.
____________________________________________________________
```

#### Command 5

##### Input

```text
on 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Missions on Dec 2 2019:
2. [D][ ] submit report (by: Dec 2 2019, 6:00 PM)
4. [E][ ] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
____________________________________________________________
```

#### Command 6

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-109: Reject an impossible filter date

#### Aim

Negatively verify strict calendar validation and preserved state.

#### Command 1

##### Input

```text
event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
on 29/2/2025
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid calendar date as d/M/yyyy, for example 2/12/2019.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [E][ ] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-110: Find a keyword across task types

#### Aim

Positively verify that `find` returns matching to-dos, deadlines, and events in
their original order while preserving completion status.

#### Command 1

##### Input

```text
todo read book
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] read book
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline return book /by 6/6/2026 1800
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] return book (by: Jun 6 2026, 6:00 PM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
event book club /from 7/6/2026 1400 /to 7/6/2026 1600
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] book club (from: Jun 7 2026, 2:00 PM to: Jun 7 2026, 4:00 PM)
Mission log now has 3 missions.
____________________________________________________________
```

#### Command 4

##### Input

```text
todo buy milk
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] buy milk
Mission log now has 4 missions.
____________________________________________________________
```

#### Command 5

##### Input

```text
mark 1
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [T][X] read book
____________________________________________________________
```

#### Command 6

##### Input

```text
find book
```

##### Expected output

```text
____________________________________________________________
Matching missions located:
1. [T][X] read book
2. [D][ ] return book (by: Jun 6 2026, 6:00 PM)
3. [E][ ] book club (from: Jun 7 2026, 2:00 PM to: Jun 7 2026, 4:00 PM)
____________________________________________________________
```

#### Command 7

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-111: Find a substring inside a description

#### Aim

Positively verify that a keyword can match a substring rather than a complete
word.

#### Command 1

##### Input

```text
todo write report
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] write report
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
todo call teammate
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] call teammate
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
find port
```

##### Expected output

```text
____________________________________________________________
Matching missions located:
1. [T][ ] write report
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-112: Find a multi-word keyword

#### Aim

Positively verify that `find` accepts a multi-word keyword and matches the
complete phrase.

#### Command 1

##### Input

```text
todo read project book
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] read project book
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
todo review project plan
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] review project plan
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
find project book
```

##### Expected output

```text
____________________________________________________________
Matching missions located:
1. [T][ ] read project book
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-113: Find a keyword containing symbols

#### Aim

Positively verify that symbol characters are treated as ordinary searchable
description text.

#### Command 1

##### Input

```text
todo read C++ book
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] read C++ book
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
find C++
```

##### Expected output

```text
____________________________________________________________
Matching missions located:
1. [T][ ] read C++ book
____________________________________________________________
```

#### Command 3

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-114: Find no matching tasks

#### Aim

Positively verify that a valid search with no matches prints an empty result
and leaves the mission log unchanged.

#### Command 1

##### Input

```text
todo wash car
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] wash car
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
find book
```

##### Expected output

```text
____________________________________________________________
Matching missions located:
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] wash car
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-115: Match descriptions case-sensitively

#### Aim

Positively verify the established case-sensitive parsing policy also applies
to description keywords.

#### Command 1

##### Input

```text
todo Read Book
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] Read Book
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
todo read book
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] read book
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
find book
```

##### Expected output

```text
____________________________________________________________
Matching missions located:
2. [T][ ] read book
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-116: Reflect status changes in repeated searches

#### Aim

Positively verify repeated searches are read-only and reflect a status change
made between searches.

#### Command 1

##### Input

```text
todo book flight
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] book flight
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
find book
```

##### Expected output

```text
____________________________________________________________
Matching missions located:
1. [T][ ] book flight
____________________________________________________________
```

#### Command 3

##### Input

```text
mark 1
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [T][X] book flight
____________________________________________________________
```

#### Command 4

##### Input

```text
find book
```

##### Expected output

```text
____________________________________________________________
Matching missions located:
1. [T][X] book flight
____________________________________________________________
```

#### Command 5

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-117: Reject a missing find keyword

#### Aim

Negatively verify that `find` without a keyword shows guidance and preserves
the existing mission.

#### Command 1

##### Input

```text
todo preserve missing search
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] preserve missing search
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
find
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a keyword after find.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] preserve missing search
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-118: Reject a wrongly capitalized find command

#### Aim

Negatively verify that `Find` is rejected under the command parser's
case-sensitive policy and leaves state unchanged.

#### Command 1

##### Input

```text
todo preserve command case
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] preserve command case
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
Find command
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Glennon doesn't recognize that command.
Try: todo, deadline, event, list, sort, find, on, mark, unmark, delete, or bye.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] preserve command case
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-119: Accept leading whitespace before find

#### Aim

Positively verify a search accepts surrounding whitespace and preserves the log.

#### Command 1

##### Input

```text
todo preserve leading search
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] preserve leading search
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
 find leading
```

##### Expected output

```text
____________________________________________________________
Matching missions located:
1. [T][ ] preserve leading search
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] preserve leading search
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-120: Add a date-only deadline

#### Aim

Verify that an ordinary date-only deadline defaults to 11:59 PM.

#### Command 1

##### Input

```text
deadline submit report /by 2/12/2026
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] submit report (by: Dec 2 2026, 11:59 PM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-121: Filter a leap-day date-only deadline

#### Aim

Verify the leap-day boundary and interaction between date-only deadlines and date filtering.

#### Command 1

##### Input

```text
deadline renew passport /by 29/2/2028
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] renew passport (by: Feb 29 2028, 11:59 PM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
on 29/2/2028
```

##### Expected output

```text
____________________________________________________________
Missions on Feb 29 2028:
1. [D][ ] renew passport (by: Feb 29 2028, 11:59 PM)
____________________________________________________________
```

#### Command 3

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-122: Add a single-day all-day event

#### Aim

Verify that equal date-only event boundaries produce a single all-day event.

#### Command 1

##### Input

```text
event orientation /from 3/1/2027 /to 3/1/2027
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] orientation (all day: Jan 3 2027 to: Jan 3 2027)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-123: Filter a multi-day all-day event

#### Aim

Verify that a date-only event includes an intermediate date in its all-day range.

#### Command 1

##### Input

```text
event conference /from 4/3/2027 /to 6/3/2027
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] conference (all day: Mar 4 2027 to: Mar 6 2027)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
on 5/3/2027
```

##### Expected output

```text
____________________________________________________________
Missions on Mar 5 2027:
1. [E][ ] conference (all day: Mar 4 2027 to: Mar 6 2027)
____________________________________________________________
```

#### Command 3

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-124: Mix a date-only start with a timed end

#### Aim

Verify that an omitted start time defaults to midnight while an explicit end time is retained.

#### Command 1

##### Input

```text
event launch day /from 7/4/2027 /to 7/4/2027 1800
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] launch day (from: Apr 7 2027, 12:00 AM to: Apr 7 2027, 6:00 PM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-125: Mix a timed start with a date-only end

#### Aim

Verify that an omitted end time covers the remainder of the date while retaining an explicit start time.

#### Command 1

##### Input

```text
event study session /from 8/5/2027 0900 /to 8/5/2027
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] study session (from: May 8 2027, 9:00 AM to: May 8 2027, 11:59 PM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-126: List date-only and timed schedules together

#### Aim

Verify backward compatibility by listing date-only and explicit-time tasks together.

#### Command 1

##### Input

```text
deadline file taxes /by 9/6/2027
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] file taxes (by: Jun 9 2027, 11:59 PM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
event call adviser /from 9/6/2027 1030 /to 9/6/2027 1100
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] call adviser (from: Jun 9 2027, 10:30 AM to: Jun 9 2027, 11:00 AM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [D][ ] file taxes (by: Jun 9 2027, 11:59 PM)
2. [E][ ] call adviser (from: Jun 9 2027, 10:30 AM to: Jun 9 2027, 11:00 AM)
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-127: Reject a malformed date-only deadline

#### Aim

Verify that the wrong date representation is rejected without changing the mission log.

#### Command 1

##### Input

```text
deadline invalid format /by 2027-07-10
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid calendar date as d/M/yyyy with an optional HHmm time, for example 2/12/2019 or 2/12/2019 1800.
____________________________________________________________
```

#### Command 2

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
____________________________________________________________
```

#### Command 3

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-128: Reject an impossible date-only event

#### Aim

Verify that an impossible calendar date is rejected without changing the mission log.

#### Command 1

##### Input

```text
event impossible /from 31/4/2027 /to 1/5/2027
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid calendar date as d/M/yyyy with an optional HHmm time, for example 2/12/2019 or 2/12/2019 1800.
____________________________________________________________
```

#### Command 2

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
____________________________________________________________
```

#### Command 3

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-129: Reject a reversed date-only event

#### Aim

Verify that a reversed all-day range is rejected without changing the mission log.

#### Command 1

##### Input

```text
event backwards /from 12/8/2027 /to 11/8/2027
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
The event end must be after its start.
____________________________________________________________
```

#### Command 2

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
____________________________________________________________
```

#### Command 3

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-130: Sort mixed missions chronologically

#### Aim

Verify that sorting orders events by start time and deadlines by due time,
then places unscheduled to-dos after all scheduled missions.

#### Command 1

##### Input

```text
todo unscheduled patrol
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] unscheduled patrol
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline late report /by 3/9/2026 1800
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] late report (by: Sep 3 2026, 6:00 PM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
event early briefing /from 1/9/2026 0800 /to 1/9/2026 0900
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] early briefing (from: Sep 1 2026, 8:00 AM to: Sep 1 2026, 9:00 AM)
Mission log now has 3 missions.
____________________________________________________________
```

#### Command 4

##### Input

```text
deadline middle report /by 2/9/2026 1200
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] middle report (by: Sep 2 2026, 12:00 PM)
Mission log now has 4 missions.
____________________________________________________________
```

#### Command 5

##### Input

```text
sort
```

##### Expected output

```text
____________________________________________________________
Mission log sorted chronologically:
1. [E][ ] early briefing (from: Sep 1 2026, 8:00 AM to: Sep 1 2026, 9:00 AM)
2. [D][ ] middle report (by: Sep 2 2026, 12:00 PM)
3. [D][ ] late report (by: Sep 3 2026, 6:00 PM)
4. [T][ ] unscheduled patrol
____________________________________________________________
```

#### Command 6

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-131: Keep equal schedule times stable

#### Aim

Verify that missions with the same chronological key retain their previous
relative order.

#### Command 1

##### Input

```text
deadline first tie /by 2/9/2026 0900
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] first tie (by: Sep 2 2026, 9:00 AM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
event second tie /from 2/9/2026 0900 /to 2/9/2026 1000
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] second tie (from: Sep 2 2026, 9:00 AM to: Sep 2 2026, 10:00 AM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
deadline earliest /by 1/9/2026 1800
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] earliest (by: Sep 1 2026, 6:00 PM)
Mission log now has 3 missions.
____________________________________________________________
```

#### Command 4

##### Input

```text
sort
```

##### Expected output

```text
____________________________________________________________
Mission log sorted chronologically:
1. [D][ ] earliest (by: Sep 1 2026, 6:00 PM)
2. [D][ ] first tie (by: Sep 2 2026, 9:00 AM)
3. [E][ ] second tie (from: Sep 2 2026, 9:00 AM to: Sep 2 2026, 10:00 AM)
____________________________________________________________
```

#### Command 5

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-132: Sort an empty mission log

#### Aim

Verify that sorting an empty log succeeds and displays an empty sorted result.

#### Command 1

##### Input

```text
sort
```

##### Expected output

```text
____________________________________________________________
Mission log sorted chronologically:
____________________________________________________________
```

#### Command 2

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-133: Sort one unscheduled mission

#### Aim

Verify the one-item boundary when the only mission is an unscheduled to-do.

#### Command 1

##### Input

```text
todo only unscheduled mission
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] only unscheduled mission
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
sort
```

##### Expected output

```text
____________________________________________________________
Mission log sorted chronologically:
1. [T][ ] only unscheduled mission
____________________________________________________________
```

#### Command 3

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-134: Preserve an already sorted log

#### Aim

Verify that sorting is idempotent when scheduled missions and a trailing
to-do are already in chronological order.

#### Command 1

##### Input

```text
event first event /from 1/10/2026 0800 /to 1/10/2026 0900
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] first event (from: Oct 1 2026, 8:00 AM to: Oct 1 2026, 9:00 AM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline second deadline /by 2/10/2026 1800
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] second deadline (by: Oct 2 2026, 6:00 PM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
todo trailing todo
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] trailing todo
Mission log now has 3 missions.
____________________________________________________________
```

#### Command 4

##### Input

```text
sort
```

##### Expected output

```text
____________________________________________________________
Mission log sorted chronologically:
1. [E][ ] first event (from: Oct 1 2026, 8:00 AM to: Oct 1 2026, 9:00 AM)
2. [D][ ] second deadline (by: Oct 2 2026, 6:00 PM)
3. [T][ ] trailing todo
____________________________________________________________
```

#### Command 5

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-135: Preserve completion status while sorting

#### Aim

Verify that sorting moves each whole mission without changing its completion
status.

#### Command 1

##### Input

```text
deadline completed later /by 3/11/2026 1800
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] completed later (by: Nov 3 2026, 6:00 PM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline pending earlier /by 1/11/2026 0900
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] pending earlier (by: Nov 1 2026, 9:00 AM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
mark 1
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [D][X] completed later (by: Nov 3 2026, 6:00 PM)
____________________________________________________________
```

#### Command 4

##### Input

```text
sort
```

##### Expected output

```text
____________________________________________________________
Mission log sorted chronologically:
1. [D][ ] pending earlier (by: Nov 1 2026, 9:00 AM)
2. [D][X] completed later (by: Nov 3 2026, 6:00 PM)
____________________________________________________________
```

#### Command 5

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-136: Sort date-only and timed missions

#### Aim

Verify chronological sorting uses the existing default time for a date-only
deadline when compared with a timed event on the same date.

#### Command 1

##### Input

```text
deadline end of day /by 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] end of day (by: Dec 2 2019, 11:59 PM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
event morning session /from 2/12/2019 0900 /to 2/12/2019 1000
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] morning session (from: Dec 2 2019, 9:00 AM to: Dec 2 2019, 10:00 AM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
sort
```

##### Expected output

```text
____________________________________________________________
Mission log sorted chronologically:
1. [E][ ] morning session (from: Dec 2 2019, 9:00 AM to: Dec 2 2019, 10:00 AM)
2. [D][ ] end of day (by: Dec 2 2019, 11:59 PM)
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-137: Reject arguments after sort

#### Aim

Verify that unsupported sort arguments are rejected and the mission log
remains unchanged.

#### Command 1

##### Input

```text
todo preserve after sort arguments
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] preserve after sort arguments
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
sort date
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
The sort command does not take arguments. Use: sort.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] preserve after sort arguments
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-138: Reject a wrongly capitalized sort command

#### Aim

Verify that command keywords remain case-sensitive and rejection preserves
the mission log.

#### Command 1

##### Input

```text
todo preserve after capital sort
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] preserve after capital sort
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
Sort
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Glennon doesn't recognize that command.
Try: todo, deadline, event, list, sort, find, on, mark, unmark, delete, or bye.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] preserve after capital sort
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-139: Accept leading whitespace before sort

#### Aim

Positively verify sorting accepts leading whitespace and preserves task details.

#### Command 1

##### Input

```text
todo preserve after spaced sort
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] preserve after spaced sort
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
 sort
```

##### Expected output

```text
____________________________________________________________
Mission log sorted chronologically:
1. [T][ ] preserve after spaced sort
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] preserve after spaced sort
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-140: Accept a tab after todo

#### Aim

Positively verify pasted tabs separate a command from its description while internal spacing and symbols remain intact.

#### Command 1

##### Input

```text
	todo	read  C++/Java 新加坡  
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] read  C++/Java 新加坡
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] read  C++/Java 新加坡
____________________________________________________________
```

#### Command 3

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-141: Accept spaced deadline values

#### Aim

Positively verify repeated spaces around a date-time and tabs around the deadline marker.

#### Command 1

##### Input

```text
  deadline   report	/by	2/12/2019   1800  
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] report (by: Dec 2 2019, 6:00 PM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [D][ ] report (by: Dec 2 2019, 6:00 PM)
____________________________________________________________
```

#### Command 3

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-142: Accept tab-separated event fields

#### Aim

Positively verify tabs between event markers, dates, and times with a multi-day range.

#### Command 1

##### Input

```text
event	flight	/from	2/12/2019	2300	/to	3/12/2019	0100
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] flight (from: Dec 2 2019, 11:00 PM to: Dec 3 2019, 1:00 AM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [E][ ] flight (from: Dec 2 2019, 11:00 PM to: Dec 3 2019, 1:00 AM)
____________________________________________________________
```

#### Command 3

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-143: Accept surrounding spaces on commands without arguments

#### Aim

Positively verify list, sort, and bye tolerate trailing and leading whitespace on an empty log.

#### Command 1

##### Input

```text
 list  
```

##### Expected output

```text
____________________________________________________________
Mission log:
____________________________________________________________
```

#### Command 2

##### Input

```text
	sort	
```

##### Expected output

```text
____________________________________________________________
Mission log sorted chronologically:
____________________________________________________________
```

#### Command 3

##### Input

```text
 bye  
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-144: Accept whitespace during task updates

#### Aim

Positively verify spaced and tab-separated indices preserve the mark, unmark, and delete lifecycle.

#### Command 1

##### Input

```text
todo spacing lifecycle
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] spacing lifecycle
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
  mark	1  
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [T][X] spacing lifecycle
____________________________________________________________
```

#### Command 3

##### Input

```text
 unmark   1 
```

##### Expected output

```text
____________________________________________________________
Mission marked incomplete:
  [T][ ] spacing lifecycle
____________________________________________________________
```

#### Command 4

##### Input

```text
 delete	1 
```

##### Expected output

```text
____________________________________________________________
Mission removed:
  [T][ ] spacing lifecycle
Mission log now has 0 missions.
____________________________________________________________
```

#### Command 5

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
____________________________________________________________
```

#### Command 6

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-145: Reject empty and whitespace-only commands

#### Aim

Negatively verify both blank representations give actionable guidance and preserve an existing mission.

#### Command 1

##### Input

```text
todo keep blank input
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] keep blank input
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text

```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a command.
____________________________________________________________
```

#### Command 3

##### Input

```text
  	 
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a command.
____________________________________________________________
```

#### Command 4

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] keep blank input
____________________________________________________________
```

#### Command 5

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-146: Reject arguments after bye without exiting

#### Aim

Negatively verify a malformed exit command leaves the session and log available.

#### Command 1

##### Input

```text
todo keep malformed exit
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] keep malformed exit
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
bye now
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
The bye command does not take arguments. Use: bye.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] keep malformed exit
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-147: Reject malformed and overflowing mission numbers

#### Aim

Negatively verify signed, Unicode, overflowing, and multiple index arguments preserve the log.

#### Command 1

##### Input

```text
todo keep strict index
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] keep strict index
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
mark +1
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid mission number.
____________________________________________________________
```

#### Command 3

##### Input

```text
unmark １
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid mission number.
____________________________________________________________
```

#### Command 4

##### Input

```text
delete 2147483648
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid mission number.
____________________________________________________________
```

#### Command 5

##### Input

```text
mark -2147483648
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid mission number.
____________________________________________________________
```

#### Command 6

##### Input

```text
delete 1 2
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid mission number.
____________________________________________________________
```

#### Command 7

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] keep strict index
____________________________________________________________
```

#### Command 8

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-148: Reject repeated /by

#### Aim

Negatively verify a repeated scheduling parameter is identified and preserves prior tasks.

#### Command 1

##### Input

```text
todo keep repeated by
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] keep repeated by
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline report /by 2/12/2019 /by 3/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please specify /by only once.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] keep repeated by
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-149: Reject repeated /from

#### Aim

Negatively verify a repeated scheduling parameter is identified and preserves prior tasks.

#### Command 1

##### Input

```text
todo keep repeated from
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] keep repeated from
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
event briefing /from 2/12/2019 /from 3/12/2019 /to 4/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please specify /from only once.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] keep repeated from
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-150: Reject repeated /to

#### Aim

Negatively verify a repeated scheduling parameter is identified and preserves prior tasks.

#### Command 1

##### Input

```text
todo keep repeated to
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] keep repeated to
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
event briefing /from 2/12/2019 /to 3/12/2019 /to 4/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please specify /to only once.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] keep repeated to
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-151: Reject parameters belonging to another task type

#### Aim

Negatively verify known scheduling flags cannot silently become description text for the wrong task type.

#### Command 1

##### Input

```text
todo keep unexpected flag
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] keep unexpected flag
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline report /from 2/12/2019 /by 3/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Unexpected parameter /from. Use: deadline <mission> /by <d/M/yyyy [HHmm]>.
____________________________________________________________
```

#### Command 3

##### Input

```text
event briefing /by 2/12/2019 /from 2/12/2019 /to 3/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Unexpected parameter /by. Use: event <mission> /from <d/M/yyyy [HHmm]> /to <d/M/yyyy [HHmm]>.
____________________________________________________________
```

#### Command 4

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] keep unexpected flag
____________________________________________________________
```

#### Command 5

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-152: Reject missing and reversed scheduling markers

#### Aim

Negatively verify missing values and incorrectly ordered event markers preserve the log.

#### Command 1

##### Input

```text
todo keep marker order
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] keep marker order
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
event briefing /to 3/12/2019 /from 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Use: event <mission> /from <d/M/yyyy [HHmm]> /to <d/M/yyyy [HHmm]>.
____________________________________________________________
```

#### Command 3

##### Input

```text
event briefing /from /to 3/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Use: event <mission> /from <d/M/yyyy [HHmm]> /to <d/M/yyyy [HHmm]>.
____________________________________________________________
```

#### Command 4

##### Input

```text
deadline report /by
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Use: deadline <mission> /by <d/M/yyyy [HHmm]>.
____________________________________________________________
```

#### Command 5

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] keep marker order
____________________________________________________________
```

#### Command 6

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-160: Allow matching descriptions across task types

#### Aim

Positively verify task type distinguishes missions with the same description.

#### Command 1

##### Input

```text
todo briefing
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] briefing
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline briefing /by 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] briefing (by: Dec 2 2019, 11:59 PM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
event briefing /from 2/12/2019 /to 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] briefing (all day: Dec 2 2019 to: Dec 2 2019)
Mission log now has 3 missions.
____________________________________________________________
```

#### Command 4

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] briefing
2. [D][ ] briefing (by: Dec 2 2019, 11:59 PM)
3. [E][ ] briefing (all day: Dec 2 2019 to: Dec 2 2019)
____________________________________________________________
```

#### Command 5

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-161: Allow recurring deadlines on different dates

#### Aim

Positively verify the deadline date distinguishes repeated descriptions.

#### Command 1

##### Input

```text
deadline weekly report /by 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] weekly report (by: Dec 2 2019, 11:59 PM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline weekly report /by 9/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] weekly report (by: Dec 9 2019, 11:59 PM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [D][ ] weekly report (by: Dec 2 2019, 11:59 PM)
2. [D][ ] weekly report (by: Dec 9 2019, 11:59 PM)
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-162: Allow event ranges with different end times

#### Aim

Positively verify an event end time is part of its identity.

#### Command 1

##### Input

```text
event meeting /from 2/12/2019 0900 /to 2/12/2019 1000
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] meeting (from: Dec 2 2019, 9:00 AM to: Dec 2 2019, 10:00 AM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
event meeting /from 2/12/2019 0900 /to 2/12/2019 1100
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] meeting (from: Dec 2 2019, 9:00 AM to: Dec 2 2019, 11:00 AM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [E][ ] meeting (from: Dec 2 2019, 9:00 AM to: Dec 2 2019, 10:00 AM)
2. [E][ ] meeting (from: Dec 2 2019, 9:00 AM to: Dec 2 2019, 11:00 AM)
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-163: Preserve case-sensitive task descriptions

#### Aim

Positively verify descriptions differing only in letter case remain distinct.

#### Command 1

##### Input

```text
todo Read Book
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] Read Book
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
todo read book
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] read book
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] Read Book
2. [T][ ] read book
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-164: Allow re-adding a deleted task

#### Aim

Positively verify deletion frees the same task details for a subsequent addition.

#### Command 1

##### Input

```text
todo repeat after deletion
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] repeat after deletion
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
delete 1
```

##### Expected output

```text
____________________________________________________________
Mission removed:
  [T][ ] repeat after deletion
Mission log now has 0 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
todo repeat after deletion
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] repeat after deletion
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 4

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] repeat after deletion
____________________________________________________________
```

#### Command 5

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-165: Preserve significant internal description spacing

#### Aim

Positively verify internal spaces and punctuation in Unicode descriptions remain significant.

#### Command 1

##### Input

```text
todo 学习 C++/Java
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] 学习 C++/Java
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
todo 学习  C++/Java
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] 学习  C++/Java
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] 学习 C++/Java
2. [T][ ] 学习  C++/Java
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-166: Allow event ranges with different start times

#### Aim

Positively verify event start times distinguish otherwise matching tasks.

#### Command 1

##### Input

```text
event workshop /from 2/12/2019 0800 /to 2/12/2019 1100
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] workshop (from: Dec 2 2019, 8:00 AM to: Dec 2 2019, 11:00 AM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
event workshop /from 2/12/2019 0900 /to 2/12/2019 1100
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] workshop (from: Dec 2 2019, 9:00 AM to: Dec 2 2019, 11:00 AM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [E][ ] workshop (from: Dec 2 2019, 8:00 AM to: Dec 2 2019, 11:00 AM)
2. [E][ ] workshop (from: Dec 2 2019, 9:00 AM to: Dec 2 2019, 11:00 AM)
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-167: Reject duplicate pending and completed to-dos

#### Aim

Negatively verify surrounding spaces and completion state do not bypass duplicate detection.

#### Command 1

##### Input

```text
todo unique mission
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] unique mission
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
todo   unique mission  
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
That mission already exists in the log.
____________________________________________________________
```

#### Command 3

##### Input

```text
mark 1
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [T][X] unique mission
____________________________________________________________
```

#### Command 4

##### Input

```text
todo unique mission
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
That mission already exists in the log.
____________________________________________________________
```

#### Command 5

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][X] unique mission
____________________________________________________________
```

#### Command 6

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-168: Reject equivalent date-only and timed deadlines

#### Aim

Negatively verify date-only and explicit end-of-day values denote the same deadline.

#### Command 1

##### Input

```text
deadline unique deadline /by 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] unique deadline (by: Dec 2 2019, 11:59 PM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline unique deadline /by 2/12/2019 2359
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
That mission already exists in the log.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [D][ ] unique deadline (by: Dec 2 2019, 11:59 PM)
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-169: Reject duplicate events

#### Aim

Negatively verify identical event ranges are rejected without losing existing tasks.

#### Command 1

##### Input

```text
event unique event /from 2/12/2019 /to 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] unique event (all day: Dec 2 2019 to: Dec 2 2019)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
event unique event /from 2/12/2019 /to 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
That mission already exists in the log.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [E][ ] unique event (all day: Dec 2 2019 to: Dec 2 2019)
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-180: Recover from a failed add save

#### Aim

Negatively verify a real filesystem save failure preserves memory and disk; positively verify a retry applies add exactly once.

- Block storage before command: 2
- Restore storage before command: 4

#### Command 1

##### Input

```text
todo saved mission
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] saved mission
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
todo added after retry
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Glennon could not save the mission data.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] saved mission
____________________________________________________________
```

#### Command 4

##### Input

```text
todo added after retry
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] added after retry
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 5

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] saved mission
2. [T][ ] added after retry
____________________________________________________________
```

#### Command 6

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-181: Recover from a failed delete save

#### Aim

Negatively verify a real filesystem save failure preserves memory and disk; positively verify a retry applies delete exactly once.

- Block storage before command: 3
- Restore storage before command: 5

#### Command 1

##### Input

```text
todo saved mission
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] saved mission
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
todo keep second mission
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] keep second mission
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
delete 1
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Glennon could not save the mission data.
____________________________________________________________
```

#### Command 4

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] saved mission
2. [T][ ] keep second mission
____________________________________________________________
```

#### Command 5

##### Input

```text
delete 1
```

##### Expected output

```text
____________________________________________________________
Mission removed:
  [T][ ] saved mission
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 6

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] keep second mission
____________________________________________________________
```

#### Command 7

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-182: Recover from a failed mark save

#### Aim

Negatively verify a real filesystem save failure preserves memory and disk; positively verify a retry applies mark exactly once.

- Block storage before command: 2
- Restore storage before command: 4

#### Command 1

##### Input

```text
todo saved mission
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] saved mission
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
mark 1
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Glennon could not save the mission data.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] saved mission
____________________________________________________________
```

#### Command 4

##### Input

```text
mark 1
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [T][X] saved mission
____________________________________________________________
```

#### Command 5

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][X] saved mission
____________________________________________________________
```

#### Command 6

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-183: Recover from a failed unmark save

#### Aim

Negatively verify a real filesystem save failure preserves memory and disk; positively verify a retry applies unmark exactly once.

- Block storage before command: 3
- Restore storage before command: 5

#### Command 1

##### Input

```text
todo saved mission
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] saved mission
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
mark 1
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [T][X] saved mission
____________________________________________________________
```

#### Command 3

##### Input

```text
unmark 1
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Glennon could not save the mission data.
____________________________________________________________
```

#### Command 4

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][X] saved mission
____________________________________________________________
```

#### Command 5

##### Input

```text
unmark 1
```

##### Expected output

```text
____________________________________________________________
Mission marked incomplete:
  [T][ ] saved mission
____________________________________________________________
```

#### Command 6

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] saved mission
____________________________________________________________
```

#### Command 7

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-184: Recover from a failed sort save

#### Aim

Negatively verify a real filesystem save failure preserves memory and disk; positively verify a retry applies sort exactly once.

- Block storage before command: 3
- Restore storage before command: 5

#### Command 1

##### Input

```text
todo saved mission
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] saved mission
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline first when sorted /by 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] first when sorted (by: Dec 2 2019, 11:59 PM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
sort
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Glennon could not save the mission data.
____________________________________________________________
```

#### Command 4

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] saved mission
2. [D][ ] first when sorted (by: Dec 2 2019, 11:59 PM)
____________________________________________________________
```

#### Command 5

##### Input

```text
sort
```

##### Expected output

```text
____________________________________________________________
Mission log sorted chronologically:
1. [D][ ] first when sorted (by: Dec 2 2019, 11:59 PM)
2. [T][ ] saved mission
____________________________________________________________
```

#### Command 6

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [D][ ] first when sorted (by: Dec 2 2019, 11:59 PM)
2. [T][ ] saved mission
____________________________________________________________
```

#### Command 7

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-190: Delete the mission number shown by find

#### Aim

Positively verify a single match at the last full-log index retains number 2, and deleting that displayed number preserves the nonmatching first mission.

#### Command 1

##### Input

```text
todo important task
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] important task
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
todo obsolete task
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] obsolete task
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
find obsolete
```

##### Expected output

```text
____________________________________________________________
Matching missions located:
2. [T][ ] obsolete task
____________________________________________________________
```

#### Command 4

##### Input

```text
delete 2
```

##### Expected output

```text
____________________________________________________________
Mission removed:
  [T][ ] obsolete task
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 5

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] important task
____________________________________________________________
```

#### Command 6

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-191: Mark the last of nonadjacent keyword matches

#### Aim

Positively verify find preserves first and last full-log indices for nonadjacent matches, and marking the displayed last index changes only that mission.

#### Command 1

##### Input

```text
todo survey coast
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] survey coast
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
todo repair engine
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] repair engine
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
todo survey forest
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] survey forest
Mission log now has 3 missions.
____________________________________________________________
```

#### Command 4

##### Input

```text
find survey
```

##### Expected output

```text
____________________________________________________________
Matching missions located:
1. [T][ ] survey coast
3. [T][ ] survey forest
____________________________________________________________
```

#### Command 5

##### Input

```text
mark 3
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [T][X] survey forest
____________________________________________________________
```

#### Command 6

##### Input

```text
find survey
```

##### Expected output

```text
____________________________________________________________
Matching missions located:
1. [T][ ] survey coast
3. [T][X] survey forest
____________________________________________________________
```

#### Command 7

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] survey coast
2. [T][ ] repair engine
3. [T][X] survey forest
____________________________________________________________
```

#### Command 8

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-192: Unmark a completed deadline located by keyword

#### Aim

Positively verify find reports the full-log middle index and completed deadline status, and unmark uses that displayed index without changing its neighbors.

#### Command 1

##### Input

```text
todo prepare envelope
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] prepare envelope
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline submit signed form /by 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] submit signed form (by: Dec 2 2019, 11:59 PM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
todo collect parcel
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] collect parcel
Mission log now has 3 missions.
____________________________________________________________
```

#### Command 4

##### Input

```text
mark 2
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [D][X] submit signed form (by: Dec 2 2019, 11:59 PM)
____________________________________________________________
```

#### Command 5

##### Input

```text
find signed
```

##### Expected output

```text
____________________________________________________________
Matching missions located:
2. [D][X] submit signed form (by: Dec 2 2019, 11:59 PM)
____________________________________________________________
```

#### Command 6

##### Input

```text
unmark 2
```

##### Expected output

```text
____________________________________________________________
Mission marked incomplete:
  [D][ ] submit signed form (by: Dec 2 2019, 11:59 PM)
____________________________________________________________
```

#### Command 7

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] prepare envelope
2. [D][ ] submit signed form (by: Dec 2 2019, 11:59 PM)
3. [T][ ] collect parcel
____________________________________________________________
```

#### Command 8

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-193: Delete an event using its date-filter number

#### Aim

Positively verify on excludes an unscheduled mission and another date while retaining the matching event at index 3, then deletes exactly that event.

#### Command 1

##### Input

```text
todo keep packing list
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] keep packing list
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline later itinerary /by 3/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] later itinerary (by: Dec 3 2019, 11:59 PM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
event departure briefing /from 2/12/2019 /to 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] departure briefing (all day: Dec 2 2019 to: Dec 2 2019)
Mission log now has 3 missions.
____________________________________________________________
```

#### Command 4

##### Input

```text
on 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Missions on Dec 2 2019:
3. [E][ ] departure briefing (all day: Dec 2 2019 to: Dec 2 2019)
____________________________________________________________
```

#### Command 5

##### Input

```text
delete 3
```

##### Expected output

```text
____________________________________________________________
Mission removed:
  [E][ ] departure briefing (all day: Dec 2 2019 to: Dec 2 2019)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 6

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] keep packing list
2. [D][ ] later itinerary (by: Dec 3 2019, 11:59 PM)
____________________________________________________________
```

#### Command 7

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-194: Mark a deadline using its date-filter number

#### Aim

Positively verify marking a deadline after on uses its full-log index beyond the filtered result count and preserves a preceding to-do.

#### Command 1

##### Input

```text
todo keep reference notes
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] keep reference notes
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline submit field notes /by 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] submit field notes (by: Dec 2 2019, 11:59 PM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
on 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Missions on Dec 2 2019:
2. [D][ ] submit field notes (by: Dec 2 2019, 11:59 PM)
____________________________________________________________
```

#### Command 4

##### Input

```text
mark 2
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [D][X] submit field notes (by: Dec 2 2019, 11:59 PM)
____________________________________________________________
```

#### Command 5

##### Input

```text
on 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Missions on Dec 2 2019:
2. [D][X] submit field notes (by: Dec 2 2019, 11:59 PM)
____________________________________________________________
```

#### Command 6

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] keep reference notes
2. [D][X] submit field notes (by: Dec 2 2019, 11:59 PM)
____________________________________________________________
```

#### Command 7

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-195: Unmark an event using its date-filter number

#### Aim

Positively verify a completed all-day event remains at its middle full-log index in on results and can be unmarked using that number.

#### Command 1

##### Input

```text
deadline earlier checkpoint /by 1/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] earlier checkpoint (by: Dec 1 2019, 11:59 PM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
event completed field visit /from 2/12/2019 /to 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] completed field visit (all day: Dec 2 2019 to: Dec 2 2019)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
todo keep route details
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] keep route details
Mission log now has 3 missions.
____________________________________________________________
```

#### Command 4

##### Input

```text
mark 2
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [E][X] completed field visit (all day: Dec 2 2019 to: Dec 2 2019)
____________________________________________________________
```

#### Command 5

##### Input

```text
on 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Missions on Dec 2 2019:
2. [E][X] completed field visit (all day: Dec 2 2019 to: Dec 2 2019)
____________________________________________________________
```

#### Command 6

##### Input

```text
unmark 2
```

##### Expected output

```text
____________________________________________________________
Mission marked incomplete:
  [E][ ] completed field visit (all day: Dec 2 2019 to: Dec 2 2019)
____________________________________________________________
```

#### Command 7

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [D][ ] earlier checkpoint (by: Dec 1 2019, 11:59 PM)
2. [E][ ] completed field visit (all day: Dec 2 2019 to: Dec 2 2019)
3. [T][ ] keep route details
____________________________________________________________
```

#### Command 8

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-196: Refresh filtered indices after sorting and deletion

#### Aim

Positively verify both filters use the current full log after sort and deletion, including the first-index boundary; later mutations use the refreshed numbers.

#### Command 1

##### Input

```text
todo unscheduled checkpoint
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] unscheduled checkpoint
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline late checkpoint /by 3/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] late checkpoint (by: Dec 3 2019, 11:59 PM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
deadline early checkpoint /by 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] early checkpoint (by: Dec 2 2019, 11:59 PM)
Mission log now has 3 missions.
____________________________________________________________
```

#### Command 4

##### Input

```text
sort
```

##### Expected output

```text
____________________________________________________________
Mission log sorted chronologically:
1. [D][ ] early checkpoint (by: Dec 2 2019, 11:59 PM)
2. [D][ ] late checkpoint (by: Dec 3 2019, 11:59 PM)
3. [T][ ] unscheduled checkpoint
____________________________________________________________
```

#### Command 5

##### Input

```text
on 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Missions on Dec 2 2019:
1. [D][ ] early checkpoint (by: Dec 2 2019, 11:59 PM)
____________________________________________________________
```

#### Command 6

##### Input

```text
delete 1
```

##### Expected output

```text
____________________________________________________________
Mission removed:
  [D][ ] early checkpoint (by: Dec 2 2019, 11:59 PM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 7

##### Input

```text
find checkpoint
```

##### Expected output

```text
____________________________________________________________
Matching missions located:
1. [D][ ] late checkpoint (by: Dec 3 2019, 11:59 PM)
2. [T][ ] unscheduled checkpoint
____________________________________________________________
```

#### Command 8

##### Input

```text
mark 1
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [D][X] late checkpoint (by: Dec 3 2019, 11:59 PM)
____________________________________________________________
```

#### Command 9

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [D][X] late checkpoint (by: Dec 3 2019, 11:59 PM)
2. [T][ ] unscheduled checkpoint
____________________________________________________________
```

#### Command 10

##### Input

```text
on 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Missions on Dec 2 2019:
____________________________________________________________
```

#### Command 11

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-197: Keep empty filters read-only before later additions

#### Aim

Positively verify find and on handle an empty log and no-match results without changing numbering; a subsequent first mission remains actionable at index 1.

#### Command 1

##### Input

```text
find absent
```

##### Expected output

```text
____________________________________________________________
Matching missions located:
____________________________________________________________
```

#### Command 2

##### Input

```text
on 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Missions on Dec 2 2019:
____________________________________________________________
```

#### Command 3

##### Input

```text
todo ordinary unscheduled chore
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] ordinary unscheduled chore
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 4

##### Input

```text
find absent
```

##### Expected output

```text
____________________________________________________________
Matching missions located:
____________________________________________________________
```

#### Command 5

##### Input

```text
on 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Missions on Dec 2 2019:
____________________________________________________________
```

#### Command 6

##### Input

```text
find ordinary
```

##### Expected output

```text
____________________________________________________________
Matching missions located:
1. [T][ ] ordinary unscheduled chore
____________________________________________________________
```

#### Command 7

##### Input

```text
mark 1
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [T][X] ordinary unscheduled chore
____________________________________________________________
```

#### Command 8

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][X] ordinary unscheduled chore
____________________________________________________________
```

#### Command 9

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-198: Reject a malformed index after keyword filtering

#### Aim

Negatively verify a nonnumeric mutation after find reports guidance and preserves every task and status; a valid displayed index still works after the error.

#### Command 1

##### Input

```text
todo keep malformed filter index
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] keep malformed filter index
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
todo locate recovery target
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] locate recovery target
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
find locate
```

##### Expected output

```text
____________________________________________________________
Matching missions located:
2. [T][ ] locate recovery target
____________________________________________________________
```

#### Command 4

##### Input

```text
mark two
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid mission number.
____________________________________________________________
```

#### Command 5

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] keep malformed filter index
2. [T][ ] locate recovery target
____________________________________________________________
```

#### Command 6

##### Input

```text
mark 2
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [T][X] locate recovery target
____________________________________________________________
```

#### Command 7

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] keep malformed filter index
2. [T][X] locate recovery target
____________________________________________________________
```

#### Command 8

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-199: Reject an out-of-range index after date filtering

#### Aim

Negatively verify an excessive full-log index after on cannot remove any mission, and the displayed valid index can be deleted after recovery.

#### Command 1

##### Input

```text
todo keep date index bounds
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] keep date index bounds
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline dated recovery target /by 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] dated recovery target (by: Dec 2 2019, 11:59 PM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
on 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Missions on Dec 2 2019:
2. [D][ ] dated recovery target (by: Dec 2 2019, 11:59 PM)
____________________________________________________________
```

#### Command 4

##### Input

```text
delete 3
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid mission number.
____________________________________________________________
```

#### Command 5

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] keep date index bounds
2. [D][ ] dated recovery target (by: Dec 2 2019, 11:59 PM)
____________________________________________________________
```

#### Command 6

##### Input

```text
delete 2
```

##### Expected output

```text
____________________________________________________________
Mission removed:
  [D][ ] dated recovery target (by: Dec 2 2019, 11:59 PM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 7

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] keep date index bounds
____________________________________________________________
```

#### Command 8

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-200: Reject a missing keyword without changing mission indices

#### Aim

Negatively verify a missing find keyword after a valid search preserves full-log order and completion state, and unmark still accepts the last displayed number.

#### Command 1

##### Input

```text
todo keep missing filter argument
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] keep missing filter argument
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
todo completed search recovery
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] completed search recovery
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
mark 2
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [T][X] completed search recovery
____________________________________________________________
```

#### Command 4

##### Input

```text
find completed
```

##### Expected output

```text
____________________________________________________________
Matching missions located:
2. [T][X] completed search recovery
____________________________________________________________
```

#### Command 5

##### Input

```text
find
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a keyword after find.
____________________________________________________________
```

#### Command 6

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] keep missing filter argument
2. [T][X] completed search recovery
____________________________________________________________
```

#### Command 7

##### Input

```text
unmark 2
```

##### Expected output

```text
____________________________________________________________
Mission marked incomplete:
  [T][ ] completed search recovery
____________________________________________________________
```

#### Command 8

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] keep missing filter argument
2. [T][ ] completed search recovery
____________________________________________________________
```

#### Command 9

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-201: Reject an impossible date without changing mission indices

#### Aim

Negatively verify an impossible on date after a valid result preserves mission order and status, and a valid retry displays the same actionable index.

#### Command 1

##### Input

```text
todo keep invalid calendar filter
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] keep invalid calendar filter
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
event calendar recovery visit /from 2/12/2019 /to 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] calendar recovery visit (all day: Dec 2 2019 to: Dec 2 2019)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
on 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Missions on Dec 2 2019:
2. [E][ ] calendar recovery visit (all day: Dec 2 2019 to: Dec 2 2019)
____________________________________________________________
```

#### Command 4

##### Input

```text
on 29/2/2025
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid calendar date as d/M/yyyy, for example 2/12/2019.
____________________________________________________________
```

#### Command 5

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] keep invalid calendar filter
2. [E][ ] calendar recovery visit (all day: Dec 2 2019 to: Dec 2 2019)
____________________________________________________________
```

#### Command 6

##### Input

```text
on 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Missions on Dec 2 2019:
2. [E][ ] calendar recovery visit (all day: Dec 2 2019 to: Dec 2 2019)
____________________________________________________________
```

#### Command 7

##### Input

```text
mark 2
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [E][X] calendar recovery visit (all day: Dec 2 2019 to: Dec 2 2019)
____________________________________________________________
```

#### Command 8

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] keep invalid calendar filter
2. [E][X] calendar recovery visit (all day: Dec 2 2019 to: Dec 2 2019)
____________________________________________________________
```

#### Command 9

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-210: Trim non-breaking spaces around a to-do description

#### Aim

Positively verify U+00A0 NO-BREAK SPACE at both description edges is removed while ordinary words remain unchanged.

#### Command 1

##### Input

```text
todo  read pasted note 
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] read pasted note
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] read pasted note
____________________________________________________________
```

#### Command 3

##### Input

```text
find pasted
```

##### Expected output

```text
____________________________________________________________
Matching missions located:
1. [T][ ] read pasted note
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-211: Trim narrow non-breaking spaces around a deadline description

#### Aim

Positively verify U+202F NARROW NO-BREAK SPACE at description edges is removed before parsing the normal /by parameter, retaining the explicit deadline time.

#### Command 1

##### Input

```text
deadline  submit narrow-spaced report  /by 2/12/2019 1800
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] submit narrow-spaced report (by: Dec 2 2019, 6:00 PM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [D][ ] submit narrow-spaced report (by: Dec 2 2019, 6:00 PM)
____________________________________________________________
```

#### Command 3

##### Input

```text
on 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Missions on Dec 2 2019:
1. [D][ ] submit narrow-spaced report (by: Dec 2 2019, 6:00 PM)
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-212: Trim figure spaces around an event description

#### Aim

Positively verify U+2007 FIGURE SPACE at description edges is removed while normal /from and /to parameters retain all-day event behavior.

#### Command 1

##### Input

```text
event  attend figure-spaced briefing  /from 2/12/2019 /to 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] attend figure-spaced briefing (all day: Dec 2 2019 to: Dec 2 2019)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [E][ ] attend figure-spaced briefing (all day: Dec 2 2019 to: Dec 2 2019)
____________________________________________________________
```

#### Command 3

##### Input

```text
on 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Missions on Dec 2 2019:
1. [E][ ] attend figure-spaced briefing (all day: Dec 2 2019 to: Dec 2 2019)
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-213: Retain a single supplementary character between mixed spaces

#### Aim

Positively verify mixed U+00A0, U+202F, U+2007, ASCII spaces, and tabs at description edges are removed without damaging a one-code-point supplementary Unicode description.

#### Command 1

##### Input

```text
todo  	   📚   	 
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] 📚
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] 📚
____________________________________________________________
```

#### Command 3

##### Input

```text
find 📚
```

##### Expected output

```text
____________________________________________________________
Matching missions located:
1. [T][ ] 📚
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-214: Preserve internal Unicode spaces in every task type

#### Aim

Positively verify internal U+00A0 in a to-do, U+202F in a deadline, and U+2007 in an event remain meaningful description characters while only the outer spaces are trimmed.

#### Command 1

##### Input

```text
todo  read book 
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] read book
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline  send report  /by 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] send report (by: Dec 2 2019, 11:59 PM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
event  join meeting  /from 2/12/2019 /to 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] join meeting (all day: Dec 2 2019 to: Dec 2 2019)
Mission log now has 3 missions.
____________________________________________________________
```

#### Command 4

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] read book
2. [D][ ] send report (by: Dec 2 2019, 11:59 PM)
3. [E][ ] join meeting (all day: Dec 2 2019 to: Dec 2 2019)
____________________________________________________________
```

#### Command 5

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-215: Preserve non-ASCII text and significant repeated internal spaces

#### Aim

Positively verify trimming U+00A0, U+202F, and U+2007 edges preserves Chinese text, punctuation, and distinct single/double internal spaces across search and status updates.

#### Command 1

##### Input

```text
todo  学习  C++/Java 
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] 学习  C++/Java
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
todo  学习 C++/Java 
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] 学习 C++/Java
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
find 学习  C++/Java
```

##### Expected output

```text
____________________________________________________________
Matching missions located:
1. [T][ ] 学习  C++/Java
____________________________________________________________
```

#### Command 4

##### Input

```text
mark 1
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [T][X] 学习  C++/Java
____________________________________________________________
```

#### Command 5

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][X] 学习  C++/Java
2. [T][ ] 学习 C++/Java
____________________________________________________________
```

#### Command 6

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-216: Keep normalized descriptions distinct across task types

#### Aim

Positively verify different Unicode padding normalizes to the same description without merging different task types, and sorting/date filtering preserve normalized text and full-log numbering.

#### Command 1

##### Input

```text
todo  shared normalized description 
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] shared normalized description
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline  shared normalized description  /by 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] shared normalized description (by: Dec 2 2019, 11:59 PM)
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 3

##### Input

```text
event  shared normalized description  /from 2/12/2019 /to 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] shared normalized description (all day: Dec 2 2019 to: Dec 2 2019)
Mission log now has 3 missions.
____________________________________________________________
```

#### Command 4

##### Input

```text
sort
```

##### Expected output

```text
____________________________________________________________
Mission log sorted chronologically:
1. [E][ ] shared normalized description (all day: Dec 2 2019 to: Dec 2 2019)
2. [D][ ] shared normalized description (by: Dec 2 2019, 11:59 PM)
3. [T][ ] shared normalized description
____________________________________________________________
```

#### Command 5

##### Input

```text
on 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Missions on Dec 2 2019:
1. [E][ ] shared normalized description (all day: Dec 2 2019 to: Dec 2 2019)
2. [D][ ] shared normalized description (by: Dec 2 2019, 11:59 PM)
____________________________________________________________
```

#### Command 6

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [E][ ] shared normalized description (all day: Dec 2 2019 to: Dec 2 2019)
2. [D][ ] shared normalized description (by: Dec 2 2019, 11:59 PM)
3. [T][ ] shared normalized description
____________________________________________________________
```

#### Command 7

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-217: Reject a to-do containing only a non-breaking space

#### Aim

Negatively verify a single U+00A0 NO-BREAK SPACE is an empty description, receives the existing todo guidance, and preserves an existing mission; a later valid addition succeeds.

#### Command 1

##### Input

```text
todo keep no-break blank state
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] keep no-break blank state
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
todo  
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a mission after todo.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] keep no-break blank state
____________________________________________________________
```

#### Command 4

##### Input

```text
todo recovered no-break input
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] recovered no-break input
Mission log now has 2 missions.
____________________________________________________________
```

#### Command 5

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] keep no-break blank state
2. [T][ ] recovered no-break input
____________________________________________________________
```

#### Command 6

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-218: Reject a deadline containing only narrow non-breaking spaces

#### Aim

Negatively verify a U+202F-only deadline description receives the existing deadline usage guidance even with a valid date, and preserves an existing mission.

#### Command 1

##### Input

```text
todo keep narrow blank state
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] keep narrow blank state
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline    /by 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Use: deadline <mission> /by <d/M/yyyy [HHmm]>.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] keep narrow blank state
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-219: Reject an event containing only figure spaces

#### Aim

Negatively verify a U+2007-only event description receives the existing event usage guidance even with valid boundaries, and preserves an existing mission.

#### Command 1

##### Input

```text
todo keep figure blank state
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] keep figure blank state
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
event    /from 2/12/2019 /to 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Use: event <mission> /from <d/M/yyyy [HHmm]> /to <d/M/yyyy [HHmm]>.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][ ] keep figure blank state
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-220: Reject a Unicode-padded duplicate of a completed to-do

#### Aim

Negatively verify U+00A0 description padding cannot bypass duplicate detection or reset the completion state of a stored to-do.

#### Command 1

##### Input

```text
todo one completed Unicode task
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] one completed Unicode task
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
mark 1
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [T][X] one completed Unicode task
____________________________________________________________
```

#### Command 3

##### Input

```text
todo  one completed Unicode task 
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
That mission already exists in the log.
____________________________________________________________
```

#### Command 4

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][X] one completed Unicode task
____________________________________________________________
```

#### Command 5

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-221: Reject a Unicode-padded equivalent deadline

#### Aim

Negatively verify U+202F description padding cannot bypass duplicate detection when date-only and explicit end-of-day dates are equivalent, preserving the original deadline.

#### Command 1

##### Input

```text
deadline one normalized deadline /by 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [D][ ] one normalized deadline (by: Dec 2 2019, 11:59 PM)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
deadline  one normalized deadline  /by 2/12/2019 2359
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
That mission already exists in the log.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [D][ ] one normalized deadline (by: Dec 2 2019, 11:59 PM)
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-222: Reject a Unicode-padded duplicate event

#### Aim

Negatively verify U+2007 description padding cannot bypass duplicate detection for the same event range, preserving the original event.

#### Command 1

##### Input

```text
event one normalized event /from 2/12/2019 /to 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission added: [E][ ] one normalized event (all day: Dec 2 2019 to: Dec 2 2019)
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
event  one normalized event  /from 2/12/2019 /to 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
That mission already exists in the log.
____________________________________________________________
```

#### Command 3

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [E][ ] one normalized event (all day: Dec 2 2019 to: Dec 2 2019)
____________________________________________________________
```

#### Command 4

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-223: Reject mixed Unicode and ASCII blank descriptions

#### Aim

Negatively verify a description made from U+00A0, U+202F, U+2007, a tab, and an ASCII space is rejected by all three creation commands with their existing guidance; every rejection preserves the completed mission.

#### Command 1

##### Input

```text
todo keep mixed blank descriptions
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] keep mixed blank descriptions
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
mark 1
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [T][X] keep mixed blank descriptions
____________________________________________________________
```

#### Command 3

##### Input

```text
todo  	   
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a mission after todo.
____________________________________________________________
```

#### Command 4

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][X] keep mixed blank descriptions
____________________________________________________________
```

#### Command 5

##### Input

```text
deadline  	    /by 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Use: deadline <mission> /by <d/M/yyyy [HHmm]>.
____________________________________________________________
```

#### Command 6

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][X] keep mixed blank descriptions
____________________________________________________________
```

#### Command 7

##### Input

```text
event  	    /from 2/12/2019 /to 2/12/2019
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Use: event <mission> /from <d/M/yyyy [HHmm]> /to <d/M/yyyy [HHmm]>.
____________________________________________________________
```

#### Command 8

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][X] keep mixed blank descriptions
____________________________________________________________
```

#### Command 9

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-230: Reject an impossible February deadline with calendar guidance

#### Aim

Negatively verify the reported 31/2/2026 calendar error for both date-only
and timed deadlines. Each rejection must retain the existing completed mission.

#### Command 1

##### Input

```text
todo preserve February deadline state
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] preserve February deadline state
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
mark 1
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [T][X] preserve February deadline state
____________________________________________________________
```

#### Command 3

##### Input

```text
deadline impossible February /by 31/2/2026
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid calendar date as d/M/yyyy with an optional HHmm time, for example 2/12/2019 or 2/12/2019 1800.
____________________________________________________________
```

#### Command 4

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][X] preserve February deadline state
____________________________________________________________
```

#### Command 5

##### Input

```text
deadline impossible February time /by 31/2/2026 1800
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid calendar date as d/M/yyyy with an optional HHmm time, for example 2/12/2019 or 2/12/2019 1800.
____________________________________________________________
```

#### Command 6

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][X] preserve February deadline state
____________________________________________________________
```

#### Command 7

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-231: Reject an impossible event end with calendar guidance

#### Aim

Negatively verify a valid event start cannot conceal an impossible 31/2/2026
end in either date-only or timed input. Each rejection must retain the existing
completed mission.

#### Command 1

##### Input

```text
todo preserve February event state
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] preserve February event state
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
mark 1
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [T][X] preserve February event state
____________________________________________________________
```

#### Command 3

##### Input

```text
event invalid February end /from 28/2/2026 /to 31/2/2026
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid calendar date as d/M/yyyy with an optional HHmm time, for example 2/12/2019 or 2/12/2019 1800.
____________________________________________________________
```

#### Command 4

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][X] preserve February event state
____________________________________________________________
```

#### Command 5

##### Input

```text
event invalid February timed end /from 28/2/2026 0900 /to 31/2/2026 1800
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid calendar date as d/M/yyyy with an optional HHmm time, for example 2/12/2019 or 2/12/2019 1800.
____________________________________________________________
```

#### Command 6

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][X] preserve February event state
____________________________________________________________
```

#### Command 7

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

### TC-232: Reject an impossible February filter with calendar guidance

#### Aim

Negatively verify on rejects the reported 31/2/2026 calendar date with
date-only guidance and preserves the existing completed mission.

#### Command 1

##### Input

```text
todo preserve February filter state
```

##### Expected output

```text
____________________________________________________________
Mission added: [T][ ] preserve February filter state
Mission log now has 1 mission.
____________________________________________________________
```

#### Command 2

##### Input

```text
mark 1
```

##### Expected output

```text
____________________________________________________________
Mission marked complete:
  [T][X] preserve February filter state
____________________________________________________________
```

#### Command 3

##### Input

```text
on 31/2/2026
```

##### Expected output

```text
____________________________________________________________
Mission control alert!
Please enter a valid calendar date as d/M/yyyy, for example 2/12/2019.
____________________________________________________________
```

#### Command 4

##### Input

```text
list
```

##### Expected output

```text
____________________________________________________________
Mission log:
1. [T][X] preserve February filter state
____________________________________________________________
```

#### Command 5

##### Input

```text
bye
```

##### Expected output

```text
____________________________________________________________
Signing off. Catch you on the next mission!
____________________________________________________________
```

## Coverage matrix

Detailed parsing, persistence, collection, and date-boundary permutations are
covered by JUnit. This plan keeps representative end-to-end console workflows
that verify command integration and exact user-visible output.

| Feature family | Positive cases and distinct risks | Negative cases and distinct risks |
| --- | --- | --- |
| Task input and display | TC-003 typed to-do/deadline; TC-005 event display; TC-008 recovery after rejected input; TC-021 symbols; TC-022 spacing; TC-031 leap-day deadline; TC-032 upper-year boundary; TC-036 mixed ordering; TC-042 cross-day event; TC-054 mixed listing; TC-122 all-day event boundaries | TC-004 missing task parts; TC-007 unknown commands; TC-096 equal timestamps; TC-047 missing event body; TC-090 malformed date-time; TC-095 impossible date; TC-097 reversed event |
| Mission state management | TC-002 mark/unmark lifecycle; TC-003 typed-task completion; TC-005 event completion; TC-010 middle deletion; TC-011 completed sole deletion; TC-014 last deletion; TC-036 mixed-task ordering; TC-054 mixed listing; TC-105 filtered mixed state | TC-007 malformed/out-of-range index; TC-009 rejected state changes; TC-019 deletion bounds; TC-069 mark bounds; TC-079 unmark bounds |
| Scheduling and date behavior | TC-003 deadline date; TC-005 same-day event; TC-031 leap day; TC-032 date upper boundary; TC-042 leap-day transition; TC-123 multi-day all-day event; TC-105 mixed date filtering | TC-090 malformed date-time; TC-095 impossible event date; TC-097 reversed event; TC-109 impossible filter date |
| Find by keyword | TC-110 mixed types and status; TC-111 substring; TC-112 multi-word phrase; TC-113 symbols; TC-114 no matches; TC-115 case-sensitive descriptions; TC-116 repeated read-only searches | TC-117 missing keyword; TC-118 wrong command case; TC-145 blank input |
| Date-only scheduling | TC-120 deadline default; TC-121 leap day and filtering; TC-122 single-day event; TC-123 multi-day filtering; TC-124 date-only start; TC-125 date-only end; TC-126 timed-input compatibility | TC-127 malformed representation; TC-128 impossible date; TC-129 reversed range |
| Chronological sorting | TC-130 mixed types; TC-131 stable ties; TC-132 empty boundary; TC-133 single unscheduled item; TC-134 idempotent order; TC-135 completion state; TC-136 date-only comparison | TC-137 unsupported arguments; TC-138 wrong command case; TC-146 malformed exit |
| Flexible command whitespace | TC-028 leading task; TC-119 search; TC-139 sort; TC-140 tab and Unicode description; TC-141 spaced date-time; TC-142 tabbed event; TC-143 trailing whitespace; TC-144 state transitions | TC-145 blank commands; TC-146 extra exit argument; TC-147 malformed numeric arguments |
| Scheduling parameters | TC-120 date-only deadline; TC-121 leap day; TC-122 same-day all-day event; TC-123 multi-day event; TC-124 omitted start time; TC-125 omitted end time; TC-126 explicit times | TC-148 repeated deadline; TC-149 repeated start; TC-150 repeated end; TC-151 wrong task flag; TC-152 missing or out-of-order flags |
| Task uniqueness | TC-160 task types; TC-161 dates; TC-162 end times; TC-163 case; TC-164 deletion and re-add; TC-165 Unicode and internal spacing; TC-166 start times | TC-167 completed or spaced duplicate; TC-168 equivalent deadline inputs; TC-169 duplicate event |
| Save consistency | TC-002 status lifecycle; TC-003 mixed additions; TC-005 event status; TC-010 middle deletion; TC-011 sole deletion; TC-130 chronological sort; TC-135 completed sort | TC-180 add failure; TC-181 delete failure; TC-182 mark failure; TC-183 unmark failure; TC-184 sort failure; all verify unchanged state and safe retry |
| Filtered mission numbering | TC-190 find/delete last; TC-191 nonadjacent first/last find/mark; TC-192 find/unmark middle completed deadline; TC-193 on/delete last event; TC-194 on/mark deadline; TC-195 on/unmark middle completed event; TC-196 sort/delete reindexing and first index; TC-197 empty/no-match results and later addition | TC-198 malformed mutation and recovery; TC-199 excessive index and recovery; TC-200 missing keyword and preserved status; TC-201 impossible date and recovery; all assert the full log is unchanged after rejection |
| Unicode description whitespace | TC-210 U+00A0 to-do edges; TC-211 U+202F deadline edges; TC-212 U+2007 event edges; TC-213 mixed edges and one supplementary code point; TC-214 internal Unicode spaces in all task types; TC-215 significant internal spacing and non-ASCII text; TC-216 normalized descriptions across task types, sorting, and filtering | TC-217 U+00A0-only to-do; TC-218 U+202F-only deadline; TC-219 U+2007-only event; TC-220 completed to-do duplicate; TC-221 equivalent deadline duplicate; TC-222 event duplicate; TC-223 mixed whitespace-only descriptions; all assert preserved mission state |
| Smoke feedback: find output spacing | TC-110 mixed types and status; TC-111 substring; TC-112 multi-word phrase; TC-113 symbols; TC-115 nonmatching first mission; TC-116 status transition; TC-119 surrounding whitespace; TC-191 nonadjacent indices; TC-192 completed deadline; all matching rows include a space after the mission number | TC-117 missing keyword; TC-118 wrong command case; TC-198 invalid update after a search; all assert unchanged missions |
| Smoke feedback: valid calendar date guidance | TC-031 leap day; TC-032 upper-year boundary; TC-042 cross-day event; TC-120 date-only deadline; TC-121 leap-day filtering; TC-124 date-only event start; TC-125 date-only event end; TC-126 explicit-time compatibility | TC-090 malformed timed deadline; TC-095 impossible event start; TC-109 impossible filter date; TC-230 impossible February deadline in both representations; TC-231 impossible February event end in both representations; TC-232 impossible February filter; all assert unchanged missions |

All 117 complete command sequences are unique. Every negative case lists the
missions afterward when state preservation is applicable. TC-001 separately
checks clean application exit.
