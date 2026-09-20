# Record User Guide

Record is a desktop task manager that keeps to-dos, deadlines, and events together in a conversational interface. This guide assumes you are using Record for the first time.

![Record main window](Ui.png)


## Quick start

1. Install Java 25.
2. Download the appropriate Record JAR using the guide below.
3. Open a terminal in the folder containing the downloaded JAR and launch Record:

   ```text
   java --enable-native-access=javafx.graphics -jar record-VERSION-common.jar
   ```

   Replace `VERSION` and the JAR name with the filename you downloaded. Your previously saved tasks load automatically.
4. Type a command in the box at the bottom.
5. Press **Enter** or select **Send**.
6. Select **List** at any time to see all tasks and their completion checkboxes.

### Choosing a download

Use `record-VERSION-common.jar` by default. It supports x64 Windows, macOS, and Linux in one file. The platform-specific downloads are smaller, while the ARM64 downloads are required for native ARM64 support.

| Your computer | Recommended download | Reason |
| --- | --- | --- |
| Windows with an Intel or AMD 64-bit processor | `record-VERSION-common.jar` or `record-VERSION-windows-x64.jar` | Both contain the required Windows x64 JavaFX libraries. |
| Intel Mac | `record-VERSION-common.jar` or `record-VERSION-mac-x64.jar` | Both contain the required macOS x64 JavaFX libraries. |
| Apple-silicon Mac, such as M1, M2, or M3 | `record-VERSION-mac-arm64.jar` | It contains JavaFX libraries compiled for Apple silicon. |
| Linux reporting `x86_64` or `amd64` | `record-VERSION-common.jar` or `record-VERSION-linux-x64.jar` | Both contain the required Linux x64 JavaFX libraries. |
| Linux reporting `aarch64` or `arm64` | `record-VERSION-linux-arm64.jar` | It contains JavaFX libraries compiled for ARM64 Linux. |

Do not download `ip-VERSION-without-dependencies.jar` for normal use. It does not include JavaFX and is intended for development rather than direct launching.

On macOS, open **Apple menu > About This Mac** to check whether the processor is Intel or Apple silicon. On Linux, run `uname -m` to see the architecture. Linux also requires a desktop graphical environment and the system GTK libraries used by JavaFX.

Commands are not case-sensitive, but task descriptions keep their capitalization. Dates use `yyyymmdd`; times use 24-hour `hh:mm`. An omitted time means `00:00` (midnight).

## Command summary

| Action | Format | Example |
| --- | --- | --- |
| Add a to-do | `todo DESCRIPTION [DATE] [TIME] [/priority PRIORITY]` | `todo Buy groceries 20260920 18:30 /priority 2` |
| Add a deadline | `deadline DESCRIPTION /by DATE [TIME] [/priority PRIORITY]` | `deadline Submit report /by 20260921 23:59 /priority high` |
| Add an event | `event DESCRIPTION /from DATE [TIME] /to DATE [TIME] [/priority PRIORITY]` | `event Project meeting /from 20260922 14:00 /to 20260922 15:00` |
| Show tasks | `list` or the **List** button | `list` |
| Complete a task | `mark NUMBER` or select its checkbox | `mark 2` |
| Reopen a task | `unmark NUMBER` or clear its checkbox | `unmark 2` |
| Delete a task | `delete NUMBER` | `delete 2` |
| Clear the conversation | `clear` | `clear` |
| Show command help | `help` | `help` |
| Exit and save | `bye` | `bye` |

Capitalized words are placeholders; do not type the labels themselves. Items in square brackets are optional.

## Adding a to-do

Use a to-do for a task without a deadline:

```text
todo Read chapter 4
```

You may put an optional schedule at the end of the description:

```text
todo Call Sam 20260920 19:30
```

Set an optional priority with `/priority`:

```text
todo Buy concert tickets /priority high
```

## Adding a deadline

Use `/by` followed by a date and optional time:

