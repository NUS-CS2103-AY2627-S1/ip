# Oreo - User Guide

Oreo is a friendly task manager for students and busy people who want to keep
track of things to do, deadlines, events, and related merchandise details.
Type a command, and Oreo will update your list, show the result, and save your
tasks for the next time you open the application.

## Quick start

### Prerequisites

- Java Development Kit (JDK) 25
- A terminal, or an IDE such as IntelliJ IDEA

### Launch Oreo

1. Download the latest `oreo-all.jar` from the project's releases page.
2. Open a terminal in the folder containing the downloaded file.
3. Run:

   ```text
   java -jar oreo-all.jar
   ```

   If you are running the project from its source folder, use `./gradlew run`
   instead. This opens the JavaFX application window; you can type commands in
   the input box and press Enter.

4. Try your first command:

   ```text
   todo read CS2103 notes
   ```

Oreo creates `data/oreo.txt` when it first needs to save a task. Keep this file
with the application if you want your tasks to persist between launches.

## Features

> **ℹ️ Notes about the command format**
>
> - Words in `UPPER_CASE` are values supplied by you. For example, replace
>   `TASK_NUMBER` in `mark TASK_NUMBER` with a number such as `1`.
> - Items in square brackets are optional. Oreo currently has no optional
>   parameters in its command formats; square brackets are used here only as
>   the standard notation for optional items.
> - A description or merchandise detail can contain spaces, such as
>   `todo buy a birthday present`.
> - Commands and prefixes are lowercase and case-sensitive. Enter `deadline`,
>   not `Deadline`; enter `/by`, `/from`, and `/to` exactly as shown.
> - Parameter order matters. For example, an event must use
>   `event DESCRIPTION /from START /to END`; `/to` cannot come before `/from`.
> - Task numbers are one-based and refer to the current numbered list. After a
>   deletion, use `list` again because the remaining tasks are re-numbered.
> - Commands that do not take parameters, such as `help`, `list`, `listall`,
>   and `bye`, ignore extra text after the command. For example, `help 123` is
>   treated as `help`.
> - `find` and `find-merchandise` search without regard to letter case. They
>   look for a keyword within the relevant text, rather than requiring an exact
>   match.
> - Use `yyyy-MM-dd` for the `on` command, for example `on 2026-10-15`.
>   Recognised deadline and event date-times may also use `d/M/yyyy HHmm`, for
>   example `15/10/2026 1800`.
> - Tasks are saved after successful additions, status changes, deletions, and
>   merchandise updates. Keep `data/oreo.txt` with the application to retain
>   your data.

### Getting help

Displays Oreo's built-in command list.

**Format:** `help`

**Example:** `help`

**Result:** Oreo displays the available commands and their formats.

### Adding tasks

#### To-do

Adds an ordinary task.

**Format:** `todo DESCRIPTION`

**Examples:** `todo buy groceries`, `todo submit assignment`

**Result:** Oreo adds the task and reports the new number of tasks.

**Tip:** `DESCRIPTION` cannot be empty.

#### Deadline

Adds a task that is due on a date or at a date and time.

**Format:** `deadline DESCRIPTION /by DATE_OR_TIME`

**Examples:**

```text
deadline return library book /by 2026-10-15
deadline submit report /by 15/10/2026 1800
```

**Result:** Oreo adds the deadline and displays its due date or time.

**Date formats:** Use `yyyy-MM-dd` for a date, such as `2026-10-15`, or
`d/M/yyyy HHmm` for a date and time, such as `15/10/2026 1800`. Other text is
accepted as a free-form date label, but it cannot be found by the `on` command.

#### Event

Adds an event with a start and end date or time.

**Format:** `event DESCRIPTION /from START /to END`

**Examples:**

```text
event project meeting /from 2026-10-15 /to 2026-10-16
event dinner /from 15/10/2026 1900 /to 15/10/2026 2100
```

**Result:** Oreo adds the event and displays its start and end values.

**Tip:** When both values are recognised dates or date-times, `START` must be
before `END`. The `/from` and `/to` prefixes are required.

### Viewing and finding tasks

#### List tasks

Displays all tasks, including their task type and completion status.

**Format:** `list`

**Example:** `list`

**Result:** Oreo shows the current numbered task list. If there are no tasks,
it displays `No tasks in the list.`

