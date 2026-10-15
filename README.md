# Bo project template

This is a project template for a greenfield Java project. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/bo/Bo.java` file, right-click it, and choose `Run Bo.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
   ____________________________________________________________
    ____        
   | __ )  ___  
   |  _ \ / _ \ 
   | |_) | (_) |
   |____/ \___/
   Hello! I'm Bo.
   What can I do for you?
   ____________________________________________________________
   list
   ____________________________________________________________
    list
   ____________________________________________________________
   bye
   ____________________________________________________________
   Bye. Hope to see you again soon!
   ____________________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Building and running the fat JAR

The project uses the Shadow Gradle plugin to package the application and its
runtime dependencies into one executable (fat) JAR. The configured entry point
is `bo.Bo`, and the generated file is named `bo.jar`.

Run these commands from the project root:

```bash
./gradlew shadowJar
```

The JAR is created at:

```text
build/libs/bo.jar
```

To run it, use Java 25 and execute:

```bash
java -jar build/libs/bo.jar
```

Bo then accepts commands through the terminal. For example, enter `list` to
display saved tasks or `bye` to exit.

To view tasks in chronological order, enter:

```text
sort
```

Deadlines are ordered by their `by` date and events by their `from` date.
Todos and tasks with legacy free-form dates are treated as undated, so they
appear after dated tasks while retaining their original relative order. The
`sort` command takes no arguments and saves the reordered task list.

On Windows, use `gradlew.bat shadowJar` and
`java -jar build\\libs\\bo.jar` instead. If you want to force a clean
rebuild, run `./gradlew clean shadowJar` before running the JAR.
