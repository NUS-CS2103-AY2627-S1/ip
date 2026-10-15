# Swell

Swell is a Java task chatbot with a calm task navigator personality. It helps users keep track of todos,
deadlines, events, tags, and searches using short text commands.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
    1. Click `Open`.
    1. Select the project directory, and click `OK`.
    1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/swell/Swell.java` file, right-click it, and choose `Run Swell.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
    ```
    ____________________________________________________________
     Hi, I'm Swell, your calm task navigator.
     Send me a task, and we'll chart a steady course.
    ____________________________________________________________
    ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Running with Gradle

Swell uses Gradle to build, run, test, and package the project.

Run the chatbot:

```bash
./gradlew run
```

Run tests:

```bash
./gradlew test
```

Build the project:

```bash
./gradlew build
```

Create the runnable JAR file:

```bash
./gradlew shadowJar
```

Run the JAR file:

```bash
java -jar build/libs/swell.jar
```

## Command Format

Swell uses short text commands in this general format:

```text
COMMAND REQUIRED_DETAILS [OPTIONAL_DETAILS]
```

Correct examples:

```text
todo read book #school
deadline submit report /by 2026-09-18 2359
event project meeting /from 2026-09-19 1400 /to 2026-09-19 1600
event quick sync /from 2026-09-19 /to 2026-09-19
mark 1
delete 2
findtag #school
```

Incorrect examples:

```text
todo
deadline submit report /by Sunday
deadline submit report /by 2026-09-18 /by 2026-09-19
event project meeting /from Monday 2pm
mark 1 2
findtag follow up
```

See `docs/README.md` for the full user guide.

## Project Architecture

Swell is organized into small classes with clear responsibilities:

- `swell.Swell` runs the main chatbot loop and coordinates the other classes.
- `swell.parser.Parser` interprets user commands and creates the correct task objects.
- `swell.task.TaskList` manages the task collection, including adding, marking, unmarking, and deleting tasks.
- `swell.ui.Ui` handles all user-facing messages.
- `swell.storage.Storage` loads tasks from `data/swell.txt` and saves changes automatically.
- `swell.task.Task`, `Todo`, `Deadline`, `Event`, and `TaskType` represent the different task types and their display formats.
- `swell.exception.SwellException` represents user input errors that the chatbot can recover from.
