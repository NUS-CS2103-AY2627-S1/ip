# Larp

Larp is a personal assistant chatbot developed as an individual project. Given below are instructions on how to run it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/Larp.java` file, right-click it, and choose `Run Larp.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    _        _    ____  ____
   | |      / \  |  _ \|  _ \
   | |     / _ \ | |_) | |_) |
   | |___ / ___ \|  _ <|  __/
   |_____/_/   \_\_| \_\_|

   Hello, I'm Larp.
   Ready when you are. Add a task, check your list, or find something.
   All set. Your tasks are saved. See you next time!
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Acknowledgements

* Notable uses of AI:
  * I used Claude Code to help implement the JavaFX GUI (Level 10), fix date-parsing and search bugs, write JUnit tests for the parser and storage classes, and update the user guide. I also routinely used it to review my code for bugs throughout the project.
