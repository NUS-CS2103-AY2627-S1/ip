# Ari

Ari is a Java 25 desktop task manager built from the SE-EDU iP template.
See the [user guide](docs/README.md) for commands, saving behavior and recovery.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. Run `./gradlew.bat run` on Windows, or `./gradlew run` on macOS/Linux,
   from the project directory to open the GUI. To use the console interface,
   run `Ari.main()` from `src/main/java/ari/Ari.java` in IntelliJ instead.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Sorting tasks

Enter `sort` to sort tasks alphabetically by description, ignoring case.
Equal descriptions keep their relative order. The response shows the new IDs;
use those IDs for subsequent mark, unmark and delete commands.

Sorting an empty or single-item list is safe. `sort descending` is rejected:
this command takes no arguments. New tasks still append to the list, so run
`sort` again when needed. Accepted changes are saved immediately; `bye` or `exit` closes Ari.
Existing saved files remain compatible, and task types, dates and completion
states are preserved. Ordering is lexicographic after case normalization,
not language-specific dictionary or natural-number ordering.

## Automated checks

GitHub Actions runs Java 25 unit tests, Checkstyle, fat-JAR packaging and all
recorded console tests on pushes to master/increment branches and PRs to master.
Reports are attached to each run as `verification-reports`, including failures.
The workflow does not deploy or publish a release.

Run the same checks locally with `./gradlew check shadowJar` (Windows:
`./gradlew.bat check shadowJar`) and `python test/run_ui_tests.py`.

## Acknowledgements

OpenAI Codex was used to assist with building and running the console UI testing workflow and reviewing implementation changes for potential issues.
