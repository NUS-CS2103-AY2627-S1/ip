# Duck project template

This is a project template for a greenfield Java project. It's named _Duck_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/duck/Launcher.java` file, right-click it, and choose
   `Run Launcher.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is
   correct, a window titled **Duck** will open.

Alternatively, launch the graphical interface from the project root:

```shell
./gradlew run
```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Testing and coverage

Run the automated tests with Java 25:

```shell
./gradlew test
```

The test task also generates a JaCoCo coverage report. Open
`build/reports/jacoco/test/html/index.html` to inspect line and branch coverage.

Run all project checks, including the coverage requirements, with:

```shell
./gradlew check
```

Automatically testable classes must maintain at least 90% line coverage and
80% branch coverage. JavaFX window and launcher classes are excluded from this
gate because they are covered by manual GUI testing.
