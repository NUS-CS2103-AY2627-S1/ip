# Verity User Guide

Verity is a task manager that helps you organise tasks and client contact
information using simple text commands. Tasks can have no clients, one client,
or multiple clients.

## Quick start

Verity requires Java 25.

### Running the JAR file

If you have been given `verity.jar`, open a terminal in the folder containing
the JAR and run:

```bash
java -jar verity.jar
```

Keep the terminal open while using Verity. The application stores its data in
a `data` folder relative to the directory from which it is launched.

### Building the JAR from source

From the project directory, run:

```bash
./gradlew shadowJar
```

On Windows, use:

```bat
gradlew.bat shadowJar
```

The generated JAR will be located at:

```text
build/libs/verity.jar
```

Run it from the project directory with:

```bash
java -jar build/libs/verity.jar
```

On Windows, use:

```bat
java -jar build\libs\verity.jar
```

### Using Verity

Enter a command in the text box and press **Enter** or click **Send**.

In the examples below, words written in `UPPER_CASE` are placeholders that you
should replace with your own values.

## Managing tasks

### Adding a task

Add a task without a date:

```text
todo DESCRIPTION
```

Example:

```text
todo Read project brief
```

Add a task with a deadline:

```text
deadline DESCRIPTION /by YYYY-MM-DD
```

Example:

```text
deadline Submit report /by 2026-09-30
```

Add an event with an inclusive date range:

```text
event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD
```

Example:

```text
event Project meeting /from 2026-09-20 /to 2026-09-20
```

Dates must use the `yyyy-MM-dd` format and must be valid calendar dates. An
event's end date cannot be before its start date. Same-day events are allowed.

### Viewing and updating tasks

| Action | Command | Example |
| --- | --- | --- |
| List all tasks | `list` | `list` |
| Mark a task as completed | `mark TASK_NUMBER` | `mark 1` |
| Mark a task as incomplete | `unmark TASK_NUMBER` | `unmark 1` |
| Delete a task | `delete TASK_NUMBER` | `delete 2` |
| Find tasks by description | `find KEYWORD` | `find report` |
| Find tasks occurring on a date | `finddate YYYY-MM-DD` | `finddate 2026-09-30` |
| Exit Verity | `bye` | `bye` |

Task numbers are displayed by the `list` command.

Task-description searches are case-insensitive and match part of a description.
For example, `find report` matches both `Write report` and `Submit REPORT`.

`finddate` matches deadlines due on the given date and events whose date range
includes that date.

## Managing clients

Client IDs such as `C001` are generated automatically. They are
case-insensitive, permanent, and are not reused after deletion.

### Adding a client

A client must have a name. All other fields are optional:

```text
client add /name Alice Tan
```

You can provide several fields in the same command:

```text
client add /name Alice Tan /phone +65 9123 4567 /email alice@example.com /company Acme /preferred email
```

Available fields are:

| Field | Requirement |
| --- | --- |
| `/name` | Required; up to 100 letters or spaces |
| `/phone` | Optional; 3–32 characters with at least three digits |
| `/email` | Optional; up to 254 characters with exactly one `@` and no spaces |
| `/address` | Optional; up to 200 characters |
| `/company` | Optional; up to 100 characters |
| `/notes` | Optional; up to 2000 characters |
| `/preferred` | Optional; must be `phone`, `email`, or `other` |

Phone numbers and email addresses must be unique when supplied. Email
comparison is case-insensitive. Phone comparison ignores spaces, parentheses,
periods, and hyphens.

Use `\n` in notes to enter a new line and `\\` to enter a literal backslash:

```text
client add /name Alice Tan /notes Called\nRequested a quotation
```

### Viewing and finding clients

List all clients, sorted by name:

```text
client list
```

View a client's details and assigned tasks:

```text
client view C001
```

Find clients using a case-insensitive partial name:

```text
client find alice
```

Only client names are searched.

### Editing a client

Supply only the fields that should change:

```text
client edit C001 /phone +65 9876 5432 /preferred phone
```

Fields that are not supplied remain unchanged.

Clear optional fields with `/clear`:

```text
client edit C001 /clear phone /clear address
```

The client ID and name cannot be cleared.

### Associating clients with tasks

Associate a client with one or more existing tasks:

```text
client associate C001 /task 1
```

```text
client associate C001 /task 1 /task 2
```

Dissociate a client from one or more tasks:

```text
client dissociate C001 /task 1
```

A client cannot be associated with the same task twice. Verity will also
prevent you from dissociating the last client assigned to a task.

### Assigning clients while adding tasks

The `/client` marker is optional and can be repeated:

```text
todo Prepare invoice /client C001 /client C002
```

```text
deadline Submit proposal /by 2026-09-30 /client C001
```

```text
event Client meeting /from 2026-09-20 /to 2026-09-20 /client C001
```

Every supplied client ID must already exist.

### Deleting a client

Request deletion with:

```text
client delete C001
```

Verity will display a warning without changing any data. Confirm permanent
deletion with:

```text
client delete C001 confirm
```

Deleting a client does not delete their tasks. Verity removes the client's
active task associations and adds a former-client note to each affected task.

## Errors and saved data

If a command is invalid, Verity explains the problem and remains open so that
you can correct the command and try again.

Common causes of errors include:

- missing descriptions, dates, task numbers, or client IDs;
- invalid or repeated command markers;
- invalid calendar dates;
- task or client numbers that do not exist;
- duplicate phone numbers or email addresses; and
- event end dates that are earlier than their start dates.

Tasks are saved automatically in `data/verity.txt`. Clients are saved in
`data/clients.txt`.

Missing data files are treated as empty first-run data. If existing data cannot
be read or is corrupted, Verity reports the problem without overwriting the
affected file. After repairing the file, enter another command to retry loading
it.

If saving fails, Verity reports the error, restores the previous data, and
continues accepting commands.
