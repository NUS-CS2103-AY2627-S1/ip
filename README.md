# Hu9o project template

This is a project template for a greenfield Java project. It's named after the Java mascot Hu9o. Given below are instructions on how to use it.

## Running Hu9o

* **GUI (default):** `./gradlew run`, or run `hu9o.gui.Launcher`. A chat window opens; type a command and press Enter or click **Send**.
* **Text CLI:** run `hu9o.Hu9o` to use Hu9o from the terminal instead.

Both front ends share the same task logic and the same `data/taskData.txt` save file.

## Features

Hu9o is a task tracker. It understands these commands:

| Command | Format | Description |
| --- | --- | --- |
| `todo` | `todo DESCRIPTION` | Add a todo. |
| `deadline` | `deadline DESCRIPTION /by DATE` | Add a task with a deadline (`DATE` as `dd/MM/yy h[mm] a`, e.g. `12/08/26 3 PM`). |
| `event` | `event DESCRIPTION /from START /to END` | Add a task that spans a period. |
| `list` | `list` | Show every task. |
| `find` | `find KEYWORD` | Show the tasks whose description contains `KEYWORD` (case-insensitive). |
| `mark` / `unmark` | `mark INDEX` / `unmark INDEX` | Mark a task done / not done. |
| `delete` | `delete INDEX` | Remove a task. |
| `bye` | `bye` | Save and exit. |

Example:

```
find book
_________________________________

Here are the matching tasks in your list:
1. [T][X] read book
2. [D][X] return book (by: 06 Jun 2026, 3 PM)
_________________________________
```

## Setting up in Intellij

Prerequisites: JDK 25 (the Azul Zulu "FX" build, which bundles JavaFX), update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/hu9o/Hu9o.java` file, right-click it, and choose `Run Hu9o.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
```
 _   _           ___ 
| | | | | | | | / _ \   ___  
| |_| | | | | || (_) | / _ \ 
|  _  | | |_| | \__,| | (_) |
|_| |_|  \___/   /_/   \___/ 

```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## AI declaration

This project used **Claude Sonnet 5** (via Claude Code) as a general coding assistant — implementing several optional feature increments, adding tests, and drafting the User Guide.