#### Find tasks

Searches task descriptions for a keyword.

**Format:** `find KEYWORD`

**Examples:** `find book`, `find CS2103`

**Result:** Oreo displays matching tasks in their original order. The search is
case-insensitive. If nothing matches, it displays `No matching tasks found.`

#### View tasks on a date

Displays deadlines and events occurring on a specified date.

**Format:** `on DATE`

**Examples:** `on 2026-10-15`, `on 2026-12-01`

**Result:** Oreo displays matching deadlines and events. Use the `yyyy-MM-dd`
format; for example, `on 15/10/2026` is invalid.

### Managing task status

#### Mark a task as done

Marks the selected task as completed.

**Format:** `mark TASK_NUMBER`

**Example:** `mark 1`

**Result:** Oreo changes the task's status marker to done and confirms the
updated task.

#### Mark a task as not done

Restores the selected task to its incomplete state.

**Format:** `unmark TASK_NUMBER`

**Example:** `unmark 1`

**Result:** Oreo changes the task's status marker back to not done.

#### Delete a task

Removes a task and any merchandise attached to it.

**Format:** `delete TASK_NUMBER`

**Example:** `delete 2`

**Result:** Oreo removes the task and re-numbers the remaining tasks.

**Tip:** Use the current number from `list`. The number must be a positive
integer within the list.

### Managing merchandise details

Merchandise is one optional free-form detail attached to a task. It can be used
for an item, location, price, or any other note related to that task.

#### Add merchandise

Adds merchandise details to a task that does not already have them.

**Format:** `merchandise TASK_NUMBER DETAILS`

**Example:** `merchandise 1 buy oat milk at FairPrice`

**Result:** Oreo displays the task together with its merchandise detail.

**Tip:** A task can have only one merchandise detail. Use `edit-merchandise` to
replace an existing detail.

#### Edit merchandise

Replaces the merchandise detail attached to a task.

**Format:** `edit-merchandise TASK_NUMBER DETAILS`

**Example:** `edit-merchandise 1 buy oat milk at Cold Storage`

**Result:** Oreo displays the updated merchandise detail. The task must already
have merchandise.

#### Delete merchandise

Removes the merchandise detail from a task without deleting the task.

**Format:** `delete-merchandise TASK_NUMBER`

**Example:** `delete-merchandise 1`

**Result:** Oreo confirms that the merchandise was removed. The task must have
merchandise attached.

#### Find merchandise

Searches merchandise details without searching task descriptions.

**Format:** `find-merchandise KEYWORD`

**Example:** `find-merchandise FairPrice`

**Result:** Oreo displays tasks whose merchandise contains the keyword. The
search is case-insensitive.

#### List tasks with merchandise

Displays every task and any merchandise attached to it.

**Format:** `listall`

**Example:** `listall`

**Result:** Oreo displays the complete task list with merchandise details.

### Exit Oreo

Closes Oreo after displaying a goodbye message.

**Format:** `bye`

**Example:** `bye`

## Command summary

| Action | Format | Example |
| --- | --- | --- |
| Help | `help` | `help` |
| List tasks | `list` | `list` |
| Add to-do | `todo DESCRIPTION` | `todo buy groceries` |
| Add deadline | `deadline DESCRIPTION /by DATE_OR_TIME` | `deadline return book /by 2026-10-15` |
| Add event | `event DESCRIPTION /from START /to END` | `event meeting /from 2026-10-15 /to 2026-10-16` |
| Find tasks | `find KEYWORD` | `find book` |
| View date | `on DATE` | `on 2026-10-15` |
| List with merchandise | `listall` | `listall` |
| Add merchandise | `merchandise TASK_NUMBER DETAILS` | `merchandise 1 at FairPrice` |
| Edit merchandise | `edit-merchandise TASK_NUMBER DETAILS` | `edit-merchandise 1 at Cold Storage` |
| Delete merchandise | `delete-merchandise TASK_NUMBER` | `delete-merchandise 1` |
| Find merchandise | `find-merchandise KEYWORD` | `find-merchandise FairPrice` |
| Mark done | `mark TASK_NUMBER` | `mark 1` |
| Mark not done | `unmark TASK_NUMBER` | `unmark 1` |
| Delete task | `delete TASK_NUMBER` | `delete 2` |
| Exit | `bye` | `bye` |
