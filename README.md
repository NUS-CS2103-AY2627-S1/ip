# Caitlyn project template

This is a project template for a greenfield Java project. In this version, the chatbot is named _Caitlyn_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/caitlyn/Launcher.java` file, right-click it, and choose `Run 'Launcher.main()'` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, Caitlyn's graphical task assistant should open.

The separate `Launcher` class is intentional: it starts JavaFX using `Application.launch`, while `Caitlyn.java` remains the command-line entry point used by the automated command-line tests.

## Using the graphical interface

Make sure Java 25 with JavaFX is active. On macOS with SDKMAN, run:

```bash
sdk use java 25.0.3.fx-zulu
```

From the project root, start the GUI with:

```bash
./gradlew run
```

Enter commands in the text field and press Enter or click **Send**. The GUI supports the same commands as the command-line version, including `todo`, `deadline`, `event`, `within`, `list`, `find`, `mark`, `unmark`, `delete`, and `bye`. Tasks are saved automatically in `data/duke.txt` after changes.

Use `within collect certificate /from 2027-01-15 /to 2027-01-25` for an action that can be completed once during a window. See the [within-period task guide](docs/README.md#within-period-tasks) for formats, examples, and saved-data recovery.

To continue using the command-line version directly, run `caitlyn.Caitlyn` from IntelliJ or use the command-line instructions below.

## Building and running a fat JAR

The project uses the [Shadow Gradle plugin](https://gradleup.com/shadow/) to package Caitlyn and its runtime dependencies into one executable JAR. The application entry point is configured as `caitlyn.Launcher` in `build.gradle`.

### Create the JAR

Make sure Java 25 is active before building. On macOS with SDKMAN, run:

```bash
sdk use java 25.0.3.fx-zulu
```

From the project root, run:

```bash
./gradlew clean shadowJar
```

On Windows, use `gradlew.bat clean shadowJar` instead.

Gradle writes the fat JAR to:

```text
build/libs/caitlyn.jar
```

The `clean` part removes previous build output; `shadowJar` then creates a fresh JAR containing the application and its runtime dependencies. The ordinary dependency-free `jar` task is disabled because this project uses the Shadow JAR as its distributable artifact.

### Run the JAR

From the project root, run:

```bash
java --add-modules javafx.controls -jar build/libs/caitlyn.jar
```

On Windows, the path uses backslashes:

```text
java --add-modules javafx.controls -jar build\libs\caitlyn.jar
```

The GUI opens after the JAR starts. Enter Caitlyn commands such as `todo borrow book` or `list` in the window, and enter `bye` when you are finished. If the JAR has not been created yet, run the `shadowJar` command above first.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