```text
deadline Submit assignment /by 20260925 23:59
```

The `/by` option is required. Invalid dates such as `20260230` and times such as `25:00` produce a warning.

## Adding an event

Events require both a start and an end:

```text
event Team presentation /from 20260926 10:00 /to 20260926 11:30
```

Options may appear in any order, so this is also valid:

```text
event /to 20260926 11:30 /priority 1 Team presentation /from 20260926 10:00
```

The end must be at or after the start. Each of `/from`, `/to`, and `/priority` may appear at most once; Record warns you about duplicates.

## Setting priority

Priority is optional and defaults to Medium. Use its name or number:

| Number | Name |
| --- | --- |
| 1 | High |
| 2 | Medium-High |
| 3 | Medium |
| 4 | Low-Medium |
| 5 | Low |

Names are not case-sensitive. `/priority LOW` and `/priority 5` are equivalent.

## Viewing and updating tasks

Enter `list` or select **List**. Every row shows its number, type (`T`, `D`, or `E`), completion state, description, priority, and relevant dates.

![Interactive task list](Task_list.png)

Select a checkbox to complete a task, or use its displayed number:

```text
mark 1
unmark 1
delete 1
```

After deletion, later numbers shift up. Open the list again if you are unsure of a number.

## Conversation controls

- Press **Up** and **Down** in the command box to revisit recent commands. Record retains the latest 100 commands for the session.
- Enter `clear` to remove visible conversation messages. This does **not** delete tasks.
- Enter `help` to display a concise summary of every supported command.
- Use the avatar selector to change the avatar beside new messages.

## Saving and recovery

Record loads `data/listdata.txt` at startup and saves there when you enter `bye`. Missing folders are created automatically. The storage component also supports current-folder, parent-folder, and nested relative paths.

Save files use a human-readable CSV format. The first line must be exactly:

```text
# Record save format v2
```

The supported records are:

```text
T,DONE,PRIORITY,"DESCRIPTION"
T,DONE,PRIORITY,"DESCRIPTION",SCHEDULED_DATE_TIME
D,DONE,PRIORITY,"DESCRIPTION",DEADLINE
E,DONE,PRIORITY,"DESCRIPTION",START_DATE_TIME,END_DATE_TIME
```

`DONE` is `0` or `1`, priority is `1` to `5`, and date-time fields use ISO format such as `2026-09-20T18:30`. Descriptions are quoted CSV fields. To include a double quote, type it twice; commas and apostrophes need no special treatment. For example:

```text
T,0,3,"Buy milk, bread, and \"\"special\"\" cheese"
```

You may add correctly formatted records in a plain-text editor. Blank lines and lines beginning with `#` after the format header are ignored. Older storage formats are not supported.

Record rejects unreadable, oversized, corrupted, and unsupported save files instead of partially importing them. If loading fails, check file permissions and confirm that every record follows the format above.

Use `bye` before closing whenever possible so the latest changes are saved.

## Input limits

- Commands: at most 10,100 characters.
- Task descriptions: at most 10,000 characters.
- Task list: at most 100,000 tasks.
- Save file: at most 256 MB.
- Command history: latest 100 commands.

These limits are designed to keep retained data below 1 GB and should not affect normal use.

## Common errors

| Problem | What to do |
| --- | --- |
| Empty or unknown command | Enter `help` or type a command from the summary. |
| Missing description | Add text describing the task. |
| Missing `/by`, `/from`, or `/to` | Add the required option and date. |
| Invalid date or time | Use a real `yyyymmdd` date and optional 24-hour `hh:mm` time. |
| Repeated option | Keep only one copy of that slash option. |
| Event end before its start | Correct `/from` or `/to`. |
| Invalid item number | Enter `list`, then use a displayed number. |
| Save file cannot be read | Check that `data/listdata.txt` is a readable file and that you have access permission. |

## Exiting

Enter `bye`. Record saves the list, shows its farewell message, and closes.
