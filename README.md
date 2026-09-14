# Arrodes project template

This is a project template for a greenfield Java project. Its application name is _Arrodes_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate `src/main/java/arrodes/Launcher.java`, right-click it, and choose `Run 'Launcher.main()'` (if the code editor is showing compile errors, try restarting the IDE). With no arguments, Arrodes starts in GUI mode. To start the CLI, pass `--cli` as a program argument.

To run the packaged JAR:

```powershell
java -jar build\libs\arrodes.jar
java -jar build\libs\arrodes.jar --cli
```

If the setup is correct, the CLI should display something like the below as the output:
   ```
   ____________________________________________________________
       _                         _
      / \   _ __ _ __ ___   __| | ___  ___
     / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
    / ___ \| |  | | | (_) | (_| |  __/\__ \
    /_/   \_\_|  |_|  \___/ \__,_|\___||___/
   Hello! I'm Arrodes.
   What can I do for you?
   ____________________________________________________________
   Bye. Hope to see you again soon!
   ____________________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
