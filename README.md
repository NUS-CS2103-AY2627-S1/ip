# Zucc project template

This is a project template for a greenfield Java project named _Zucc_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. Run `./gradlew run` (`gradlew.bat run` on Windows) from the project directory. This compiles the project and opens Zucc's JavaFX interface. You can also run `Launcher.main()` from IntelliJ.

For a terminal-only session, run `Zucc.main()` from IntelliJ instead.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Checking code style

With JDK 25 active, run Checkstyle from the project root:

```bash
./gradlew checkstyleMain checkstyleTest
```

The broader `./gradlew check` command runs both the tests and Checkstyle checks.

## Git message validation

After cloning the repository, run this command once:

```shell
git config core.hooksPath .githooks
```

This tells Git to use the project's version-controlled hooks. The commit-message hook automatically rejects messages
that violate mechanically verifiable rules, such as the 72-character line limit. The remaining semantic rules, such as
using the imperative mood, still require author review.

## Use of generative AI

OpenAI Codex was used extensively in the development of this project and produced most of the source code from written project requirements. Before each commit, a human reviewed the proposed changes and directed further revisions where necessary, often through several rounds, to keep the implementation aligned with the requirements and the project's overall design. The human author made the final decisions about which changes to accept and remains responsible for the submitted code.
