# Altair project template

This is a project template for a greenfield Java project. It's named after the Java mascot _Duke_. Given below are instructions on how to use it.

## Features

- Add three kinds of tasks: `todo`, `deadline` (with a due date), and `event`
  (with a start and end date).
- `list` all tasks, `mark` / `unmark` them as done, and `delete` them.
- `find` tasks whose description contains a keyword.
- Flags a new task whose description matches one already in the list and asks
  for confirmation before adding it, so accidental re-entry does not silently
  create a second copy.
- Automatic save to `./data/altair.txt` after every change, reloaded on start-up.
  A missing, corrupted, or unreadable save file does not stop the app: Altair
  reports what went wrong and still starts normally with an empty task list.
- Runs as a JavaFX GUI or as a plain text UI in the terminal; both share the same
  command handling.

Dates are entered in `yyyy-MM-dd` format and shown back as `MMM dd yyyy`.

## Usage

| Command | Example |
| --- | --- |
| `todo <description>` | `todo read book` |
| `deadline <description> /by <yyyy-MM-dd>` | `deadline return book /by 2025-10-15` |
| `event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>` | `event orientation /from 2025-08-01 /to 2025-08-03` |
| `list` | `list` |
| `mark <task number>` | `mark 2` |
| `unmark <task number>` | `unmark 2` |
| `delete <task number>` | `delete 2` |
| `find <keyword>` | `find book` |
| `bye` | `bye` |

The full user guide, with sample output for each command, is in
[`docs/README.md`](docs/README.md).

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/altair/Altair.java` file, right-click it, and choose `Run Altair.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:

```
   _____  .__   __         .__        
  /  _  \ |  | _/  |______ |__|______ 
 /  /_\  \|  | \   __\__  \|  \_  __ \
/    |    \  |__|  |  / __ \|  ||  | \/
\____|__  /____/|__| (____  /__||__|  
        \/                \/
```
**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
