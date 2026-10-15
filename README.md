# Thomas

_Thomas_ is a desktop chatbot for keeping a to-do list. It runs as a JavaFX window and saves your tasks between runs.

- **Using Thomas:** see the [user guide](docs/README.md). In short: install JDK 25, download `thomas.jar`, and run `java -jar thomas.jar` from a terminal.
- **Developing Thomas:** follow the setup below, or run `./gradlew run` from the project root.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/thomas/Launcher.java` file, right-click it, and choose `Run Launcher.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, a window opens and Thomas greets you.<br>
   To run the text-only version instead, run `src/main/java/thomas/Thomas.java` the same way; you should see something like the below as the output:
   ```
     ________                              
    /_  __/ /_  ____  ____ ___  ____ ______
     / / / __ \/ __ \/ __ `__ \/ __ `/ ___/
    / / / / / / /_/ / / / / / / /_/ (__  ) 
   /_/ /_/ /_/\____/_/ /_/ /_/\__,_/____/  
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Launching the JavaFX GUI

Make sure JDK 25 is configured, then run the following command from the project
root:

```terminal
.\gradlew run
```

Alternatively, in IntelliJ, open `src/main/java/thomas/Launcher.java` and run
`Launcher.main()`. This opens the Thomas the Tank Engine JavaFX conversation window.

## Building the JAR

With JDK 25 installed, run this from the project root:

```terminal
.\gradlew shadowJar
```

Retrieve the bundled application JAR from `build\libs\thomas.jar`.

## AI use

AI assistance (Claude Code) was used throughout this project: to write and refactor application code, JUnit tests and the text-UI test plan, and to set up the agent guidance in `AGENTS.md`.
All AI-generated changes were reviewed, tested and committed by me, and every commit is authored solely by me.

