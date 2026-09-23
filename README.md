# Max User Guide

Max is a task-management application for keeping track of todos, deadlines, and
events. Tasks are saved automatically, so they are available the next time Max
is started.

## Setting up in Intellij

Prerequisites: JDK 25 and the latest version of IntelliJ IDEA.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate `src/main/java/max/ui/Launcher.java`, right-click it, and choose `Run 'Launcher.main()'`. If the setup is correct, the Max window should appear.

### Running from the command line

From the project root, run:

```powershell
.\gradlew.bat run
```

This starts Max with the JavaFX user interface. To build a distributable JAR,
run:

```powershell
.\gradlew.bat shadowJar
```

The JAR is created at `build/libs/max.jar`.

## User guide

Enter commands in the input box and submit them. Dates must use the
`yyyy-MM-dd` format, for example `2026-09-17`. Times are optional and use
`HH:mm`, for example `2026-09-17 14:30`. Task numbers are shown in the task
list and start at 1.

### Adding tasks

| Command | Description | Example |
| --- | --- | --- |
| `todo DESCRIPTION` | Adds a task without a date. | `todo Read chapter 3` |
| `deadline DESCRIPTION /by DATE [TIME]` | Adds a task with a due date and optional time. | `deadline Submit report /by 2026-09-20 18:00` |
| `event DESCRIPTION /from DATE [TIME] /to DATE [TIME]` | Adds an event spanning a date/time range. | `event Project meeting /from 2026-09-18 10:00 /to 2026-09-18 11:00` |

### Viewing and finding tasks

| Command | Description | Example |
| --- | --- | --- |
| `list` | Displays all tasks. | `list` |
| `sort` | Sorts tasks by deadline, earliest first. | `sort` |
| `on DATE` | Shows deadlines and events occurring on a date. | `on 2026-09-18` |
| `find KEYWORD` | Shows tasks whose descriptions contain the keyword. | `find report` |
| `help` | Displays the available commands. | `help` |

### Updating tasks

Replace `NUMBER` with the task number shown by `list`:

| Command | Description | Example |
| --- | --- | --- |
| `mark NUMBER` | Marks a task as done. | `mark 1` |
| `unmark NUMBER` | Marks a task as not done. | `unmark 1` |
| `delete NUMBER` | Deletes a task. | `delete 1` |

Use `bye` to close Max. The task list is saved automatically after each
successful command in `src/data/Max.txt`.

### Example session

```text
todo Read chapter 3
deadline Submit report /by 2026-09-20
list
mark 1
find report
bye
```

## Troubleshooting

- Ensure that the project is configured to use **JDK 25**, not an older JDK.
- If IntelliJ shows stale compilation errors after setup, restart the IDE and
  reload the Gradle project.
- Keep Java source files under `src/main/java` and resources under
  `src/main/resources`, as expected by Gradle.
