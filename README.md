# Rei

Rei is a calm study companion for managing todos, deadlines, events, and tags.
It is named after _Rei_ from the K-Pop group IVE.

Read the [User Guide](https://nivremi.github.io/ip/) or
[download the latest JAR](https://github.com/nivremi/ip/releases/latest).

![Rei GUI](docs/Ui.png)

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate `src/main/java/rei/Launcher.java`, right-click it, and choose
   `Run Launcher.main()` to open the JavaFX interface. If the code editor is showing
   compile errors, try restarting the IDE.

## Continuous integration

GitHub Actions runs the Java CI workflow on every push and pull request.
It uses Java 25 on Windows to build the JAR, run JUnit (including JavaFX tests),
check Java coding conventions, and run the recorded CLI tests.
Windows matches the project's tested JavaFX environment and CLI test classpath.

Open the repository's **Actions** tab to inspect a run. Test reports and the CLI
transcript are uploaded as `test-reports`; successful runs also provide `rei-jar`.

## Running Rei

- Run the JavaFX interface with `.\gradlew.bat run` on Windows or `./gradlew run` on macOS/Linux.
- Keep using the text interface with `.\gradlew.bat runCli` on Windows or `./gradlew runCli` on macOS/Linux.
- Build the distributable application with `.\gradlew.bat build` on Windows or `./gradlew build` on
  macOS/Linux. The executable fat JAR is created at
  `build/libs/rei.jar` and can be started using `java -jar build/libs/rei.jar`.

## Tagging tasks

Use `tag TASK_NUMBER #TAG` to organize an existing task. Tags start with `#`
and can contain letters, numbers, `_`, or `-`. Rei stores tags in lowercase,
shows them when listing tasks, and finds an exact tag with `find #TAG`.

```text
todo prepare slides
tag 1 #School
find #school
```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
