# UI Test Plan

## Automated Test-UI Skill

Run `.agents/skills/test-ui/scripts/run-ui-tests.sh` after a command-line behavior or persistence update. The executable cases are in `test/ui-test-cases.tsv`; its input and expected-output fragments correspond to the scenarios below. The script builds `lynn.jar`, runs each case in a temporary directory, prints the console input/output, and terminates at the first failed check.

### Core task flow

* Aim: Verify all three task types, marking, listing, and exit in one session.
* Inputs: `todo read book`, `deadline return book /by 2026-09-01`, `event team meeting /from Mon 2pm /to 4pm`, `mark 2`, `list`, `bye`.
* Expected output: The list contains a todo, a completed deadline shown as `Sep 1 2026`, and an event; Lynn then says goodbye.

### Errors preserve state

* Aim: Verify malformed and unknown commands show a helpful error without adding a task.
* Inputs: `todo`, `deadline bad /by next Thursday`, `unknown command`, `list`, `bye`.
* Expected output: Each invalid input produces an error; the following list is empty.

### State remains correct after an error

* Aim: Interleave successful and failing commands to ensure an error does not corrupt task state.
* Inputs: `todo buy bread`, `mark 1`, `mark 9`, `list`, `unmark 1`, `delete 1`, `list`, `bye`.
* Expected output: The invalid `mark 9` reports that task 9 does not exist; task 1 remains marked until `unmark 1`, and deletion leaves zero tasks.

### Friendly handling of an empty command

* Aim: Verify that an accidental blank command gives actionable guidance and does not change the task list.
* Inputs: An empty line, followed by `list` and `bye`.
* Expected output: Lynn asks the user to enter a command and the list remains empty.

### Lynn's personality

* Aim: Verify that Lynn presents a consistent, encouraging personality when starting.
* Inputs: Start Lynn and enter `bye`.
* Expected output: The welcome message describes Lynn as an upbeat study companion and encourages the user to make progress one small step at a time.

### Flexible command input

* Aim: Verify that command keywords are case-insensitive and extra spacing around arguments is accepted.
* Inputs: `  DEADLINE  submit report   /BY   2026-10-15  `, `LIST`, and `BYE`.
* Expected output: Lynn adds and lists the deadline as `Oct 15 2026`, then exits normally.

## Level 7: Persistent Storage

### First launch creates storage

* Aim: Verify that Lynn can start on a new computer without an existing data folder or file.
* Inputs: `bye`
* Expected output: Lynn greets the user and exits normally. A `data/lynn.txt` file is created in the working directory.

### Save and reload tasks

* Aim: Verify that all task types and their completion state survive a restart.
* First-run inputs:
  ```text
  todo read book
  deadline return book /by 2026-09-01
  event team meeting /from Mon 2pm /to 4pm
  mark 2
  delete 3
  bye
  ```
* Second-run inputs:
  ```text
  list
  bye
  ```
* Expected output after `list`:
  ```text
  Here are the tasks in your list:
  1.[T][ ] read book
  2.[D][X] return book (by: Sep 1 2026)
  ```

## Level 8: Deadline Dates

### Parse and format a deadline date

* Aim: Verify that a deadline is stored as a date rather than unprocessed text.
* Inputs: `deadline submit report /by 2026-10-15`, followed by `list`.
* Expected output: The task is displayed as `[D][ ] submit report (by: Oct 15 2026)`.

### Reject an invalid deadline date

* Aim: Verify that malformed date input produces a helpful error without adding a task.
* Inputs: `deadline submit report /by next Thursday`, followed by `list`.
* Expected output: Lynn asks for a `yyyy-MM-dd` date, and `list` does not contain `submit report`.

### Descriptions containing separators

* Aim: Verify that task descriptions containing `|` do not corrupt the save file.
* First-run inputs: `todo read | annotate book`, followed by `bye`.
* Second-run inputs: `list`, followed by `bye`.
* Expected output after `list`: `1.[T][ ] read | annotate book`.

## Level 9: Find

### Find tasks by a keyword

* Aim: Verify that `find` matches task descriptions without regard to letter case.
* Inputs: Add `Read Book`, `buy bread`, and `book club`, then run `find BOOK`, `find calendar`, and `find`.
* Expected output: The first search lists `Read Book` and `book club` only; a search with no matches lists no tasks; an empty keyword produces a helpful error.

## C-Help: In-app command guidance

* Aim: Verify that Lynn provides an in-app help page listing the supported commands.
* Inputs: `help`, followed by `bye`.
* Expected output: Lynn displays a command reference containing the `todo`, `find`, `help`, and `bye` commands, then exits normally.
