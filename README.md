# HappyBot

HappyBot is a chatbot project built from this greenfield Java project template. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
2. Open the project into Intellij as follows:
   1. Click `Open`.
   2. Select the project directory, and click `OK`.
   3. If there are any further prompts, accept the defaults.
3. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
4. After that, locate the `src/main/java/happybot/Launcher.java` file, right-click it, and choose
   `Run Launcher.main()` (if the code editor is showing compile errors, try restarting the IDE).
   IntelliJ then creates a `Launcher` run configuration, which you can start again using the green
   Run button in the toolbar. Alternatively, start the GUI from the project root with
   `./gradlew run`. If the setup is correct, the HappyBot chat window will open with a command field
   and a **Send** button.

HappyBot accepts a command when you press **Enter** or click **Send**. Its existing commands and
saved task data work in both the graphical and console interfaces. The console interface remains
available by running `HappyBot.main()` and starts with this message:

   ```text
   ____________________________________________________________
   H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
   H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
   HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
   H   H  A   A  P      P         Y    B   B  O   O    T
   H   H  A   A  P      P         Y    BBBB    OOO     T
   Hello! I'm HappyBot.
   How can I cheer you up today?
   ____________________________________________________________
   
   ```

## Saved data

HappyBot automatically loads tasks from `data/HappyBot.txt` when it starts and saves the task
list after a task is added, marked, unmarked, or deleted. The file is a UTF-8 plain-text file and
is created together with its parent folder when it is first needed.

Saving uses Java's `Files.write` method to write directly to the data file. This keeps the file
handling concise and avoids temporary-file naming conflicts and file-system-specific atomic move
behavior.

If the data file contains unreadable task lines, HappyBot skips those lines, loads the remaining
tasks, and displays a warning. This includes a later task with the same type, case-insensitive
description, and date/time details as an earlier one. If the file cannot be read or written,
HappyBot reports the problem instead of ending the session. Tasks remain available in memory for
the rest of that session when a save fails.

HappyBot also holds an operating-system lock on a companion file while it uses its data file. If
another HappyBot session already owns that lock, the second session displays a notice and does not
read or change the task list. Close the first session before starting another one for the same data
file. The operating system releases the lock when HappyBot exits or crashes.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## AI Use

Codex and Claude Code were used as development assistants throughout most project increments.
They helped to:

1. Discuss and evaluate Java and object-oriented design choices, including task modelling,
   package structure, parsing, validation, and storage.
2. Implement and refactor features for task management, date and time handling, duplicate-task
   detection, data persistence and recovery, console output, and the JavaFX interface.
3. Diagnose edge cases involving invalid input, saved-data errors, task-file locking, and failed
   saves.
4. Create, update, and run JUnit and console UI tests, including test cases for new features and
   boundary conditions.
5. Complete the optional `A-MoreErrorHandling` and `A-MoreTesting` increments.
6. Prepare and revise project documentation and test plan instructions.

AI suggestions were reviewed, adapted, and tested before use. Some proposed designs were rejected
in favour of simpler solutions based on the Java standard library. The project author made the final
design decisions and is responsible for the submitted work.
