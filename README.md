# Es project

This is a project template for a greenfield Java project. It is named Es. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open IntelliJ (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first).
2. Open the project into IntelliJ as follows:
   1. Click `Open`.
   2. Select the project directory, and click `OK`.
   3. If there are any further prompts, accept the defaults.
3. Configure the project to use **JDK 25** (not other versions) as explained [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
4. After that, locate the `src/main/java/Es.java` compatibility launcher (or `src/main/java/es/Es.java`), right-click it, and choose `Run 'Es.main()'` (or `Run 'es.Es.main()'`). If the setup is correct, you should see something like the below as the output:
   ```
    _____
   | ____|___
   |  _| / __|
   | |___\__ \
   |_____|___/
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Building and running a fat JAR

The Shadow plugin is configured in `build.gradle` with `es.Es` as the application entry point. From the project root, create the self-contained JAR with:

```powershell
$env:GRADLE_USER_HOME = "$pwd\.gradle-home"
.\gradlew.bat shadowJar
```

The output is written to `build\libs\duke.jar`. Run it with:

```powershell
java -jar build\libs\duke.jar
```

The fat JAR includes the application and its runtime dependencies, so no separate classpath is needed.


## AI Usage

Codex Terra Medium:
   Perform manual tasks: update name, ASCII banner, user greeting, test cases, error messages, throwing exceptions
   Perform simple tasks: Replicate user's response, store list of responses, 
                         marking and unmarking tasks, created task, deadline, event classes with inheritance,
                         delete task
   Perform complex tasks: Present changes visually, test case skill

Cursor:
    Edited Task structure to use ArrayList instead of Array
    Added Enums data structure for command words and task type
    Added save and load data file functionality to a relative file path, including error handling

Codex Luna Light:
    Added conversion of deadline strings to dates, along with error handling for incorrect date formats
    Restructured code to follow OOP principles by extracting multiple classes
    Maintain tests for the highest-value behavior (at least the top 50% of non-trivial methods).
    Update JUnit tests
    Restructured code to package classes
    Edited README.md setup instructions for IntelliJ and Jar
    Created Project Skill and updated Agent.md to follow coding standards
    Updated checkstyle and fixed checkstyle violations
    Updated code according to java coding standard
    Imported GUI from javafx-tutorial files and integrated with current responses
    Updated GUI background and display colors
    Added delay after bye before closing the GUI
    Modified error handling
    Modified responses according to personality (reserved, sophisticated)
    Updated User Guide 
    

