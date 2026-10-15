# Sunny project template

This is a greenfield java project for a task-managing style chatbot. Named Sunny as the chatbot is ironically personified
to be mean. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/sunny/Sunny.java` file, right-click it, and choose `run "run ip"` (if the code 
2. editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below in the terminal as the output:
   ```
   ____________________________________________________________
   Yeah, it's me, Sunny.
   How can I annoy you today?
   ____________________________________________________________
   ```

To launch the graphical interface, use `.\gradlew clean run` in the terminal, or locate the `src/main/java/Launcher.java` 
file and choose `run "Launcher.main()`.

For a full suite of commands presently available, you may visit the [Sunny User Guide](https://vanillajohn.github.io/ip/).
If you plan to add on to Sunny, please ensure it continues insulting the user in an endearing way.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move 
Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
