# Koara User Guide

> Your steady Singaporean sidekick for tasks and training clients.

![The Koara desktop app showing its full chat interface](Ui.png)

Koara is a desktop chatbot that keeps personal tasks and client records in one place. Type a command, press **Enter** (or click **LOCK IN**), and Koara replies in the conversation. Errors appear as highlighted cards so they are easy to spot.

## Quick start

1. Install **Java 25**.
2. Clone this repository and open a terminal in its root folder.
3. Build the app:
   - Windows: `gradlew.bat shadowJar`
   - macOS/Linux: `./gradlew shadowJar`
4. Start Koara with `java -jar build/libs/koara.jar`.

> **Command notation:** Words in `UPPERCASE` are placeholders. Replace each one with your own value; do not type brackets around it. Dates use `YYYY-MM-DD`.

## Manage tasks

### Add a to-do

Adds a task without a date.

- Format: `todo DESCRIPTION`
- Example: `todo prepare weekly review`

### Add a deadline

Adds a task that must be completed by a date.

- Format: `deadline DESCRIPTION /by YYYY-MM-DD`
- Example: `deadline submit progress report /by 2026-10-02`

### Add an event

Adds an activity with a start and end date. The end date cannot be before the start date.

- Format: `event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD`
- Example: `event strength workshop /from 2026-10-10 /to 2026-10-11`

### View and update tasks

Use the number shown by `list` when marking, unmarking, or deleting a task.

| Action | Format | Example |
| --- | --- | --- |
| Show every task | `list` | `list` |
| Mark as done | `mark NUMBER` | `mark 2` |
| Mark as not done | `unmark NUMBER` | `unmark 2` |
| Delete a task | `delete NUMBER` | `delete 3` |
| Find tasks by description | `find KEYWORD` | `find report` |

Task searches match the exact letter case. For example, `find Report` and `find report` can produce different results.

## Manage clients

Client commands keep a name, phone number, goal, and notes together. Phone numbers may begin with `+` and contain 7–16 digits; spaces are allowed.

### Add a client

- Format: `client add NAME /phone PHONE /goal GOAL /notes NOTES`
- Example: `client add Alex Tan /phone +65 9123 4567 /goal Run 5 km /notes Prefers evenings`

Koara prevents duplicate client names and phone numbers.

### View, find, edit, and delete clients

Use the number shown by `client list` when editing or deleting a client. Client searches are not case-sensitive and check all client details.

| Action | Format | Example |
| --- | --- | --- |
| Show every client | `client list` | `client list` |
| Find clients | `client find KEYWORD` | `client find alex` |
| Edit a client | `client edit NUMBER /name NAME /phone PHONE /goal GOAL /notes NOTES` | `client edit 1 /name Alex Tan /phone +65 9123 4567 /goal Run 10 km /notes Saturday mornings` |
| Delete a client | `client delete NUMBER` | `client delete 1` |

All four fields are required when adding or editing a client.

## Exit Koara

- Format: `bye`

Koara says goodbye and closes the application.

## Data and error handling

Koara saves changes automatically in the project folder:

- Tasks: `data/koara.txt`
- Clients: `data/clients.txt`

If either file does not exist, Koara starts with an empty list and creates the file when needed. If saved data cannot be read or a change cannot be written safely, Koara shows an error instead of crashing or silently losing the current state.

Extra spaces around or between command words are accepted. If a command is incomplete or invalid, read the highlighted error card, correct the value it identifies, and try again.

## Command summary

| Command | What it does |
| --- | --- |
| `todo DESCRIPTION` | Adds a to-do |
| `deadline DESCRIPTION /by YYYY-MM-DD` | Adds a deadline |
| `event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD` | Adds an event |
| `list` | Lists tasks |
| `mark NUMBER` / `unmark NUMBER` | Changes a task's completion status |
| `delete NUMBER` | Deletes a task |
| `find KEYWORD` | Finds tasks |
| `client add ...` | Adds a client |
| `client list` | Lists clients |
| `client find KEYWORD` | Finds clients |
| `client edit NUMBER /name ...` | Replaces a client's details |
| `client delete NUMBER` | Deletes a client |
| `bye` | Exits Koara |
