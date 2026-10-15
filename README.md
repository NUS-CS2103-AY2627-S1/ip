# BenBot

BenBot is a JavaFX personal-assistant chatbot for tasks, contacts, notes, and
expenses. Type commands in its conversation window to organize your information
and save it between sessions.

## Features

- Create to-dos, deadlines, and events with dates and optional times.
- List tasks, search their descriptions, mark them done or not done, and delete
  them by number.
- Add, list, and delete contacts with names, phone numbers, and email addresses.
- Record, list, and delete short text notes.
- Track expenses, list their total, and delete individual entries. Amounts use
  exact decimal arithmetic and display with two decimal places.
- Keep separate numbered lists for tasks, contacts, notes, and expenses.
- Save all four lists on `bye` or when the JavaFX window closes, and reload them
  at startup.

## Run BenBot

Use **JDK 25**. From the project root on macOS or Linux, run:

```bash
./gradlew run
```

On Windows Command Prompt, use:

```bat
gradlew.bat run
```

The Gradle task opens BenBot's JavaFX window. Enter a command in the text field
and press Enter or select **Send**.

## Commands

Use lowercase command words and replace uppercase placeholders with your own
values. Square brackets indicate optional input; do not type the brackets.
Enter one command per line. Leading, trailing, and repeated whitespace is
normalized before a command is processed.

### Tasks

| Command | Purpose | Example |
| --- | --- | --- |
| `todo DESCRIPTION` | Add a to-do | `todo read book` |
| `deadline DESCRIPTION /by DATE [TIME]` | Add a deadline | `deadline submit report /by 15/9/2026 1800` |
| `event DESCRIPTION /from START /to END` | Add an event | `event team meeting /from 15/9/2026 1400 /to 15/9/2026 1600` |
| `list` | Show all tasks | `list` |
| `find KEYWORD` | Search task descriptions, ignoring case | `find book` |
| `mark TASK_NUMBER` | Mark a task as done | `mark 2` |
| `unmark TASK_NUMBER` | Mark a task as not done | `unmark 2` |
| `delete TASK_NUMBER` | Delete a task | `delete 2` |

Dates use `day/month/year`, such as `15/9/2026`. Optional times use the 24-hour
`HHmm` format, such as `1800`. Each event boundary (`START` and `END`) accepts a
date with an optional time; the end must be after the start.

BenBot stores up to 100 tasks. `find` matches text anywhere in a task description
and keeps the original task numbers in its results. It searches tasks only.
BenBot rejects an exact duplicate of a task already in the list.

### Contacts

| Command | Purpose |
| --- | --- |
| `contact NAME /phone PHONE /email EMAIL` | Add a contact |
| `contacts` | Show all contacts |
| `delete-contact CONTACT_NUMBER` | Delete a contact |

For example:

```text
contact Alice Tan /phone 91234567 /email alice@example.com
contacts
delete-contact 1
```

All three fields are required, with `/phone` before `/email`. Names can contain
spaces; phone numbers and email addresses must each be a single value without
spaces.

### Notes

| Command | Purpose |
| --- | --- |
| `note TEXT` | Record a nonempty text note |
| `notes` | Show all notes |
| `delete-note NOTE_NUMBER` | Delete a note |

For example:

```text
note Watch Arrival
notes
delete-note 1
```

Notes may contain multiple words. Whitespace between words is normalized to
single spaces.

### Expenses

| Command | Purpose |
| --- | --- |
| `expense DESCRIPTION /amount AMOUNT` | Add an expense |
| `expenses` | Show all expenses and their total |
| `delete-expense EXPENSE_NUMBER` | Delete an expense |

For example:

```text
expense bus fare /amount 2.40
expenses
delete-expense 1
```

Use a positive decimal amount such as `2`, `2.4`, or `2.40`, without a currency
symbol, sign, grouping separator, or scientific notation. Amounts may have at
most two decimal places; BenBot does not round fractional cents. Expense
displays and totals use `$`, with no currency conversion.

### Numbers and saving

Each list starts at 1. Use the number shown by `list`, `contacts`, `notes`, or
`expenses` with the corresponding command. Deleting an entry renumbers later
entries in that list. Task completion commands apply only to tasks.

Use `bye` to save all data and exit. Closing the JavaFX window also saves the
data. Changes are saved at exit, rather than after each command. If an exit save
fails, BenBot reports the problem and remains open so the storage location can
be fixed and the save retried.

All lists are stored in `data/stored-task` relative to the directory from which
BenBot is launched. Start BenBot from the same directory to reload the same
data. Task completion status is preserved too. BenBot validates the complete
stored file before loading it, so a corrupted line does not partially change
the live data. It will not overwrite a file that failed to load; fix that file
and restart BenBot. Saves replace the previous file only after a complete
temporary file has been written.

## Set up IntelliJ IDEA

1. Open the repository as a project in a recent IntelliJ IDEA version.
2. Configure the project SDK and Gradle JVM as JDK 25, with the project language
   level set to `SDK default`.
3. Allow Gradle to finish importing the project.
4. Run the `run` Gradle task, or run `benbot.Launcher.main()`.

Keep `src/main/java` as the source root so Gradle and IntelliJ IDEA can locate
the Java source files.

## Test and check the project

Run the tests and Checkstyle checks with JDK 25:

```bash
./gradlew check
```

GitHub Actions runs the same checks on Linux, macOS, and Windows for pushes and
pull requests. On Windows Command Prompt, replace `./gradlew` with `gradlew.bat`.

## Build the executable JAR

Create the JAR containing BenBot and its dependencies with:

```bash
./gradlew shadowJar
```

The generated file is `build/libs/benbot.jar`. With Java 25, start it using:

```bash
java -jar build/libs/benbot.jar
```

## AI usage acknowledgement

This project was developed with assistance from
[OpenAI Codex](https://developers.openai.com/codex/), accessed through ChatGPT.
Codex assisted with requirements analysis, Java implementation and refactoring,
automated tests, error handling and storage safety, documentation review, and
Git workflows. All AI-generated suggestions and code were reviewed, tested, and
accepted by the project author, who remains responsible for the final work. No
other AI tools were used.
