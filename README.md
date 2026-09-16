# Panda

Panda is a desktop chatbot for managing todos, deadlines, and events.

## User Guide

Read the product website and complete user guide at
[lawjiakai11.github.io/ip](https://lawjiakai11.github.io/ip/).

The guide explains Panda's commands, date formats, sorting controls, automatic saving, and error handling.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/panda/app/Launcher.java` file, right-click it, and choose `Run Launcher.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, a JavaFX window for Panda should appear.

To run the original console chatbot directly, run `panda.app.Panda.main()` instead.

## Checkstyle

Checkstyle is a static analysis tool that checks Java source code against a configured set of style rules. This project uses the SE-EDU-aligned configuration in `config/checkstyle/` to automatically check naming, imports, indentation, braces, whitespace, line length, and Javadoc.

The SE-EDU coding-standard skill remains useful because it guides code while it is being written and covers conventions that are difficult to enforce automatically. Checkstyle complements it by providing a repeatable, objective build check. They serve different purposes, so this project uses both rather than choosing only one.

Run Checkstyle manually from the project root with:

```bash
./gradlew checkstyleMain checkstyleTest
```

To run Checkstyle together with the other verification tasks, use:

```bash
./gradlew check
```

HTML reports are written to `build/reports/checkstyle/main.html` and `build/reports/checkstyle/test.html`.

## Building and running the fat JAR

This project is configured to build a runnable fat JAR using the Shadow plugin.

1. Make sure Java 25 is active.
2. From the project root, run:
   ```bash
   ./gradlew shadowJar
   ```
3. The packaged JAR is created at:
   ```text
   build/libs/panda.jar
   ```
4. Run it with:
   ```bash
   java -jar build/libs/panda.jar
   ```

This produces a single executable JAR that includes the application and its dependencies.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
