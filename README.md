# Duke project template

This is a project template for a greenfield Java project. It's named after the Java mascot _Duke_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/Duke.java` file, right-click it, and choose `Run Duke.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    ____        _        
   |  _ \ _   _| | _____ 
   | | | | | | | |/ / _ \
   | |_| | |_| |   <  __/
   |____/ \__,_|_|\_\___|
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Creating and running the fat JAR

The Shadow plugin packages the application and all its runtime dependencies into
one executable JAR file.

1. From the project root, create the fat JAR:

   ```shell
   ./gradlew shadowJar
   ```

   On Windows, use `gradlew.bat shadowJar` instead.

2. Find the generated JAR at `build/libs/luna.jar`.

3. Run it from the project root with Java 25:

   ```shell
   java -jar build/libs/luna.jar
   ```

Running `./gradlew build` also creates the same fat JAR, in addition to running
the project's normal verification tasks.

## Acknowledgements

### Reuse of previous work

Substantial portions of this project were reused and adapted by **Sarina Ke**
from her own CS2103T individual project submitted during a previous course
attempt. The reuse covers significant parts of the application's codebase,
including its task-management logic, command processing, persistence, tests,
and graphical user interface. The reused work has since been reviewed and
further modified for the current submission.

Source: Sarina Ke's previous CS2103T individual project submission.

### AI-assisted work

- **Tool:** OpenAI Codex
- **Used by:** Sarina Ke
- **Extent of use:** Used extensively throughout the project to suggest,
  generate, modify, refactor, and review code; improve JavaFX FXML and CSS;
  diagnose errors; develop and run tests; and edit project documentation. The
  generated suggestions and changes were reviewed and integrated by Sarina Ke.
