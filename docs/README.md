# Zinc User Guide

Zinc is a chatbot for managing tasks and contacts from a desktop application. Type a
command, and Zinc will help you keep your plans and people organised without missing a
beat.

![The Zinc chatbot interface](Ui.png)

## <span style="color: #7B61FF;">Feature Navigation</span>

**<span style="color: #B56A00;">General:</span>**
[View help](#general-help) ·
[Change the background](#general-background) ·
[Exit Zinc](#general-exit) ·
[Saving data](#saving-data) ·
[Full command list](#command-summary)

**<span style="color: #7B61FF;">Tasks:</span>**
[Add a todo](#task-todo) ·
[Add a deadline](#task-deadline) ·
[Add an event](#task-event) ·
[List tasks](#task-list) ·
[Find tasks](#task-find) ·
[Mark a task](#task-mark) ·
[Unmark a task](#task-unmark) ·
[Delete a task](#task-delete)

**<span style="color: #008C9E;">Contacts:</span>**
[Add a contact](#contact-add) ·
[List or find contacts](#contact-list) ·
[Update a contact](#contact-update) ·
[Delete a contact](#contact-delete)

## <span style="color: #008C9E;">Quick Start</span>

### System Requirements

- Java 25
- Windows, macOS, or Linux with a graphical desktop

Follow these steps to download and start Zinc:

1. Ensure that Java 25 or later is installed.
2. Download `zinc.jar` from [here](https://github.com/yeoznc/ip/releases/tag/A-Release).
3. Place `zinc.jar` in a dedicated folder for Zinc to store data.
4. Open a terminal in that folder.
5. Run Zinc:

   ```shell
   java -jar zinc.jar
   ```
6. Select the **Message Zinc...** field and enter a command, such as
   `todo Borrow a guitar`.
7. Press <kbd>Enter</kbd> or select the **>** button. Read Zinc's response in the
   conversation area.
8. Use the interface shortcuts when needed:
   - **Command List** opens Zinc's built-in command reference.
   - **Contact List** displays all saved contacts.
   - **Task List** displays all saved tasks.
   - **New conversation** clears the visible conversation and starts a fresh one. It
     does not delete tasks or contacts.
9. Use the **Up Arrow** and **Down Arrow** keys in the command field to navigate
   commands entered during the current session.
10. Enter `bye` or select **Exit** when you are ready to close Zinc.

When launched this way, Zinc creates a `data` directory beside `zinc.jar` to store your
tasks and contacts. Developers who want to build Zinc from source can follow the
[project README](../README.md).

## <span style="color: #B56A00;">Command Notation</span>

This guide uses the following notation:

- Words in angle brackets, such as `<description>`, are values you must supply.
- Words in square brackets, such as `[/p <phone number>]`, are optional.
- Dates must use `DD/MM/YY`, for example `16/09/26`.
- Times use the 24-hour clock in either `HHMM` or `HH:MM` form, for example `1830`
  or `18:30`. Zinc uses `00:00` when a time is omitted.
- Task numbers start at `1` and are shown by the `list` command.

Commands and field prefixes are not case-sensitive. Task descriptions, contact names,
and other values retain the spelling and capitalisation you enter.

## <span style="color: #7B61FF;">Managing Tasks</span>

Zinc supports todos, deadlines, and events. It automatically saves every change.

<a id="task-todo"></a>

### <span style="color: #7B61FF;">Adding a todo: `todo`</span>

Adds a task without a date or time.

Format: `todo <description>`

Example:

```text
todo Borrow a guitar
```

Zinc adds the todo to the task list and reports the updated number of tasks.

<a id="task-deadline"></a>

### <span style="color: #7B61FF;">Adding a deadline: `deadline`</span>

Adds a task that must be completed by a particular date and, optionally, time.

Format: `deadline <description> /by <DD/MM/YY> [HHMM or HH:MM]`

Examples:

```text
deadline Submit project report /by 20/09/26
deadline Buy concert tickets /by 20/09/26 18:30
```

If no time is supplied, the deadline is stored at midnight at the start of that date.

<a id="task-event"></a>

### <span style="color: #7B61FF;">Adding an event: `event`</span>

Adds an event with a start and end. The end cannot be earlier than the start.

Format: `event <description> /from <DD/MM/YY> [HHMM or HH:MM] /to <DD/MM/YY> [HHMM or HH:MM]`

Example:

```text
event Band rehearsal /from 18/09/26 1900 /to 18/09/26 21:00
```

The `/from` field must appear before `/to`. A missing time is stored as midnight.

<a id="task-list"></a>

### <span style="color: #7B61FF;">Listing tasks: `list`</span>

Displays every task and its number.

Format: `list`

Example:

```text
list
```

Use the task numbers in the displayed list with `mark`, `unmark`, and `delete`. `ls` is
a shorter alias for listing every task.

To list deadlines due and events ending on a particular date, add a date:

```text
list 20/09/26
```

Todos are not included in date-filtered results because they have no end date. Filtered
results retain their original task numbers.

<a id="task-find"></a>

### <span style="color: #7B61FF;">Finding tasks: `find`</span>

Finds tasks whose descriptions contain a keyword or phrase.

Format: `find <keyword>`

Example:

```text
find guitar
```

The search is case-sensitive. For example, `find guitar` does not match `Guitar lesson`.

<a id="task-mark"></a>

### <span style="color: #7B61FF;">Marking a task as complete: `mark`</span>

Marks a task as completed using its number from the task list.

Format: `mark <task number>`

Example:

```text
mark 2
```

<a id="task-unmark"></a>

### <span style="color: #7B61FF;">Marking a task as incomplete: `unmark`</span>

Changes a completed task back to incomplete.

Format: `unmark <task number>`

Example:

```text
unmark 2
```

<a id="task-delete"></a>

### <span style="color: #7B61FF;">Deleting a task: `delete`</span>

Permanently removes a task using its number from the task list.

Format: `delete <task number>`

Example:

```text
delete 2
```

Task numbers can change after a deletion, so use `list` again before performing another
number-based action.

## <span style="color: #008C9E;">Managing Contacts</span>

Each contact has a required name and optional phone number and description. Phone
numbers, when supplied, must contain exactly eight digits. Contact names can contain
spaces, but each saved name must be unique.

The shorter `ct` alias can replace `contact` in any contact command.

<a id="contact-add"></a>

### <span style="color: #008C9E;">Adding a contact: `contact add`</span>

Format: `contact add /n <name> [/p <8-digit phone number>] [/d <description>]`

Examples:

```text
contact add /n Joan Jett
contact add /n Freddie Mercury /p 87654321 /d Lead vocalist
```

The `/p` and `/d` fields can be omitted. Zinc displays `-` for an omitted value when it
lists the contact.

<a id="contact-list"></a>

### <span style="color: #008C9E;">Listing and finding contacts: `contact list`</span>

Displays all saved contacts when no name is supplied.

Format: `contact list [name]`

Examples:

```text
contact list
contact list Joan Jett
```

Supplying a keyword to search for returns contacts whose name contains that keyword in a case-insensitive manner.
You can also use `contact ls`, `ct list`, or `ct ls`.

<a id="contact-update"></a>

### <span style="color: #008C9E;">Updating a contact: `contact update`</span>

Updates one or more fields of the contact with the supplied current name. Fields that
you omit keep their existing values.

Format: `contact update <current name> [/n <new name>] [/p <8-digit phone number>] [/d <description>]`

Examples:

```text
contact update Joan Jett /p 81234567
contact update Freddie Mercury /n Freddie /d Singer and songwriter
```

The current name must match exactly, including capitalisation. At least one replacement
field is required. Use `ct update` as a shorter alias.

<a id="contact-delete"></a>

### <span style="color: #008C9E;">Deleting a contact: `contact delete`</span>

Permanently removes the contact with the supplied exact name.

Format: `contact delete /n <name>`

Example:

```text
contact delete /n Joan Jett
```

The aliases `contact del`, `ct delete`, and `ct del` perform the same action.

## <span style="color: #B56A00;">General Features</span>

<a id="general-help"></a>

### <span style="color: #B56A00;">Viewing help: `help`</span>

Enter `help` or select **Command List** to open Zinc's in-app command reference. The
reference groups commands into task, contact, and general categories and shows their
formats and aliases.

<a id="general-background"></a>

### <span style="color: #B56A00;">Changing the background: `ui background`</span>

Changes the main window's background for the current session.

Format: `ui background <morning|evening|night|auto>`

Examples:

```text
ui background night
ui background auto
```

Use `ui bg` as a shorter alias. In `auto` mode, Zinc selects the background using the
computer's local time:

- **Morning:** 06:00 to 17:59
- **Evening:** 18:00 to 21:59
- **Night:** 22:00 to 05:59

<a id="general-command-history"></a>

### <span style="color: #B56A00;">Navigating command history</span>

Zinc remembers commands entered in the command field during the current application
session. 

Press the **Up Arrow** to restore older commands and the **Down Arrow**
to move towards newer commands. 

Command history is cleared when Zinc closes.

<a id="general-exit"></a>

### <span style="color: #B56A00;">Exiting Zinc: `bye`</span>

Enter `bye` or select **Exit** to close Zinc safely.

```text
bye
```

<a id="saving-data"></a>

## <span style="color: #008C9E;">Saving Data</span>

Zinc automatically saves task changes to `data/zincTasks.txt` and contact changes to
`data/zincContacts.txt`. It restores both lists the next time it starts. The `data/`
directory is excluded from Git, keeping personal data out of repository commits.

Do not manually edit the storage files. If Zinc detects malformed saved data at startup,
it displays a warning and starts the affected list empty.

<a id="command-summary"></a>

## <span style="color: #7B61FF;">Command Summary</span>
<a id = "command-summary"></a>

| Purpose | Command |
| --- | --- |
| Add a todo | `todo <description>` |
| Add a deadline | `deadline <description> /by <DD/MM/YY> [HHMM or HH:MM]` |
| Add an event | `event <description> /from <DD/MM/YY> [HHMM or HH:MM] /to <DD/MM/YY> [HHMM or HH:MM]` |
| List tasks | `list` |
| List tasks ending on a date | `list <DD/MM/YY>` |
| Find tasks | `find <keyword>` |
| Mark or unmark a task | `mark <task number>` / `unmark <task number>` |
| Delete a task | `delete <task number>` |
| Add a contact | `contact add /n <name> [/p <8-digit phone number>] [/d <description>]` |
| List or find contacts | `contact list [name]` |
| Update a contact | `contact update <current name> [/n <new name>] [/p <8-digit phone number>] [/d <description>]` |
| Delete a contact | `contact delete /n <name>` |
| Open help | `help` |
| Change the background | `ui background <morning\|evening\|night\|auto>` |
| Navigate command history | **Up Arrow** / **Down Arrow** |
| Exit Zinc | `bye` |
