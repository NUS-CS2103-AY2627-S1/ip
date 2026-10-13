# Wangsa User Guide

Wangsa helps you keep track of todos, deadlines, and events. Type a short command
to add a task, find it, or mark it done. Changes are saved automatically.

[Quick start](#quick-start) · [Command summary](#command-summary) ·
[Command details](#features) · [Saving tasks](#saving-tasks) ·
[Troubleshooting](#troubleshooting) · [Optional AI setup](#optional-ai-setup)

## Quick start

1. Install **[Java 25](https://adoptium.net/temurin/releases/?version=25)** if needed.
   Run `java -version` in a terminal to check.
2. Download `Wangsa.jar` from the
   [latest release](https://github.com/Manutd1234/ip/releases/latest) and put it
   in a folder you own, such as **Documents/Wangsa**.
3. Open a terminal in that folder and run `java -jar Wangsa.jar`.
4. Click the command box at the bottom. Type one complete command, then press
   **Enter** or click **SEND**. Do not type commands into the conversation area.

```shell
java -jar Wangsa.jar
```

No database setup, account, API key, or separate JavaFX installation is needed.

The JAR supports Windows x64, Linux x64, and Intel/Apple Silicon Macs.
Linux needs a graphical desktop with GTK 3. You do not need to install JavaFX
separately or set up AI to manage tasks.

Version 0.8 improves saved-data validation and keeps application writes inside
its launch folder. Existing `data/wangsa.txt` files from v0.6 and v0.7 remain
compatible. Always open the terminal in the folder containing the JAR and your
usual `data` folder before launching. The JavaFX interface and task commands
remain the same; terminal help retains the ASCII-safe punctuation from v0.7.

![Wangsa desktop with its task list and command box](Ui.png)

Messages are labelled **YOU** and **WANGSA**, with small arrow symbols to distinguish
the two sides. The app uses no external artwork or audio.

Try these commands one at a time, starting with an empty list:

```text
todo read the project brief
deadline submit report /by 2026-09-20
list
mark 1
```

You should have two tasks, with the first marked done.

Building the app yourself? See the
[developer README](https://github.com/Manutd1234/ip#run-from-source).

## Command summary

Replace capitalized placeholders with your own text. Each command has more detail below.

| Action | Format | Example |
| --- | --- | --- |
| Add todo | `todo DESCRIPTION` | `todo read a book` |
| Add deadline | `deadline DESCRIPTION /by YYYY-MM-DD` | `deadline return book /by 2026-09-20` |
| Add event | `event DESCRIPTION /from START /to END` | `event lunch /from 12pm /to 1pm` |
| List | `list` | `list` |
| Find | `find KEYWORD` | `find book` |
| Sort | `sort` | `sort` |
| Complete | `mark NUMBER` | `mark 2` |
| Reopen | `unmark NUMBER` | `unmark 2` |
| Delete | `delete NUMBER` | `delete 2` |
| Built-in help | `help` | `help` |
| AI help (optional) | `@ai QUESTION` | `@ai How do I add a deadline?` |
| Exit | `bye` | `bye` |

## Features

### Before you start

- Use lowercase command names and markers. Type `list`, not `/list`.
  Your task description can use capitals, such as `ST2334`.
- Replace placeholders with your own text: `todo DESCRIPTION` becomes
  `todo read a book`. Do not type the angle brackets shown in the desktop hints.
- Put spaces around `/by`, `/from`, and `/to`. Use each required marker once,
  in the order shown. These markers are reserved in deadline and event commands.
- Extra spaces around a command are ignored. Spaces or tabs can separate its
  parts; spaces inside descriptions are kept. Required values cannot be blank.
- Task numbers start at 1. `list` and `find` use the same numbers.
  Sorting or deleting can change them, so check the current list.
- The limit is **100 tasks**, including completed ones. Duplicate tasks are allowed.

The desktop shows **[To do]** or **[Done]** beside each task. Due dates and event
times appear underneath, aligned with the task text. Use the number at the start
of the task for `mark`, `unmark`, or `delete`.

The optional terminal interface uses compact markers: `[T]` for todo, `[D]` for
deadline, `[E]` for event, `[ ]` for incomplete, and `[X]` for complete.

### Adding a todo: `todo`

**Format:** `todo DESCRIPTION`

Example: `todo read a book`

Adds a task without a date to the end of the list: `[To do] read a book`.

![The todo command adds read a book as an incomplete task](screenshots/todo.jpg)

The screenshots use a small demo list from v0.6 on macOS. The JavaFX interface
is unchanged in v0.8. Your task numbers and counts depend on your own list, and
commands work the same way on supported systems.

### Adding a deadline: `deadline`

**Format:** `deadline DESCRIPTION /by YYYY-MM-DD`

Example: `deadline submit report /by 2026-09-20`

Adds `[To do] submit report`, with **Due: 20 Sep 2026** underneath.

- Keep the slash in `/by`, with a space on each side. Plain `by` does not work.
- Use `YYYY-MM-DD`: a four-digit year, two-digit month, and two-digit day.
- The date must exist. September has 30 days, so `2026-09-31` is invalid.
- Past dates are allowed. Times are not supported.

If you see an error, no task was added. Enter the corrected command in the box
at the bottom; you do not need to delete anything first.

![The deadline command adds submit report with Due: 20 Sep 2026 underneath](screenshots/deadline.jpg)

### Adding an event: `event`

**Format:** `event DESCRIPTION /from START /to END`

Example: `event project meeting /from Monday 2pm /to Monday 4pm`

Adds `[To do] project meeting`, with **From: Monday 2pm** and **To: Monday 4pm**
on separate lines.
Both start and end are required. They are stored as text: Wangsa does not check
their order or send reminders.

![The event command adds project meeting with separate start and end lines](screenshots/event.jpg)

### Listing all tasks: `list`

`list` shows every task, including completed tasks, with its current number.

![The list command shows a todo, deadline, and event numbered 1 to 3](screenshots/list.jpg)

### Finding tasks: `find`

**Format:** `find KEYWORD`

Example: `find book` matches `read book`, `BOOK flight`, and `booking tickets`.

Search ignores case and checks descriptions only, not dates or event times.
Several words form one phrase: `find read book` looks for that exact phrase.
Wangsa tells you if nothing matches.

Results keep their full-list numbers. If the only result is numbered 4, use
`mark 4`, not `mark 1`.

![The find book command returns read a book with its full-list number 2](screenshots/find.jpg)

In this example, the demo list has already been sorted. The matching task keeps
number **2**, even though it is the only search result.

### Sorting by deadline: `sort`

`sort` puts deadlines first, earliest to latest, then todos and events.
Tasks with equal dates keep their relative order, as do todos and events.
Completed tasks follow the same rule. The new order is saved and displayed.
Use `sort` on its own, not `sort /by date`. Leading or trailing spaces are allowed.

![The sort command moves submit report before the todo and event](screenshots/sort.jpg)

Compare this with the `list` screenshot: the deadline is now task **1** and
`read a book` is task **2**.

### Completing a task: `mark`

**Format:** `mark NUMBER`

Example: `mark 2` changes task 2 from **[To do]** to **[Done]**.
The task stays in the list. Marking it again has no further effect.

![The mark 2 command changes read a book to Done and increases the completed count](screenshots/mark.jpg)

### Reopening a task: `unmark`

**Format:** `unmark NUMBER`

Example: `unmark 2` changes task 2 from **[Done]** to **[To do]**.
Unmarking an incomplete task has no further effect.

![The unmark 2 command changes read a book back to To do](screenshots/unmark.jpg)

### Deleting a task: `delete`

**Format:** `delete NUMBER`

Example: `delete 2` removes task 2. Later tasks move up one number.

**Deletion is immediate and has no undo.** Run `list` first if you are unsure.

![The delete 2 command confirms removal of read a book and shows two remaining tasks](screenshots/delete.jpg)

### Exiting Wangsa: `bye`

`bye` closes the app. Successful changes are already saved.
Closing the window also keeps those changes.

### Built-in command help: `help`

`help` shows four groups: **Add tasks**, **View and find**, **Update tasks**, and
**Help and exit**. The desktop gives a short explanation and an example you can
type for each command. The terminal uses plain `-` bullets and `--` separators
so the same help remains readable in Windows consoles. Both work offline,
without an API key.

![The help command opens the built-in guide with command explanations and examples](screenshots/help.jpg)

The screenshot shows the start of the help reply. Scroll down to read the rest,
or enlarge the window to see more at once.

### Optional AI command help: `@ai`

**Format:** `@ai QUESTION`

Example: `@ai How do I add a deadline for my report?`

The AI explains commands and suggests examples. It **cannot change tasks or run
commands**. Answers can be wrong, so check suggestions against `help`.
Questions must be 1–1000 characters long.

Only your question and the command reference are sent to Groq—not saved tasks
or previous messages. Each question is independent. Avoid putting sensitive
information in a question.

Without a key, or if the service fails, Wangsa shows **Offline help**.
Normal task commands still work. See [Optional AI setup](#optional-ai-setup)
if you want to enable AI; it is not needed to manage tasks.

You can use task commands while the desktop is waiting for an answer, but only
one AI question can run at a time. The terminal waits for the answer before
processing another command. Closing the desktop cancels the request.

### Desktop shortcuts

- **list** shows your tasks immediately.
- **new todo**, **find**, **mark #**, and **ask AI** fill in a command for you to finish.
- **Up/Down** in the command box recall successful commands from this session.
- Resize the window or scroll to read longer conversations.
- Wangsa replies stay on the left and your messages stay on the right, even when maximized.
- Long replies open at their beginning. Scroll down to read the rest.
- Help groups stack into one column when there is less room.

The header shows total and completed task counts. Errors have a red border and
an **ACTION NEEDED** label. Tasks are saved, but the chat and command history
reset when the app closes.

## Saving tasks

Tasks save automatically to **`data/wangsa.txt` in the launch folder**.
The folder and file are created after your first change. There is no database
to install or set up. When you follow the quick-start steps, this is beside the JAR.
The desktop and terminal share this file; run one instance at a time.

When running from source, the save file is in the project's `data` folder.

Always launch from the folder containing your usual `data` folder. Version
0.7 finds saves beside the JAR even if you launch from somewhere else; version
0.8 uses the terminal's current folder instead. It does not move
or overwrite saves in another folder. To keep using existing tasks, change to
their folder before launching.

The packaged app also creates `.wangsa/javafx-natives`
in the launch folder. This contains bundled JavaFX libraries, not your tasks.
It can be removed while Wangsa is closed and will be recreated on the next launch.

To back up your tasks, close Wangsa and copy the entire `data` folder.
To restore them, close Wangsa and replace its `data` folder with the backup.
Keep the JAR and `data` folder together when moving the app. To update Wangsa,
close it and replace only the JAR, keeping your existing `data` folder.

Earlier database versions used `wangsa.db`. This version leaves that file
untouched and does not import it. Keep a backup if it contains tasks you need
to transfer. Existing valid `wangsa.txt` files still work.

If saving fails, the change is cancelled. If loading fails, the desktop shows
**STORAGE UNAVAILABLE** and blocks task commands to protect the saved data.
`help` and `bye` still work. The message gives the file path and, for malformed
data, the line to check. Back up the file before correcting it, or restore a
backup, then restart. The terminal exits after a storage error.

## Troubleshooting

| Problem | What to try |
| --- | --- |
| Unknown command or missing value | Use lowercase commands without a leading slash. Follow the formats above. `list`, `sort`, `help`, and `bye` take no extra values. |
| Deadline needs a `/by` date | Include the slash: `deadline ST2334 /by 2026-09-30`, not `deadline ST2334 by 2026-09-30`. |
| Invalid date | Check the calendar as well as the format. `2026-09-31` does not exist; use `2026-09-30` if you mean the last day of September. Re-enter the whole corrected command. |
| Invalid task number | Run `list` and use a number shown there. Add a task first if the list is empty. |
| Task list is full | Delete a task before adding another. Completed tasks count towards the limit. |
| Tasks seem missing | Open a terminal in the folder containing your usual `data` folder, then launch Wangsa from there. |
| Cannot load or save | Close other instances and check folder access. Back up the data before repairing it. |
| App will not open | Check Java 25 with `java -version`. Run `java -jar Wangsa.jar` in a terminal to see the error. Linux also needs GTK 3 and a graphical desktop. |
| Terminal help has odd punctuation | Download the latest release. Version 0.7 and later use ASCII punctuation in terminal help. The desktop help is unaffected. |
| AI shows offline help | Check the Groq key, connection, model access, and usage limits. Restart after changing settings. `help` works without AI. |

## Optional AI setup

Skip this section if you only want to manage tasks. Built-in `help` works offline.

1. Create a Groq API key in the [Groq console](https://console.groq.com/keys).
2. Open a terminal in the folder containing `Wangsa.jar` and run the commands
   for your system. Replace the example key with your own.

   macOS/Linux:

   ```shell
   export LLM_API_KEY='your-groq-api-key'
   java -jar Wangsa.jar
   ```

   Windows PowerShell:

   ```powershell
   $env:LLM_API_KEY='your-groq-api-key'
   java -jar Wangsa.jar
   ```

3. In Wangsa, try `@ai How do I add a deadline?`.

These settings apply to that terminal session. Close any already-running Wangsa
window before relaunching. Keep your real key out of source code and shared files.

In IntelliJ, use your run configuration's **Environment variables**. For a
source run, launch Gradle from the terminal where you set the variable.

The default model is `openai/gpt-oss-20b`, hosted by Groq. Set `LLM_MODEL` before
launching to choose another [supported Groq model](https://console.groq.com/docs/models).
Access, usage limits, and charges depend on your Groq account.
Requests have a 20-second timeout and no retries. See
[AI help](#optional-ai-command-help-ai) for privacy and limitations.

## Credits

The guide structure draws on the
[AddressBook Level 3 User Guide](https://se-education.org/addressbook-level3/UserGuide.html).
Development and documentation were assisted by OpenAI Codex with GPT-6 Astra.
See [Credits](https://manutd1234.github.io/ip/CREDITS.html) for AI usage, code sources,
libraries, and the Windows students who tested v0.6 and reported the terminal
follow-up fixed in v0.7. The interface symbols are drawn in code, without
external media.
