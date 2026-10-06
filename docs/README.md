# TBC - User Guide

**TBC** is a desktop personal assistant chatbot optimized for fast keyboard-based task and place management through a Command Line Interface (CLI) paired with a graphical user interface (GUI).

![TBC Graphical User Interface](Ui.png)

---

## Table of Contents
- [Quick Start](#quick-start)
- [Command Format Conventions](#command-format-conventions)
- [Task Management Features](#task-management-features)
  - [Adding a To-Do: `todo`](#adding-a-to-do-todo)
  - [Adding a Deadline: `deadline`](#adding-a-deadline-deadline)
  - [Adding an Event: `event`](#adding-an-event-event)
  - [Listing All Tasks: `list`](#listing-all-tasks-list)
  - [Marking a Task as Done: `mark`](#marking-a-task-as-done-mark)
  - [Marking a Task as Incomplete: `unmark`](#marking-a-task-as-incomplete-unmark)
  - [Deleting a Task: `delete`](#deleting-a-task-delete)
  - [Searching Tasks by Keyword: `find`](#searching-tasks-by-keyword-find)
  - [Undoing Previous Action: `undo`](#undoing-previous-action-undo)
- [Place Management Features](#place-management-features)
  - [Adding a Place: `place`](#adding-a-place-place)
  - [Listing All Places: `places`](#listing-all-places-places)
  - [Searching Places: `findplace`](#searching-places-findplace)
  - [Deleting a Place: `deleteplace`](#deleting-a-place-deleteplace)
- [General Features](#general-features)
  - [Exiting the Application: `bye`](#exiting-the-application-bye)
  - [Theme Toggle: Dark Mode](#theme-toggle-dark-mode)
  - [Data Persistence](#data-persistence)
- [Command Summary](#command-summary)

---

## Quick Start

1. Ensure that you have **Java 25** installed on your system.
2. Download the latest `tbc.jar` from the releases page.
3. Place `tbc.jar` into an empty working folder.
4. Open a terminal, navigate to the folder containing the file, and launch the application:
   ```bash
   java -jar tbc.jar
   ```
   > **Note**: To suppress Java 25 native library access warnings in the terminal, run with:
   > ```bash
   > java --enable-native-access=javafx.graphics -jar tbc.jar
   > ```
5. Type your command into the text box and press **Enter** (or click **Send**) to execute it.

---

## Command Format Conventions

* Words in `UPPER_CASE` represent parameters supplied by the user.
  * Example: In `todo DESCRIPTION`, `DESCRIPTION` is a parameter such as `read book`.
* Parameters in square brackets `[ ... ]` are optional.
  * Example: `place NAME [/details DETAILS]`
* Date and time arguments can be formatted as:
  * `yyyy-MM-dd` (e.g. `2026-10-15`)
  * `yyyy-MM-dd HHmm` (e.g. `2026-10-15 1800`)
* The pipe character `|` is reserved for data storage and cannot be used in descriptions, details, or keywords.
* Commands and parameters are separated by spaces. Leading and trailing whitespace are automatically trimmed.
* Item indices (`INDEX`) must be positive integers starting from `1` matching the currently displayed list.

---

## Task Management Features

### Adding a To-Do: `todo`

Adds a simple task without a specific deadline or schedule.

* **Format**: `todo DESCRIPTION`
* **Example**: `todo read textbook chapter 4`
* **Expected Output**:
  ```text
  Got it. I've added this task:
    [T][ ] read textbook chapter 4
  Now you have 1 tasks in the list.
  ```

### Adding a Deadline: `deadline`

Adds a task that must be completed by a specific date or time.

* **Format**: `deadline DESCRIPTION /by DATE_TIME`
* **Parameters**:
  * `DATE_TIME`: Accepts `yyyy-MM-dd` or `yyyy-MM-dd HHmm`.
* **Example**: `deadline submit assignment /by 2026-10-15 1800`
* **Expected Output**:
  ```text
  Got it. I've added this task:
    [D][ ] submit assignment (by: Oct 15 2026, 6:00 PM)
  Now you have 2 tasks in the list.
  ```

### Adding an Event: `event`

Adds a task that spans a start and end date/time. The start time must not be later than the end time.

* **Format**: `event DESCRIPTION /from START_DATE_TIME /to END_DATE_TIME`
* **Parameters**:
  * `START_DATE_TIME` and `END_DATE_TIME`: Accept `yyyy-MM-dd` or `yyyy-MM-dd HHmm`.
* **Example**: `event team sprint planning /from 2026-10-16 1400 /to 2026-10-16 1600`
* **Expected Output**:
  ```text
  Got it. I've added this task:
    [E][ ] team sprint planning (from: Oct 16 2026, 2:00 PM to: Oct 16 2026, 4:00 PM)
  Now you have 3 tasks in the list.
  ```

### Listing All Tasks: `list`

Displays all current tasks stored in your list along with their status, type, and chronological details.

* **Format**: `list`
* **Expected Output**:
  ```text
  Here are the tasks in your list:
  1. [T][ ] read textbook chapter 4
  2. [D][ ] submit assignment (by: Oct 15 2026, 6:00 PM)
  3. [E][ ] team sprint planning (from: Oct 16 2026, 2:00 PM to: Oct 16 2026, 4:00 PM)
  ```

### Marking a Task as Done: `mark`

Marks the task at the specified index as completed (`[X]`).

* **Format**: `mark INDEX`
* **Example**: `mark 1`
* **Expected Output**:
  ```text
  Nice! I've marked this task as done:
    [T][X] read textbook chapter 4
  ```

### Marking a Task as Incomplete: `unmark`

Reverts the task at the specified index back to incomplete (`[ ]`).

* **Format**: `unmark INDEX`
* **Example**: `unmark 1`
* **Expected Output**:
  ```text
  OK, I've marked this task as not done yet:
    [T][ ] read textbook chapter 4
  ```

### Deleting a Task: `delete`

Permanently removes the task at the specified index from the task list.

* **Format**: `delete INDEX`
* **Example**: `delete 2`
* **Expected Output**:
  ```text
  Noted. I've removed this task:
    [D][ ] submit assignment (by: Oct 15 2026, 6:00 PM)
  Now you have 2 tasks in the list.
  ```

### Searching Tasks by Keyword: `find`

Finds and lists all tasks whose description contains the specified search keyword (case-sensitive search).

* **Format**: `find KEYWORD`
* **Example**: `find sprint`
* **Expected Output**:
  ```text
  Here are the matching tasks in your list:
  1. [E][ ] team sprint planning (from: Oct 16 2026, 2:00 PM to: Oct 16 2026, 4:00 PM)
  ```

### Undoing Previous Action: `undo`

Reverts the most recent task list or place list mutation (such as an addition, deletion, mark, or unmark).

* **Format**: `undo`
* **Expected Output**:
  ```text
  Got it. I've undone the previous command.
  Now you have 3 tasks in the list.
  ```

---

## Place Management Features

### Adding a Place: `place`

Adds a location bookmark with optional descriptive details or notes.

* **Format**: `place NAME [/details DETAILS]` or `place NAME [/desc DETAILS]`
* **Parameters**:
  * `NAME`: Non-empty place name.
  * `DETAILS`: Optional descriptive notes or address.
* **Examples**:
  * `place Sentosa`
  * `place Jumbo Seafood /details Clark Quay, chili crab`
* **Expected Output**:
  ```text
  Got it. I've added this place:
    Jumbo Seafood (details: Clark Quay, chili crab)
  Now you have 1 places in the list.
  ```

### Listing All Places: `places`

Displays all saved places currently in your place list.

* **Format**: `places`
* **Expected Output**:
  ```text
  Here are the places in your list:
  1. Sentosa
  2. Jumbo Seafood (details: Clark Quay, chili crab)
  ```

### Searching Places: `findplace`

Searches places by keyword across both place names and details (case-insensitive search).

* **Format**: `findplace KEYWORD`
* **Example**: `findplace crab`
* **Expected Output**:
  ```text
  Here are the matching places in your list:
  1. Jumbo Seafood (details: Clark Quay, chili crab)
  ```

### Deleting a Place: `deleteplace`

Removes the place at the specified index from the place list.

* **Format**: `deleteplace INDEX`
* **Example**: `deleteplace 1`
* **Expected Output**:
  ```text
  Noted. I've removed this place:
    Sentosa
  Now you have 1 places in the list.
  ```

---

## General Features

### Exiting the Application: `bye`

Exits the session and terminates the chatbot application.

* **Format**: `bye`
* **Expected Output**:
  ```text
  Bye bye.
  ```

### Theme Toggle: Dark Mode

Toggles the visual color scheme between light mode and dark mode.

* **Action**: Click the theme toggle button in the top right corner of the window.
* **Behavior**:
  * In light mode (default), the button displays **Dark Mode**. Clicking it switches the background, chat bubbles, and text controls to a dark color palette.
  * In dark mode, the button displays **Light Mode**. Clicking it returns the window to the default light theme.

### Data Persistence

All tasks and places are automatically saved to local text files after each mutating command:
* **Tasks**: saved to `./data/tbc.txt`
* **Places**: saved to `./data/places.txt`

Data files are created automatically if they do not exist. Any existing saved records are loaded when the application launches.

---

## Command Summary

| Action | Format | Example |
| :--- | :--- | :--- |
| **Add To-Do** | `todo DESCRIPTION` | `todo read book` |
| **Add Deadline** | `deadline DESCRIPTION /by DATE_TIME` | `deadline return book /by 2026-10-15 1800` |
| **Add Event** | `event DESCRIPTION /from START /to END` | `event workshop /from 2026-10-16 1400 /to 2026-10-16 1600` |
| **List Tasks** | `list` | `list` |
| **Mark Task** | `mark INDEX` | `mark 1` |
| **Unmark Task** | `unmark INDEX` | `unmark 1` |
| **Delete Task** | `delete INDEX` | `delete 2` |
| **Find Task** | `find KEYWORD` | `find assignment` |
| **Undo Mutation** | `undo` | `undo` |
| **Add Place** | `place NAME [/details DETAILS]` | `place Sentosa /details Island resort` |
| **List Places** | `places` | `places` |
| **Find Place** | `findplace KEYWORD` | `findplace resort` |
| **Delete Place** | `deleteplace INDEX` | `deleteplace 1` |
| **Exit** | `bye` | `bye` |