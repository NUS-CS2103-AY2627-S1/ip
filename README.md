# Snoopy

Snoopy is a Java chatbot for managing todos, deadlines, and events. It includes a JavaFX graphical
interface and retains its text interface for automated testing.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. Let Gradle finish importing the project and downloading its JavaFX dependencies.

## Running Snoopy

Run the graphical interface from the repository root:

```bash
./gradlew run
```

Enter commands in the field at the bottom and press **Enter** or click **Send**. Enter `bye` to close the
application.

To run the retained text interface in IntelliJ, locate `src/main/java/snoopy/Snoopy.java`, right-click it,
and choose **Run Snoopy.main()**.

## Building the JAR file

Build a distributable JAR containing Snoopy and its JavaFX dependencies:

```bash
./gradlew clean shadowJar
```

The generated file is `build/libs/snoopy.jar`. Run it using Java 25:

```bash
java -jar build/libs/snoopy.jar
```

The bundled JavaFX libraries allow the JAR to run on Windows, macOS, and Linux without a separate
JavaFX installation.

## Supported commands

- `todo <description>`
- `deadline <description> /by <yyyy-MM-dd>`
- `event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>`
- `list`
- `mark <task number>`
- `unmark <task number>`
- `delete <task number>`
- `find <keyword>`
- `bye`

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
