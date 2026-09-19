# Gary User Guide

Gary is your sea-snail task companion. Manage tasks, deadlines, events,
and contacts in one desktop chat window.

<img width="924" height="800" alt="image" src="https://github.com/user-attachments/assets/2bcc3cef-5423-4435-8077-f1e23f81679e" />

[Quick Start](#quick-start) · [Available Commands](#available-commands) ·
[Viewing Help](#viewing-help) · [Tasks](#tasks) · [Contacts](#contacts) · [Goodbye](#goodbye) ·
[Saving your data](#saving-your-data) · [Troubleshooting](#troubleshooting)

## Quick Start

1. Ensure you have **Java 25** installed on your computer. Verify your version by running:

   ```shell
   java -version
   ```

2. Download `gary.jar` from the [releases page](https://github.com/Azora04/ip/releases).
3. Move the JAR into an empty folder where you want Gary to store your tasks and contacts.
4. Open a terminal, navigate to that folder, and launch Gary:

   ```shell
   java -jar "gary.jar"
   ```

On macOS, use the terminal command above. Double-clicking a JAR may use a different
Java installation and only show a generic Console error. Check `java -version`
in that same terminal. Releases from v0.3 include both Intel and Apple Silicon
Mac libraries, as well as Windows x64 libraries, and are tested with Java 25.0.3.

Older releases may not include every feature in this guide. To build the current
version, download or clone the [repository](https://github.com/Azora04/ip), open a
terminal in the project folder, and run `gradlew.bat shadowJar` on Windows or
`./gradlew shadowJar` on macOS/Linux. The result is `build/libs/gary.jar`.

## Using Gary

Type your command into the bottom text field and press **Enter** or click **Send**.
Try `todo read book`, then `list`.

Resize the window to suit your screen. Use the mouse wheel to scroll through the
conversation, including over the input bar while typing. Errors appear in a
contrasting warning style; correct the command and submit it again.

### Available Commands

- `help` : Shows all available commands.
- `list` : Views all tasks.
- `todo` : Adds a task without a date.
- `deadline` : Adds a task with a due date.
- `event` : Adds an event with a start date and an end date.
- `mark` : Marks a task as completed.
- `unmark` : Marks a task as not completed.
- `delete` : Deletes a task by its index number.
- `find` : Finds tasks whose descriptions contain a keyword.
- `contact add` : Adds a contact with a name, phone number, and email.
- `contact list` : Views all contacts.
- `contact find` : Finds contacts by name, phone number, or email.
- `contact delete` : Deletes a contact by its index number.
- `bye` : Ends the conversation.

### Command basics

- Replace placeholders such as `DESCRIPTION` or `<description>` with your values;
  do not type the angle brackets. `<index>` means the task or contact number.
- Commands and markers such as `/by` ignore case; descriptions keep your casing.
- Dates use **YYYY-MM-DD**, for example `2026-12-25`. Times are not supported.
- Numbers start at **1**. Use the current number from `list` or `contact list`.
- Send one command at a time. Do not put quotation marks around descriptions.

## Viewing Help

Shows a concise reference of all supported commands.

- **Format:** `help`

Sample input:

```text
help
```

Sample output:

```text
Here are the available commands:
  - list : Views all tasks
  - todo <description> : Adds a todo task
  - deadline <description> /by <date> : Adds a deadline task
  - event <description> /from <start date> /to <end date> : Adds an event task
  - mark <index> : Marks a task as completed
  - unmark <index> : Marks a task as not completed
  - delete <index> : Deletes a task from the list
  - find <keyword> : Finds tasks by description
  - contact add <name> /phone <phone> /email <email> : Adds a contact
  - contact list : Views all contacts
  - contact find <keyword> : Finds contacts by name, phone, or email
  - contact delete <index> : Deletes a contact
  - help : Shows this command guide
  - bye : Ends the conversation
  - Date format: YYYY-MM-DD (e.g. 2026-09-30)
```

## Tasks

### Add a task

| Type | Format | Example |
| --- | --- | --- |
| To-do, without a date | `todo DESCRIPTION` | `todo read book` |
| Deadline | `deadline DESCRIPTION /by YYYY-MM-DD` | `deadline submit report /by 2026-09-30` |
| Event | `event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD` | `event project meeting /from 2026-09-20 /to 2026-09-21` |

Descriptions and dates must not be empty. An event may start and end on the same
date, but its end date cannot be earlier than its start date.

Adding a to-do to an empty list produces:

```text
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
```

### View and manage tasks

| Command | What it does |
| --- | --- |
| `list` | Show all tasks and their current numbers. |
| `mark 1` | Mark task 1 as done. |
| `unmark 1` | Mark task 1 as not done. |
| `delete 1` | Permanently remove task 1. There is no undo command. |
| `find book` | Find tasks whose descriptions contain `book`, ignoring case. |

`[T]`, `[D]`, and `[E]` mean to-do, deadline, and event.
`[X]` means done; `[ ]` means not done.

```text
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] submit report (by: 2026-09-30)
3.[E][ ] project meeting (from: 2026-09-20 to: 2026-09-21)
```

Search results retain the original task numbers. Deleting a task renumbers later
tasks, so run `list` again before acting on another number. `find` searches the
description, not the date. With no matches, only the search heading is displayed.

## Contacts

Add a contact with a name, phone number, and email address. All three are required,
and `/phone` must come before `/email`:

```text
contact add Alice Tan /phone 91234567 /email alice@example.com
```

Gary confirms the details and the new contact count.

| Command | What it does |
| --- | --- |
| `contact list` | Show all contacts and their numbers. |
| `contact find Alice` | Search names, phone numbers, and emails, ignoring case. |
| `contact delete 1` | Permanently remove contact 1. |

Contact numbers are separate from task numbers. Search results keep the original
numbers; deletion renumbers the contacts after the deleted entry. A search with
no matches displays only the search heading.

## Goodbye

Ends the conversation with a farewell from Gary.

- **Format:** `bye`

Sample input:

```text
bye
```

Sample output:

```text
Meow! Sea you again soon!
```

In the desktop interface, lowercase `bye` disables the input. Close the window
with its close button. Restart Gary to begin another conversation.

## Saving your data

Gary automatically saves changes in `data/gary.txt` and `data/contacts.txt`,
relative to the folder from which you launched the app. It loads those files
the next time you start it from that folder.

- On the first run, missing files mean empty lists. Gary creates the folder and
  files when you add data.
- To move Gary, copy the JAR **and the `data` folder**.
- Back up the `data` folder before editing or replacing its files.
- Gary rejects descriptions and contact fields containing a vertical bar (`|`) surrounded by spaces,
  because that sequence separates fields in the data files. Avoid editing data files while Gary is
  running; malformed records may be skipped on loading.

> If Gary cannot save, your latest changes may exist only in the current session.
> Keep a copy of the details and resolve the folder access problem before closing.

## Troubleshooting

| Problem | What to try |
| --- | --- |
| Java is not recognized, or its version is unsupported | Install Java 25, reopen the terminal, and check `java -version`. |
| Unable to access the JAR | Open the terminal in its folder and run `java -jar "gary.jar"`. |
| Unknown command | Type `help`. Use `contact list` for contacts and `list` for tasks. |
| Missing information or invalid format | Follow Gary's suggested syntax, including the required markers. |
| Invalid date | Use a real date with a two-digit day and month, such as `2027-01-05`. |
| Invalid task or contact number | Run the relevant list command and use a current positive number. |
| Lists are unexpectedly empty | Check you launched Gary from the usual folder and its `data` folder is present. |
| Unable to load or save | Check folder permissions and that data paths are files, not directories. Back up existing data before repairs. Load errors may appear in the terminal. |

Still stuck? [Report an issue](https://github.com/Azora04/ip/issues) with the command,
error message, and Java version. Omit private task and contact details.
