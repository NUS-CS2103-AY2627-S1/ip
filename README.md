# Mira

Mira is a desktop chatbot that keeps track of todos, deadlines, and events through
a JavaFX chat interface.
Given below are instructions on how to run it.

- [User guide](https://sonlexuan3000.github.io/ip/)
- [Download Mira](https://github.com/sonlexuan3000/ip/releases/latest)

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/mira/Launcher.java` file, right-click it,
   and choose `Run Launcher.main()` (if the code editor is showing compile errors,
   try restarting the IDE). If the setup is correct, the Mira chat window appears.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Running with Gradle

Launch Mira's graphical interface from the project folder:

```shell
./gradlew run
```

Run the automated tests:

```shell
./gradlew test
```

Check the Java coding standard:

```shell
./gradlew checkstyleMain checkstyleTest
```

## Creating an executable JAR

Build the standalone application:

```shell
./gradlew clean shadowJar
```

The executable is created at `build/libs/mira.jar`. Copy that file into any
folder and launch it from that folder with:

```shell
java -jar mira.jar
```

Mira stores its tasks in `data/mira.txt`, relative to the folder from which the
JAR is launched.

## Acknowledgements

OpenAI Codex assisted `sonlexuan3000` with code, tests, documentation and review
across the project.
