# Beemo

Beemo is a personal assistant chatbot built as an introductory software engineering project. Given below are instructions on how to set it up.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/beemo/Beemo.java` file, right-click it, and choose `Run Beemo.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    ____  _____ _____ __  __  ___
   | __ )| ____| ____|  \/  |/ _ \
   |  _ \|  _| |  _| | |\/| | | | |
   | |_) | |___| |___| |  | | |_| |
   |____/|_____|_____|_|  |_|\___/
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Creating an executable JAR

From the project root, build the fat JAR using the Gradle wrapper:

```shell
./gradlew clean shadowJar
```

The generated JAR is located at `build/libs/beemo.jar`. To distribute and run it:

1. Copy `beemo.jar` into an empty folder.
2. Open a terminal in that folder.
3. Run:

   ```shell
   java -jar beemo.jar
   ```

The user needs Java 25 but does not need Gradle. When the task list first changes, Beemo
creates its `data/beemo.txt` save file relative to the folder containing the JAR.
