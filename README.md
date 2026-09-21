# PDD

PDD is a desktop chatbot for tracking todos, deadlines, and events from the
command line or a JavaFX GUI, with tasks saved to disk between runs.

See the [user guide](docs/README.md) for the full list of commands and
example usage.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/pdd/PDD.java` file, right-click it, and choose `Run PDD.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    ____  ____  ____  
   |  _ \|  _ \|  _ \ 
   | |_) | | | | | | |
   |  __/| |_| | |_| |
   |_|   |____/|____/ 
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Running PDD

This project builds with Gradle:

- `./gradlew run` — launch the JavaFX GUI.
- `./gradlew runText` — launch the console text UI.
- `./gradlew test` — run the JUnit test suite.
- `./gradlew shadowJar` — build the runnable fat JAR at `build/libs/pdd.jar`
  (run it with `java -jar build/libs/pdd.jar`).

Both UIs share the same save file, `./data/pdd.txt`, so tasks persist
across runs and across whichever UI you use.
