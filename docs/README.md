# FRIDAY User Guide

FRIDAY is a JavaFX mission assistant for managing tasks and contacts. Its chat window accepts the same task commands as the legacy console UI.

## Quick start

1. Install **Java 25.0.3.fx-zulu** and open the project root in IntelliJ as a Gradle project.
2. Select **application > run** in IntelliJ, or run `./gradlew --quiet --console=plain run` from the project root.
3. In the chat window, enter `hello`, `todo buy groceries`, `list`, and `help`.

FRIDAY saves task changes automatically. Use the number shown by `list` with `mark`, `unmark`, and `delete`.
Enter `bye` when you are finished; close and reopen FRIDAY to begin another session.

See [First conversation](#first-conversation) for a guided example, or jump to the
[Command reference](#command-reference).

## Setup and running

Use **Java 25.0.3.fx-zulu**. Gradle support is included through the committed wrapper,
which downloads **Gradle 9.6.1** on first use; no global Gradle installation is needed.
Java 25 requires Gradle 9.1 or newer, so do not substitute an older installed Gradle.

From the project root on macOS/Linux with SDKMAN installed:

```bash
sdk use java 25.0.3.fx-zulu
./gradlew --version
./gradlew clean build
./gradlew --quiet --console=plain run
```

`--version` should show Gradle 9.6.1 and the selected Java 25.0.3 JVM. `clean build` should
finish with `BUILD SUCCESSFUL`; `run` opens FRIDAY's chat window. Enter a command and press **Enter**
or select **Send**; try `hello`, then `bye`. On Windows use `gradlew.bat` (or `.\gradlew.bat` in PowerShell)
instead of `./gradlew`, and set `JAVA_HOME` to the required JDK before running it.
The first download needs internet access; subsequent runs can reuse the cached distribution.

In IntelliJ, import the project root using `build.gradle`, use the **Gradle wrapper**, and set
both **Project SDK** and **Gradle JVM** to **25.0.3.fx-zulu**. Reload the Gradle project, then run
**application > run**. For an existing project that will not import, close IntelliJ, back up and
move aside `.idea` and `.iml` files, and reopen the root as a Gradle project, following
[scenario 2 of the course tutorial](https://se-education.org/guides/tutorials/gradle.html).
No IDE files are removed automatically.

Keep `src/main/java` as the source root; `src`, `main`, and `java` are not Java packages.
Gradle launches `friday.Launcher`, which starts the JavaFX `friday.Main` application. The separate
`friday.Friday` entry point remains available for the command-driven console regression runner. Gradle's `run`
task uses the project root as its working directory, so existing data remains at `data/friday.txt`; no migration is needed.
Generated output is under the ignored `build` directory. `clean` removes that output, not task data.

## Gradle troubleshooting

- **Wrong Java version / unsupported class-file version:** check `java -version` and
  `./gradlew --version`, select the required SDK, and set IntelliJ's Gradle JVM to match.
  Stop stale daemons with `./gradlew --stop` and reload the project.
- **Could not find the main class:** reload `build.gradle` and use `friday.Launcher`, not `Duke`.
- **Permission denied on `./gradlew`:** restore the executable bit with `chmod +x gradlew`.
- **Download failure:** check internet/proxy access and retry. Do not disable the wrapper's
  checksum verification to work around a failed or mismatched download.
- **No tests run:** JUnit classes belong under `src/test/java` with `@Test` or
  `@ParameterizedTest` methods. Reload the Gradle project and run `./gradlew test --rerun-tasks`.
  A fresh run must discover tests; `NO-SOURCE` is not the expected result after A-JUnit.

## For developers

Run `./gradlew test` for automated checks and `./gradlew desktopTest` for opt-in JavaFX checks.
Detailed setup, test, and CI guidance is in the repository [README](../README.md).

## Managing contacts

Use the same commands in the GUI chat or console. In the formats below, `NAME` and `KEYWORD` are
nonblank text, `PHONE` is a Singapore mobile number, `EMAIL` is an email address, and `NUMBER` is
an existing positive contact number. Square brackets mean an optional part; do not type the brackets.

| Command | Accepted format | Result |
| --- | --- | --- |
| Add | `contact add NAME /phone PHONE [/email EMAIL]` | Adds a contact with a phone number, optionally with an email address. |
| Add | `contact add NAME /email EMAIL [/phone PHONE]` | Adds a contact with an email address, optionally with a phone number. |
| List | `contact list` | Shows all contacts in insertion order. |
| Find | `contact find KEYWORD` | Shows contacts containing the literal keyword in a name, phone number, or email address. |
| Delete | `contact delete NUMBER` | Removes the numbered contact and renumbers later contacts. |

For example:

```text
contact add Alice Tan /phone 91234567 /email alice@example.com
contact add Bob /email bob@example.com
contact list
contact find EXAMPLE
contact delete 1
```

Require a name and at least one of `/phone` or `/email`. Names are unique ignoring case after
trimming surrounding whitespace. The name comes before the fields; fields may appear in either
order, once each, and cannot be empty. Command words and prefixes are lowercase. Singapore phone
numbers contain exactly eight digits starting with 6, 8, or 9, without separators or `+65`.
Email addresses require one `@`, no spaces, and a dotted domain; FRIDAY does not verify delivery.
Control characters and unknown field prefixes are rejected. To correct a contact, delete and add it again.

Contacts appear in insertion order. Search matches a literal substring in any individual field,
ignoring case, and preserves the original contact numbers. Contact numbers are independent of task
numbers; deleting one contact renumbers later contacts. Missing phone/email details display as `-`.
Internal spaces remain significant: `Alice Tan` and `Alice  Tan` are different names, and searches
do not merge their spaces. Field prefixes are whitespace-delimited tokens beginning with `/`;
unknown prefixes are errors. A slash inside a word, such as `Alice/Bob`, is ordinary text.

For an empty contact list, adding Alice with both details produces this response:

```text
____________________________________________________________
Contact logged. Here's the entry:
  Alice Tan (phone: 91234567, email: alice@example.com)
Now you have 1 contact in the list.
```

After adding Bob from the example above, `contact find BOB@` returns his original number:

```text
____________________________________________________________
Contact scan complete. Here's what matches:
Use the number shown here with contact delete.
2.Bob (phone: -, email: bob@example.com)
```

Use `contact delete 2` to remove Bob. An empty list reports `Your contact directory is empty.`; a search
without matches reports `Contact scan complete. No matches found.` Neither operation writes to the save file.

| Invalid example | Reason and correction |
| --- | --- |
| `contact add Alice` | Supply `/phone PHONE`, `/email EMAIL`, or both. |
| `contact add Alice /phone 91234567 /email` | Omit an unused field instead of leaving its value blank. |
| `contact add Alice /phone +6591234567` | Use `91234567`, without the country code. |
| `contact add Alice /email alice@` | Use an address such as `alice@example.com`. |
| `contact add Alice /phone 91234567 /phone 81234567` | Supply each prefix only once. |
| `contact add ALICE TAN /phone 81234567` after adding Alice Tan | Choose a distinct name; changing case does not make it unique. |
| `contact find` | Supply a nonblank search keyword or phrase. |
| `contact delete 0`, `contact delete 1 2` | Supply one existing positive contact number. |
| `contact list extra`, `contact edit 1` | Use a supported command; editing is not provided. |

Invalid commands do not change contacts or either save file. Add-command errors are checked in
this order: command structure, phone syntax, email syntax, then duplicate name.

### Contact storage and recovery

Contacts save automatically to the separate UTF-8 file `data/contacts.txt`; no task-file migration is
needed. Records have the form `C|NAME|PHONE|EMAIL`, with an empty optional field for a missing detail.
Literal pipes and backslashes are escaped as `\|` and `\\`. A missing or empty file starts an empty list.
Invalid records or duplicate names reject the whole file: FRIDAY warns and disables contact saving
until you repair the file and restart. Contact changes remain in memory in that session. Task saving
is independent. Ordinary contact write failures retain memory and retry on the next contact mutation.

Example records, including missing optional fields:

```text
C|Alice Tan|91234567|alice@example.com
C|Bob||bob@example.com
C|Carol|81234567|
```

Names containing `|` or `\` are encoded with `\|` or `\\`; for example, the name `Alice | Tan`
is stored as `Alice \| Tan`. Unknown escapes, extra/missing fields, invalid values, or duplicate
names make the entire contact file unreadable. FRIDAY never loads a partial contact list.

If loading fails, back up `data/contacts.txt`, repair its records (or move it aside to start an
empty list), then restart FRIDAY. While saving is disabled, new contacts and deletions exist only
in memory and will not survive restart. Repairing the file without restarting does not re-enable saving.

If an ordinary save fails, check the destination and folder permissions. The current session retains
your changes; the next successful contact addition or deletion saves its complete contact list.
Listing and searching do not retry writes. Do not close FRIDAY before saving if you need those changes.
Snapshots are written to a temporary file before replacement; task records are not rewritten by contact commands.

## First conversation

Type a command in the GUI text field, then press **Enter** or select **Send**. FRIDAY supports todos, deadlines,
events, searches, and a few helper commands. The chat shows your command on the right and FRIDAY's response on the left.

### Try a short conversation

On a fresh task list, submit these commands one at a time:

```text
hello
todo Review the mission briefing
list
mark 1
thanks
help
```

FRIDAY greets you with “At your service. What's the next task?”, acknowledges the new task with
“Task logged. Here's the next objective:”, and confirms completion with
“Objective complete. Nicely handled:”. If you already have tasks, use the number shown by `list`
for the newly added task instead of `1`. These commands create and save a real task.

Try `todo` without a description to see a **Command error** panel, then enter a valid command to
continue. Enter `bye` last: it ends the session and disables the input and Send button.

## Command reference

Commands are case-sensitive; use the lowercase names shown below. Replace uppercase placeholders
with your own values. Square brackets indicate optional fields; do not type the brackets.

| Command | Purpose |
| --- | --- |
| `todo DESCRIPTION` | Add an undated task. |
| `deadline DESCRIPTION /by DEADLINE` | Add a task with a due date/time. |
| `event DESCRIPTION /from START /to END` | Add an event with a start and end. |
| `list` | Show all tasks and their current numbers. |
| `find KEYWORD` | Search task descriptions using a case-sensitive literal phrase. |
| `on yyyy-MM-dd` | Show deadlines and events on a date. |
| `mark TASK_NUMBER` | Mark a task as complete. |
| `unmark TASK_NUMBER` | Reopen a completed task. |
| `delete TASK_NUMBER` | Remove a task. |
| `contact add NAME /phone PHONE [/email EMAIL]` | Add a contact with a phone and optional email. |
| `contact add NAME /email EMAIL` | Add an email-only contact. |
| `contact list` | Show the contact directory. |
| `contact find KEYWORD` | Search contact fields without regard to case. |
| `contact delete NUMBER` | Remove a contact using its displayed number. |
| `hello` | Ask FRIDAY for a greeting. |
| `thanks` | Receive an acknowledgment. |
| `help` | Show the command briefing in the conversation. |
| `bye` | End the session. |

For date/time examples, see [Dates and times](#dates-and-times).
For phone and email rules, see [Managing contacts](#managing-contacts).

## Using the GUI

FRIDAY uses a dark navy, cyan, and gold theme with original user and FRIDAY AI-core avatars.
Your commands appear in compact bubbles on the right; assistant replies use wider panels on the left.

- **Command error** panels use a red border and tint. Read the explanation, correct your command, and submit again.
- **Warning** panels use amber styling for storage problems. A successful task change can still carry a warning
  when it exists only in memory. Follow the existing file-recovery instructions before closing FRIDAY.
- Empty lists and searches are ordinary replies, not errors.
- The window opens at 480 × 640 pixels. Resize it as needed, down to 360 × 440 pixels.
  Replies wrap, and the input area stays at the bottom.
- Scroll upward to read older messages. Submitting a new command scrolls to the latest reply.
- Press **Enter** or click **Send**; focus returns to the input for the next command. Blank input does nothing.
- After `bye`, input and Send are disabled. Close and reopen FRIDAY to begin another session.

![Compact commands and clear errors](images/friday-default.png)

![Long replies at the minimum window size](images/friday-minimum.png)

See [artwork provenance](images/artwork.md) for the generated avatar credits and prompts.

## Adding todos

Use `todo` for tasks with no date or time.

Example:
```text
todo borrow book
```

FRIDAY adds the task as a todo item.

## Adding deadlines

Use `deadline` when a task has a due date.

Example:
```text
deadline return book /by 2019-12-02
```

FRIDAY stores the deadline as a `LocalDateTime` and displays `Dec 02 2019`.

## Adding events

Use `event` for tasks with a start and end time.

Example:
```text
event project meeting /from 2019-12-02 14:00 /to 2019-12-02 16:00
```

FRIDAY stores the start and end as `LocalDateTime` values. The end cannot be before the start;
equal endpoints are allowed. Give a full date for both endpoints, including events within one day.

## Dates and times

Deadlines and event endpoints accept these formats:

| Input format | Example | Display |
| --- | --- | --- |
| `yyyy-MM-dd` | `2019-10-15` | `Oct 15 2019` |
| `yyyy-MM-dd HH:mm` | `2019-12-02 18:00` | `Dec 02 2019, 18:00` |
| `d/M/yyyy HHmm` | `2/12/2019 1800` | `Dec 02 2019, 18:00` |

Slash-separated dates are day/month/year, so `2/12/2019` means 2 December.
Times use the 24-hour clock. Date-only input means midnight; midnight is displayed as a date alone.
These are local dates and times without a timezone. Month names always display in English.
Invalid dates (such as `2019-02-29`), invalid times (such as `24:00`), and vague dates (such as
`Sunday`) produce an error without adding a task. The ISO storage form `2019-12-02T18:00` is also accepted.

## Listing tasks

Use `list` to show all current tasks.

Example:
```text
list
```

FRIDAY shows each task with its number. Use that number for `mark`, `unmark`, and `delete`.

## Finding tasks on a date

Use `on yyyy-MM-dd` to show deadlines and events on a specific date:

```text
on 2019-12-02
```

Deadlines match their due date. Events match every calendar date from their start date through their
end date, including both boundary dates (even an end at midnight). Completed tasks are included;
undated todos are excluded. The displayed numbers are the original list numbers, so they still work
with `mark`, `unmark`, and `delete`. This command does not change or save the task list.

## Finding tasks by description

Use `find KEYWORD`, for example `find book`, to search task descriptions. Matching is a
case-sensitive literal substring search: `book` matches `read book` and `bookshelf`, but
not `Book`. All task types and completed tasks are included. Dates, task-type markers,
and completion markers are not searched.

Surrounding keyword whitespace is trimmed. Multiple words are treated as one exact phrase,
so `find read book` searches for `read book`, not either word separately. Blank keywords
produce `Invalid find format. Use: find KEYWORD`; no matches produce `Task scan complete. No matches found.`

Results retain their original list numbers, just like date queries. For example, if task 1
does not match, the first result might be numbered 2. Use these numbers with `mark`, `unmark`,
or `delete`. Search does not change the task list or write to the save file.

## Marking tasks

Use `mark` to mark a task as done.

Example:
```text
mark 2
```

Use `unmark` to change it back to not done.

Example:
```text
unmark 2
```

## Deleting tasks

Use `delete` to remove a task from the list.

Example:
```text
delete 3
```

## Saving and loading

Run FRIDAY with the project root as the working directory (including in IntelliJ).
Tasks load automatically at startup and save after each addition, deletion, or completion-status change
to `data/friday.txt`, relative to that directory. No save command or `bye` is required.
FRIDAY creates the `data` folder and file on the first change if they do not exist.
Local task data is excluded from Git.

The UTF-8 text format uses one task per line, with `0` for not done and `1` for done:

```text
T|1|read book
D|0|return book|2019-12-02T00:00
E|0|project meeting|2019-12-02T14:00|2019-12-02T16:00
```

Fields are separated by `|` with no added spaces. Within text fields, `\|` means a literal pipe,
`\\` a backslash, `\n` a newline, and `\r` a carriage return. Empty files represent empty lists.
Date fields use ISO date-time values with minute precision, independently of the console display.

Older Level 7 records still load when their date fields match a supported format.
Text such as `Sunday` or `Mon 2pm` cannot be converted without guessing a date.
If your save contains such text, back up the file and replace those fields with explicit dates
before restarting. FRIDAY will preserve an unreadable or invalid original file as described below.

If the file is malformed or unreadable, FRIDAY starts with an empty session list and displays a warning.
Saving stays disabled for that session to protect the original file; any new changes remain only in memory.
Back up and repair the file (or move it aside to start fresh), then restart FRIDAY.
If a save fails, FRIDAY warns you and keeps the session running. Fix the folder or file permissions;
the next task-list change retries saving the full list. Successful saves write a temporary snapshot before
replacing the old file, using an atomic move when the filesystem supports it.

## A-Personality: FRIDAY Mission Assistant

FRIDAY is calm, capable, and occasionally dryly witty. The **FRIDAY | Mission Control** window pairs
this voice with navy surfaces, cyan highlights, restrained gold details, and compact existing avatars.
The header reads “Mission control for your tasks and contacts”.

| Interaction | FRIDAY's reply |
| --- | --- |
| Startup | FRIDAY online. Let's keep your day under control. |
| `hello` | At your service. What's the next task? |
| `thanks` | Anytime. Organization is rather my specialty. |
| Add a task | Task logged. Here's the next objective: |
| `mark 1` | Objective complete. Nicely handled: |
| `unmark 1` | Task reopened. Back on the radar: |
| `delete 1` | Task removed from your agenda: |
| `bye` | FRIDAY signing off. I'll be here when you return. |

`help` displays a command briefing covering tasks, contacts, searches, dates, and session commands.
Task and contact numbers and command syntax retain their usual meaning. Unknown commands suggest
`help`; other validation errors retain their specific correction details. Storage warnings continue
to identify recovery steps and when changes exist only in memory.

![FRIDAY mission assistant](images/friday-default.png)

The [artwork provenance](images/artwork.md) still applies; this personality update uses original
wording and the existing portraits, with no additional external project ideas or artwork.

## Error handling

FRIDAY gives clear messages for invalid input. For example, it handles:

- empty `todo` descriptions
- unknown commands
- missing deadline or event details
- missing or invalid task numbers for `mark`, `unmark`, and `delete`

This keeps the chatbot from silently accepting bad input.
