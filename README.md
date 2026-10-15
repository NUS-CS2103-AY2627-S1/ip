# Probe project template

This is a project template for a greenfield Java project. The chatbot is named _Probe_ because it investigates questions and helps users find answers. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/Probe.java` file, right-click it, and choose `Run Probe.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see the following output:
   ```
   ____________________________________________________________
    ____  ____   ___  ____  _____
   |  _ \|  _ \ / _ \| __ )| ____|
   | |_) | |_) | | | |  _ \|  _|
   |  __/|  _ <| |_| | |_) | |___
   |_|   |_| \_\\___/|____/|_____|
   ____________________________________________________________
   Hello! I'm Probe.
   What can I do for you?
   ____________________________________________________________
   Bye. Hope to see you again soon!
   ____________________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
