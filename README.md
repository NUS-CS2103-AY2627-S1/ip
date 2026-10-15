# Lumi project

Lumi is a calm, celestial task navigator that helps users chart one clear step at a time. Its concise language and
midnight-inspired interface keep task management friendly without distracting from the work. Given below are
instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/lumi/Launcher.java` file, right-click it, and choose
   `Run Launcher.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is
   correct, a JavaFX window titled `Lumi` should appear. Enter a message using the text field and either press Enter or
   click Send to see the user message and Lumi's response.

**Warning:** Keep `src/main/java` as the source root for all Java packages. Do not move the `lumi` package outside this folder, because build tools such as Gradle expect this standard layout.

## Building the fat JAR

The Shadow plugin packages Lumi and all of its runtime dependencies into one executable file named `lumi.jar`. Ensure that `java -version` reports Java 25 before building or running it.

From the project root, run the following command on Windows:

```powershell
.\gradlew.bat shadowJar
```

On macOS or Linux, run:

```shell
./gradlew shadowJar
```

The completed JAR is created at `build/libs/lumi.jar`. The normal Gradle `build` task also creates this JAR because the Shadow task is connected to the build lifecycle.

To run Lumi from the project root on Windows:

```powershell
java -jar build\libs\lumi.jar
```

On macOS or Linux:

```shell
java -jar build/libs/lumi.jar
```

The responsive GUI uses a midnight, lavender, and warm-gold palette with a compact Lumi monogram. Its FXML views are
in `src/main/resources/view`, while the linked stylesheets are in `src/main/resources/css`. The GUI and console share
the same task list, saved data, and command handling. Both interfaces support `todo`, `deadline`, `event`, `list`,
`find`, `mark`, `unmark`, `delete`, `snooze`, and `bye`. Use `snooze <task number> /to <date/time>` to move a deadline
or event to a new date or time. Lumi preserves the duration when moving an event.

In IntelliJ, you can build the same JAR by opening the Gradle tool window, finding the `shadowJar` task, and running it. Refresh the project view afterward if the `build` folder is not immediately visible.

## Checking code style

The project uses Checkstyle 11.0.0 with the SE-EDU Java coding-standard rules. Run both the production-code and
test-code checks from the project root:

On Windows:

```powershell
.\gradlew.bat checkstyleMain checkstyleTest
```

On macOS or Linux:

```shell
./gradlew checkstyleMain checkstyleTest
```

The normal Gradle `check` and `build` tasks also run these Checkstyle checks.
