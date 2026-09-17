# Bro task manager

Bro is a friendly task-management chatbot with the energy of a laid-back productivity wingman.
The instructions below explain how to set up and run it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/bro/Bro.java` file, right-click it, and choose `Run Bro.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see Bro's greeting.
   ```text
 +----------------------------+
 |       B R O // TASK HQ      |
 |   YOUR PRODUCTIVITY WINGMAN |
 +----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Building and running the executable JAR

The Shadow plugin packages Bro and its runtime dependencies into one executable
JAR file. From the project root, run:

```bash
./gradlew shadowJar
```

On Windows, use `gradlew.bat shadowJar` instead. The generated file is:

```text
build/libs/bro.jar
```

The `build/` directory contains generated files and should not be committed.
To test distribution behavior, copy `bro.jar` into an empty folder, open a
command window in that folder, and run:

```bash
java -jar "bro.jar"
```

Bro stores its task data in `data/bro.txt` relative to the folder from which
the JAR is run. To distribute a release, create a GitHub release and attach
`build/libs/bro.jar` as a binary instead of committing the JAR to Git.

DISCLAIMER: Codex was used to develop part of this project