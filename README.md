# Margit task manager

![Margit interface](docs/Ui.png)

Margit, the Fell Task Keeper, is a desktop task manager for recording,
reviewing, and planning your tasks.

## Command summary

| Command | Format | Purpose |
| --- | --- | --- |
| Add todo | `todo DESCRIPTION` | Adds an undated task. |
| Add deadline | `deadline DESCRIPTION /by DATE_OR_TIME` | Adds a task due by a date or date-time. |
| Add event | `event DESCRIPTION /from DATE_OR_TIME /to DATE_OR_TIME` | Adds an event with a start and end. |
| List tasks | `list` | Shows all saved tasks. |
| Mark task | `mark INDEX` | Marks a listed task as complete. |
| Unmark task | `unmark INDEX` | Restores a listed task as incomplete. |
| Delete task | `delete INDEX` | Permanently removes a listed task. |
| Find tasks | `find KEYWORD` | Finds task descriptions containing a keyword. |
| View a date | `on DATE` | Shows scheduled tasks that occur on a date. |
| Sort tasks | `sort` | Permanently sorts tasks into chronological order. |
| Exit | `bye` | Ends the terminal session or shows a GUI farewell. |

See the [full User Guide](docs/README.md) for command examples, date formats,
and detailed behaviour.

## Prerequisites

Use Java 25. Keep `src/main/java` as the Java source root.

## Build a fat JAR

From the repository root, run:

```powershell
.\gradlew.bat shadowJar
```

Gradle compiles the application and packages its runtime dependencies into a single executable fat JAR at:

```text
build/libs/margit.jar
```

The `build/` directory is ignored by Git, so do not commit this generated JAR. Publish it as a GitHub Release asset when you need to share it.

## Run the JAR

Copy `build/libs/margit.jar` to an empty folder, open PowerShell in that folder, and run:

```powershell
java -jar margit.jar
```

Running it from an empty folder prevents its `data/Margit.txt` save file from mixing with files from another run.

## Run tests

```powershell
.\gradlew.bat test
```
