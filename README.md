# Glennon

Glennon is a chatbot developed as a greenfield Java project. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25 (on macOS, use `25.0.3.fx-zulu`), and IntelliJ with Java 25 support.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After Gradle finishes importing, locate `src/main/java/glennon/gui/Launcher.java`, right-click it, and choose `Run Launcher.main()`. Glennon opens a chat window with its greeting.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Using the GUI

On macOS, select the required JDK and launch from the project root:

```shell
export JAVA_HOME="$HOME/.sdkman/candidates/java/25.0.3.fx-zulu"
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew run
```

Type a command and press **Enter** or click **Send**. Glennon's replies appear
on the left; your commands appear on the right. Messages wrap when you resize
the window, and new replies scroll into view automatically. Enter `bye` to see
the sign-off message before the window closes. Missions are saved after each
change, using the same `data/glennon.txt` as the console application.

For example, try `todo read book`, `list`, `mark 1`, and `find book`. A storage
load error disables input to prevent accidentally replacing unreadable data.

The original console remains available through `./gradlew runCli`, or by
running `glennon.Glennon` in IntelliJ. Do not run both interfaces against the
same data file simultaneously.

### Tutorial structure

The GUI follows the [SE-EDU JavaFX tutorial, Parts 1–5](https://se-education.org/guides/tutorials/javaFxPart1.html):

- `Launcher` starts `Main`, which loads the FXML and injects a Glennon session.
- `MainWindow` handles Enter and Send through one handler; `Glennon.getResponse`
  reuses the existing parser, commands, and output formatter.
- `MainWindow.fxml` uses `AnchorPane`, `ScrollPane`, and `VBox`; `DialogBox.fxml`
  uses `fx:root` for the reusable `HBox` message component.
- CSS controls colors, bubble shapes, and interaction states. Glennon's robot
  avatar appears once in the header, and an orbital background fills the
  conversation area while message cards keep text readable.

Open `src/main/resources/view/MainWindow.fxml` or `DialogBox.fxml` in Scene
Builder to edit the layout. Keep the FXML namespace at JavaFX 17 for compatibility
with the tutorial's Gradle dependencies. On macOS, the required Zulu FX JDK
provides the matching native JavaFX runtime.

### GUI checks

`./gradlew clean test` runs the business logic tests and twenty-one real JavaFX
interaction tests. These tests need a desktop session and open temporary
windows; their mission data is isolated from your files. See
[the GUI regression plan](docs/gui-test-plan.md) for coverage and visual checks.
See [the error-handling regression checks](docs/error-handling-tests.md) for
the console plan, storage-failure fixtures, and exact-output checks.

### Automated test coverage

With the required JDK selected, run `./gradlew clean check jacocoTestReport`
to rebuild, run all JUnit and Checkstyle checks, and measure core coverage.
Open `build/reports/jacoco/test/html/index.html` for the coverage report and
`build/reports/tests/test/index.html` for individual test results.

The core report excludes JavaFX presentation classes; their twenty-one interaction
tests still run in the full suite. See [the testing guide](docs/testing.md) for
the tested contracts, coverage limits, console regression command, and manual
portability checks.

## Dates and times

Deadline and event dates use the `d/M/yyyy` format and may include a time in
`HHmm` format. The time uses a 24-hour clock, so `1800` means 6:00 PM. A
date-only deadline defaults to 11:59 PM, while date-only event boundaries make
an all-day event.

For example:

```text
deadline return book /by 2/12/2019 1800
event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600
deadline submit report /by 3/12/2019
event conference /from 4/12/2019 /to 5/12/2019
```

Glennon displays those date-times in a more readable format:

```text
[D][ ] return book (by: Dec 2 2019, 6:00 PM)
[E][ ] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
```

An event must end after it starts. Identical start and end timestamps are
rejected, while the same date at both all-day boundaries remains valid.

Glennon accepts surrounding spaces and tabs and reports missing, repeated, or
unexpected scheduling parameters. It also rejects duplicate missions with the
same type, case-sensitive description, and schedule, regardless of completion
status. See [the user guide](docs/README.md) for the full input rules and
storage-error recovery steps.

Use `on d/M/yyyy` to list deadlines due on a date and events that overlap that
date. To-dos are excluded because they have no date:

```text
on 2/12/2019
```

## Checking coding style

Checkstyle automatically checks production and test Java sources for formatting,
import ordering, naming, and Javadoc rules. It complements the
`seedu-java-coding-standard` skill: automated checks are repeatable, while code
review is still needed for meaningful names, clear comments, and design.

The configuration in `config/checkstyle/` comes from
[AddressBook Level 3](https://github.com/se-edu/addressbook-level3/tree/master/config/checkstyle),
following the [SE-EDU tutorial](https://se-education.org/guides/tutorials/checkstyle.html).
Checkstyle is pinned to version 11.0.0. Both errors and warnings fail the build.

On macOS, select the required JDK and run the checks from the project root:

```shell
export JAVA_HOME="$HOME/.sdkman/candidates/java/25.0.3.fx-zulu"
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew checkstyleMain checkstyleTest
```

Reports are generated at `build/reports/checkstyle/main.html` and
`build/reports/checkstyle/test.html`. `./gradlew check` runs both Checkstyle tasks
and the JUnit suite; the console UI test plan is run separately.

For editor feedback, install IntelliJ's Checkstyle-IDEA plugin, select version
11.0.0, add `config/checkstyle/checkstyle.xml` as an active local configuration,
and set its scan scope to include Java test sources.

## Creating an executable JAR

The Shadow plugin packages Glennon and all of its runtime dependencies into one
executable (or "fat") JAR. From the project root, run:

```shell
./gradlew clean shadowJar
```

On Windows, use `gradlew.bat clean shadowJar` instead. The generated file is:

```text
build/libs/Glennon.jar
```

To test the distributable in the same way as an end user, copy `Glennon.jar`
into an empty folder, open a terminal in that folder, and run:

```shell
java -jar "Glennon.jar"
```

Glennon reads and writes its `data/glennon.txt` save file relative to the folder
from which the JAR is run. The `build/` directory is ignored by Git, so the
generated JAR should be attached to a GitHub release instead of committed to
the repository.
