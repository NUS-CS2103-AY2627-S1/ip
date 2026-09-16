# Kibo

Kibo is your cheerful pocket cheerleader for everyday tasks. **Small steps, brighter days!**
It celebrates finished tasks, encourages you when plans change, and keeps your list close at hand.
Its cream-and-gold chat window pairs friendly replies with clearly labeled red error messages.

Kibo is built as an individual project for CS2103T.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/kibo/Kibo.java` file, right-click it, and choose `Run Kibo.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see the following output:
   ```
   Hey! I'm Kibo, your pocket cheerleader.
   Small steps, brighter days!
   Ready? Try 'list' or 'todo read book'.
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Creating and running the fat JAR

From the project root, create a fat JAR containing Kibo and its runtime dependencies:

```shell
./gradlew shadowJar
```

On Windows, use `gradlew.bat shadowJar` instead. The generated JAR is located at
`build/libs/kibo.jar`. Run it from the project root with:

```shell
java -jar build/libs/kibo.jar
```

Kibo reads and writes its task data relative to the directory from which the JAR is run.

## Viewing schedules

Use `schedule yyyy-MM-dd` to view deadlines due on a date and events that start on that date.
For an event to appear in a schedule, begin its `/from` value with an ISO date:

```text
event project meeting /from 2019-12-02 2pm /to 4pm
schedule 2019-12-02
```

The time text remains flexible. Existing events with free-form starts such as `Mon 2pm` continue
to load and display normally, but they are excluded from dated schedules because they do not
identify a calendar date.

## Running automated tests

From the project root, run `./gradlew check` (or `gradlew.bat check` on Windows) to run JUnit
tests and Checkstyle. JUnit results are in `build/reports/tests/test/index.html`; the JaCoCo
coverage report is generated automatically at `build/reports/jacoco/test/html/index.html`.
Use `./gradlew test --rerun-tasks` to force a fresh test run.

Run file-based tests through Gradle, including when using IntelliJ's test runner: they use
`build/test-work` as their working directory so they never replace your real `data/duke.txt`.
See [the automated testing guide](test/automated-testing.md) for coverage and limitations,
and [the UI test plan](test/ui-test-plan.md) for acceptance and manual checks.
