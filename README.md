# Amy

Amy is a friendly task-management chatbot available through both a console
interface and a JavaFX graphical interface.

![Amy GUI](docs/Ui.png)

## Features

- Add three types of tasks:
  - `todo <description>` for a regular task.
  - `deadline <description> /by <date/time>` for a task with a deadline.
  - `event <description> /from <start> /to <end>` for an event.
- List all tasks with their type, completion status, and details using `list`.
- Mark a task as complete with `mark <task number>`.
- Mark a task as incomplete with `unmark <task number>`.
- Search task descriptions, without case sensitivity, using `find <keyword>`.
- Remove a task using `delete <task number>`.
- Undo the most recent successful task-changing command with `undo`.
  Amy asks for confirmation before applying the undo and accepts `yes`/`y` or
  `no`/`n`.
- Switch between light and dark GUI themes with `light mode` and `dark mode`.
- Persist tasks between launches in `~/.amy/amy.txt`.
- Start with an empty task list when the data file does not exist.
- Skip corrupted records while loading valid saved tasks.
- Handle unknown commands, missing arguments, invalid task numbers, invalid
  dates, and invalid undo confirmations with helpful messages.
- Display the product name **Amy** in the GUI title bar.
- Show a friendly startup error dialog if the GUI cannot be loaded.
- Exit the application with `bye`.

## Example commands

```text
todo buy groceries
deadline submit report /by 18/9/2026 1800
event team meeting /from 19/9/2026 1400 /to 19/9/2026 1600
list
mark 1
find report
undo
yes
bye
```

An undo confirmation includes the complete task details:

```text
Confirm undo: remove the task "[T][ ] buy groceries"? [yes/no]
```

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/amy/Amy.java` file, right-click it, and choose `Run Amy.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
   ________________________________________________________________________________
    A     m   m  y   y
   A A    mm mm   y y
  AAAAA   m m m    y
  A   A   m   m    y
   A   A   m   m    y
   Hello! I'm Amy.
   What can I do for you?
   ________________________________________________________________________________
   read book
   ________________________________________________________________________________
   added: read book
   ________________________________________________________________________________
   return book
   ________________________________________________________________________________
   added: return book
   ________________________________________________________________________________
   list
   ________________________________________________________________________________
   Here are the tasks in your list:
   1.[ ] read book
   2.[ ] return book
   ________________________________________________________________________________
   mark 2
   ________________________________________________________________________________
   Nice! I've marked this task as done:
     [X] return book
   ________________________________________________________________________________
   list
   ________________________________________________________________________________
   Here are the tasks in your list:
   1.[ ] read book
   2.[X] return book
   ________________________________________________________________________________
   unmark 2
   ________________________________________________________________________________
   OK, I've marked this task as not done yet:
     [ ] return book
   ________________________________________________________________________________
   bye
   ________________________________________________________________________________
   Bye. Hope to see you again soon!
   ________________________________________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
