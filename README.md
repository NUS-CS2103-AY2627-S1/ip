# Luna

Luna is a JavaFX task manager with a chatbot-style interface. It keeps active
tasks between sessions and supports todos, deadlines, events, searching,
completion tracking, deletion, and archiving.

## Requirements

- JDK 25
- IntelliJ IDEA or a terminal with PowerShell, Command Prompt, Bash, or Zsh

## Run Luna

On Windows:

```powershell
.\gradlew.bat run
```

On macOS or Linux:

```bash
./gradlew run
```

If you use IntelliJ IDEA, import the repository as a Gradle project and set the
project SDK and language level to JDK 25.

## Commands

| Command | Description |
|---|---|
| `help` | Display the complete command guide |
| `todo <description>` | Add a todo |
| `deadline <description> /by <yyyy-MM-dd>` | Add a deadline |
| `event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>` | Add an event |
| `list` | Display active tasks |
| `find <keyword>` | Find active tasks by description |
| `mark <index>` | Mark an active task as completed |
| `unmark <index>` | Mark an active task as incomplete |
| `delete <index>` | Permanently delete an active task |
| `archive <index>` | Move one active task into the archive |
| `archive all` | Move all active tasks into the archive |
| `list archived` | Display archived tasks |
| `bye` | Exit Luna |

Indexes are one-based and match the numbers displayed by `list`.

## Data storage

Luna stores data locally in plain-text files:

- `data/luna.txt` contains active tasks.
- `data/archive.txt` contains archived tasks in archive order.

Archived tasks use the same timestamp-free format as active tasks. The archive
is append-only, while `delete` remains a permanent deletion.

## Test the project

Run the complete JUnit and Checkstyle suite on Windows:

```powershell
.\gradlew.bat check
```

On macOS or Linux:

```bash
./gradlew check
```

See the [user guide](docs/README.md) for complete command instructions and the
[archive test plan](tests/test-plan.md) for acceptance scenarios.
