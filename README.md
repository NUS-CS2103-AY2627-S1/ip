# Toot

Toot is a chatbot built as a greenfield Java project. Given below are instructions on how to use it.

## Build, test, and run with Gradle

Prerequisite: JDK 25.

```shell
./gradlew build
./gradlew run
```

`build` compiles the application and runs the JUnit and Checkstyle suites. `run`
starts Toot's JavaFX interface.

The original console interface remains available for quick manual and regression
testing:

```shell
./gradlew runCli
```

## Create and run the executable JAR

```shell
./gradlew shadowJar
java -jar build/libs/Toot.jar
```

The generated `build/libs/Toot.jar` is self-contained. It can be copied into an
empty folder and run there; Toot will create `data/toot.txt` beside it when the
first task is saved. The generated JAR is build output and should not be committed.

## Setting up in IntelliJ

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/toot/Toot.java` file, right-click it, and choose `Run Toot.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    _____           _
   |_   _|__   ___ | |_
     | |/ _ \ / _ \| __|
     | | (_) | (_) | |_
     |_|\___/ \___/ \__|
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
