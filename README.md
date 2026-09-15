# Luke project template

This is a project template for a greenfield Java project. It's named after the Java mascot _Duke_. Luke is currently a command-line task tracker that can store todos, deadlines, and events.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/luke/Luke.java` file, right-click it, and choose `Run Luke.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    _         _        
   | |  _   _| | _____ 
   | | | | | | |/ / _ \
   | |_| |_| |   <  __/
   |_____\__,_|_|\_\___|
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location many Java tools expect to find source files.

## Running from the terminal

Run Luke from the project root:

```bash
./gradlew run
```

To build a runnable JAR for release:

```bash
./gradlew jar
```

The release artifact is created at `build/lib/luke.jar`. Run it with:

```bash
java -jar build/lib/luke.jar
```

## Running tests

Run the JUnit test suite from the project root:

```bash
./gradlew test
```

## User guide

For supported commands and examples, see [docs/README.md](docs/README.md).

## AI assistance disclosure

Codex was used to generate the unit tests and to write most of the user
documentation and Javadocs. The generated content was reviewed and integrated
into the project by the author.

Codex was used to refactor code snippets to align with the Java Coding Standard.

Codex was used to generate the JavaFX, images and .css for the GUI design.