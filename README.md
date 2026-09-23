# Vermithor

Vermithor is a Java 25 task manager with both a text-based interface and a JavaFX GUI. It helps you record todo tasks, deadlines, events, and searches in a persistent task list.

## Features

- Add todo, deadline, and event tasks
- Mark, unmark, delete, find, and sort tasks
- Save tasks between sessions
- Use the responsive JavaFX GUI or the command-line interface

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate `src/main/java/vermithor/Launcher.java`, right-click it, and choose `Run Launcher.main()`.

## Build from the command line

Use JDK 25, then use Gradle to create the JavaFX fat JAR:

```sh
./gradlew clean test shadowJar
java -jar build/libs/vermithor.jar
```

The generated fat JAR bundles JavaFX and is intended to run on a computer with Java 25.

The `sort` command orders tasks alphabetically by description.

## Example commands

```text
todo read a book
deadline submit report /by 2026-09-18
event team meeting /from 10:00 /to 11:00
find report
sort
```

For date-like event values, use valid dates in `yyyy-MM-dd` format. The end date
must be later than the start date; free-form times such as `10:00` remain supported.

**Warning:** Keep the `src/main/java` folder as the root folder for Java files, as this is the default location expected by Gradle.
