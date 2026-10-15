# koko.Koko project

This is a greenfield Java project named koko.Koko. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/koko.Koko.java` file, right-click it, and choose `Run koko.Koko.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    _  __     _          
   | |/ /___ | | _____   
   | ' // _ \| |/ / _ \  
   | . \ (_) |   < (_) | 
   |_|\_\___/|_|\_\___/  
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Commands

Commands are case-sensitive. Dates and times use the format `yyyy-MM-dd HHmm`.

| Command | Purpose |
| --- | --- |
| `todo DESCRIPTION` | Adds a to-do task. |
| `deadline DESCRIPTION /by DATE_TIME` | Adds a deadline. |
| `event DESCRIPTION /from DATE_TIME /to DATE_TIME` | Adds an event. |
| `list` | Lists all tasks. |
| `find KEYWORD` | Lists tasks whose descriptions contain the keyword. |
| `mark TASK_NUMBER` | Marks a task as done. |
| `unmark TASK_NUMBER` | Marks a task as not done. |
| `delete TASK_NUMBER` | Deletes a task. |
| `update TASK_NUMBER FIELDS` | Updates only the supplied fields of an existing task. |

### Updating a task

Use `/desc` to replace a description. Use `/by` for a deadline, and `/from` or `/to` for an event. Fields not supplied remain unchanged.

```text
update 1 /desc read chapter 3
update 2 /desc submit final draft /by 2026-09-18 2359
update 3 /to 2026-09-20 1800
update 3 /from 2026-09-20 1400 /to 2026-09-20 1800
```

The task type and completion status are preserved. For example, updating only an event's `/to` time keeps its description and `/from` time unchanged. A field that does not apply to the selected task type, a repeated field, missing field value, invalid date-time, or an event ending before it starts is rejected.
