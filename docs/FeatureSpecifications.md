# ShrekAndDonkey Feature Specifications

**Target user:** A student or busy individual who wants a lightweight way to manage personal tasks, deadlines, and events from one chat interface.

ShrekAndDonkey is a Shrek-themed task chatbot. Commands are lowercase. Task numbers are one-based numbers shown by the `list` command.

## 1. List Tasks

- **Feature:** List Tasks
- **Purpose:** Displays all tasks in the current swamp list.
- **Command format:** `list`
- **Example:** `list`
- **Output:** Displays every task with a number, task type, completion status, description, and any relevant date.
- **Errors:** None. An empty list is displayed without an error.

## 2. Add a To-do

- **Feature:** Add a To-do
- **Purpose:** Adds a task with no associated date or time.
- **Command format:** `todo DESCRIPTION`
- **Example:** `todo buy onions`
- **Parameters:**
  - `DESCRIPTION` must not be blank.
- **Output:** Adds the task as `[T][ ] DESCRIPTION` and displays the updated task count.
- **Errors:** A blank description displays an error message.

## 3. Add a Deadline

- **Feature:** Add a Deadline
- **Purpose:** Adds a task with a due date or date and time.
- **Command format:** `deadline DESCRIPTION /by DATE`
- **Example:** `deadline submit report /by 2026-10-15 18:00`
- **Parameters:**
  - `DESCRIPTION` must not be blank.
  - `/by` is required.
  - `DATE` must use a supported date format.
- **Output:** Adds the task as `[D][ ] DESCRIPTION (by: DATE)`.
- **Errors:** A missing `/by` marker, blank description, or invalid date displays an error message.

## 4. Add an Event

- **Feature:** Add an Event
- **Purpose:** Adds a task with a start and end date or time.
- **Command format:** `event DESCRIPTION /from DATE /to DATE`
- **Example:** `event swamp meeting /from 2026-10-20 14:00 /to 2026-10-20 16:00`
- **Parameters:**
  - `DESCRIPTION` must not be blank.
  - `/from` and `/to` are required.
  - Both dates must use a supported date format.
  - The end date and time cannot be earlier than the start date and time.
- **Output:** Adds the task as `[E][ ] DESCRIPTION (from: DATE to: DATE)`.
- **Errors:** Missing markers, blank description, invalid dates, or an end before the start display an error message.

## 5. Mark a Task as Done

- **Feature:** Mark a Task as Done
- **Purpose:** Marks an incomplete task as completed.
- **Command format:** `mark N`
- **Example:** `mark 1`
- **Parameters:** `N` must be a task number from 1 to the number of listed tasks.
- **Output:** Updates the task status from `[ ]` to `[X]`.
- **Errors:** A non-numeric or out-of-range task number displays an error message.

## 6. Mark a Task as Not Done

- **Feature:** Mark a Task as Not Done
- **Purpose:** Marks a completed task as incomplete.
- **Command format:** `unmark N`
- **Example:** `unmark 1`
- **Parameters:** `N` must be a task number from 1 to the number of listed tasks.
- **Output:** Updates the task status from `[X]` to `[ ]`.
- **Errors:** A non-numeric or out-of-range task number displays an error message.

## 7. Delete a Task

- **Feature:** Delete a Task
- **Purpose:** Removes a task from the swamp list.
- **Command format:** `delete N`
- **Example:** `delete 2`
- **Parameters:** `N` must be a task number from 1 to the number of listed tasks.
- **Output:** Removes the task and displays the updated task count.
- **Errors:** A non-numeric or out-of-range task number displays an error message.

## 8. Find Tasks

- **Feature:** Find Tasks
- **Purpose:** Displays tasks whose descriptions contain a keyword.
- **Command format:** `find KEYWORD`
- **Example:** `find report`
- **Parameters:** `KEYWORD` must not be blank.
- **Output:** Displays the matching tasks, numbered from 1.
- **Errors:** A missing keyword displays an error message. A valid keyword with no matches displays an empty result list.

## 9. Sort Tasks by Date

- **Feature:** Sort Tasks by Date
- **Purpose:** Orders deadline and event tasks by their date.
- **Command format:** `sort`
- **Example:** `sort`
- **Output:** Sorts deadlines by deadline date and events by start date. Tasks without dates appear after dated tasks. Tasks with equal dates retain their existing order.
- **Errors:** None.

## 10. Automatic Task Persistence

- **Feature:** Automatic Task Persistence
- **Purpose:** Loads the task list from `data/happyFile.txt` at startup and saves it after every successful change.
- **Behavior:** Creates the `data` directory and an empty `happyFile.txt` file automatically when they are missing.
- **Saved commands:** `todo`, `deadline`, `event`, `mark`, `unmark`, `delete`, and `sort`.
- **Errors:** The application does not start when the existing task file cannot be loaded. It displays an error when a
  changed task list cannot be saved.

## 11. Exit the Application

- **Feature:** Exit the Application
- **Purpose:** Ends the current ShrekAndDonkey session.
- **Command format:** `bye`
- **Example:** `bye`
- **Output:** Displays the chatbot goodbye message and closes the GUI.
- **Errors:** None.

## Supported Date Formats

The `deadline` and `event` commands accept these date formats:

- `yyyy-MM-dd`, for example `2026-10-15`
- `yyyy-MM-dd HH:mm`, for example `2026-10-15 18:00`
- `yyyy-MM-ddTHH:mm`, for example `2026-10-15T18:00`
- `d/M/yyyy HHmm`, for example `15/10/2026 1800`

## Unsupported Commands

An unrecognized command displays an error message with the available command keywords.
