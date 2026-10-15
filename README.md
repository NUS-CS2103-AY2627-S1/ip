# Zeus

Zeus is a chatbot application written in Java. Given below are instructions on how to set it up.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/zeus/Zeus.java` file, right-click it, and choose `Run Zeus.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
   ____________________________________________________________
    _____
   |__  /___ _   _ ___
     / // _ \ | | / __|
    / /|  __/ |_| \__ \
   /____\___|\__,_|___/
   Zeus awaits your command. ⚡
   Type 'help' to see available commands.
   ____________________________________________________________
   todo borrow book
   ____________________________________________________________
   It is decreed. Your task has been added.
     [T][ ] borrow book
   Now you have 1 tasks in the list.
   ____________________________________________________________
   deadline return book /by 2026-09-06
   ____________________________________________________________
   It is decreed. Your task has been added.
     [D][ ] return book (by: Sep 6 2026)
   Now you have 2 tasks in the list.
   ____________________________________________________________
   event project meeting /from 2026-09-07 /to 2026-09-08
   ____________________________________________________________
   It is decreed. Your task has been added.
     [E][ ] project meeting (from: Sep 7 2026 to: Sep 8 2026)
   Now you have 3 tasks in the list.
   ____________________________________________________________
   mark 2
   ____________________________________________________________
   A worthy task completed.
     [D][X] return book (by: Sep 6 2026)
   ____________________________________________________________
   unmark 2
   ____________________________________________________________
   OK, I've marked this task as not done yet:
     [D][ ] return book (by: Sep 6 2026)
   ____________________________________________________________
   list
   ____________________________________________________________
   Here are the tasks in your list:
   1.[T][ ] borrow book
   2.[D][ ] return book (by: Sep 6 2026)
   3.[E][ ] project meeting (from: Sep 7 2026 to: Sep 8 2026)
   ____________________________________________________________
   delete 3
   ____________________________________________________________
   Noted. I've removed this task:
     [E][ ] project meeting (from: Sep 7 2026 to: Sep 8 2026)
   Now you have 2 tasks in the list.
   ____________________________________________________________
   todo
   ____________________________________________________________
   OOPS!!! A todo needs a description after 'todo'.
   ____________________________________________________________
   blah
   ____________________________________________________________
   OOPS!!! Even Zeus cannot understand that command. Type 'help' to see available commands.
   ____________________________________________________________
   bye
   ____________________________________________________________
   Farewell, mortal.
   ____________________________________________________________
   ```

## Data storage

Zeus accepts deadline and event dates in `yyyy-MM-dd` format, such as `2026-09-06`, and displays them as `Sep 6 2026`. It saves the current task list to `data/zeus.txt` whenever a task is added, marked, unmarked, or deleted. It loads valid saved tasks automatically at startup and reports malformed records without crashing.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
