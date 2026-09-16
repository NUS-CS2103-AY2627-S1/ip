# Will chatbot

This is a command-line task-tracking chatbot built for CS2103T, named _Will_. Given below are instructions on how to set it up and run it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/will/Will.java` file, right-click it, and choose `Run Will.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    __        _____ _     _     
    \ \      / /_ _| |   | |    
     \ \ /\ / / | || |   | |    
      \ V  V /  | || |___| |___ 
       \_/\_/  |___|_____|_____|

   What's up!!! I'm Will.
   How may I assist you?
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Running the GUI

Will also has a JavaFX GUI (`will.gui.Launcher`). To run it from Gradle:

```
.\gradlew.bat run
```

(or `./gradlew run` on macOS/Linux). This launches the same chatbot
behind a chat-style window instead of the console.

## Building a runnable JAR

To build a single JAR file with all dependencies (including JavaFX)
bundled in:

```
.\gradlew.bat shadowJar
```

The output is written to `build/libs/will.jar`. Run it with:

```
java -jar build/libs/will.jar
```
